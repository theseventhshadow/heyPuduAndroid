# Implementación: Sistema de Creación de Lanzamientos con Portadas

## ✅ Cambios Realizados

### 1. Componente CreateReleaseBottomSheet (MEJORADO)
**Archivo:** `ui/components/CreateReleaseBottomSheetV2.kt`

Características ampliadas:
- ModalBottomSheet que permite crear álbumes o podcasts
- **Selector de portada integrado** 📸
- Dos opciones iniciales: "Álbum Musical" 📀 y "Podcast" 🎙️
- **Para Álbumes:**
  - Título
  - Descripción
  - **Portada (seleccionar de galería)**
  - Género (Rock, Pop, Jazz, etc.)
- **Para Podcasts:**
  - Título
  - Descripción
  - **Portada (seleccionar de galería)**
  - Categoría (Educativo, Entretenimiento, etc.)
  - Frecuencia (Semanal, Mensual, etc.)
  - Selector de idioma (Español/Inglés)
- Validación de título requerido
- Botón de crear con indicador de carga
- Manejo de errores mejorado
- La portada se sube automáticamente a Firebase Storage

### 2. Componente ReleaseCoverPicker (NUEVO)
**Archivo:** `ui/components/ReleaseCoverPicker.kt`

Funcionalidades:
- Botón para seleccionar imagen de la galería
- Interfaz amigable con fondo rosa
- Indicador de carga mientras se selecciona imagen
- Retorna la URI seleccionada

### 3. Funciones en UserRepository (MEJORADO)
**Archivo:** `data/UserRepository.kt`

Se añadió 1 función nueva:

```kotlin
// Subir portada de lanzamiento
suspend fun uploadReleaseCover(imageUri: Uri, releaseId: String, type: String): String
```

Esta función:
- Sube la imagen a Firebase Storage
- Ubicación: `release_covers/{type}/{releaseId}.jpg`
- Retorna la URL descargable de la imagen
- Maneja automáticamente el tipo (albums/podcasts)

### 4. Mejoras en ProfileViewModel
**Archivo:** `features/profile/viewmodel/ProfileViewModel.kt`

El ViewModel ahora carga automáticamente los lanzamientos cuando se abre un perfil:
```kotlin
loadUserAlbums(userId)
loadUserPodcasts(userId)
```

### 5. Pestaña "Lanzamientos" en ProfileScreen (MEJORADO)
**Archivo:** `features/profile/ui/ProfileScreen.kt`

Mejoras:
- Mostrar portadas de álbumes y podcasts
- Mejor presentación visual
- Cargar automáticamente desde Firebase
- Actualizarse en tiempo real

---

## 🔄 Flujo Completo de Creación

1. Usuario en ProfileScreen → Presiona "+ Crear Lanzamiento"
2. Se abre CreateReleaseBottomSheet
3. Usuario elige Álbum o Podcast
4. Completa:
   - Título
   - Descripción
   - **Selecciona portada desde galería** ← NUEVO
   - Datos específicos (género/categoría/etc)
5. Presiona "Crear Álbum/Podcast"
6. Sistema ejecuta en paralelo:
   - Sube portada a Firebase Storage
   - Obtiene URL de descarga
   - Guarda álbum/podcast en Firestore con URL
7. Se cierra BottomSheet
8. Lista se actualiza automáticamente con la portada

---

## 📊 Ubicación en Firebase Storage

**Estructura:**
```
gs://hey--pudu.firebasestorage.app/
└── release_covers/
    ├── albums/
    │   ├── {albumId}.jpg
    │   ├── {albumId}.jpg
    │   └── ...
    └── podcasts/
        ├── {podcastId}.jpg
        ├── {podcastId}.jpg
        └── ...
```

**Ejemplo:**
```
gs://hey--pudu.firebasestorage.app/release_covers/albums/1730829234567.jpg
```

---

## 💾 Datos en Firebase

### Colección: albums
```json
{
  "albumId": "1730829234567",
  "artistId": "uid",
  "artistUsername": "nombre",
  "artistPhotoUrl": "url",
  "title": "Mi Primer Álbum",
  "description": "Descripción del álbum",
  "coverUrl": "https://firebasestorage.googleapis.com/.../albums/1730829234567.jpg",
  "genre": "Rock",
  "releaseDate": 1730829234567,
  "totalTracks": 0,
  "createdAt": 1730829234567,
  "updatedAt": 1730829234567,
  "isPublished": true,
  "likes": []
}
```

### Colección: podcasts
```json
{
  "podcastId": "1730829234567",
  "creatorId": "uid",
  "creatorUsername": "nombre",
  "creatorPhotoUrl": "url",
  "title": "Mi Primer Podcast",
  "description": "Descripción del podcast",
  "coverUrl": "https://firebasestorage.googleapis.com/.../podcasts/1730829234567.jpg",
  "category": "Educativo",
  "language": "es",
  "frequency": "Semanal",
  "releaseDate": 1730829234567,
  "totalSeasons": 1,
  "totalEpisodes": 0,
  "createdAt": 1730829234567,
  "updatedAt": 1730829234567,
  "isPublished": true,
  "likes": []
}
```

---

## 📋 Próximas Mejoras

1. **Editar portada**: Permitir cambiar la portada después de crear
2. **Previsualización**: Mostrar la portada seleccionada antes de crear
3. **Cortar/Redimensionar**: Permitir editar la imagen antes de subir
4. **Usar cámara**: Opción para tomar foto directamente
5. **Múltiples formatos**: Soportar PNG, WebP, etc.
6. **Optimización**: Comprimir automáticamente antes de subir
7. **Tracks/Episodios**: Poder añadir canciones o episodios después

---

## ✨ Notas Importantes

- ✅ Las portadas se suben a Firebase Storage automáticamente
- ✅ Las URLs son válidas y descargables
- ✅ La UI es responsive y amigable
- ✅ Manejo completo de errores
- ✅ Indicadores de carga visuales
- ✅ Funciona en tiempo real con Firestore
- ⚠️ El archivo antiguo `CreateReleaseBottomSheet.kt` debe eliminarse (usar `CreateReleaseBottomSheetV2.kt`)
- ⚠️ El archivo `CreateReleaseBottomSheetUpdated.kt` también debe eliminarse

---

## 🚀 Cómo Usar

En ProfileScreen, cuando se abre la pestaña "Lanzamientos" y estás en tu propio perfil:

1. Presiona "+ Crear Lanzamiento"
2. Elige Álbum o Podcast
3. Completa todos los campos
4. Presiona "Seleccionar de galería" para elegir portada
5. Presiona "Crear Álbum/Podcast"
6. ¡Listo! Tu lanzamiento está creado con portada




