package com.example.druge.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.adapter.AutoAdapter;
import com.example.druge.commonCode.dao.AssetsAddInfoGetRfid;
import com.example.druge.entry.Inventory;
import com.example.druge.entry.InventoryCommitData;
import com.example.druge.entry.Stockdrug;
import com.example.druge.entry.TaskListInfo;
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
import com.example.druge.tools.Utils;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.dialog.ProgressDialog;
import com.example.druge.ui.view.FingerDrawView;
import com.example.druge.ui.view.ScrollListView;
import com.rscja.deviceapi.RFIDWithUHF;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**  新增
 * Created by 16486 on 2020/1/15.
 */
public class AssetsAddInfoActivity extends BaseTabFragmentActivity implements View.OnClickListener ,DialogInterface.OnKeyListener,AdapterView.OnItemLongClickListener {
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

    boolean loopFlag = false;                               //循环刷新标识

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

    HandlerUtils handlerUtils;

    //存储扫描异常数据
    List<String> errorMaps = new ArrayList<>();

    @BindView(id= R.id.tv_assets_add_all_count)
    TextView tvAllCount;
    @BindView(id= R.id.tv_assets_add_current_count)
    TextView tvCurrentCount;
    @BindView(id= R.id.tv_assets_add_error_count)
    TextView tvErrorCount;
    //循环查询对象
    AssetsAddInfoGetRfid loopRead;

    @BindView(id= R.id.et_add_assets_remark)
    EditText etRemark;
    //任务id
    int taskId;
    int dataId;
    //任务类型2：新增，5：归还
    int taskType;
    @BindView(id= R.id.tv_add_storage_name_title)
    TextView storageNameTitle;
    @BindView(id=R.id.finger_assets_loan_sign)
    FingerDrawView finger;
    @BindView(id=R.id.tv_title_sign)
    TextView tvTitleSign;

