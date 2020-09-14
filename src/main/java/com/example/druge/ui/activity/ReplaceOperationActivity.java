package com.example.druge.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import com.badoualy.stepperindicator.StepperIndicator;
import com.example.druge.R;
import com.example.druge.entry.Stockdrug;
import com.example.druge.entry.TaskListInfoItem;
import com.example.druge.tools.ActionUrl;
import com.example.druge.tools.AlertDialogCallBack;
import com.example.druge.tools.DialogUtils;
import com.example.druge.tools.UIHelper;
import com.example.druge.tools.Utils;
import com.example.druge.ui.fragment.ScanDrugeFragment;
import com.example.druge.ui.fragment.ScanReplaceFragment;
import com.example.druge.ui.fragment.ScanReplaceResultFragment;
import com.example.druge.ui.page.CustomNoScrollViewPager;
import com.example.druge.ui.page.ReceivePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 替换药品背景页面
 */
public class ReplaceOperationActivity extends BaseTabFragmentActivity {
    public CustomNoScrollViewPager pager;               //pagerView分页流程对象
    ReceivePagerAdapter pagerAdapter;                   //pagerView控件适配器
    ArrayList<Fragment> views = new ArrayList<>();      //pagerView中的适配器中的fragment集合
    public Stockdrug replaceDrugeOld = new Stockdrug();                                //药品原替换药箱id
    public  Stockdrug replaceDrugeNew = new Stockdrug();                                //药品替换新药箱id

    public List<TaskListInfoItem> oldResult = new ArrayList<>();        //原有药品数据
    public List<TaskListInfoItem> newResult = new ArrayList<>();        //替换成的药品数据

    public String currentStatus;


    public  boolean isLoadResult = false;

    public boolean isShouwDruge = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_receive_product_operation);
        currentStatus= ActionUrl.OLDDRUGE;
        //初始化分页fragment集合
        initView(views);
        //获取fragment管理器
        FragmentManager fragmentManager = getSupportFragmentManager();
        //把fragment集合中的数据添加到适配器中
        pagerAdapter = new ReceivePagerAdapter(fragmentManager,views);
        pager= findViewById(R.id.pager);
        assert pager != null;
        //设置pagerView的适配器
        pager.setAdapter(pagerAdapter);
        //分页标题控件
        final StepperIndicator indicator =findViewById(R.id.stepper_indicator);
        //分页标题和pagerView绑定一下
        indicator.setViewPager(pager, false);

        if(mReader!=null ){
            mReader.free();
        }
        //初始化rfid读取模块
        initUHF();
        isShouwDruge=true;

    }
    private void initView(ArrayList<Fragment> views) {
        ScanDrugeFragment scanOldDrugeFragment = new ScanDrugeFragment();
        ScanReplaceFragment scanOldReplaceFragment = new ScanReplaceFragment();
        ScanDrugeFragment scanNewDrugeFragment = new ScanDrugeFragment();
        ScanReplaceFragment scanNewReplaceFragment = new ScanReplaceFragment();

        ScanReplaceResultFragment resultFragment = new ScanReplaceResultFragment();
        views.add(scanOldDrugeFragment);
        views.add(scanOldReplaceFragment);
        views.add(scanNewDrugeFragment);
        views.add(scanNewReplaceFragment);
        views.add(resultFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 ||keyCode == 280){
            if (pagerAdapter.getCurrentFragment() instanceof ScanDrugeFragment) {

                ScanDrugeFragment scanDrugeFragment =(ScanDrugeFragment)pagerAdapter.getCurrentFragment();
                scanDrugeFragment.read();
            }else if(pagerAdapter.getCurrentFragment() instanceof ScanReplaceFragment){
                ScanReplaceFragment scanReplaceFragment =(ScanReplaceFragment)pagerAdapter.getCurrentFragment();
                scanReplaceFragment.read();
            }
        }else  if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(pagerAdapter.getCurrentFragment() instanceof  ScanReplaceFragment){
                ScanReplaceFragment scanReplaceFragment =(ScanReplaceFragment)pagerAdapter.getCurrentFragment();
                if(scanReplaceFragment.loopRead.getLoopFlag()){
                    UIHelper.ToastMessage(ReplaceOperationActivity.this,R.string.stopWarning);
                    return true;
                }
            }
            DialogUtils.showAlertDialog(this, new AlertDialogCallBack() {
                @Override
                public void alertDialogFunction() {
                    Utils.finishActivity(ReplaceOperationActivity.this,mReader,null);

                }
            },"是否结束当前流程",null,null);
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }
}
