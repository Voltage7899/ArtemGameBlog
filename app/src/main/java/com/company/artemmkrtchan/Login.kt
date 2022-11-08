package com.company.artemmkrtchan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.company.artemmkrtchan.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val mAuth: FirebaseAuth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sign.setOnClickListener {


            if(!binding.loginEmail.text.toString().isEmpty() && !binding.loginPass.text.toString().isEmpty()){
                binding.progressBar.visibility= View.VISIBLE

                mAuth.signInWithEmailAndPassword(binding.loginEmail.text.toString(),binding.loginPass.text.toString()).addOnSuccessListener {


                    Toast.makeText(this,"Добро пожаловать", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,MainActivity::class.java))

                }.addOnFailureListener {
                    Toast.makeText(this,"Ошибка"+it.message,Toast.LENGTH_LONG).show()
                }

            }
            else{
                Toast.makeText(this,"Введите все поля",Toast.LENGTH_LONG).show()
            }
        }


        binding.Reg.setOnClickListener {
            startActivity(Intent(this,Reg::class.java))
        }
    }
}