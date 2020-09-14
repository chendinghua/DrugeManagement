package com.example.druge.commonCode.dao;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.druge.commonCode.LoopRead;
import com.example.druge.entry.ManualReturnData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16486 on 2020/3/23.
 */

public class ManualAddReturnGetRfid extends LoopRead {

    private List<ManualReturnData> tempList = new ArrayList<>();

    public List<ManualReturnData> getTempList() {
        return tempList;
    }

    public void setTempList(List<ManualReturnData> tempList) {
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



            Log.d("resultDataRfid", "run: "+isExistsDataIndex  +"Rfid"+rfid);
            //判断扫描的数据是否在集合中存在
            if (isExistsDataIndex > -1) {
                //判断是否 是需要扫描拣货的货品数据状态
                if ("false".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                    //           tempList.get(isExistsDataIndex).put("isUpdate", "true");
                    msg.what = -1;
                    //   successCount++;
                    //判断是否已确认扫描到的货品
                } else if ("true".equals(tempList.get(isExistsDataIndex).getIsFocus())) {
                    msg.what = 1;
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
            }
        }else{
            msg.what=1;
        }

        return msg;
    }
}
