package com.example.smartpick.features.feed.data

import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject

interface FeedRepository {
    suspend fun getPosts(): List<Pair<Post, User>>
}

class FeedRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : FeedRepository {
    override suspend fun getPosts(): List<Pair<Post, User>> {
        // Mock data to ensure the app builds and shows the new UI
//        val mockUser = User(
//            id = "user1",
//            fullName = "Lê Hải An",
//            avatarUrl = "https://via.placeholder.com/150"
//        )
//        return listOf(
//            Pair(
//                Post(
//                    id = "1",
//                    idUser = "user1",
//                    content = "Góc setup làm việc tối giản với chiếc bàn nâng hạ và màn hình Ultrawide. Rất recommend anh em dùng thử giá đỡ màn hình của Human Motion nhé, siêu chắc chắn! 🖥️✨",
//                    createAt = "2 giờ trước",
//                    images = listOf("https://images.unsplash.com/photo-1498050108023-c5249f4df085")
//                ),
//                mockUser
//            ),
//            Pair(
//                Post(
//                    id = "2",
//                    idUser = "user1",
//                    content = "Vừa sắm em bàn phím cơ Keychron Q1 Pro. Cảm giác gõ phím quá đã, build nhôm nguyên khối đầm tay. ⌨️🔥",
//                    createAt = "5 giờ trước",
//                    images = emptyList()
//                ),
//                mockUser
//            )
//        )
//    }
        return emptyList()
    }
}