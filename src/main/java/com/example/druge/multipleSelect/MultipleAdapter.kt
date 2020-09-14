package com.example.druge.multipleSelect

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.ViewGroup
import android.widget.Toast
import com.example.druge.multipleSelect.viewholder.BaseViewHolder
import com.example.druge.multipleSelect.viewholder.DecorateFactory
import com.example.druge.sqlite.SynUtils
import com.example.druge.tools.NetUtil
import com.example.druge.tools.UIHelper
import com.example.druge.ui.activity.HomeActivity
import com.example.druge.ui.activity.LocalhostDataCommitOrRollbackActivity
import com.example.druge.ui.dialog.ProgressDialog
import com.example.druge.ui.fragment.PickUpTaskListFragment
import com.goyourfly.multiple.adapter.menu.MenuController
import com.goyourfly.multiple.adapter.menu.MenuBar
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by gaoyufei on 2017/6/8.
 * 这是个Adapter的子类，使用装饰者模式
 * 对调用者的Adapter进行修改，增加多选
 * 功能
 */

class MultipleAdapter(val activity:Activity,val pickupTask:PickUpTaskListFragment?, val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                      val stateChangeListener: StateChangeListener?,
                      val popupToolbar: MenuBar?,
                      val ignoreType: Array<Int>?,
                      val list: MutableList<in Any>?,
                      val decorateFactory: DecorateFactory,
                      val duration: Long,
                       val type :Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), MenuController {

    var showState = ViewState.DEFAULT
    val selectIndex = SparseBooleanArray()
    var selectNum = 0
    var handler = Handler()
    var recyclerView:RecyclerView? = null

    init {
        popupToolbar?.initControl(this)
    }

    var run = Runnable {
        if (showState == ViewState.DEFAULT_TO_SELECT) {
            showState = ViewState.SELECT
        } else if (showState == ViewState.SELECT_TO_DEFAULT) {
            showState = ViewState.DEFAULT
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder !is BaseViewHolder) {
            adapter.onBindViewHolder(viewHolder, position)
            return
        }
        injectRecyclerView(viewHolder.viewHolder)
        /**
         * 先调用外界的绑定ViewHolder
         */
        adapter.bindViewHolder(viewHolder.viewHolder,position)
        adapter.onBindViewHolder(viewHolder.viewHolder, position)
        /**
         * 如果被忽略，则不往下走
         */
        if (isIgnore(position))
            return

        if (selectIndex.get(position)) {
            viewHolder.selectStateChanged(SelectState.SELECT)
        } else {
            viewHolder.selectStateChanged(SelectState.UN_SELECT)
        }

        viewHolder.showStateChanged(showState)
    }

    fun injectRecyclerView(childHolder:RecyclerView.ViewHolder){
        try {
            val child = RecyclerView.ViewHolder::class
            if(recyclerView != null) {
                val mOwnerRecyclerView = child.java.getDeclaredField("mOwnerRecyclerView")
                mOwnerRecyclerView.isAccessible = true
                mOwnerRecyclerView.set(childHolder, recyclerView)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val outerHolder = adapter.onCreateViewHolder(viewGroup, viewType)
        if(ignoreType != null && ignoreType.contains(viewType)){
            return outerHolder
        }
        return decorateFactory.decorate(outerHolder, this)
    }

    override fun getItemId(position: Int): Long {
        return adapter.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return adapter.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return adapter.itemCount
    }

    override fun selectAll() {
        var count = 0
        for (i in 0..itemCount - 1) {
            if (!isIgnore(i)) {
                selectIndex.put(i, true)
                count ++
            }
        }
        selectNum = count
        popupToolbar?.onUpdateTitle(selectNum, getTotal())
        notifyDataSetChanged()
    }

    override fun selectNothing() {
        for (i in 0..itemCount - 1) {
            selectIndex.put(i, false)
        }
        selectNum = 0
        popupToolbar?.onUpdateTitle(selectNum, getTotal())


        notifyDataSetChanged()
        if(activity is HomeActivity){
            var  homeActivity = activity as HomeActivity;
            homeActivity.hideButton(false)

        }
    }

    /**
     * 在选择模式中的点击才在这里处理
     * 正常模式的话，会传递给调用者的
     * adapter
     */
    fun onItemClick(position: Int) {
        if (isIgnore(position))
            return
        if (showState != ViewState.SELECT)
            return
        selectIndex.put(position, !selectIndex[position])
        selectNum += if (selectIndex[position]) 1 else -1
        popupToolbar?.onUpdateTitle(selectNum, getTotal())
        if (selectIndex[position]) {
            stateChangeListener?.onSelect(position, selectNum)
        } else {
            stateChangeListener?.onUnSelect(position, selectNum)
        }
        if (selectNum <= 0) {
            cancel()
        } else {
            notifyItemChanged(position)
        }
    }

    /**
     * Item长按
     */
    fun onItemLongClick(position: Int): Boolean {

        if(activity is HomeActivity){
            var  homeActivity = activity as HomeActivity;
            homeActivity.hideButton(true)

        }

        if (isIgnore(position))
            return false
        if (showState == ViewState.DEFAULT) {
            selectMode(false)
            selectIndex.put(position, true)
            selectNum = 1
            stateChangeListener?.onSelect(position, selectNum)
            popupToolbar?.onUpdateTitle(selectNum, getTotal())
        } else if (showState == ViewState.SELECT) {
            selectNum = 0
            cancel(false)
        }
        notifyDataSetChanged()
        return true
    }

    /**
     * 判断这个类型是否被忽略
     */
    fun isIgnore(position: Int): Boolean {
        if (ignoreType == null
                || ignoreType.isEmpty())
            return false
        val type = getItemViewType(position)
        return ignoreType.contains(type)
    }

    override fun refresh() {
        notifyDataSetChanged()
    }

    override fun selectMode(refresh: Boolean) {
        selectIndex.clear()
        showState = ViewState.DEFAULT_TO_SELECT
        popupToolbar?.show()
        popupToolbar?.onUpdateTitle(selectNum, getTotal())
        if (refresh)
            notifyDataSetChanged()
        stateChangeListener?.onSelectMode()
        handler.postDelayed(run, duration)
    }

    override fun getTotal(): Int {
        val count = (0 until itemCount).count { !isIgnore(it) }
        return count
    }

    override fun done(refresh: Boolean) {
        /*     Log.d("done","点击了")
             if (showState == ViewState.DEFAULT)
                 return
             showState = ViewState.SELECT_TO_DEFAULT
             popupToolbar?.dismiss()
             if (refresh)
                 notifyDataSetChanged()
             handler.postDelayed(run, duration)
             stateChangeListener?.onDone(getSelectIndex())
             selectIndex.clear()*/
        var dialog : ProgressDialog = ProgressDialog.createDialog(activity)


        if (showState == ViewState.DEFAULT)
            return
        showState = ViewState.SELECT_TO_DEFAULT
        popupToolbar?.dismiss()
        handler.postDelayed(run, duration)

        val select = getSelectIndex()

        val tempList : MutableList<in Any?>? =ArrayList()
        var i=0
        for(i in select){
            tempList?.add(list?.get(i))
        }
        val resultHandler : Handler = object : Handler(){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if(msg.data.getBoolean("result")){
                    Collections.reverse(select)
                    for (index in select) {
                        list?.removeAt(index)
                    }
                    stateChangeListener?.onDelete(getSelectIndex())
                    selectIndex.clear()
                    if (refresh)
                        notifyDataSetChanged()

                    UIHelper.ToastMessage(activity,"数据提交成功")
                    if(type==2 || type==3){
                        if(activity is LocalhostDataCommitOrRollbackActivity){
                            var localHostActivity = activity as LocalhostDataCommitOrRollbackActivity
                            localHostActivity.back()
                        }
                    }

                }else{
                    showState = ViewState.SELECT_TO_DEFAULT
                    popupToolbar?.dismiss()
                    if (refresh)
                        notifyDataSetChanged()
                    handler.postDelayed(run, duration)
                    selectIndex.clear()
                    stateChangeListener?.onCancel()
                    UIHelper.ToastMessage(activity,"数据提交失败")
                }
                //重新初始化数据
                pickupTask?.initTaskData()
                dialog.dismiss()
                if(activity is HomeActivity){
                    var  homeActivity = activity as HomeActivity;
                    homeActivity.hideButton(false)

                }
            }
        }
        if(NetUtil.isNetworkConnected(activity)) {


            dialog.show()
            Thread(Runnable
            {
                kotlin.run {
                    var msg: Message = resultHandler.obtainMessage()
                    var bundle: Bundle = Bundle()
                    when (type) {
                    //同步添加接收数据
                        0 -> {
                            bundle.putBoolean("result", SynUtils().addSynTaskInfo(activity, tempList))
                            msg.data = bundle
                            resultHandler.sendMessage(msg)
                        }
                    //同步提交接收数据
                        1 -> {
                            bundle.putBoolean("result", SynUtils().synCommitData(activity, tempList))
                            msg.data = bundle
                            resultHandler.sendMessage(msg)

                        }
                        2 -> {
                            bundle.putBoolean("result", SynUtils().synDelData(activity, tempList))
                            msg.data = bundle
                            resultHandler.sendMessage(msg)

                        }
                        else -> {

                        }


                    }


                }
            }).start()

            //判断数据是否同步
        }else{
            UIHelper.ToastMessage(activity,"暂无网络")
            showState = ViewState.SELECT_TO_DEFAULT
            popupToolbar?.dismiss()
            if (refresh)
                notifyDataSetChanged()
            handler.postDelayed(run, duration)
            selectIndex.clear()
            stateChangeListener?.onCancel()
            if(activity is HomeActivity){
                var  homeActivity = activity as HomeActivity;
                homeActivity.hideButton(false)

            }
        }


    }

    override fun delete(refresh: Boolean) {
        if (showState == ViewState.DEFAULT)
            return
        showState = ViewState.SELECT_TO_DEFAULT
        popupToolbar?.dismiss()
        handler.postDelayed(run, duration)

        val select = getSelectIndex()


        Collections.reverse(select)
        for (index in select) {
            list?.removeAt(index)
        }
        stateChangeListener?.onDelete(getSelectIndex())
        selectIndex.clear()
        if (refresh)
            notifyDataSetChanged()
    }

    override fun getSelect(): ArrayList<Int> {
        return getSelectIndex()
    }

    override fun cancel(refresh: Boolean): Boolean {


        if (showState == ViewState.DEFAULT)
            return false
        showState = ViewState.SELECT_TO_DEFAULT
        popupToolbar?.dismiss()
        if(activity is HomeActivity){
            var  homeActivity = activity as HomeActivity;
            homeActivity.hideButton(false)

        }
        if (refresh)
            notifyDataSetChanged()
        handler.postDelayed(run, duration)
        selectIndex.clear()
        stateChangeListener?.onCancel()
        return true
    }


    fun getSelectIndex(): ArrayList<Int> {
        val list = arrayListOf<Int>()
        for (i in 0 until itemCount) {
            if (selectIndex[i]) {
                list.add(i)
            }
        }
        return list
    }

    fun setData(data: Any) {
        if (list != null) {
            list.clear()
            list.add(data)
        } else {
            throw NullPointerException("Please call MultipleSelect.BindList before setData")
        }
    }

    fun setData(data: List<Any>) {
        if (list != null) {
            list.clear()
            list.addAll(data)
        } else {
            throw NullPointerException("Please call MultipleSelect.BindList before setData")
        }
    }

    fun addData(data: Any) {
        if (list != null) {
            list.add(data)
        } else {
            throw NullPointerException("Please call MultipleSelect.BindList before addData")
        }
    }

    fun addData(data: List<Any>) {
        if (list != null) {
            list.addAll(data)
        } else {
            throw NullPointerException("Please call MultipleSelect.BindList before addData")
        }
    }


    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
        adapter.setHasStableIds(hasStableIds)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        adapter.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder?): Boolean {
        super.onFailedToRecycleView(holder)
        return adapter.onFailedToRecycleView(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        adapter.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        adapter.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}
