package com.example.druge.entry;

import java.util.List;

/**  替换药箱集合
 * Created by 16486 on 2020/4/26.
 */

public class ReplaceDrugeInfo  {

    private Integer stockNo;        //药箱编号

    private String  stockNoName;     //药箱名称

    private List<TaskListInfoItem> taskListInfoItems; //药品信息集合

    public ReplaceDrugeInfo(Integer stockNo, String stockNoName) {
        this.stockNo = stockNo;
        this.stockNoName = stockNoName;
    }

    public ReplaceDrugeInfo() {
    }

    public Integer getStockNo() {
        return stockNo;
    }

    public void setStockNo(Integer stockNo) {
        this.stockNo = stockNo;
    }

    public String getStockNoName() {
        return stockNoName;
    }

    public void setStockNoName(String stockNoName) {
        this.stockNoName = stockNoName;
    }

    public List<TaskListInfoItem> getTaskListInfoItems() {
        return taskListInfoItems;
    }

    public void setTaskListInfoItems(List<TaskListInfoItem> taskListInfoItems) {
        this.taskListInfoItems = taskListInfoItems;
    }
}
