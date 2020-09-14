package com.example.druge.entry.UserInfo;

import com.example.druge.entry.RightInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 16486 on 2020/8/26.
 */

public class UserRightInfo implements Serializable{

    User user;

    List<RightInfo> rightInfos;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<RightInfo> getRightInfos() {
        return rightInfos;
    }

    public void setRightInfos(List<RightInfo> rightInfos) {
        this.rightInfos = rightInfos;
    }
}
