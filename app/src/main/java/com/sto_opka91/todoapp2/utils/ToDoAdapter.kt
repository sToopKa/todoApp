package com.sto_opka91.todoapp2.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import com.sto_opka91.todoapp2.R
import com.sto_opka91.todoapp2.databinding.EachTodoItemBinding

class ToDoAdapter(private val list: MutableList<ToDoData>):RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {
    private var listener:ToDoAdapterClicksInterface? = null
    fun setListener(listener: ToDoAdapterClicksInterface){
        this.listener = listener
    }
    inner class ToDoViewHolder(val binding:EachTodoItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text = this.task
                if(this.date !=""){
                    binding.etData.visibility = View.VISIBLE
                    binding.etData.text = this.date
                }else{
                    binding.etData.visibility = View.GONE
                    binding.tvDate.setText(R.string.date_not_set)
                }
                binding.deleteTask.setOnClickListener{
                    listener?.onDeleteBtnClick(this)
                }
                binding.editTask.setOnClickListener{
                    listener?.onEditBtnClick(this)
                }
            }
        }
    }
    interface ToDoAdapterClicksInterface{
        fun onDeleteBtnClick(toDoData: ToDoData)
        fun onEditBtnClick(toDoData: ToDoData)
    }
}