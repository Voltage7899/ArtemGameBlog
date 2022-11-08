package com.company.artemmkrtchan

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.company.artemmkrtchan.databinding.ActivityAddPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.ObjectInput
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import kotlin.collections.HashMap

class AddPost : AppCompatActivity() {
    var imageCurrentUri:Uri?=null
    lateinit var binding:ActivityAddPostBinding
    val storageReference:StorageReference= FirebaseStorage.getInstance().reference
    val firestore:FirebaseFirestore=FirebaseFirestore.getInstance()
    val currentUser=FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uCropContract= object: ActivityResultContract<List<Uri>, Uri>(){
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
        val cropImage=registerForActivityResult(uCropContract){ uri ->
            imageCurrentUri=uri
            binding.imageAdd.setImageURI(uri)

        }
        val getContent=registerForActivityResult(ActivityResultContracts.GetContent()){
                uri ->
            val inputUri=uri.toString().toUri()

            val outputUri= File(Environment.getExternalStorageDirectory(),"croppedImage.jpg").toUri()

            val listUri= listOf<Uri>(inputUri,outputUri)
            cropImage.launch(listUri)

        }

        binding.postAdd.setOnClickListener {

            if(!binding.descriptionAdd.text.isEmpty() && imageCurrentUri!=null){
                binding.progressBarAddPost.visibility= View.VISIBLE

                val currentDate=SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()).toString()
                val currentTime=LocalTime.now().toString()
                val filePath = storageReference.child("post_images").child(currentTime+currentDate+currentUser?.uid.toString()+".jgp")
                filePath.putFile(imageCurrentUri.toString().toUri()).addOnCompleteListener{

                    filePath.downloadUrl.addOnSuccessListener {

                        val downloadUri=it

                        val map=HashMap<String,String>()
                        map.put("postImageUrl",downloadUri.toString())
                        map.put("description",binding.descriptionAdd.text.toString())
                        map.put("userId",currentUser?.uid.toString())
                        map.put("dateTime",currentTime)
//                        map.put("userName",)
//                        map.put("dateTime",)



                        firestore.collection("Posts").add(map).addOnCompleteListener {
                            binding.progressBarAddPost.visibility= View.INVISIBLE
                            Toast.makeText(this,"Пост добавлен", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this,MainActivity::class.java))

                        }.addOnFailureListener {
                            binding.progressBarAddPost.visibility= View.INVISIBLE
                            Toast.makeText(this,"Ошибка " + it.message, Toast.LENGTH_LONG).show()

                        }

                    }

                }

            }
            else{

                Toast.makeText(this,"Введите описание и картинку", Toast.LENGTH_LONG).show()

            }

        }

        binding.imageAdd.setOnClickListener {
            getContent.launch("image/*")
        }
    }
}