package com.example.iwanttobelieve.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
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
fun FeedScreen(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToEditPost: (String) -> Unit,
    feedViewModel: FeedViewModel = viewModel()
) {
    val posts by feedViewModel.posts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("I want to believe") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreatePost) {
                Icon(Icons.Default.Add, contentDescription = "Nova Publicação")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(posts) { post ->
                PostItem(
                    post = post,
                    onLikeClick = { feedViewModel.toggleLike(it) },
                    onDeleteClick = { feedViewModel.deletePost(it) },
                    onEditClick = { onNavigateToEditPost(post.id) },
                    onAddComment = { postId, text ->
                        feedViewModel.addComment(postId, text)
                    }
                )
            }
        }
    }
}
