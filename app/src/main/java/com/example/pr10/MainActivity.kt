package com.example.pr10

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pr10.ui.theme.Pr10Theme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pr10Theme {
                ShoppingCartScreen()
            }
        }
    }
}

data class Cart(
    val products: List<Product>
)

data class Product(
    val id: Int,
    val name: String,
    val price: Int
)

@Composable
fun ShoppingCartScreen() {
    val context = LocalContext.current

    var products by remember {
        mutableStateOf(
            listOf(
                Product(0, "Товар #1", 100),
                Product(1, "Товар #2", 150),
                Product(2, "Товар #3", 56)
            )
        )
    }

    val totalSum = products.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Товары в корзине:")

        if (products.isEmpty()) {
            Text(text = "Корзина пуста")
        } else {
            products.forEach { product ->
                Text(text = "${product.name} - ${product.price} рублей")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Товаров на сумму: ${totalSum} рублей")

        Spacer(modifier = Modifier.height(16.dp))

        AddProductSection {
            val newProduct = Product(
                id = products.size,
                name = "Товар #${products.size + 1}",
                price = Random.nextInt(0, 101)
            )
            val updated = products + newProduct
            products = updated
            if (updated.sumOf { it.price } > 500) {
                Toast.makeText(context, "Доставка бесплатная!", Toast.LENGTH_SHORT).show()
            }
        }

        RemoveProductSection(onRemove = {
            products = products.dropLast(1)
        }, isVisible = products.isNotEmpty())
    }
}

@Composable
fun AddProductSection(onAdd: () -> Unit) {
    Button(onClick = onAdd) {
        Text(text = "Добавить товар")
    }
}

@Composable
fun RemoveProductSection(onRemove: () -> Unit, isVisible: Boolean) {
    if (isVisible) {
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRemove) {
            Text(text = "Удалить товар")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingCartPreview() {
    Pr10Theme {
        ShoppingCartScreen()
    }
}
