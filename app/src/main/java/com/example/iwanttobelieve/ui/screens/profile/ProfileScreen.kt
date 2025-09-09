package com.example.iwanttobelieve.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iwanttobelieve.ui.screens.feed.components.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit, // 🚀 adicionado para navegação
    profileViewModel: ProfileViewModel = viewModel()
) {
    val user by profileViewModel.user.collectAsStateWithLifecycle()
    val posts by profileViewModel.posts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                actions = {
                    Row {
                        Button(onClick = { onNavigateToEditProfile() }) {
                            Text("Editar perfil")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            profileViewModel.logout()
                            onLogout()
                        }) {
                            Text("Logout")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 🔹 Dados do usuário
            user?.let {
                Text(
                    text = "Nome: ${it.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Email: ${it.email}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Lista de publicações do usuário
            Text("Minhas publicações", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    PostItem(
                        post = post,
                        onLikeClick = { profileViewModel.toggleLike(it) },
                        onDeleteClick = { profileViewModel.deletePost(it) },
                        onEditClick = { /* 🚀 pode abrir tela de edição do post */ },
                        onAddComment = { postId, text ->
                            profileViewModel.addComment(postId, text)
                        }
                    )
                }
            }
        }
    }
}
