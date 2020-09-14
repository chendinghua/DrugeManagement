package com.example.druge.entry;

/**  调拨任务表
 * Created by 16486 on 2020/4/2.
 */
public class Allot{
    private Integer ID;
    private Integer TaskID;                     //任务id
    private String CreateTime;                  //创建时间
    private String EndTime;                     //结束时间
    private String ConTent;                     //标题
    private String CreateUserName;              //创建用户名称
    private String TaskType;                    //任务类型名称
    private String InDeptName;                  //申请公司
    private String OutDeptName;                 //执行公司
    private Integer Status;                      //状态id
    private String StatusName;                  //状态名
    private String Remark;                      //描述




    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getTaskID() {
        return TaskID;
    }

    public void setTaskID(Integer taskID) {
        TaskID = taskID;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getConTent() {
        return ConTent;
    }

    public void setConTent(String conTent) {
        ConTent = conTent;
    }

    public String getCreateUserName() {
        return CreateUserName;
    }

    public void setCreateUserName(String createUserName) {
        CreateUserName = createUserName;
    }

    public String getTaskType() {
        return TaskType;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public String getInDeptName() {
        return InDeptName;
    }

    public void setInDeptName(String inDeptName) {
        InDeptName = inDeptName;
    }

    public String getOutDeptName() {
        return OutDeptName;
    }

    public void setOutDeptName(String outDeptName) {
        OutDeptName = outDeptName;
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

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
