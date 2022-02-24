package com.example.treenity_constraint.ui.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treenity_constraint.data.model.mypage.walklog.WalkLog
import com.example.treenity_constraint.data.repository.mypage.WalkLogRepository
import kotlinx.coroutines.*


class WalkLogViewModel constructor(private val repository: WalkLogRepository): ViewModel() {

    var walkLogs = MutableLiveData<List<WalkLog>>()
    var walkLogList = ArrayList<WalkLog>()
    var job: Job? = null
    var item = WalkLog("",0,0)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("tag","Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

//    init {
//        getWalkLog()
//    }

//    private fun getWalkLog() = viewModelScope.launch {
//        repository.getWalkLogs().let { response ->
//
//            val walkLogs = ArrayList<WalkLog>()
//
//            if (response.isSuccessful) {
//
//                for(i in 7 downTo 1) // Walk Log 중 최근 것 7개만 가져옴(일주일 고려)
//                    walkLogs.add(response.body()!![response.body()!!.size - i])
//
//                _resp.postValue(walkLogs)
//            } else {
//                Log.d("tag", "getWalkLogData Error: ${response.message()}")
//            }
//        }
//    }

    fun getWalkLog() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repository.getWalkLogs()
            withContext(Dispatchers.Main) {


                if (response.isSuccessful) {

                    // Walk Log 중 최근 것 7개만 가져옴(일주일 고려)
                    for(i in 7 downTo 1) {
                        item = response.body()!![response.body()!!.size - i]
                        walkLogList.add(item)
                        // test
                        Log.d("tag", "getWalkLog : $item")

                    }

                    walkLogs.postValue(walkLogList)

                } else {
                    Log.d("tag","Error : ${response.message()} ")
                }
            }
        }
    }
//    fun getLiveDataObserver(): MutableLiveData<List<WalkLog>> {
//        return liveDataList
//    }
//
//    fun makeAPICall() {
//        val retroInstance = MyPageNetworkModule.provideRetrofitInstance()
//        val call = retroInstance.getWalkLogs()
//        call.enqueue(object : Callback<List<WalkLog>>{
//            override fun onResponse(call: Call<List<WalkLog>>, response: Response<List<WalkLog>>) {
//
//                var walkLogs = ArrayList<WalkLog>()
//
//                for(i in 7 downTo 1) // Walk Log 중 최근 것 7개만 가져옴
//                    walkLogs.add(response.body()!![response.body()!!.size - i])
//
//                liveDataList.postValue(walkLogs)
//            }
//
//            override fun onFailure(call: Call<List<WalkLog>>, t: Throwable) {
//                liveDataList.postValue(null)
//            }
//
//
//        })
//    }
}