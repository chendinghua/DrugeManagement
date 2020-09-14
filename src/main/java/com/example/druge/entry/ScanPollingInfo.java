package com.example.druge.entry;

/**
 * Created by 16486 on 2020/3/30.
 */

public class ScanPollingInfo {
    private Integer Id;
    private Integer PollingID;
    private String RfidNo;
    private Integer Status;
    private String AttID;
    private String Remark;
    private String Template;
    private String UserName;
    private String DepName;

    private String AssetsType;
    private String AssetsType1;
    private String StatusName;

    private String IsFocus="false";


    public String getIsFocus() {
        return IsFocus;
    }

    public void setIsFocus(String isFocus) {
        IsFocus = isFocus;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDepName() {
        return DepName;
    }

    public void setDepName(String depName) {
        DepName = depName;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getPollingID() {
        return PollingID;
    }

    public void setPollingID(Integer pollingID) {
        PollingID = pollingID;
    }

    public String getRfidNo() {
        return RfidNo;
    }

    public void setRfidNo(String rfidNo) {
        RfidNo = rfidNo;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getAttID() {
        return AttID;
    }

    public void setAttID(String attID) {
        AttID = attID;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getTemplate() {
        return Template;
    }

    public void setTemplate(String template) {
        Template = template;
    }

    public String getAssetsType() {
        return AssetsType;
    }

    public void setAssetsType(String assetsType) {
        AssetsType = assetsType;
    }

    public String getAssetsType1() {
        return AssetsType1;
    }

    public void setAssetsType1(String assetsType1) {
        AssetsType1 = assetsType1;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }
}
