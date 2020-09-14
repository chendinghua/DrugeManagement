package com.example.druge.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.entry.Stockdrug;
import com.example.druge.tools.ActionUrl;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.SettingPower;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.UserConfig;
import com.example.druge.ui.Listener.OnMultiClickListener;
import com.example.druge.ui.activity.ReplaceOperationActivity;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.dialog.ProgressDialog;
import com.example.druge.ui.view.ScrapInfoDialog;
import com.rscja.deviceapi.RFIDWithUHF;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/** 替换扫描药箱
 * Created by 16486 on 2020/4/23.
 */
public class ScanDrugeFragment extends Fragment{
    @BindView(id=R.id.btn_scan_druge_commit)
    Button btnScanCommit;                   //提交按钮
    @BindView(id=R.id.et_scan_druge_rfid)
    EditText etScanRfid;                    //扫描文档
    @BindView(id=R.id.tv_scan_druge_title)
    TextView tvScanTitle;                   //扫描标题

    ReplaceOperationActivity activity;

    private ScrapInfoDialog.Builder builder;
    private ScrapInfoDialog mDialog;
    OnMultiClickListener multiClickListener;

    @BindView(id=R.id.btn_scan_druge_back)
    Button btnBack;
    @BindView(id=R.id.include_scan_druge_power_setting)
    View powerSetting;

    private Button lowFrequency;         //低频
    private Button mediumFrequency;       //中频
    private Button  highFrequency;        //高频
    private TextView currentFrequency;     //当前频率
    Timer timer = null;

    private MyTaskTimer mTask = null;

