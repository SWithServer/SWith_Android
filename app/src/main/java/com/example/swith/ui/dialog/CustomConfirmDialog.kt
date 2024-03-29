package com.example.swith.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.swith.R
import com.example.swith.databinding.DialogConfirmBinding

class CustomConfirmDialog(private val title: String, private val content: String) :
    DialogFragment() {
    private lateinit var binding: DialogConfirmBinding

    interface CustomListener {
        fun onConfirm()
    }

    private lateinit var customListener: CustomListener

    fun setCustomListener(listener: CustomListener) {
        customListener = listener
    }

    override fun onResume() {
        super.onResume()

        // 디바이스 크기별 세팅
        val params = dialog?.window?.attributes
        val windowManager = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics
        } else {
            TODO("VERSION.SDK_INT < R")
        }
        val deviceWidth = size.bounds.width()

        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm, container, false)
        isCancelable = false
        // 모서리 직각 제거
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            tvConfirmTitle.text = title
            tvConfirmContent.text = content
            btnDialogCancel.setOnClickListener { dismiss() }
            btnDialogConfirm.setOnClickListener { customListener.onConfirm() }
        }
    }

}