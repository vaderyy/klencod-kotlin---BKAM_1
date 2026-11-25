package com.webkam.app.ui.market

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.webkam.app.data.model.MarketType
import com.webkam.app.databinding.FragmentMarketBinding
import com.webkam.app.ui.product.ProductListActivity
import com.webkam.app.utils.PreferenceManager

class MarketFragment : Fragment() {

    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lang = PreferenceManager.getLanguage(requireContext())
        val markets = MarketType.values().toList()

        val adapter = MarketAdapter(markets, lang) { marketType ->
            val intent = Intent(requireContext(), ProductListActivity::class.java)
            intent.putExtra("market_type", marketType.name)
            startActivity(intent)
        }

        binding.rvMarkets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
