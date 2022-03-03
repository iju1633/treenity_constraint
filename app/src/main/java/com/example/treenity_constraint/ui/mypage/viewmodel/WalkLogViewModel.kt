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
}