package com.example.druge.commonCode.dao;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.druge.commonCode.LoopRead;
import com.example.druge.entry.TaskListInfoItem;

import java.util.List;

/**
 * Created by 16486 on 2020/1/16.
 */

public class AssetsAddInfoGetRfid extends LoopRead {

    private List<TaskListInfoItem> tempList;

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
        if (rfid.startsWith("1001")) {

            //判断rfid在数据库是否存在
            int isExistsDataIndex = -1;
            for (int i = 0; i < tempList.size(); i++) {
                if (rfid.equalsIgnoreCase(tempList.get(i).getRfidNo())) {
                    isExistsDataIndex = i;
                }
            }
            //判断扫描的数据是否在集合中存在
            if (isExistsDataIndex != -1) {
                //判断是否 是需要扫描拣货的货品数据状态
                if ("false".equals(tempList.get(isExistsDataIndex).getIsFocus())) {

                    tempList.get(isExistsDataIndex).setIsFocus("true");
                    TaskListInfoItem temp = tempList.get(isExistsDataIndex);
                    tempList.remove(isExistsDataIndex);

                    tempList.add(0,temp);

                    msg.what = 1;
                    //成功数值添加
                    int successCount = getSuccessCount();
                    setSuccessCount(++successCount);

                    Log.d("currentCount", "algorithm: "+getSuccessCount());
                    //判断是否已确认扫描到的货品
                } else if ("true".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                    msg.what = 1;
                    //判断是否为异常扫描到的货品
                } else if ("error".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                    msg.what = -1;
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("rfid", rfid);
                msg.setData(bundle);
                msg.what = -2;
            }
        } else {
            msg.what = 1;
        }
        msg.obj = tempList;

        return msg;
    }
}
