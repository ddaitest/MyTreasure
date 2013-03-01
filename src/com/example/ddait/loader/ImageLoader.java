package com.example.ddait.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ddai.lib.commandqueue.ICommandHandler;
import com.example.ddait.loader.listener.OnSetImageListener;

public class ImageLoader {
	int viewWidth = 300;
	int viewHeight = 300;
	protected String storePath;

	protected ExecutorService searchThreadPool;

	protected ExecutorService downloadThreadPool;
	Context context;

	public ImageLoader(Context _context) {
		context = _context;
		int cpuNumber = Runtime.getRuntime().availableProcessors();
		searchThreadPool = Executors.newFixedThreadPool(cpuNumber);
		downloadThreadPool = Executors.newFixedThreadPool(cpuNumber);
		storePath = Environment.getExternalStorageDirectory() + "/DDAI/";
		int cacheSize = ((ActivityManager) _context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		cacheSize = 1024 * 1024 * cacheSize >> 3;
		thumbnailCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}

		};
	}

	public static String getFileName(String url) {
		String preKey = url.replaceAll("/", "%2F");
		preKey = preKey.replaceAll("%2F%2F", "%2F");
		String[] names = preKey.split("%2F");
		String key = "";
		StringBuffer buffer = new StringBuffer();
		buffer.append(url.length());
		for (String name : names) {
			buffer.append(name);
		}
		key = Base64.encodeToString(buffer.toString().getBytes(),
				Base64.DEFAULT);
		key = key.replaceAll("/", "_");
		return key;
	}

	public synchronized void loadRemoteImage(String url, ImageView view,
			 OnSetImageListener setImageListener) {
		synchronized (view) {
			// viewPool.add(new WeakReference<ImageView>(view));
			Bitmap result = thumbnailCache.get(url);
			if (result != null && !result.isRecycled()) {
				view.setImageBitmap(result);
			} else if (cancelWork(url, view)) {
				final SearchTask task = new SearchTask(url, view,
						setImageListener);
				final AsyncDrawable ad = new AsyncDrawable(
						context.getResources(), null, task);
				Drawable d = view.getDrawable();
				if (d != null) {
					d.setCallback(null);
					d = null;
				}
				view.setImageDrawable(ad);
				view.setScaleType(ScaleType.CENTER_CROP);
				try {
//					new Thread(task).start();
					searchThreadPool.execute(task);
				} catch (RejectedExecutionException e) {
				}
			}
		}
	}

	protected boolean cancelWork(String url, ImageView view) {
		SearchTask task = getSearchTask(view);
		if (task != null) {
			final String taskURL = task.url;
			if ((!TextUtils.isEmpty(taskURL))
					&& (taskURL.equalsIgnoreCase(url)) && (!task.stop)) {
				return false;
			} else {
				task.cancelWork();
			}
		}
		return true;
	}

	private static SearchTask getSearchTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getTask();
			}
		}
		return null;
	}

	protected LruCache<String, Bitmap> thumbnailCache;

	class SearchTask implements Runnable {
		private final WeakReference<ImageView> imageViewReference;
		private String url;
		private boolean stop = false;
		private OnSetImageListener setImageListener;

		public SearchTask(String url, ImageView view,
				OnSetImageListener setImageListener) {
			imageViewReference = new WeakReference<ImageView>(view);
			this.url = url;
			this.setImageListener = setImageListener;
		}

		@Override
		public void run() {
			loadRemote();
		}

		public synchronized void cancelWork() {
			stop = true;
		}

		private void loadRemote() {
			Bitmap bitmap = null;
			final String filename = getFileName(url);
			if ((!stop) && (getAttachedImageView() != null)) {
				// exist in SDcard
				final File file = new File(storePath, filename);
				bitmap = readFromFile(file);
			}
			if (bitmap == null) {
				// Add download task.
				ICommandHandler listener = new ICommandHandler() {
					@Override
					public void onFinish() {
						if ((!stop) && (getAttachedImageView() != null)) {
							final File file = new File(storePath, filename);
							Bitmap bitmap = readFromFile(file);
							final ImageView imageView = getAttachedImageView();
							if (imageView != null && (!stop) && bitmap != null) {
								// ihc.putCache(url, bitmap);
								thumbnailCache.put(url, bitmap);
								setImageListener.setImageBitmap(imageView,
										bitmap);
							} else {
								bitmap.recycle();
							}
						}
					}

				};
				DownloadTask task = new DownloadTask(url, filename, listener);
//				new Thread(task).start();
				downloadThreadPool.execute(task);
				return;
			} else {
				final ImageView imageView = getAttachedImageView();
				if (imageView != null && (!stop)) {
					thumbnailCache.put(url, bitmap);
					setImageListener.setImageBitmap(imageView, bitmap);
				} else {
					bitmap.recycle();
				}
			}
		}

		private Bitmap readFromFile(File file) {
			Bitmap bitmap = null;
			int width = viewWidth;
			int height = viewHeight;
			if (file.exists()) {
				bitmap = decodeBitmap(file.getAbsolutePath(), width, height);
			}
			return bitmap;
		}

		private synchronized Bitmap decodeBitmap(String filename, int width,
				int height) {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filename, options);
			if (options.outWidth < 1 || options.outHeight < 1) {
				String fn = filename;
				File ft = new File(fn);
				if (ft.exists()) {
					ft.delete();
					return null;
				}
			}
			// Calculate inSampleSize
			options.inSampleSize = calculateOriginal(options, width, height);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap bm1 = BitmapFactory.decodeFile(filename, options);
			if (bm1 == null) {
				return null;
			}
			return bm1;
		}

		private int calculateOriginal(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			int inSampleSize = 1;
			final int height = options.outHeight;
			final int width = options.outWidth;

			if (height > reqHeight || width > reqWidth) {
				if (width > height) {
					inSampleSize = Math.round((float) height
							/ (float) reqHeight);
				} else {
					inSampleSize = Math.round((float) width / (float) reqWidth);
				}
				final float totalPixels = width * height;
				final float totalReqPixelsCap = (reqWidth * reqHeight * 3);

				while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
					inSampleSize++;
				}
			}
			return inSampleSize;
		}

		/**
		 * Returns the ImageView associated with this task as long as the
		 * ImageView's task still points to this task as well. Returns null
		 * otherwise.
		 */
		private ImageView getAttachedImageView() {
			final ImageView imageView = imageViewReference.get();
			final SearchTask bitmapWorkerTask = getSearchTask(imageView);

			if (this == bitmapWorkerTask) {
				return imageView;
			}

			return null;
		}
	}

	private class DownloadTask implements Runnable {
		String filename;
		String urlString;

		ICommandHandler tl;

		public DownloadTask(String url, String filename,
				ICommandHandler listener) {
			this.filename = filename;
			this.urlString = url;
			this.tl = listener;
		}

		@Override
		public void run() {

			try {
				downloadInFile();
				if (tl != null) {
					tl.onFinish();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		private void downloadInFile() throws ClientProtocolException,
				JSONException, Exception {
			/**
			 * Workaround for bug pre-Froyo, see here for more info:
			 * http://android
			 * -developers.blogspot.com/2011/09/androids-http-clients.html
			 */
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
				System.setProperty("http.keepAlive", "false");
			}
			HttpURLConnection urlConnection = null;
			FileOutputStream out = null;
			
			
			File storeFile = Environment.getExternalStorageDirectory();
			String basePath = storeFile.getPath();
			File destDir = new File(basePath, "/DDAI/");
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			
			File cacheFile = new File(destDir, filename);
			if (!cacheFile.exists()) {
				cacheFile.createNewFile();
			}
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(20000);
			urlConnection.setReadTimeout(20000);
			final InputStream is = urlConnection.getInputStream();
			out = new FileOutputStream(cacheFile);
			byte[] buffer = new byte[1024 * 8];
			int b = -1;
			while ((b = is.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			out.flush();
			out.close();
			cacheFile.setLastModified(System.currentTimeMillis());
		}
	}

	/**
	 * A custom Drawable that will be attached to the imageView while the work
	 * is in progress. Contains a reference to the actual worker task, so that
	 * it can be stopped if a new binding is required, and makes sure that only
	 * the last started worker process can bind its result, independently of the
	 * finish order.
	 */
	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<SearchTask> task;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				SearchTask bitmapWorkerTask) {
			super(res, bitmap);
			task = new WeakReference<SearchTask>(bitmapWorkerTask);
		}

		public SearchTask getTask() {
			return task.get();
		}
	}
}
