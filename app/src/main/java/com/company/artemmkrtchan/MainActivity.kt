package com.company.artemmkrtchan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.company.artemmkrtchan.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {


    lateinit var binding:ActivityMainBinding
    val fireStore:FirebaseFirestore=FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_holder,HomeFragment.newInstance()).commit()
        setSupportActionBar(binding.toolbar)
        setTitle("Game")

        binding.floatingActionButton.setOnClickListener{

            startActivity(Intent(this,AddPost::class.java))

        }

        binding.bottomMainNavMenu.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.bottom_home->{
                    supportFragmentManager.beginTransaction().replace(R.id.main_fragment_holder,HomeFragment.newInstance()).commit()
                }
                R.id.bottom_account->{
                    supportFragmentManager.beginTransaction().replace(R.id.main_fragment_holder,AccountFragment.newInstance()).commit()
                }
            }
            true
        }

    }

    override fun onStart() {
        super.onStart()

        val currentUser=FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){

            fireStore.collection("Users").document(currentUser.uid).get()
                .addOnCompleteListener {

                    if(!it.result.exists()){
                        startActivity(Intent(this,AccountSettings::class.java))
                    }


                }.addOnFailureListener {
                    Toast.makeText(this,"Ошибка"+it.message,Toast.LENGTH_LONG).show()

                }



        }
        else{
            startActivity(Intent(this,Login::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_top_items,menu)



        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.account_setting_menu->{
                startActivity(Intent(this,AccountSettings::class.java))
            }
            R.id.log_out_menu->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,Login::class.java))
            }
        }



        return super.onOptionsItemSelected(item)

    }
}