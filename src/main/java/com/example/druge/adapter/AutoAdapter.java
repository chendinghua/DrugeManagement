package com.example.druge.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.druge.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 16486 on 2020/3/28.
 */

public class AutoAdapter<T> extends BaseAdapter {
    Context context;
    List<T> list;
    List<String> param = new ArrayList<>();

    public AutoAdapter(Context context, List<T> list,String ... param) {
        this.context = context;
        this.list = list;
        for (String s : param) {
            this.param.add(s);
        }
        Log.d("autoAdapter", "AutoAdapter: "+ Arrays.toString(param));

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
        View  view = from.inflate(R.layout.activity_auto_item, null);
        // 从要显示的条目布局中 获得指定的组件

        LinearLayout layoutAuto = view.findViewById(R.id.layout_auto);

        Object model= list.get(position);

        Class cla = list.get(position).getClass();
        //遍历所有属性
        Field[] field = cla.getDeclaredFields();

        for (int j = 0;j<param.size();j++){

            for (int i =0;i<field.length;i++) {
                try {

                    // 获取属性的名字
                    String name = field[i].getName();

                        // 获取属性类型
                        String type = field[i].getGenericType().toString();

                        //关键。。。可访问私有变量
                        field[i].setAccessible(true);
                    if(param.get(j).equalsIgnoreCase(name)) {
                        //给属性设置
                        if (type.equals("class java.lang.String")) {
                            // 如果type是类类型，则前面包含"class "，后面跟类名
                            Method m = model.getClass().getMethod("get" + name);
                            // 调用getter方法获取属性值
                            String value = (String) m.invoke(model);
                            if (value != null) {
                                TextView tvTitle = new TextView(view.getContext());
                                tvTitle.setText(value);


                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

                                tvTitle.setLayoutParams(lp);

                                tvTitle.setGravity(Gravity.CENTER);
                                tvTitle.setMaxLines(2);
                                tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                                layoutAuto.addView(tvTitle);
                                System.out.println("attribute value:" + value);
                                //判断是否参数名是否为标识字段

                            }else{
                                TextView tvTitle = new TextView(view.getContext());


                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

                                tvTitle.setLayoutParams(lp);

                                tvTitle.setGravity(Gravity.CENTER);
                                tvTitle.setMaxLines(2);
                                tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                                layoutAuto.addView(tvTitle);
                                //判断是否参数名是否为标识字段
                            }
                        }
                        if (type.equals("class java.lang.Integer")) {
                            Method m = model.getClass().getMethod("get" + name);
                            Integer value = (Integer) m.invoke(model);
                            if (value != null) {

                                TextView tvTitle = new TextView(view.getContext());
                                tvTitle.setText(value+"");

                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                                tvTitle.setLayoutParams(lp);

                                tvTitle.setGravity(Gravity.CENTER);
                                tvTitle.setMaxLines(2);
                                tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                                layoutAuto.addView(tvTitle);

                                System.out.println("attribute value:" + value);
                            }else{
                                TextView tvTitle = new TextView(view.getContext());
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                                tvTitle.setLayoutParams(lp);
                                tvTitle.setGravity(Gravity.CENTER);
                                tvTitle.setMaxLines(2);
                                tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                                layoutAuto.addView(tvTitle);

                            }
                        }
                        break;
                    } else if(name.equalsIgnoreCase("IsFocus")){
                        //if(context instanceof AssetsAddInfoActivity || context instanceof AssetsLoanInfoActivity || context instanceof ScanPollingOptionActivity){
                            // 如果type是类类型，则前面包含"class "，后面跟类名
                            Method m = model.getClass().getMethod("get" + name);
                            // 调用getter方法获取属性值
                            String value = (String) m.invoke(model);
                            if (value != null) {

                                if ("false".equals(value)) {
                                    view.setBackgroundColor(context.getResources().getColor(R.color.red));
                                } else if ("true".equals(value)) {
                                    view.setBackgroundColor(context.getResources().getColor(R.color.green));
                                } else if ("error".equals(value)) {
                                    view.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                                }else if("success".equals(value)){
                                    view.setBackgroundColor(context.getResources().getColor(R.color.gray1));
                                }
                            }
                      //  }
                    }
                } catch (NoSuchMethodException e) {

                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }


        }
/*

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
*/





        return view;
    }
    public void claerList (){
        list.clear();
        notifyDataSetChanged();
    }
}
