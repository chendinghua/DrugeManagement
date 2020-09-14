package com.example.druge.ui.page;

/**
 * Created by Administrator on 2019/1/15/015.
 */

public interface OnPageChangeListener {
    /**
     * 点击分页按钮时触发此操作
     * @param curPage 当前页
     * @param numPerPage 每页显示个数
     */
    public void pageChanged(int curPage, int numPerPage);
}
