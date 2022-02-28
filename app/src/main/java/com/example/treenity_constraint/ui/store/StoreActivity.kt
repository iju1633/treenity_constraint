package com.example.treenity_constraint.ui.store

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.treenity_constraint.data.model.store.StoreItem
import com.example.treenity_constraint.databinding.StoreActivityMainBinding
import com.example.treenity_constraint.databinding.StoreConfirmationMainBinding
import com.example.treenity_constraint.ui.mypage.viewmodel.UserViewModel
import com.example.treenity_constraint.ui.store.adapter.StoreAdapter
import com.example.treenity_constraint.ui.store.viewmodel.SeedsViewModel
import com.example.treenity_constraint.ui.store.viewmodel.WaterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreActivity : AppCompatActivity() {
    lateinit var storeAdapter: StoreAdapter
    private lateinit var binding : StoreActivityMainBinding
    private lateinit var binding2 : StoreConfirmationMainBinding

    private val seedsViewModel: SeedsViewModel by viewModels()
    private val waterViewModel: WaterViewModel by viewModels()


    // User
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StoreActivityMainBinding.inflate(layoutInflater)
        binding2 = StoreConfirmationMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initRecyclerView()

        // 상점페이지의 seeds 연결
        seedsViewModel.responseSeeds.observe(this, { seedList ->
            storeAdapter.itemList = seedList
        })

        // 상점페이지의 water 연결
        waterViewModel.responseWater.observe(this, { water->
            binding.apply {
                (water[0].cost.toString() + "P").also { bucketPrice.text = it }
                bucketImage.load(water[0].imagePath)
            }
        })

        // point 와 buckets
        userViewModel.userResp.observe(this, { user->

            binding.apply {
                (user.point.toString() + "P").also { point.text = it }
                ("X " + user.buckets.toString()).also { bucket.text = it }
            }
        })

        // 이벤트 등록 : Seeds의 아이템을 누르면, 해당 아이템의 index 를 상세페이지로 전달해줌
        storeAdapter.setOnItemClickListener(object : StoreAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) { 

                val nextIntent = Intent(this@StoreActivity, ConfirmationActivity::class.java)
                nextIntent.putExtra("index", position) // item list 의 index 를 넘겨준다!
                startActivity(nextIntent)
            }
        })

        // 이벤트 등록 : Water 의 아이템을 누르면, 상세페이지로 화면 전환
        binding.water.setOnClickListener{

            val intent = Intent(this@StoreActivity, ConfirmationActivity::class.java)
            startActivity(intent)
        }

        // 유저가 보유한 나무 목록을 보여주는 페이지의 좌측 상단 뒤로가기 아이콘을 누르면 마이페이지 창으로 전환됨
        binding.gotoAr.setOnClickListener {
            // val intent = Intent(this, ArActivity::class.java)
            startActivity(intent)
        }

    }

    // adapter 부착
    private fun initRecyclerView() {
        // init adapter
        var item = StoreItem(0, "", "", 0, "")
        storeAdapter = StoreAdapter(listOf(item))

        binding.storeRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.storeRecycler.adapter = storeAdapter

    }
}