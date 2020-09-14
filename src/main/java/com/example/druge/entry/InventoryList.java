package com.example.druge.entry;

import java.io.Serializable;

/**
 * Created by 16486 on 2020/3/10.
 */

public class InventoryList implements Serializable{

     private Integer ID;                //id
    private Integer StorageId;          //库存id
    private Integer ProductID;          //产品id
    private Integer AssetsTypeID;       //资产类型id
    private String RfidNo;              //RFID
    private String SerialNo;            //序列号
    private String Remark;              //描述
    private Integer Status;             //状态
    private Integer TaskType;           //
    private Integer TaskId=0;             //当前任务状态id
    private Integer QcType;             //QC验证
    private String ProductName;         //产品名称
    private String AsstypeName;         //资产类型名称
    private String IsFocus="false";             //焦点标识

     private Integer stockid=0;              //药箱id
     private String StockName;              //药箱名称

    private String StatusName;          //库存状态名称

    private Integer StorageStatus;      //库存状态id

    private Integer OpStatus;

    public Integer getOpStatus() {
        return OpStatus;
    }

    public void setOpStatus(Integer opStatus) {
        OpStatus = opStatus;
    }

    public Integer getStorageStatus() {
        return StorageStatus;
    }

    public void setStorageStatus(Integer storageStatus) {
        StorageStatus = storageStatus;
    }

    public Integer getStorageId() {
        return StorageId;
    }

    public void setStorageId(Integer storageId) {
        StorageId = storageId;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public Integer getStockid() {
        return stockid;
    }

    public void setStockid(Integer stockid) {
        this.stockid = stockid;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public InventoryList(int ID,String rfidNo, String serialNo, String productName, String asstypeName, String isFocus) {
        this.ID = ID;
        RfidNo = rfidNo;
        SerialNo = serialNo;
        ProductName = productName;
        AsstypeName = asstypeName;
        this.IsFocus=isFocus;
    }

    public InventoryList(Integer ID, Integer assetsTypeID, String rfidNo, String serialNo, Integer status, String productName, String asstypeName, String isFocus) {
        this.ID = ID;
        AssetsTypeID = assetsTypeID;
        RfidNo = rfidNo;
        SerialNo = serialNo;
        Status = status;
        ProductName = productName;
        AsstypeName = asstypeName;
        this.IsFocus = isFocus;
    }

    public InventoryList() {
    }

    public String getIsFocus() {
        return IsFocus;
    }

    public void setIsFocus(String isFocus) {
        IsFocus = isFocus;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getProductID() {
        return ProductID;
    }

    public void setProductID(Integer productID) {
        ProductID = productID;
    }

    public Integer getAssetsTypeID() {
        return AssetsTypeID;
    }

    public void setAssetsTypeID(Integer assetsTypeID) {
        AssetsTypeID = assetsTypeID;
    }

    public String getRfidNo() {
        return RfidNo;
    }

    public void setRfidNo(String rfidNo) {
        RfidNo = rfidNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public Integer getTaskType() {
        return TaskType;
    }

    public void setTaskType(Integer taskType) {
        TaskType = taskType;
    }

    public Integer getTaskId() {
        return TaskId;
    }

    public void setTaskId(Integer taskId) {
        TaskId = taskId;
    }

    public Integer getQcType() {
        return QcType;
    }

    public void setQcType(Integer qcType) {
        QcType = qcType;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getAsstypeName() {
        return AsstypeName;
    }

    public void setAsstypeName(String asstypeName) {
        AsstypeName = asstypeName;
    }
}
