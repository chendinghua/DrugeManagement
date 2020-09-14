package com.example.druge.multipleSelect.viewholder.color

import com.example.druge.multipleSelect.MultipleAdapter
import com.example.druge.multipleSelect.SelectState
import com.example.druge.multipleSelect.viewholder.BaseViewHolder

/**
 * Created by gaoyufei on 2017/6/8.
 */
class ColorViewHolder(view: android.view.View,
                      viewHolder: android.support.v7.widget.RecyclerView.ViewHolder,
                      adapter: MultipleAdapter,
                      val colorViewId: Int,
                      val defaultColor: Int,
                      val selectColor: Int) : BaseViewHolder(view, viewHolder, adapter) {


    val colorView: android.view.View by lazy {
        if (colorViewId == 0)
            view
        else
            itemView.findViewById(colorViewId)
    }


    override fun selectStateChanged(state: Int) {
        if (state == SelectState.UN_SELECT) {
            colorView.setBackgroundColor(defaultColor)
        } else if (state == SelectState.SELECT) {
            colorView.setBackgroundColor(selectColor)
        }
    }
}
