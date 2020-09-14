package com.example.druge.entry;

/**
 * Created by 16486 on 2020/4/20.
 */

public class SelectProductList {

     private Integer ID;
     private String StockName;
    private String DrugTypeName;
    private String DrugName;
    private String RFIDNo;
    private String SerialNo;
    private String StatusName;
    private Integer Status;
    private String TaskName;
    private String UserName;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public String getDrugTypeName() {
        return DrugTypeName;
    }

    public void setDrugTypeName(String drugTypeName) {
        DrugTypeName = drugTypeName;
    }

    public String getDrugName() {
        return DrugName;
    }

    public void setDrugName(String drugName) {
        DrugName = drugName;
    }

    public String getRFIDNo() {
        return RFIDNo;
    }

    public void setRFIDNo(String RFIDNo) {
        this.RFIDNo = RFIDNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
