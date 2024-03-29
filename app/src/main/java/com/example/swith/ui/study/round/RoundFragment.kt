package com.example.swith.ui.study.round

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.data.entity.GetSessionRes
import com.example.swith.R
import com.example.swith.databinding.FragmentRoundBinding
import com.example.swith.ui.adapter.RoundRVAdapter
import com.example.swith.ui.study.announce.AnnounceActivity
import com.example.swith.ui.study.create.RoundCreateActivity
import com.example.swith.utils.base.BaseFragment
import com.example.swith.viewmodel.RoundViewModel

class RoundFragment : BaseFragment<FragmentRoundBinding>(R.layout.fragment_round) {
    private val viewModel: RoundViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.roundListRv.apply {
            adapter = RoundRVAdapter().apply {
                setItemClickListener(object : RoundRVAdapter.myItemClickListener {
                    override fun onItemClick(round: GetSessionRes) {
                        viewModel.setCurrentData(round.sessionIdx)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.study_frm, RoundTabFragment())
                            .commitAllowingStateLoss()
                    }
                })
            }
        }

        observeViewModel()

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
                if (it.resultCode == Activity.RESULT_OK) {
                    setViewVisibility(true)
                    viewModel.loadData()
                    setPastText()
                }
            }

        initListener()
    }

    private fun observeViewModel() {
        setViewVisibility(true)
        viewModel.loadData()
        setPastText()
        viewModel.roundLiveData.observe(viewLifecycleOwner, Observer {
            (binding.roundListRv.adapter as RoundRVAdapter).setData(it)
            binding.roundNoticeContentTv.text = it.announcementContent
            setManagerLayout(it.admin)
            binding.roundAddBtn.visibility = if (it.admin) View.VISIBLE else View.INVISIBLE
        })

        viewModel.mutableScreenState.observe(viewLifecycleOwner, Observer {
            setViewVisibility(false)
        })

        viewModel.mutableErrorType.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireActivity(), "$it 오류", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setViewVisibility(beforeDataLoad: Boolean) {
        with(binding) {
            if (beforeDataLoad) {
                roundCircularIndicator.visibility = View.VISIBLE
                roundMainLayout.visibility = View.INVISIBLE
            } else {
                roundMainLayout.visibility = View.VISIBLE
                roundCircularIndicator.visibility = View.INVISIBLE
            }
        }
    }

    private fun initListener() {
        with(binding) {
            roundNoticeLayout.setOnClickListener {
                activityResultLauncher.launch(Intent(activity, AnnounceActivity::class.java).apply {
                    putExtra("manager", viewModel.roundLiveData.value?.admin)
                    putExtra("groupIdx", viewModel.groupIdx)
                })
            }
            roundAddBtn.setOnClickListener {
                activityResultLauncher.launch(
                    Intent(
                        activity,
                        RoundCreateActivity::class.java
                    ).apply {
                        putExtra("minuteMin", viewModel.roundLiveData.value?.attendanceValidTime)
                        putExtra("groupIdx", viewModel.groupIdx)
                    })
            }
            roundPreviousTv.setOnClickListener {
                viewModel.pastVisible = !viewModel.pastVisible
                setPastText()
                viewModel.roundLiveData.value?.let { viewModel.setPastData() }
            }
            roundPullToRefresh.apply {
                setOnRefreshListener {
                    isRefreshing = false
                    viewModel.loadData()
                }
                setColorSchemeResources(R.color.color_swith)
            }
        }
    }

    private fun setPastText() {
        with(binding.roundPreviousTv) {
            if (viewModel.pastVisible) {
                text = "지난 회차 안보기"
                setTextColor(resources.getColor(R.color.color_cdcdcd, null))
            } else {
                text = "지난 회차 보기"
                setTextColor(resources.getColor(R.color.color_ADA0FF, null))
            }
        }
    }

}