package com.example.druge.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.druge.R;
import com.example.druge.adapter.AutoAdapter;
import com.example.druge.entry.Allot;
import com.example.druge.entry.DicInfo;
import com.example.druge.entry.DiclistByGroupName;
import com.example.druge.entry.Polling;
import com.example.druge.entry.Stockdrug;
import com.example.druge.entry.TaskInfo;
import com.example.druge.entry.TaskListInfo;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.sqlite.DBHelper;
import com.example.druge.sqlite.Dao.DicInfoDao;
import com.example.druge.sqlite.Dao.SynTempDao;
import com.example.druge.tools.AlertDialogCallBack;
import com.example.druge.tools.DialogUtils;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.NetUtil;
import com.example.druge.tools.SettingPower;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.UserConfig;
import com.example.druge.ui.Listener.OnMultiClickListener;
import com.example.druge.ui.activity.AssetsAddInfoActivity;
import com.example.druge.ui.activity.AssetsLoanInfoActivity;
import com.example.druge.ui.activity.HomeActivity;
import com.example.druge.ui.activity.InventoryOperationActivity;
import com.example.druge.ui.activity.ReplaceOperationActivity;
import com.example.druge.ui.activity.TaskListInfoActivity;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.dialog.ProgressDialog;
import com.example.druge.ui.page.OnPageChangeListener;
import com.example.druge.ui.page.PageControl;
import com.example.druge.ui.view.ScrapInfoDialog;
import com.rscja.deviceapi.RFIDWithUHF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 任务管理列表
 * Created by Administrator on 2019/1/16/016.
 */

public class TaskListFragment extends Fragment implements OnPageChangeListener,AdapterView.OnItemClickListener,DialogInterface.OnKeyListener{




    //任务信息列表
    @BindView(id=R.id.lv_task_List)
    ListView integrationTaskList;
    //分页对象
    @BindView(id=R.id.task_PageControl)
    PageControl taskPageControl;


    //任务状态下拉列表
    @BindView(id=R.id.tv_select_task_status)
    private Spinner spSelectTaskStatus;
    //用户数据的搜索任务内容
    @BindView(id=R.id.et_task_content)
    private EditText taskContent;
    //字典集合
    List<DiclistByGroupName> taskStatusList;

    ArrayAdapter<String> arrayAdapter ;


    private  int currentStatusIndex=-1;
    /** popup窗口里的ListView */
    private ListView mTypeLv;

    /** popup窗口 */
    private PopupWindow typeSelectPopup;

    /** 模拟的假数据 */
    private List<String> testData = new ArrayList<>();

    /** 数据适配器 */
    private ArrayAdapter<String> testDataAdapter;
    //任务列表集合适配器
    BaseAdapter adapter ;
    HomeActivity activity;
    //查询按钮
    @BindView(id=R.id.btn_commit_task_content)
    private Button commitTaskContent;
    //刷新按钮
    @BindView(id=R.id.sf_task_refresh)
    private SwipeRefreshLayout taskRefresh;

    private boolean isRefreshLoading =false;

    //新增归还按钮
    @BindView(id=R.id.btn_return_task)
    private Button returnTask;

    private boolean isLoading =true;
    //任务列表信息集合
    List<TaskListInfo> taskListInfos = new ArrayList<>();

    List<Polling> pollingList = new ArrayList<>();

    @BindView(id=R.id.layout_task_title)
    LinearLayout tasktitle;
    @BindView(id=R.id.layout_allot_title)
    LinearLayout allotTitle;
    @BindView(id=R.id.layout_polling_title)
    LinearLayout pollingTitle;

    List<Allot> allots = new ArrayList<>();

    private ScrapInfoDialog.Builder builder;
    private ScrapInfoDialog mDialog;

    ProgressDialog progress;