    boolean isLoad=true;
    ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_scan_druge,container,false);
        AnnotateUtil.initBindView(this,view);
        activity=(ReplaceOperationActivity) getActivity();
        progress = ProgressDialog.createDialog(activity);


        multiClickListener  = new OnMultiClickListener(activity) {
            @Override
            public void onMultiClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_low_frequency:
                        SettingPower.updateFrequency(activity.mReader,activity,1,currentFrequency,progress);
                        break;
                    case R.id.btn_medium_frequency:
                        SettingPower.updateFrequency(activity.mReader,activity,2,currentFrequency,progress);
                        break;
                    case R.id.btn_high_frequency:
                        SettingPower.updateFrequency(activity.mReader,activity,3,currentFrequency,progress);
                        break;
                    //扫描药箱标签提交
                    case R.id.btn_scan_druge_commit:
                        String inputRfid = etScanRfid.getText().toString().trim();
                        //判断扫描内容不能为空
                        if (inputRfid.equals("")) {
                            UIHelper.ToastMessage(activity, "扫描内容不能为空");
                            return;
                        }
                        HashMap<String, Object> checkRfidParam = new HashMap<String, Object>();
                        checkRfidParam.put("rfidno", inputRfid);
                        checkRfidParam.put("Userid", UserConfig.UserId);
                        checkRfidParam.put("UserName", UserConfig.UserName);

                        //调用后台接口根据rfid获取产品信息
                        InteractiveDataUtil.interactiveMessage(activity, checkRfidParam, handler, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET);
                        break;
                    //扫描新的药箱标签点击返回
                    case R.id.btn_scan_druge_back:
                        activity.isShouwDruge=false;

                        if(timer ==null)
                            timer= new Timer();
                        if(timer != null && mTask != null){
                            mTask.cancel();
                        }
                        mTask = new MyTaskTimer();

                        timer.schedule(mTask,0,1000);                //启动定时器

                                activity.pager.setCurrentItem(activity.pager.getCurrentItem()-1);
                                activity.currentStatus=ActionUrl.OLDDRUGE;
                        break;
                }

            }
        };

        initPowerSetting();

        initListener();

        builder = new ScrapInfoDialog.Builder(activity);



        if(isLoad) {
            if(timer ==null)
                timer= new Timer();
            if(timer != null && mTask != null){
                mTask.cancel();
            }
            mTask = new MyTaskTimer();
            timer.schedule(mTask, 0, 1000);                //启动定时器
            isLoad=false;
        }

        SettingPower.updateFrequency(activity.mReader,activity,1,currentFrequency,progress);
        return view;
    }
     Handler trimerHandler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("currentStatus", "onCreateView: "+activity.currentStatus);
            if(ActionUrl.OLDDRUGE.equals(activity.currentStatus)){
                btnBack.setVisibility(View.GONE);
                //     tvScanTitle.setText("请扫描替换前药箱");
            }else{
                btnBack.setVisibility(View.VISIBLE);
                //     tvScanTitle.setText("请扫描替换后药箱");
            }
            if(timer != null){
                mTask.cancel();
                timer.cancel();
                timer = null;

            }
        }
    };
    class  MyTaskTimer extends TimerTask{

        @Override
        public void run() {
            //希望定时器做什么，在此实现，
            //可以使用一个Handler对象，将定时消息传递到界面线程
            if(activity.isShouwDruge && trimerHandler !=null){
                Message msg = new Message();
                trimerHandler.sendMessage(msg);
            }
        }
    }

    private void initListener() {
        btnScanCommit.setOnClickListener(multiClickListener);
        btnBack.setOnClickListener(multiClickListener);
        lowFrequency.setOnClickListener(multiClickListener);         //低频
        mediumFrequency.setOnClickListener(multiClickListener);       //中频
        highFrequency.setOnClickListener(multiClickListener);        //高频
    }

    private void initPowerSetting() {

        lowFrequency =   powerSetting.findViewById(R.id.btn_low_frequency);
        mediumFrequency =   powerSetting.findViewById(R.id.btn_medium_frequency);
        highFrequency =   powerSetting.findViewById(R.id.btn_high_frequency);
        currentFrequency =   powerSetting.findViewById(R.id.tv_current_frequency);
    }


    HandlerUtils handler = new HandlerUtils(activity, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            if (MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().get("method"))) {

                final Stockdrug stockdrugItem = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);
                if (stockdrugItem != null && stockdrugItem.getQelType() == 1&& stockdrugItem.getStatus() == 1 && ActionUrl.OLDDRUGE.equals(activity.currentStatus)) {
                    /**
                     * 判断药箱信息是否有效
                     判断是扫描旧药箱
                     */
                    showTwoButtonDialog("确认箱号物品信息", "确认", "取消", stockdrugItem, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.pager.setCurrentItem(activity.pager.getCurrentItem()+1);
                            //记录当前原药箱id
                            activity.replaceDrugeOld=stockdrugItem;
                            mDialog.dismiss();
                            activity.isShouwDruge=false;

                            if(timer ==null)
                                timer= new Timer();
                            if(timer != null && mTask != null){
                                mTask.cancel();
                            }
                            mTask = new MyTaskTimer();

                            timer.schedule(mTask,0,1000);                //启动定时器

                            etScanRfid.setText("");
                        //    activity.currentStatus=ActionUrl.NEWDRUGE;
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    /*判断药箱信息是否有效
                    判断是扫描新药箱
                    判断当前扫描的是新替换药箱id不等于旧药箱id
                      */
                }else if(stockdrugItem != null && stockdrugItem.getQelType() == 1&& stockdrugItem.getStatus() == 1
                        && ActionUrl.NEWDRUGE.equals(activity.currentStatus) && activity.replaceDrugeOld.getID()!=stockdrugItem.getID()) {
                    showTwoButtonDialog("确认箱号物品信息", "确认", "取消", stockdrugItem, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.pager.setCurrentItem(activity.pager.getCurrentItem()+1);
                            //记录当前原药箱id
                            activity.replaceDrugeNew=stockdrugItem;
                            mDialog.dismiss();
                            activity.isShouwDruge=false;
                            if(timer ==null)
                                timer= new Timer();
                            if(timer != null && mTask != null){
                                mTask.cancel();
                            }
                            mTask = new MyTaskTimer();
                            timer.schedule(mTask,0,1000);                //启动定时器

                            etScanRfid.setText("");
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                }
                else {
                    Toast.makeText(activity, "箱号数据异常", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message ms) {
            if(MethodEnum.GETQELSTOCKDRUG.equals(ms.getData().get("method"))){
                Toast.makeText(activity,"暂无药箱信息",Toast.LENGTH_SHORT).show();
            }
        }
    });

    //扣动物理按键触发的扫描rfid数据
    public void read(){
            try {
                RFIDWithUHF initUHF = RFIDWithUHF.getInstance();
                initUHF.free();
                initUHF.init();
                String strUII = RFIDWithUHF.getInstance().inventorySingleTag();
                if (!TextUtils.isEmpty(strUII)) {
                    String strResult = "";
                    //这里是用来生成编码的
                    final String strEPC = RFIDWithUHF.getInstance().convertUiiToEPC(strUII);
                    if (strEPC.startsWith("2001") || strEPC.startsWith("1001")) {
                        etScanRfid.setText(strEPC);
                    } else {
                        UIHelper.ToastMessage(activity, "扫描标签类型错误");
                        etScanRfid.setText("");
                    }

                } else {
                    UIHelper.ToastMessage(activity, R.string.uhf_msg_inventory_fail);
                }
            } catch (Exception e) {
            }
    }
        private void showTwoButtonDialog(String alertText, String confirmText, String cancelText, Stockdrug stockdrug, View.OnClickListener conFirmListener, View.OnClickListener cancelListener) {
            mDialog = builder.setMessage(alertText).
                    initScrapProductInfo(stockdrug)
                    .setPositiveButton(confirmText, conFirmListener)
                    .setNegativeButton(cancelText, cancelListener)
                    .createTwoButtonDialog();
            mDialog.show();
        }
}
