package com.webkam.app.data.repository

import android.content.Context
import androidx.room.*
import com.webkam.app.data.model.Product
import kotlinx.coroutines.flow.Flow

// DAO Interface
@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE marketType = :marketType ORDER BY createdAt DESC")
    fun getProductsByMarket(marketType: String): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR marketType LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE imageHash = :hash LIMIT 1")
    suspend fun getProductByImageHash(hash: String): Product?

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long)
}

// Room Database
@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: ProductDatabase? = null

        fun getInstance(context: Context): ProductDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "bakam_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

// Repository
class ProductRepository(context: Context) {

    private val dao = ProductDatabase.getInstance(context).productDao()

    suspend fun insertProduct(product: Product): Long = dao.insertProduct(product)

    suspend fun updateProduct(product: Product) = dao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = dao.deleteProduct(product)

    fun getProductsByMarket(marketType: String) = dao.getProductsByMarket(marketType)

    fun getAllProducts() = dao.getAllProducts()

    suspend fun getProductById(id: Long) = dao.getProductById(id)

    fun searchProducts(query: String) = dao.searchProducts(query)

    suspend fun getProductByImageHash(hash: String) = dao.getProductByImageHash(hash)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
