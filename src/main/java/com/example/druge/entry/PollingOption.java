package com.example.druge.entry;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by 16486 on 2020/3/26.
 */

public class PollingOption {
    private Integer ID;                             //巡检id
    private Integer Template;                       //模板id
    private String Remark;   //巡检选项描述集合
    private List<PollingOptionCommitData> datas;

    private List<ImagesData> AttImgList;            //图片数据集合

    public List<ImagesData> getAttImgList() {
        return AttImgList;
    }

    public void setAttImgList(List<ImagesData> attImgList) {
        AttImgList = attImgList;
    }

    public Integer getTemplate() {
        return Template;
    }

    public void setTemplate(Integer Template) {
        this.Template = Template;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
        setDatas(JSON.parseArray(remark,PollingOptionCommitData.class));
    }

    public List<PollingOptionCommitData> getDatas() {
        return datas;
    }

    public void setDatas(List<PollingOptionCommitData> datas) {
        this.datas = datas;
    }
}
