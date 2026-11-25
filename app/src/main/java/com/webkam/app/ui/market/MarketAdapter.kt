package com.webkam.app.ui.market

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webkam.app.data.model.MarketType
import com.webkam.app.databinding.ItemMarketBinding

class MarketAdapter(
    private val markets: List<MarketType>,
    private val lang: String,
    private val onClick: (MarketType) -> Unit
) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    inner class MarketViewHolder(val binding: ItemMarketBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val binding = ItemMarketBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val market = markets[position]
        with(holder.binding) {
            tvMarketName.text = if (lang == "ar") market.nameAr else market.nameEn
            root.setOnClickListener { onClick(market) }

            // Set icon based on market type
            ivMarketIcon.setImageResource(getMarketIcon(market))
        }
    }

    override fun getItemCount() = markets.size

    private fun getMarketIcon(market: MarketType): Int {
        return when (market) {
            MarketType.FOOD -> com.webkam.app.R.drawable.ic_food
            MarketType.LIVESTOCK -> com.webkam.app.R.drawable.ic_livestock
            MarketType.CLEANING -> com.webkam.app.R.drawable.ic_cleaning
            MarketType.CLOTHES -> com.webkam.app.R.drawable.ic_clothes
            MarketType.HERBS -> com.webkam.app.R.drawable.ic_herbs
            MarketType.PHONES -> com.webkam.app.R.drawable.ic_phones
            MarketType.SHOES -> com.webkam.app.R.drawable.ic_shoes
            MarketType.LEGUMES -> com.webkam.app.R.drawable.ic_legumes
            MarketType.PHARMACY -> com.webkam.app.R.drawable.ic_pharmacy
            MarketType.GOLD -> com.webkam.app.R.drawable.ic_gold
            MarketType.HOME -> com.webkam.app.R.drawable.ic_home_goods
            MarketType.TOYS -> com.webkam.app.R.drawable.ic_toys
            MarketType.ELECTRICAL -> com.webkam.app.R.drawable.ic_electrical
            MarketType.WHOLESALE -> com.webkam.app.R.drawable.ic_wholesale
        }
    }
}
