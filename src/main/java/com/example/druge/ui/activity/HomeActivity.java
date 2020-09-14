package com.example.druge.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.ResideMenu.ResideMenu;
import com.example.druge.ResideMenu.ResideMenuItem;
import com.example.druge.entry.RightInfo;
import com.example.druge.tools.ApplicationContent;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.UserConfig;
import com.example.druge.ui.fragment.PickUpTaskListFragment;
import com.example.druge.ui.fragment.SelectFAMSProductFragment;
import com.rscja.deviceapi.RFIDWithUHF;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.util.List;

/**
 * Created by 16486 on 2019/10/30.
 */
public class HomeActivity  extends SuperFragmentActivity implements View.OnClickListener{

    private ResideMenu mResideMenu;

    private RelativeLayout leftMenu;
    private RelativeLayout rightMenu;

    private boolean isLogin;
    private String userName;


    public  int UserId;

    public Barcode2DWithSoft barcode2DWithSoft=null; //二维码对象


    HomeKeyEventBroadCastReceiver receiver;

    List<RightInfo> rightList=null;

    public int actionUrl;

    public static   Fragment currentFragment;
    public RFIDWithUHF mReader;


    TextView tvHomeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);





        isLogin  = getIntent().getExtras().getBoolean("isLogin");
        //判断页面是否登录
        if(isLogin) {
            userName = getIntent().getExtras().getString("userName");
            UserId = UserConfig.UserId;

            SharedPreferences.Editor userInfoEditor = getSharedPreferences("login_config", Context.MODE_PRIVATE).edit();
            userInfoEditor.putInt("UserId", UserId);
            userInfoEditor.putString("userName", userName);
            userInfoEditor.apply();
        }else{

            SharedPreferences sp = getSharedPreferences("login_config",Context.MODE_PRIVATE);
            UserId =sp.getInt("UserId",0);
            userName = sp.getString("userName","");
        }
        //获取左右侧的操作模块
        SharedPreferences rightSp = getSharedPreferences("right_info_config", Context.MODE_PRIVATE);
        rightList = JSON.parseArray(rightSp.getString("rightList",""),RightInfo.class);


        //初始化控件
        initView();
        //设置左右测的功能模块
        setUpMenu();
        //初始化RFID功率模块


        initUHF();
        int initLoadIndex=0;
        for (int i =0;i<rightList.size();i++){
            if("-1".equals(rightList.get(i).getActionUrl())){
                initLoadIndex=i;
                break;
            }
        }
        if(rightList.size()>0)
        initFragment(rightList.get(initLoadIndex).getActionForm(),rightList.get(initLoadIndex).getActionUrl(),rightList.get(initLoadIndex).getName());
    }

    public void initFragment(String actionFrom,String strActionUrl,String homeName){
        tvHomeName.setText(homeName);

        String classStr = "com.example.druge.ui.fragment."+actionFrom;
        Log.d("actionFrm", "onClick: "+classStr);
        Class clazz = null;
        try {
            clazz = Class.forName(classStr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Fragment fragment =null;
        try {
            //跳转的类强制转换为fragment对象
            fragment  =(Fragment)clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(fragment==null){
            UIHelper.ToastMessage(this,"跳转页面异常");
        }
        //判断任务标识是否为空
        if(!"null".equals(strActionUrl))
            actionUrl =Integer.parseInt(strActionUrl);
        Log.d("actionUrl", "onClick: "+actionUrl);
        changeFragment(fragment);


    }

    //物理按键监听事件
    long mExitTime;
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //返回按钮触发
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Object mHelperUtils;
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }
            return true;
        }
        //扣动物理按键触发
        if (event.getKeyCode() == 139 || event.getKeyCode() == 280) {
            //判断是查询页面
            if(currentFragment instanceof SelectFAMSProductFragment){
                SelectFAMSProductFragment selectfamsProductFragment = (SelectFAMSProductFragment) currentFragment;
                selectfamsProductFragment.read();
            }

            }
        return super.onKeyDown(keyCode, event);
    }

    public void hideButton(boolean vis){

        if(currentFragment instanceof PickUpTaskListFragment){
            PickUpTaskListFragment pickUpTaskListFragment = (PickUpTaskListFragment) currentFragment;
            pickUpTaskListFragment.hideButton(vis);
        }

    }

    /***
     * 初始化RFID模块
     */
    public void initUHF() {


        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {

            UIHelper.ToastMessage(this,ex.getMessage());

            return;
        }

        if (mReader != null) {
            new HomeActivity.InitRfidTask().execute();
        }
    }

    //加载RFID模块
    public class InitRfidTask extends AsyncTask<String, Integer, Boolean> {
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
                Toast.makeText(HomeActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(HomeActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    @Override
    protected void onResume() {
        barcode2DWithSoft=Barcode2DWithSoft.getInstance();
        //这里是用来重新加载条形码扫描模块
        if(ApplicationContent.isLoadingScanCode) {
            receiver = new HomeKeyEventBroadCastReceiver();
            registerReceiver(receiver, new IntentFilter("com.rscja.android.KEY_DOWN"));
            new InitTask().execute();
            ApplicationContent.isLoadingScanCode=false;
        }
        super.onResume();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        //上面导航
        LinearLayout homeTop = (LinearLayout) this.findViewById(R.id.home_top);
//		AseoZdpAseo.initFinalTimer(this, AseoZdpAseo.BOTH_TYPE);
        leftMenu = (RelativeLayout) homeTop.findViewById(R.id.home_left);
        rightMenu = (RelativeLayout) homeTop.findViewById(R.id.home_right);
        tvHomeName = (TextView) homeTop.findViewById(R.id.home_name);

        leftMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mResideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        rightMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mResideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
    }
    /**
     * 上面导航的配置
     */
    private void setUpMenu() {
        // attach to current activity;
        mResideMenu = new ResideMenu(this);
        mResideMenu.setBackground(R.raw.bg);
        mResideMenu.attachToActivity(this);
        mResideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {
            }
            @Override
            public void closeMenu() {
            }
        });
        mResideMenu.setScaleValue(0.6f);
        for (int i = 0;i<rightList.size();i++){
            ResideMenuItem items = new ResideMenuItem(this,R.raw.icon_home,rightList.get(i).getName());
            items.setOnClickListener(this);
            //跳转的activity名称
            items.setTag(rightList.get(i).getActionForm()+","+rightList.get(i).getActionUrl()+","+rightList.get(i).getName());

            mResideMenu.addMenuItem(items,("left".equals(rightList.get(i).getRemark()))?ResideMenu.DIRECTION_LEFT:ResideMenu.DIRECTION_RIGHT|ResideMenu.SCROLL_INDICATOR_TOP);
        }
        mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT); //设置向右滑动没有反映
        mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

    }
    //加载显示的Fragment页面
    private void changeFragment(Fragment targetFragment){
        currentFragment  = targetFragment;
        mResideMenu.clearIgnoredViewList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_fm_container, targetFragment);
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
    @Override
    public void onClick(View v) {

        tvHomeName.setText(v.getTag().toString().split(",")[2]);

        //页面跳转
        String classStr = "com.example.druge.ui.fragment."+ v.getTag().toString().split(",")[0];
        Log.d("actionFrm", "onClick: "+classStr);
        Class clazz = null;
        try {
            clazz = Class.forName(classStr);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Fragment fragment =null;
        try {
            //跳转的类强制转换为fragment对象
            fragment  =(Fragment)clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(fragment==null){
            UIHelper.ToastMessage(this,"跳转页面异常");
        }
        //判断任务标识是否为空
        if(!"null".equals(v.getTag().toString().split(",")[1]))
        actionUrl =Integer.parseInt( v.getTag().toString().split(",")[1]);
        Log.d("actionUrl", "onClick: "+actionUrl);
        changeFragment(fragment);

        mResideMenu.closeMenu();

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mResideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {

        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void retry() {
    }

    @Override
    public void netError() {
    }
    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            boolean reuslt=false;
            if(barcode2DWithSoft!=null) {
                reuslt=  barcode2DWithSoft.open(HomeActivity.this);
            }
            return reuslt;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                barcode2DWithSoft.setParameter(6, 1);
                barcode2DWithSoft.setParameter(22, 0);
                barcode2DWithSoft.setParameter(23, 55);
                barcode2DWithSoft.setParameter(402, 1);
                Toast.makeText(HomeActivity.this,"Success",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(HomeActivity.this,"fail",Toast.LENGTH_SHORT).show();
            }
            mypDialog.cancel();
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(HomeActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";//home key
        static final String SYSTEM_RECENT_APPS = "recentapps";//long home key
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.rscja.android.KEY_DOWN")) {
                int reason = intent.getIntExtra("Keycode",0);
                //getStringExtra
                boolean long1 = intent.getBooleanExtra("Pressed",false);
                // home key处理点
                if(reason==280 || reason==66){
                }
            }
        }
    }
}
