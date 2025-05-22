package com.example.td2.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.td2.model.Api
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class QuoteViewModel : ViewModel() {
    private val _quoteState = mutableStateOf<QuoteUiState>(QuoteUiState.Loading)
    val quoteState: State<QuoteUiState> = _quoteState

    fun fetchQuote() {
        viewModelScope.launch {
            _quoteState.value = QuoteUiState.Loading
            try {
                val jsonString = Api.retrofitService.getQuote()

                // Extraction avec regex
                val quoteRegex = "\"q\":\"(.*?)\"".toRegex()
                val authorRegex = "\"a\":\"(.*?)\"".toRegex()

                val quote = quoteRegex.find(jsonString)?.groupValues?.get(1) ?: ""
                val author = authorRegex.find(jsonString)?.groupValues?.get(1) ?: ""

                // Afficher directement la réponse JSON brute
                _quoteState.value = QuoteUiState.Success("$quote\n\n- $author")
            } catch (e: Exception) {
                _quoteState.value = QuoteUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}

sealed class QuoteUiState {
    object Loading : QuoteUiState()
    data class Success(val quote: String, val imageUrl: String = "https://st.depositphotos.com/1013513/2312/i/450/depositphotos_23122598-stock-photo-silhouette-of-happy-young-woman.jpg") : QuoteUiState()
    data class Error(val message: String) : QuoteUiState()
}

@Composable
fun quoteScreen(viewModel: QuoteViewModel = viewModel(), navController : NavController) {
    val quoteState = viewModel.quoteState.value
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.fetchQuote()
    }

    LaunchedEffect(quoteState) {
        if (quoteState is QuoteUiState.Success) {
            withContext(Dispatchers.IO) {
                try {
                    val url = URL(quoteState.imageUrl)
                    val bitmap = BitmapFactory.decodeStream(url.openStream())
                    imageBitmap = bitmap?.asImageBitmap()
                } catch (e: Exception) {
                    println("Erreur de chargement d'image: ${e.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (quoteState) {
            is QuoteUiState.Loading -> {
                CircularProgressIndicator()
            }
            is QuoteUiState.Success -> {
                Text(text = quoteState.quote)
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Image",
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(900.dp, 600.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
            is QuoteUiState.Error -> {
                Text(text = "Erreur: ${quoteState.message}")
                Button(onClick = { viewModel.fetchQuote() }) {
                    Text("Réessayer")
                }
            }
        }
        Button(onClick = {navController.navigate(NavRoutes.MAIN_SCREEN.route)}){
            Text("Go back !")
        }    }
}
