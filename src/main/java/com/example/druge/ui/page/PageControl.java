package com.example.druge.ui.page;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.druge.R;


/**
 * Created by Administrator on 2019/1/15/015.
 */

public class PageControl extends LinearLayout implements View.OnClickListener{
    private ImageButton firstImg;           //第一页
    private ImageButton preImg;             //上一页
    private ImageButton nextImg;            //下一页
    private ImageButton endImg;             //最后一页
    private TextView totalPageText;         //显示总页数
    private TextView totalCountText;        //显示总数量
    private TextView curPageText;           //显示当前页数
    public  int numPerPage=9;               //每页数量
    public int  curPage=1;                  //当前页数
    public  int count=0;                    //总数量
    private OnPageChangeListener pageChangeListener;
    public PageControl(Context context) {
        super(context);
        initPageComposite(context);
    }
    public PageControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPageComposite(context);
    }
    public PageControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPageComposite(context);
    }
    private void initPageComposite(Context context){
        this.setPadding(5,5,5,5);
        firstImg=new ImageButton(context);
        firstImg.setTag(1);
        firstImg.setImageResource(R.drawable.bg_firstpage);
        firstImg.setPadding(0,0,0,0);
        LayoutParams layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParam.setMargins(20,0,5,0);
        firstImg.setLayoutParams(layoutParam);
        firstImg.setOnClickListener(this);
        this.addView(firstImg);
        preImg=new ImageButton(context);
        preImg.setTag(2);
        preImg.setImageResource(R.drawable.bg_prepage);
        preImg.setPadding(0,0,0,0);
        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParam.setMargins(20,0,5,0);
        preImg.setLayoutParams(layoutParam);
        preImg.setOnClickListener(this);
        this.addView(preImg);
        nextImg=new ImageButton(context);
        nextImg.setTag(3);
        nextImg.setImageResource(R.drawable.bg_nextpage);
        nextImg.setPadding(0,0,0,0);
        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParam.setMargins(20,0,5,0);
        nextImg.setLayoutParams(layoutParam);
        nextImg.setOnClickListener(this);
        this.addView(nextImg);
        endImg=new ImageButton(context);
        endImg.setTag(4);
        endImg.setImageResource(R.drawable.bg_lastpage);
        endImg.setPadding(0,0,0,0);
        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParam.setMargins(20,0,5,0);
        endImg.setLayoutParams(layoutParam);
        endImg.setOnClickListener(this);
        this.addView(endImg);
        totalCountText = new TextView(context);
        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParam.setMargins(25,0,5,0);
        totalCountText.setLayoutParams(layoutParam);
        totalCountText.setText("总数量");
        this.addView(totalCountText);

        totalPageText=new TextView(context);
        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParam.setMargins(25,0,5,0);
        totalPageText.setLayoutParams(layoutParam);
        totalPageText.setText("总页数");
        this.addView(totalPageText);
        curPageText=new TextView(context);
        layoutParam=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParam.setMargins(25,0,5,0);
        curPageText.setLayoutParams(layoutParam);
        curPageText.setText("当前页");


        this.addView(curPageText);
    }
    /**
     * 初始化分页组件的显示状态
     * @param newCount
     */
    public void initPageShow(int newCount){
        count=newCount;
        int totalPage=count%numPerPage==0?count/numPerPage:count/numPerPage+1;
        curPage=1;
        firstImg.setEnabled(false);
        preImg.setEnabled(false);
        if(totalPage<=1){
            endImg.setEnabled(false);
            nextImg.setEnabled(false);
        }else{
            endImg.setEnabled(true);
            nextImg.setEnabled(true);
        }
       totalCountText.setText("总数量"+count);
        totalPageText.setText("总页数 "+totalPage);
        curPageText.setText("当前页 "+curPage);
    }
    /**
     * 分页按钮被点击时更新状态,该方法要在initPageShow后调用
     */
    @Override
    public void onClick(View view) {

       /* if(pageChangeListener==null){
            return;
        }*/
        int totalPage=count%numPerPage==0?count/numPerPage:count/numPerPage+1;
        switch(Integer.parseInt(view.getTag().toString())){
            case 1:
                curPage=1;
                firstImg.setEnabled(false);
                preImg.setEnabled(false);
                if(totalPage>1){
                    nextImg.setEnabled(true);
                    endImg.setEnabled(true);
                }
                Log.d("btnClick", "onClick: <<");
                break;
            case 2:
                curPage--;
                if(curPage<=1){
                    firstImg.setEnabled(false);
                    preImg.setEnabled(false);
                }
                if(totalPage>1){
                    nextImg.setEnabled(true);
                    endImg.setEnabled(true);
                }
                Log.d("btnClick", "onClick: <");
                break;
            case 3:
                curPage++;
                if(curPage>=totalPage){
                    nextImg.setEnabled(false);
                    endImg.setEnabled(false);
                }
                firstImg.setEnabled(true);
                preImg.setEnabled(true);
                Log.d("btnClick", "onClick: >");
                break;
            case 4:
                curPage=totalPage;
                nextImg.setEnabled(false);
                endImg.setEnabled(false);
                firstImg.setEnabled(true);
                preImg.setEnabled(true);
                Log.d("btnClick", "onClick: >>");
                break;
            default:
                break;
        }
        totalPageText.setText("总页数 "+totalPage);
        curPageText.setText("当前页 "+curPage);
        pageChangeListener.pageChanged(curPage,numPerPage);
    }
    public OnPageChangeListener getPageChangeListener() {
        return pageChangeListener;
    }
    /**
     * 设置分页监听事件
     * @param pageChangeListener
     */
    public void setPageChangeListener(OnPageChangeListener pageChangeListener) {
        this.pageChangeListener = pageChangeListener;
    }
}
