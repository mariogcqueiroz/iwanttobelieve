# I Want To Believe
Trabalho para a matéria programação de Dispositivos Móveis


Aplicativo de rede social para compartilhamento de posts com imagens, curtidas e comentários, desenvolvido em Kotlin com Jetpack Compose e integrado ao Firebase Firestore.

Funcionalidades

Feed de posts

Exibe todos os posts em ordem decrescente de timestamp.

Mostra autor, descrição, imagem e comentários.

Curtidas

Usuários autenticados podem curtir e descurtir posts de qualquer autor.

Contador de curtidas atualizado em tempo real.

Comentários

Usuários autenticados podem adicionar comentários em qualquer post.

Comentários aparecem com nome do autor e timestamp.

Criação de posts

Usuários autenticados podem criar posts com descrição e imagem (imagem gerada por URL estática do Picsum).

Edição e deleção de posts

Apenas o autor do post pode editar a descrição ou deletar o post.

Perfil do usuário

Cada usuário possui um perfil com nome e UID.

Usuário só pode editar seu próprio perfil.

Arquitetura

MVVM (Model-View-ViewModel)

ViewModel gerencia o estado da UI e interage com o Repository.

Repository Pattern

PostRepository lida com todas as operações do Firestore.

ProfileRepository gerencia informações do usuário.

AuthRepository para autenticação

Kotlin Coroutines

Operações assíncronas com withContext, await, e Flow.

Jetpack Compose

Interface declarativa para listas, cards de posts, botões de like e comentários.

Firebase

Autenticação

Controle de usuários autenticados via FirebaseAuth.

Firestore

Coleções:

/users → perfis de usuário.

/posts → posts, likes e comentários.

Instruções de uso

Clone o repositório:

git clone https://github.com/mariogcqueiroz/iwanttobelieve.git


Configure o Firebase.

Execute o aplicativo:

Compile e rode no Android Studio.

Faça login com um usuário autenticado(ou cadastre um novo).

Teste criar post, curtir e comentar.