    List<DicInfo> dicInfos =new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_task_list,container,false);
        activity=(HomeActivity) getActivity();
        AnnotateUtil.initBindView(this,view);
        builder = new ScrapInfoDialog.Builder(activity);
        progress  = ProgressDialog.createDialog(activity);

        //初始化UI控件
        initUI();
        //初始化控件点击事件
        initListener();





        adapter = new AutoAdapter<TaskListInfo>(activity, taskListInfos, "ID", "TaskTypeName", "Content", "CreatorName", "StatusName");

        integrationTaskList.setAdapter(adapter);
        testData();


    //    loadingData("",taskPageControl.curPage,taskPageControl.numPerPage,(currentStatusIndex==-1)?currentStatusIndex:Integer.parseInt(dicInfos.get(currentStatusIndex).getValue()),true);
        //  taskPageControl.initPageShow(20);

        return view;
    }

    private void initListener() {
        spSelectTaskStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                currentStatusIndex    =  position-1;

                loadingData(taskContent.getText().toString(),taskPageControl.curPage,taskPageControl.numPerPage,(currentStatusIndex==-1)?currentStatusIndex:Integer.parseInt(dicInfos.get(currentStatusIndex).getValue()),false);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        taskPageControl.setPageChangeListener(this);
        commitTaskContent.setOnClickListener(multiClickListener);
        integrationTaskList.setOnItemClickListener(this);
        returnTask.setOnClickListener(multiClickListener);
        taskRefresh.setSize(SwipeRefreshLayout.LARGE);
        //设置进度圈的背景色。这里随便给他设置了一个颜色：浅绿色
        taskRefresh.setProgressBackgroundColorSchemeColor(Color.CYAN);
        //设置进度动画的颜色。这里面最多可以指定四个颜色，我这也是随机设置的，大家知道怎么用就可以了
        taskRefresh.setColorSchemeResources(android.R.color.holo_orange_dark
                ,android.R.color.holo_blue_dark
                ,android.R.color.holo_red_dark
                ,android.R.color.widget_edittext_dark);
        //设置手势滑动监听器
        taskRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //发送一个延时1秒的handler信息
                //   handler.sendEmptyMessageDelayed(199,1000);
                loadingData(taskContent.getText().toString(),taskPageControl.curPage,taskPageControl.numPerPage,(currentStatusIndex==-1)?currentStatusIndex:Integer.parseInt(dicInfos.get(currentStatusIndex).getValue()),false);
                isRefreshLoading=true;
                isLoading=true;
            }
        });
    }

    private void initUI() {
      /*  if(activity.actionUrl==5 || activity.actionUrl==3){
            returnTask.setVisibility(View.VISIBLE);
            returnTask.setText(activity.actionUrl==5?"新增归还":"新增借出");
        }else if(activity.actionUrl==7) {
            returnTask.setVisibility(View.VISIBLE);
            returnTask.setText("新增替换");
        }else
        {
            returnTask.setVisibility(View.GONE);
        }*/
        if(activity.actionUrl==0){

        }
        testData.add("全部状态");
        arrayAdapter = new ArrayAdapter<String>(activity,R.layout.popup_text_item,testData);
        spSelectTaskStatus.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    HandlerUtils handler = new HandlerUtils(activity, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            if (MethodEnum.GETDICBYGROUPNAME.equals(msg.getData().get("method"))) {
                taskStatusList = JSON.parseArray(JSON.parseObject(msg.getData().getString("result")).getString("Data"), DiclistByGroupName.class);
                testData = new ArrayList<>();
                testData.add("全部状态");
                for (int i = 0; i < taskStatusList.size(); i++) {
                    testData.add(taskStatusList.get(i).getName());
                }
            } else if (MethodEnum.GETTASKLIST.equals(msg.getData().get("method"))) {
                JSONObject resultData = JSON.parseObject(msg.getData().getString("result"));
                TaskInfo taskList = JSON.parseObject(resultData.getString("Data"), TaskInfo.class);
                List<TaskListInfo> results = taskList.getResult();
                taskListInfos.clear();
                taskListInfos.addAll(results);
                adapter.notifyDataSetChanged();
                if (isLoading) {
                    taskPageControl.initPageShow(taskList.getRowsCount());
                }
                isLoading = false;

                if (isRefreshLoading) {
                    taskRefresh.setRefreshing(false);

                }
            }else if(MethodEnum.GETQELSTOCKDRUG.equals(msg.getData().get("method"))){

                final Stockdrug stockdrugItem = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), Stockdrug.class);



                if (stockdrugItem != null && stockdrugItem.getQelType()==1 && activity.actionUrl==3 && stockdrugItem.getStatus()==1
                        ||stockdrugItem != null && stockdrugItem.getQelType()==1 && activity.actionUrl==5 && stockdrugItem.getStatus()==2
                        ) {
                    boolean isNotAddData = false;           //标识借出药箱有未入库
                    List<TaskListInfoItem> items = stockdrugItem.getDrugInfo();
                    for (TaskListInfoItem item :items){
                        if(item.getStatus()==0){
                            isNotAddData=true;
                        }
                    }

                    if(isNotAddData && activity.actionUrl==3){
                     Toast.makeText(activity,"有未入库数据，无法进行借出",Toast.LENGTH_SHORT).show();
                        return;
                    }
                        showTwoButtonDialog("确认箱号信息", "确认", "取消", stockdrugItem, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, AssetsLoanInfoActivity.class);
                            Bundle bundle = new Bundle();
                            Log.d("rfidNoLog", "onClick: taskList   "+stockdrugItem.getRfidNo());
                            bundle.putInt("taskType",activity.actionUrl);
                            bundle.putString("RfidNo",stockdrugItem.getRfidNo());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            mDialog.dismiss();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(activity,(activity.actionUrl==3?"借出":"归还")+"箱号数据异常",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message ms) {
            if (MethodEnum.GETTASKLIST.equals(ms.getData().get("method"))) {
                isLoading = false;

                if (isRefreshLoading) {
                    taskRefresh.setRefreshing(false);

                }

            } else if (MethodEnum.GETPOLLLIST.equals(ms.getData().get("method"))) {
                if (isRefreshLoading) {
                    taskRefresh.setRefreshing(false);

                }
            }else if(MethodEnum.GETQELSTOCKDRUG.equals(ms.getData().get("method"))){
                Toast.makeText(activity,"暂无药箱信息",Toast.LENGTH_SHORT).show();
            }
        }
    });
    private void showTwoButtonDialog(String alertText, String confirmText, String cancelText, Stockdrug stockdrug, View.OnClickListener conFirmListener, View.OnClickListener cancelListener) {
        mDialog = builder.setMessage(alertText).
                initScrapProductInfo(stockdrug)
                .setPositiveButton(confirmText, conFirmListener)
                .setNegativeButton(cancelText, cancelListener)
                .createTwoButtonDialog();
        mDialog.show();
    }

    /***
     * @param pageIndex 当前页
     * @param pageSize  每页数量
     */
    private void loadingData(final String taskContent, final int pageIndex, final int pageSize, final int taskStatus, final boolean loading) {
     /*   int taskType=-1;
        if("add".equals(activity.actionUrl)){
            taskType=2;
        }
*/
        //判断是否为药品替换页面并且网络是畅通的状态则会去调用接口
        if(activity.actionUrl==7 && NetUtil.isNetworkConnected(activity)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", activity.UserId);
            params.put("taskContent", taskContent);
            params.put("taskType", (activity.actionUrl == 0) ? -1 : activity.actionUrl);
            params.put("pageIndex", pageIndex);
            params.put("pageSize", pageSize);
            params.put("Status", taskStatus);
            params.put("orderType", -1);

           /* if (activity.actionUrl == 9 || activity.actionUrl == 10) {
                InteractiveDataUtil.interactiveMessage(activity, params, handler, MethodEnum.GETALLOTLIST, InteractiveEnum.GET);
            } else {
*/
                InteractiveDataUtil.interactiveMessage(activity, params, handler,  MethodEnum.GETTASKLIST, InteractiveEnum.GET);
          //  }

        }else{


            progress.show();
           new Thread(new Runnable() {
               @Override
               public void run() {
                   SQLiteDatabase db = DBHelper.getInstance(activity).getReadableDatabase();
                   Message msg = handlerOffline.obtainMessage();
                  //  Bundle bundle = new Bundle();
                       Bundle bundle =  initTaskInfo(db,taskContent,pageIndex,pageSize,taskStatus);
                   msg.what=1;
            //       db.beginTransaction();
                   try {

                   if(loading){
                       msg.what=2;
                      bundle.putSerializable("dicData",(Serializable) initStatusInfo(db));
                   }
                   msg.setData(bundle);

               //    db.setTransactionSuccessful();

                       handlerOffline.sendMessage(msg);
                   }catch (Exception e){
                   }finally {

                       if(db!=null) {
                       //    db.endTransaction();
                           db.close();
                       }
                   }


               }
           }).start();
        }
    }

    public Bundle initTaskInfo(SQLiteDatabase db, final String taskContent,final int pageIndex, final int pageSize,final int taskStatus){

        Log.d("taskData", "initTaskInfo: taskContent:  "+taskContent+ "   pageIndex:"+  pageIndex +"  pageSize:  "+ pageSize+"    taskStatus:  "+taskStatus +"   activity.actionUrl:  "+ activity.actionUrl);
        HashMap<String, Object> params = new HashMap<>();
        //  params.put("Content", taskContent);
        params.put("TaskTypeId", activity.actionUrl);
        if(taskStatus!=-1)
            params.put("Status", taskStatus);


        SynTempDao synTempDao = new SynTempDao(activity);
        Bundle bundle =  new Bundle();
        //获取sqlite中的任务数据信息
        bundle.putSerializable("data", (Serializable) synTempDao.getTaskAllDate(db,params,taskContent,pageIndex-1,pageSize));
        bundle.putInt("rowCount",synTempDao.getAllDateCount(db,params));
        return bundle;
    }

    final Handler handlerOffline = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                //获取字典表中数据，初始化任务状态数据
                case 2:
                    dicInfos  = (List<DicInfo>) msg.getData().getSerializable("dicData");

                  if(dicInfos!=null) {
                      testData.clear();
                      testData.add("全部状态");
                      for (int i = 0; i < dicInfos.size(); i++) {
                          testData.add(dicInfos.get(i).getName());
                      }
                      arrayAdapter.notifyDataSetChanged();
                  }
                //任务列表信息
                case 1:
                    Bundle bundle  =  msg.getData();
                    List<TaskListInfo>  results =(List<TaskListInfo>) bundle.getSerializable("data");
                    int rowCount = bundle.getInt("rowCount");
                    taskListInfos.clear();
                    adapter.notifyDataSetChanged();
                    if(results!=null) {
                        taskListInfos.addAll(results);
                        if (isLoading) {
                            taskPageControl.initPageShow(rowCount);
                        }
                        isLoading = false;

                        if (isRefreshLoading) {
                            taskRefresh.setRefreshing(false);

                        }
                    }
                    progress.dismiss();
                    break;


            }


        }
    };


    @Override
    public void pageChanged(int curPage, int numPerPage) {
        Log.d("pages", "curPage"+curPage+"numPerPage"+numPerPage);
        loadingData(taskContent.getText().toString(),curPage,numPerPage,(currentStatusIndex==-1)?currentStatusIndex:Integer.parseInt(dicInfos.get(currentStatusIndex).getValue()),false);
    }
    EditText inputServer;
    TextView tvSubStrFrequency;
    OnMultiClickListener multiClickListener = new OnMultiClickListener(activity) {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.btn_commit_task_content:
                    //点击查询按钮时刷新分页数据
                    isLoading = true;
                    loadingData(taskContent.getText().toString(), taskPageControl.curPage, taskPageControl.numPerPage, (currentStatusIndex == -1) ? currentStatusIndex : Integer.parseInt(dicInfos.get(currentStatusIndex).getValue()),false);
                    break;
                case R.id.tv_select_task_status:
                 /*   initSelectPopup();
                    // 使用isShowing()检查popup窗口(下拉列表)是否在显示状态
                    if (typeSelectPopup != null && !typeSelectPopup.isShowing()) {
                        typeSelectPopup.showAsDropDown(tvSelectTaskStatus, 0, 10);
                    }*/
                    break;
                case R.id.btn_return_task:
                    if(activity.actionUrl==7) {
                        Intent intent = new Intent(activity, ReplaceOperationActivity.class);
                        startActivity(intent);
                    }else {
                        LinearLayout linearLayoutParent = new LinearLayout(activity);
                        linearLayoutParent.setOrientation(LinearLayout.VERTICAL);

                        inputServer = new EditText(activity);
                        inputServer.setEnabled(false);


                        LinearLayout linearLayout = new LinearLayout(activity);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        Button low = new Button(activity);
                        low.setText("低频");
                        low.setTag("1");
                        low.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SettingPower.updateFrequency(activity.mReader, activity, 1, tvSubStrFrequency,progress);
                            }
                        });
                        Button medium = new Button(activity);
                        medium.setText("中频");
                        medium.setTag("2");
                        medium.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SettingPower.updateFrequency(activity.mReader, activity, 2, tvSubStrFrequency,progress);
                            }
                        });
                        Button high = new Button(activity);
                        high.setText("高频");
                        high.setTag("3");
                        high.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SettingPower.updateFrequency(activity.mReader, activity, 3, tvSubStrFrequency,progress);
                            }
                        });
                        tvSubStrFrequency = new TextView(activity);

                        linearLayout.addView(low);
                        linearLayout.addView(medium);
                        linearLayout.addView(high);
                        linearLayout.addView(tvSubStrFrequency);


                        linearLayoutParent.addView(inputServer);
                        linearLayoutParent.addView(linearLayout);
                        //弹出提示框
                        DialogUtils.showAlertDialog(activity, new AlertDialogCallBack() {
                            @Override
                            public void alertDialogFunction() {
                                //判断扫描内容不能为空
                                if (inputServer.getText().toString().trim().equals("")) {
                                    UIHelper.ToastMessage(activity, "扫描内容不能为空");
                                    return;
                                }
                                HashMap<String, Object> checkRfidParam = new HashMap<String, Object>();
                                checkRfidParam.put("rfidno", inputServer.getText().toString());
                                checkRfidParam.put("Userid", UserConfig.UserId);
                                checkRfidParam.put("UserName", UserConfig.UserName);

                                //调用后台接口根据rfid获取产品信息
                                InteractiveDataUtil.interactiveMessage(activity, checkRfidParam, handler, MethodEnum.GETQELSTOCKDRUG, InteractiveEnum.GET);

                            }
                        }, "请扫描" + (activity.actionUrl == 3 ? "借出" : "归还") + "药箱的RFID", TaskListFragment.this, linearLayoutParent);
                        SettingPower.updateFrequency(activity.mReader, activity, 1, tvSubStrFrequency,progress);
                    }
                    break;
            }
        }
    };

    //弹窗控件物理按键事件
    @Override
    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 139 || keyEvent.getKeyCode() == 280) {
            try {
             /*   RFIDWithUHF.getInstance().free();
                RFIDWithUHF.getInstance().init();*/
                String strUII = RFIDWithUHF.getInstance().inventorySingleTag();
                if (!TextUtils.isEmpty(strUII)) {
                    //这里是用来生成编码的
                    final String strEPC = RFIDWithUHF.getInstance().convertUiiToEPC(strUII);
                    if(strEPC.startsWith("2001")) {
                        Log.d("epcCode", "onKey: " + strEPC);
                        inputServer.setText(strEPC);
                    }else{
                        UIHelper.ToastMessage(activity,"扫描非药箱类型标签");
                    }
                } else {
                    UIHelper.ToastMessage(activity, R.string.uhf_msg_inventory_fail);
                }
            } catch (Exception e) {
            }
            return false;
        }
        return false;
    }

  /*  *//**
     * 初始化popup窗口
     *//*
    private void initSelectPopup() {
        mTypeLv = new ListView(activity);

        // 设置适配器
        testDataAdapter = new ArrayAdapter<String>(activity, R.layout.popup_text_item, testData);
        mTypeLv.setAdapter(testDataAdapter);
        // 设置ListView点击事件监听
        mTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                final String data = testData.get(position);
                // 把选择的数据展示对应的TextView上
                tvSelectTaskStatus.setText(data);
                currentStatusIndex = position-1;
                isLoading=true;
                loadingData(taskContent.getText().toString(),taskPageControl.curPage,taskPageControl.numPerPage,(currentStatusIndex==-1)?currentStatusIndex:Integer.parseInt(dicInfos.get(currentStatusIndex).getValue()),false);
                // 选择完后关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
        typeSelectPopup = new PopupWindow(mTypeLv, tvSelectTaskStatus.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        // 取得popup窗口的背景图片
        Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.bg_filter_down);
        typeSelectPopup.setBackgroundDrawable(drawable);
        typeSelectPopup.setFocusable(true);
        typeSelectPopup.setOutsideTouchable(true);
        typeSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                typeSelectPopup.dismiss();
            }
        });
    }*/

    @Override
    public void onResume() {
        loadingData("",taskPageControl.curPage,taskPageControl.numPerPage,(currentStatusIndex==-1)?currentStatusIndex:Integer.parseInt(taskStatusList.get(currentStatusIndex).getValue()),false);
        super.onResume();
    }
    /**
     * 模拟假数据
     */
    private void testData() {

        //判断是调拨任务
        if(activity.actionUrl==9 || activity.actionUrl==10){

            tasktitle.setVisibility(View.GONE);
            allotTitle.setVisibility(View.VISIBLE);

        }else {

            tasktitle.setVisibility(View.VISIBLE);
            allotTitle.setVisibility(View.GONE);

        }

        //巡检任务
        if(activity.actionUrl==0){
            tasktitle.setVisibility(View.GONE);
            allotTitle.setVisibility(View.GONE);
            pollingTitle.setVisibility(View.VISIBLE);
        }else{
            pollingTitle.setVisibility(View.GONE);
        }





       // InteractiveDataUtil.interactiveMessage(activity,taskStatusParam,handler, MethodEnum.GETDICBYGROUPNAME, InteractiveEnum.GET);
    }

    public  List<DicInfo> initStatusInfo(SQLiteDatabase db){
        HashMap<String,Object> taskStatusParam = new HashMap<>();
        taskStatusParam.put("GroupName","TaskStatus");
        DicInfoDao dicInfoDao =  new DicInfoDao(activity);
      /*  Bundle bundle = new Bundle();
        bundle.putSerializable("dicData",(Serializable) dicInfoDao.getAllDate(db,taskStatusParam));*/

      return dicInfoDao.getAllDate(db,taskStatusParam);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=null;

        if (taskListInfos.get(position).getStatus() == 2 || taskListInfos.get(position).getStatus() == 3) {
            intent = new Intent(activity, TaskListInfoActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putInt("taskId", taskListInfos.get(position).getID());
            bundle1.putInt("taskType",taskListInfos.get(position).getTaskType());
            intent.putExtras(bundle1);
        } else if (taskListInfos.get(position).getStatus() == 1) {
            switch (taskListInfos.get(position).getTaskType()) {
                //新增
                case 1:
                //调拨入库
                case 2:

                    //出库
                case 6:
                    intent = new Intent(activity, AssetsAddInfoActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("taskId", taskListInfos.get(position).getID());
                    bundle1.putInt("dataId", taskListInfos.get(position).getDataID());
                    bundle1.putInt("taskType", taskListInfos.get(position).getTaskType());
                    intent.putExtras(bundle1);
                    break;
                //盘点
                case 8:
                    intent = new Intent(activity, InventoryOperationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("dataId", taskListInfos.get(position).getID());
                    intent.putExtras(bundle);
                    break;
                //借出
                case 3:

                //归还
                case 5:
                    intent = new Intent(activity, AssetsLoanInfoActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("taskType", taskListInfos.get(position).getID());
                    intent.putExtras(bundle2);
            }
        }
        if(intent!=null)
            startActivity(intent);
    }
}