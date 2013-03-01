package com.example.ddait;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.ddai.lib.ui.hlist.HorizontalListView;
import com.ddai.lib.ui.hlist.TestListener;
import com.example.ddait.loader.ImageLoader;
import com.example.ddait.loader.listener.OnSetImageListener;

@TargetApi(11)
public class TestHList extends Activity {

	ListView good1;
	Gallery good2;

	LinearLayout ll;
	Button bt1;
	Images data = new Images();
	int until = -1;
	int count = 5;
	MyAdapter adapter;
	int screenHeight, screenWidth;

	ImageLoader loader;
	String[] vv = new String[] { "AAA", "BBB", "CCC", "DDD", "EEE", "FFF",
			"GGG" };
	SparseArray<MyHAdapter> adapters = new SparseArray<TestHList.MyHAdapter>();
	SparseArray<HorizontalListView.ListStatus> status = new SparseArray<HorizontalListView.ListStatus>();
	private SparseArray<Integer> lastViews = new SparseArray<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_h_list);
		bt1 = (Button) findViewById(R.id.button1);
		bt1.setText("Load......");
		// ll = (LinearLayout) findViewById(R.id.linear_layout1);
		// listview.setAdapter(adapter);
		loader = new ImageLoader(this);

		good1 = (ListView) findViewById(R.id.scrollView1);
		adapter = new MyAdapter(this);
		good1.setAdapter(adapter);
		// for (String flag : vv) {
		// addHLV(flag);
		// }

		// hv2.setLayoutParams(lps2);

		// ll.addView(hv2);
		// ll.invalidate();

		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				test1();
				test2();
			}
		});
		int cacheSize = ((ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		cacheSize = 1024 * 1024 * cacheSize >> 3;
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
	}

	// private void addHLV(String flag) {
	// View hcontainer1 = getLayoutInflater().inflate(R.layout.h_contianer,
	// null);
	// android.widget.LinearLayout.LayoutParams lps1 = new
	// android.widget.LinearLayout.LayoutParams(
	// LayoutParams.MATCH_PARENT, 300);
	//
	// HorizontalListView hv1 = (HorizontalListView) hcontainer1
	// .findViewById(R.id.hlv);
	// hv1.setLayoutParams(lps1);
	// TextView tv1 = (TextView) hcontainer1.findViewById(R.id.textView1);
	// tv1.setText(flag);
	// MyHAdapter ada1 = new MyHAdapter(this, loader);
	// adapters.put(flag, ada1);
	// hv1.setAdapter(ada1);
	// ll.addView(hcontainer1);
	// }
	public static String formatDate(SimpleDateFormat sdf,final String str) {
//		String format = "EEE MMM dd HH:mm:ss Z yyyy";
//		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
//		// SimpleDateFormat sdf = new SimpleDateFormat(format);
//		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		long millionSeconds = System.currentTimeMillis();
		try {
			millionSeconds = sdf.parse(str).getTime();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		String result = changeTime(System.currentTimeMillis() - millionSeconds);
		System.out.println("result  ==  " + result);
		return result;
	}

	public static String changeTime(long millis) {
		if (millis <= 0) {
			return "";
		} else if (millis >= (86400000)) { // 一天的毫秒数86400000=24*3600000
			return (millis / 86400000) + "天";
		} else if (millis >= 3600000) {
			return (millis / 3600000) + "小时";
		} else if (millis >= 60000) {
			return (millis / 60000) + "分";
		} else {
			return "刚刚";
		}
	}
	
	protected void test2() {
		new Thread() {
			public void run() {
				ArrayList<String> times = new ArrayList<String>();
				String s1 = "Wed Jan 16 09:54:44 +0800 2013";
				for(int i=0;i<100;i++){
					times.add(s1);
				}
				
				String format = "EEE MMM dd HH:mm:ss Z yyyy";
				SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				long t  = System.currentTimeMillis();
				for(String s:times){
					formatDate(sdf,s1);
				}
				System.out.println("USE " + (System.currentTimeMillis()-t));
			};
		}.start();
	}

	protected void test1() {
		new Thread() {
			@Override
			public void run() {
				ArrayList<Source> result = data.loadData(until, count);
				until = Integer.valueOf(result.get(result.size() - 1).name);
				for (Source s : result) {
					s.width = getWidth();
					s.height = 300;
				}
				int length = adapters.size();
				MyHAdapter a;
				for (int i = 0; i < length; i++) {
					a = adapters.get(i);
					a.addSource(result);
					a.notifyDataSetChanged();
				}
			}
		}.run();
	}

	boolean loading = false;

	private void loadMore() {
		if (!loading) {
			loading = true;
			test1();
		}
	}

	int tmp = 0;

	public int getWidth() {
		if (tmp == 0) {
			tmp = 100;
		} else if (tmp == 100) {
			tmp = 200;
		} else if (tmp == 200) {
			tmp = 300;
		} else if (tmp == 300) {
			tmp = 100;
		}
		return tmp;
	}

	class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ImageLoader loader;
		Activity context;

		public MyAdapter(Activity context) {
			this.context = context;
			mInflater = LayoutInflater.from(context);
			loader = new ImageLoader(context);
		}

		@Override
		public int getCount() {
			return vv.length;
		}

		@Override
		public Object getItem(int position) {
			return vv[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.h_contianer,
						null);
				android.widget.LinearLayout.LayoutParams lps1 = new android.widget.LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 300);
				HorizontalListView hv1 = (HorizontalListView) convertView
						.findViewById(R.id.hlv);
				hv1.setLayoutParams(lps1);
				TextView tv1 = (TextView) convertView
						.findViewById(R.id.textView1);
				holder = new ViewHolder();
				holder.hlv = hv1;
				holder.text = tv1;
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				status.put(holder.position, holder.hlv.getStatus());
				holder.position = position;
			}
			String flag = vv[position];
			holder.text.setText(flag);
			MyHAdapter ada1 = adapters.get(position);
			if (ada1 == null) {
				ada1 = new MyHAdapter(context, position, loader, holder.hlv);
				adapters.put(position, ada1);
			}
			holder.hlv.setAdapter(ada1);
			if (status.get(position) != null) {
				holder.hlv.setListStatus(status.get(position));
			}
			return convertView;
		}

		private class ViewHolder {
			TextView text;
			HorizontalListView hlv;
			int position;
		}
	}

	class MyHAdapter extends BaseAdapter {
		Interpolator accelerator = new AccelerateInterpolator();
		private LayoutInflater mInflater;
		ImageLoader loader;
		ArrayList<Source> sources = new ArrayList<Source>();
		private int flag = 0;

		@TargetApi(14)
		public MyHAdapter(Activity context, final int flag,
				ImageLoader _loader, final HorizontalListView list) {
			this.flag = flag;
			mInflater = LayoutInflater.from(context);
			loader = _loader;
			TestListener msl = new TestListener() {

				@Override
				public void onShowNewItem(int position) {
					int flagVisible = 0;
					if (lastViews.indexOfKey(flag) >= 0) {
						flagVisible = lastViews.get(flag);
					}
					if (position > flagVisible) {
						View localView = list
								.getChildAt(list.getChildCount() - 1);
						if (localView != null) {
							// localView.setRotationY(-25F);
							localView.setTranslationX(50F);
							ViewPropertyAnimator localViewPropertyAnimator = localView
									.animate().translationX(0.0F)
									.setDuration(1000L)
									.setInterpolator(accelerator);
							localViewPropertyAnimator.setListener(new AnimLst(
									localView));
							localViewPropertyAnimator.setStartDelay(50).start();
						}

						flagVisible = Math.max(flagVisible, position);
						lastViews.put(flag, flagVisible);
					}

				}
			};
			list.setOnScrollListener(msl);
		}

		public void addSource(ArrayList<Source> news) {
			sources.addAll(news);
		}

		private class AnimLst implements Animator.AnimatorListener {
			View view;

			public AnimLst(View view) {
				this.view = view;
			}

			@TargetApi(11)
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}

			@TargetApi(11)
			@Override
			public void onAnimationEnd(Animator animation) {
				this.view.setTranslationX(0.0F);
				this.view.setTranslationY(0.0F);
				this.view.setRotationX(0.0F);
				this.view.setRotationY(0.0F);
				this.view.invalidate();
			}

			@TargetApi(11)
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@SuppressLint("NewApi")
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.test_h_list_item, null);
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
			holder.icon.getLayoutParams().height = s.height;
			holder.icon.getLayoutParams().width = s.width;
			holder.icon.setLayoutParams(holder.icon.getLayoutParams());
			holder.text.setText(s.name);
			loader.loadRemoteImage(s.pic, holder.icon,
					new OnSetImageListener() {

						@Override
						public void setImageBitmap(final ImageView imageView,
								final Bitmap bitmap) {
							runOnUiThread(new Runnable() {
								public void run() {
									imageView.setImageBitmap(bitmap);
									// list.setSelection(adapter.getCount()-1);
								}
							});
						}
					});
			return convertView;
		}

		private class ViewHolder {
			TextView text;
			ImageView icon;
		}

	}
}
