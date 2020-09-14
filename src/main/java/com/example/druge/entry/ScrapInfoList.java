package com.example.druge.entry;

/**
 * Created by Administrator on 2019/3/6/006.
 */

//报废列表信息
public class ScrapInfoList {

     private int ID;                //编号
    private int StorageID;          //库存id
     private String ProductType;    //产品类型id
    private String StockNo;         //库位编号
    private String SerialNo;     //批次号或序列号
    private int Num;                //数量
    private String ScrapReason;     //报废描述

    private String MaintainReason;  //维修描述

    private String OpUser;          //操作员
    private String OpTime;          //操作时间
    private int Status;             //状态
    private String statusName;      //状态名

    private String Code;            //job号
    private String ItemNo;          //part号

    private String StockName;       //库位名

    public String getMaintainReason() {
        return MaintainReason;
    }

    public void setMaintainReason(String maintainReason) {
        MaintainReason = maintainReason;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public int getStorageID() {
        return StorageID;
    }

    public void setStorageID(int storageID) {
        StorageID = storageID;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }

    public String getStockNo() {
        return StockNo;
    }

    public void setStockNo(String stockNo) {
        StockNo = stockNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

    public String getScrapReason() {
        return ScrapReason;
    }

    public void setScrapReason(String scrapReason) {
        ScrapReason = scrapReason;
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

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
