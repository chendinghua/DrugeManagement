package com.example.druge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.druge.R;
import com.example.druge.entry.TaskListInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by 16486 on 2020/1/10.
 */

public class TaskListAdapter  extends BaseAdapter{
    private Context context;
    private List<TaskListInfo> list;

    public TaskListAdapter(Context context, List<TaskListInfo> list) {
        this.context = context;
        this.list = list;
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
          View  view = LayoutInflater.from(context).inflate(R.layout.fragment_task_list_item, null);
            // 从要显示的条目布局中 获得指定的组件

            TextView taskId = view.findViewById(R.id.tv_title_task_id);
            TextView typeName = view.findViewById(R.id.tv_title_task_type_name);
            TextView taskName = view.findViewById(R.id.tv_title_task_name);
            TextView createName = view.findViewById(R.id.tv_title_task_CreateName);
            TextView statusName = view.findViewById(R.id.tv_title_task_statusName);

            if(list!=null){
                taskId.setText(list.get(position).getID()+"");
                typeName.setText(list.get(position).getTaskTypeName());
                taskName.setText(list.get(position).getContent());
                createName.setText(list.get(position).getCreatorName());
                statusName.setText(list.get(position).getStatusName());
            }

        Object model= list.get(position);

        Class cla = list.get(position).getClass();
        //遍历所有属性
        Field[] field = cla.getDeclaredFields();

        for (int i =0;i<field.length;i++) {
            try {

                // 获取属性的名字
                String name = field[i].getName();
                // 获取属性类型
                String type = field[i].getGenericType().toString();


                //关键。。。可访问私有变量
                field[i].setAccessible(true);
                //给属性设置
                if (type.equals("class java.lang.String")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = model.getClass().getMethod("get" + name);
                    // 调用getter方法获取属性值
                    String value = (String) m.invoke(model);
                    if (value != null) {

                        System.out.println("attribute   "+ name + " value:" + value);
                    }

                }
                if (type.equals("class java.lang.Integer")) {
                    Method m = model.getClass().getMethod("get" + name);
                    Integer value = (Integer) m.invoke(model);
                    if (value != null) {
                        System.out.println("attribute   "+ name + " value:" + value);
                    }
                }
                if (type.equals("class java.lang.Short")) {
                    Method m = model.getClass().getMethod("get" + name);
                    Short value = (Short) m.invoke(model);
                    if (value != null) {
                        System.out.println("attribute   "+ name + " value:" + value);
                    }
                }
                if (type.equals("class java.lang.Double")) {
                    Method m = model.getClass().getMethod("get" + name);
                    Double value = (Double) m.invoke(model);
                    if (value != null) {
                        System.out.println("attribute   "+ name + " value:" + value);
                    }
                }
                if (type.equals("class java.lang.Boolean")) {
                    Method m = model.getClass().getMethod("get" + name);
                    Boolean value = (Boolean) m.invoke(model);
                    if (value != null) {
                        System.out.println("attribute   "+ name + " value:" + value);
                    }
                }

            } catch (NoSuchMethodException e) {

            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return view;
    }
}
