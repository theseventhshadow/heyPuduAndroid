import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface CachedPostDao {
    @Query("SELECT * FROM cached_posts ORDER BY timestamp DESC LIMIT 10")
    suspend fun getLatestPosts(): List<CachedPost>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CachedPost>)

    @Query("DELETE FROM cached_posts WHERE id NOT IN (SELECT id FROM cached_posts ORDER BY timestamp DESC LIMIT 10)")
    suspend fun trimCacheToTen()

    @Query("DELETE FROM cached_posts")
    suspend fun clearAll(): Int // Retorna el número de filas eliminadas
}
