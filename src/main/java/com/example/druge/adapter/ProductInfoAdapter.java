package com.example.druge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.druge.R;
import com.example.druge.entry.ProductInfo;

import java.util.List;

/** 产品信息适配器
 * Created by 16486 on 2020/1/3.
 */
public class ProductInfoAdapter extends BaseAdapter{

    Context context;
    List<ProductInfo> list;

    public ProductInfoAdapter(Context context, List<ProductInfo> list) {
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
          View  view = from.inflate(R.layout.activity_product_record_list_items, null);
            // 从要显示的条目布局中 获得指定的组件

            TextView tvProductName = view.findViewById(R.id.tv_title_product_record_productName);
            TextView tvUnit = view.findViewById(R.id.tv_title_product_record_unit);
            TextView tvOpTime = view.findViewById(R.id.tv_title_product_record_opTime);
            TextView tvOpUser = view.findViewById(R.id.tv_title_product_record_opUser);
            TextView tvOpType = view.findViewById(R.id.tv_title_product_record_opType);

            if(list!=null){
                tvProductName.setText(list.get(position).getAssestName());
                tvUnit.setText(list.get(position).getUnit());
                tvOpTime.setText(list.get(position).getOpTime());
                tvOpUser.setText(list.get(position).getOpUser());
                tvOpType.setText(list.get(position).getOpType());
            }



        return view;
    }
    public void claerList (){
        list.clear();
        notifyDataSetChanged();
    }

}
