package com.example.druge.ui.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.druge.R;
import com.example.druge.adapter.ViewPagerAdapter;
import com.example.druge.fragment.KeyDwonFragment;
import com.example.druge.tools.UIHelper;
import com.example.druge.widget.NoScrollViewPager;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.utility.StringUtility;
/**
 * Created by Administrator on 2015-03-10.
 */
public class BaseTabFragmentActivity extends FragmentActivity {

	private final int offscreenPage = 2; //����ViewPager���ڵļ���ҳ��

//	protected ActionBar mActionBar;


	protected NoScrollViewPager mViewPager;
	protected ViewPagerAdapter mViewPagerAdapter;


	protected List<KeyDwonFragment> lstFrg = new ArrayList<KeyDwonFragment>();
	protected List<String> lstTitles = new ArrayList<String>();

	// public Reader mReader;
	public RFIDWithUHF mReader;
	private int index = 0;

	private ActionBar.Tab tab_kill, tab_lock, tab_set ;
	private DisplayMetrics metrics;
	private AlertDialog dialog;
	private long[] timeArr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initUHF() {


		try {
			mReader = RFIDWithUHF.getInstance();
		} catch (Exception ex) {

			toastMessage(ex.getMessage());

			return;
		}

		if (mReader != null) {
			new InitTask().execute();
		}
	}

	protected void initViewPageData() {

	}

	/**
	 * ����ActionBar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
//		return super.onCreateOptionsMenu(menu);
	}

	protected void initViewPager() {

		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), lstFrg, lstTitles);

		mViewPager = (NoScrollViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOffscreenPageLimit(offscreenPage);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 139 ||keyCode == 280) {

			if (event.getRepeatCount() == 0) {

				if (mViewPager != null) {

					KeyDwonFragment sf = (KeyDwonFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
					sf.myOnKeyDwon();

				}
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void gotoActivity(Intent it) {
		startActivity(it);
	}

	public void gotoActivity(Class<? extends BaseTabFragmentActivity> clz) {
		Intent it = new Intent(this, clz);
		gotoActivity(it);
	}

	public void toastMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void toastMessage(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * �豸�ϵ��첽��
	 *
	 * @author liuruifeng
	 */
	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog mypDialog;

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return mReader.init();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			mypDialog.cancel();

			if (!result) {
				Toast.makeText(BaseTabFragmentActivity.this, "init fail",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mypDialog = new ProgressDialog(BaseTabFragmentActivity.this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("init...");
			mypDialog.setCanceledOnTouchOutside(false);
			mypDialog.show();
		}
	}

	@Override
	protected void onDestroy() {

		if (mReader != null) {
			mReader.free();
		}
		super.onDestroy();
	}

	/**
	 * ��֤ʮ����������Ƿ���ȷ
	 *
	 * @param str
	 * @return
	 */
	public boolean vailHexInput(String str) {

		if (str == null || str.length() == 0) {
			return false;
		}

		// ���ȱ�����ż��
		if (str.length() % 2 == 0) {
			return StringUtility.isHexNumberRex(str);
		}

		return false;
	}

	public void getUHFVersion() {


		if(mReader!=null) {

			String rfidVer = mReader.getHardwareType();

			UIHelper.alert(this, R.string.action_uhf_ver,
					rfidVer, R.drawable.webtext);
		}
	}

	protected HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
	protected SoundPool soundPool;
	protected float volumnRatio;
	protected AudioManager am;
	/**
	 * 播放提示音
	 *
	 * @param id 成功1，失败2
	 */
	public void playSound(int id) {
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
		volumnRatio = audioCurrentVolumn / audioMaxVolumn;
		try {
			soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
					volumnRatio, // 右声道音量
					1, // 优先级，0为最低
					0, // 循环次数，0无不循环，-1无永远循环
					1 // 回放速度 ，该值在0.5-2.0之间，1为正常速度
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
