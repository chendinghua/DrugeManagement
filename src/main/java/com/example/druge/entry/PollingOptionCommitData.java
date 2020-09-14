package com.example.druge.entry;

/** 巡检提交数据
 * Created by 16486 on 2020/3/25.
 */
public class PollingOptionCommitData {

    private Integer dicId;      //巡检选项id
    private String  dicName;    //巡检选项名称
    private boolean dicType;    //巡检提交类型
    private String  dicRemark;  //巡检选项异常描述

    public Integer getDicId() {
        return dicId;
    }

    public void setDicId(Integer dicId) {
        this.dicId = dicId;
    }

    public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }

    public boolean isDicType() {
        return dicType;
    }

    public void setDicType(boolean dicType) {
        this.dicType = dicType;
    }

    public String getDicRemark() {
        return dicRemark;
    }

    public void setDicRemark(String dicRemark) {
        this.dicRemark = dicRemark;
    }
}
