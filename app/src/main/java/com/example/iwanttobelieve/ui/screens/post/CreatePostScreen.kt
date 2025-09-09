package com.example.iwanttobelieve.ui.screens.post

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iwanttobelieve.util.Resource

@Composable
fun CreatePostScreen(
    onPostCreated: () -> Unit,
    createPostViewModel: CreatePostViewModel = viewModel()
) {
    var description by remember { mutableStateOf("") }

    val postState by createPostViewModel.postCreationState.collectAsStateWithLifecycle()
    val context = LocalContext.current



    LaunchedEffect(postState) {
        when (val state = postState) {
            is Resource.Success -> if (state.data == Unit) onPostCreated()
            is Resource.Error -> Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nova Publicação", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(24.dp))

        if (postState is Resource.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        createPostViewModel.createPost(description)
                    } else {
                        Toast.makeText(context, "A descrição é obrigatória.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar")
            }
        }
    }
}