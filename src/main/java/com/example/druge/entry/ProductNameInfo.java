package com.example.druge.entry;

/** 药品名称信息
 * Created by 16486 on 2020/4/20.
 */
public class ProductNameInfo {

     private Integer ID;
     private String ProductName;
    private Integer ProductType;
    private String DescInfo;
    private String Unit;
    private String Weight;
    private String Size;
    private Integer MaxStorageNum;
    private Integer MinStorageNum;
    private String LoseEfficacyDate;
    private String Remark;
    private Integer Status;
    private String OpTime;
    private String OpUser;
    private String StartEfficacyDate;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Integer getProductType() {
        return ProductType;
    }

    public void setProductType(Integer productType) {
        ProductType = productType;
    }

    public String getDescInfo() {
        return DescInfo;
    }

    public void setDescInfo(String descInfo) {
        DescInfo = descInfo;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public Integer getMaxStorageNum() {
        return MaxStorageNum;
    }

    public void setMaxStorageNum(Integer maxStorageNum) {
        MaxStorageNum = maxStorageNum;
    }

    public Integer getMinStorageNum() {
        return MinStorageNum;
    }

    public void setMinStorageNum(Integer minStorageNum) {
        MinStorageNum = minStorageNum;
    }

    public String getLoseEfficacyDate() {
        return LoseEfficacyDate;
    }

    public void setLoseEfficacyDate(String loseEfficacyDate) {
        LoseEfficacyDate = loseEfficacyDate;
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

    public String getOpTime() {
        return OpTime;
    }

    public void setOpTime(String opTime) {
        OpTime = opTime;
    }

    public String getOpUser() {
        return OpUser;
    }

    public void setOpUser(String opUser) {
        OpUser = opUser;
    }

    public String getStartEfficacyDate() {
        return StartEfficacyDate;
    }

    public void setStartEfficacyDate(String startEfficacyDate) {
        StartEfficacyDate = startEfficacyDate;
    }
}
