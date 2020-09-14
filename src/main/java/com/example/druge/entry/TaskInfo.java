package com.example.druge.entry;

import java.util.List;

/**
 * Created by Administrator on 2019/3/8/008.
 */
//任务列表内容
public class TaskInfo  extends PageInfo{

    private List<TaskListInfo> Result;  //任务列表信息


    public List<TaskListInfo> getResult() {
        return Result;
    }

    public void setResult(List<TaskListInfo> result) {
        Result = result;
    }
}
