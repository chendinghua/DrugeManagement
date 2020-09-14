package com.example.druge.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.druge.R;
import com.example.druge.entry.ReplaceRfidList;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.tools.AlertDialogCallBack;
import com.example.druge.tools.DialogUtils;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.UserConfig;
import com.example.druge.ui.Listener.OnMultiClickListener;
import com.example.druge.ui.activity.ReplaceOperationActivity;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;
import com.example.druge.ui.view.kyindicator.KyIndicator;
import com.example.druge.ui.view.kyindicator.OnSelectedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/** 药品替换结果页面
 * Created by 16486 on 2020/4/24.
 */
public class ScanReplaceResultFragment  extends Fragment{


    //是否显示
    boolean isLoad=false;


    public  String ACTION_NAME="my_action_fragment";


    private List<MyFragment> fragmentList    = new ArrayList<MyFragment>();

    private FragmentManager fragmentManager;


    private LayoutInflater inflate;

    ReplaceOperationActivity activity;

    List<List<TaskListInfoItem>> taskForeahList=new ArrayList<>();


    //分页控件
    @BindView(id=R.id.viewpager)
    private ViewPager viewPager;

    @BindView(id=R.id.tv_scan_replace_result_title)
    TextView tvDrugeName;                   //药箱名称

    //标题信息
    @BindView(id=R.id.kyIndicator)
    private KyIndicator yIndicator;
    @BindView(id=R.id.et_scan_replace_result_remark)
    EditText etRemark;
    @BindView(id=R.id.btn_scan_replace_result_back)
    Button btnBack;
    @BindView(id=R.id.btn_scan_replace_result_commit)
    Button btnCommit;
     Timer timer = null;

    MyTimerTask mTask=null;

    boolean isLoadView = true;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_replace_result, container, false);
        AnnotateUtil.initBindView(this, view);
        activity = (ReplaceOperationActivity) getActivity();

        initListener();


        if(isLoadView) {
            if(timer ==null)
                timer= new Timer();
            if(timer != null && mTask != null){
                mTask.cancel();
            }
            mTask = new MyTimerTask();
            timer.schedule(mTask, 0, 1000);                //启动定时器
            isLoad=false;
        }
    return view;
    }
    final    Handler  handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            yIndicator.removeAllViews();
            taskForeahList.clear();
            fragmentList.clear();
            taskForeahList.add(activity.oldResult);
            taskForeahList.add(activity.newResult);
            bindEvent();
            addTab();
            if(timer != null){
                mTask.cancel();
                timer.cancel();
                timer = null;

            }
        }
    };

    class  MyTimerTask extends TimerTask {
        @Override

        public void run()

        {
            //希望定时器做什么，在此实现，
            //可以使用一个Handler对象，将定时消息传递到界面线程
            if(activity.isLoadResult && handler !=null){
                Message msg = new Message();
                handler.sendMessage(msg);
            }
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
    private void initListener() {
        btnBack.setOnClickListener(multiClickListener);
        btnCommit.setOnClickListener(multiClickListener);
    }

    MyPagerAdapter myPageAdapater;
    private void addTab() {
        //创建view
        if(inflate == null)
            inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tab = (View) (inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            TextView tv = (TextView) tab.findViewById(R.id.text);
            tv.setText("出库药品替换");
            fragmentList.add(new MyFragment());
            tab = (View) (inflate.inflate(R.layout.tab, null));
            yIndicator.addTab(tab);
            tv = (TextView) tab.findViewById(R.id.text);
            tv.setText("入库药品替换");
            fragmentList.add(new MyFragment());

        //     yIndicator.setCurrentTab(0);//默认当前tab为序号0的

        //     initListData(yIndicator.getSelectedIndex());
        myPageAdapater =  new MyPagerAdapter(this.fragmentManager);
        this.fragmentManager = activity.getSupportFragmentManager();
        viewPager.setAdapter(new MyPagerAdapter(this.fragmentManager));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        //  loadHandler.postDelayed(runLoadData,100);

    }

    Handler loadHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initListData(0);

        }
    };


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
    OnMultiClickListener multiClickListener = new OnMultiClickListener(activity) {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.btn_scan_replace_result_back:
                    activity.isLoadResult=false;
                    if(timer ==null)
                        timer= new Timer();
                    if(timer != null && mTask != null){
                        mTask.cancel();
                    }
                    mTask = new MyTimerTask();

                    timer.schedule(mTask,0,1000);                //启动定时器

                    activity.newResult.clear();
                    activity.pager.setCurrentItem(activity.pager.getCurrentItem()-1);
                    break;
                case R.id.btn_scan_replace_result_commit:
                    DialogUtils.showAlertDialog(activity, new AlertDialogCallBack() {
                        @Override
                        public void alertDialogFunction() {
                            List<ReplaceRfidList> rfidLists = new ArrayList<>();
                            for (TaskListInfoItem oldInfo : activity.oldResult){
                                ReplaceRfidList oldRfid = new ReplaceRfidList();
                                oldRfid.setProductID(oldInfo.getAssestId());
                                oldRfid.setStorageID(oldInfo.getStorageId());
                                oldRfid.setrFIDNo(oldInfo.getRfidNo());
                                oldRfid.setStatus(2);
                                rfidLists.add(oldRfid);
                            }
                            for (TaskListInfoItem newInfo : activity.newResult){
                                ReplaceRfidList newRfid = new ReplaceRfidList();
                                newRfid.setProductID(newInfo.getAssestId());
                                newRfid.setStorageID(newInfo.getStorageId());
                                newRfid.setrFIDNo(newInfo.getRfidNo());
                                newRfid.setStatus(1);
                                rfidLists.add(newRfid);
                            }
                            HashMap<String,Object> commitReplaceParam = new HashMap<>();
                            commitReplaceParam.put("OpUserID", UserConfig.UserId);
                            commitReplaceParam.put("OpUser",UserConfig.UserName);
                            commitReplaceParam.put("Remark",etRemark.getText().toString());
                            commitReplaceParam.put("InStockNo",activity.replaceDrugeOld.getID());
                            commitReplaceParam.put("OutStockNo",activity.replaceDrugeNew.getID());
                            commitReplaceParam.put("RfidList",rfidLists);
                            InteractiveDataUtil.interactiveMessage(activity,commitReplaceParam,handlerUtils, MethodEnum.ADDREPLACE, InteractiveEnum.POST);
                        }
                    },"是否确认提交替换数据",null,null);


                    break;
            }
        }
    };
    HandlerUtils handlerUtils = new HandlerUtils(activity, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            if(MethodEnum.ADDREPLACE.equals(msg.getData().getString("method"))){
                Toast.makeText(activity,"数据提交成功",Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message ms) {
            if(MethodEnum.ADDREPLACE.equals(ms.getData().getString("method"))){
                Toast.makeText(activity,"数据提交失败",Toast.LENGTH_SHORT).show();
            }
        }
    });


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
        //更新当前page总数量
        updateTitleInfo(index);
        //sendBroadcast(intent);

        //使用本地广播更加安全

        LocalBroadcastManager.getInstance(activity).sendBroadcastSync(intent);
        //  return currentTaskList ;
    }
    //更新药箱信息标题
    private void updateTitleInfo(int index) {
        if(index==0){
            tvDrugeName.setText(activity.replaceDrugeOld.getName());
        }else{
            tvDrugeName.setText(activity.replaceDrugeNew.getName());
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
}
