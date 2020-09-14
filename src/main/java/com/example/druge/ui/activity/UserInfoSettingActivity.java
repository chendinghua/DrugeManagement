package com.example.druge.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.druge.R;
import com.example.druge.tools.AlertDialogCallBack;
import com.example.druge.tools.DialogUtils;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MD5;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.NetUtil;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.UserConfig;

import java.util.HashMap;

/**  用户信息设置页面
 * Created by Administrator on 2019/2/22/022.
 */

public class UserInfoSettingActivity extends Activity implements View.OnClickListener{

    EditText oldPwd;                //旧密码
    EditText newPwd;                //新密码
    EditText reNewPwd;              //重新输入的新密码
    Button savePwd;                 //保存密码按钮
    Button btnQuit;                 //返回上一页

  //  private int UserId;             //用户id
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_setting);
     //   UserId=getIntent().getExtras().getInt("UserId");
        //初始化UI控件
        initUI();
        //初始化控件点击事件
        initListener();
    }
    private void initUI() {
        oldPwd = findViewById(R.id.et_old_pwd);
        newPwd = findViewById(R.id.et_new_pwd);
        reNewPwd = findViewById(R.id.et_re_new_pwd);
        savePwd = findViewById(R.id.btn_reset_pwd_save);
        btnQuit = findViewById(R.id.btn_quit_pwd_setting);
    }
    private void initListener() {
        savePwd.setOnClickListener(this);
        btnQuit.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reset_pwd_save:
                if(!NetUtil.isNetworkConnected(this)){
                    UIHelper.ToastMessage(this,"当前网络异常");
                    return;
                }

                final  String newPwdStr =newPwd.getText().toString();
                final String oldPwdStr = oldPwd.getText().toString();
                String rePwdStr = reNewPwd.getText().toString();
                if(newPwdStr.trim().length()<6 || oldPwdStr.trim().length()<6 || rePwdStr.trim().length()<6 ){
                    UIHelper.ToastMessage(UserInfoSettingActivity.this,"密码长度不能少于6");
                    return;
                }else if(!newPwdStr.equals(rePwdStr)){
                    UIHelper.ToastMessage(UserInfoSettingActivity.this,"两次密码输入不同");
                    return;
                }else if(newPwdStr.equals(oldPwdStr)){
                    UIHelper.ToastMessage(UserInfoSettingActivity.this,"旧密码和新密码不能相同");
                    return;
                }
                DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                    @Override
                    public void alertDialogFunction() {
                        HandlerUtils handler = new HandlerUtils(UserInfoSettingActivity.this, new HandlerUtilsCallback() {
                            @Override
                            public void handlerExecutionFunction(Message msg) {
                                UIHelper.ToastMessage(UserInfoSettingActivity.this,"密码修改成功");
                                finish();
                            }
                        }, new HandlerUtilsErrorCallback() {
                            @Override
                            public void handlerErrorFunction(Message ms) {
                                UIHelper.ToastMessage(UserInfoSettingActivity.this,"密码修改失败");
                                finish();
                            }
                        });
                        HashMap<String,Object> param = new HashMap<>();
                        param.put("UserID", UserConfig.UserId);
                        param.put("OldPwd", MD5.ganerateMD5(oldPwdStr+ "H@S#J$2%0&1*8$"));
                        param.put("NewPwd", MD5.ganerateMD5(newPwdStr+"H@S#J$2%0&1*8$"));
                        InteractiveDataUtil.interactiveMessage(UserInfoSettingActivity.this,param,handler, MethodEnum.RESETPWD, InteractiveEnum.POST);
                    }
                },"是否确定修改密码",null,null);
                break;
            case R.id.btn_quit_pwd_setting:
                finish();
                break;
        }
    }
}
