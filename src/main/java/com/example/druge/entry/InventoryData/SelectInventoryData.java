package com.example.druge.entry.InventoryData;

/**
 * Created by 16486 on 2020/8/25.
 */

public class SelectInventoryData {

    private Integer id;
    private Integer type;

    private String DataInfo;


    public String getDataInfo() {
        return DataInfo;
    }

    public void setDataInfo(String dataInfo) {
        DataInfo = dataInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
