package com.example.druge.entry;

/**
 * Created by Administrator on 2019/3/13/013.
 */
//字典表数据
public class DiclistByGroupName {

    private int ID;                 //编号
    private String Name;            //显示名称
    private String Value;           //数据值
    private String GroupName;       //组名称
    private int State;              //状态

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }
}
