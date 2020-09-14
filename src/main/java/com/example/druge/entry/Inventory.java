package com.example.druge.entry;

import java.util.List;

/**
 * Created by 16486 on 2020/3/10.
 */

public class Inventory {

      private Integer  ID;
      private Integer  Type;
      private String   StartTime;
      private String   EndTime;
      private String   Result;
      private String   OpUser;
      private String   OpTime;
      private Integer  Status;
      private String   Remark;
      private String   DetailList;

    private List<InventoryList> InventoryList;

    public List<InventoryList> getInventoryList() {
        return InventoryList;
    }

    public void setInventoryList(List<com.example.druge.entry.InventoryList> inventoryList) {
        InventoryList = inventoryList;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
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

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getDetailList() {
        return DetailList;
    }

    public void setDetailList(String detailList) {
        DetailList = detailList;
    }
}
