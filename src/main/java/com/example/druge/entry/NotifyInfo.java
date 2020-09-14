package com.example.druge.entry;

import java.io.Serializable;

/**  消息通知对象
 * Created by Administrator on 2019/4/1/001.
 */

public class NotifyInfo implements Serializable{
    private int  ID;
    private int UserID;
    private String Title;
    private String Content;
    private int Type;
    private int Platform;
    private int Status;
    private int CreatorID;
    private String  CreatorName;
    private String  CreateTime;

    private String TypeName;

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getPlatform() {
        return Platform;
    }

    public void setPlatform(int platform) {
        Platform = platform;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
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

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
}
