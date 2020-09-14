package com.example.druge.entry;

import java.util.List;

/** 药箱信息
 * Created by 16486 on 2020/4/13.
 */
public class Stockdrug {
    private Integer ID;
    private String Name;
    private String StockNo;
    private String AreaNo;
    private Integer MaxNum;
    private Integer ProductType;
    private String OpUser;
    private String OpTime;
    private Integer Status;
    private String RfidNo;
    private Integer IsDefault;
    private Integer QelType;

    private List<TaskListInfoItem> DrugInfo;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStockNo() {
        return StockNo;
    }

    public void setStockNo(String stockNo) {
        StockNo = stockNo;
    }

    public String getAreaNo() {
        return AreaNo;
    }

    public void setAreaNo(String areaNo) {
        AreaNo = areaNo;
    }

    public Integer getMaxNum() {
        return MaxNum;
    }

    public void setMaxNum(Integer maxNum) {
        MaxNum = maxNum;
    }

    public Integer getProductType() {
        return ProductType;
    }

    public void setProductType(Integer productType) {
        ProductType = productType;
    }

    public String getOpUser() {
        return OpUser;
    }

    public void setOpUser(String opUser) {
        OpUser = opUser;
    }

    public String getOpTime() {
        return OpTime;
    }

    public void setOpTime(String opTime) {
        OpTime = opTime;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getRfidNo() {
        return RfidNo;
    }

    public void setRfidNo(String rfidNo) {
        RfidNo = rfidNo;
    }

    public Integer getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(Integer isDefault) {
        IsDefault = isDefault;
    }

    public Integer getQelType() {
        return QelType;
    }

    public void setQelType(Integer qelType) {
        QelType = qelType;
    }

    public List<TaskListInfoItem> getDrugInfo() {
        return DrugInfo;
    }

    public void setDrugInfo(List<TaskListInfoItem> drugInfo) {
        DrugInfo = drugInfo;
    }
}
