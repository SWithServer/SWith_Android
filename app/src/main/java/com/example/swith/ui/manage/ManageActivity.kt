package com.example.swith.ui.manage

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.swith.R
import com.example.swith.databinding.ActivityManageBinding
import com.example.swith.utils.ToolBarManager

class ManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage)
        ToolBarManager(this).initToolBar(binding.toolbarManage,
            titleVisible = false,
            backVisible = true
        )
        initListener()
    }

    private fun initListener(){
        with(binding){
            layoutManageStudy.setOnClickListener { /* 스터디 관리 화면으로 */ }
            layoutManageRound.setOnClickListener { /* 회차 관리 화면으로 */ }
            layoutManageAttend.setOnClickListener { /* 출석 관리 화면으로 */ }
            layoutManageUser.setOnClickListener { /* 유저 관리 화면으로 */ }
            layoutManageFinish.setOnClickListener { /* 스터디 종료 */ }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}