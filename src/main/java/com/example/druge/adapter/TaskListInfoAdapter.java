package com.example.druge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.druge.R;
import com.example.druge.entry.TaskListInfoItem;

import java.util.List;

/** 任务信息列表  新增操作列表
 * Created by 16486 on 2020/1/9.
 */
public class TaskListInfoAdapter extends BaseAdapter {
    private Context context;
    private List<TaskListInfoItem> list;

    private boolean isShowStorageStatusName=true;


    public TaskListInfoAdapter(Context context, List<TaskListInfoItem> list,boolean isShowStorageStatusName) {
        this.context = context;
        this.list = list;
        this.isShowStorageStatusName=isShowStorageStatusName;
    }

    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View contextView, ViewGroup viewGroup) {



        // 通过LayoutInflater 类的 from 方法 再 使用 inflate()方法得到指定的布局
        // 得到ListView中要显示的条目的布局
        View  view = LayoutInflater.from(context).inflate(R.layout.activity_task_info_list_item, null);
        // 从要显示的条目布局中 获得指定的组件

        TextView storageId = view.findViewById(R.id.tv_title_task_storage_id);
        TextView rfid = view.findViewById(R.id.tv_title_task_rfid);
        TextView serialNo = view.findViewById(R.id.tv_title_task_serialNo);
        TextView productType = view.findViewById(R.id.tv_title_task_productType);
        TextView productName = view.findViewById(R.id.tv_title_task_productName);

        TextView storageStatusName = view.findViewById(R.id.tv_title_task_StorageStatusName);

        if(list!=null){
            storageId.setText(list.get(position).getTaDeId()==null?"":list.get(position).getTaDeId()+"");
            rfid.setText(list.get(position).getRfidNo());
            serialNo.setText(list.get(position).getSerialNo());
            productType.setText(list.get(position).getAssestName());
            productName.setText(list.get(position).getAssestTypeName());
            if(isShowStorageStatusName){
                storageStatusName.setVisibility(View.VISIBLE);
                storageStatusName.setText(list.get(position).getStorageStatusName());
            }else{
                storageStatusName.setVisibility(View.GONE);
            }
        }
      //  if(context instanceof AssetsAddInfoActivity   || context instanceof AssetsLoanInfoActivity){
            if("false".equals(list.get(position).getIsFocus())){
                view.setBackgroundColor(context.getResources().getColor(R.color.red));
            }else if("true".equals(list.get(position).getIsFocus()) ){
                view.setBackgroundColor(context.getResources().getColor(R.color.green));
            }else if("error".equals(list.get(position).getIsFocus())){
                view.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            }else  if("success".equals(list.get(position).getIsFocus())){
                view.setBackgroundColor(context.getResources().getColor(R.color.gray1));
            }

        //}



        return view;
    }
}
