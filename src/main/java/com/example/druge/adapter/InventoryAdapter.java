package com.example.druge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.druge.R;
import com.example.druge.entry.InventoryList;

import java.util.List;

/** 产品信息适配器
 * Created by 16486 on 2020/1/3.
 */
public class InventoryAdapter extends BaseAdapter{

    Context context;
    List<InventoryList> list;

    public InventoryAdapter(Context context, List<InventoryList> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        int len = 0;
        if(list!=null)
        len= list.size();
            return len;
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
            LayoutInflater from = LayoutInflater.from(context);
          View  view = from.inflate(R.layout.activity_inventory_operation_items, null);
            // 从要显示的条目布局中 获得指定的组件

            TextView tvRfid = view.findViewById(R.id.tv_title_inventory_operation_rfid);
            TextView tvSerialNo = view.findViewById(R.id.tv_title_inventory_operation_SerialNo);
            TextView tvPorductName = view.findViewById(R.id.tv_title_inventory_operation_product_name);
            TextView tvAssetsType = view.findViewById(R.id.tv_title_inventory_operation_assets_type);

            if(list!=null){
                tvRfid.setText(list.get(position).getRfidNo());
                tvSerialNo.setText(list.get(position).getSerialNo());
                tvPorductName.setText(list.get(position).getProductName());
                tvAssetsType.setText(list.get(position).getAsstypeName());
            }

            if("false".equals(list.get(position).getIsFocus())){
                view.setBackgroundColor(context.getResources().getColor(R.color.red));
            }else if("true".equals(list.get(position).getIsFocus()) ){
                view.setBackgroundColor(context.getResources().getColor(R.color.green));
            }else if("error".equals(list.get(position).getIsFocus())){
                view.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            }


        return view;
    }
    public void claerList (){
        list.clear();
        notifyDataSetChanged();
    }

}
