package com.webkam.app.ui.camera

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.webkam.app.R
import com.webkam.app.data.model.MarketType
import com.webkam.app.databinding.ActivityCameraBinding
import com.webkam.app.ui.product.ProductViewModel
import com.webkam.app.ui.product.ProductViewModelFactory
import com.webkam.app.utils.CurrencyUtils
import com.webkam.app.utils.ImageUtils
import com.webkam.app.utils.LocaleHelper
import com.webkam.app.utils.PreferenceManager
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private val viewModel: ProductViewModel by viewModels { ProductViewModelFactory(this) }

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var lang = "ar"
    private var isProcessing = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else {
            Toast.makeText(this,
                if (lang == "ar") "الكاميرا مطلوبة لهذه الميزة" else "Camera permission required",
                Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = PreferenceManager.getLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lang = PreferenceManager.getLanguage(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.tvInstruction.text =
            if (lang == "ar") "صوّر المنتج للتعرف على سعره" else "Capture product to identify price"

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCapture.setOnClickListener {
            if (!isProcessing) captureAndRecognize()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureAndRecognize() {
        isProcessing = true
        binding.tvInstruction.text =
            if (lang == "ar") "جارٍ التعرف على المنتج..." else "Recognizing product..."

        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxy.toBitmap()
                    imageProxy.close()
                    // Process on background
                    lifecycleScope.launch {
                        processImage(bitmap)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    isProcessing = false
                    binding.tvInstruction.text =
                        if (lang == "ar") "خطأ في التقاط الصورة" else "Capture error"
                }
            }
        )
    }

    private suspend fun processImage(bitmap: Bitmap) {
        val hash = ImageUtils.generateImageHash(bitmap)
        val product = viewModel.getProductByImageHash(hash)

        if (product != null) {
            // Exact match found
            showProductResult(product.name, product.price, product.wholesalePrice,
                try { MarketType.valueOf(product.marketType) } catch (e: Exception) { null })
        } else {
            // Try similarity search across all products
            val allProducts = viewModel.getAllProducts().value ?: emptyList()
            var bestMatch: com.webkam.app.data.model.Product? = null
            var bestScore = 0f

            for (p in allProducts) {
                val score = ImageUtils.calculateSimilarity(hash, p.imageHash)
                if (score > bestScore) {
                    bestScore = score
                    bestMatch = p
                }
            }

            if (bestScore >= 0.75f && bestMatch != null) {
                showProductResult(bestMatch.name, bestMatch.price, bestMatch.wholesalePrice,
                    try { MarketType.valueOf(bestMatch.marketType) } catch (e: Exception) { null })
            } else {
                showProductNotFound()
            }
        }
    }

    private fun showProductResult(
        name: String, price: Double, wholesalePrice: Double?, market: MarketType?
    ) {
        runOnUiThread {
            isProcessing = false
            val marketLabel = if (lang == "ar") market?.nameAr ?: "" else market?.nameEn ?: ""
            val priceFormatted = CurrencyUtils.formatPrice(price, lang)
            val wsLine = if (wholesalePrice != null)
                "\n${if (lang == "ar") "سعر الجملة" else "Wholesale"}: ${CurrencyUtils.formatPrice(wholesalePrice, lang)}"
            else ""

            AlertDialog.Builder(this)
                .setTitle(if (lang == "ar") "✓ تم التعرف على المنتج" else "✓ Product Identified")
                .setMessage(
                    "📦 $name\n" +
                    "🏪 $marketLabel\n" +
                    "💰 ${if (lang == "ar") "السعر" else "Price"}: $priceFormatted$wsLine"
                )
                .setPositiveButton(if (lang == "ar") "حسناً" else "OK") { _, _ ->
                    binding.tvInstruction.text =
                        if (lang == "ar") "صوّر المنتج للتعرف على سعره" else "Capture product to identify price"
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun showProductNotFound() {
        runOnUiThread {
            isProcessing = false
            binding.tvInstruction.text =
                if (lang == "ar") "صوّر المنتج للتعرف على سعره" else "Capture product to identify price"

            AlertDialog.Builder(this)
                .setTitle(if (lang == "ar") "⚠ غير موجود" else "⚠ Not Found")
                .setMessage(
                    if (lang == "ar")
                        "هذا المنتج غير مُضاف.\nيرجى إضافته أولاً من قائمة الأسواق."
                    else
                        "This product is not added.\nPlease add it first from the market list."
                )
                .setPositiveButton(if (lang == "ar") "حسناً" else "OK", null)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
