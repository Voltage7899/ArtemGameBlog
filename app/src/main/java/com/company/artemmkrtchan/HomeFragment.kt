package com.company.artemmkrtchan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.artemmkrtchan.databinding.FragmentHomeBinding
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject


class HomeFragment : Fragment() {

    lateinit var binding:FragmentHomeBinding
    val blogPostList=ArrayList<mBlogPost>()
    val adapterPosts:RecAdapter= RecAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(inflater)

        binding.postRec.layoutManager=LinearLayoutManager(activity)
        binding.postRec.adapter=adapterPosts

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        firestore.collection("Posts").addSnapshotListener(object:EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                for(document in value!!.documentChanges){
                    if(document.type==DocumentChange.Type.ADDED){
                        var mBlogPost=document.document.toObject(mBlogPost::class.java)
                        blogPostList.add(mBlogPost)


                    }
                }
                adapterPosts.loadListToAdapter(blogPostList)

            }

        })

        return binding.root
    }


    companion object {


        fun newInstance() = HomeFragment()

    }
}
