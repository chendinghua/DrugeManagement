package com.example.druge.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.druge.R;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;

import java.util.HashMap;

/** 消息通知详细页面
 * Created by Administrator on 2019/4/1/001.
 */
public class NotifyListInfoActivity extends Activity implements View.OnClickListener{

    TextView tvTitle;           //标题
    TextView tvContent;         //描述内容
    TextView tvType;            //类型
    TextView tvCreatorName;     //创建人


    int UserId;

    Button notifyBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_list_info);
        //初始化UI控件
        initUI();
        initData();

    }

    private void initData() {
        //消息通知任务id
       int notifyId =  getIntent().getExtras().getInt("notifyId");

        HashMap<String ,Object> updateNotifyParam  = new HashMap<>();
        updateNotifyParam.put("ID",notifyId);
        //修改消息通知状态
        InteractiveDataUtil.interactiveMessage(this,updateNotifyParam,null, MethodEnum.UPDATENOTIFYSTATUS, InteractiveEnum.POST);
        UserId =getIntent().getExtras().getInt("UserId");

      String title = getIntent().getExtras().getString("title");
      String content = getIntent().getExtras().getString("content");
      String typeName =getIntent().getExtras().getString("typeName");
      String creatorName = getIntent().getExtras().getString("creatorName");
        tvTitle.setText(title);
        tvContent.setText(content);
        tvType.setText(typeName);
        tvCreatorName.setText(creatorName);
    }

    private void initUI() {
        tvTitle = findViewById(R.id.tv_notify_list_info_title);
        tvContent = findViewById(R.id.tv_notify_list_info_content);
        tvType = findViewById(R.id.tv_notify_list_info_type);
        tvCreatorName = findViewById(R.id.tv_notify_list_info_creatorName);

        notifyBack = findViewById(R.id.btn_notify_list_back);

        notifyBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
     /*   MainActivity.currentFragment = new PickTaskListFragment();
        MainActivity.currentPageName="消息通知管理";
        MainActivity.setData(ActionUrlEnum.NOTIFYLIST);*/
        Intent intent = new Intent(this,HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isLogin",false);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
         /*   HomeActivity.currentFragment = new PickTaskListFragment();
            HomeActivity.tvHomeName="消息通知管理";
            HomeActivity.setData(ActionUrlEnum.NOTIFYLIST);*/
            Intent intent = new Intent(this,HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isLogin",false);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