    AddInfoTaskDao addInfoTaskDao;
    @BindView(id=R.id.btn_refresh)
    Button btnRefresh;
    ProgressDialog progress;

    Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_add_info);
        mContext =this;

        progress = ProgressDialog.createDialog(this);
        getActionBar().hide();

        addInfoTaskDao = new AddInfoTaskDao(this);

        addInfoTaskDao.deleteOrder();

        loopRead = new AssetsAddInfoGetRfid();
        AnnotateUtil.initBindView(this);
        //初始化UI控件
        initUI();
        //初始化控件点击事件
        initListener();
        //初始化rfid读取模块
        initUHF();
        //rfid读取模块功率初始化
        mRreaderLoading();
        //设置隐藏部分标题
        storageNameTitle.setVisibility(View.GONE);
        handlerUtils  = new HandlerUtils(this, new HandlerUtilsCallback() {
            @Override
            public void handlerExecutionFunction(Message msg) {
                Log.d("URL", "loadingHandler");
                //初始化listView列表数据
                if (MethodEnum.GETTASKINFO.equals(msg.getData().getString("method"))) {

                    tagList.clear();
                    tempList.clear();
                    adapter.notifyDataSetChanged();
                    TaskListInfo taskListInfo = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), TaskListInfo.class);
                    //    if(scanInfoList!=null){



                    if(taskListInfo.getTaskDetailList()!=null){
                        List <TaskListInfoItem> items = taskListInfo.getTaskDetailList();

                  /*      for (int i =0;i<items.size();i++){
                            items.get(i).setStorageStatusName("在库");
                        }
*/
                        tagList.addAll(items);
                        tempList.addAll(items);
                        currentDataCount = taskListInfo.getTaskDetailList().size();
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
                    //获取正常资产信息
                } else if (MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().getString("method"))) {
                    Stockdrug stockdrugItem = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);
                    if (stockdrugItem != null && stockdrugItem.getQelType()==2 && stockdrugItem.getDrugInfo().size()>0) {
                        List<TaskListInfoItem> drugInfos =  stockdrugItem.getDrugInfo();
                        drugInfos.get(0).setIsFocus("error");

                        tagList.add(drugInfos.get(0));
                        tempList.add(drugInfos.get(0));
                        errorCount++;

                    }else if(stockdrugItem!=null && stockdrugItem.getDrugInfo().size()==0){
                        TaskListInfoItem taskListInfoItem = new TaskListInfoItem();
                        taskListInfoItem.setRfidNo(stockdrugItem.getRfidNo());
                        taskListInfoItem.setIsFocus("error");
                        tagList.add(taskListInfoItem);
                        tempList.add(taskListInfoItem);
                        errorCount++;
                    }
                    adapter.notifyDataSetChanged();
                    updateCommitStatus();
                    //数据提交操作
                }else if(MethodEnum.UPDATEADD.equals(msg.getData().getString("method"))){
                    clearBufferData();
                    Toast.makeText(AssetsAddInfoActivity.this,"数据提交成功",Toast.LENGTH_SHORT).show();
                    Utils.finishActivity(AssetsAddInfoActivity.this,mReader,null);

                }else if(MethodEnum.GETRETURNAFFIRM.equals(msg.getData().getString("method"))){
                    Toast.makeText(AssetsAddInfoActivity.this,"数据提交成功",Toast.LENGTH_SHORT).show();
                    Utils.finishActivity(AssetsAddInfoActivity.this,mReader,null);
                    //调拨入库数据提交
                }else if(MethodEnum.POSTSUMMITIN.equals(msg.getData().getString("method"))){
                    Toast.makeText(AssetsAddInfoActivity.this,"数据提交成功",Toast.LENGTH_SHORT).show();
                    Utils.finishActivity(AssetsAddInfoActivity.this,mReader,null);
                    //出库数据提交
                }else if(MethodEnum.UPDATEOUTSTORAGE.equals(msg.getData().getString("method"))){
                    Toast.makeText(AssetsAddInfoActivity.this,"数据提交成功",Toast.LENGTH_SHORT).show();
                    Utils.finishActivity(AssetsAddInfoActivity.this,mReader,null);
                }
            }
        }, new HandlerUtilsErrorCallback() {
            @Override
            public void handlerErrorFunction(Message ms) {
                //扫描查询显示异常资产信息
                if(MethodEnum.GETQELSTOCKDRUG.equals(ms.getData().getString("method"))){
                    Stockdrug stockdrugItem = JSON.parseObject(JSON.parseObject(ms.getData().getString("result")).getString("Data"), Stockdrug.class);
                    if (stockdrugItem != null ) {
                        TaskListInfoItem drugInfo =new TaskListInfoItem();
                        drugInfo.setIsFocus("error");
                        drugInfo.setRfidNo(stockdrugItem.getRfidNo());
                        tagList.add(drugInfo);
                        tempList.add(drugInfo);
                        errorCount++;

                    }
                    adapter.notifyDataSetChanged();
                    updateCommitStatus();
                }else if(MethodEnum.UPDATEADD.equals(ms.getData().getString("method"))){
                    Toast.makeText(AssetsAddInfoActivity.this,"数据提交失败",Toast.LENGTH_SHORT).show();
                }else if(MethodEnum.GETRETURNAFFIRM.equals(ms.getData().getString("method"))){
                    Toast.makeText(AssetsAddInfoActivity.this,"数据提交失败",Toast.LENGTH_SHORT).show();
                }else if(MethodEnum.POSTSUMMITIN.equals(ms.getData().getString("method"))){
                    Toast.makeText(AssetsAddInfoActivity.this,"数据提交失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //初始化数据
        initLoadingData();
    }


    //销毁读取
    public void destroyReadTag(){
        successCount = loopRead.getSuccessCount();
        updateCommitStatus();
        loopRead.setLoopFlag(false);
    }
    //初始化触发事件方法
    private void initListener() {

        lowFrequency.setOnClickListener(this);         //低频
        mediumFrequency.setOnClickListener(this);       //中频
        highFrequency.setOnClickListener(this);        //高频
        assetsAddList.setOnItemLongClickListener(this);
        btnCommit.setOnClickListener(this);

        btnRefresh.setOnClickListener(this);

        productScanRefresh.setSize(SwipeRefreshLayout.LARGE);
        btnCommit.setOnClickListener(this);
        //设置进度圈的背景色。这里随便给他设置了一个颜色：浅绿色
        productScanRefresh.setProgressBackgroundColorSchemeColor(Color.CYAN);
        //设置进度动画的颜色。这里面最多可以指定四个颜色，我这也是随机设置的，大家知道怎么用就可以了
        productScanRefresh.setColorSchemeResources(android.R.color.holo_orange_dark
                ,android.R.color.holo_blue_dark
                ,android.R.color.holo_red_dark
                ,android.R.color.widget_edittext_dark);
        //设置手势滑动监听器
      /*  productScanRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //发送一个延时1秒的handler信息
                //   handler.sendEmptyMessageDelayed(199,1000);
                loopRead.setLoopFlag(false);
                loopRead.clearSuccessCount();
                loopRead.clearList();
                successCount=0;
                isRefreshLoading = true;
                tempList.clear();
                tagList.clear();
                errorMaps.clear();
                adapter.notifyDataSetChanged();
                //  initLoadingData();
                new Handler().postDelayed(runnable,2000);

            }
        });*/
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

    Handler refreshHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            List<TaskListInfoItem> taskListInfoItems =  addInfoTaskDao.getAllDate();
            tempList.addAll(taskListInfoItems);
            tagList.addAll(taskListInfoItems);
            currentDataCount=taskListInfoItems.size();
            adapter.notifyDataSetChanged();
            updateCommitStatus();
            progress.dismiss();
          /*  productScanRefresh.setRefreshing(false);
            isRefreshLoading = false;*/
        }
    };
    private void clearBufferData(){
        addInfoTaskDao.deleteOrder();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshHandler.sendMessage(new Message());
        }
    };


    public void initLoadingData(){
        taskId=  getIntent().getExtras().getInt("taskId");
        dataId = getIntent().getExtras().getInt("dataId");
        taskType=getIntent().getExtras().getInt("taskType");
        if(taskType==1|| taskType==2 || taskType==6) {
            finger.setVisibility(View.GONE);
            tvTitleSign.setVisibility(View.GONE);
        }

        progress.show();
   /*     HashMap<String, Object> receiveProductScanOperationParam = new HashMap<>();
        receiveProductScanOperationParam.put("ID",taskId);
        Log.d("addInfo", "   "+taskId);
        InteractiveDataUtil.interactiveMessage(this,receiveProductScanOperationParam, handlerUtils, MethodEnum.GETTASKINFO, InteractiveEnum.GET);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                SynTempInfoDao synTempInfoDao = new SynTempInfoDao(mContext);
                SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
                try {
                    Message msg =   handlerOffline.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data",(Serializable) synTempInfoDao.getTaskDataList(db, taskId,new Integer[]{1,2,6}));
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


    private void mRreaderLoading() {
       SettingPower.updateFrequency(mReader,this,2,currentFrequency,progress);

        /* if(mReader.setPower( sp.getInt("mediumFrequency",15))){
            currentFrequency.setText("当前频率:中频");
        }else {
            currentFrequency.setText("模块初始化异常");
        }*/
    }
    private void initUI() {


        sp=getSharedPreferences("pda_config", Context.MODE_PRIVATE);
        lowFrequency.setTag(sp.getInt("lowFrequency",5)+"");
        mediumFrequency.setTag(sp.getInt("mediumFrequency",15)+"");
        highFrequency.setTag(sp.getInt("highFrequency",30)+"");
        adapter = new AutoAdapter<TaskListInfoItem>(this,tagList,"TaDeId","RfidNo","SerialNo","AssestName","AssestTypeName");
        assetsAddList.setAdapter(adapter);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 280:
                //判断是否提交签名
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
                        Utils.finishActivity(AssetsAddInfoActivity.this,mReader,null);
                    }
                },"是否结束当前流程",null,null);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    //读取标签
    public  void readTag() {
        if (!loopRead.getLoopFlag()) {
            if (this.mReader.startInventoryTag(0,0)) {
                loopRead.setLoopFlag(true);
                loopRead.setTempList(tempList);
                loopRead.read(readResultHandler);
                assetsAddList.setScrollEnable(false);
                assetsAddList.setOnItemLongClickListener(null);
            } else {
                //用户点击停止识别则暂停上面扫描的线程
                mReader.stopInventory();
                UIHelper.ToastMessage(this, R.string.uhf_msg_inventory_open_fail);
                assetsAddList.setScrollEnable(true);
                assetsAddList.setOnItemLongClickListener(this);
            }
        } else {// 停止识别
            destroyReadTag();
            assetsAddList.setScrollEnable(true);

            assetsAddList.setOnItemLongClickListener(this);

        }
    }

    @Override
    public void onClick(View v) {
        if(loopRead.getLoopFlag()){
            Toast.makeText(this,"请停止扫描",Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()){
            case R.id.btn_assets_add_low_power:
                progress.show();
                SettingPower.updateFrequency(mReader,this,1,currentFrequency,progress);
                //updateFrequency(1,currentFrequency,true);
                break;
            case R.id.btn_assets_add_medium_power:
                progress.show();
                SettingPower.updateFrequency(mReader,this,2,currentFrequency,progress);
                break;
            case R.id.btn_assets_add_high_power:
                progress.show();
                SettingPower.updateFrequency(mReader,this,3,currentFrequency,progress);

                break;
            //扫描提交事件
            case R.id.btn_assets_add_commit:
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
                                    data.setStatus(2);
                                    data.setId(tagList.get(i).getTaDeId());
                                    data.setRfidNo(tagList.get(i).getRfidNo());
                                    commitDatas.add(data);


                                }
                                HashMap<String, Object> commitParam = new HashMap<String, Object>();
                                commitParam.put("ID", taskId);
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
            case R.id.btn_refresh:
                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {
                        clearListData();
                      //  new Handler().postDelayed(runnable,1000);
                        initLoadingData();
                    }
                },"是否刷新数据",null,null);


                break;
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        destroyReadTag();
        if(loopRead.getLoopFlag()){
            UIHelper.ToastMessage(this,"请停止扫描再长按删除");
            return false;
        }
        if (tagList.get(position).getIsFocus().equals("error")) {
            myShowDialog(position);
        }
        return false;
    }
    //扫描返回数据结果
    Handler readResultHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case -1:
                    Log.d("playSoundError", "error "+loopFlag);
                    tagList.clear();
                    tagList.addAll((List<TaskListInfoItem>) msg.obj);
                    adapter.notifyDataSetChanged();
                    SoundManager.getInstance(AssetsAddInfoActivity.this).playSound(2);
                    break;
                case 1:
                    Log.d("playSoundError", "success "+loopFlag);
                    tagList.clear();
                    tagList.addAll((List<TaskListInfoItem>) msg.obj);
                    adapter.notifyDataSetChanged();
                    //  activity.playSound(1);
                    SoundManager.getInstance(AssetsAddInfoActivity.this).playSound(1);
                    successCount=loopRead.getSuccessCount();
                    updateCommitStatus();
                    break;
                case -2:
                    SoundManager.getInstance(AssetsAddInfoActivity.this).playSound(2);
                    String rfid =msg.getData().getString("rfid");
                  /*  if(errorMaps.size()==0){
                        errorMaps.add(rfid);
                        HashMap<String,Object> moveParam = new HashMap<>();
                        moveParam.put("RFIDNo",rfid);
                        moveParam.put("UserId", UserConfig.UserId);
                        moveParam.put("UserName",UserConfig.UserName);
                        InteractiveDataUtil.interactiveMessage(AssetsAddInfoActivity.this,moveParam,handlerUtils, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET);
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
                            InteractiveDataUtil.interactiveMessage(AssetsAddInfoActivity.this,moveParam,handlerUtils, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET);
                        }
                    }*/
                    TaskListInfoItem drugInfo =new TaskListInfoItem();
                    drugInfo.setIsFocus("error");
                    drugInfo.setRfidNo(rfid);
                    tagList.add(drugInfo);
                    tempList.add(drugInfo);
                    errorCount++;

                    break;
            }
            updateCommitStatus();
            super.handleMessage(msg);
        }
    };

    TextView tvSubStrFrequency;
    //显示扫描异常数据窗口(删除异常数据)
    private void myShowDialog(int index){
        currentIndex = index;
        final AlertDialog.Builder builder;
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        Button low=new Button(this);
        low.setText("低频");
        low.setTag("1");
        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPower.updateFrequency(mReader,AssetsAddInfoActivity.this,1,tvSubStrFrequency,progress);
            }
        });
        Button medium = new Button(this);
        medium.setText("中频");
        medium.setTag("2");
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPower.updateFrequency(mReader,AssetsAddInfoActivity.this,2,tvSubStrFrequency,progress);
            }
        });
        Button high = new Button(this);
        high.setText("高频");
        high.setTag("3");
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPower.updateFrequency(mReader,AssetsAddInfoActivity.this,3,tvSubStrFrequency,progress);
            }
        });
        tvSubStrFrequency = new TextView(this);
        linearLayout.addView(low);
        linearLayout.addView(medium);
        linearLayout.addView(high);
        linearLayout.addView(tvSubStrFrequency);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("请扫找到错误的rfid,即可删除").setIcon(android.R.drawable.ic_dialog_info).setView(linearLayout);
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //inputServer.getText().toString();
                currentIndex=-1;
                SettingPower.updateFrequency(mReader,AssetsAddInfoActivity.this,2,currentFrequency,progress);
                //updateFrequency(2,currentFrequency,true);
                dialog.dismiss();
            }
        });
        SettingPower.updateFrequency(mReader,AssetsAddInfoActivity.this,1,tvSubStrFrequency,progress);

        //updateFrequency(1,tvSubStrFrequency,false);
        builder.setOnKeyListener(this);
        builder.show();
    }
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 139 || event.getKeyCode() == 280) {
            try {
                String strUII = RFIDWithUHF.getInstance().inventorySingleTag();
                if (!TextUtils.isEmpty(strUII)) {
                    String strResult="";
                    //这里是用来生成编码的
                    final String strEPC = RFIDWithUHF.getInstance().convertUiiToEPC(strUII);
                    if(currentIndex!=-1) {
                        String errorEpc = tagList.get(currentIndex).getRfidNo();

                        Log.d("strEPC", "onKey: " + strEPC);
                        Log.d("strEPC", "onKey: ERROR "+errorEpc.substring(4,24));

                        if ((errorEpc.length() > 24 ? errorEpc.substring(4,errorEpc.length()):errorEpc).equalsIgnoreCase(strEPC)) {
                            tagList.remove(currentIndex);
                            tempList.remove(currentIndex);
                            for (int i =0;i<errorMaps.size();i++){
                                if(errorMaps.get(i).equals(errorEpc)){
                                    errorMaps.remove(i);
                                    break;
                                }
                            }
                            UIHelper.ToastMessage(this, "删除成功");
                            currentIndex = -1;
                            errorCount--;
                            dialog.dismiss();
                            adapter.notifyDataSetChanged();
                            SettingPower.updateFrequency(mReader,AssetsAddInfoActivity.this,2,currentFrequency,progress);

                            //  updateFrequency(parentFrequency, currentFrequency, true);
                            updateCommitStatus();
                        } else {
                            UIHelper.ToastMessage(this, "扫描数据错误");
                        }
                    }
                } else {
                    UIHelper.ToastMessage(this, R.string.uhf_msg_inventory_fail);
                }
            } catch (Exception e) {
            }
            return false;
        }
        return false;
    }
    //更新标题数量
    private void updateCommitStatus() {
        //判断是归还类型，如果是归还则可以进行部分归还提交
     /*   if(taskType==5){
            if (successCount >0 && errorCount == 0) {
                btnCommit.setEnabled(true);
            } else {
                btnCommit.setEnabled(false);
            }
        }else {*/

        if (successCount == currentDataCount && errorCount == 0) {
            btnCommit.setEnabled(true);
        } else {
            btnCommit.setEnabled(false);
        }
        // }
        tvAllCount.setText(currentDataCount+"");
        tvCurrentCount.setText(successCount+"");
        tvErrorCount.setText(errorCount+"");
    }
}
