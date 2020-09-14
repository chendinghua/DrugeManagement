package com.example.druge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.adapter.TaskListInfoAdapter;
import com.example.druge.entry.ReplaceDrugeInfo;
import com.example.druge.entry.TaskListInfo;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.sqlite.DBHelper;
import com.example.druge.sqlite.Dao.SynTempInfoDao;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.fragment.MyFragment;
import com.example.druge.ui.view.kyindicator.KyIndicator;
import com.example.druge.ui.view.kyindicator.OnSelectedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**  任务详情页面
 * Created by Administrator on 2019/3/11/011.
 */

public class TaskListInfoActivity extends FragmentActivity implements View.OnClickListener{

    @BindView(id=R.id.tv_task_id)
    private TextView taskId;            //任务编号

    @BindView(id=R.id.tv_task_num)
    private TextView taskNum;          //任务数量
    @BindView(id=R.id.tv_task_content)
    private TextView taskContent;       //任务描述
    @BindView(id=R.id.tv_task_creator_name)
    private TextView taskCreatorName;   //创建人名
    @BindView(id=R.id.tv_task_creator_time)
    private TextView CreatorTime;       //创建时间
    @BindView(id=R.id.tv_task_status)
    private TextView taskStatus;        //任务状态名
    @BindView(id=R.id.tv_remark)
    private TextView remark;            //备注
    @BindView(id=R.id.btn_task_back)
    private Button btnBack;             //返回上一页

    @BindView(id=R.id.tv_task_opUser)
    private TextView opUser;

    @BindView(id=R.id.tv_task_opTime)
    private TextView opTime;

    @BindView(id=R.id.layout_task_info_pick)
    private LinearLayout layoutTaskInfoPack;
    @BindView(id=R.id.lv_task_info_list)
    private ListView taskPickInfoList;



    private int transferTaskId;

    private BaseAdapter adapter;

    private List<TaskListInfoItem> tagList = new ArrayList<>();
    //标题信息
    @BindView(id=R.id.kyIndicator)
    private KyIndicator yIndicator;
    //分页控件
    @BindView(id=R.id.viewpager)
    private ViewPager viewPager;

    @BindView(id=R.id.layout_task_result)
    LinearLayout layoutTaskResult;

    @BindView(id=R.id.layout_inventory_result)
    LinearLayout layoutInventoryResult;

    @BindView(id=R.id.layout_null_result)
    LinearLayout layoutNullResult;

    @BindView(id=R.id.layout_replace)
    LinearLayout layoutReplace;         //替换页面名称布局

    @BindView(id=R.id.tv_task_replace_result_title)
    TextView tvReplaceStockName;

    private LayoutInflater inflate;

    private List<MyFragment> fragmentList    = new ArrayList<MyFragment>();

    private FragmentManager fragmentManager;

    private Handler fragmentHandler;

    public  String ACTION_NAME="my_action_fragment";

    public Handler getFragmentHandler() {
        return fragmentHandler;
    }

    public void setFragmentHandler(Handler fragmentHandler) {
        this.fragmentHandler = fragmentHandler;
    }

    List<List<TaskListInfoItem>> taskForeahList=new ArrayList<>();


    boolean isLoad=false;

    Context context;

    @BindView(id=R.id.tv_page_count)
    TextView pageCount;

    @BindView(id=R.id.layout_page_info)
    LinearLayout layoutPageInfo;

    private Integer taskType;


