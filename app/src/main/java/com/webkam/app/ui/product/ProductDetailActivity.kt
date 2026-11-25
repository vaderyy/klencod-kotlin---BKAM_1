package com.webkam.app.ui.product

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.webkam.app.R
import com.webkam.app.data.model.MarketType
import com.webkam.app.data.model.Product
import com.webkam.app.data.model.ProductSize
import com.webkam.app.databinding.ActivityProductDetailBinding
import com.webkam.app.utils.ImageUtils
import com.webkam.app.utils.LocaleHelper
import com.webkam.app.utils.PreferenceManager
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: ProductViewModel by viewModels { ProductViewModelFactory(this) }

    private var capturedBitmap: Bitmap? = null
    private var imagePath: String = ""
    private var imageHash: String = ""
    private lateinit var marketType: MarketType
    private var lang = "ar"
    private var selectedSize = ProductSize.NONE

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let { handleCapturedImage(it) }
        }
    }

    // Gallery launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleGalleryImage(uri)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = PreferenceManager.getLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lang = PreferenceManager.getLanguage(this)
        val marketName = intent.getStringExtra("market_type") ?: MarketType.FOOD.name
        marketType = MarketType.valueOf(marketName)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.tvTitle.text = if (lang == "ar") "إضافة منتج" else "Add Product"
        binding.btnBack.setOnClickListener { finish() }
        updateSizeSelection(ProductSize.NONE)
    }

    private fun setupClickListeners() {
        // Add image button
        binding.btnAddImage.setOnClickListener { showImageSourceDialog() }
        binding.ivProductPreview.setOnClickListener { showImageSourceDialog() }

        // Size buttons
        binding.btnSizeSmall.setOnClickListener { updateSizeSelection(ProductSize.SMALL) }
        binding.btnSizeMedium.setOnClickListener { updateSizeSelection(ProductSize.MEDIUM) }
        binding.btnSizeLarge.setOnClickListener { updateSizeSelection(ProductSize.LARGE) }
        binding.btnSizeNone.setOnClickListener { updateSizeSelection(ProductSize.NONE) }

        // Save button
        binding.btnSave.setOnClickListener { validateAndSave() }
    }

    private fun showImageSourceDialog() {
        val options = if (lang == "ar")
            arrayOf("تصوير منتج", "استرداد منتج")
        else
            arrayOf("Take Photo", "Choose from Gallery")

        AlertDialog.Builder(this)
            .setTitle(if (lang == "ar") "اختر المصدر" else "Choose Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun handleCapturedImage(bitmap: Bitmap) {
        capturedBitmap = bitmap
        imageHash = ImageUtils.generateImageHash(bitmap)
        binding.ivProductPreview.setImageBitmap(bitmap)
        binding.btnAddImage.text = if (lang == "ar") "تغيير الصورة" else "Change Image"
    }

    private fun handleGalleryImage(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            handleCapturedImage(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this,
                if (lang == "ar") "خطأ في تحميل الصورة" else "Error loading image",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSizeSelection(size: ProductSize) {
        selectedSize = size
        val activeColor = getColor(R.color.color_primary)
        val inactiveColor = getColor(R.color.color_surface)

        binding.btnSizeSmall.setBackgroundColor(if (size == ProductSize.SMALL) activeColor else inactiveColor)
        binding.btnSizeMedium.setBackgroundColor(if (size == ProductSize.MEDIUM) activeColor else inactiveColor)
        binding.btnSizeLarge.setBackgroundColor(if (size == ProductSize.LARGE) activeColor else inactiveColor)
        binding.btnSizeNone.setBackgroundColor(if (size == ProductSize.NONE) activeColor else inactiveColor)
    }

    private fun validateAndSave() {
        val name = binding.etProductName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()

        if (capturedBitmap == null) {
            showError(if (lang == "ar") "الرجاء إضافة صورة للمنتج" else "Please add a product image")
            return
        }
        if (name.isEmpty()) {
            showError(if (lang == "ar") "الرجاء إدخال اسم المنتج" else "Please enter product name")
            return
        }
        if (priceStr.isEmpty()) {
            showError(if (lang == "ar") "الرجاء إدخال السعر" else "Please enter price")
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            showError(if (lang == "ar") "الرجاء إدخال سعر صحيح" else "Please enter valid price")
            return
        }

        showWholesaleDialog(name, price)
    }

    private fun showWholesaleDialog(name: String, price: Double) {
        val options = if (lang == "ar")
            arrayOf("إدخال سعر الجملة", "بدون")
        else
            arrayOf("Add Wholesale Price", "No Wholesale")

        AlertDialog.Builder(this)
            .setTitle(if (lang == "ar") "هل تريد إضافة مجموعة (جملة)؟" else "Add Wholesale Group?")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showWholesalePriceInput(name, price)
                    1 -> saveProduct(name, price, null)
                }
            }
            .show()
    }

    private fun showWholesalePriceInput(name: String, price: Double) {
        val input = android.widget.EditText(this).apply {
            hint = if (lang == "ar") "سعر الجملة" else "Wholesale Price"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        AlertDialog.Builder(this)
            .setTitle(if (lang == "ar") "سعر الجملة" else "Wholesale Price")
            .setView(input)
            .setPositiveButton(if (lang == "ar") "حفظ" else "Save") { _, _ ->
                val wsPrice = input.text.toString().toDoubleOrNull()
                saveProduct(name, price, wsPrice)
            }
            .setNegativeButton(if (lang == "ar") "إلغاء" else "Cancel", null)
            .show()
    }

    private fun saveProduct(name: String, price: Double, wholesalePrice: Double?) {
        val bitmap = capturedBitmap ?: return
        val fileName = "product_${System.currentTimeMillis()}"
        val savedPath = ImageUtils.saveBitmapInternal(this, bitmap, fileName)
        val hash = ImageUtils.generateImageHash(bitmap)

        val product = Product(
            name = name,
            marketType = marketType.name,
            imagePath = savedPath,
            imageHash = hash,
            size = selectedSize.name,
            price = price,
            wholesalePrice = wholesalePrice
        )

        lifecycleScope.launch {
            viewModel.insertProduct(product) {
                runOnUiThread {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        if (lang == "ar") "تم حفظ المنتج بنجاح ✓" else "Product saved successfully ✓",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun showError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
