package com.example.iwanttobelieve.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditProfileScreen(
    onDone: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val user by profileViewModel.user.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user) {
        name = user?.name ?: ""
        email = user?.email ?: ""
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                loading = true
                profileViewModel.updateProfile(name, email) { result ->
                    loading = false
                    if (result.isSuccess) {
                        message = "Perfil atualizado"
                        onDone()
                    } else {
                        message = "Erro ao atualizar: ${result.exceptionOrNull()?.message}"
                    }
                }
            }) {
                Text("Salvar")
            }
        }

        message?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it)
        }
    }
}