    List<ReplaceDrugeInfo> replaceList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_info);
        AnnotateUtil.initBindView(this);
        context=this;
        isLoad=false;
        transferTaskId = getIntent().getExtras().getInt("taskId");
        taskType = getIntent().getExtras().getInt("taskType");
        bindEvent();
        addTab();
        //初始化UI控件
        initUI();
        //加载数据
        loadingData();
        btnBack.setOnClickListener(this);
    }


   Handler  loadHandler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           initListData(0);

       }
   };

  MyPagerAdapter myPageAdapater;
    private void addTab() {
        //创建view
        if(inflate == null)
            inflate = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //判断为归还
        if(taskType==5) {
            View tab = (View) (inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            TextView tv = (TextView) tab.findViewById(R.id.text);
            tv.setText("已归还");
            fragmentList.add(new MyFragment());
            tab = (View) (inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            tv = (TextView) tab.findViewById(R.id.text);
            tv.setText("未归还");
            fragmentList.add(new MyFragment());
        //判断为盘点
        }else if(taskType==8){
            View tab = (View)(inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            TextView tv  = (TextView)tab.findViewById(R.id.text);
            tv.setText("已盘点");


            fragmentList.add(new MyFragment());

            tab = (View)(inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            tv  = (TextView)tab.findViewById(R.id.text);
            tv.setText("未盘点");
            fragmentList.add(new MyFragment());

            tab = (View)(inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            tv  = (TextView)tab.findViewById(R.id.text);
            tv.setText("异常");

            fragmentList.add(new MyFragment());

        }else{
            View tab = (View) (inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            TextView tv = (TextView) tab.findViewById(R.id.text);
            tv.setText("药品替换出库");
            fragmentList.add(new MyFragment());
            tab = (View) (inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            tv = (TextView) tab.findViewById(R.id.text);
            tv.setText("药品替换入库");
            fragmentList.add(new MyFragment());

        }
       //     yIndicator.setCurrentTab(0);//默认当前tab为序号0的

        //     initListData(yIndicator.getSelectedIndex());
        myPageAdapater =  new MyPagerAdapter(this.fragmentManager);
        this.fragmentManager = this.getSupportFragmentManager();
        viewPager.setAdapter(new MyPagerAdapter(this.fragmentManager));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
     //  loadHandler.postDelayed(runLoadData,100);

    }



    /**
     * 定义viewpager适配器
     * @author pc
     *
     */
    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int arg0) {
          MyFragment myFragment =   fragmentList.get(arg0);
            if(!isLoad) {
                isLoad=true;
                myFragment.updateData(loadHandler);
            }
            return myFragment;
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }

    /**
     * 定义OnPageChangeListener  监听器
     * @author pc
     *
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            initListData(position);

            yIndicator.setCurrentTab(position);

        }

    }

    private void bindEvent() {
        yIndicator.setOnSelectedListener(new OnSelectedListener(){
            @Override
            public void OnSelected(View tab, int index) {
                TextView textTv = (TextView)tab.findViewById(R.id.text);
                textTv.setTextColor(0xFF1283EC);
                viewPager.setCurrentItem(index);
            }
            @Override
            public void OnNoSelected(View tab, int index) {
                TextView textTv = (TextView)tab.findViewById(R.id.text);
                textTv.setTextColor(0xFF646464);
            }
        });
    }

    private void loadingData() {
     /*   HashMap<String,Object> param = new HashMap<>();
        param.put("id",transferTaskId);
        InteractiveDataUtil.interactiveMessage(this, param,handler, MethodEnum.GETTASKINFO, InteractiveEnum.GET);*/


        new Thread(new Runnable() {
            @Override
            public void run() {
                SynTempInfoDao synTempInfoDao = new SynTempInfoDao(context);
                SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
                try {
                    Message msg =   handlerResult.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data",(Serializable) synTempInfoDao.getTaskDataList(db, transferTaskId,new Integer[]{1,2,6,3,5,8}));
                    msg.setData(bundle);
                    msg.what=1;
                    handlerResult.sendMessage(msg);

                }catch (Exception e){

                }finally {
                    db.close();
                }
            }
        }).start();


    }

    Handler handlerResult = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            layoutNullResult.setVisibility(View.GONE);
            List<TaskListInfoItem> taskListInfoItems = (List<TaskListInfoItem>) msg.getData().getSerializable("data");
            if (taskListInfoItems != null && taskListInfoItems.size() > 0) {
                // 判断为归还
                if (taskType == 5) {
                    layoutInventoryResult.setVisibility(View.VISIBLE);
                    layoutTaskResult.setVisibility(View.GONE);
                    layoutPageInfo.setVisibility(View.VISIBLE);
                    layoutReplace.setVisibility(View.GONE);
                    List<TaskListInfoItem> successInventoryData = new ArrayList<>();
                    List<TaskListInfoItem> failInventoryData = new ArrayList<>();
                    for (int i = 0; i < taskListInfoItems.size(); i++) {
                        switch (taskListInfoItems.get(i).getStatus()) {
                            //正常扫描到的数据
                            case 1:
                                taskListInfoItems.get(i).setIsFocus("true");
                                successInventoryData.add(taskListInfoItems.get(i));

                                break;
                            //未扫描数据
                            case 2:
                                taskListInfoItems.get(i).setIsFocus("false");
                                failInventoryData.add(taskListInfoItems.get(i));
                                break;
                        }
                    }
                    taskForeahList.add(successInventoryData);
                    taskForeahList.add(failInventoryData);
                    yIndicator.setCurrentTab(0);//默认当前tab为序号0的
                } else if (taskType == 8) {
                    layoutInventoryResult.setVisibility(View.VISIBLE);
                    layoutTaskResult.setVisibility(View.GONE);
                    layoutPageInfo.setVisibility(View.VISIBLE);
                    layoutReplace.setVisibility(View.GONE);
                    List<TaskListInfoItem> successInventoryData = new ArrayList<>();
                    List<TaskListInfoItem> failInventoryData = new ArrayList<>();
                    List<TaskListInfoItem> errorInventoryData = new ArrayList<>();


                    for (int i = 0; i < taskListInfoItems.size(); i++) {
                        switch (taskListInfoItems.get(i).getStatus()) {
                            case 1:
                                taskListInfoItems.get(i).setIsFocus("true");
                                successInventoryData.add(taskListInfoItems.get(i));
                                break;
                            case 2:
                                taskListInfoItems.get(i).setIsFocus("false");
                                failInventoryData.add(taskListInfoItems.get(i));
                                break;
                            case 3:
                                taskListInfoItems.get(i).setIsFocus("error");
                                errorInventoryData.add(taskListInfoItems.get(i));
                                break;
                        }
                    }
                    taskForeahList.add(successInventoryData);
                    taskForeahList.add(failInventoryData);
                    taskForeahList.add(errorInventoryData);
                    yIndicator.setCurrentTab(0);//默认当前tab为序号0的
                    //判断为替换
                } else{
                    for (int i =0;i<taskListInfoItems.size();i++){
                        taskListInfoItems.get(i).setIsFocus("success");
                    }

                    layoutReplace.setVisibility(View.GONE);
                    layoutPageInfo.setVisibility(View.GONE);
                    layoutInventoryResult.setVisibility(View.GONE);
                    layoutTaskResult.setVisibility(View.VISIBLE);
                    tagList.clear();
                    tagList.addAll(taskListInfoItems);

                    Log.d("url", "handlerExecutionFunction: 已经进来了");
                    adapter.notifyDataSetChanged();

                }


            }
        }
    };




    HandlerUtils handler = new HandlerUtils(this, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            if(MethodEnum.GETTASKINFO.equals(msg.getData().getString("method"))){
                TaskListInfo taskInfo =  JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), TaskListInfo.class);
                taskId.setText(taskInfo.getID()+"");
                taskNum.setText(taskInfo.getNum()+"");
                taskContent.setText(taskInfo.getContent());
                taskCreatorName.setText(taskInfo.getCreatorName());
                CreatorTime.setText(taskInfo.getCreatorTime());
                taskStatus.setText(taskInfo.getStatusName()+"");
                remark.setText(taskInfo.getRemark());
                opTime.setText(taskInfo.getOpTime());
                opUser.setText(taskInfo.getOpUser());
                layoutNullResult.setVisibility(View.GONE);



                if(taskInfo.getTaskDetailList()!=null){
                    List<TaskListInfoItem> taskListInfoItems =taskInfo.getTaskDetailList();
                  // 判断为归还
                    if(taskInfo.getTaskType()==5 ){
                       layoutInventoryResult.setVisibility(View.VISIBLE);
                        layoutTaskResult.setVisibility(View.GONE);
                        layoutPageInfo.setVisibility(View.VISIBLE);
                        layoutReplace.setVisibility(View.GONE);
                        List<TaskListInfoItem> successInventoryData = new ArrayList<>();
                        List<TaskListInfoItem> failInventoryData = new ArrayList<>();
                        for (int i = 0;i<taskListInfoItems.size();i++){
                            switch (taskListInfoItems.get(i).getStatus()){
                                //正常扫描到的数据
                                case 2:
                                    taskListInfoItems.get(i).setIsFocus("true");
                                    successInventoryData.add(taskListInfoItems.get(i));

                                    break;
                                //未扫描数据
                                case 1:
                                    taskListInfoItems.get(i).setIsFocus("false");
                                    failInventoryData.add(taskListInfoItems.get(i));
                                    break;
                            }
                        }
                        taskForeahList.add(successInventoryData);
                        taskForeahList.add(failInventoryData);
                        yIndicator.setCurrentTab(0);//默认当前tab为序号0的
                    } else if(taskInfo.getTaskType()==8) {
                        layoutInventoryResult.setVisibility(View.VISIBLE);
                        layoutTaskResult.setVisibility(View.GONE);
                        layoutPageInfo.setVisibility(View.VISIBLE);
                        layoutReplace.setVisibility(View.GONE);
                        List<TaskListInfoItem> successInventoryData = new ArrayList<>();
                        List<TaskListInfoItem> failInventoryData = new ArrayList<>();
                        List<TaskListInfoItem> errorInventoryData = new ArrayList<>();


                        for (int i = 0;i<taskListInfoItems.size();i++){
                            switch (taskListInfoItems.get(i).getStatus()){
                                case 1:
                                    taskListInfoItems.get(i).setIsFocus("true");
                                    successInventoryData.add(taskListInfoItems.get(i));
                                    break;
                                case 2:
                                    taskListInfoItems.get(i).setIsFocus("false");
                                    failInventoryData.add(taskListInfoItems.get(i));
                                    break;
                                case 3:
                                    taskListInfoItems.get(i).setIsFocus("error");
                                    errorInventoryData.add(taskListInfoItems.get(i));
                                    break;
                            }
                        }
                        taskForeahList.add(successInventoryData);
                        taskForeahList.add(failInventoryData);
                        taskForeahList.add(errorInventoryData);
                        yIndicator.setCurrentTab(0);//默认当前tab为序号0的
                    //判断为替换
                    }else if(taskInfo.getTaskType()==7){
                        layoutInventoryResult.setVisibility(View.VISIBLE);
                        layoutTaskResult.setVisibility(View.GONE);
                        layoutPageInfo.setVisibility(View.VISIBLE);
                        layoutReplace.setVisibility(View.VISIBLE);
                        if(taskListInfoItems.size()>0) {
                            TaskListInfoItem stockInfo =    taskListInfoItems.get(0);
                            //替换入库药箱
                            ReplaceDrugeInfo inReplaceDrugeInfo = new ReplaceDrugeInfo(stockInfo.getInStockNo(),stockInfo.getInstockNoName());
                            //替换出库药箱
                            ReplaceDrugeInfo outReplaceDrugeInfo = new ReplaceDrugeInfo(stockInfo.getOutStockNo(),stockInfo.getOutstockNoName());
                            replaceList.add(inReplaceDrugeInfo);
                            replaceList.add(outReplaceDrugeInfo);
                            List<TaskListInfoItem> inTaskList=  new ArrayList<>();
                            List<TaskListInfoItem> outTaskList=  new ArrayList<>();
                            for (int i = 0;i<taskListInfoItems.size();i++){
                                switch (taskListInfoItems.get(i).getStatus()){
                                    //正常扫描到的数据
                                    case 2:
                                        taskListInfoItems.get(i).setIsFocus("true");
                                        outTaskList.add(taskListInfoItems.get(i));
                                        break;
                                    //未扫描数据
                                    case 1:
                                        taskListInfoItems.get(i).setIsFocus("true");
                                        inTaskList.add(taskListInfoItems.get(i));
                                        break;
                                }
                            }
                            inReplaceDrugeInfo.setTaskListInfoItems(inTaskList);
                            outReplaceDrugeInfo.setTaskListInfoItems(outTaskList);
                            taskForeahList.add(outTaskList);
                            taskForeahList.add(inTaskList);
                            yIndicator.setCurrentTab(0);//默认当前tab为序号0的
                        }

                    }
                    else{
                        for (int i =0;i<taskListInfoItems.size();i++){
                            taskListInfoItems.get(i).setIsFocus("success");
                        }

                        layoutReplace.setVisibility(View.GONE);
                        layoutPageInfo.setVisibility(View.GONE);
                        layoutInventoryResult.setVisibility(View.GONE);
                        layoutTaskResult.setVisibility(View.VISIBLE);
                        tagList.clear();
                        tagList.addAll(taskListInfoItems);

                        Log.d("url", "handlerExecutionFunction: 已经进来了");
                        adapter.notifyDataSetChanged();

                    }

                }
            }
        }
    });
    private void initUI() {
        adapter = new TaskListInfoAdapter(this,tagList,false);
        taskPickInfoList.setAdapter(adapter);
    }

    private void updatePageCountInfo(List<TaskListInfoItem> taskListInfoItems){
        if(taskListInfoItems!=null)
            pageCount.setText(taskListInfoItems.size()+"");

    }
    private void updatePageCountInfo(ReplaceDrugeInfo drugeInfoList){
        if(drugeInfoList!=null){
            pageCount.setText(""+drugeInfoList.getTaskListInfoItems().size());
            tvReplaceStockName.setText(drugeInfoList.getStockNoName());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_task_back:
                finish();
                break;
        }
    }
    //初始化PagerView的fragment的数据
    private void initListData(int index){
        Log.d("myFragment", "handlerExecutionFunction: ");
        List<TaskListInfoItem> currentTaskList = new ArrayList<>();

        if (taskForeahList != null && taskForeahList.size() > 0) {
            currentTaskList.addAll(taskForeahList.get(index));
        }
      /*  Message msg=   handler.obtainMessage();
        msg.obj=currentTaskList;
        handler.sendMessage(msg);*/



        //3.使用广播传递数据

        Intent intent = new Intent();

        intent.setAction(ACTION_NAME);
        intent.putExtra("taskListInfoItems", (Serializable)currentTaskList);
        if(taskType!=7) {
            //更新当前page总数量
            updatePageCountInfo(currentTaskList);
        }else{
            if(replaceList.size()>0)
            updatePageCountInfo(replaceList.get(index));
        }

         //sendBroadcast(intent);

        //使用本地广播更加安全

        LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
        //  return currentTaskList ;
    }



}
