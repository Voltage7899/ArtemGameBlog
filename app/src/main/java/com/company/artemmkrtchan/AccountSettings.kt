package com.company.artemmkrtchan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

import com.company.artemmkrtchan.databinding.ActivityAccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import java.io.File


class AccountSettings : AppCompatActivity() {

    val storageRef:StorageReference= FirebaseStorage.getInstance().reference
    val mAuth:FirebaseAuth=FirebaseAuth.getInstance()
    val firebaseFireStore:FirebaseFirestore=FirebaseFirestore.getInstance()
    var imageCurrentUri:Uri?=null
    var downloadUri:Uri?=null
    val viewModel:viewModel by viewModels()
    lateinit var binding:ActivityAccountSettingsBinding
    val userId=FirebaseAuth.getInstance().currentUser?.uid
    val uCropContract= object:ActivityResultContract<List<Uri>,Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri=input[0]
            val outputUri=input[1]

            val uCrop= UCrop.of(inputUri,outputUri)
                .withAspectRatio(5f,5f)
                .withMaxResultSize(800,800)

            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return  UCrop.getOutput(intent!!)!!
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentData(userId)

        binding.imageSettings.setImageResource(R.drawable.default_image)

        val cropImage=registerForActivityResult(uCropContract){ uri ->
            imageCurrentUri=uri
            binding.imageSettings.setImageURI(uri)

        }

        binding.saveSettings.setOnClickListener {

            if(!binding.userNameSettings.text.isEmpty() && imageCurrentUri!=null){

                binding.progressBarSettings.visibility= View.VISIBLE


                var imagePath=storageRef.child("profile_images").child(userId.toString()+".jpg")


                imagePath.putFile(imageCurrentUri.toString().toUri()).addOnCompleteListener{

                    imagePath.downloadUrl.addOnSuccessListener {


                        downloadUri=it
                        Toast.makeText(this,"Картинка загружена в хранилище картинок "+ downloadUri.toString(),Toast.LENGTH_LONG).show()
                        viewModel.liveDataCurrentUser.value=downloadUri
                    }



                    viewModel.liveDataCurrentUser.observe(this, Observer {
                        val user=mUser(binding.userNameSettings.text.toString(),it.toString())

                        firebaseFireStore.collection("Users").document(userId.toString())
                            .set(user).addOnCompleteListener{

                                binding.progressBarSettings.visibility= View.INVISIBLE
                                Toast.makeText(this,"Данные пользователя успешно загружены в бд ",Toast.LENGTH_LONG).show()
                                startActivity(Intent(this,MainActivity::class.java))

                            }.addOnFailureListener {

                                Toast.makeText(this,"Ошибка при загрузке в базу данных "+ it.message,Toast.LENGTH_LONG).show()

                            }
                    })




                }.addOnFailureListener{
                    binding.progressBarSettings.visibility= View.INVISIBLE
                    Toast.makeText(this,"Ошибка "+it.message,Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this,"Ошибка,введите имя и загрузите картинку ",Toast.LENGTH_LONG).show()
            }

        }

        val getContent=registerForActivityResult(ActivityResultContracts.GetContent()){
            uri ->
            val inputUri=uri.toString().toUri()

            val outputUri= File(android.os.Environment.getExternalStorageDirectory(),"croppedImage.jpg").toUri()

            val listUri= listOf<Uri>(inputUri,outputUri)
            cropImage.launch(listUri)

        }





        binding.imageSettings.setOnClickListener{

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                getContent.launch("image/*")

            }
            else{
                Toast.makeText(this,"Нет разрешения", Toast.LENGTH_LONG).show()

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),1)

            }

        }

    }
    fun getCurrentData(uId:String?){

        firebaseFireStore.collection("Users").document(uId.toString()).get().addOnCompleteListener {

            if(it.result.exists()){

                val name=it.result.getString("name")
                val image=it.result.getString("image")

                binding.userNameSettings.setText(name)


                Picasso.get().load(image).into(binding.imageSettings)


            }
            else{

            }

        }.addOnFailureListener {

            Toast.makeText(this,"Ошибка получения данных "+it.message,Toast.LENGTH_LONG).show()

        }

    }

}