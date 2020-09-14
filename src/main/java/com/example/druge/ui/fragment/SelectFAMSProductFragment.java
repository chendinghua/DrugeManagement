package com.example.druge.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.adapter.AutoAdapter;
import com.example.druge.commonCode.LoopRead;
import com.example.druge.commonCode.dao.SimpleGetRfid;
import com.example.druge.entry.Stockdrug;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.tools.AlertDialogCallBack;
import com.example.druge.tools.DialogUtils;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.SettingPower;
import com.example.druge.tools.SoundManage;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.UserConfig;
import com.example.druge.ui.activity.HomeActivity;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.dialog.ProgressDialog;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by Administrator on 2019/2/22/022.
 */

/**
 * 产品信息查询页面
 */
public class SelectFAMSProductFragment extends Fragment implements View.OnClickListener{

    private Button lowFrequency;         //低频
    private Button mediumFrequency;       //中频
    private Button  highFrequency;        //高频
    private TextView currentFrequency;     //当前频率
    HomeActivity activity;
    SharedPreferences sp;

    private BaseAdapter adapter ;

    List<TaskListInfoItem> tagList = new ArrayList<>();

    @BindView(id=R.id.tv_select_fams_product_productName)
     TextView tvProductName;
    @BindView(id=R.id.tv_select_fams_product_unit)
     TextView tvProductUnit;
    @BindView(id=R.id.tv_select_fams_product_opUser)
     TextView tvProductOpUser;
    @BindView(id=R.id.tv_select_fams_product_new_time)
     TextView tvProductNewTime;
    @BindView(id=R.id.tv_select_fams_product_opType)
     TextView tvProductOpType;



    EditText scanRfid;

    Button btnSelect;
    //出库列表的layout布局(用来整体隐藏和显示信息)
    LinearLayout stockListLayout;

    ListView selectProductList;


    RadioButton rbScanCode;

    RadioButton rbScanRfid;


    LoopRead loopRead;
    @BindView(id=R.id.layout_durge_info)
    LinearLayout layoutDruge;
    @BindView(id=R.id.layout_stock_info)
    LinearLayout layoutStock;

    @BindView(id=R.id.tv_select_druge_stock_name)
    TextView tvStockName;
    @BindView(id=R.id.tv_select_druge_stock_serialNo)
    TextView tvStockSerialNo;
    @BindView(id=R.id.tv_select_druge_stock_max)
    TextView tvStockMax;
    @BindView(id=R.id.tv_select_druge_stock_opUser)
    TextView tvStockOpuser;

    @BindView(id=R.id.tv_select_fams_product_serialNo)
    TextView tvProductSerialNo;
    //药箱内药品信息集合布局
    @BindView(id=R.id.layout_stock_item)
    LinearLayout layoutStockItem;

    @BindView(id=R.id.layout_select_radio)
    LinearLayout layoutRadio;
    //提交检测描述布局
    @BindView(id=R.id.layout_select_remark)
    LinearLayout layoutRemark;
    //检测提交结果按钮
    @BindView(id=R.id.btn_select_commit)
    Button btnSelectCommit;
    @BindView(id=R.id.et_select_remark)
    EditText etRemark;
    //药品信息对象
    TaskListInfoItem taskListInfoItem;
    @BindView(id=R.id.tv_select_druge_name)
    TextView tvDrugeName;

    @BindView(id=R.id.tv_select_current_count)
    TextView currentCount;

