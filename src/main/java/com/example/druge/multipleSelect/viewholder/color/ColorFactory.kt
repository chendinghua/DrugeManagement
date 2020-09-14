package com.example.druge.multipleSelect.viewholder.color

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import com.example.druge.multipleSelect.MultipleAdapter
import com.example.druge.multipleSelect.viewholder.BaseViewHolder
import com.example.druge.multipleSelect.viewholder.DecorateFactory

/**
 * Created by gaoyufei on 2017/6/8.
 */

class ColorFactory(val colorViewId:Int,
                   val defaultColor:Int,
                   val selectColor:Int): DecorateFactory {

    constructor() : this(0,Color.TRANSPARENT,Color.LTGRAY)

    override fun decorate(viewHolder: RecyclerView.ViewHolder, adapter: MultipleAdapter): BaseViewHolder {
        return ColorViewHolder(viewHolder.itemView,viewHolder,adapter,colorViewId,defaultColor,selectColor)
    }

}
