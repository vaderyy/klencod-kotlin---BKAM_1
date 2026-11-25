package com.webkam.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Market categories enum
enum class MarketType(val nameAr: String, val nameEn: String) {
    FOOD("سوق غذائية", "Food Market"),
    LIVESTOCK("سوق مواشي", "Livestock Market"),
    CLEANING("سوق تنظيف", "Cleaning Market"),
    CLOTHES("سوق ملابس", "Clothes Market"),
    HERBS("سوق عطار", "Herbs Market"),
    PHONES("سوق هواتف", "Phones Market"),
    SHOES("سوق احذية", "Shoes Market"),
    LEGUMES("سوق بقوليات", "Legumes Market"),
    PHARMACY("سوق صيدلية", "Pharmacy Market"),
    GOLD("سوق ذهب", "Gold Market"),
    HOME("سوق منزلية", "Home Market"),
    TOYS("سوق ألعاب", "Toys Market"),
    ELECTRICAL("سوق كهربائية", "Electrical Market"),
    WHOLESALE("جملة", "Wholesale")
}

// Product size enum
enum class ProductSize(val labelAr: String, val labelEn: String) {
    SMALL("صغير", "Small"),
    MEDIUM("متوسط", "Medium"),
    LARGE("كبير", "Large"),
    NONE("بدون", "None")
}

// Product entity for Room database
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val marketType: String,
    val imagePath: String,       // Path to image stored in app internal storage
    val imageHash: String,       // Hash of image for recognition
    val size: String,
    val price: Double,
    val wholesalePrice: Double?,  // null = no wholesale
    val createdAt: Long = System.currentTimeMillis()
)

// Language preference
data class AppLanguage(val code: String, val displayName: String) {
    companion object {
        val ARABIC = AppLanguage("ar", "العربية")
        val ENGLISH = AppLanguage("en", "English")
    }
}

// Theme mode
enum class ThemeMode {
    LIGHT, DARK
}

// Font size
enum class FontSize(val scale: Float) {
    SMALL(0.85f),
    MEDIUM(1.0f),
    LARGE(1.2f)
}
