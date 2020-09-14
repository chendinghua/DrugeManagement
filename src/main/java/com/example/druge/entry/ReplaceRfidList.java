package com.example.druge.entry;

/**
 * Created by 16486 on 2020/4/24.
 */

public class ReplaceRfidList {
    public String rFIDNo ;
    public int storageID ;
    /// <summary>
    /// 状态：1、需要入库的 2、需要出库的
    /// </summary>
    public int status ;
    /// <summary>
    /// 药品ID
    /// </summary>
    public int productID ;

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
