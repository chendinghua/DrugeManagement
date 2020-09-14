package com.example.druge.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.entry.NotifyInfo;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.ui.activity.NotifyListInfoActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2019/1/16/016.
 */
public class ExampleService extends Service{
    private static final String TAG = "ExampleService";


    private NotificationManager notificationManager;
    private Notification notificastion;
    private int UserId;
    SharedPreferences   sp;

    SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        Log.i(TAG, "ExampleService-onCreate");
        super.onCreate();
        sp = getSharedPreferences("isLoading_config",Context.MODE_PRIVATE);
        editor  = getSharedPreferences("isLoading_config", Context.MODE_PRIVATE).edit();
        editor.putBoolean("isLoading",true);
        editor.apply();
    }
    boolean isStart;
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "ExampleService-onStart");

    }
    static int count=0;
    HandlerUtils handler = new HandlerUtils(this, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
           /* if(checkBrowser("com.example.uhf")){
                Log.d(TAG, "handlerExecutionFunction: 系统存在");
            }else {
                Log.d(TAG, "handlerExecutionFunction: 系统不存在");
            }*/
            //   checkBrowser();

            if(MethodEnum.GETNOTIFYLIST.equals(msg.getData().getString("method"))){

                List<NotifyInfo> notifyLists = JSON.parseArray(JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data")).getString("Result"), NotifyInfo.class);
                for (int i =0;i<notifyLists.size();i++){
           /*         Log.d(TAG, "handlerExecutionFunction: 系统存在");
                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(
                            ExampleService.this);
                    builder.setContentTitle(notifyLists.get(i).getTitle());
                    builder.setContentText(notifyLists.get(i).getContent());
                    builder.setContentInfo("我是通知附加信息"+count );
                    // 创建一个普通的意图，为下方的pendingIntent做准备
                    Intent intent = new Intent(ExampleService.this, NotifyListInfoActivity.class);
                    //    Toast.makeText(ExampleService.this,""+UserId,Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putInt("UserId",UserId);
                    // intent.putExtra("UserId",UserId+"");
                    intent.putExtras(bundle);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // 创建pendingIntent 传入上文定制好的意图
                    int notifyId = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            ExampleService.this, notifyId, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    //Intent.FLAG_ACTIVITY_NEW_TASK
                    // 如此点击完成后会跳转到拨号界面
                    builder.setContentIntent(pendingIntent);
                    builder.setSmallIcon(R.drawable.bg_boeing_logo);
                    notificastion = builder.getNotification();
                    count++;
                    notificationManager.notify(0, notificastion);*/
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(ExampleService.this);
                    builder.setSmallIcon(R.drawable.bg_hsj_logo); // 这里使用的系统默认图标，可自行更换
                    builder.setTicker("您有一条新消息！");
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bg_hsj_logo));
                    builder.setContentTitle(notifyLists.get(i).getTitle());
                    builder.setContentText(notifyLists.get(i).getContent());
                    builder.setAutoCancel(true);
                    // 点击后要执行的操作，打开MainActivity
                    Intent intent = new Intent(ExampleService.this, NotifyListInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("notifyId",notifyLists.get(i).getID());
                    bundle.putInt("UserId",notifyLists.get(i).getUserID());

                    bundle.putString("title",notifyLists.get(i).getTitle());
                    bundle.putString("content",notifyLists.get(i).getContent());
                    bundle.putString("typeName",notifyLists.get(i).getTypeName());
                    bundle.putString("creatorName",notifyLists.get(i).getCreatorName());
                    intent.putExtras(bundle);
                    PendingIntent pendingIntents = PendingIntent.getActivity(ExampleService.this, count, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntents);

                    // 启动Notification，getNotification()方法已经过时了，不推荐使用，使用build()方法替代
                    notificationManager.notify(count, builder.build());

                    count++;
                  /*  NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE );
                    Notification.Builder builder = new Notification.Builder(ExampleService.this);
                    builder.setSmallIcon(R.drawable.bg_boeing_logo); // 这里使用的系统默认图标，可自行更换
                    builder.setTicker("您有一条新消息！");
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bg_boeing_logo));
                    builder.setContentTitle("这是第一行标题栏");
                    builder.setContentText("这里是第二行，用来显示主要内容");
                    builder.setAutoCancel(true );

                    // 点击后要执行的操作，启动多个意图
                    Intent intentOne = new Intent(ExampleService.this, NotifyListInfoActivity.class);
                    intentOne.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
                    Intent intentTwo = new Intent(ExampleService.this, NotifyListInfoActivity.class);
                    intentTwo.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
                    Intent[] intents = new Intent[2];
                    intents[0] = intentOne;
                    intents[1] = intentTwo;

                    PendingIntent pendingIntents = PendingIntent.getActivities(ExampleService.this, 0, intents, PendingIntent.FLAG_ONE_SHOT);
                    builder.setContentIntent(pendingIntents);
                    // 启动 Notification
                    notificationManager.notify(1, builder.getNotification());*/
                }
            }else if(MethodEnum.HEARTBEAT.equals(msg.getData().getString("method"))) {
              if("true".equals(JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data")).getString("HasNotifyData"))) {
                  HashMap<String, Object> exampleParam = new HashMap<>();
                  exampleParam.put("userID", UserId);
                  exampleParam.put("isGet",0);
                  exampleParam.put("status",-1);
                  InteractiveDataUtil.interactiveMessage(getBaseContext(),exampleParam, handler, MethodEnum.GETNOTIFYLIST, InteractiveEnum.GET);
              }
            }
        }
    });
    boolean isLoop = true;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //执行文件的下载或者播放等操作
        Log.i(TAG, "ExampleService-onStartCommand");
		/*
		 * 这里返回状态有三个值，分别是:
		 * 1、START_STICKY：当服务进程在运行时被杀死，系统将会把它置为started状态，但是不保存其传递的Intent对象，之后，系统会尝试重新创建服务;
		 * 2、START_NOT_STICKY：当服务进程在运行时被杀死，并且没有新的Intent对象传递过来的话，系统将会把它置为started状态，
		 *   但是系统不会重新创建服务，直到startService(Intent intent)方法再次被调用;
		 * 3、START_REDELIVER_INTENT：当服务进程在运行时被杀死，它将会在隔一段时间后自动创建，并且最后一个传递的Intent对象将会再次传递过来。
		 */
        SharedPreferences sp=getSharedPreferences("login_config", Context.MODE_PRIVATE);
        UserId = sp.getInt("UserId",-1);

        //判断是否为开机启动，或者登录重新启动
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (isLoop) {
                        try {
                            if (UserId != -1) {
                                HashMap<String, Object> param = new HashMap<String, Object>();
                                param.put("userID", UserId);
                                //执行心跳监控
                                InteractiveDataUtil.interactiveMessage(getBaseContext(),param, handler, MethodEnum.HEARTBEAT, InteractiveEnum.POST);
                                Thread.sleep(10000);
                            } else {
                                isLoop = false;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "ExampleService-onBind");
        return null;
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "ExampleService-onDestroy");
        super.onDestroy();
    }
  /*  public boolean checkBrowser(String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }*/
  /*public void checkBrowser() {
      List<Programe>  programes =  getRunningProcess();
      for (Programe programe :programes){
      }
  }*/
   /* public List<Programe> getRunningProcess(){
             PackagesInfo pi = new PackagesInfo(this);
               ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
              //获取正在运行的应用
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
                //获取包管理器，在这里主要通过包名获取程序的图标和程序名
                PackageManager pm =this.getPackageManager();
                List<Programe> list = new ArrayList<>();

        for(ActivityManager.RunningAppProcessInfo ra : run){
                       //这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
                  *//* if(ra.processName.equals("system") || ra.processName.equals("com.Android.phone")){
                          continue;
                         }*//*
            Log.d(TAG, "programe "+ra.processName);
                      Programe  pr = new Programe();
                     pr.setIcon(pi.getInfo(ra.processName).loadIcon(pm));
                      pr.setName(pi.getInfo(ra.processName).loadLabel(pm).toString());
                      System.out.println(pi.getInfo(ra.processName).loadLabel(pm).toString());
                   list.add(pr);
                  }
               return list;
          }*/
}