package com.example.druge.entry;

import java.util.List;

/**
 * Created by Administrator on 2019/3/8/008.
 */

//任务列表集合内容
public class TaskListInfo {

   // "id","taskType","name","createName","statusName"

    private Integer ID;                 //编号
    private int DataID;             //订单id
    private int TaskType;           //任务类型id
    private String TaskTypeName;    //任务类型名称
    private String Content;         //任务备注
    private int CreatorID;          //创建人id
    private String CreatorName;     //创建人名称
    private String CreatorTime;     //创建时间
    private int Status;             //任务状态id
    private String StatusName;      //任务状态名称
    private String Remark;          //描述

    private int Num;                //数量

    private String OpUser;          //操作人
    private String OpTime;          //操作时间

    private List<TaskListInfoItem> TaskDetailList;


    public List<TaskListInfoItem> getTaskDetailList() {
        return TaskDetailList;
    }

    public void setTaskDetailList(List<TaskListInfoItem> taskDetailList) {
        TaskDetailList = taskDetailList;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int num) {
        Num = num;
    }

   /* public List<TaskListInfoItem> getTaskDetailList() {
        return TaskDetailList;
    }

    public void setTaskDetailList(List<TaskListInfoItem> taskDetailList) {
        TaskDetailList = taskDetailList;
    }
*/
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

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public int getDataID() {
        return DataID;
    }

    public void setDataID(int dataID) {
        DataID = dataID;
    }

    public int getTaskType() {
        return TaskType;
    }

    public void setTaskType(int taskType) {
        TaskType = taskType;
    }

    public String getTaskTypeName() {
        return TaskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        TaskTypeName = taskTypeName;
    }



    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getCreatorID() {
        return CreatorID;
    }

    public void setCreatorID(int creatorID) {
        CreatorID = creatorID;
    }

    public String getCreatorName() {
        return CreatorName;
    }

    public void setCreatorName(String creatorName) {
        CreatorName = creatorName;
    }

    public String getCreatorTime() {
        return CreatorTime;
    }

    public void setCreatorTime(String creatorTime) {
        CreatorTime = creatorTime;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
