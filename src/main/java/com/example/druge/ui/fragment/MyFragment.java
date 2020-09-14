package com.example.druge.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.druge.R;
import com.example.druge.adapter.TaskListInfoAdapter;
import com.example.druge.entry.TaskListInfoItem;

import java.util.ArrayList;
import java.util.List;

public class MyFragment extends Fragment {
	private TaskListInfoAdapter adapter;
	private LayoutInflater inflate;
	private List<TaskListInfoItem> datas=new ArrayList<>();
	private ListView listView;
	Activity activity;
	private boolean isLoadActivity=false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		LinearLayout root = (LinearLayout)inflater.inflate(R.layout.page, null);
		listView = (ListView)root.findViewById(R.id.listView);
		adapter = new TaskListInfoAdapter(getActivity().getBaseContext(),datas,false);
		listView.setAdapter(adapter);
		myReceiver = new MyReceiver();

		IntentFilter intentFilter = new IntentFilter();
			activity =  getActivity();
		intentFilter.addAction("my_action_fragment");
		LocalBroadcastManager.getInstance(activity).registerReceiver(myReceiver, intentFilter);
		Log.d("myfragment", "onCreateView: ");
		isLoadActivity=true;
		return root;
	}
	private MyReceiver myReceiver;
	@Override
	public void onResume() {
		super.onResume();
	}
	//创建广播接收实例
	class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//获取数据
			List<TaskListInfoItem> taskListInfoItems =(List<TaskListInfoItem>) intent.getSerializableExtra("taskListInfoItems");
			if(taskListInfoItems!=null){
				datas.clear();
				datas.addAll(taskListInfoItems);
				adapter.notifyDataSetChanged();
			}
			Log.d("myfragment", "onReceive: ");
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(myReceiver);
	}
	public void  updateData(final  Handler handler){
	  	final Handler handler1 = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if(!isLoadActivity){
					handler1.postDelayed(this,500);
				}else{
					handler.sendMessage(new Message());
				}
			}
		};
		handler1.postDelayed(runnable,500);
	}
}
