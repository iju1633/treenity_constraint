package com.example.treenity_constraint.ui.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.treenity_constraint.R
import com.example.treenity_constraint.data.model.store.StoreItem
import com.example.treenity_constraint.databinding.StoreActivityMainBinding
import com.example.treenity_constraint.databinding.StoreConfirmationMainBinding
import com.example.treenity_constraint.di.NetWorkModule
import com.example.treenity_constraint.ui.mypage.viewmodel.UserViewModel
import com.example.treenity_constraint.ui.store.viewmodel.SeedsViewModel
import com.example.treenity_constraint.ui.store.viewmodel.WaterViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response
import kotlin.properties.Delegates

@AndroidEntryPoint
class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding : StoreConfirmationMainBinding
    private lateinit var binding2 : StoreActivityMainBinding
    private val seedsViewModel: SeedsViewModel by viewModels()
    private val waterViewModel: WaterViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private var itemId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StoreConfirmationMainBinding.inflate(layoutInflater)
        binding2 = StoreActivityMainBinding.inflate(layoutInflater)


        // 상세페이지의 component 와 연결
        seedsViewModel.responseSeeds.observe(this, { seedList ->
            var secondIntent = intent
            val index = secondIntent.getIntExtra("index", seedList.size) // index 는 seedList 의 size 를 초과할 수 없음을 이용

            if(index != seedList.size) {
                binding.apply {
                    itemImage.load(seedList[index].imagePath)
                    seedName.text = seedList[index].name
                    description.text = seedList[index].description
                    (seedList[index].cost.toString() + "P").also { cost.text = it }

                    itemId = seedList[index].itemId
                }
            }
            else { // seed 를 누르지 않았다면, 즉, bucket 을 선택했다면
                waterViewModel.responseWater.observe(this, { water ->
                    binding.apply {
                        itemImage.load(water[0].imagePath)
                        seedName.text = water[0].name
                        description.text = water[0].description
                        (water[0].cost.toString() + "P").also { cost.text = it }

                        itemId = water[0].itemId // 후에 POST 요청을 위해 필요한 부분
                    }
                })
            }
        })

        setContentView(binding.root)

//        binding.gotoStore.setOnClickListener { // 뒤로가기 버튼
//            val intent = Intent(this@ConfirmationActivity, StoreActivity::class.java)
//            startActivity(intent)
//        }


        binding.bringConfirmation.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setIcon(R.drawable.store_tree_icon)    // 제목 아이콘
            builder.setTitle("CONFIRMATION")    // 제목
            builder.setView(layoutInflater.inflate(R.layout.store_confirmation_dialog, null))

            // BUY 버튼 눌렀을 때 이벤트 -> POST 요청
            builder.setPositiveButton("BUY") { dialog, which ->

                val apiInterface = NetWorkModule.provideRetrofitInstance()
                val item = StoreItem(0,"", "", itemId, "") // 구매한 item 의 id 를 넣어 객체 생성
                val call = apiInterface.pushTreeItem(item) // body 로 전달
                
                // test
                Log.d("tag", "onCreate: id of your Item you just bought is $itemId")
                
                call.enqueue(object : retrofit2.Callback<StoreItem> {
                    override fun onResponse(call: Call<StoreItem>, response: Response<StoreItem>) {
                        Log.d("tag", "onResponse: " + response.code()) // TODO 서버 에러 500이 뜨는 경우가 있고, 물을 하루에 3번 이상 살 시, 어떤 에러코드를 response 하는 지 물어볼 것
//                        Toast.makeText(this@ConfirmationActivity, "Successfully purchased", Toast.LENGTH_SHORT).show()
//                        if(response.code() == 500)
//                            Toast.makeText(this@ConfirmationActivity, "You can only buy upto 3 per day", Toast.LENGTH_SHORT).show()
                        
                        // point 와 bucket 데이터 갱신
                        userViewModel.userResp.observe(this@ConfirmationActivity, { user->

                            binding2.apply {
                                (user.point.toString() + "P").also { point.text = it }
                                ("X " + user.buckets.toString()).also { bucket.text = it }
                            }
                        })
                    }

                    override fun onFailure(call: Call<StoreItem>, t: Throwable) {
                        Log.d("tag", "onFailure: " + t.message)
                        finish()
                    }

                })

                // 상점페이지로 화면 전환
                val intent = Intent(this@ConfirmationActivity, StoreActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Return 버튼 추가
            builder.setNegativeButton("RETURN") { dialog, which ->
                // 아무 처리 안하면 상세페이지로 복귀
            }

            // 뒤로 가기 or 바깥 부분 터치
            builder.setOnCancelListener {
                // 아무 처리 안하면 상세페이지로 복귀
            }

            builder.show()
        }
    }
}