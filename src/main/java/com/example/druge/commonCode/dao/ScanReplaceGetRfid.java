package com.example.druge.commonCode.dao;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.druge.commonCode.LoopRead;
import com.example.druge.entry.TaskListInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16486 on 2020/4/24.
 */

public class ScanReplaceGetRfid  extends LoopRead {

    private List<TaskListInfoItem> tempList = new ArrayList<>();

    public List<TaskListInfoItem> getTempList() {
        return tempList;
    }

    public void setTempList(List<TaskListInfoItem> tempList) {
        this.tempList = tempList;
    }

    @Override
    public void clearList() {
        if(tempList!=null)
            tempList.clear();
    }

    @Override
    public Message algorithm(String rfid) {
        Message msg = new Message();
        //判断是否为有效货物标签 (1001 物品  1002包装  1003货架)
        if(rfid.startsWith("1001")) {
            Log.d("epcCode1", "run: " + rfid);
            //判断rfid在数据库是否存在
            int isExistsDataIndex = -1;


            for (int i = 0; i < tempList.size(); i++) {
                if (rfid.equalsIgnoreCase(tempList.get(i).getRfidNo())) {
                    isExistsDataIndex = i;
                }
            }
        if(isExistsDataIndex!=-1){
            if("true".equals(tempList.get(isExistsDataIndex).getIsFocus())){
                msg.what=1;
            }else{
                msg.what=2;
            }
        }else{
            //不在ListView集合里面的数据  所以为异常数据
            Bundle bundle = new Bundle();
            bundle.putString("rfid",rfid);
            msg.setData(bundle);
            msg.what=-2;
        }
        }else{
            msg.what=1;
        }

        return msg;
    }
}
