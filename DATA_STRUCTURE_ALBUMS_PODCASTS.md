# Estructura de Datos para Álbumes y Podcasts

## 1. ÁLBUMES (Para Artistas/Cantantes)

### Colección: `albums`
```
albums/{albumId}
├── albumId: String (único, generado)
├── artistId: String (referencia al usuario/artista)
├── artistUsername: String
├── artistPhotoUrl: String
├── title: String (nombre del álbum)
├── description: String (descripción o sinopsis)
├── coverUrl: String (imagen de portada)
├── genre: String (género musical)
├── releaseDate: Long (fecha de lanzamiento)
├── totalTracks: Int (cantidad de canciones)
├── createdAt: Long
├── updatedAt: Long
├── isPublished: Boolean (si está visible públicamente)
└── likes: List<String> (IDs de usuarios que le dieron like)
```

### Colección anidada: `albums/{albumId}/tracks`
```
albums/{albumId}/tracks/{trackId}
├── trackId: String
├── albumId: String (referencia al álbum)
├── title: String (nombre de la canción)
├── description: String
├── audioUrl: String (enlace a la canción en Storage)
├── duration: Long (duración en milisegundos)
├── trackNumber: Int (número en el álbum, ej: 1, 2, 3...)
├── releaseDate: Long
├── playCount: Int
├── likes: List<String>
└── createdAt: Long
```

---

## 2. PODCASTS (Para Podcasters)

### Colección: `podcasts`
```
podcasts/{podcastId}
├── podcastId: String (único, generado)
├── creatorId: String (referencia al usuario/podcaster)
├── creatorUsername: String
├── creatorPhotoUrl: String
├── title: String (nombre del podcast)
├── description: String (descripción)
├── coverUrl: String (imagen de portada)
├── category: String (categoría: true crime, educativo, entretenimiento, etc)
├── language: String (idioma)
├── frequency: String (regularidad: semanal, diaria, mensual, etc)
├── releaseDate: Long (fecha de primer episodio)
├── totalSeasons: Int
├── totalEpisodes: Int
├── createdAt: Long
├── updatedAt: Long
├── isPublished: Boolean
└── likes: List<String>
```

### Colección anidada: `podcasts/{podcastId}/seasons`
```
podcasts/{podcastId}/seasons/{seasonId}
├── seasonId: String
├── podcastId: String
├── seasonNumber: Int (temporada 1, 2, 3...)
├── title: String (ej: "Temporada 1: El Misterio")
├── description: String
├── coverUrl: String (portada específica de la temporada)
├── releaseDate: Long
├── totalEpisodes: Int
├── createdAt: Long
└── isPublished: Boolean
```

### Colección anidada: `podcasts/{podcastId}/seasons/{seasonId}/episodes`
```
podcasts/{podcastId}/seasons/{seasonId}/episodes/{episodeId}
├── episodeId: String
├── podcastId: String
├── seasonId: String
├── episodeNumber: Int (número dentro de la temporada)
├── globalEpisodeNumber: Int (número global del podcast)
├── title: String (nombre del episodio)
├── description: String
├── audioUrl: String
├── duration: Long (duración en ms)
├── releaseDate: Long
├── playCount: Int
├── likes: List<String>
├── transcript: String (transcripción opcional)
└── createdAt: Long
```

---

## 3. USUARIO (Extensión)

### Campos adicionales en `users/{userId}`
```
├── userType: String (enum: "regular", "artist", "podcaster", "musician")
├── artistProfile: {
│   ├── bio: String
│   ├── genres: List<String>
│   ├── albums: Int (contador de álbumes)
│   └── followers: Int
├── podcastProfile: {
│   ├── bio: String
│   ├── categories: List<String>
│   ├── podcasts: Int (contador de podcasts)
│   └── followers: Int
└── verifiedArtist: Boolean (insignia de verificación)
```

---

## 4. COLECCIONES DE REFERENCIA (Para búsquedas rápidas)

### Colección: `userContent` (caché para búsquedas rápidas)
```
userContent/{userId}
├── albums: List<{albumId, title, coverUrl}>
├── podcasts: List<{podcastId, title, coverUrl}>
└── lastUpdated: Long
```

