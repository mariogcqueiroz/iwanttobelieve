package com.example.iwanttobelieve.ui.screens.feed.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.iwanttobelieve.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PostItem(
    post: Post,
    onLikeClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onAddComment: (postId: String, text: String) -> Unit
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    var commentText by remember { mutableStateOf("") }

    val formattedDate = remember(post.timestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(post.timestamp.toDate())
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Imagem da publicação",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                // Autor + Data
                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Postado em $formattedDate",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = post.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))

                // Curtidas + editar/excluir
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onLikeClick(post.id) }) {
                        if (currentUid != null && post.likedBy.contains(currentUid)) {
                            Icon(Icons.Filled.Favorite, contentDescription = "Descurtir")
                        } else {
                            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Curtir")
                        }
                    }
                    Text("${post.likesCount} curtida${if (post.likesCount == 1) "" else "s"}")

                    Spacer(modifier = Modifier.weight(1f))

                    if (currentUid != null && currentUid == post.authorUid) {
                        IconButton(onClick = { onEditClick(post.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { onDeleteClick(post.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Comentários
                if (post.comments.isNotEmpty()) {
                    Text("Comentários:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    post.comments.forEach { comment ->
                        Text("💬 ${comment.userName}: ${comment.text}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campo para adicionar comentário
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Adicionar comentário...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(post.id, commentText)
                            commentText = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}