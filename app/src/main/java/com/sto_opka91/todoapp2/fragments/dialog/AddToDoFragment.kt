package com.sto_opka91.todoapp.fragments.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.sto_opka91.todoapp2.databinding.FragmentAddToDoBinding
import com.sto_opka91.todoapp2.fragments.dialog.TimePickerHelper
import com.sto_opka91.todoapp2.utils.ToDoData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddToDoFragment : DialogFragment(){
private lateinit var binding: FragmentAddToDoBinding
private lateinit var listener: DialogNextBtnClickListener
private var toDoData: ToDoData? = null
private  var calendar: Calendar
private lateinit var timePickerHelper: TimePickerHelper
init {
    calendar = Calendar.getInstance()
}

fun setListener(listener:DialogNextBtnClickListener)
{
    this.listener = listener
}
    companion object{
        const  val TAG = "AddToDoPoPupFragment"
        @JvmStatic
        fun newInstance(taskId:String, task: String)= AddToDoFragment().apply{
            arguments = Bundle().apply{
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddToDoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments!=null){
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )
            binding.todoEt.setText(toDoData?.task)
        }
        registerEvents()
    }

    private fun registerEvents() {
        binding.todoNextBtn.setOnClickListener {
            val toDoTask = binding.todoEt.text.toString()
            val date = binding.editTextDate.text.toString()
            if(toDoTask.isNotEmpty()){
                if(toDoData==null){
                    listener.onSaveTask(toDoTask, binding.todoEt, date)
                }else{
                    toDoData?.task = toDoTask
                    listener.onUpdateTask(toDoData!!, binding.todoEt)
                }

            }else{
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.todoClose.setOnClickListener {
            dismiss()
        }
        binding.ivPickTime.setOnClickListener {
            timePickerHelper = TimePickerHelper(requireContext()) { selectedDateTimeInMillis ->
                calendar.timeInMillis = selectedDateTimeInMillis
                updateDateTimeEditText()
            }
            timePickerHelper.showDateTimePicker()
        }
    }

    private fun updateDateTimeEditText() {
        val formattedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)
        binding.editTextDate.setText(formattedDateTime)
    }

    interface DialogNextBtnClickListener{
        fun onSaveTask(toDo: String, toDoEt: TextInputEditText, date: String)
        fun onUpdateTask(toDoData: ToDoData, toDoEt: TextInputEditText)
    }
}
