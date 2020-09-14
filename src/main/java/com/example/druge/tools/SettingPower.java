package com.example.druge.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.druge.R;
import com.example.druge.ui.dialog.ProgressDialog;
import com.rscja.deviceapi.RFIDWithUHF;

/**
 * Created by 16486 on 2020/3/23.
 */

public class SettingPower {

    /**
     * @param index 修改频率的类型id
     * @param tvFrequency 显示修改的类型

     */
    public static void updateFrequency(final RFIDWithUHF mReader, final Context context, final int index, final TextView tvFrequency , final ProgressDialog progress){

        if(progress!=null)
        progress.show();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.getData().getInt("index")){
                    case 1:
                        tvFrequency.setText("当前功率:低频");
                        break;
                    case 2:

                        tvFrequency.setText("当前功率:中频");
                        break;
                    case 3:
                        tvFrequency.setText("当前功率:高频");
                        break;
                }
                if(msg.getData().getBoolean("isSettingSuccess")){
                    UIHelper.ToastMessage(context, R.string.uhf_msg_set_power_succ);
                }else{
                    UIHelper.ToastMessage(context, R.string.uhf_msg_set_power_fail);
                }
                if(progress!=null){
                    progress.dismiss();
                }

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences  sp=context.getSharedPreferences("pda_config", Context.MODE_PRIVATE);
                boolean isSettingSuccess=false;
                mReader.free();
                mReader.init();
                switch (index){
                    case 1:
                       isSettingSuccess =mReader.setPower(sp.getInt("lowFrequency",5));
                        break;
                    case 2:
                        isSettingSuccess=mReader.setPower(sp.getInt("mediumFrequency",15));

                        break;
                    case 3:
                        isSettingSuccess = mReader.setPower(sp.getInt("highFrequency",30));
                        break;
                }
               Message msg =  handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("index",index);
                bundle.putBoolean("isSettingSuccess",isSettingSuccess);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();

    }
}
