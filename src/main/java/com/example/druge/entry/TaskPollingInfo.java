package com.example.druge.entry;

import java.util.List;

/**
 * Created by 16486 on 2020/3/31.
 */

public class TaskPollingInfo  extends PageInfo{
    List<Polling> Result;

    public List<Polling> getResult() {
        return Result;
    }

    public void setResult(List<Polling> result) {
        Result = result;
    }
}
