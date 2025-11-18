import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_posts")
data class CachedPost(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val authorId: String,
    val authorName: String?,
    val authorProfileUrl: String?,
    val audioUrl: String?,
    val likes: Int,
    val views: Int,
    val timestamp: Long
)
