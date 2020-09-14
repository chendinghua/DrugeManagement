package com.example.druge.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Administrator on 2019/1/16/016.
 */

public class HandlerUtils extends Handler {
    private HandlerUtilsCallback callback;
    private HandlerUtilsErrorCallback errorCallback;
    private Context context;
    public HandlerUtils(Context context, HandlerUtilsCallback callback) {
        this.callback=callback;
        this.context=context;
    }
    public HandlerUtils(Context context,HandlerUtilsCallback callback,HandlerUtilsErrorCallback errorCallback){
        this.context=context;
        this.callback=callback;
        this.errorCallback=errorCallback;
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what){
            case 1:
                callback.handlerExecutionFunction(msg);
                break;
            case -1:
                    if(errorCallback!=null){
                     errorCallback.handlerErrorFunction(msg);
                        Log.d("JSON", "HandlerUtilsErrorCallback: ");
                    }else {
                        if(context!=null)
                        UIHelper.ToastMessage(context, "数据异常");
                    }
                break;
            case -2:
                if(context!=null)
                    if(!MethodEnum.HEARTBEAT.equals(msg.getData().getString("method")))
                UIHelper.ToastMessage(context,"访问远程服务有误");
                break;
        }
        super.handleMessage(msg);
    }
}
