package com.example.druge.ui.Listener;

import android.content.Context;
import android.view.View;

import com.example.druge.tools.UIHelper;

/**
 * Created by 16486 on 2020/3/23.
 */

public abstract class OnMultiClickListener implements View.OnClickListener{
    // 两次点击按钮之间的点击间隔不能少于3000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 3000;
    private static long lastClickTime;
    private Context context;

    private View oldClick;
    public OnMultiClickListener(Context context){
        this.context=context;
    }

    public abstract void onMultiClick(View v);

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME && oldClick==null || oldClick!=null && oldClick.getId()!=v.getId() && (curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME ) {
            oldClick=v;
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(v);
        }else{
            oldClick=null;
            if(context!=null)
            UIHelper.ToastMessage(context,"请勿重复点击");
        }
    }
}
