package com.example.druge.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.druge.R;
import com.example.druge.tools.UserConfig;
import com.example.druge.tools.Utils;
import com.example.druge.ui.activity.AboutInfoActivity;
import com.example.druge.ui.activity.HomeActivity;
import com.example.druge.ui.activity.LoginActivity;
import com.example.druge.ui.activity.PdaParamSettingActivity;
import com.example.druge.ui.activity.UserInfoSettingActivity;

/**
 * Created by Administrator on 2019/2/22/022.
 */

/***
 * 个人中心页面
 */
public class UserInformationFragment extends Fragment implements View.OnClickListener{
    private Button paramSetting;    //手持机rfid功率设置
    private Button userInfo;        //用户信息
    private Button userExit;        //退出系统
    private HomeActivity activity;


    private  Button btnAbout;
    TextView tvUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_user_information,container,false);
        activity = (HomeActivity) getActivity();
        //初始化UI控件
        initUI(view);
        //初始化控件点击事件
        initListener();
        return view;
    }
    private void initUI(View view) {
        paramSetting =view.findViewById(R.id.btn_param_setting);
        userInfo = view.findViewById(R.id.btn_user_info);
        userExit = view.findViewById(R.id.btn_user_exit);
        btnAbout = view.findViewById(R.id.btn_about);
        tvUserName = view.findViewById(R.id.tv_userNameTitle);
        tvUserName.setText(UserConfig.UserName);
    }
    private void initListener() {
        paramSetting.setOnClickListener(this);
        userInfo.setOnClickListener(this);
        userExit.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //设置pda的rfid功率
            case R.id.btn_param_setting:
                /*Intent intent = new Intent(activity, PdaParamSettingActivity.class);
                startActivity(intent);*/
                Utils.gotoActivity(activity,PdaParamSettingActivity.class,null,null);

                break;
            //设置用户信息
            case R.id.btn_user_info:
              /*  Intent intent1 = new Intent(activity, UserInfoSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("UserId",activity.UserId);
                intent1.putExtras(bundle);
                startActivity(intent1);*/
                Bundle bundle = new Bundle();
               // bundle.putInt("UserId",activity.UserId);
                Utils.gotoActivity(activity,UserInfoSettingActivity.class,bundle,null);

                break;
            //退出返回登录页面
            case R.id.btn_user_exit:

            /*    HashMap<String,Object> userExitMap = new HashMap<>();
                userExitMap.put("UserName",activity.userName);
                userExitMap.put("Token",activity.token);
                InteractiveDataUtil.interactiveMessage(userExitMap,null, MethodEnum.LOGINSIGNOUT, InteractiveEnum.POST);
            */

                SharedPreferences.Editor editor=activity.getSharedPreferences("login_config", Context.MODE_PRIVATE).edit();
                //清除密码
                editor.putString("password", "");
                editor.apply();

                Utils.gotoActivity(activity,LoginActivity.class,null, activity.mReader);

                break;

            case  R.id.btn_about:
                Utils.gotoActivity(activity,AboutInfoActivity.class,null,null);


                break;
        }
    }
}
