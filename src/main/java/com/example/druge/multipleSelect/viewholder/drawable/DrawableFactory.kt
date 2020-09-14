package com.example.druge.multipleSelect.viewholder.color

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import com.example.druge.multipleSelect.MultipleAdapter
import com.example.druge.multipleSelect.viewholder.BaseViewHolder
import com.example.druge.multipleSelect.viewholder.DecorateFactory

/**
 * Created by gaoyufei on 2017/6/8.
 */

class DrawableFactory(val colorViewId:Int,
                                 val default:Drawable,
                                 val select:Drawable): DecorateFactory {

    override fun decorate(viewHolder: RecyclerView.ViewHolder, adapter: MultipleAdapter): BaseViewHolder {
        return DrawableViewHolder(viewHolder.itemView,viewHolder,adapter,colorViewId,default,select)
    }

}
