package com.example.druge.commonCode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.exception.ConfigurationException;

/** RFID循环读取工具类
 * Created by 16486 on 2019/12/20.
 */
public abstract class LoopRead {

    private Handler tagHandler = new Handler();
    private boolean loopFlag = false;
    private  Runnable myRunnable;
    private int successCount=0;
    //如果为药品为true  如为药箱为false
    protected boolean scanType =true;


    public boolean isScanType() {
        return scanType;
    }

    public void setScanType(boolean scanType) {
        this.scanType = scanType;
    }

    public void clearSuccessCount(){
        successCount=0;
    }


    public Runnable getMyRunnable() {
        return myRunnable;
    }

    protected void setSuccessCount(int successCount){
        this.successCount=successCount;
    }
    public int getSuccessCount(){
        return successCount;
    }

    public boolean getLoopFlag(){
        return loopFlag;

    }

    //清除临时存储集合
    public abstract void clearList();
    //开启循环标识
    public void setLoopFlag(boolean loopFlag){
        this.loopFlag=loopFlag;
    }
    //停止读取数据
    public void closeRead(){
        loopFlag=false;
        if(myRunnable!=null)
        tagHandler.removeCallbacks(myRunnable);
    }





    public void read(final Handler handler){


        myRunnable = new Runnable(){

            public void run() {
                RFIDWithUHF mReader = null;
                try {
                    mReader = RFIDWithUHF.getInstance();
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                }
                String strTid;
                String strResult;
                String[] res = null;
                if (loopFlag && mReader != null) {
                    res = mReader.readTagFromBuffer();
                    if (res != null) {
                        strTid = res[0];
                        if (strTid.length() != 0 && !strTid.equals("0000000" +
                                "000000000") && !strTid.equals("000000000000000000000000")) {
                            strResult = "TID:" + strTid + "\n";
                        } else {
                            strResult = "";
                        }
                        Log.d("epcCode", "run: " + res[1] + strResult);

                        String newRfid = (res[1] + strResult).length() == 24 ? (res[1] + strResult) : (res[1] + strResult).substring(4, (res[1] + strResult).length());
                        Log.d("epcCode1", "run: " + newRfid);
                        handler.sendMessage(algorithm(newRfid));

                    }
                }
                tagHandler.postDelayed(this,1);
            }

        };
        tagHandler.postDelayed(myRunnable,1);
    }

    public abstract Message algorithm(String rfid);


}
