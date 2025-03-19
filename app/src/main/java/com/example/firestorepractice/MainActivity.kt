package com.example.firestorepractice

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firestorepractice.databinding.ActivityMainBinding
import com.example.firestorepractice.databinding.StudentAddLayoutBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() , StudentAdaptor.StudentInterface{
    private lateinit var binding: ActivityMainBinding
    private var studentslist = ArrayList<StudentDetails>()
    private lateinit var adapter: StudentAdaptor
    private val db = Firebase.firestore
    private val collectionName = "Students"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = StudentAdaptor(studentslist, this)
        binding.recyclerId.layoutManager = LinearLayoutManager(this)
        binding.recyclerId.adapter = adapter

        binding.appBar.setOnClickListener {
            startActivity(Intent(this,SecondaryActivity::class.java))
        }
        binding.appBar1.setOnClickListener {
            startActivity(Intent(this,PaymentActivity::class.java))
        }

        db.collection(collectionName).addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            for (values in value!!.documentChanges) {
                val objectModel = convertObject(values.document)

                when (values.type) {
                    DocumentChange.Type.ADDED -> {
                        objectModel.let { studentslist.add(it) }
                        Log.e("", "objectModel: ${studentslist.size}")
                        Log.e("", "objectModel: $studentslist")
                    }

                    DocumentChange.Type.MODIFIED -> {
                        objectModel.let {
                            val index = getIndex(objectModel)
                            if (index > -1) {
                                studentslist.set(index, objectModel)
                            }
                        }

                    }

                    DocumentChange.Type.REMOVED -> {
                        objectModel.let {
                            val index = getIndex(objectModel)
                            if (index > -1) {
                                studentslist.removeAt(index)
                            }
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }


        binding.floatingAddBtn.setOnClickListener{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.student_add_layout)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.show()

            val inputRoll = dialog.findViewById<EditText>(R.id.add_roll)
            val inputData = dialog.findViewById<EditText>(R.id.add_name)
            val inputSub = dialog.findViewById<EditText>(R.id.add_subject)
            val addBtn = dialog.findViewById<Button>(R.id.add_btn)

            addBtn.setOnClickListener {
                if(inputRoll.editableText.toString().isEmpty() && !inputRoll.editableText.toString().isDigitsOnly()){
                    inputRoll.error="Enter Roll No"
                }
                else if(inputData.editableText.toString().isEmpty()){
                    inputData.error="Enter Name"
                }
                else if(inputSub.editableText.toString().isEmpty()){
                    inputSub.error="Enter Subject"
                }
                else {
                    db.collection(collectionName)
                        .add(
                            StudentDetails(
                                rollno = inputRoll.text.toString().toInt(),
                                name = inputData.text.toString(),
                                subject = inputSub.text.toString()
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                            adapter.notifyDataSetChanged()
                            dialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Adding failed", Toast.LENGTH_SHORT).show()
                        }
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }
    }

    fun getIndex(objectModel: StudentDetails): Int {
        var index =-1
        index = studentslist.indexOfFirst {
                element ->
            element.id?.equals(objectModel.id) == true
        }
        return index
    }

    fun convertObject(document: QueryDocumentSnapshot): StudentDetails {
        val fireData:StudentDetails= document.toObject(StudentDetails::class.java)
        fireData.id = document.id?:""
        return fireData
    }

    override fun onDelete(position: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Delete")
        dialog.setMessage("Are you sure you want to delete ?")
        dialog.setPositiveButton("Yes"){ _,_ ->
            studentslist[position].id?.let{
                db.collection(collectionName).document(it).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this,"Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Delete Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        dialog.setNegativeButton("No"){ _,_ ->
            dialog.create().dismiss()
        }
        dialog.create()
        dialog.show()
    }

    override fun onUpdate(position: Int) {
        val binding = StudentAddLayoutBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()

        binding.addBtn.setText("Update")
        binding.addRoll.setText(studentslist[position].rollno.toString())
        binding.addName.setText(studentslist[position].name)
        binding.addSubject.setText(studentslist[position].subject)

            binding.addBtn.setOnClickListener {
                if(binding.addRoll.editableText.toString().isEmpty()){
                    binding.addRoll.error="Enter Roll No"
                }
                else if(binding.addName.editableText.toString().isEmpty()){
                    binding.addName.error = "Enter Name"
                }
                else if(binding.addSubject.editableText.toString().isEmpty()){
                    binding.addSubject.error = "Enter Subject"
                }
                else{
                studentslist[position].id?.let {
                    db.collection(collectionName).document(it).set(StudentDetails(rollno = binding.addRoll.text.toString().toInt(), name = binding.addName.text.toString(), subject = binding.addSubject.text.toString()))
                        .addOnSuccessListener {
                            Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"Update Failed",Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
        }
    }

}