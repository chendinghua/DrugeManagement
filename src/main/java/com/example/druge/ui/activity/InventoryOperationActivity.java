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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.adapter.AutoAdapter;
import com.example.druge.entry.Inventory;
import com.example.druge.entry.InventoryCommitData;
import com.example.druge.entry.InventoryList;
import com.example.druge.entry.Stockdrug;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.sqlite.DBHelper;
import com.example.druge.sqlite.Dao.InventoryDao;
import com.example.druge.sqlite.Dao.SynTempInfoDao;
import com.example.druge.tools.AlertDialogCallBack;
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
import com.example.druge.ui.dialog.ProgressDialog;
import com.example.druge.ui.view.ScrollListView;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.exception.ConfigurationException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**  盘点处理页面
 * Created by Administrator on 2019/3/29/029.
 */

public class InventoryOperationActivity extends BaseTabFragmentActivity implements View.OnClickListener,DialogInterface.OnKeyListener,AdapterView.OnItemLongClickListener{

    TextView tvCurrentCount;            //需要扫描的数量
    TextView tvErrorCount;              //异常扫描的数量
    TextView tvSuccessCount;            //正常扫到的数量

    private Button lowFrequency;         //低频
    private Button mediumFrequency;       //中频
    private Button  highFrequency;        //高频
    private TextView currentFrequency;     //当前频率

    private Button btnRefresh;             //刷新按钮

    boolean loopFlag = false;               //是否循环扫描标签

    //记录扫描操作页面扫描频率
    int parentFrequency;
    //记录扫描单个频率
    int subStrFrequency=1;
    //当前下标
    int currentIndex=-1;

    List<InventoryList> tempList= new ArrayList<>();  //用来存储扫临时描到的数据

    List<InventoryList> tagList = new ArrayList<>();  //用来存储扫描到的数据

    int  errorCount;                //错误数量统计

    int successCount;               //成功数量统计
    int currentCount;               //当前处理数量统计

    SharedPreferences sp;           //pda设置存储对象

    int dataId;                     //盘点任务id

    Button btnScan;         //扫描按钮

    Button btnBack;         //返回按钮

    Button btnCommit;       //提交按钮

    ScrollListView lvInventoryList;       //数据列表

    AutoAdapter adapter;          //数据列表适配器

    EditText inventoryOoperation;   //盘点结果描述
    //  String userName;                //用户名

    //  int taskId;         //存储盘点任务id

    //   String actionUrl;   //用来存储实际操作的页面  (盘点页面和出货检查)

   /* LinearLayout layoutInventory;   //盘点页面布局
    LinearLayout layoutScanOutBound;    //出库检查页面布局*/

    LinearLayout layoutResult;        //描述页面布局

    SwipeRefreshLayout inventoryRefresh;            //刷新控件

    boolean isRefreshLoading=false;                 //手动向上滑动刷新

    private String pickNo="";

    private int inventoryType=0;

    private InventoryDao inventoryDao;

    //存储临时异常的rfid标签集合
    List<String> errorMaps = new ArrayList<>();

