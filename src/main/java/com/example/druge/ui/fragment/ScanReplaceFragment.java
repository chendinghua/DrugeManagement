package com.example.druge.ui.fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.adapter.AutoAdapter;
import com.example.druge.commonCode.dao.ScanReplaceGetRfid;
import com.example.druge.entry.Stockdrug;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.tools.ActionUrl;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.SettingPower;
import com.example.druge.tools.SoundManager;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.UserConfig;
import com.example.druge.ui.Listener.OnMultiClickListener;
import com.example.druge.ui.activity.ReplaceOperationActivity;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 扫描借出和归还
 * Created by 16486 on 2020/4/23.
 */
public class ScanReplaceFragment  extends Fragment{
    @BindView(id=R.id.tv_scan_replace_title)
    TextView tvScanReplace;                 //药箱名
    @BindView(id=R.id.include_scan_replace_power_setting)
    View powerSetting;                      //设置功率页面
    @BindView(id= R.id.tv_scan_replace_add_current_count)
    TextView tvCurrentCount;                        //正常数量
    @BindView(id= R.id.tv_scan_replace_add_error_count)
    TextView tvErrorCount;                          //异常数量

    @BindView(id=R.id.btn_scan_replace_back)
    Button btnBack;                                 //返回按钮
    @BindView(id=R.id.btn_scan_replace_commit)
    Button btnCommit;                               //下一步按钮

    private Button lowFrequency;         //低频
    private Button mediumFrequency;       //中频
    private Button  highFrequency;        //高频
    private TextView currentFrequency;     //当前频率

    ReplaceOperationActivity activity;

    int successCount=0;

    int errorCount=0;

    //循环查询对象
    public  ScanReplaceGetRfid loopRead;


    BaseAdapter adapter ;                 //listView适配器



    List<TaskListInfoItem> tagList = new ArrayList<>();   //存储显示listView集合对象
    List<TaskListInfoItem> tempList = new ArrayList<>();  //临时存储listView集合对象

    OnMultiClickListener onMultiClickListener;

    //刷新按钮
    @BindView(id=R.id.sf_scan_replace_refresh)
    private SwipeRefreshLayout replaceRefresh;
    List<String> errorMaps = new ArrayList<>();
    @BindView(id=R.id.lv_scan_replace_List)
    ListView lvScanReplace;             //扫描表格

