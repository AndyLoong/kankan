package com.android.music.cardview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.yourspage.cardinterface.IGioneeCardViewInterface;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class CardViewLayoutCopy extends RelativeLayout implements IGioneeCardViewInterface {
	private static final String TAG = CardViewLayoutCopy.class.getSimpleName();

	private EditText mCardViewSearchText;
	private ImageView mCardViewSearchPic;
	private ImageView mRefresh;
	private GridView mGridView;
	private LinearLayout mDownLaod, mHistory, mChannel;
	private TextView mHome;
	ArrayList<CardViewItem> mCardViewList;
	public CardViewAdapter mCardViewAdapter;
	private Context mContext;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				mCardViewList = (ArrayList<CardViewItem>) msg.obj;
				if (mCardViewList != null && mCardViewList.size() != 0) {
					mCardViewAdapter.notifyDataSetChanged();
					new DownloadPicThread().start();
				}
				break;
			}
			case 1: {
				mCardViewAdapter.notifyDataSetChanged();
				break;
			}
			default:
				break;
			}
		}

	};
	OnKeyListener onKey = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				// 这里写发送信息的方法
				OnClickSearch();
				return true;
			}
			return false;
		}
	};
	private OnItemClickListener GridViewItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			// if(position == 0){
			Log.d(TAG, "position ===== " + position);
			String videoId = mCardViewList.get(position).getVideoId();
			Log.d(TAG, "videoId ===== " + videoId);
			CardViewOnCliclJump(videoId);
			// }else if(position == 1){
			// Log.d(TAG, "position ===1== " + position);
			// }else if(position == 2){
			// Log.d(TAG, "position ====2= " + position);k
			// }

		}
	};

	private void CardViewOnCliclJump(String str) {
		try {
			Uri uri = Uri.parse("youkuApp://activity/play?vid=" + str
					+ "&source=jinli_fuyiping");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mContext.startActivity(intent);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private OnClickListener SearchOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			OnClickSearch();
		}
	};
	private OnClickListener HistoryOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				Uri uri = Uri.parse("youkuApp://activity/history");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
				mContext.startActivity(intent);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private OnClickListener DownLaodOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				Uri uri = Uri.parse("youkuApp://activity/download");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
				mContext.startActivity(intent);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private OnClickListener HomeOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				Uri uri = Uri.parse("youkuApp://activity/home");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
				mContext.startActivity(intent);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	// youkuApp://activity/channelRank?id=85&title=xxx
	private OnClickListener ChannelOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				Uri uri = Uri.parse("youkuApp://activity/channelRank?");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
				mContext.startActivity(intent);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public CardViewLayoutCopy(Context context) {
		this(context, null);

		// TODO Auto-generated constructor stub
	}

	public CardViewLayoutCopy(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CardViewLayoutCopy(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		Log.d(TAG, "CardViewLayout");
		mContext = context;
		View.inflate(
				context,
				context.getResources().getIdentifier("video_cardview_layout",
						"layout", context.getPackageName()), this);

		ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		setLayoutParams(vlp);
		mCardViewAdapter = new CardViewAdapter(mContext);
		mCardViewList = new ArrayList<CardViewItem>();
//		initLayout(context);
	}

	private void initLayout(Context context) {
		// TODO Auto-generated method stub
		Log.d(TAG, "initLayout");

		mCardViewSearchText = (EditText) findViewById(context.getResources()
				.getIdentifier("search_text", "id", context.getPackageName()));
		mCardViewSearchPic = (ImageView) findViewById(context.getResources()
				.getIdentifier("search_pic", "id", context.getPackageName()));// card_01_img
		mRefresh = (ImageView) findViewById(context.getResources()
				.getIdentifier("refresh", "id", context.getPackageName()));
		mCardViewSearchText
				.setSelection(mCardViewSearchText.getText().length());
		mGridView = (GridView) findViewById(context.getResources()
				.getIdentifier("gridView", "id", context.getPackageName()));
		mDownLaod = (LinearLayout) findViewById(context.getResources()
				.getIdentifier("cardview_mine_01", "id",
						context.getPackageName()));
		mHistory = (LinearLayout) findViewById(context.getResources()
				.getIdentifier("cardview_mine_02", "id",
						context.getPackageName()));
		mChannel = (LinearLayout) findViewById(context.getResources()
				.getIdentifier("cardview_mine_03", "id",
						context.getPackageName()));
		mHome = (TextView) findViewById(context.getResources().getIdentifier(
				"home", "id", context.getPackageName()));
		if (mGridView != null)
			mGridView.setAdapter(mCardViewAdapter);
		mCardViewAdapter.notifyDataSetChanged();
		mGridView.setOnItemClickListener(GridViewItemClickListener);
		mCardViewSearchPic.setOnClickListener(SearchOnClickListener);
		mCardViewSearchText.setOnKeyListener(onKey);
		mDownLaod.setOnClickListener(DownLaodOnClickListener);
		mHistory.setOnClickListener(HistoryOnClickListener);
		mHome.setOnClickListener(HomeOnClickListener);
		mChannel.setOnClickListener(ChannelOnClickListener);
		mRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getCardviewInfo();
			}
		});
		getCardviewInfo();

	}

	private void getCardviewInfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ArrayList<CardViewItem> items = CardViewUtils
							.getCardViewInfo(getContext());
					Message message = mHandler.obtainMessage();
					message.what = 0;
					message.obj = items;
					mHandler.removeMessages(0);
					mHandler.sendMessage(message);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "getCardviewInfo e ===== " + e);
				}
			}
		}).start();
	}

	public static final String MUSIC_DIR = "/" + Environment.DIRECTORY_MUSIC;// +
																				// "/";
	public static final String DTS_PATH = Environment
			.getExternalStorageDirectory().getPath() + MUSIC_DIR + "/CardView";

	private class DownloadPicThread extends Thread {
		@Override
		public void run() {
			boolean b = FileUtil.createNewDirectory(DTS_PATH);// /storage/emulated/0/Music/Download/sort/
			for (int i = 0; i < mCardViewList.size(); i++) {
				String path = DTS_PATH + "/"
						+ mCardViewList.get(i).getVideoId();
				if (!FileUtil.isExist(path)) {
					CardViewUtils.downloadSingleFile(mCardViewList.get(i)
							.getPicUrl(), path);
				}
			}
			mHandler.obtainMessage(1).sendToTarget();
		}
	}

	@Override
	public View getCardView(Context arg0) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public String getCardViewName() {
		// TODO Auto-generated method stub
		return "Music";
	}

	@Override
	public void init(Context arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAdd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAllowInvalidate(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCardFilePath(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaxSize(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetWorkAuthority(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public boolean isCardSupport() {
		// TODO Auto-generated method stub
		boolean isShowCardView = false;
		if (isPackageExist(mContext, "com.youku.phone.jinli")) {
			isShowCardView = true;
		}
		return isShowCardView;
	}

	private void OnClickSearch() {
		try {
			if (mCardViewSearchText.getText() == null) {
				return;
			}
			Uri uri = Uri.parse("youkuApp://activity/searchResult?keyword="
					+ mCardViewSearchText.getText());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mContext.startActivity(intent);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isPackageExist(Context context, String packageName) {
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	public class CardViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private Context mContext;

		public CardViewAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCardViewList == null ? 0 : mCardViewList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		private class CardViewHolder {
			TextView mTextView;
			TextView mSonTextView;
			ImageView mImageView;

		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			CardViewHolder holder = null;
			if (view == null) {
				holder = new CardViewHolder();
				view = View.inflate(
						mContext,
						mContext.getResources().getIdentifier(
								"video_cardview_gridview_item", "layout",
								mContext.getPackageName()), null);
				// View.inflate(context,context.getResources().getIdentifier("cardview_layout","layout",
				// context.getPackageName()), this);
				// view =
				// mInflater.inflate(mContext,mContext.getResources().getIdentifier("cardview_gridview_item","layout",
				// mContext.getPackageName()), null);
				// view = mInflater.inflate(R.layout.cardview_gridview_item ,
				// null);
				holder.mImageView = (ImageView) view.findViewById(mContext
						.getResources().getIdentifier("card_img", "id",
								mContext.getPackageName()));
				holder.mTextView = (TextView) view.findViewById(mContext
						.getResources().getIdentifier("card_name", "id",
								mContext.getPackageName()));
				holder.mSonTextView = (TextView) view.findViewById(mContext
						.getResources().getIdentifier("card_details", "id",
								mContext.getPackageName()));
				// holder.mImageView =
				// (ImageView)view.findViewById(R.id.card_img);
				// holder.mTextView =
				// (TextView)view.findViewById(R.id.card_name);
				// holder.mSonTextView =
				// (TextView)view.findViewById(R.id.card_details);
				view.setTag(holder);
			} else {
				holder = (CardViewHolder) view.getTag();
			}
			try {
				holder.mImageView.setImageBitmap(BitmapFactory
						.decodeStream(new FileInputStream(new File(DTS_PATH
								+ "/"
								+ mCardViewList.get(position).getVideoId()))));
				holder.mTextView
						.setText(mCardViewList.get(position).getTitle());
				holder.mSonTextView.setText(mCardViewList.get(position)
						.getSonTitle());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
			}

			return view;
		}

	}

}
