package com.webkam.app.ui.product

import android.content.Context
import androidx.lifecycle.*
import com.webkam.app.data.model.Product
import com.webkam.app.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(context: Context) : ViewModel() {

    private val repository = ProductRepository(context)

    fun getProductsByMarket(marketType: String): LiveData<List<Product>> =
        repository.getProductsByMarket(marketType).asLiveData()

    fun getAllProducts(): LiveData<List<Product>> =
        repository.getAllProducts().asLiveData()

    fun searchProducts(query: String): LiveData<List<Product>> =
        repository.searchProducts(query).asLiveData()

    fun insertProduct(product: Product, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertProduct(product)
            onComplete(id)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    suspend fun getProductByImageHash(hash: String): Product? =
        repository.getProductByImageHash(hash)
}

class ProductViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