    ProgressDialog progress;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_fams_product,container,false);
        activity =(HomeActivity) getActivity();
        progress = ProgressDialog.createDialog(activity);
        //注解自动绑定控件
        AnnotateUtil.initBindView(this,view);
         loopRead = new SimpleGetRfid();

        //初始化UI控件
        initUI(view);
        //初始化控件点击事件
        initListener();
        //初始化读写频率
        mRreaderLoading();
        return view;
    }
    //二维码扫描回调函数
    public Barcode2DWithSoft.ScanCallback  ScanBack1= new Barcode2DWithSoft.ScanCallback(){
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            String barcode;
            if (length < 1) {
                if (length == -1) {
                    UIHelper.ToastMessage(activity,"Scan cancel");// inputServer.setText("Scan cancel");
                } else if (length == 0) {
                    UIHelper.ToastMessage(activity,"Scan TimeOut");   //inputServer.setText("Scan TimeOut");
                } else {
                    //  Log.i(TAG,"Scan fail");
                }
            }else{
                SoundManage.PlaySound(activity, SoundManage.SoundType.SUCCESS);
                barcode="";
                //  String res = new String(dd,"gb2312");
                try {
                    // Log.i("Ascii",seldata);
                    barcode = new String(bytes, 0, length, "ASCII");
                    zt();
                }
                catch (UnsupportedEncodingException ex)   {}

                scanRfid.setText(barcode);
                // inputServer.setText(barCode);
               Log.d("ScanCodeEnabled", "扫描到了数据 "+barcode);
               /*  HashMap<String,Object> scanCodeParam = new HashMap<String, Object>();
                scanCodeParam.put("jobID",barcode);
                InteractiveDataUtil.interactiveMessage(scanCodeParam,handler,MethodEnum.GETINSTOCKINFOBYJONID,InteractiveEnum.GET);
*/
            }

        }
    };
    void zt() {

        Vibrator vibrator = (Vibrator)activity.getSystemService(activity.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    //扣动物理按键触发的扫描rfid数据
    public void read(){
        Log.d("readTag", "read: "+rbScanRfid.isChecked());

     //   if(rbScanRfid.isChecked()) {
            if (!getResources().getBoolean(R.bool.is_main_scan_cycle)) {
                try {
                    RFIDWithUHF initUHF = RFIDWithUHF.getInstance();
                    initUHF.free();
                    initUHF.init();
                    String strUII = RFIDWithUHF.getInstance().inventorySingleTag();
                    if (!TextUtils.isEmpty(strUII)) {
                        String strResult = "";
                        //这里是用来生成编码的
                        final String strEPC = RFIDWithUHF.getInstance().convertUiiToEPC(strUII);
                        if ( strEPC.startsWith("1001") ||  strEPC.startsWith("2001")) {
                            scanRfid.setText(strEPC);
                        } else {
                            UIHelper.ToastMessage(activity, "扫描标签类型错误");
                            scanRfid.setText("");
                        }

                    } else {
                        UIHelper.ToastMessage(activity, R.string.uhf_msg_inventory_fail);
                    }
                } catch (Exception e) {
                }
            } else {
                readTag();
            }
            return;
    }
    boolean loopFlag = false;
    //读取标签
    public  void readTag() {
        try {
            RFIDWithUHF mReader =      RFIDWithUHF.getInstance();
            if (!loopFlag) {

                if (mReader.startInventoryTag(0,0)) {
                    loopFlag = true;
                    loopRead.setLoopFlag(true);
                    loopRead.setScanType(rbScanRfid.isChecked());
                    loopRead.read(handler);
                 //   new SelectFAMSProductFragment.TagThread().start();
                } else {
                    //用户点击停止识别则暂停上面扫描的线程
                    mReader.stopInventory();
                    UIHelper.ToastMessage(activity,R.string.uhf_msg_inventory_open_fail);
                }
            } else {// 停止识别
                loopRead.setLoopFlag(false);
                loopRead.closeRead();

                loopFlag=false;
            }
        }catch (ConfigurationException e){
            e.printStackTrace();
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    scanRfid.setText(msg.getData().getString("rfid"));
                    loopFlag=false;
                    break;
                case -1:
                    UIHelper.ToastMessage(activity,"扫描货品标签类型错误");
                    break;
            }
        }
    };

    HandlerUtils handlerUtils = new HandlerUtils(activity, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
                //根据扫描到的库存RFID获取到库存的详细信息
                if (MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().getString("method"))) {
                    Stockdrug stockdrug =   JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);

                    //判断扫描的是药品
                    if(rbScanCode.isChecked()){
                        if(stockdrug!=null && stockdrug.getDrugInfo().size()>0){
                            taskListInfoItem  = stockdrug.getDrugInfo().get(0);

                            tvProductName.setText(taskListInfoItem.getAssestName());
                            tvProductUnit.setText(taskListInfoItem.getAssestTypeName());
                            tvProductOpUser.setText(taskListInfoItem.getOverTime());
                            tvProductNewTime.setText(taskListInfoItem.getBeginTime());

                            tvProductOpType.setText(taskListInfoItem.getStatusName());
                            tvProductSerialNo.setText(taskListInfoItem.getSerialNo());
                            tvDrugeName.setText(taskListInfoItem.getStockName());
                        }
                    //判断是医药箱
                    }else{
                     if(stockdrug!=null){
                          tvStockName.setText(stockdrug.getName());
                          tvStockSerialNo.setText(stockdrug.getStockNo());
                          tvStockMax.setText(stockdrug.getMaxNum()+"");
                          tvStockOpuser.setText(stockdrug.getOpUser());
                         for (int i =0;i<stockdrug.getDrugInfo().size();i++){
                             stockdrug.getDrugInfo().get(i).setIsFocus("success");
                         }
                         currentCount.setText(""+stockdrug.getDrugInfo().size());
                         tagList.clear();
                         tagList.addAll(stockdrug.getDrugInfo());
                         adapter.notifyDataSetChanged();
                     }
                    }
                }else if(MethodEnum.ADDCHECK.equals(msg.getData().getString("method"))){
                    clearData(true);
                    Toast.makeText(activity,"检查数据提交成功",Toast.LENGTH_SHORT).show();
                }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message msg) {
            if (MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().getString("method"))) {
                UIHelper.ToastMessage(activity, "扫描条码数据不存在,请重新扫描");
                clearData(false);
            }else if(MethodEnum.ADDCHECK.equals(msg.getData().getString("method"))){
                clearData(true);
                Toast.makeText(activity,"检查数据提交失败",Toast.LENGTH_SHORT).show();
            }
        }
    });
    //清除数据
    private void clearData(boolean flag){
         tvProductName.setText("");
         tvProductUnit.setText("");
         tvProductOpUser.setText("");
         tvProductNewTime.setText("");
         tvProductOpType.setText("");
        tvProductSerialNo.setText("");

         tvStockName.setText("");
         tvStockSerialNo.setText("");
         tvStockMax.setText("");
         tvStockOpuser.setText("");
        tvDrugeName.setText("");
        currentCount.setText("");
        taskListInfoItem=null;

        tagList.clear();
        adapter.notifyDataSetChanged();
        if(flag) {
            scanRfid.setText("");
        }

    }

    private void initListener() {
        lowFrequency.setOnClickListener(this);         //低频
        mediumFrequency.setOnClickListener(this);       //中频
        highFrequency.setOnClickListener(this);        //高频
        btnSelect.setOnClickListener(this);

        rbScanCode.setOnClickListener(this);
        rbScanRfid.setOnClickListener(this);
        btnSelectCommit.setOnClickListener(this);
    }
    private void mRreaderLoading() {
         /*   if(activity.mReader.setPower( sp.getInt("lowFrequency",5))){
                currentFrequency.setText("当前频率:低频");
            }else {
                currentFrequency.setText("模块初始化异常");
            }*/
        SettingPower.updateFrequency(activity.mReader,activity,2,currentFrequency,progress);
    }
    private void initUI(View view) {
        layoutStock.setVisibility(View.GONE);
        layoutStockItem.setVisibility(View.GONE);
        layoutDruge.setVisibility(View.VISIBLE);

        if(activity.actionUrl==2){
            layoutRadio.setVisibility(View.GONE);
            layoutRemark.setVisibility(View.VISIBLE);
            btnSelectCommit.setVisibility(View.VISIBLE);
        }else{
            layoutRadio.setVisibility(View.VISIBLE);
            layoutRemark.setVisibility(View.GONE);
            btnSelectCommit.setVisibility(View.GONE);
        }

        rbScanCode = view.findViewById(R.id.rb_scanCode);
        rbScanRfid = view.findViewById(R.id.rb_scanRfid);


        selectProductList = view.findViewById(R.id.lv_select_boeing_outStockList);
        stockListLayout = view.findViewById(R.id.stock_list_layout);
        sp=activity.getSharedPreferences("pda_config", Context.MODE_PRIVATE);
        currentFrequency = view.findViewById(R.id.tv_select_boeing_product_current_frequency);
        lowFrequency = view.findViewById(R.id.btn_select_boeing_product_low_frequency);
        mediumFrequency = view.findViewById(R.id.btn_select_boeing_product_medium_frequency);
        highFrequency = view.findViewById(R.id.btn_select_boeing_product_high_frequency);
        lowFrequency.setTag(sp.getInt("lowFrequency",5)+"");
        mediumFrequency.setTag(sp.getInt("mediumFrequency",15)+"");
        highFrequency.setTag(sp.getInt("highFrequency",30)+"");

        scanRfid = view.findViewById(R.id.et_select_boeing_scan_rfid);
        btnSelect = view.findViewById(R.id.btn_select_boeing_product_select);


        adapter = new AutoAdapter<TaskListInfoItem>(activity,tagList,"storageId","RfidNo","SerialNo","AssestName","AssestTypeName","StatusName");
        selectProductList.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_select_boeing_product_low_frequency:
                SettingPower.updateFrequency(activity.mReader,activity,1,currentFrequency,progress);

                break;
            case R.id.btn_select_boeing_product_medium_frequency:

                SettingPower.updateFrequency(activity.mReader,activity,2,currentFrequency,progress);
                break;
            case R.id.btn_select_boeing_product_high_frequency:

                SettingPower.updateFrequency(activity.mReader,activity,3,currentFrequency,progress);
            case R.id.btn_select_boeing_product_select:
                clearData(false);
                String rfidStr = scanRfid.getText().toString();
                if(!rfidStr.equals("")){
                     //根据RFID数据来查询货物数据
                        HashMap<String, Object> moveParam = new HashMap<>();
                        moveParam.put("UserID", UserConfig.UserId);
                        moveParam.put("UserName",UserConfig.UserName);
                        moveParam.put("RFIDno",scanRfid.getText().toString());
                        InteractiveDataUtil.interactiveMessage(activity,moveParam, handlerUtils, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET);
                  //      UIHelper.ToastMessage(activity, "" + rfidStr);

                }else {
                    if(rbScanCode.isChecked()){
                        UIHelper.ToastMessage(activity, "请扫描条形码");
                    }else {

                        UIHelper.ToastMessage(activity, "请扫描rfid标签");
                    }
                }
                break;
            case R.id.rb_scanCode:
                layoutStock.setVisibility(View.GONE);
                layoutStockItem.setVisibility(View.GONE);
                layoutDruge.setVisibility(View.VISIBLE);
                clearData(true);
                break;
            case R.id.rb_scanRfid:
                layoutStock.setVisibility(View.VISIBLE);
                layoutStockItem.setVisibility(View.VISIBLE);
                layoutDruge.setVisibility(View.GONE);

                clearData(true);
                break;

            case R.id.btn_select_commit:
                if(taskListInfoItem!=null) {
                    DialogUtils.showAlertDialog(activity, new AlertDialogCallBack() {
                        @Override
                        public void alertDialogFunction() {
                            HashMap<String, Object> commitParam = new HashMap<>();
                            commitParam.put("StorageID",taskListInfoItem.getStorageId());
                            commitParam.put("OpUserID",UserConfig.UserId);
                            commitParam.put("OpUser",UserConfig.UserName);
                            commitParam.put("Remark",etRemark.getText().toString());
                            InteractiveDataUtil.interactiveMessage(activity, commitParam, handlerUtils, MethodEnum.ADDCHECK, InteractiveEnum.POST);
                        }
                    },"是否提交检查数据",null,null);
                   }else{
                    Toast.makeText(activity,"药品信息不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}