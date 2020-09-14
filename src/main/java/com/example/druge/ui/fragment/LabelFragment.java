package com.example.druge.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.druge.R;
import com.example.druge.adapter.AutoAdapter;
import com.example.druge.entry.DiclistByGroupName;
import com.example.druge.entry.ProductNameInfo;
import com.example.druge.entry.SelectProduct;
import com.example.druge.entry.SelectProductList;
import com.example.druge.tools.HandlerUtils;
import com.example.druge.tools.HandlerUtilsCallback;
import com.example.druge.tools.HandlerUtilsErrorCallback;
import com.example.druge.tools.InteractiveDataUtil;
import com.example.druge.tools.InteractiveEnum;
import com.example.druge.tools.MethodEnum;
import com.example.druge.tools.NetUtil;
import com.example.druge.ui.activity.HomeActivity;
import com.example.druge.ui.api.AnnotateUtil;
import com.example.druge.ui.api.BindView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 16486 on 2020/4/17.
 */

public class LabelFragment extends Fragment {

    @BindView(id=R.id.et_label_druge_serialNo)
    EditText etSerialNo;                         //输入的序列号文本框
    @BindView(id=R.id.sp_label_druge_type)
    Spinner spDrugeType;                        //药品类型下拉列表
    @BindView(id=R.id.sp_label_druge_name)
    Spinner spStorageName;                      //药品名称下拉列表
    @BindView(id=R.id.btn_label_druge_search)
    Button btnSearch;                           //查找提交按钮
    @BindView(id=R.id.tv_label_current_count)
    TextView currentCount;                      //当前总数量

    //药品类型集合
    List<DiclistByGroupName> drugeTypeList;

    HomeActivity activity;

    //药品类型名称集合
    List<String> drugeTypeDataList = new ArrayList<>();
    //药品类型适配器
    ArrayAdapter drugeTypeAdapter ;

    //药品名称下拉列表适配器
    ArrayAdapter storageDataAdapter;

    //药品名称集合
    List<String> storageDataList = new ArrayList<>();

    List<ProductNameInfo> productNameList = new ArrayList<>();
    @BindView(id=R.id.lv_storage_list)
    ListView lvStorageList;             //药品信息集合对象


    List<SelectProductList> selectProductList= new ArrayList<>();
    AutoAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_label, container, false);
        AnnotateUtil.initBindView(this,view);
        activity =(HomeActivity)getActivity();
        drugeTypeAdapter = new ArrayAdapter<String>(activity,R.layout.item_text1,drugeTypeDataList);
        spDrugeType.setAdapter(drugeTypeAdapter);
        storageDataAdapter = new ArrayAdapter<String>(activity,R.layout.item_text1,storageDataList);
        spStorageName.setAdapter(storageDataAdapter);

        adapter = new AutoAdapter<SelectProductList>(activity,selectProductList,"ID","RFIDNo","DrugTypeName","DrugName","SerialNo");
        lvStorageList.setAdapter(adapter);
        //初始化类型下拉列表数据

        if(NetUtil.isNetworkConnected(activity)) {
            initData();
        }
        initListener();

        return view;
    }


    private void initListener() {
        spDrugeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                storageDataList.clear();
                storageDataAdapter.notifyDataSetChanged();
               HashMap<String,Object> drugeTypeParam = new HashMap<String, Object>();
                drugeTypeParam.put("type",drugeTypeList.get(i).getValue());
                //获取药品名称
                InteractiveDataUtil.interactiveMessage(activity,drugeTypeParam,handler,MethodEnum.GETPRODUCTLIST,InteractiveEnum.GET);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,Object> labelParam  = new HashMap<>();
                labelParam.put("SeriaNo",etSerialNo.getText().toString());
                labelParam.put("DrugTypeID",drugeTypeList.get(spDrugeType.getSelectedItemPosition()).getValue());
                if(storageDataList.size()>0){
                    labelParam.put("DrugID",productNameList.get(spStorageName.getSelectedItemPosition()).getID());
                }
                labelParam.put("pageIndex",1);
                labelParam.put("pageSize",100000);
                InteractiveDataUtil.interactiveMessage(activity,labelParam,handler,MethodEnum.GETDRUGEINFO,InteractiveEnum.GET);
            }
        });

    }

    HandlerUtils handler = new HandlerUtils(activity, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            //显示药品类型列表
            if(MethodEnum.GETDICBYGROUPNAME.equals(msg.getData().getString("method"))){
                drugeTypeList = JSON.parseArray(JSON.parseObject(msg.getData().getString("result")).getString("Data"), DiclistByGroupName.class);
                drugeTypeDataList.clear();

                DiclistByGroupName dicList = new DiclistByGroupName();
                dicList.setID(-1);
                dicList.setName("全部类型");
                drugeTypeList.add(0,dicList);
                for (int i = 0; i < drugeTypeList.size(); i++) {
                    drugeTypeDataList.add(drugeTypeList.get(i).getName());
                }
                drugeTypeAdapter.notifyDataSetChanged();
                spDrugeType.setSelection(0);
                //获取药品名称列表
            }else if(MethodEnum.GETPRODUCTLIST.equals(msg.getData().getString("method"))){
                productNameList = JSON.parseArray(JSON.parseObject(msg.getData().getString("result")).getString("Data"), ProductNameInfo.class);
                storageDataList.clear();
                ProductNameInfo productNameInfo = new ProductNameInfo();
                productNameInfo.setID(-1);
                productNameInfo.setProductName("-全部-");
                productNameList.add(0,productNameInfo);
                for (int i =0;i<productNameList.size();i++){
                    storageDataList.add(productNameList.get(i).getProductName());
                }
                storageDataAdapter.notifyDataSetChanged();
                //药品信息列表
            }else if(MethodEnum.GETDRUGEINFO.equals(msg.getData().getString("method"))){
                selectProductList.clear();
                adapter.notifyDataSetChanged();
                SelectProduct selectProduct = JSON.parseObject(JSON.parseObject(msg.getData().getString("result")).getString("Data"), SelectProduct.class);
                if(selectProduct!=null && selectProduct.getResult().size()>=0){
                    selectProductList.addAll(selectProduct.getResult());
                    adapter.notifyDataSetChanged();
                    currentCount.setText(""+selectProduct.getResult().size());
                }

            }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message ms) {

        }
    });



    private void initData() {
        HashMap<String,Object> taskStatusParam = new HashMap<>();
        taskStatusParam.put("groupName","drugType");
        //获取药品类型信息
        InteractiveDataUtil.interactiveMessage(activity,taskStatusParam,handler, MethodEnum.GETDICBYGROUPNAME, InteractiveEnum.GET);

    }
}
