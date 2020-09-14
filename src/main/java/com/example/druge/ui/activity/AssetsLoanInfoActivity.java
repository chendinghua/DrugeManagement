package com.example.druge.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.adapter.AutoAdapter;
import com.example.druge.commonCode.dao.AssetsLoanInfoGetRfid;
import com.example.druge.entry.InventoryCommitData;
import com.example.druge.entry.RfidList;
import com.example.druge.entry.Stockdrug;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.sqlite.DBHelper;
import com.example.druge.sqlite.Dao.AddInfoTaskDao;
import com.example.druge.sqlite.Dao.SynTempInfoDao;
import com.example.druge.tools.AlertDialogCallBack;
import com.example.druge.tools.AlertDialogNegativeCallBack;
import com.example.druge.tools.DialogUtils;
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
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.dialog.ProgressDialog;
import com.example.druge.ui.view.FingerDrawView;
import com.example.druge.ui.view.ScrollListView;
import com.example.druge.ui.view.SlideCutListView;
import com.example.druge.widget.LazyViewPager;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**  借出和归还
 * Created by Administrator on 2019/2/25/025.
 */
public class AssetsLoanInfoActivity extends BaseTabFragmentActivity implements View.OnClickListener, LazyViewPager.OnPageChangeListener,AdapterView.OnItemLongClickListener,SlideCutListView.RemoveListener{

    //存储临时异常的rfid标签集合
    List<String> errorMaps = new ArrayList<>();


    //上传的临时数据集合 如再次扫描到了视为错误数据
    private List<String> errorDatas = new ArrayList<>();
    @BindView(id= R.id.tv_assets_add_all_count)
    TextView tvAllCount;
    @BindView(id= R.id.tv_assets_add_current_count)
    TextView tvCurrentCount;
    @BindView(id= R.id.tv_assets_add_error_count)
    TextView tvErrorCount;



    @BindView(id= R.id.lv_assets_add_list)
    ScrollListView assetsAddList;             //listView数据集合控件

    BaseAdapter adapter ;                 //listView适配器

    List<TaskListInfoItem> tagList = new ArrayList<>();   //存储显示listView集合对象
    List<TaskListInfoItem> tempList = new ArrayList<>();  //临时存储listView集合对象

    @BindView(id= R.id.btn_assets_add_low_power)
    private Button lowFrequency;         //低频
    @BindView(id= R.id.btn_assets_add_medium_power)
    private Button mediumFrequency;       //中频
    @BindView(id= R.id.btn_assets_add_high_power)
    private Button  highFrequency;        //高频
    @BindView(id= R.id.tv_assets_add_current_power)
    private TextView currentFrequency;     //当前频率


    int successCount;                       //成功扫描数量
    int errorCount;                         //异常扫描数量
    int currentDataCount;                   //当前加载的数量

    //记录扫描操作页面扫描频率
    int parentFrequency;
    //记录扫描单个频率
    int subStrFrequency=1;
    //当前异常下标
    int currentIndex=-1;
    SharedPreferences sp;

    boolean isRefreshLoading = false;               //滑动刷新控件标识变量

    //刷新控件
    @BindView(id= R.id.sf_assets_add_refresh)
    private SwipeRefreshLayout productScanRefresh;
    //提交控件
    @BindView(id= R.id.btn_assets_add_commit)
    Button btnCommit;

    AssetsLoanInfoGetRfid loopRead;

    @BindView(id= R.id.finger_assets_loan_sign)
    FingerDrawView fingerAssetsLoanSign;

    @BindView(id= R.id.btn_assets_loan_cancel)
    Button btnCancel;
    private  int taskType;

    //是否上传签名
    boolean isSign=false;

    //设置功率布局
    @BindView(id= R.id.layout_assets_loan_power)
    LinearLayout layoutPower;

    //数量布局
    @BindView(id= R.id.layout_assets_loan_title)
    LinearLayout layoutTitile;

    //签名显示图片
    @BindView(id= R.id.image_sign_result)
    ImageView imageSignResult;

    int attID=-1;

    @BindView(id= R.id.et_add_assets_remark)
    EditText etRemark;