---

## 5. FIRESTORE RULES NECESARIAS

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Álbumes
    match /albums/{albumId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.artistId;
      
      match /tracks/{trackId} {
        allow read: if true;
        allow create: if request.auth != null && get(/databases/$(database)/documents/albums/$(albumId)).data.artistId == request.auth.uid;
        allow update, delete: if request.auth != null && get(/databases/$(database)/documents/albums/$(albumId)).data.artistId == request.auth.uid;
      }
    }
    
    // Podcasts
    match /podcasts/{podcastId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.creatorId;
      
      match /seasons/{seasonId} {
        allow read: if true;
        allow create, update, delete: if request.auth != null && get(/databases/$(database)/documents/podcasts/$(podcastId)).data.creatorId == request.auth.uid;
        
        match /episodes/{episodeId} {
          allow read: if true;
          allow create, update, delete: if request.auth != null && get(/databases/$(database)/documents/podcasts/$(podcastId)).data.creatorId == request.auth.uid;
        }
      }
    }
  }
}
```

---

## 6. STORAGE PATHS

```
gs://bucket/
├── albums/{artistId}/{albumId}/
│   ├── cover.jpg
│   └── tracks/{trackId}.mp3
├── podcasts/{creatorId}/{podcastId}/
│   ├── cover.jpg
│   ├── seasons/{seasonId}/
│   │   ├── cover.jpg
│   │   └── episodes/{episodeId}.mp3
```

---

## 7. MODELOS KOTLIN SUGERIDOS

```kotlin
// Álbum
data class Album(
    val albumId: String = "",
    val artistId: String = "",
    val artistUsername: String = "",
    val artistPhotoUrl: String = "",
    val title: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val genre: String = "",
    val releaseDate: Long = 0,
    val totalTracks: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false,
    val likes: List<String> = emptyList()
)

data class Track(
    val trackId: String = "",
    val albumId: String = "",
    val title: String = "",
    val description: String = "",
    val audioUrl: String = "",
    val duration: Long = 0,
    val trackNumber: Int = 0,
    val releaseDate: Long = 0,
    val playCount: Int = 0,
    val likes: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

// Podcast
data class Podcast(
    val podcastId: String = "",
    val creatorId: String = "",
    val creatorUsername: String = "",
    val creatorPhotoUrl: String = "",
    val title: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val category: String = "",
    val language: String = "es",
    val frequency: String = "",
    val releaseDate: Long = 0,
    val totalSeasons: Int = 0,
    val totalEpisodes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false,
    val likes: List<String> = emptyList()
)

data class Season(
    val seasonId: String = "",
    val podcastId: String = "",
    val seasonNumber: Int = 0,
    val title: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val releaseDate: Long = 0,
    val totalEpisodes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false
)

data class Episode(
    val episodeId: String = "",
    val podcastId: String = "",
    val seasonId: String = "",
    val episodeNumber: Int = 0,
    val globalEpisodeNumber: Int = 0,
    val title: String = "",
    val description: String = "",
    val audioUrl: String = "",
    val duration: Long = 0,
    val releaseDate: Long = 0,
    val playCount: Int = 0,
    val likes: List<String> = emptyList(),
    val transcript: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

---

## 8. FLUJO DE LA APP

### Para el ícono de música en MainTopBar:
1. Al presionar el ícono → Abre una pantalla `ArtistContentScreen`
2. Muestra:
   - Álbumes del artista actual (si es músico)
   - Podcasts del artista actual (si es podcaster)
   - Feed de contenido de artistas seguidos
3. Al seleccionar un álbum → Muestra `AlbumDetailScreen` con todas las canciones
4. Al seleccionar un podcast → Muestra `PodcastDetailScreen` con temporadas y episodios
5. Al seleccionar un episodio/canción → Reproduce con el reproductor

---

## 9. BÚSQUEDAS Y FILTROS

Se pueden agregar índices en Firestore para:
- Buscar álbumes por género
- Buscar podcasts por categoría
- Buscar por artista/creador
- Ordenar por popularidad (likes, playCount)
- Filtrar por fecha de lanzamiento


