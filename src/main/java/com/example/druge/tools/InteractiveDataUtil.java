package com.example.druge.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/1/15/015.
 */

public class InteractiveDataUtil {

    /***
     *  调用接口的方法
     * @param activity 当前的页面对象
     * @param params    数据传入集合
     * @param handler   结果响应对象
     * @param method    调用接口名
     * @param interactiveType   调用接口方式 POST、GET
     */
    public static void interactiveMessage(final Object activity , final HashMap<String,Object> params, final Handler handler, final String method, final String interactiveType ){
        final   PopupWindow popupWindow = new PopupWindow();
     final Runnable startRunnable = new Runnable() {
          @Override
          public void run() {
              if(activity instanceof Activity)
              ShowLoadingDialog.show(popupWindow, (Activity) activity);
          }
      };
    final  Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (popupWindow != null && popupWindow.isShowing())
                    popupWindow.dismiss();
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    };

        new Thread(new Runnable() {
            @Override
            public  void run() {
                new Handler(Looper.getMainLooper()).post(startRunnable);//在子线程中直接去new 一个handler

                String result="";
                HttpUtils httpUtils = new HttpUtils();
                if(interactiveType.equals(InteractiveEnum.GET)){
                    result =httpUtils.sendGetMessage( (Context) activity,params,method);
                }else if(interactiveType.equals(InteractiveEnum.POST)){
                    result = httpUtils.sendPostMessage((Context)activity, params,method);
                }else if(interactiveType.equals(InteractiveEnum.UPLOAD)){
                    result=httpUtils.uploadImage((Context)activity,params,method);
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                Log.d("JSON", "run: "+result);
                if(result.trim().equals("")){
                    msg.what=-2;
                }else {
                    JSONObject object = JSON.parseObject(result);
                    if (object.getBoolean("Success")) {
                        //操作成功
                        msg.what = 1;
                        bundle.putString("result",result);
                    } else {
                        bundle.putString("result",result);
                        //操作失败
                        msg.what = -1;
                    }
                }
                bundle.putString("method",method);
                msg.setData(bundle);
                if(handler!=null)
                    handler.sendMessage(msg);

                new Handler(Looper.getMainLooper()).post(stopRunnable);//在子线程中直接去new 一个handler

            }
        }).start();

    }

    /***
     * 用来处理绑定的数据
     * @param activity 当前的页面对象
     * @param params    数据传入集合
     * @param handler   结果响应对象
     * @param method    调用接口名
     * @param interactiveType   调用接口方式 POST、GET
     * @param bindDate  绑定处理的数据
     */
    public static void interactiveMessage(final Activity activity, final HashMap<String,Object> params, final Handler handler, final String method, final String interactiveType,final String bindDate ){
        final   PopupWindow popupWindow = new PopupWindow();

        final  Handler loadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);
                if(popupWindow!=null)
                    popupWindow.dismiss();
            }
        };


        if(activity instanceof Activity){
            ShowLoadingDialog.show(popupWindow, activity);

        }

        new Thread(new Runnable() {
            @Override
            public  void run() {
                String result="";
                HttpUtils httpUtils = new HttpUtils();
                if(interactiveType.equals(InteractiveEnum.GET)){
                    result =httpUtils.sendGetMessage(activity, params,method);
                }else if(interactiveType.equals(InteractiveEnum.POST)){
                    result = httpUtils.sendPostMessage(activity,params,method);
                }else if(interactiveType.equals(InteractiveEnum.UPLOAD)){
                    result=httpUtils.uploadImage(activity, params,method);
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                Log.d("JSON", "run: "+result);
                if(result.trim().equals("")){
                    msg.what=-2;
                }else {
                    JSONObject object = JSON.parseObject(result);
                    if (object.getBoolean("Success")) {
                        //操作成功
                        msg.what = 1;
                        bundle.putString("result",result);
                    } else {
                        bundle.putString("result",result);
                        //操作失败
                        msg.what = -1;
                    }
                }
                bundle.putString("method",method);
                bundle.putString("bindDate",bindDate);
                msg.setData(bundle);
                if(handler!=null)
                    handler.sendMessage(msg);

                loadHandler.sendMessage(new Message());
            }
        }).start();
    }
}
