package com.example.swith.ui.study

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.lifecycle.Observer
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swith.R
import com.example.swith.databinding.FragmentCalendarBinding
import com.example.swith.ui.BaseFragment
import com.example.swith.ui.adapter.CalendarRoundRVAdapter
import com.example.swith.ui.study.create.RoundCreateActivity
import com.example.swith.viewmodel.RoundViewModel
import com.prolificinteractive.materialcalendarview.*
import java.time.ZoneId
import java.time.ZonedDateTime

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar){
    private val viewModel : RoundViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView(){
        with(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))){
            binding.tvCalendarDate.text = "${year%1000}/${monthValue}/${dayOfMonth}"
            viewModel.setCalendarData(year, monthValue, dayOfMonth)
        }

        with(binding.rvCalendarRound){
            adapter = CalendarRoundRVAdapter()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        with(binding.calendarView) {
            state().edit()
                .isCacheCalendarPositionEnabled(false)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
            addDecorators(RoundDecorator(), TodayDecorator())

            setHeaderTextAppearance(R.style.CustomHeader)
            setWeekDayTextAppearance(R.style.CustomWeekDay)
            setDateTextAppearance(R.style.CustomDate)
            setDateSelected(CalendarDay.today(), true)

            setOnDateChangedListener { _, date, _ ->
                binding.tvCalendarDate.text = "${date.year % 1000}/${date.month}/${date.day}"
                viewModel.setCalendarData(date.year, date.month, date.day)
            }
        }
        with(viewModel.calendarLiveData){
            observe(viewLifecycleOwner, Observer {
                (binding.rvCalendarRound.adapter as CalendarRoundRVAdapter).setData(it)
                value?.isEmpty()?.let { it1 -> setRVVisibility(it1) }
            })
        }

        binding.btnCreateCalendar.setOnClickListener { startActivity(Intent(activity, RoundCreateActivity::class.java)) }
        binding.btnNoCreateCalendar.setOnClickListener { startActivity(Intent(activity, RoundCreateActivity::class.java)) }
    }

    inner class TodayDecorator: DayViewDecorator{
        override fun shouldDecorate(date: CalendarDay): Boolean {
            with(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))){
                if (year == date.year && monthValue == date.month && dayOfMonth == date.day) return true
            }
            return false
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object: ForegroundColorSpan(Color.rgb(255, 127, 0)) {})
            view?.addSpan(object: StyleSpan(Typeface.BOLD){} )
        }
    }

    inner class RoundDecorator: DayViewDecorator{
        override fun shouldDecorate(date: CalendarDay): Boolean = viewModel.roundDayExists(date.year, date.month, date.day)

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(CustomDotSpan())
        }
    }

    inner class CustomDotSpan : LineBackgroundSpan{
        private val radius : Float = 8f
        override fun drawBackground(
            canvas: Canvas,
            paint: Paint,
            left: Int,
            right: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            text: CharSequence,
            start: Int,
            end: Int,
            lineNumber: Int
        ) {
            // 원 그리고 색깔 칠하기
            canvas.drawCircle(((left + right) / 2 ).toFloat(), bottom + radius, radius, paint.apply { color = Color.BLUE })
            // 글짜 색만 다시 칠하기
            // 현재 진한 하늘색
            paint.color = Color.rgb(72, 180, 224)
        }
    }

    private fun setRVVisibility(isEmpty: Boolean){
        with(binding){
            rvCalendarRound.visibility = if (isEmpty) View.INVISIBLE else View.VISIBLE
            btnCreateCalendar.visibility = if(isEmpty) View.INVISIBLE else View.VISIBLE
            tvNoRound.visibility = if (isEmpty) View.VISIBLE else View.INVISIBLE
            btnNoCreateCalendar.visibility = if(isEmpty) View.VISIBLE else View.INVISIBLE
        }
    }
}