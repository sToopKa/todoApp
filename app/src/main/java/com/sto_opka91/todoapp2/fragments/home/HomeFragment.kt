package com.sto_opka91.todoapp.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.sto_opka91.todoapp.fragments.dialog.AddToDoFragment

import com.sto_opka91.todoapp2.databinding.FragmentHomeBinding
import com.sto_opka91.todoapp2.utils.AlarmReceiver
import com.sto_opka91.todoapp2.utils.SaveToDoData
import com.sto_opka91.todoapp2.utils.ToDoAdapter
import com.sto_opka91.todoapp2.utils.ToDoData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class HomeFragment : Fragment(), AddToDoFragment.DialogNextBtnClickListener,
    ToDoAdapter.ToDoAdapterClicksInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var databasesRef: DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private  var popUpFragment: AddToDoFragment?= null
    private lateinit var adapter: ToDoAdapter
    private lateinit var mList: MutableList<ToDoData>
    private lateinit var alarmManager: AlarmManager
    private lateinit  var pendingIntent: PendingIntent
    private lateinit var pLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvents()
        createNotificationChannel()
        registerPermissionListener()
        checkPostPermission()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPostPermission(){
        when{
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    ==PackageManager.PERMISSION_GRANTED ->{

                    }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->{
                    Toast.makeText(context, "We need you permission", Toast.LENGTH_SHORT).show()
                }
            else ->{
                pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private fun registerPermissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it) {

                Toast.makeText(context, "Notification!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createNotificationChannel() {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){

            val name:CharSequence = "ToDoReminderChannel"
            val description = "Channel for alarmManager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("todoApp",name, importance)
            channel.description = description
            val notificationManager: NotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun getDataFromFirebase() {
        databasesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for(taskSnapshot in snapshot.children){
                    val todoTask = taskSnapshot.key?.let{
                        ToDoData(it, taskSnapshot.child("toDo").value.toString(),taskSnapshot.child("date").value.toString() )
                    }
                    if(todoTask!=null){
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun registerEvents() {
        binding.tvAdd.setOnClickListener{
            if(popUpFragment!=null)
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = AddToDoFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                AddToDoFragment.TAG
            )
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databasesRef = FirebaseDatabase
            .getInstance()
            .reference
            .child("Tasks")
            .child(auth.currentUser?.uid.toString())
        binding.rvToDo.setHasFixedSize(true)
        binding.rvToDo.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.rvToDo.adapter = adapter
    }

    override fun onSaveTask(toDo: String, toDoEt: TextInputEditText, date: String) {
        val saveTask = SaveToDoData(toDo,date)
        val taskId = databasesRef.push().key
        databasesRef
            .push()
            .setValue(saveTask)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Todo saved successfully", Toast.LENGTH_SHORT).show()
                setAlarm(date, toDo, taskId)
                toDoEt.text = null
            } else {
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popUpFragment!!.dismissAllowingStateLoss()
        }.addOnFailureListener { e ->
            Log.e("myLog", "Failure saving todo", e)
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun changeDateString(date:String):Date{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val dateCalendar: Date = sdf.parse(date)!!
        return dateCalendar
    }

    private fun setAlarm(date:String, todo:String, taskId: String?) {

        if(date!=""){
            alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            pendingIntent = Intent(context, AlarmReceiver::class.java).let{intent ->
                  intent.putExtra("todo",todo)
                intent.putExtra("taskId", taskId)
                Log.d( "myLog", "до  "+ todo)
                Log.d( "myLog", "до  "+ taskId)
                PendingIntent.getBroadcast(context,taskId.hashCode(),intent,PendingIntent.FLAG_IMMUTABLE)
            }
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = changeDateString(date)
            alarmManager.set(
               AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

        }
    }

    override fun onUpdateTask(toDoData: ToDoData, toDoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databasesRef.updateChildren(map).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                toDoEt.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popUpFragment!!.dismiss()
        }
    }

    override fun onDeleteBtnClick(toDoData: ToDoData) {
        databasesRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditBtnClick(toDoData: ToDoData) {
        if(popUpFragment!=null){
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = AddToDoFragment.newInstance(toDoData.taskId, toDoData.task)
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                AddToDoFragment.TAG
            )
        }

    }


}