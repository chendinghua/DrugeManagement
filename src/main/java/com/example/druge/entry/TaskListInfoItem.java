package com.example.druge.entry;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/6/14/014.
 */
public class TaskListInfoItem implements Serializable{

    private Integer TaDeId;                    //任务详情id
    private Integer StorageId;              //库存id
    private String RfidNo;                //rfid
    private String SerialNo;            //序列号
    private Integer AssestId;           //资产名称id
    private String AssestName;         //产品名称
    private String AssestTypeName;         //资产类型

    private Integer AssestTypeId;



    private Integer StorageStatus;      //资产状态

    private String StorageStatusName;    //资产状态名称

    private String StatusName;          //库存状态

    private String UserName;            //操作人

    private Integer Status;             //盘点状态

      private String BeginTime;         //生产日期
      private String OverTime;          //过期日期


      private Integer  stockid;         //药箱id
      private String   StockName;       //药箱名称


     private Integer InStockNo;         //入库药箱编号
     private Integer OutStockNo;        //出库药箱编号
     private String InstockNoName;      //入库药箱名称
     private String OutstockNoName;     //出库药箱名称

    private Integer taskId;             //当前任务id

    public Integer getAssestTypeId() {
        return AssestTypeId;
    }

    public void setAssestTypeId(Integer assestTypeId) {
        AssestTypeId = assestTypeId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getInStockNo() {
        return InStockNo;
    }

    public void setInStockNo(Integer inStockNo) {
        InStockNo = inStockNo;
    }

    public Integer getOutStockNo() {
        return OutStockNo;
    }

    public void setOutStockNo(Integer outStockNo) {
        OutStockNo = outStockNo;
    }

    public String getInstockNoName() {
        return InstockNoName;
    }

    public void setInstockNoName(String instockNoName) {
        InstockNoName = instockNoName;
    }

    public String getOutstockNoName() {
        return OutstockNoName;
    }

    public void setOutstockNoName(String outstockNoName) {
        OutstockNoName = outstockNoName;
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

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getOverTime() {
        return OverTime;
    }

    public void setOverTime(String overTime) {
        OverTime = overTime;
    }

    private String IsFocus="false";             //焦点标识

    public String getStorageStatusName() {
        return StorageStatusName;
    }

    public void setStorageStatusName(String storageStatusName) {
        StorageStatusName = storageStatusName;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public Integer getAssestId() {
        return AssestId;
    }

    public void setAssestId(Integer assestId) {
        AssestId = assestId;
    }

    public Integer getTaDeId() {
        return TaDeId;
    }

    public void setTaDeId(Integer taDeId) {
        TaDeId = taDeId;
    }

    public Integer getStorageStatus() {
        return StorageStatus;
    }

    public void setStorageStatus(Integer storageStatus) {
        StorageStatus = storageStatus;
    }

    public String getIsFocus() {
        return IsFocus;
    }

    public void setIsFocus(String isFocus) {
        IsFocus = isFocus;
    }

    public Integer getStorageId() {
        return StorageId;
    }

    public void setStorageId(Integer storageId) {
        StorageId = storageId;
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


    public String getAssestName() {
        return AssestName;
    }

    public void setAssestName(String assestName) {
        AssestName = assestName;
    }

    public String getAssestTypeName() {
        return AssestTypeName;
    }

    public void setAssestTypeName(String assestTypeName) {
        AssestTypeName = assestTypeName;
    }
}
