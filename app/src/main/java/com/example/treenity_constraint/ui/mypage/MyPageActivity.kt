package com.example.treenity_constraint.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.treenity_constraint.data.model.mypage.tree.Item
import com.example.treenity_constraint.databinding.MypageActivityMainBinding
import com.example.treenity_constraint.databinding.TestBinding
import com.example.treenity_constraint.ui.mypage.adapter.MyTreeRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {

    // MyPage main
    private lateinit var binding: MypageActivityMainBinding

    // User
    private val userViewModel: UserViewModel by viewModels()

    // My Tree
    private lateinit var binding2: TestBinding
    private val myTreeViewModel: MyTreeViewModel by viewModels()
    private lateinit var myTreeRecyclerViewAdapter: MyTreeRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflate
        binding = MypageActivityMainBinding.inflate(layoutInflater)
        binding2 = TestBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // User 데이터 로드
        getMyUserData()

        // My Tree 데이터 로드 & 더보기 아이템 추가
        getMyTreeData()

        // MyTree 에 들어갈 recyclerview 에 어댑터 attach
        setViews()

        // 이벤트 등록 : 마지막 아이템을 누르면 나무 목록 리스트 페이지 전환
        myTreeRecyclerViewAdapter.setOnItemClickListener(object : MyTreeRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if(position == 3) { //
                    val nextIntent = Intent(this@MyPageActivity, TreeListActivity::class.java)
                    startActivity(nextIntent)
                }
            }
        })
    }

    private fun getMyUserData() {
        userViewModel.userResp.observe(this, { user->

            binding.apply {
                username.text = user.username
                point.text = user.point.toString()
                bucket.text = user.buckets.toString()
                totalWalk.text = user.totalWalks.toString()
            }
        })
    }

    private fun getMyTreeData() {
        myTreeViewModel.responseMyTree.observe(this, { listMyTrees ->
            myTreeRecyclerViewAdapter.trees = listMyTrees
        })


    }

    private fun setViews() {
        // init adapter
        var item = Item("", "")
        myTreeRecyclerViewAdapter = MyTreeRecyclerViewAdapter(listOf(item))

        // rv 에 myTreeRecyclerviewAdapter 붙이기
        binding.itemRecycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false)
        binding.itemRecycler.adapter = myTreeRecyclerViewAdapter
    }


}