package com.example.druge.entry.SynData;

import java.io.Serializable;

/**
 * Created by 16486 on 2020/8/21.
 */

public class SynTemp  implements Serializable{

    private   Integer    Id=0;                    //id
    private String      TaskType;               //任务类型
    private Integer     taskTypeId;             //任务类型id
    private  Integer TaskID;                    //当前任务表的id
    private String       OpUser;
    private  Integer      OpUserID;
    private String       OpTime;
    private String       remark;
    private String      Content;
    private String       imageFile;

    private Integer     Status;                 //任务状态id

    private String      StatusName;             //任务状态

    private  Integer DataID;                    //相对应的任务的id

    private String endtime;                     //提交任务时间

    private Integer submitUserID;               //操作任务提交的人id

    private String submitUser;                  //操作任务提交的人

    public Integer getSubmitUserID() {
        return submitUserID;
    }

    public void setSubmitUserID(Integer submitUserID) {
        this.submitUserID = submitUserID;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Integer getDataID() {
        return DataID;
    }

    public void setDataID(Integer dataID) {
        DataID = dataID;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    private String pickNo;                      //当前操作的批次号

    public String getPickNo() {
        return pickNo;
    }

    public void setPickNo(String pickNo) {
        this.pickNo = pickNo;
    }

    public Integer getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(Integer taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getTaskType() {
        return TaskType;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public Integer getTaskID() {
        return TaskID;
    }

    public void setTaskID(Integer taskID) {
        TaskID = taskID;
    }


    public String getOpUser() {
        return OpUser;
    }

    public void setOpUser(String opUser) {
        OpUser = opUser;
    }

    public Integer getOpUserID() {
        return OpUserID;
    }

    public void setOpUserID(Integer opUserID) {
        OpUserID = opUserID;
    }

    public String getOpTime() {
        return OpTime;
    }

    public void setOpTime(String opTime) {
        OpTime = opTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
