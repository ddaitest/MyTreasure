package com.example.ddait;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ddait.loader.ImageLoader;
import com.example.ddait.loader.listener.OnSetImageListener;

@SuppressLint("NewApi")
public class TestListAnim extends Activity {

	ListView list;
	DAdapter adapter;
	Button bt1, bt2, bt3, bt4;
	Images data = new Images();
	int until = -1;
	int count = 5;
	Handler handler;
	boolean loading = false;

	private void loadMore() {
		if (!loading) {
			loading = true;
			new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					final ArrayList<Source> result = data.loadData(until, count);
					until = Integer.valueOf(result.get(result.size() - 1).name);
					runOnUiThread(new Runnable() {
						public void run() {
							adapter.addSource(result);
							adapter.notifyDataSetChanged();
						}
					});
					loading = false;
				};
			}.start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_list_anim);
		list = (ListView) findViewById(R.id.listView1);
		((TextView) findViewById(R.id.textView1)).setText("001");
		adapter = new DAdapter(this, list);
		list.setAdapter(adapter);
		bt1 = (Button) findViewById(R.id.button1);
		bt2 = (Button) findViewById(R.id.button2);
		bt3 = (Button) findViewById(R.id.button3);
		bt4 = (Button) findViewById(R.id.button4);
		bt1.setText("Load......");
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				test1();
			}
		});
		bt2.setText("TEST!!!");
		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				test2();
			}
		});
		bt3.setText("TEST!!!");
		bt3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				test3();
			}
		});
		bt4.setText("Clean All");
		bt4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clean();
			}
		});
	}
	
	Test test =new Test();
	
	private void test2() {
		test.t1();
	}

	private void test3() {
		test.t2();
	}

	private void clean() {
		String path = Environment.getExternalStorageDirectory() + "/DDAI";
		File pf = new File(path);
		File[] fs = pf.listFiles();
		for (File f : fs) {
			f.delete();
		}
		Toast.makeText(this, "Clean Ok", Toast.LENGTH_SHORT);
	}

	protected void test1() {
		new Thread() {
			@Override
			public void run() {
				ArrayList<Source> result = data.loadData(until, count);
				until = Integer.valueOf(result.get(result.size() - 1).name);
				adapter.addSource(result);
				adapter.notifyDataSetChanged();
				// list.setSelection(adapter.getCount() - 1);
			}
		}.run();
	}

	private class DAdapter extends BaseAdapter {

		Interpolator accelerator = new AccelerateInterpolator();
		private LayoutInflater mInflater;
		ImageLoader loader;
		ArrayList<Source> sources = new ArrayList<Source>();
		private int flagVisible = 0;

		private class AnimLst implements Animator.AnimatorListener {
			View view;

			public AnimLst(View view) {
				this.view = view;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				this.view.setTranslationX(0.0F);
				this.view.setTranslationY(0.0F);
				this.view.setRotationX(0.0F);
				this.view.setRotationY(0.0F);
				this.view.invalidate();
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

		}

		public DAdapter(Activity context, final ListView listView) {
			mInflater = LayoutInflater.from(context);
			loader = new ImageLoader(context);

			listView.setOnScrollListener(new OnScrollListener() {
				private int totalItemCount = 0;

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					Log.d("DDAI", "listView.onScrollStateChanged  scrollState="
							+ scrollState);
					if(scrollState== OnScrollListener.SCROLL_STATE_IDLE){
						if(flagVisible>=(totalItemCount-4)){
							loadMore();
						}
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					this.totalItemCount = totalItemCount;
					Log.d("DDAI", "listView.onScroll  firstVisibleItem="
							+ firstVisibleItem + ",visibleItemCount"
							+ visibleItemCount + ",totalItemCount="
							+ totalItemCount);
					int last = firstVisibleItem + visibleItemCount;
					if (last > flagVisible) {
						View localView = view.getChildAt(view.getChildCount() - 1);
						view.getChildCount();
						Log.d("DDAI",
								"view.getChildCount()=" + view.getChildCount());
						Log.d("DDAI", "localView at " + (last - 1) + "="
								+ localView);
						if (localView != null) {
							localView.setRotationX(25F);
							localView.setTranslationY(30F);
							ViewPropertyAnimator localViewPropertyAnimator = localView
									.animate().rotationXBy(30F).rotationX(0.0F)
									.translationY(0.0F).setDuration(1000L)
									.setInterpolator(accelerator);
							localViewPropertyAnimator.setListener(new AnimLst(
									localView));
							localViewPropertyAnimator.setStartDelay(50).start();
						}

						// ObjectAnimator visToInvis =
						// ObjectAnimator.ofFloat(localView,
						// "rotationX", 30f, 0f);
						// visToInvis.setDuration(1500);
						// // visToInvis.setStartDelay(100);
						// visToInvis.setInterpolator(accelerator);
						// visToInvis.start();
						flagVisible = Math.max(flagVisible, last);
					}

				}
			});
		}

		public void addSource(ArrayList<Source> news) {
			sources.addAll(news);
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			super.notifyDataSetChanged();
		}

		@Override
		public void notifyDataSetInvalidated() {
			// TODO Auto-generated method stub
			super.notifyDataSetInvalidated();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			super.registerDataSetObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			super.unregisterDataSetObserver(observer);
		}

		@Override
		public int getCount() {
			return sources.size();
		}

		@Override
		public Object getItem(int position) {
			return sources.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		ArrayList<String> ids = new ArrayList<String>();

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.test_list_item, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.imageView1);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// Bind the data efficiently with the holder.
			Source s = sources.get(position);
			holder.text.setText(s.name);
			loader.loadRemoteImage(s.pic, holder.icon,
					new OnSetImageListener() {

						@Override
						public void setImageBitmap(final ImageView imageView,
								final Bitmap bitmap) {
							runOnUiThread(new Runnable() {
								public void run() {
									Log.d("DDAI", "111");
									imageView.setImageBitmap(bitmap);
									// list.setSelection(adapter.getCount()-1);
								}
							});
						}
					});
			String hash = convertView.hashCode() + "";
			if (!ids.contains(hash)) {
				Log.e("DDAI", "NEW");
				ids.add(hash);
			}
			Log.d("DDAI",
					"VIEWS = " + ids.size() + " / " + list.getChildCount());
			return convertView;
		}

		private class ViewHolder {
			TextView text;
			ImageView icon;
		}

	}
}
