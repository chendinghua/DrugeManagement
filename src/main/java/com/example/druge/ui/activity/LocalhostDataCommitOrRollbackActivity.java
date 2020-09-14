package com.example.druge.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;

import com.example.druge.R;
import com.example.druge.adapter.DemoSectionAdapter;
import com.example.druge.entry.SynData.SynTemp;
import com.example.druge.multipleSelect.MultipleAdapter;
import com.example.druge.multipleSelect.MultipleSelect;
import com.example.druge.multipleSelect.viewholder.color.ColorFactory;
import com.example.druge.sqlite.DBHelper;
import com.example.druge.sqlite.Dao.SynTempDao;
import com.goyourfly.multiple.adapter.menu.SimpleDeleteSelectAllMenuBar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import butterknife.internal.Utils;

/** 本地数据提交或放弃页面
 * Created by 16486 on 2020/8/28.
 */
public class LocalhostDataCommitOrRollbackActivity extends AppCompatActivity{
    private RecyclerView recycler;

    DemoSectionAdapter<SynTemp> demoAdapter;
    Context mContext;

    MultipleAdapter adapter;

    int type ;

    public void back(){
        finish();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localhost_data_commit_or_rollback);

         type = getIntent().getExtras().getInt("type");
//type=1;

        recycler = findViewById(R.id.localhost_recycler);
        mContext = this;
        demoAdapter = new DemoSectionAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.addItemDecoration(new DividerItemDecoration(mContext, RecyclerView.VERTICAL));
        adapter = MultipleSelect
                .with(this)
                .adapter(demoAdapter)
                .decorateFactory(new ColorFactory())
                .linkList(demoAdapter.getList())
                .ignoreViewType( Utils.arrayOf(1))
                .customMenu(new SimpleDeleteSelectAllMenuBar(this, mContext.getResources().getColor(R.color.colorPrimary), Gravity.TOP))
                .setType(type)
                .build();
        recycler.setAdapter(adapter);
        initData(type);
    }



    private void initData(final int typeData) {
       final  Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<SynTemp> data = (List<SynTemp>) msg.getData().getSerializable("data");
                if(data!=null) {
                    demoAdapter.getList().addAll(data);
                    adapter.notifyDataSetChanged();
                }

            }
        };


    new Thread(new Runnable() {
        @Override
        public void run() {
            SynTempDao dao = new SynTempDao(mContext);
          SQLiteDatabase db=  DBHelper.getInstance(mContext).getReadableDatabase();

            HashMap<String ,Object> map  = new HashMap<>();
            map.put("Status",2);
            if(typeData==2){
                map.put("status",1);
            }
           Message msg =  handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",(Serializable) dao.getAllDate(db,map,0,10000,"or"));
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }).start();
    }
}
