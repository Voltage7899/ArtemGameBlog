package com.company.artemmkrtchan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.company.artemmkrtchan.databinding.ActivityRegBinding
import com.google.firebase.auth.FirebaseAuth

class Reg : AppCompatActivity() {
    lateinit var binding:ActivityRegBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.RegReg.setOnClickListener {
            binding.progressBarReg.visibility= View.VISIBLE
            if (!binding.regEmail.text.toString().isEmpty() && !binding.regPass.text.toString().isEmpty() && !binding.regPassConfirm.text.toString().isEmpty()){

                if(binding.regPass.text.toString().equals(binding.regPassConfirm.text.toString())){

                    val mAuth = FirebaseAuth.getInstance()

                    mAuth.createUserWithEmailAndPassword(binding.regEmail.text.toString(),binding.regPass.text.toString()).addOnSuccessListener {

                        Toast.makeText(this,"Добро пожаловать", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this,MainActivity::class.java))

                    }.addOnFailureListener {
                        binding.progressBarReg.visibility= View.INVISIBLE
                        Toast.makeText(this,"Ошибка "+ it.message, Toast.LENGTH_LONG).show()
                    }


                }
                else{
                    binding.progressBarReg.visibility= View.INVISIBLE
                    Toast.makeText(this,"Пароли должны совпадать", Toast.LENGTH_LONG).show()
                }
            }
            else{
                binding.progressBarReg.visibility= View.INVISIBLE
                Toast.makeText(this,"Заполните все поля", Toast.LENGTH_LONG).show()
            }

        }
    }
}