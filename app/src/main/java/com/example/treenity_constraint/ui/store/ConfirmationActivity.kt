package com.example.treenity_constraint.ui.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.treenity_constraint.R
import com.example.treenity_constraint.data.model.store.PostItem
import com.example.treenity_constraint.databinding.StoreConfirmationMainBinding
import com.example.treenity_constraint.di.NetWorkModule
import com.example.treenity_constraint.ui.store.viewmodel.SeedsViewModel
import com.example.treenity_constraint.ui.store.viewmodel.WaterViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Response
import kotlin.properties.Delegates

@AndroidEntryPoint
class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding : StoreConfirmationMainBinding
    private val seedsViewModel: SeedsViewModel by viewModels()
    private val waterViewModel: WaterViewModel by viewModels()
    private var itemId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = StoreConfirmationMainBinding.inflate(layoutInflater)


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

        binding.gotoStore.setOnClickListener {
            val intent = Intent(this@ConfirmationActivity, StoreActivity::class.java)
            startActivity(intent)
        }

        binding.bringConfirmation.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setIcon(R.drawable.store_tree_icon)    // 제목 아이콘
            builder.setTitle("CONFIRMATION")    // 제목
            builder.setView(layoutInflater.inflate(R.layout.store_confirmation_dialog, null))

            // BUY 버튼 눌렀을 때 이벤트 -> POST 요청
            builder.setPositiveButton("BUY") { dialog, which ->
                // TODO : 여기에서 /users/{id}/items 로 post 요청할 것 : parameter -> itemId

                val apiInterface = NetWorkModule.provideRetrofitInstance()
                val call = apiInterface.pushTreeItem(itemId) // 여기에 buy 하는 item 의 id 를 넣어야 함
                
                // test
                Log.d("tag", "onCreate: id of your Item you just bought is $itemId")
                
                call.enqueue(object : retrofit2.Callback<PostItem> {
                    override fun onResponse(call: Call<PostItem>, response: Response<PostItem>) {
                        Log.d("tag", "onResponse: " + response.code())
//                        Log.d("tag", "onResponse: " + response.body()!!.itemName)
                    }

                    override fun onFailure(call: Call<PostItem>, t: Throwable) {
                        Log.d("tag", "onFailure: " + t.message)
                    }

                })

                // 상점페이지로 화면 전환
                val intent = Intent(this@ConfirmationActivity, StoreActivity::class.java)
                startActivity(intent)
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