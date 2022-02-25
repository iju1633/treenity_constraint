package com.example.treenity_constraint.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.treenity_constraint.data.model.mypage.tree.Item
import com.example.treenity_constraint.data.repository.mypage.WalkLogRepository
import com.example.treenity_constraint.databinding.ItemMytreeRowBinding
import com.example.treenity_constraint.databinding.MypageMypageActivityMainBinding
import com.example.treenity_constraint.di.MyPageNetworkModule
import com.example.treenity_constraint.ui.mypage.adapter.MyTreeRecyclerViewAdapter
import com.example.treenity_constraint.ui.mypage.viewmodel.MyTreeViewModel
import com.example.treenity_constraint.ui.mypage.viewmodel.UserViewModel
import com.example.treenity_constraint.ui.mypage.viewmodel.ViewModelFactory
import com.example.treenity_constraint.ui.mypage.viewmodel.WalkLogViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {

    // MyPage main
    private lateinit var binding: MypageMypageActivityMainBinding

    // User
    private val userViewModel: UserViewModel by viewModels()

    // My Tree
    private lateinit var binding2: ItemMytreeRowBinding
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
    var idAndDate = mapOf<Float, String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflate
        binding = MypageMypageActivityMainBinding.inflate(layoutInflater)
        binding2 = ItemMytreeRowBinding.inflate(layoutInflater)

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
                idAndDate.plus(Pair(it[i].walkLogId.toFloat(), it[i].date))

            // BarEntry 에 데이터 삽입
            for(i in 0 until walks.size)
                barEntries.add(BarEntry(walkLogIds[i], walks[i]))

            // setting bar chart data
                // initialize bar data set
            barDataSet = BarDataSet(barEntries,"Walk Logs")

                //set colors
            barDataSet.color = ColorTemplate.rgb("#ff7b22")

            barData = BarData(barDataSet)
            barData.barWidth = 0.35f

                //binding 으로 접근하여 barData 전달
            binding.barChart.data = barData


            // prepare chart
            binding.barChart.run {
                setFitBars(true)

                description.isEnabled = false //차트 옆에 별도로 표기되는 description이다. false로 설정하여 안보이게 했다.
                isClickable = false
                setDrawValueAboveBar(true)
                setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 설정
                setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
                setScaleEnabled(false) // 확대 안되게 설정
                setDrawBarShadow(false)//그래프의 그림자
                setDrawGridBackground(false)//격자구조 넣을건지
                axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                    granularity = 1000f // 1000 단위마다 선을 그리려고 granularity 설정 해 주었다.
                    setDrawLabels(true) //
                    setDrawGridLines(true) //격자 라인 활용
                    setDrawAxisLine(false) // 축 그리기 설정

                    textSize = 14f //라벨 텍스트 크기
                }
                xAxis.run {
                    position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                    setDrawAxisLine(true) // 축 그림
                    setDrawGridLines(false) // 격자
                    textSize = 12f // 텍스트 크기
                    valueFormatter = object: ValueFormatter() { // 가장 최신 데이터의 날짜만을 x축에 표시
                        override fun getFormattedValue(value: Float): String? {
                            return if(value.equals(walkLogIds[walkLogIds.size-1])) dates[dates.size-1] else ""
                        }
                    }
                }
                axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
                //axisLeft.isEnabled = false
                animateY(1000)
                legend.isEnabled = false //차트 범례 설정

                invalidate()
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