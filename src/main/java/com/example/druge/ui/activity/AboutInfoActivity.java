package com.example.druge.ui.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.druge.R;
import com.example.druge.updateVersion.UpdateManager;

/**关于页面详情
 * Created by Administrator on 2019/4/25/025.
 */

public class AboutInfoActivity extends Activity {

    TextView currentVersion;        //当前版本信息
    Button checkUpdateVersion;      //检查最新版本信息

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_info);
        currentVersion = findViewById(R.id.tv_current_version);
        checkUpdateVersion = findViewById(R.id.btn_check_update_version);

        try {
                    //获取当前的版本名
                   PackageManager pm = getPackageManager();
                    android.content.pm.PackageInfo pinfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    String versionName = pinfo.versionName;
            currentVersion.setText(versionName);
             } catch (PackageManager.NameNotFoundException e) {
             }
             //点击检查更新按钮
        checkUpdateVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager manager = new UpdateManager(AboutInfoActivity.this);
                // 检查软件更新
                manager.checkUpdate();
            }
        });

    }
}
