package com.example.swith.ui.region

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swith.R
import com.example.swith.data.CityResponse
import com.example.swith.databinding.ActivityGuBinding
import com.example.swith.repository.ApiService
import com.example.swith.repository.RetrofitService
import com.example.swith.ui.adapter.RegionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GuActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityGuBinding
    var regionCode:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gu)

        initData()
        initView()

    }

    private fun initView() {
        binding.clickListener=this@GuActivity
    }

    private fun initData() {
        //도시 코드값을 받아온다
        intent.getStringExtra("regionCode")?.run {
            regionCode = this
        }
        test(regionCode.substring(0,2)+"*000000")
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.ib_back->{
                //TODO onResume 생각하자
                finish()
            }
        }
    }
    fun test(code: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitService.REG_CODE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val regionService = retrofit.create(ApiService::class.java)
        regionService.getCityCode(code).enqueue(object : Callback<CityResponse> {
            override fun onResponse(
                call: Call<CityResponse>,
                response: Response<CityResponse>
            ) {
                Log.e("doori", response.toString())
                response.body()?.apply {
                    val regResponse = this as CityResponse
                    val regcodes = regResponse.regcodes
                    Log.e("doori", response.toString())
                    val cityDataList: ArrayList<String> = arrayListOf()
                    val cityHash= HashMap<String,String>()
                    for (a in regcodes) {
                        Log.e("doori", "이름 : ${a.name} , 코드 : ${a.code}")
                        cityDataList.add(a.name)
                        cityHash.put(a.name,a.code)
                    }
                    val adapter = RegionAdapter(cityDataList)
                    binding.rcRegion.adapter=adapter
                    binding.rcRegion.layoutManager= LinearLayoutManager(this@GuActivity)

                    adapter.setItemClickListener(object : RegionAdapter.OnItemClickListener {
                        override fun onClick(v: View, position: Int) {
                            val city = adapter.getName(position)
                            Log.e("doori","선택한 값은 $city") //여기서 city는 구 정보가 string으로 나오는건가?
                            val code = cityHash.get(city)
                            Log.e("doori","코드 값은 $code") //구를 포함하는 ex) 서울특별시 종로구면 -> 1111000000 인가?
                            Intent(this@GuActivity,DongActivity::class.java).run {
                                putExtra("regionCode",code)
                                startActivity(this)
                            }
                        }
                    })
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                Log.e("doori", t.toString())
            }
        })
    }
}