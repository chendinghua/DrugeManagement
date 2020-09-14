package com.example.druge.entry;

/**
 * 盘点数据提交
 * Created by Administrator on 2019/4/1/001.
 */

public class InventoryCommitData {
    private int Status;                 //状态
    private String RfidNo;              //盘点的RFID
    private int StorageStatus;          //库存状态  1、正常数据  2、未盘点数据  3、异常数据
    private int StockID;                //药箱id
    private int TaskID;                 //所属任务id

    private int id;                     //任务详情id


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStorageStatus() {
        return StorageStatus;
    }

    public void setStorageStatus(int storageStatus) {
        StorageStatus = storageStatus;
    }

    public int getStockID() {
        return StockID;
    }

    public void setStockID(int stockID) {
        StockID = stockID;
    }

    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getRfidNo() {
        return RfidNo;
    }

    public void setRfidNo(String rfidNo) {
        RfidNo = rfidNo;
    }
}
