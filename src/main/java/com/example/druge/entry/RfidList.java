package com.example.druge.entry;

/**
 * Created by 16486 on 2020/4/14.
 */

public class RfidList {
    public String rFIDNo ;
    public int storageID;
    /// <summary>
    /// 状态：1、未扫描到 2、扫描到的
    /// </summary>
    public int status;
    /// <summary>
    /// 药品ID
    /// </summary>
    public int productID;

    public RfidList(String rFIDNo, int storageID, int status, int productID) {
        this.rFIDNo = rFIDNo;
        this.storageID = storageID;
        this.status = status;
        this.productID = productID;
    }

    public RfidList() {
    }

    public String getrFIDNo() {
        return rFIDNo;
    }

    public void setrFIDNo(String rFIDNo) {
        this.rFIDNo = rFIDNo;
    }

    public int getStorageID() {
        return storageID;
    }

    public void setStorageID(int storageID) {
        this.storageID = storageID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }
}
