package com.webkam.app.ui.product

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.webkam.app.R
import com.webkam.app.data.model.MarketType
import com.webkam.app.databinding.ActivityProductListBinding
import com.webkam.app.utils.LocaleHelper
import com.webkam.app.utils.PreferenceManager
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(this)
    }
    private lateinit var adapter: ProductAdapter
    private lateinit var marketType: MarketType
    private var lang = "ar"

    override fun attachBaseContext(newBase: Context) {
        val lang = PreferenceManager.getLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lang = PreferenceManager.getLanguage(this)
        val marketName = intent.getStringExtra("market_type") ?: MarketType.FOOD.name
        marketType = MarketType.valueOf(marketName)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeProducts()
    }

    private fun setupToolbar() {
        val title = if (lang == "ar") marketType.nameAr else marketType.nameEn
        binding.tvMarketTitle.text = title
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(lang) { product ->
            // Long press = delete dialog
            AlertDialog.Builder(this)
                .setTitle(if (lang == "ar") "حذف المنتج" else "Delete Product")
                .setMessage(if (lang == "ar") "هل تريد حذف ${product.name}؟" else "Delete ${product.name}?")
                .setPositiveButton(if (lang == "ar") "حذف" else "Delete") { _, _ ->
                    lifecycleScope.launch { viewModel.deleteProduct(product) }
                }
                .setNegativeButton(if (lang == "ar") "إلغاء" else "Cancel", null)
                .show()
        }

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(this@ProductListActivity, 2)
            this.adapter = this@ProductListActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("market_type", marketType.name)
            startActivity(intent)
        }
    }

    private fun observeProducts() {
        viewModel.getProductsByMarket(marketType.name).observe(this) { products ->
            adapter.submitList(products)
            binding.tvEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
