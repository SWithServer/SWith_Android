package com.example.swith.ui.study.create

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.example.swith.R
import com.example.swith.data.DateTime
import com.example.swith.databinding.ActivityRoundCreateBinding
import com.example.swith.databinding.DialogTimepickerBinding
import com.example.swith.utils.ToolBarManager
import java.time.LocalDateTime
import java.util.*


class RoundCreateActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private val year = calendar.get(Calendar.YEAR)
    private val month = calendar.get(Calendar.MONTH)
    private val day = calendar.get(Calendar.DAY_OF_MONTH)

    private var startTime: DateTime? = null
    private var endTime: DateTime? = null

    lateinit var binding: ActivityRoundCreateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_round_create)

        ToolBarManager(this).initToolBar(binding.toolbarCreate,
            titleVisible = false,
            backVisible = true
        )
        initCheckBox()
        initListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initCheckBox(){
        with(binding){
            etCreatePlace.isEnabled = false
            cbCreateOnline.setOnCheckedChangeListener{ _, isChecked ->
                if (isChecked) {
                    cbCreateOffline.isChecked = false
                    etCreatePlace.apply {
                        isEnabled = false
                        setText("?????????")
                    }
                } else{
                    etCreatePlace.apply{
                        if (cbCreateOffline.isChecked) isEnabled = true
                        hint = "??????(????????? ??????)"
                        setText("")
                    }
                }
            }
            cbCreateOffline.setOnCheckedChangeListener{ _, isChecked ->
                if (isChecked) {
                    cbCreateOnline.isChecked = false
                    etCreatePlace.apply {
                        isEnabled = true
                    }
                }
                else {
                    etCreatePlace.apply{
                        isEnabled = false
                        setText("")
                    }
                }
            }
        }
    }

    private fun initListener(){
        // ?????? ?????? ?????? ??????????????? ???
        with(binding){
            btnCreateStartDate.setOnClickListener {
                initDateTimePicker(true)
            }
            btnCreateEndDate.setOnClickListener {
                initDateTimePicker(false)
            }
            etCreateDetail.doAfterTextChanged {
                // ??? ?????? ??? ???????????? ?????? empty??? ?????? ??? -> ?????? ?????????
                setAddButton()
            }
            etCreatePlace.doAfterTextChanged {
                setAddButton()
            }
            tvCreateReset.setOnClickListener {
                // ?????? ????????? ?????????
                startTime = null
                endTime = null
                btnCreateStartDate.text = "?????? ??????"
                btnCreateEndDate.text = "?????? ??????"
                cbCreateOffline.isChecked = false
                cbCreateOnline.isChecked = false
                etCreatePlace.apply {
                    isClickable = false
                    setText("")
                    hint = resources.getString(R.string.create_place_hint)
                }
                etCreateDetail.apply {
                    setText("")
                    hint = resources.getString(R.string.create_detail)
                }
            }
            btnCreateAdd.setOnClickListener {
                // Todo : ViewModel ?????? post
            }
        }
    }

    private fun initDateTimePicker(isStart: Boolean){
        with(binding) {
            DatePickerDialog(this@RoundCreateActivity, { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
            }, year, month, day).apply {
                // ???????????? ?????? ??????(?????? ??????) ????????????
                // start date, end date ????????? ???????????? ??????
                setOnDateSetListener { _, dateYear, monthOfYear, dayOfMonth ->
                    val dialogBinding : DialogTimepickerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_timepicker, null, false)
                    dialogBinding.npHourPicker.apply {
                        wrapSelectorWheel = false
                        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                        with(LocalDateTime.now()) {
                            val minuteIdx = minute / 10
                            // ?????? ???????????? ?????? ????????? ??? ?????? ????????? ???
                            minValue = if (!isStart && startTime != null && startTime?.year == dateYear && startTime?.month == monthOfYear + 1 && startTime?.day == dayOfMonth) {
                                if (startTime?.minute == 50) startTime?.hourOfDay!! + 1
                                else startTime?.hourOfDay!!
                            }
                            // ????????? ??????
                            else if (year == dateYear && monthValue == monthOfYear + 1 && day == dayOfMonth) {
                                if (!isStart && startTime == null){
                                    // ?????? ????????? 40???????????? ?????? ?????? ????????? 50??? ???????????? ????????? ????????? ??????????????? ???????????? ?????? ???????????? ????????????
                                    if (minuteIdx >= 4) {
                                        hour + 1.also {
                                            value = hour + 1
                                        }
                                    }
                                    else  hour .also {
                                            value = hour
                                        }
                                }
                                else if (minuteIdx == 5 && minute % 10 > 0){
                                    hour + 1 .also {
                                        value = hour + 1
                                    }
                                } else hour .also {
                                    value = hour
                                }
                            } else 0
                            maxValue = if (isStart && endTime != null && endTime?.year == dateYear && endTime?.month == monthOfYear + 1 && endTime?.day == dayOfMonth){
                                if (endTime?.minute == 0) endTime?.hourOfDay!! - 1
                                else endTime?.hourOfDay!!
                            } else 23

                            setOnValueChangedListener { _, _, newVal ->
                                dialogBinding.npMinutePicker.apply{
                                    value = minValue
                                    if (!isStart && startTime != null && startTime?.year == dateYear && startTime?.month == monthOfYear + 1 && startTime?.day == dayOfMonth){
                                        if (newVal == startTime?.hourOfDay){
                                            minValue = (startTime?.minute!! / 10) + 1
                                            displayedValues = arrayOf("0", "10", "20", "30", "40", "50").sliceArray(minValue..5)
                                        } else {
                                            displayedValues = arrayOf("0", "10", "20", "30", "40", "50")
                                            minValue = 0
                                        }
                                        maxValue = 5
                                    }else if (isStart && endTime != null && dateYear == endTime?.year && monthOfYear + 1 == endTime?.month && dayOfMonth == endTime?.day) {
                                        if (endTime?.hourOfDay == newVal) {
                                            maxValue = (endTime?.minute!! / 10) - 1
                                            displayedValues =
                                                arrayOf("0", "10", "20", "30", "40", "50").sliceArray(
                                                    0..maxValue
                                                )
                                        } else {
                                            maxValue = 5
                                            displayedValues = arrayOf("0", "10", "20", "30", "40", "50")
                                        }
                                    }
                                    else if (year == dateYear && monthValue == monthOfYear + 1 && day == dayOfMonth) {
                                        if (newVal.toString() == hour.toString()) {
                                            minValue = minuteIdx + 1
                                            displayedValues = arrayOf("0", "10", "20", "30", "40", "50").sliceArray(minuteIdx + 1..5)
                                        } else {
                                            displayedValues = arrayOf("0", "10", "20", "30", "40", "50")
                                            value = 0
                                            minValue = 0
                                        }
                                        maxValue = 5
                                    }

                                }
                            }
                        }
                    }

                    dialogBinding.npMinutePicker.apply {
                        wrapSelectorWheel = false
                        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                        with(LocalDateTime.now()) {
                            if (!isStart && startTime != null && dateYear == startTime?.year && monthOfYear + 1 == startTime?.month && dayOfMonth == startTime?.day) {
                                // ?????? ????????? ????????? ?????? ????????? ????????? ????????? ??????
                                if (startTime?.hourOfDay.toString() == dialogBinding.npHourPicker.value.toString()) {
                                    minValue = (startTime?.minute!! / 10) + 1
                                    maxValue = 5
                                    displayedValues =
                                        arrayOf("0", "10", "20", "30", "40", "50").sliceArray(
                                            minValue..5
                                        )
                                } else {
                                    minValue = 0
                                    maxValue = 5
                                    displayedValues = arrayOf("0", "10", "20", "30", "40", "50")
                                }
                            }else if (isStart && endTime != null && dateYear == endTime?.year && monthOfYear + 1 == endTime?.month && dayOfMonth == endTime?.day) {
                                if (endTime?.hourOfDay.toString() == dialogBinding.npHourPicker.value.toString()) {
                                    maxValue = (endTime?.minute!! / 10) - 1
                                    displayedValues =
                                        arrayOf("0", "10", "20", "30", "40", "50").sliceArray(
                                            0..maxValue
                                        )
                                } else {
                                    maxValue = 5
                                    displayedValues = arrayOf("0", "10", "20", "30", "40", "50")
                                }
                            }
                            else if (year != dateYear || monthValue != monthOfYear + 1 || day != dayOfMonth) {
                                minValue = 0
                                displayedValues = arrayOf("0", "10", "20", "30", "40", "50")
                                maxValue = 5
                            }
                            else if (year == dateYear && monthValue == monthOfYear + 1 && day == dayOfMonth && hour.toString() == dialogBinding.npHourPicker.value.toString()){
                                minValue = if (isStart) minute / 10 + 1 else minute / 10 + 2
                                displayedValues = arrayOf("0", "10", "20", "30", "40", "50").sliceArray(minValue..5)
                                maxValue = 5
                            } else if (!isStart && year == dateYear && monthValue == monthOfYear + 1 && day == dayOfMonth && (hour+1).toString() == dialogBinding.npHourPicker.value.toString()){
                                minValue = 1
                                displayedValues = arrayOf("0", "10", "20", "30", "40", "50").sliceArray(1..5)
                                maxValue = 5
                            }
                            else {
                                minValue = 0
                                displayedValues = arrayOf("0", "10", "20", "30", "40", "50")
                                maxValue = 5
                            }
                        }
                    }
                    AlertDialog.Builder(this@RoundCreateActivity)
                        .setTitle(if (isStart) "?????? ??????" else "?????? ??????")
                        .setView(dialogBinding.root)
                        .setPositiveButton("??????") { dialog, which
                            ->
                            if (isStart) {
                                btnCreateStartDate.text = String.format(
                                    "?????? : ${dateYear}.${monthOfYear + 1}.${dayOfMonth} ${dialogBinding.npHourPicker.value}:%02d",
                                    (dialogBinding.npMinutePicker.value) * 10
                                )
                                startTime = DateTime(dateYear, monthOfYear + 1, dayOfMonth, dialogBinding.npHourPicker.value, dialogBinding.npMinutePicker.value * 10)

                                // ?????? ????????? ?????? ?????? ?????? ??? ????????? ????????? ?????? ????????? ?????? ?????? ?????? ????????? ?????? ??????(?????? ????????? ??? ??????) ?????? ?????? ?????????
                                if (compareStartEndTime() == 2 || compareStartEndTime() == 3){
                                    btnCreateEndDate.text = "?????? ??????"
                                    endTime = null
                                }
                            }
                            else {
                                btnCreateEndDate.text = String.format(
                                    "?????? : ${dateYear}.${monthOfYear + 1}.${dayOfMonth} ${dialogBinding.npHourPicker.value}:%02d",
                                    (dialogBinding.npMinutePicker.value) * 10
                                )
                                endTime = DateTime(dateYear, monthOfYear + 1, dayOfMonth, dialogBinding.npHourPicker.value, dialogBinding.npMinutePicker.value * 10)

                                // ?????? ????????? ?????? ?????? ?????? ??? ????????? ????????? ?????? ????????? ?????? ?????? ?????? ????????? ?????? ??????(?????? ????????? ??? ??????) ?????? ?????? ?????????
                                if (compareStartEndTime() == 2 || compareStartEndTime() == 3){
                                    btnCreateStartDate.text = "?????? ?????? "
                                    startTime = null
                                }
                            }
                        }
                        .setNegativeButton("??????") { _, _
                            -> this.show() }
                        .create()
                        .show()
                }
                if(isStart || startTime == null){
                    datePicker.minDate = System.currentTimeMillis() - 1000
                } else {
                    startTime?.let{
                        calendar.set(startTime?.year!!, startTime?.month!! - 1, startTime?.day!!)
                        datePicker.minDate = calendar.timeInMillis
                    }
                }

                if(isStart){
                    endTime?.let{
                        calendar.set(endTime?.year!!, endTime?.month!! - 1, endTime?.day!!)
                        datePicker.maxDate = calendar.timeInMillis
                    }
                }
                show()
            }
        }

    }

    private fun setAddButton(){
        with(binding) {
            btnCreateAdd.apply {
                visibility = if (!etCreatePlace.text.isNullOrBlank() && !etCreateDetail.text.isNullOrBlank() && startTime != null && endTime != null) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun compareStartEndTime() : Int {
        // 0 : ?????? null
        // 1 : startTime ??? ??? ?????? (startTime ??? ?????? ??? ??????)
        // 2 : endTime ??? ??? ?????? (??? ?????? ?????? ???????????? ???)
        // 3 : ?????? ??????
        startTime?.let{
            endTime?.let{
                val startTimeToLong = String.format("%4d%02d%02d%02d%02d", startTime?.year, startTime?.month, startTime?.day, startTime?.hourOfDay, startTime?.minute).toLong()
                val endTimeToLong = String.format("%4d%02d%02d%02d%02d", endTime?.year, endTime?.month, endTime?.day, endTime?.hourOfDay, endTime?.minute).toLong()
                return if (startTimeToLong < endTimeToLong) 1
                else if (startTimeToLong > endTimeToLong) 2
                else 3
            }
        }
        return 0
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val rect = Rect()
            currentFocus?.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                currentFocus?.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
