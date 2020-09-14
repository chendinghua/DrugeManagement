package com.example.druge.entry.SynTempInfoData;

/**
 * Created by 16486 on 2020/8/23.
 */
public class SynTempInfoPDA {
     private Integer      Id;        //编号
     private Integer  StorageId;           //库存id
    private String     RfidNo;               //RFID
    private String    SerialNo;               //序列号
    private String    DrugTypeName;               //产品类型名称
    private Integer   DrugTypeId;                   //产品类型id
    private Integer   StockId;                  //库位id
    private String    StockName;                //库位名
    private String    productName;              //产品名称
    private Integer    ProductId;               //产品名称id
    private Integer  synId;                     //同步表id

    public Integer OpStatus;                    //操作状态

    public String StatusName;                   //库存状态名称

    private Integer StorageStatus;              //库存状态id

    public Integer getStorageStatus() {
        return StorageStatus;
    }

    public void setStorageStatus(Integer storageStatus) {
        StorageStatus = storageStatus;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public Integer getOpStatus() {
        return OpStatus;
    }

    public void setOpStatus(Integer opStatus) {
        OpStatus = opStatus;
    }

    public Integer getDrugTypeId() {
        return DrugTypeId;
    }

    public void setDrugTypeId(Integer drugTypeId) {
        DrugTypeId = drugTypeId;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public Integer getSynId() {
        return synId;
    }

    public void setSynId(Integer synId) {
        this.synId = synId;
    }





    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getStorageId() {
        return StorageId;
    }

    public void setStorageId(Integer storageId) {
        StorageId = storageId;
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

    public String getDrugTypeName() {
        return DrugTypeName;
    }

    public void setDrugTypeName(String drugTypeName) {
        DrugTypeName = drugTypeName;
    }


    public Integer getProductId() {
        return ProductId;
    }

    public void setProductId(Integer productId) {
        ProductId = productId;
    }

    public Integer getStockId() {
        return StockId;
    }

    public void setStockId(Integer stockId) {
        StockId = stockId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
