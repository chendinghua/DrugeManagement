package com.example.druge.commonCode.dao;

import android.os.Bundle;
import android.os.Message;

import com.example.druge.commonCode.LoopRead;

/**
 * Created by 16486 on 2020/1/3.
 */

public class SimpleGetRfid extends   LoopRead{
    @Override
    public void clearList() {

    }

    //数据扫描RFID处理算法
    @Override
    public Message algorithm(String rfid) {
        Message msg = new Message();
        if(rfid.startsWith("1001") && scanType || rfid.startsWith("2001") && !scanType) {
            //判断rfid在数据库是否存在
            Bundle bundle = new Bundle();
            bundle.putString("rfid",rfid);
            msg.setData(bundle);
            msg.what=1;
        }else{
            msg.what=-1;
        }
        return msg;
    }
}