    ProgressDialog progress;

    Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_operation);
        mContext =this;
        dataId = getIntent().getExtras().getInt("dataId");
        inventoryDao = new InventoryDao(this);

        inventoryDao.deleteOrder();
        progress= ProgressDialog.createDialog(this);
        //初始化UI控件
        initUI();
        clearBufferData();

        //初始化事件
        initListener();
        //初始化数据
        loadingData();

        //初始化rfid读取模块
        initUHF();
        //初始化声音模块
        //   initSound();
        //rfid读取模块功率初始化
        mRreaderLoading();

    }



    /* HandlerUtils handlerUtils = new HandlerUtils(this, new HandlerUtilsCallback() {
         @Override
         public void handlerExecutionFunction(Message msg) {
             //获取盘点信息列表
             if (MethodEnum.GETINVENTORTLIST.equals(msg.getData().getString("method"))) {
                 tagList.clear();
                 tempList.clear();

                 Inventory inventory = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"),Inventory.class);



                 inventoryType = inventory.getType();
                 List<InventoryList> inventoryLists = inventory.getInventoryList();
                 if (inventoryLists != null) {
                     tagList.addAll(inventoryLists);
                     tempList.addAll(inventoryLists);
                     currentCount = inventoryLists.size();
                     errorCount=0;
                     successCount=0;
                     adapter.notifyDataSetChanged();
                     updateTitleInfoCount();
                     if (isRefreshLoading) {
                         inventoryRefresh.setRefreshing(false);
                         isRefreshLoading=false;
                     }
                     inventoryDao.foreachAddData(inventoryLists);
                 }
                 //提交盘点任务
             } else if (MethodEnum.ADDINVENTORYINFO.equals(msg.getData().getString("method"))) {
                 UIHelper.ToastMessage(InventoryOperationActivity.this, "提交盘点数据成功");
                 mReader.free();
                 finish();
                 //获取出货检查列表
             }   //获取产品信息
             else if(MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().getString("method"))){
                 Stockdrug stockdrug = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);
                 if(stockdrug!=null && stockdrug.getDrugInfo().size()>0){
                     TaskListInfoItem  listInfoItem= stockdrug.getDrugInfo().get(0);
                         InventoryList tempInventoryList  =
                                 new InventoryList(listInfoItem.getStorageId()
                                         ,listInfoItem.getAssestId()
                                         ,listInfoItem.getRfidNo(),
                                         listInfoItem.getSerialNo(),
                                         listInfoItem.getStorageStatus(),
                                         listInfoItem.getAssestName(),
                                         listInfoItem.getAssestTypeName(),
                                         "error");
                     tempInventoryList.setStockid(listInfoItem.getStockid());
                     tempInventoryList.setStockName(listInfoItem.getStockName());
                     tempInventoryList.setStatus(listInfoItem.getStatus());
                     tempInventoryList.setStatusName(listInfoItem.getStatusName());
                     tempInventoryList.setTaskId(listInfoItem.getTaskId());

                         tagList.add(0,tempInventoryList);
                         tempList.add(0,tempInventoryList);
                         errorCount++;
                 }
                 updateTitleInfoCount();
                 adapter.notifyDataSetChanged();

             }
         }
     }, new HandlerUtilsErrorCallback() {
         @Override
         public void handlerErrorFunction(Message ms) {
             //异常提交盘点
             if (MethodEnum.ADDINVENTORYINFO.equals(ms.getData().getString("method"))) {
                 UIHelper.ToastMessage(InventoryOperationActivity.this, "提交盘点数据失败");
                 //查询异常盘点的RFID标签信息
             }else if(MethodEnum.GETQELSTOCKDRUG.equals(ms.getData().getString("method"))){
                 Stockdrug stockdrug = JSON.parseObject(JSON.parseObject(ms.getData().getString("result")).getString("Data"), Stockdrug.class);
                 if(stockdrug!=null) {

                     InventoryList tempInventroyList = new InventoryList(stockdrug.getRfidNo(),"未知标签类型","","","error");
                     tagList.add(0,tempInventroyList);
                     tempList.add(0,tempInventroyList);
                     errorCount++;

                 }
                 updateTitleInfoCount();
                 adapter.notifyDataSetChanged();
             }
         }
     });*/
    //单机版获取数据和提交数据
    Handler handlerOffline  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //初始化数据
                case 1:
                    List<InventoryList> lists =( List<InventoryList>) msg.getData().getSerializable("data");
                    if(lists!=null) {

                        tagList.addAll(lists);
                        tempList.addAll(lists);
                        currentCount = lists.size();
                        errorCount = 0;
                        successCount = 0;
                        adapter.notifyDataSetChanged();
                        updateTitleInfoCount();
                    }
                    break;
                //提交数据结果
                case 2:
                    if(msg.getData().getBoolean("isSuccess",false)){
                        UIHelper.ToastMessage(InventoryOperationActivity.this, "提交盘点数据成功");
                        mReader.free();
                        finish();
                    }else{
                        UIHelper.ToastMessage(InventoryOperationActivity.this, "提交盘点数据失败");

                    }
                    break;
            }
            progress.dismiss();
        }
    };

    private void loadingData() {
        progress.show();
        //判断是盘点页面
   /*     HashMap<String, Object> inventoryParam = new HashMap<>();
        inventoryParam.put("id", dataId);
        //调用盘点信息接口  根据盘点任务id获取盘点信息
        InteractiveDataUtil.interactiveMessage(InventoryOperationActivity.this,inventoryParam, handlerUtils, MethodEnum.GETINVENTORTLIST, InteractiveEnum.GET);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                SynTempInfoDao synTempInfoDao = new SynTempInfoDao(mContext);
                SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
                try {
                 Message msg =   handlerOffline.obtainMessage();
                   Bundle bundle = new Bundle();
                   bundle.putSerializable("data",(Serializable) synTempInfoDao.getInentoryDataList(db, dataId));
                    msg.setData(bundle);
                    msg.what=1;
                    handlerOffline.sendMessage(msg);

                }catch (Exception e){

                }finally {
                    db.close();
                }
            }
        }).start();


        setTitle("盘点操作");
        //     layoutScanOutBound.setVisibility(View.GONE);

    }

    //初始化默认RFID读取模块
    private void mRreaderLoading() {


        SettingPower.updateFrequency(mReader,this,2,currentFrequency,progress);
    }
    private void initListener() {

        lowFrequency.setOnClickListener(this);         //低频
        mediumFrequency.setOnClickListener(this);       //中频
        highFrequency.setOnClickListener(this);        //高频
        btnBack.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);            //刷新按钮
        //数据列表每行的长按事件  用来删除异常数据的
        lvInventoryList.setOnItemLongClickListener(this);

        inventoryRefresh.setSize(SwipeRefreshLayout.LARGE);
        btnCommit.setOnClickListener(this);
        //设置进度圈的背景色。这里随便给他设置了一个颜色：浅绿色
        inventoryRefresh.setProgressBackgroundColorSchemeColor(Color.CYAN);
        //设置进度动画的颜色。这里面最多可以指定四个颜色，我这也是随机设置的，大家知道怎么用就可以了
        inventoryRefresh.setColorSchemeResources(android.R.color.holo_orange_dark
                ,android.R.color.holo_blue_dark
                ,android.R.color.holo_red_dark
                ,android.R.color.widget_edittext_dark);
        //设置手势滑动监听器
        inventoryRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //发送一个延时1秒的handler信息
                //   handler.sendEmptyMessageDelayed(199,1000);
                loopFlag=false;
                isRefreshLoading = true;
                //重新加载列表中的数据
                loadingData();
                errorMaps.clear();

            }
        });
    }
    private void initUI() {
        btnRefresh = findViewById(R.id.btn_inventory_refresh);
        inventoryRefresh = findViewById(R.id.sf_inventory_refresh);
        //初始化pda设置对象
        sp=getSharedPreferences("pda_config", Context.MODE_PRIVATE);
        layoutResult = findViewById(R.id.layout_result);

        currentFrequency = findViewById(R.id.tv_inventory_operation_current_frequency);
        lowFrequency =findViewById(R.id.btn_inventory_operation_low_frequency);
        mediumFrequency = findViewById(R.id.btn_inventory_operation_medium_frequency);
        highFrequency = findViewById(R.id.btn_inventory_operation_high_frequency);
        tvCurrentCount = findViewById(R.id.tv_inventory_operation_current_count);
        tvErrorCount = findViewById(R.id.tv_inventory_operation_error_count);
        tvSuccessCount = findViewById(R.id.tv_inventory_operation_success_count);

        lowFrequency.setTag(sp.getInt("lowFrequency",5)+"");
        mediumFrequency.setTag(sp.getInt("mediumFrequency",15)+"");
        highFrequency.setTag(sp.getInt("highFrequency",30)+"");
        lvInventoryList = findViewById(R.id.lv_inventory_operation_operation_list);

        btnBack = findViewById(R.id.btn_inventory_operation_back);
        btnCommit = findViewById(R.id.btn_inventory_operation_commit);

        btnScan = findViewById(R.id.btn_inventory_operation_scanner);

        inventoryOoperation = findViewById(R.id.et_inventory_operation_result);
        //判断处理盘点
        //初始化盘点页面的适配器
        adapter = new AutoAdapter<InventoryList>(this,tagList,"RfidNo","SerialNo","ProductName","AsstypeName","StockName","StatusName");

        lvInventoryList.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        if(loopFlag){
            UIHelper.ToastMessage(this,R.string.stopWarning);
            return ;
        }
        switch (v.getId()){
            //点击扫描按钮
            case R.id.btn_inventory_operation_scanner:
                readTag();
                break;
            //设置低频
            case R.id.btn_inventory_operation_low_frequency:

                SettingPower.updateFrequency(mReader,this,1,currentFrequency,progress);
                break;
            //设置中频
            case R.id.btn_inventory_operation_medium_frequency:
                SettingPower.updateFrequency(mReader,this,2,currentFrequency,progress);
                break;
            //设置高频
            case R.id.btn_inventory_operation_high_frequency:
                SettingPower.updateFrequency(mReader,this,3,currentFrequency,progress);
                break;
            //返回按钮
            case R.id.btn_inventory_operation_back:
                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {
                        mReader.free();
                        finish();
                    }
                },"是否取消当前操作",null,null);

                break;
            //提交盘点结果
            case R.id.btn_inventory_operation_commit:
                loopFlag=false;
                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<InventoryCommitData> commitDatas = new ArrayList<InventoryCommitData>();
                                for (int i = 0; i < tagList.size(); i++) {
                                    InventoryCommitData commitData = new InventoryCommitData();
                                    commitData.setRfidNo(tagList.get(i).getRfidNo());
                                    //判断是否为成功读取
                                    if ("true".equals(tagList.get(i).getIsFocus())) {
                                        commitData.setStatus(1);
                                        //判断是否为未读取数据
                                    } else if ("false".equals(tagList.get(i).getIsFocus())) {
                                        commitData.setStatus(2);
                                        //判断是否为异常读取数据
                                    } else if ("error".equals(tagList.get(i).getIsFocus())) {
                                        commitData.setStatus(3);
                                    }
                                    //         commitData.setStorageStatus(tagList.get(i).getStatus());
                                    commitData.setId(tagList.get(i).getID());
                                    commitData.setStockID(tagList.get(i).getStockid());
                                    commitData.setTaskID(tagList.get(i).getTaskId());
                                    commitDatas.add(commitData);
                                }
                                HashMap<String, Object> commitParam = new HashMap<String, Object>();
                                commitParam.put("ID", dataId);
                                commitParam.put("EndTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                commitParam.put("Result", inventoryOoperation.getText().toString());
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
                        //提交盘点结果
                       // InteractiveDataUtil.interactiveMessage(InventoryOperationActivity.this,commitParam, handlerUtils, MethodEnum.ADDINVENTORYINFO, InteractiveEnum.POST);

                    }
                }, "是否取消当前操作", null, null);
                //  }
                break;
            case  R.id.btn_inventory_refresh:
                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {
                        clearListData();
                    //    progress.show();
                        //new Handler().postDelayed(runnable,1000);
                        loadingData();
                    }
                },"是否刷新数据",null,null);

                break;
        }
    }

  /*  Handler refreshHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            List<InventoryList> inventoryLists =  inventoryDao.getAllDate();
            tempList.addAll(inventoryLists);
            tagList.addAll(inventoryLists);
            currentCount=inventoryLists.size();
            adapter.notifyDataSetChanged();
            updateTitleInfoCount();
            progress.dismiss();

        }
    };*/
    private void clearBufferData(){
        inventoryDao.deleteOrder();
    }

    public void clearListData(){
        currentCount=0;
        successCount=0;
        errorCount=0;
        loopFlag=false;
        updateTitleInfoCount();
        tempList.clear();
        tagList.clear();
        errorMaps.clear();
        adapter.notifyDataSetChanged();
    }


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

                SettingPower.updateFrequency(mReader,InventoryOperationActivity.this,1,tvSubStrFrequency,progress);
                //  updateFrequency(1,tvSubStrFrequency,false);
            }
        });
        Button medium = new Button(this);
        medium.setText("中频");
        medium.setTag("2");
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPower.updateFrequency(mReader,InventoryOperationActivity.this,2,tvSubStrFrequency,progress);
                // updateFrequency(2,tvSubStrFrequency,false);
            }
        });
        Button high = new Button(this);
        high.setText("高频");
        high.setTag("3");
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPower.updateFrequency(mReader,InventoryOperationActivity.this,3,tvSubStrFrequency,progress);
                //   updateFrequency(3,tvSubStrFrequency,false);
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
                SettingPower.updateFrequency(mReader,InventoryOperationActivity.this,2,currentFrequency,progress);
                //  updateFrequency(parentFrequency,currentFrequency,true);
                dialog.dismiss();
            }
        });
        SettingPower.updateFrequency(mReader,InventoryOperationActivity.this,1,tvSubStrFrequency,progress);

        // updateFrequency(subStrFrequency,tvSubStrFrequency,false);
        builder.setOnKeyListener(this);
        builder.show();
    }
    //弹窗扫描物理按键控件 来扫描要去移除掉的rfid数据
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 139 || event.getKeyCode() == 280) {
            try {
                mReader.free();
                mReader.init();
                String strUII = RFIDWithUHF.getInstance().inventorySingleTag();
                if (!TextUtils.isEmpty(strUII)) {
                    String strResult="";
                    //这里是用来生成编码的
                    final String strEPC = RFIDWithUHF.getInstance().convertUiiToEPC(strUII);
                    if(currentIndex!=-1) {
                        //获取当前长按的rfid异常数据
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
                            //  updateTitle(taskType);
                            UIHelper.ToastMessage(this, "删除成功");
                            //把当前成功删除掉的数据的下标清除掉
                            currentIndex = -1;
                            errorCount--;
                            //删除成功吧弹出隐藏掉
                            dialog.dismiss();
                            //更新一下列表信息
                            adapter.notifyDataSetChanged();
                            SettingPower.updateFrequency(mReader,this,2,currentFrequency,progress);
                            //updateFrequency(parentFrequency, currentFrequency, true);
                            //更新一下标题信息数据
                            updateTitleInfoCount();
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //扫到异常的数据
                case -1:
                    tagList.clear();
                    tagList.addAll((List<InventoryList>)msg.obj);
                    adapter.notifyDataSetChanged();
                    //   playSound(2);

                    SoundManager.getInstance(InventoryOperationActivity.this).playSound(2);
                    break;
                //扫到正常的数据
                case 1:
                    tagList.clear();
                    tagList.addAll((List<InventoryList>)msg.obj);
                    adapter.notifyDataSetChanged();
                    SoundManager.getInstance(InventoryOperationActivity.this).playSound(1);
                    break;
                case -2:
                    String rfid =msg.getData().getString("rfid");

                    InventoryList tempInventroyList = new InventoryList(0,rfid,"未知标签类型","","","error");
                    tagList.add(0,tempInventroyList);
                    tempList.add(0,tempInventroyList);
                    errorCount++;

                    break;
            }

            updateTitleInfoCount();
        }
    };
    //更改标题扫描数量统计信息
    private void updateTitleInfoCount(){

        tvCurrentCount.setText(""+currentCount);
        tvSuccessCount.setText(""+successCount);
        tvErrorCount.setText(""+errorCount);
    }

    //页面跳转切换物理控件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(loopFlag){
                UIHelper.ToastMessage(this,R.string.stopWarning);
                return true;
            }

            DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                @Override
                public void alertDialogFunction() {
                   /* mReader.free();
                    finish();*/
                    Utils.finishActivity(InventoryOperationActivity.this,mReader,tagThread);
                }
            },"是否退出当前操作",null,null);
        }
        //扣动扫描物理按键
        if (keyCode == 139 ||keyCode == 280){

            readTag();
        }
        return true;
    }
    TagThread tagThread;
    //读取标签
    public  void readTag() {
        if (!loopFlag) {
            //  mContext.mReader.setEPCTIDMode(true);
            if (mReader.startInventoryTag(0,0)) {
                loopFlag = true;
                tagThread =  new TagThread();
                tagThread.start();
                lvInventoryList.setScrollEnable(false);
                lvInventoryList.setOnItemLongClickListener(null);
                //   setViewEnabled(false);
            } else {
                lvInventoryList.setScrollEnable(true);
                lvInventoryList.setOnItemLongClickListener(this);
                //用户点击停止识别则暂停上面扫描的线程
                mReader.stopInventory();
                UIHelper.ToastMessage(this,R.string.uhf_msg_inventory_open_fail);
            }
        } else {// 停止识别
            lvInventoryList.setScrollEnable(true);
            lvInventoryList.setOnItemLongClickListener(this);
            loopFlag=false;
        }
    }
    //列表长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(loopFlag){
            UIHelper.ToastMessage(this,"请停止扫描再长按删除");
            return false;
        }
        //异常数据弹窗扫描删除数据
        if (tagList.get(position).getIsFocus().equals("error")) {

            myShowDialog(position);
        }
        return false;
    }

    String TAG="inventoryTAG";

    class TagThread extends Thread {
        public void run() {
            RFIDWithUHF mReader = null;
            try {
                mReader = RFIDWithUHF.getInstance();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
            String strTid;
            String strResult;
            String[] res = null;
            while (loopFlag && mReader != null) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res[0];
                    if (strTid.length() != 0 && !strTid.equals("0000000" +
                            "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }

                    Log.d("epcCode", "run: "+res[1] + strResult);

                    String newRfid = (res[1] + strResult).length() == 24 ? (res[1] + strResult) : (res[1] + strResult).substring(4, (res[1] + strResult).length());
                    Log.d("epcCode1", "run: "+newRfid);
                    Message msg = new Message();
                    if(newRfid.startsWith("1001")) {
                        //判断rfid在原有的列表中是否存在
                        int isExistsDataIndex = -1;
                        for (int i = 0; i < tempList.size(); i++) {
                            if (newRfid.equalsIgnoreCase(tempList.get(i).getRfidNo())) {
                                isExistsDataIndex = i;
                                break;
                            }
                        }
                        Log.d("epcCode1", "run:  index          "+isExistsDataIndex);
                        //判断扫描的数据是否在集合中存在
                        if (isExistsDataIndex > -1) {
                            //判断是否 是需要扫描拣货的货品数据状态
                            if ("false".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                                InventoryList currentData = tempList.get(isExistsDataIndex);
                                InventoryList tempInventoryList  =
                                        new InventoryList();
                                tempInventoryList.setID(currentData.getID());
                                tempInventoryList.setAssetsTypeID(currentData.getAssetsTypeID());
                                tempInventoryList.setRfidNo(currentData.getRfidNo());
                                tempInventoryList.setSerialNo(currentData.getSerialNo());
                                tempInventoryList.setStatus(currentData.getStatus());
                                tempInventoryList.setProductName(currentData.getProductName());
                                tempInventoryList.setAsstypeName(currentData.getAsstypeName());
                                tempInventoryList.setStockid(currentData.getStockid());
                                tempInventoryList.setStockName(currentData.getStockName());
                                tempInventoryList.setStatus(currentData.getStatus());
                                tempInventoryList.setStatusName(currentData.getStatusName());
                                tempInventoryList.setTaskId(currentData.getTaskId());
                                tempInventoryList.setIsFocus("true");
                                tempList.remove(isExistsDataIndex);
                                tempList.add(tempInventoryList);
                                msg.what = 1;
                                successCount++;
                                //判断是否已确认扫描到的货品
                                Log.d(TAG, "run:\"false\".equals(tempList.get(isExistsDataIndex).get(\"isUpdate\"))) "+ msg.what );

                            } else if ("true".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                                msg.what = 1;
                                Log.d(TAG, "run:\"true\".equals(tempList.get(isExistsDataIndex).get(\"isUpdate\")) "+ msg.what );
                                //判断是否为异常扫描到的货品
                            } else if ("error".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                                msg.what = -1;

                                Log.d(TAG, "run: \"error\".equals(tempList.get(isExistsDataIndex).get(\"isUpdate\"))"+ msg.what );
                            }
                        } else if(isExistsDataIndex==-1) {
                            //不在ListView集合里面的数据  所以为异常数据

                            Bundle bundle = new Bundle();
                            bundle.putString("rfid",newRfid);
                            msg.setData(bundle);
                            //tempList.add(0, errorMap);
                            msg.what=-2;
                            Log.d(TAG, "run:  "+ msg.what );
                        }
                    }else {
                        msg.what = 1;
                        Log.d(TAG, "run: "+ msg.what );
                    }
                    msg.obj=tempList;
                    handler.sendMessage(msg);

                }
            }
        }
    }
}
