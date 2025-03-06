package com.example.firestorepractice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firestorepractice.databinding.StudentListLayoutBinding

class StudentAdaptor(var list: ArrayList<StudentDetails>,var studentInterface: StudentInterface) :RecyclerView.Adapter<StudentAdaptor.Viewholder>() {
    inner class Viewholder(var binding: StudentListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(studentDetails: Int){
                binding.studentRollno.text = list[studentDetails].rollno.toString()
                binding.studentName.text = list[studentDetails].name
                binding.studentSubject.text = list[studentDetails].subject
                binding.studentDelete.setOnClickListener {
                    studentInterface.onDelete(studentDetails)
                }
                binding.studentUpdate.setOnClickListener {
                    studentInterface.onUpdate(studentDetails)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_list_layout, parent, false)
        return Viewholder(StudentListLayoutBinding.bind(view))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.bind(position)
    }

    interface StudentInterface{
        fun onDelete(position: Int)
        fun onUpdate(position: Int)
    }
}