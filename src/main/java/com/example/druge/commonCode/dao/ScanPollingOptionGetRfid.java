package com.example.druge.commonCode.dao;

import android.os.Message;
import android.util.Log;

import com.example.druge.commonCode.LoopRead;
import com.example.druge.entry.ScanPollingInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16486 on 2020/3/23.
 */

public class ScanPollingOptionGetRfid extends LoopRead {

    private List<ScanPollingInfo> tempList = new ArrayList<>();

    public List<ScanPollingInfo> getTempList() {
        return tempList;
    }

    public void setTempList(List<ScanPollingInfo> tempList) {
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
            //表示RFID数据存在并且物品未扫描
            if(isExistsDataIndex>-1 && "false".equals(tempList.get(isExistsDataIndex).getIsFocus())){
                tempList.get(isExistsDataIndex).setIsFocus("true");
                tempList.get(isExistsDataIndex).setStatusName("巡检中");
                ScanPollingInfo temp= tempList.get(isExistsDataIndex);
                tempList.remove(isExistsDataIndex);
                tempList.add(0,temp);


                msg.obj=tempList;
                msg.what=1;
                //判断物品已经扫描
            }else if(isExistsDataIndex>-1){
                msg.what=0;
                //判断不符合扫描的RFID
            }else if(isExistsDataIndex==-1){
                msg.what=-1;
            }

        }else{
            msg.what=-1;
        }

        return msg;
    }


}
