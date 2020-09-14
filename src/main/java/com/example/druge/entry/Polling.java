package com.example.druge.entry;

/**
 * Created by 16486 on 2020/3/30.
 */

public class Polling {

    private Integer ID;
    private Integer Type;
    private String StartTime;
    private String EndTime;
    private String OpUser;
    private String OpTime;
    private Integer Status;
    private String CreateUserName;
    private String StatusName;
    private String TypeName;

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

    public String getCreateUserName() {
        return CreateUserName;
    }

    public void setCreateUserName(String createUserName) {
        CreateUserName = createUserName;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }
}
