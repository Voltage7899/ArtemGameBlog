package com.company.artemmkrtchan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.artemmkrtchan.databinding.ElementBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class RecAdapter : RecyclerView.Adapter<RecAdapter.ViewHolder>() {



    private var ListInAdapter = ArrayList<mBlogPost>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecAdapter.ViewHolder, position: Int) {
        holder.bind(ListInAdapter[position])
    }

    override fun getItemCount(): Int {
        return ListInAdapter.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ElementBinding.bind(itemView)
        var userName:String=""
        var urlProfileImage:String=""
        fun bind(post: mBlogPost) {
            getUsersPostData(post.userId.toString())
            binding.elDate.text = post.dateTime
            binding.elDescription.text = post.description
            Picasso.get().load(post.postImageUrl).fit().into(binding.elImagePost)




        }
        fun getUsersPostData(userId:String){
            var user:mUser?=null
            val firestore=FirebaseFirestore.getInstance()
            firestore.collection("Users").document(userId).get().addOnCompleteListener {
                user=it.result.toObject(mUser::class.java)
                binding.elUserName.text = user?.name
                Picasso.get().load(user?.image).fit().into(binding.elPostUserImage)

            }
        }

    }

    fun loadListToAdapter(productList: ArrayList<mBlogPost>) {
        ListInAdapter = productList
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onClick(kino: mBlogPost) {

        }
    }

    fun deleteItem(i: Int): String? {
        var id = ListInAdapter.get(i).userId

        ListInAdapter.removeAt(i)

        notifyDataSetChanged()

        return id
    }

}