    @BindView(id= R.id.tv_add_storage_name_title)
    TextView storageNameTitle;


    private Integer stockId;

    private AddInfoTaskDao loanInfoTaskDao;

    ProgressDialog progress;
    @BindView(id=R.id.btn_refresh)
    Button btnRefresh;

    Context mContext;

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_add_info);
        mContext = this;
        progress = ProgressDialog.createDialog(this);
        AnnotateUtil.initBindView(this);
        loopRead = new AssetsLoanInfoGetRfid();
        loanInfoTaskDao  = new AddInfoTaskDao(this);
        loanInfoTaskDao.deleteOrder();
        getActionBar().hide();
        //初始化UI控件
        initUI();
        //初始化控件点击事件
        initListener();
        //初始化rfid读取模块
        initUHF();
        //rfid读取模块功率初始化
        mRreaderLoading();
        loading();

        //设置显示部分标题
        storageNameTitle.setVisibility(View.VISIBLE);
    }

    private void mRreaderLoading() {

     /*   if(mReader.setPower( sp.getInt("mediumFrequency",15))){
            currentFrequency.setText("当前频率:中频");
        }else {
            currentFrequency.setText("模块初始化异常");
        }*/
        SettingPower.updateFrequency(mReader,this,2,currentFrequency,progress);

    }

    private void initListener() {

        lowFrequency.setOnClickListener(this);         //低频
        mediumFrequency.setOnClickListener(this);       //中频
        highFrequency.setOnClickListener(this);        //高频

        btnCancel.setOnClickListener(this);

        btnRefresh.setOnClickListener(this);

        assetsAddList.setOnItemLongClickListener(this);
        btnCommit.setOnClickListener(this);

    }

    private void initUI() {
        productScanRefresh.setSize(SwipeRefreshLayout.LARGE);
        //显示签字控件
        fingerAssetsLoanSign.setVisibility(View.VISIBLE);
        //隐藏签字图片
        imageSignResult.setVisibility(View.GONE);

        //显示重写控件
        btnCancel.setVisibility(View.VISIBLE);

        //设置进度圈的背景色。这里随便给他设置了一个颜色：浅绿色
        productScanRefresh.setProgressBackgroundColorSchemeColor(Color.CYAN);
        //设置进度动画的颜色。这里面最多可以指定四个颜色，我这也是随机设置的，大家知道怎么用就可以了
        productScanRefresh.setColorSchemeResources(android.R.color.holo_orange_dark
                ,android.R.color.holo_blue_dark
                ,android.R.color.holo_red_dark
                ,android.R.color.widget_edittext_dark);
        //设置手势滑动监听器
        productScanRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //发送一个延时1秒的handler信息
                //   handler.sendEmptyMessageDelayed(199,1000);
                loopRead.setLoopFlag(false);
                loopRead.clearSuccessCount();
                loopRead.clearList();
                successCount=0;
                isRefreshLoading = true;

                errorMaps.clear();
                loading();
            }
        });
        sp=getSharedPreferences("pda_config", Context.MODE_PRIVATE);


        lowFrequency.setTag(sp.getInt("lowFrequency",5)+"");
        mediumFrequency.setTag(sp.getInt("mediumFrequency",15)+"");
        highFrequency.setTag(sp.getInt("highFrequency",30)+"");


            adapter = new AutoAdapter<TaskListInfoItem>(this,tagList,"StorageId","RfidNo","SerialNo","AssestName","AssestTypeName","StorageStatusName");

        assetsAddList.setAdapter(adapter);

    }

    HandlerUtils handlerUtils = new HandlerUtils(this, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            //获取拣货列表信息
            if (MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().get("method")) && "LoadData".equals(msg.getData().get("bindDate"))) {

                tagList.clear();
                tempList.clear();
                adapter.notifyDataSetChanged();
                Stockdrug stockdrug = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);

                if(stockdrug!=null ){
                    stockId = stockdrug.getID();
                    List <TaskListInfoItem> items = stockdrug.getDrugInfo();

                        for (int i =0;i<items.size();i++){
                            items.get(i).setStorageStatusName(taskType==3?"在库":"不在库");
                        }
                    tagList.addAll(items);
                    tempList.addAll(items);
                    loanInfoTaskDao.foreachAddData(items);
                    currentDataCount = stockdrug.getDrugInfo().size();

                }
                errorCount = 0;
                successCount = 0;
                adapter.notifyDataSetChanged();
                updateCommitStatus();
                //  }
                if (isRefreshLoading) {
                    productScanRefresh.setRefreshing(false);
                    isRefreshLoading = false;
                }
                //用户点击提交数据成功
            }
            //查询资产信息
            /*else if (MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().getString("method"))) {
                Stockdrug stockdrug = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);
                if (stockdrug != null && stockdrug.getDrugInfo().size()>0) {
                    TaskListInfoItem listInfoItem = stockdrug.getDrugInfo().get(0);
                    //
                    boolean isData = false;
                    for (int i =0;i<tagList.size();i++){
                        if(tagList.get(i).getRfidNo().equals(listInfoItem.getRfidNo()) && "false".equals(tagList.get(i).getIsFocus())){
                            listInfoItem.setIsFocus("true");
                            listInfoItem.setTaDeId(tagList.get(i).getTaDeId());
                            //移除对应数据
                            tagList.remove(i);
                            //添加查询对应的数据
                            tagList.add(i,listInfoItem);
                            tempList.clear();
                            tempList.addAll(tagList);
                            isData=true;
                            successCount++;

                            adapter.notifyDataSetChanged();
                            updateCommitStatus();



                            SoundManager.getInstance(AssetsLoanInfoActivity.this).playSound(1);
                            break;
                        }
                    }
                    //根据RFID查到库存，但是库存信息和现有列表的信息比对不上。
                    if(!isData){
                        listInfoItem.setIsFocus("error");
                        tagList.add(0, listInfoItem);
                        tempList.add(0,listInfoItem);
                        loopRead.addErrorMaps(listInfoItem);
                        errorCount++;
                        adapter.notifyDataSetChanged();
                        updateCommitStatus();
                        SoundManager.getInstance(AssetsLoanInfoActivity.this).playSound(2);
                    }


                }
                //提交领用或者借出数据成功
            }*/else if (MethodEnum.POSTRECEADD.equals(msg.getData().getString("method"))){
                Toast.makeText(AssetsLoanInfoActivity.this,"数据提交成功",Toast.LENGTH_SHORT).show();
                mReader.free();
                finish();
            }else if(MethodEnum.POSTPRODUCTRETUN.equals(msg.getData().getString("method"))){
                Toast.makeText(AssetsLoanInfoActivity.this,"数据提交成功",Toast.LENGTH_SHORT).show();
                mReader.free();
                finish();
            }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message ms) {
            //异常数据显示

            if(MethodEnum.GETQELSTOCKDRUG.equals(ms.getData().getString("method"))  && "LoadData".equals(ms.getData().get("bindDate"))){
                Toast.makeText(getBaseContext(),"数据加载失败",Toast.LENGTH_SHORT).show();
            }/*else if(MethodEnum.GETQELSTOCKDRUG.equals(ms.getData().getString("method"))  ){
                Stockdrug stockdrug = JSON.parseObject(JSON.parseObject(ms.getData().getString("result")).getString("Data"), Stockdrug.class);

                if(stockdrug!=null) {
                    TaskListInfoItem listInfoItem = new TaskListInfoItem();
                    listInfoItem.setRfidNo(stockdrug.getRfidNo());
                    listInfoItem.setAssestName("未知标签类型");
                    listInfoItem.setIsFocus("error");
                    tagList.add(0, listInfoItem);
                    loopRead.addErrorMaps(listInfoItem);
                    errorCount++;

                    adapter.notifyDataSetChanged();
                    updateCommitStatus();
                    SoundManager.getInstance(AssetsLoanInfoActivity.this).playSound(2);
                }
            }*/else if (MethodEnum.POSTRECEADD.equals(ms.getData().getString("method"))){
                Toast.makeText(AssetsLoanInfoActivity.this,"数据提交失败",Toast.LENGTH_SHORT).show();
            }else if(MethodEnum.POSTPRODUCTRETUN.equals(ms.getData().getString("method"))){
                Toast.makeText(AssetsLoanInfoActivity.this,"数据提交失败",Toast.LENGTH_SHORT).show();
            }
        }
    });
    //更新签名图片上传之后的状态
    private void updateSignControlStatus(){
        if(isSign){
            layoutPower.setVisibility(View.GONE);
            layoutTitile.setVisibility(View.GONE);
            productScanRefresh.setEnabled(false);
            fingerAssetsLoanSign.setVisibility(View.GONE);
            imageSignResult.setVisibility(View.VISIBLE);
            btnCancel.setText("返回");

        }else{
            layoutPower.setVisibility(View.VISIBLE);
            layoutTitile.setVisibility(View.VISIBLE);
            productScanRefresh.setEnabled(true);
            fingerAssetsLoanSign.setVisibility(View.VISIBLE);
            imageSignResult.setVisibility(View.GONE);
            btnCancel.setText("重写");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode){
            case 280:
                //判断是否提交签名
                if(!isSign)
                readTag();

                break;
            case  KeyEvent.KEYCODE_BACK:
                if(loopRead.getLoopFlag()){
                    UIHelper.ToastMessage(this,R.string.stopWarning);
                    return true;
                }

                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {
                        mReader.free();
                        finish();
                    }
                },"是否结束当前流程",null,null);
               break;
        }

        return super.onKeyDown(keyCode, event);


    }

    private void loading() {
        progress.show();
        taskType = getIntent().getExtras().getInt("taskType");
        /*String RfidNo = getIntent().getExtras().getString("RfidNo");
        Log.d("rfidNoLog", "loading: loaninfo   "+RfidNo);
        HashMap<String, Object> receiveProductScanOperationParam = new HashMap<>();

        receiveProductScanOperationParam.put("RFIDno",RfidNo);
        receiveProductScanOperationParam.put("UserID",UserConfig.UserId);
        receiveProductScanOperationParam.put("UserName",UserConfig.UserName);
        InteractiveDataUtil.interactiveMessage(this,receiveProductScanOperationParam,handlerUtils, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET,"LoadData");*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                SynTempInfoDao synTempInfoDao = new SynTempInfoDao(mContext);
                SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
                try {
                    Message msg =   handlerOffline.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data",(Serializable) synTempInfoDao.getTaskDataList(db, taskType,new Integer[]{3,5}));
                    msg.setData(bundle);
                    msg.what=1;
                    handlerOffline.sendMessage(msg);

                }catch (Exception e){

                }finally {
                    db.close();
                }
            }
        }).start();

    }

    Handler handlerOffline = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                //初始化数据
                case 1:
                    List<TaskListInfoItem> taskListInfoItems =( List<TaskListInfoItem>)  msg.getData().getSerializable("data");

                    if(taskListInfoItems!=null){

                  /*      for (int i =0;i<items.size();i++){
                            items.get(i).setStorageStatusName("在库");
                        }
*/
                        tagList.addAll(taskListInfoItems);
                        tempList.addAll(taskListInfoItems);
                        currentDataCount = taskListInfoItems.size();
                        //       addInfoTaskDao.foreachAddData(items);
                    }
                    errorCount = 0;
                    successCount = 0;
                    adapter.notifyDataSetChanged();
                    updateCommitStatus();
                    //  }
                    if (isRefreshLoading) {
                        productScanRefresh.setRefreshing(false);
                        isRefreshLoading = false;
                    }

                    progress.dismiss();

                    break;
                case 2:
                    if(msg.getData().getBoolean("isSuccess",false)){
                        UIHelper.ToastMessage(mContext, "提交盘点数据成功");
                        mReader.free();
                        finish();
                    }else{
                        UIHelper.ToastMessage(mContext, "提交盘点数据失败");

                    }
                    break;
            }
        }
    };


    public  void readTag() {
        if (!loopRead.getLoopFlag()) {
            //  mContext.mReader.setEPCTIDMode(true);
            if (mReader.startInventoryTag(0,0)) {
                //设置读取标识
                loopRead.setLoopFlag(true);
                //赋值临时存储集合
                loopRead.setTempList(tempList);
                //读取返回的handler
                loopRead.read(handler);
                assetsAddList.setScrollEnable(false);
                assetsAddList.setOnItemLongClickListener(null);
                //   new AssetsLoanInfoActivity.TagThread().start();
                //   setViewEnabled(false);
            } else {
                assetsAddList.setScrollEnable(true);
                assetsAddList.setOnItemLongClickListener(this);
                //用户点击停止识别则暂停上面扫描的线程
                mReader.stopInventory();
                UIHelper.ToastMessage(this, R.string.uhf_msg_inventory_open_fail);
            }
        } else {// 停止识别
            destroyReadTag();
            assetsAddList.setScrollEnable(true);
            assetsAddList.setOnItemLongClickListener(this);
        }
    }
    //销毁读取
    public void destroyReadTag(){
        //  tagHandler.removeCallbacks(myRunable);
    //    successCount = loopRead.getSuccessCount();
        updateCommitStatus();
        loopRead.setLoopFlag(false);
    }

    //扫描返回数据结果
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case -1:

                    SoundManager.getInstance(AssetsLoanInfoActivity.this).playSound(2);
                    break;
                //rfid标签存在
                case 1:
                    tagList.clear();
                    tagList.addAll((List<TaskListInfoItem>)msg.obj);
                    adapter.notifyDataSetChanged();
                    successCount++;
                    updateCommitStatus();
                    SoundManager.getInstance(AssetsLoanInfoActivity.this).playSound(1);
                    break;
                case 2:
                    SoundManager.getInstance(AssetsLoanInfoActivity.this).playSound(1);
                    break;

                //标签不存在
                case -2:
                    String rfid =msg.getData().getString("rfid");


                    TaskListInfoItem listInfoItem = new TaskListInfoItem();
                    listInfoItem.setRfidNo(rfid);
                    listInfoItem.setAssestName("未知标签类型");
                    listInfoItem.setIsFocus("error");
                    tagList.add(0, listInfoItem);
                    tempList.add(0,listInfoItem);
                    loopRead.addErrorMaps(listInfoItem);
                    errorCount++;

                    adapter.notifyDataSetChanged();
                    updateCommitStatus();
                    SoundManager.getInstance(AssetsLoanInfoActivity.this).playSound(2);
                    break;
            }
            updateCommitStatus();
            super.handleMessage(msg);
        }
    };

    /**
     * 更改提交按钮状态
     */
    public void updateCommitStatus(){
        tvAllCount.setText(currentDataCount+"");
        tvCurrentCount.setText(successCount+"");
        tvErrorCount.setText(errorCount+"");

       /* if(successCount==currentDataCount && taskType==3 && errorCount==0 || successCount>0 && taskType==5 && errorCount==0){
            btnCommit.setEnabled(true);
        }else{
            btnCommit.setEnabled(false);
        }*/
        if( successCount>0 && errorCount==0){
            btnCommit.setEnabled(true);
        }else{
            btnCommit.setEnabled(false);
        }

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_assets_add_low_power:
                SettingPower.updateFrequency(mReader,this,1,currentFrequency,progress);

            //    updateFrequency(1,currentFrequency,true);
                break;
            case R.id.btn_assets_add_medium_power:
                SettingPower.updateFrequency(mReader,this,2,currentFrequency,progress);
           //     updateFrequency(2,currentFrequency,true);
                break;
            case R.id.btn_assets_add_high_power:

                SettingPower.updateFrequency(mReader,this,3,currentFrequency,progress);
              //  updateFrequency(3,currentFrequency,true);
                break;
            case  R.id.btn_assets_loan_cancel:
                if(!isSign) {
                    fingerAssetsLoanSign.resetCanvas();
                }else{
                    isSign=false;
                    attID=-1;
                    updateSignControlStatus();
                }
                break;
            case R.id.btn_refresh:
                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {
                        clearListData();

                        loading();

                     //   new Handler().postDelayed(runnable,1000);
                    }
                },"是否刷新数据",null,null);


                break;

            //数据提交
            case  R.id.btn_assets_add_commit:
                if(loopRead.getLoopFlag()){
                    Toast.makeText(this,"请停止扫描再提交数据",Toast.LENGTH_SHORT).show();
                    return;
                }
          /*      final HandlerUtilsCallback handlerUtilsCallback = 	new HandlerUtilsCallback() {
                    @Override
                    public void handlerExecutionFunction(Message msg) {

                        Toast.makeText(AssetsLoanInfoActivity.this, "图片上传成功" , Toast.LENGTH_SHORT).show();
                        //获取上传图片返回的id
                        attID = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getJSONObject("Data").getString("FileID"),Integer.class);
                        //表示图片上传成功
                        isSign=true;
                        updateSignControlStatus();
                        imageSignResult.setImageBitmap(fingerAssetsLoanSign.getmBitmap());

                    }
                };
                final HandlerUtilsErrorCallback handlerUtilsErrorCallback = new HandlerUtilsErrorCallback() {
                    @Override
                    public void handlerErrorFunction(Message ms) {
                   //     UIHelper.ToastMessage(AssetsLoanInfoActivity.this,JSON.parseObject(JSON.parseObject(ms.getData().getString("result")).getString("Message"),String.class));
                        UIHelper.ToastMessage(AssetsLoanInfoActivity.this,ms.getData().getString("result"));
                        isSign=false;
                        attID=-1;
                        updateSignControlStatus();

                    }
                };
                //判断是否上传图片数据
                if(!isSign) {

                    DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                        @Override
                        public void alertDialogFunction() {
                            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), fingerAssetsLoanSign.getmBitmap(), null, null));
                            final HashMap<String, Object> param = new HashMap<String, Object>();
                            param.put("file", uri2File(uri));
                            InteractiveDataUtil.interactiveMessage(AssetsLoanInfoActivity.this, param, new HandlerUtils(AssetsLoanInfoActivity.this,
                                    handlerUtilsCallback, handlerUtilsErrorCallback), MethodEnum.UPLOADFILE + UserConfig.UserId, InteractiveEnum.UPLOAD);

                        }
                    }, new AlertDialogNegativeCallBack() {
                        @Override
                        public void alertDialogFunction() {
                        }
                    }, "是否上传签名图片", null, null);
          }else{
                    //判断签名图片数据的id是否有效
                    if(attID!=-1) {

                        DialogUtils.showAlertDialog(AssetsLoanInfoActivity.this, new AlertDialogCallBack() {
                            @Override
                            public void alertDialogFunction() {
                                //借出
                                 if(taskType==3){
                                  HashMap<String,Object> commitAllotParam  = new HashMap<String, Object>();
                                     commitAllotParam.put("opuserid", UserConfig.UserId);
                                     commitAllotParam.put("AttID", attID);
                                    commitAllotParam.put("opUserName", UserConfig.UserName);
                                     commitAllotParam.put("Remark",etRemark.getText().toString());
                                     commitAllotParam.put("StockID",stockId);
                                     InteractiveDataUtil.interactiveMessage(AssetsLoanInfoActivity.this,commitAllotParam,handlerUtils, MethodEnum.POSTRECEADD, InteractiveEnum.POST);

                                //归还
                                }else if(taskType==5){
                                    List<RfidList> rfidLists  = new ArrayList<RfidList>();

                                     for (int i = 0;i<tagList.size();i++){
                                       RfidList rfidList =  new RfidList(tagList.get(i).getRfidNo(),tagList.get(i).getStorageId(),("true".equals(tagList.get(i).getIsFocus())?2:1),tagList.get(i).getStorageId());
                                        rfidLists.add(rfidList);
                                     }

                                     HashMap<String, Object> commitLoanMap = new HashMap<>();
                                     commitLoanMap.put("opuserid", UserConfig.UserId);
                                     commitLoanMap.put("OpuserName", UserConfig.UserName);
                                     commitLoanMap.put("AttID", attID);
                                     commitLoanMap.put("remark", etRemark.getText().toString());
                                     commitLoanMap.put("RFIDList",rfidLists);
                                     commitLoanMap.put("StockID",stockId);
                                     //领用和借出数据提交
                                     InteractiveDataUtil.interactiveMessage(AssetsLoanInfoActivity.this, commitLoanMap, handlerUtils, MethodEnum.POSTPRODUCTRETUN, InteractiveEnum.POST);
                                 }
                            }
                        }, new AlertDialogNegativeCallBack() {
                            @Override
                            public void alertDialogFunction() {

                            }
                        },"请仔细核对数据",null,null);



                    }else{
                        isSign=false;
                        updateSignControlStatus();
                    }
                }*/
                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {
                      /*  //数据提交
                        HashMap<String, Object> addParam = new HashMap<>();
                        addParam.put("Taskid", taskId);
                        addParam.put("dataid", dataId);
                        addParam.put("opuser", UserConfig.UserName);
                        addParam.put("opuserid", UserConfig.UserId);
                        addParam.put("remark", etRemark.getText().toString());
                        if(taskType==1 || taskType==2){
                            addParam.put("Type",taskType);
                        }
                        InteractiveDataUtil.interactiveMessage(AssetsAddInfoActivity.this, addParam, handlerUtils,taskType==6?MethodEnum.UPDATEOUTSTORAGE:MethodEnum.UPDATEADD , InteractiveEnum.POST);*/



                        //   }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<InventoryCommitData> commitDatas = new ArrayList<InventoryCommitData>();
                                for (int i =0 ; i<tagList.size();i++){

                                    InventoryCommitData data = new InventoryCommitData();
                                    if("false".equals( tagList.get(i).getIsFocus())) {
                                        data.setStatus(2);
                                    }else {
                                        data.setStatus(1);
                                    }
                                    data.setId(tagList.get(i).getTaDeId());
                                    data.setRfidNo(tagList.get(i).getRfidNo());
                                    commitDatas.add(data);


                                }
                                HashMap<String, Object> commitParam = new HashMap<String, Object>();
                                commitParam.put("ID", taskType);
                                commitParam.put("EndTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                commitParam.put("Result", etRemark.getText().toString());
                                commitParam.put("DetailList", commitDatas);
                                SynTempInfoDao dao = new SynTempInfoDao(mContext);
                                SQLiteDatabase db =  DBHelper.getInstance(mContext).getWritableDatabase();
                                Message msg =   handlerOffline.obtainMessage();
                                Bundle bundle = new Bundle();
                                msg.what=2;
                                bundle.putBoolean("isSuccess",dao.addIventoryCommit(db, commitParam));
                                msg.setData(bundle);
                                handlerOffline.sendMessage(msg);
                            }
                        }).start();


                    }
                }, new AlertDialogNegativeCallBack() {
                    @Override
                    public void alertDialogFunction() {

                    }
                },"是否提交数据",null,null);

                break;
        }
    }


    public void clearListData(){
        loopRead.setLoopFlag(false);
        loopRead.clearSuccessCount();
        loopRead.clearList();
        successCount=0;
        errorCount=0;
        currentDataCount=0;
        isRefreshLoading = true;
        updateCommitStatus();
        tempList.clear();
        tagList.clear();
        errorMaps.clear();
        adapter.notifyDataSetChanged();
    }





    //ListView长按事件触发  用来删除异常扫描到的数据
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(loopRead.getLoopFlag()){
            UIHelper.ToastMessage(this,"请停止扫描再长按删除");
            return false;
        }
        if (tagList.get(position).getIsFocus().equals("error")) {
        }
        return false;
    }
    //listView删除操作
    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        if("error".equals(tagList.get(position).getIsFocus())) {
            tagList.remove(position);

            for (int i =0;i<errorMaps.size();i++){
                if(errorMaps.get(i).equals(tagList.get(position))){
                    errorMaps.remove(i);
                }
            }

            adapter.notifyDataSetChanged();
            errorCount--;
            updateCommitStatus();
            UIHelper.ToastMessage(this,"删除数据成功");
        }else{
            UIHelper.ToastMessage(this,"非异常数据无法删除");
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
