package com.simbastudio.imtokrus.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.simbastudio.imtokrus.R
import com.simbastudio.imtokrus.adapter.PostAdapter
import com.simbastudio.imtokrus.model.Post


class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let{PostAdapter(it, postList as ArrayList<Post>)}
        recyclerView.adapter = postAdapter

        checkFollowings()

        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    (followingList as ArrayList<String>).clear()

                    for(snapshot in p0.children)
                    {
                        snapshot.key?.let {(followingList as ArrayList<String>).add(it)}
                    }
                    readPosts()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun readPosts() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addListenerForSingleValueEvent(object : ValueEventListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(p0: DataSnapshot) {

                (postList as ArrayList<Post>).clear()

                for (snapshot in p0.children) {
                    val post = snapshot.getValue(Post::class.java)
                    (postList as ArrayList<Post>).add(post!!)
                }
                (postList as ArrayList<Post>).reverse()
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }
}