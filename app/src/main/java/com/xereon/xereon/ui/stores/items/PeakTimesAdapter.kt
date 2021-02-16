package com.xereon.xereon.ui.stores.items

import com.xereon.xereon.view.barChart.BarChartAdapter

class PeakTimesAdapter: BarChartAdapter() {
    private lateinit var data: Array<Int>
    private var currentPopularity: Int = -1
    private var currentHour = -1

    fun submitData(data: Array<Int>, currentPopularity: Int = -1, hourOfDay: Int = -1) {
        this.data = data
        this.currentHour = hourOfDay
        this.currentPopularity = currentPopularity
        notifyDataSetChanged()
    }

    override fun getPopularity(index: Int) =
        if (index == currentHour) currentPopularity   else data[index];

    override fun getCurrentHour() =
        currentHour
}