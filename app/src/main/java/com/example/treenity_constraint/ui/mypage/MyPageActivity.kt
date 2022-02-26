package com.example.treenity_constraint.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.treenity_constraint.R
import com.example.treenity_constraint.data.model.mypage.tree.Item
import com.example.treenity_constraint.data.repository.mypage.WalkLogRepository
import com.example.treenity_constraint.databinding.ItemMytreeRowBinding
import com.example.treenity_constraint.databinding.MypageMypageActivityMainBinding
import com.example.treenity_constraint.databinding.MypageMytreeAlertBinding
import com.example.treenity_constraint.databinding.SettingsActivityBinding
import com.example.treenity_constraint.di.MyPageNetworkModule
import com.example.treenity_constraint.ui.mypage.adapter.MyTreeRecyclerViewAdapter
import com.example.treenity_constraint.ui.mypage.viewmodel.MyTreeViewModel
import com.example.treenity_constraint.ui.mypage.viewmodel.UserViewModel
import com.example.treenity_constraint.ui.mypage.viewmodel.ViewModelFactory
import com.example.treenity_constraint.ui.mypage.viewmodel.WalkLogViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {

    // MyPage main
    private lateinit var binding: MypageMypageActivityMainBinding

    // User
    private val userViewModel: UserViewModel by viewModels()

    // My Tree
    private val myTreeViewModel: MyTreeViewModel by viewModels()
    private lateinit var myTreeRecyclerViewAdapter: MyTreeRecyclerViewAdapter

    // Walk Log
    var barEntries = ArrayList<BarEntry>()
    private lateinit var barDataSet: BarDataSet
    private lateinit var barData: BarData

    private lateinit var walkLogViewModel: WalkLogViewModel
    var walkLogIds = ArrayList<Float>()
    var walks = ArrayList<Float>()
    var dates = ArrayList<String>()
    var idAndDate: MutableMap<Float, String> = mutableMapOf()

    private lateinit var binding3: MypageMytreeAlertBinding
//    lateinit var alertDialog: AlertDialog 나중에 혹시 하자고 할까봐 남겨둠



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflate
        binding = MypageMypageActivityMainBinding.inflate(layoutInflater)
        binding3 = MypageMytreeAlertBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // User 데이터 로드
        getMyUserData()

        // WalkLog
        val walkLogRepository = WalkLogRepository(MyPageNetworkModule.provideRetrofitInstance())
        walkLogViewModel = ViewModelProvider(this, ViewModelFactory(walkLogRepository)).get(WalkLogViewModel::class.java)
        setBarChart()

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

        // 이벤트 등록 : 설정 아이콘 누르면 환경 설정 페이지로 전환
        binding.settings.setOnClickListener {
            val nextIntent = Intent(this@MyPageActivity, SettingsActivity::class.java)
            startActivity(nextIntent)
        }
    }


    private fun setBarChart() {

        // retrofit2 를 통해 data fetch
        walkLogViewModel.getWalkLog()

        walkLogViewModel.walkLogs.observe(this, {

            //get data from api and put them in barEntries/pieEntries(리스트 크기만큼 for loop 를 돌며 추가)
            val index = it.size - 1

            // walkLogId 와 walk 와 date 를 모두 따로따로 ArrayList 로 저장
            for(i in 0..index) {// x축
                walkLogIds.add(it[i].walkLogId.toFloat())
            }

            for(i in 0..index) { // y축
                walks.add(it[i].walks.toFloat())
            }

            for(i in 0..index) { // bar touch 하면 보여줄 값
                dates.add(it[i].date)
            }

            for(i in 0..index) // (id, date) 구조로 map 에 데이터 추가
                idAndDate[it[i].walkLogId.toFloat()] = it[i].date

            // BarEntry 에 데이터 삽입
            for(i in 0 until walks.size)
                barEntries.add(BarEntry(walkLogIds[i], walks[i]))

            // setting bar chart data
                // initialize bar data set
            barDataSet = BarDataSet(barEntries,"Walk Logs")

                //set colors
            barDataSet.color = ColorTemplate.rgb("#FF408F43") // 바 색상
            barDataSet.valueTextSize = 12f

            barData = BarData(barDataSet)
            barData.barWidth = 0.35f

                //binding 으로 접근하여 barData 전달
            binding.barChart.data = barData


            // prepare chart
            binding.barChart.run {
                data = barData
                setFitBars(true)

                description.isEnabled = false //차트 옆에 별도로 표기되는 description
                setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
                setScaleEnabled(false) // 확대 안되게 설정
                setDrawBarShadow(false) // 그래프의 그림자
                // 나중에 혹시 하자고 할까봐 남겨둠
//                axisLeft.run { // 왼쪽 축. 즉 Y방향 축을 뜻한다.
//                    granularity = 1000f // 1000 단위마다 선을 그리려고 granularity 설정 해 주었다.
//                    setDrawLabels(true) //
//                    setDrawGridLines(true) //격자 라인 활용
//                    setDrawAxisLine(false) // 축 그리기 설정
//
//                    textSize = 14f //라벨 텍스트 크기
//                }
                xAxis.run {
                    position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                    setDrawAxisLine(true) // 축 그림
                    setDrawGridLines(false) // 격자
                    textSize = 12f // 텍스트 크기
                    valueFormatter = object: ValueFormatter() { // MM/dd 형태로 날짜 모두 표시
                        override fun getFormattedValue(value: Float): String? {

                            return idAndDate[value]?.substring(5,10)?.replace("-", "/")
                        }
                    }
                }
                // 나중에 혹시 하자고 할까봐 남겨둠
//                setOnChartValueSelectedListener(object: OnChartValueSelectedListener { // 바를 touch 하면 생성된 날짜와 걸음 수를 보여주는 dialog 띄움
//
//                    override fun onValueSelected(e: Entry?, h: Highlight?) {
//
//                        val view = LayoutInflater.from(this@MyPageActivity).inflate(R.layout.mypage_mytree_alert, null)
//
//                        val date = view.findViewById<TextView>(R.id.date)
//                        val walk = view.findViewById<TextView>(R.id.walks)
//
//                        date.text = idAndDate[e!!.x]
//                        (e.y.toInt().toString() + " steps").also { walk.text = it }
//
//                        val builder = AlertDialog.Builder(this@MyPageActivity)
//                        builder.setView(view)
//
//                        alertDialog = builder.create()
//                        alertDialog.show()
//
//                    }
//
//                    override fun onNothingSelected() {}
//                })
                axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 설정
                axisLeft.isEnabled = false // 왼쪽 Y축을 안보이게 설정
                animateY(2000)
                legend.isEnabled = false //차트 범례 설정

                invalidate() // refresh
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