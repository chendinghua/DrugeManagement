package com.example.druge.entry;

import java.util.List;

/**
 * Created by Administrator on 2019/3/6/006.
 */
//报废列表对象
public class ScrapInfo {

    private int PageCurrent;            //当前页
    private int PageSize;               //每页数量
    private int RowsCount;              //总数量
    private int PageCount;              //总页数
    private List<ScrapInfoList> Result; //信息集合

    public int getPageCurrent() {
        return PageCurrent;
    }

    public void setPageCurrent(int pageCurrent) {
        PageCurrent = pageCurrent;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public int getRowsCount() {
        return RowsCount;
    }

    public void setRowsCount(int rowsCount) {
        RowsCount = rowsCount;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
    }

    public List<ScrapInfoList> getResult() {
        return Result;
    }

    public void setResult(List<ScrapInfoList> result) {
        Result = result;
    }
}
