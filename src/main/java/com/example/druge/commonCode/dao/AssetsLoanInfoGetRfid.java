package com.example.druge.commonCode.dao;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.druge.commonCode.LoopRead;
import com.example.druge.entry.TaskListInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16486 on 2020/1/20.
 */

public class AssetsLoanInfoGetRfid extends LoopRead{
    private List<TaskListInfoItem> tempList = new ArrayList<>();

    public List<TaskListInfoItem> getTempList() {
        return tempList;
    }

    public void setTempList(List<TaskListInfoItem> tempList) {
        this.tempList = tempList;
    }

    private List<TaskListInfoItem> errorMaps = new ArrayList<>();


    public void addErrorMaps(TaskListInfoItem errorData)
    {
        errorMaps.add(errorData);
    }

    @Override
    public void clearList() {
        if(tempList!=null)
            tempList.clear();
        if(errorMaps!=null)
            errorMaps.clear();
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

            for (int i=0;i<errorMaps.size();i++)
            {
                if(rfid.equalsIgnoreCase(errorMaps.get(i).getRfidNo())){
                    isExistsDataIndex=-4;
                }
            }

            Log.d("resultDataRfid", "run: "+isExistsDataIndex  +"Rfid"+rfid);
            //判断扫描的数据是否在集合中存在
            if (isExistsDataIndex > -1) {
                //判断是否 是需要扫描拣货的货品数据状态
                if ("false".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                               tempList.get(isExistsDataIndex).setIsFocus("true");
                    msg.what = 1;
                    msg.obj=tempList;
                    //   successCount++;
                    //判断是否已确认扫描到的货品
                } else if ("true".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                    msg.what = 2;
                    //判断是否为异常扫描到的货品
                } else if ("error".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                    msg.what = -1;
                }
            }else if(isExistsDataIndex==-1){
                //不在ListView集合里面的数据  所以为异常数据
                Bundle bundle = new Bundle();
                bundle.putString("rfid",rfid);
                msg.setData(bundle);
                msg.what=-2;
            }else if(isExistsDataIndex==-4){
                msg.what=-1;
            }
        }else{
            msg.what=2;
        }

        return msg;
    }
}
