package com.example.druge.entry;

/**
 * Created by 16486 on 2020/4/13.
 */

public class DrugInfo {
    private Integer ID;
    private Integer ProductID;
    private Integer AssetsTypeID;
    private String RfidNo;
    private String SerialNo;
    private String Remark;
    private Integer Status;
    private Integer TaskType;
    private Integer TaskId;
    private String UserID;
    private String UserName;
    private Integer StockID;

    private String ProductName;
    private String DrugType;

    private String isFocus="false";

    public String getIsFocus() {
        return isFocus;
    }

    public void setIsFocus(String isFocus) {
        this.isFocus = isFocus;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDrugType() {
        return DrugType;
    }

    public void setDrugType(String drugType) {
        DrugType = drugType;
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

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public Integer getStockID() {
        return StockID;
    }

    public void setStockID(Integer stockID) {
        StockID = stockID;
    }
}
