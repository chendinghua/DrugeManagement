package com.example.druge.entry;

import java.util.List;

/** 产品信息记录表
 * Created by 16486 on 2020/1/3.
 */
public class ProductInfo extends TaskListInfoItem{

    //维修表id
     private Integer DataID;
    //产品名称
    private String productName;
    //产品单位
    private String unit;
    //操作时间
    private String opTime;
    //当前用户名
    private String opUser;
    //大小
    private String size;
    //重量
    private String weight;

    //操作类型
    private String opType;
    //产品信息集合
    List<ProductInfo> productInfos;
    //选择的维护类型
    private Integer MainType;

    public Integer getDataID() {
        return DataID;
    }

    public void setDataID(Integer dataID) {
        DataID = dataID;
    }

    public Integer getMainType() {
        return MainType;
    }

    public void setMainType(Integer mainType) {
        MainType = mainType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<ProductInfo> getProductInfos() {
        return productInfos;
    }

    public void setProductInfos(List<ProductInfo> productInfos) {
        this.productInfos = productInfos;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getOpUser() {
        return opUser;
    }

    public void setOpUser(String opUser) {
        this.opUser = opUser;
    }

    public String getOpTime() {
        return opTime;
    }

    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }
}
