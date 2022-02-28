package com.example.treenity_constraint.ui.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.treenity_constraint.R
import com.example.treenity_constraint.databinding.StoreConfirmationMainBinding
import com.example.treenity_constraint.ui.store.viewmodel.SeedsViewModel
import com.example.treenity_constraint.ui.store.viewmodel.WaterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding : StoreConfirmationMainBinding
    private val seedsViewModel: SeedsViewModel by viewModels()
    private val waterViewModel: WaterViewModel by viewModels()


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
                }
            }
            else { // seed 를 누르지 않았다면, 즉, bucket 을 선택했다면
                waterViewModel.responseWater.observe(this, { water ->
                    binding.apply {
                        itemImage.load(water[0].imagePath)
                        seedName.text = water[0].name
                        description.text = water[0].description
                        (water[0].cost.toString() + "P").also { cost.text = it }
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

            // BUY 버튼 눌렀을 때 이벤트
            builder.setPositiveButton("BUY") { dialog, which ->
                // TODO : 여기에서 /users/{id}/items 로 post 요청할 것 : parameter -> itemId
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