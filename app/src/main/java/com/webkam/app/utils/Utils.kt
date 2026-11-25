package com.webkam.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import com.webkam.app.data.model.FontSize
import com.webkam.app.data.model.ThemeMode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Locale

// ─── PreferenceManager ───────────────────────────────────────────────────────

object PreferenceManager {

    private const val PREF_NAME = "bakam_prefs"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_THEME = "theme"
    private const val KEY_FONT_SIZE = "font_size"
    private const val KEY_FIRST_LAUNCH = "first_launch"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "ar") ?: "ar"

    fun setLanguage(context: Context, lang: String) =
        prefs(context).edit().putString(KEY_LANGUAGE, lang).apply()

    fun getTheme(context: Context): ThemeMode {
        val name = prefs(context).getString(KEY_THEME, ThemeMode.LIGHT.name)
        return ThemeMode.valueOf(name ?: ThemeMode.LIGHT.name)
    }

    fun setTheme(context: Context, theme: ThemeMode) =
        prefs(context).edit().putString(KEY_THEME, theme.name).apply()

    fun getFontSize(context: Context): FontSize {
        val name = prefs(context).getString(KEY_FONT_SIZE, FontSize.MEDIUM.name)
        return FontSize.valueOf(name ?: FontSize.MEDIUM.name)
    }

    fun setFontSize(context: Context, size: FontSize) =
        prefs(context).edit().putString(KEY_FONT_SIZE, size.name).apply()

    fun isFirstLaunch(context: Context): Boolean =
        prefs(context).getBoolean(KEY_FIRST_LAUNCH, true)

    fun setFirstLaunchDone(context: Context) =
        prefs(context).edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
}

// ─── LocaleHelper ────────────────────────────────────────────────────────────

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    fun isArabic(context: Context): Boolean =
        PreferenceManager.getLanguage(context) == "ar"
}

// ─── ImageUtils ──────────────────────────────────────────────────────────────

object ImageUtils {

    // Save bitmap to app internal storage (not gallery)
    fun saveBitmapInternal(context: Context, bitmap: Bitmap, fileName: String): String {
        val dir = File(context.filesDir, "products")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$fileName.jpg")
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    // Load bitmap from path
    fun loadBitmapFromPath(path: String): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inSampleSize = 2
            }
            BitmapFactory.decodeFile(path, options)
        } catch (e: Exception) {
            null
        }
    }

    // Generate hash from bitmap for recognition
    fun generateImageHash(bitmap: Bitmap): String {
        val resized = Bitmap.createScaledBitmap(bitmap, 32, 32, false)
        val pixels = IntArray(32 * 32)
        resized.getPixels(pixels, 0, 32, 0, 0, 32, 32)
        val sb = StringBuilder()
        pixels.forEach { pixel ->
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            val gray = (r * 0.299 + g * 0.587 + b * 0.114).toInt()
            sb.append(gray.toString(16).padStart(2, '0'))
        }
        return md5(sb.toString()).take(32)
    }

    // Calculate perceptual similarity between two hashes
    fun calculateSimilarity(hash1: String, hash2: String): Float {
        if (hash1.length != hash2.length) return 0f
        var matches = 0
        for (i in hash1.indices) {
            val h1 = hash1[i].digitToIntOrNull(16) ?: continue
            val h2 = hash2[i].digitToIntOrNull(16) ?: continue
            val diff = Math.abs(h1 - h2)
            if (diff <= 2) matches++
        }
        return matches.toFloat() / hash1.length
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray()))
            .toString(16).padStart(32, '0')
    }

    // Fix image rotation from EXIF
    fun fixImageRotation(imagePath: String, bitmap: Bitmap): Bitmap {
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap
        }
    }

    // Delete image file from storage
    fun deleteImageFile(path: String) {
        try {
            File(path).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// ─── CurrencyUtils ───────────────────────────────────────────────────────────

object CurrencyUtils {
    fun formatPrice(price: Double, lang: String): String {
        return if (lang == "ar") {
            "%.2f دينار".format(price)
        } else {
            "%.2f LYD".format(price)
        }
    }
}
