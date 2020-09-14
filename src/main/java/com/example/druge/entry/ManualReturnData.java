package com.example.druge.entry;

/** 手动归还扫描数据查询
 * Created by 16486 on 2020/3/23.
 */
public class ManualReturnData {

    private String AssetsTypeName;
    private Integer AssetsTypeID;
    private String productName;
    private Integer productid;
    private String RfidNo;
    private String SerialNo;
    private Integer TaskType;
    private String TaskName;
    private Integer Userid;
    private String username;

    private String isFocus;

    public String getIsFocus() {
        return isFocus;
    }

    public void setIsFocus(String isFocus) {
        this.isFocus = isFocus;
    }

    public String getAssetsTypeName() {
        return AssetsTypeName;
    }

    public void setAssetsTypeName(String assetsTypeName) {
        AssetsTypeName = assetsTypeName;
    }

    public Integer getAssetsTypeID() {
        return AssetsTypeID;
    }

    public void setAssetsTypeID(Integer assetsTypeID) {
        AssetsTypeID = assetsTypeID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
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

    public Integer getTaskType() {
        return TaskType;
    }

    public void setTaskType(Integer taskType) {
        TaskType = taskType;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public Integer getUserid() {
        return Userid;
    }

    public void setUserid(Integer userid) {
        Userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
