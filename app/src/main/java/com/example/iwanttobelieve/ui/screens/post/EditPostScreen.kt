package com.example.iwanttobelieve.ui.screens.post

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iwanttobelieve.ui.screens.feed.FeedViewModel
import kotlinx.coroutines.launch


@Composable
fun EditPostScreen(
    postId: String,
    onDone: () -> Unit,
    feedViewModel: FeedViewModel = viewModel()
) {
    var description by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(postId) {
        val post = feedViewModel.getPostById(postId)
        description = post?.description ?: ""
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição do Post") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                loading = true
                feedViewModel.viewModelScope.launch {
                    val result = feedViewModel.editPost(postId, description)
                    loading = false
                    if (result.isSuccess) {
                        message = "Post atualizado!"
                        onDone()
                    } else {
                        message = "Erro: ${result.exceptionOrNull()?.message}"
                    }
                }
            }) {
                Text("Salvar Alterações")
            }
        }

        message?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it)
        }
    }
}
