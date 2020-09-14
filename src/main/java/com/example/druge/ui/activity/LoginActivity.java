package com.example.druge.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.druge.R;
import com.example.druge.entry.RightInfo;
import com.example.druge.entry.UserInfo.User;
import com.example.druge.entry.UserInfo.UserRightInfo;
import com.example.druge.service.AppListener;
import com.example.druge.service.ExampleService;
import com.example.druge.sqlite.DBHelper;
import com.example.druge.sqlite.Dao.UserInfoDao;
import com.example.druge.sqlite.SynUtils;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.HttpUtils;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MD5;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.MyCatchException;
import com.example.druge.tools.NetUtil;
import com.example.druge.tools.ServiceUtils;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.UserConfig;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.dialog.ProgressDialog;

import java.util.HashMap;

/**
 * Created by 16486 on 2019/10/30.
 */
public class LoginActivity  extends Activity {
    //初始化密码区控件
    @BindView(id=R.id.mEditTextPassword)
    EditText mPassword;
    //初始化用户区控件
    @BindView(id=R.id.mEditTextAccount)
    EditText mAccount;
    @BindView(id=R.id.image_setting)
    ImageView imageSetting;

    ProgressDialog dialog;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = ProgressDialog.createDialog(this);
        //全局异常信息记录对象初始化
        MyCatchException mException= MyCatchException.getInstance();
        mException.init(getApplicationContext());  //注册
        //控件注解对象初始化
        AnnotateUtil.initBindView(this);
        SharedPreferences sp=getSharedPreferences("login_config", Context.MODE_PRIVATE);
        mAccount.setText(sp.getString("username",null));
        if(!NetUtil.isNetworkConnected(this)){
            Toast.makeText(this,"无网络连接",Toast.LENGTH_SHORT).show();
        }else {
        }
    }
    public void updateSetting(View view){
        Intent intent = new Intent(this,SettingActionUrlActivity.class);
        startActivity(intent);
    }

    //登陆触发的事件
    public void commitData(View view){
        switch (view.getId()){
            case R.id.mButtonLogin:
                final String userNameStr = mAccount.getText().toString().trim();
                final String passwordStr = MD5.ganerateMD5(mPassword.getText().toString()+ "H@S#J$2%0&1*8$");
                //单机登陆
                if(!NetUtil.isNetworkConnected(this)){
                    // Toast.makeText(this,"无网络连接",Toast.LENGTH_SHORT).show();

                    final Handler handler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what){
                                case -1:
                                    UIHelper.ToastMessage(LoginActivity.this,"当前本地没有缓存的账号信息");
                                    break;
                                case 1:
                                    UserRightInfo userRightInfo = (UserRightInfo)msg.getData().getSerializable("result");
                                    if(userRightInfo==null){
                                        UIHelper.ToastMessage(LoginActivity.this,"用户名或密码输入有误，登录失败");
                                    }else{
                                        User user = userRightInfo.getUser();
                                        if(user!=null) {
                                            goActivity(user.getUserId(),user.getName(),JSON.toJSONString(userRightInfo.getRightInfos()));
                                        }else{
                                            UIHelper.ToastMessage(LoginActivity.this,"用户信息有误");
                                        }
                                    }
                                    break;
                            }
                            dialog.dismiss();
                        }
                    };
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SynUtils synUtils = new SynUtils();
                            UserRightInfo userRightInfo=null;
                            UserInfoDao userInfoDao = new UserInfoDao(LoginActivity.this);
                            Message msg = handler.obtainMessage();
                          SQLiteDatabase db = DBHelper.getInstance(LoginActivity.this).getReadableDatabase();
                            db.beginTransaction();
                            try {
                                if (!userInfoDao.isDataExist(db)) {
                                    msg.what = -1;

                                } else {
                                    msg.what = 1;
                                    userRightInfo = synUtils.offlineLoginUser(db, LoginActivity.this, userNameStr, passwordStr);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("result", userRightInfo);
                                    msg.setData(bundle);
                                }
                                db.setTransactionSuccessful();
                            }catch (Exception e){

                            }finally {
                                if(db!=null) {
                                    db.endTransaction();
                                    db.close();
                                }
                            }
                            handler.sendMessage(msg);
                        }
                    }).start();

                    return;
                }

                final HandlerUtils handler = new HandlerUtils(this, new HandlerUtilsCallback() {
                    @Override
                    public void handlerExecutionFunction(Message msg) {
                        if(MethodEnum.LOGINSIGNIN.equals(msg.getData().getString("method"))) {
                            //点击记住密码把用户名和密码保存到本地文件里面

                            final JSONObject obj = JSON.parseObject(msg.getData().getString("result"));
                            final int userId = obj.getJSONObject("Data").getInteger("UserID");
                            final String userName = obj.getJSONObject("Data").getString("UserName");
                            final String name = obj.getJSONObject("Data").getString("Name");






                            /*         if(!ServiceUtils.isServiceRunning(LoginActivity.this,"com.example.fams.service.ExampleService")) {
                            Intent serviceIntent = new Intent(LoginActivity.this, ExampleService.class);
                            Bundle serviceBundle = new Bundle();
                            editor.putInt("UserId",userId);
                            editor.apply();
                            serviceIntent.putExtras(serviceBundle);
                            startService(serviceIntent);
                        }else{
                            editor.putInt("UserId", obj.getJSONObject("Data").getInteger("UserID"));
                        }
                        if(!ServiceUtils.isServiceRunning(LoginActivity.this,"com.example.fams.service.AppListener")) {
                            Intent appLisetenerService =  new Intent(LoginActivity.this, AppListener.class);
                            startService(appLisetenerService);
                        }*/

                         /*   UserConfig.UserId = obj.getJSONObject("Data").getInteger("UserID");
                            UserConfig.UserName = obj.getJSONObject("Data").getString("UserName");
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("token", obj.getJSONObject("Data").getString("Token"));
                            bundle.putInt("UserId", obj.getJSONObject("Data").getInteger("UserID"));
                            bundle.putString("userName", obj.getJSONObject("Data").getString("Name"));
                            bundle.putBoolean("isLogin", true);
                            bundle.putBoolean("isLoading",true);
                            intent.putExtras(bundle);
                            startActivity(intent);*/
                            final String password =MD5.ganerateMD5(mPassword.getText().toString()+ "H@S#J$2%0&1*8$");
                            final Handler handler1 = new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    String mssage =    "数据提交"+(msg.getData().getBoolean("result",false)?"成功":"失败");
                                    UIHelper.ToastMessage(LoginActivity.this,mssage);
                                    //页面跳转
                                    goActivity(userId,name,obj.getJSONObject("Data").getString("RightList") );
                                }
                            };
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SynUtils synUtils = new SynUtils();
                                    boolean result=      synUtils.saveLoaclUserInfo(LoginActivity.this,userName,password,name,userId,JSON.parseArray(obj.getJSONObject("Data").getString("RightList"), RightInfo.class));
                                    Message msg = handler1.obtainMessage();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("result",result);
                                    msg.setData(bundle);
                                    handler1.sendMessage(msg);
                                }
                            }).start();



                        }
                    }
                }, new HandlerUtilsErrorCallback() {
                    @Override
                    public void handlerErrorFunction(Message ms) {
                        if(MethodEnum.LOGINSIGNIN.equals(ms.getData().getString("method"))) {
                            UIHelper.ToastMessage(LoginActivity.this,"用户名或密码输入有误，登录失败");
                        }
                    }
                });
                if(userNameStr.equals("") || passwordStr.equals("")) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空.", Toast.LENGTH_LONG).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("userName", mAccount.getText().toString());
                            params.put("userPwd", MD5.ganerateMD5(mPassword.getText().toString()+ "H@S#J$2%0&1*8$"));
                            params.put("Platform", 2);
                            params.put("deviceNo", HttpUtils.getIMEI(LoginActivity.this));
                            params.put("deviceIp", HttpUtils.getIpAddress(LoginActivity.this));
                            Log.d("ipv4", "run: "+HttpUtils.getIpAddress(LoginActivity.this));
                            InteractiveDataUtil.interactiveMessage(LoginActivity.this,params,handler, MethodEnum.LOGINSIGNIN, InteractiveEnum.POST);
                        }
                    }).start();
                }
                break;
        }
    }

    public  void goActivity(int userId,String userName,String rightListStr){

        SharedPreferences.Editor rightEditor = getSharedPreferences("right_info_config", Context.MODE_PRIVATE).edit();
        rightEditor.putString("rightList", rightListStr);
        rightEditor.apply();
        sendLocalhostUserNameAndPassword();
        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();

        UserConfig.UserId =userId;
        UserConfig.UserName = userName;
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("UserId", userId);
        bundle.putString("userName", userName);
        bundle.putBoolean("isLogin", true);
        bundle.putBoolean("isLoading",true);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private  void sendLocalhostUserNameAndPassword(){
        SharedPreferences.Editor editor=getSharedPreferences("login_config", Context.MODE_PRIVATE).edit();
        //存储账号密码到本地
        editor.putBoolean("is_remember", true);
        editor.putString("username", mAccount.getText().toString().trim());
        editor.putString("password", mPassword.getText().toString().trim());
        editor.apply();
    }
    //第一次点击事件发生的时间
    private long mExitTime;
    //点击两次返回退出app   System.currentTimeMillis()系统当前时间
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
            return true;
        } else if(KeyEvent.KEYCODE_HOME==keyCode){
            //写要执行的动作或者任务
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return super.onKeyDown(keyCode, event);
    }
}