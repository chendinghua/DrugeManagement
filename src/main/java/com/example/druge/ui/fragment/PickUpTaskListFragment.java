package com.example.druge.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.druge.R;
import com.example.druge.adapter.DemoSectionAdapter;
import com.example.druge.entry.TaskInfo;
import com.example.druge.entry.TaskListInfo;
import com.example.druge.multipleSelect.MultipleAdapter;
import com.example.druge.multipleSelect.MultipleSelect;
import com.example.druge.multipleSelect.viewholder.color.ColorFactory;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.NetUtil;
import com.example.druge.tools.UIHelper;
import com.example.druge.ui.activity.HomeActivity;
import com.example.druge.ui.activity.LocalhostDataCommitOrRollbackActivity;
import com.goyourfly.multiple.adapter.menu.SimpleDeleteSelectAllMenuBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import butterknife.internal.Utils;


/**
 * 接取任务页面
 * Created by 16486 on 2020/8/19.
 */
public class PickUpTaskListFragment extends Fragment {
    @BindView(R.id.btn_pick_up_task_commit)
    Button btnPickUpTaskCommit;
    @BindView(R.id.btn_pick_up_task_rollback)
    Button btnPickUpTaskRollback;
    @BindView(R.id.sf_pick_up_task_refresh)
    SwipeRefreshLayout sfPickUpTaskRefresh;
    private View view;
    //   @BindView(R.id.recycler)
    private RecyclerView recycler;

    DemoSectionAdapter<TaskListInfo> demoAdapter;

    HomeActivity activity;

    Unbinder unbinder;

    MultipleAdapter adapter;


    List<TaskListInfo> list = new ArrayList<>();

    boolean  isLoading=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_pick_up_task_list, container, false);
        recycler = view.findViewById(R.id.recycler);
        unbinder = ButterKnife.bind(this, view);
        activity = (HomeActivity) getActivity();
        demoAdapter = new DemoSectionAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(activity));

        recycler.addItemDecoration(new DividerItemDecoration(activity, RecyclerView.VERTICAL));
        adapter = MultipleSelect
                .with(activity)
                .adapter(demoAdapter)
                .decorateFactory(new ColorFactory())
                .linkList(demoAdapter.getList())
                .ignoreViewType(Utils.arrayOf(1))
                .customMenu(new SimpleDeleteSelectAllMenuBar(activity, activity.getResources().getColor(R.color.colorPrimary), Gravity.TOP))
                .build();
        recycler.setAdapter(adapter);

        sfPickUpTaskRefresh.setSize(SwipeRefreshLayout.LARGE);
        //设置进度圈的背景色。这里随便给他设置了一个颜色：浅绿色
        sfPickUpTaskRefresh.setProgressBackgroundColorSchemeColor(Color.CYAN);
        //设置进度动画的颜色。这里面最多可以指定四个颜色，我这也是随机设置的，大家知道怎么用就可以了
        sfPickUpTaskRefresh.setColorSchemeResources(android.R.color.holo_orange_dark
                ,android.R.color.holo_blue_dark
                ,android.R.color.holo_red_dark
                ,android.R.color.widget_edittext_dark);
        //设置手势滑动监听器
        sfPickUpTaskRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //发送一个延时1秒的handler信息
                //   handler.sendEmptyMessageDelayed(199,1000);
                if(NetUtil.isNetworkConnected(activity)) {
                    initTaskData();
                    isLoading = true;
                }else{
                Message msg =     handlerStop.obtainMessage();
                    msg.what=-1;
                    handlerStop.sendMessage(msg);
                }
            }
        });


        return view;

    }

    Handler handlerStop = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                isLoading=false;
                sfPickUpTaskRefresh.setRefreshing(false);
        }
    };

    public void hideButton(boolean vis) {
        if (vis) {
            btnPickUpTaskCommit.setVisibility(View.GONE);
            btnPickUpTaskRollback.setVisibility(View.GONE);
        } else {
            btnPickUpTaskCommit.setVisibility(View.VISIBLE);
            btnPickUpTaskRollback.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        initTaskData();
        super.onResume();
    }

    public void initTaskData() {
        if (NetUtil.isNetworkConnected(activity)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("userID", activity.UserId);
            params.put("taskContent", "");
            params.put("taskType", -1);
            params.put("pageIndex", 1);
            params.put("pageSize", 10000);
            params.put("Status", 1);
            params.put("orderType", -1);

            InteractiveDataUtil.interactiveMessage(activity, params, handler, MethodEnum.GETTASKLIST, InteractiveEnum.GET);
        }
    }

    HandlerUtils handler = new HandlerUtils(activity, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {


            if (MethodEnum.GETTASKLIST.equals(msg.getData().get("method"))) {
                JSONObject resultData = JSON.parseObject(msg.getData().getString("result"));
                TaskInfo taskList = JSON.parseObject(resultData.getString("Data"), TaskInfo.class);
                List<TaskListInfo> results = taskList.getResult();

                demoAdapter.getList().clear();
                ;
                demoAdapter.getList().addAll(results);
                adapter.notifyDataSetChanged();
                if (isLoading) {
                    isLoading=false;
                    sfPickUpTaskRefresh.setRefreshing(false);

                }
            }
        }
    });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        activity.getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick({R.id.btn_pick_up_task_commit, R.id.btn_pick_up_task_rollback})
    public void onViewClicked(View view) {
        if (!NetUtil.isNetworkConnected(activity)) {
            {

                UIHelper.ToastMessage(activity, "暂无网络信息");
                return;
            }
        }
        switch (view.getId()) {
            case R.id.btn_pick_up_task_commit:


            case R.id.btn_pick_up_task_rollback:
                Intent intent = new Intent(activity, LocalhostDataCommitOrRollbackActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", Integer.parseInt(view.getTag().toString()));
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }


/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
         case   R.id.action_radio_btn_left :
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(RadioBtnFactory())
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build();
          case  R.id.action_radio_btn_right :
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(RadioBtnFactory(Gravity.RIGHT))
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build();

           case R.id.action_check_box :
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(CheckBoxFactory(color = resources.getColor(R.color.colorPrimary)))
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build()
            }
            R.id.action_background_color :
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(ColorFactory())
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build();
           *//* R.id.action_more -> {
                startActivity(Intent(this,MoreActivity::class.java))
            }*//*
        return super.onOptionsItemSelected(item);
    }

    override fun onBackPressed() {
        if(!(recycler.adapter as MultipleAdapter).cancel()){
            super.onBackPressed()
        }
    }*/


}
