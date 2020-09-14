package com.example.druge.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.druge.R
import com.example.druge.entry.SynData.SynTemp
import com.example.druge.entry.TaskListInfo
import java.util.*
import javax.xml.transform.Source
import kotlin.collections.ArrayList

/**
 * Created by gaoyufei on 2017/6/8.
 */
class DemoSectionAdapter<T>  : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    public final  val TYPE_SECTION  = 1
    val TYPE_CONTENT  = 2

    val list = ArrayList<T>()

    /*init {
        for (i in 0..100) {
            addItem(Data("index:$i",if(i % 10 == 0) TYPE_SECTION else TYPE_CONTENT))
        }
    }*/


    fun removeItem(position: Int) {
        list.removeAt(position)
    }
    //获得 T object
    inline fun <reified T> instanceOf( t: T)  = T::class.java.newInstance()

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder?, p1: Int) {
      //  if (getItemViewType(p1) == TYPE_CONTENT) {
            val holder = p0 as MyViewHolder
            if( list.get(p1) is  TaskListInfo  ) {



                var task =  list.get(p1) as TaskListInfo
                holder.taskId.text =task.id.toString()
                holder.typeName.text = task.taskTypeName
                holder.taskName.text =task.content
                holder.createName.text = task.creatorName
                holder.statusName.text =task.statusName
            }else if(list.get(p1) is SynTemp){

                var task =  list.get(p1) as SynTemp
                holder.taskId.text =task.id.toString()
                holder.typeName.text = task.taskType
                holder.taskName.text =task.content
                holder.createName.text = task.opUser
                holder.statusName.text =task.statusName

            }
   //     }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
      /*  if(p1 == TYPE_SECTION){
            return SectionViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_section, p0, false))
        }else{*/
            return MyViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.fragment_task_list_item, p0, false))
       // }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

/*    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }*/


    data class Data(val str: String,val type:Int)

    override fun getItemCount(): Int {
        return list.size
    }

    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view)


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskId: TextView by lazy {
            view.findViewById<TextView>(R.id.tv_title_task_id) as TextView
        }
        val typeName: TextView by lazy {
            view.findViewById<TextView>(R.id.tv_title_task_type_name) as TextView
        }
        val taskName: TextView by lazy {
            view.findViewById<TextView>(R.id.tv_title_task_name) as TextView
        }
        val createName: TextView by lazy {
            view.findViewById<TextView>(R.id.tv_title_task_CreateName) as TextView
        }
        val statusName: TextView by lazy {
            view.findViewById<TextView>(R.id.tv_title_task_statusName) as TextView
        }

        init {
            view.setOnClickListener {
            //    Toast.makeText(view.context, "Hello", Toast.LENGTH_SHORT).show()
            }
         /*  val image =  view.findViewById<ImageView>(R.id.image_star) as ImageView
                  image  .setOnClickListener {
                        Toast.makeText(view.context, "Star", Toast.LENGTH_SHORT).show()
                    }*/
        }
    }
}
