import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedPost::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cachedPostDao(): CachedPostDao
}
