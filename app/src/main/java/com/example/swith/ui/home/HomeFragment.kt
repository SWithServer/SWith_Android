package com.example.swith.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swith.R
import com.example.swith.data.Study
import com.example.swith.databinding.FragmentHomeBinding
import com.example.swith.ui.study.StudyActivity

class HomeFragment : Fragment(){
    lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.homeStudyRv.apply {
            adapter = HomeStudyRVAdapter().apply {
                setMyItemClickListener(object: HomeStudyRVAdapter.myItemClickListener{
                    override fun onItemClick(study: Study) {
                        val intent = Intent(activity, StudyActivity::class.java)
                        startActivity(intent)
                    }
                })
            }
        }

        viewModel.studyLiveData.observe(viewLifecycleOwner, Observer{
            // 스터디가 1개 이상 존재하면 스터디 리사이클러 뷰 보여줌
            setViewVisibility(it.isNotEmpty())
            (binding.homeStudyRv.adapter as HomeStudyRVAdapter).setData(it)
        })

        binding.homeStudyAddIv.setOnClickListener{
            // 기능 확인을 위해 누르면 스터디 하나 추가되게 함
            viewModel.addStudy(Study("스터디 2", "스터디", 3, 2))
        }

        return binding.root
    }

    private fun setViewVisibility(isStudyExists: Boolean){
        with(binding) {
            if (isStudyExists) {
                homeScrollviewSv.visibility = View.VISIBLE
                homeNoStudyLayout.visibility = View.GONE
            } else{
                homeScrollviewSv.visibility = View.GONE
                homeNoStudyLayout.visibility = View.VISIBLE
            }
        }
    }
}