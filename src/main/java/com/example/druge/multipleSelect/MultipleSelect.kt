package com.example.druge.multipleSelect

import android.app.Activity
import android.support.v7.widget.RecyclerView
import com.example.druge.R
import com.example.druge.multipleSelect.viewholder.DecorateFactory
import com.example.druge.multipleSelect.viewholder.color.ColorFactory
import com.example.druge.ui.fragment.PickUpTaskListFragment
import com.goyourfly.multiple.adapter.menu.SimpleDoneMenuBar
import com.goyourfly.multiple.adapter.menu.MenuBar

/**
 * Created by gaoyufei on 2017/6/8.
 */
object MultipleSelect {

    @JvmStatic
    fun with(activity: Activity): Builder {
        return Builder(activity)
    }


    open class Builder(val activity: Activity) {

        /**
         * 用户的Adapter
         */
        var adapter: RecyclerView.Adapter<in RecyclerView.ViewHolder>? = null


        var  pickupTask: PickUpTaskListFragment?=null
        /**
         * 选择中风格，默认给出两种风格
         */
        private var decorateFactory: DecorateFactory? = null


        private var customMenu: MenuBar? = null

        private var type   =0

        /**
         * 状态回调
         */
        private var stateChangeListener: StateChangeListener? = null

        private var list: MutableList<out Any>? = null

        private var ignoreType: Array<Int>? = null
        /**
         * 动画时长
         */
        var duration = 300L

        fun fragment( pickupTask:PickUpTaskListFragment):Builder{

            this.pickupTask=pickupTask
            return this
        }

        fun adapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>): Builder {
            this.adapter = adapter as RecyclerView.Adapter<in RecyclerView.ViewHolder>
            return this
        }

        fun decorateFactory(decorateFactory: DecorateFactory): Builder {
            this.decorateFactory = decorateFactory
            return this
        }

        fun linkList(list: MutableList<out Any>): Builder {
            this.list = list
            return this
        }

        fun ignoreViewType(ignore: Array<Int>): Builder {
            this.ignoreType = ignore
            return this
        }

        fun customMenu(menuBar: MenuBar): Builder {
            this.customMenu = menuBar
            return this
        }

        fun stateChangeListener(listener: StateChangeListener): Builder {
            this.stateChangeListener = listener
            return this
        }

        fun setType(type : Int): Builder {
            this.type = type
            return this
        }

        fun build(): MultipleAdapter {
            if (adapter == null)
                throw NullPointerException("You must specific the adapter")

            if (decorateFactory == null) {
                decorateFactory = ColorFactory()
            }
            adapter
            if (customMenu == null) {
                customMenu = SimpleDoneMenuBar(activity, activity.resources.getColor(R.color.colorPrimary))
            }
            val linkList = if (list == null) null else list as MutableList<in Any>
            return MultipleAdapter(activity,pickupTask,adapter!!, stateChangeListener, customMenu, ignoreType, linkList, decorateFactory!!, duration,type)
        }

    }
}
