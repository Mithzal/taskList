package com.example.td2.ui.quote

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.MaterialTheme
import com.example.td2.navigation.NavRoutes


class QuoteViewModel : ViewModel() {
    private val _quoteState = mutableStateOf<QuoteUiState>(QuoteUiState.Loading)
    val quoteState: State<QuoteUiState> = _quoteState

    fun fetchQuote() {
        viewModelScope.launch {
            _quoteState.value = QuoteUiState.Loading
            try {
                val jsonString = Api.retrofitService.getQuote()

                // Extraction with regex
                val quoteRegex = "\"q\":\"(.*?)\"".toRegex()
                val authorRegex = "\"a\":\"(.*?)\"".toRegex()

                val quote = quoteRegex.find(jsonString)?.groupValues?.get(1) ?: ""
                val author = authorRegex.find(jsonString)?.groupValues?.get(1) ?: ""

                _quoteState.value = QuoteUiState.Success("$quote\n\n- $author")
            } catch (e: Exception) {
                _quoteState.value = QuoteUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}

sealed class QuoteUiState {
    object Loading : QuoteUiState()
    data class Success(val quote: String, val imageUrl: String = "https://picsum.photos/200/300") : QuoteUiState()
    data class Error(val message: String) : QuoteUiState()
}

@Composable
fun quoteScreen(viewModel: QuoteViewModel = viewModel(), navController: NavController) {
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (quoteState) {
            is QuoteUiState.Loading -> {
                CircularProgressIndicator()
            }
            is QuoteUiState.Success -> {
                // Image en fond d'écran
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Fond d'écran",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Conteneur semi-transparent pour la citation
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quoteState.quote,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    CircularProgressIndicator()
                }
            }
            is QuoteUiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Erreur: ${quoteState.message}")
                    Button(onClick = { viewModel.fetchQuote() }) {
                        Text("Réessayer")
                    }
                }
            }
        }

        // Bouton retour en bas de l'écran
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(onClick = { navController.navigate(NavRoutes.MAIN_SCREEN.route) }) {
                Text("Go back !")
            }
        }
    }
}