    ProgressDialog progress;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_replace, container, false);
        AnnotateUtil.initBindView(this, view);
        activity =(ReplaceOperationActivity) getActivity();
        progress = ProgressDialog.createDialog(activity);
        onMultiClickListener = new OnMultiClickListener(activity) {
            @Override
            public void onMultiClick(View v) {
                if(loopRead.getLoopFlag()){
                    Toast.makeText(activity,"请停止扫描RFID",Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (v.getId()){
                    case R.id.btn_low_frequency:
                        SettingPower.updateFrequency(activity.mReader,activity,1,currentFrequency,progress);
                        break;
                    case R.id.btn_medium_frequency:
                        SettingPower.updateFrequency(activity.mReader,activity,2,currentFrequency,progress);
                        break;
                    case R.id.btn_high_frequency:
                        SettingPower.updateFrequency(activity.mReader,activity,3,currentFrequency,progress);
                        break;
                    //扫描提交
                    case R.id.btn_scan_replace_commit:
                        if(ActionUrl.OLDDRUGE.equals(activity.currentStatus)){
                            activity.currentStatus = ActionUrl.NEWDRUGE;
                            activity.oldResult.clear();
                            activity.oldResult.addAll(tagList);
                            //显示扫描药箱返回按钮
                            activity.isShouwDruge=true;
                        }else{
                            activity.newResult.clear();
                            activity.newResult.addAll(tagList);
                            //显示扫描药箱返回按钮
                            activity.isShouwDruge=true;

                            //加载结果标识
                            activity.isLoadResult=true;
                        }

                        activity.pager.setCurrentItem(activity.pager.getCurrentItem()+1);
                        break;
                    //返回上一步
                    case R.id.btn_scan_replace_back:
                        if(ActionUrl.OLDDRUGE.equals(activity.currentStatus)){
                            tagList.clear();
                            tempList.clear();
                            activity.replaceDrugeOld = new Stockdrug();
                            activity.oldResult.clear();
                        }else{
                            tagList.clear();
                            tempList.clear();
                            activity.replaceDrugeNew = new Stockdrug();
                            activity.newResult.clear();
                        }
                        errorMaps.clear();
                        successCount=0;
                        errorCount=0;
                        updateCommitStatus();
                        adapter.notifyDataSetChanged();
                        activity.isShouwDruge=true;
                        activity.pager.setCurrentItem(activity.pager.getCurrentItem()-1);


                        break;


                }
            }
        };

        //初始化设置功率控件
        initPowerSettingLayout();
        loopRead = new ScanReplaceGetRfid();

        initListener();

        return view;
    }
    private void initListener() {
        lowFrequency.setOnClickListener(onMultiClickListener);         //低频
        mediumFrequency.setOnClickListener(onMultiClickListener);       //中频
        highFrequency.setOnClickListener(onMultiClickListener);        //高频
        btnBack.setOnClickListener(onMultiClickListener);               //返回
        btnCommit.setOnClickListener(onMultiClickListener);             //下一步

        replaceRefresh.setSize(SwipeRefreshLayout.LARGE);
        //设置进度圈的背景色。这里随便给他设置了一个颜色：浅绿色
        replaceRefresh.setProgressBackgroundColorSchemeColor(Color.CYAN);
        //设置进度动画的颜色。这里面最多可以指定四个颜色，我这也是随机设置的，大家知道怎么用就可以了
        replaceRefresh.setColorSchemeResources(android.R.color.holo_orange_dark
                ,android.R.color.holo_blue_dark
                ,android.R.color.holo_red_dark
                ,android.R.color.widget_edittext_dark);
        //设置手势滑动监听器
        replaceRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //发送一个延时1秒的handler信息
                //   handler.sendEmptyMessageDelayed(199,1000);
                tagList.clear();
                tempList.clear();
                adapter.notifyDataSetChanged();
                errorMaps.clear();
                successCount=0;
                errorCount=0;
                updateCommitStatus();
                replaceRefresh.setRefreshing(false);
            }
        });
    }
    private void initPowerSettingLayout() {

        if(ActionUrl.OLDDRUGE.equals(activity.currentStatus)){
            btnCommit.setEnabled(true);
            tvScanReplace.setText(activity.replaceDrugeOld.getName());
        }else{
            btnCommit.setEnabled(false);
            tvScanReplace.setText(activity.replaceDrugeNew.getName());
        }
        lowFrequency =   powerSetting.findViewById(R.id.btn_low_frequency);
        mediumFrequency =   powerSetting.findViewById(R.id.btn_medium_frequency);
        highFrequency =   powerSetting.findViewById(R.id.btn_high_frequency);
        currentFrequency =   powerSetting.findViewById(R.id.tv_current_frequency);
        SettingPower.updateFrequency(activity.mReader,activity,2,currentFrequency,progress);

        adapter = new AutoAdapter<TaskListInfoItem>(activity,tagList,"StorageId","RfidNo","SerialNo","AssestName","AssestTypeName","Status");
        lvScanReplace.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        tagList.clear();
        tempList.clear();
        adapter.notifyDataSetChanged();
        errorMaps.clear();
        successCount=0;
        errorCount=0;
        updateCommitStatus();
        super.onResume();
    }

    public void read(){
        if (!loopRead.getLoopFlag()) {
            if (activity.mReader.startInventoryTag(0,0)) {
                loopRead.setLoopFlag(true);
                loopRead.setTempList(tempList);
                loopRead.read(readResultHandler);
            } else {
                //用户点击停止识别则暂停上面扫描的线程
                activity.mReader.stopInventory();
                UIHelper.ToastMessage(activity, R.string.uhf_msg_inventory_open_fail);
            }
        } else {// 停止识别
            destroyReadTag();

        }
    }

    //销毁读取
    public void destroyReadTag(){
        updateCommitStatus();
        loopRead.setLoopFlag(false);
    }

    //更新标题数量
    private void updateCommitStatus() {
        /**
         * 判断扫描是原药箱的药品并且异常数量为0
         * 判断扫描是替换药箱的药品异常数量为0并且扫描数量大于0
         */
        if (successCount >0 && errorCount == 0 && ActionUrl.NEWDRUGE.equals(activity.currentStatus)
                || errorCount==0 && ActionUrl.OLDDRUGE.equals(activity.currentStatus)
                ) {
            btnCommit.setEnabled(true);
        } else {
            btnCommit.setEnabled(false);
        }
        // }
        tvCurrentCount.setText(successCount+"");
        tvErrorCount.setText(errorCount+"");
    }


    //扫描返回数据结果
    Handler readResultHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case -2:
                    SoundManager.getInstance(activity).playSound(2);
                    String rfid =msg.getData().getString("rfid");
                    if(errorMaps.size()==0){
                        errorMaps.add(rfid);
                        HashMap<String,Object> moveParam = new HashMap<>();
                        moveParam.put("RFIDNo",rfid);
                        moveParam.put("UserId", UserConfig.UserId);
                        moveParam.put("UserName",UserConfig.UserName);
                        InteractiveDataUtil.interactiveMessage(activity,moveParam,handlerUtils, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET);
                    }else {
                        boolean isRfid = false;
                        for (int i = 0; i < errorMaps.size(); i++) {
                            if(errorMaps.get(i).equals(rfid)){
                                isRfid=true;
                                break;
                            }
                        }
                        if(!isRfid){
                            errorMaps.add(rfid);
                            HashMap<String,Object> moveParam = new HashMap<>();
                            moveParam.put("RFIDNo",rfid);
                            moveParam.put("UserId", UserConfig.UserId);
                            moveParam.put("UserName",UserConfig.UserName);
                            InteractiveDataUtil.interactiveMessage(activity,moveParam,handlerUtils, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET);
                        }
                    }
                    break;
                //返回msg.what 1、正常 2、异常
                default:
                    SoundManager.getInstance(activity).playSound(msg.what);
                    break;
            }
            updateCommitStatus();
            super.handleMessage(msg);
        }
    };

    HandlerUtils handlerUtils = new HandlerUtils(activity, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            if (MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().getString("method"))) {
                Stockdrug stockdrugItem = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);
                if (stockdrugItem != null && stockdrugItem.getQelType()==2 && stockdrugItem.getDrugInfo().size()>0) {
                    List<TaskListInfoItem> drugInfos =  stockdrugItem.getDrugInfo();

                    if(drugInfos.get(0).getStockid() == activity.replaceDrugeOld.getID() && drugInfos.get(0).getStatus()==1 && ActionUrl.OLDDRUGE.equals(activity.currentStatus)
                            || drugInfos.get(0).getStockid() == activity.replaceDrugeNew.getID() && drugInfos.get(0).getStatus()==1 && ActionUrl.NEWDRUGE.equals(activity.currentStatus)
                            ) {
                        drugInfos.get(0).setIsFocus("true");
                        successCount++;
                    }else{
                        drugInfos.get(0).setIsFocus("false");
                        errorCount++;
                    }
                    tagList.add(drugInfos.get(0));
                    tempList.add(drugInfos.get(0));
                }
                adapter.notifyDataSetChanged();
                updateCommitStatus();
                //数据提交操作
            }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message ms) {

        }
    });

}
