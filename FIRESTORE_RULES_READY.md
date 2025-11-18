# ✅ Reglas de Firestore - BASADAS EN TUS REGLAS ACTUALES

## 📝 Tus reglas actuales + Álbumes y Podcasts

Copia exactamente esto y pégalo en Firebase Console:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Publicaciones de Audio
    match /posts/{postId} {
      allow read, write: if request.auth != null;
    }

    // Usuarios
    match /users/{userId} {
      allow read: if request.auth != null;
      allow create, delete: if request.auth != null && request.auth.uid == userId;
      allow update: if request.auth != null && (
        request.auth.uid == userId ||
        request.resource.data.diff(resource.data).affectedKeys().hasOnly(['following', 'followers'])
      );
    }

    // Álbumes - NUEVO
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

    // Podcasts - NUEVO
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

    // Caché de posts - NUEVO
    match /cachedPosts/{postId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 🎯 Pasos para actualizar:

1. **Abre Firebase Console**: https://console.firebase.google.com
2. **Selecciona tu proyecto**: "hey-pudu" o como se llame
3. **Ve a Firestore Database** en el menú izquierdo
4. **Haz clic en la pestaña "Rules"** (al lado de "Data")
5. **Selecciona TODO el texto** (Ctrl+A o Cmd+A)
6. **Borra todo**
7. **Pega las reglas de arriba** (Ctrl+V o Cmd+V)
8. **Haz clic en "Publish"** (botón azul arriba a la derecha)

---

## ✅ Cuando veas esto, ya está listo:

```
✓ Rules updated successfully
```

---

## 🔄 Después:

Cierra la app completamente y abre nuevamente, luego:

1. Ve a tu perfil
2. Haz clic en el botón de "+" o "Crear Lanzamiento"
3. Selecciona "Álbum Musical"
4. Llena los campos y crea el álbum

**Debe funcionar sin errores de permisos** ✓

---

## 📋 Qué se agregó:

| Colección | Lectura | Creación | Edición | Eliminación |
|-----------|---------|----------|---------|------------|
| **albums** | Todos | Solo autenticados | Solo artista | Solo artista |
| **albums/tracks** | Todos | Solo artista | Solo artista | Solo artista |
| **podcasts** | Todos | Solo autenticados | Solo creador | Solo creador |
| **podcasts/seasons** | Todos | Solo creador | Solo creador | Solo creador |
| **podcasts/episodes** | Todos | Solo creador | Solo creador | Solo creador |
| **cachedPosts** | Solo autenticados | Solo autenticados | Solo autenticados | Solo autenticados |

---

## ⚠️ Si aún no funciona después de publicar:

1. Espera 30 segundos (los cambios pueden tardar)
2. Recarga la app completamente
3. Intenta nuevamente
4. Si sigue sin funcionar, ve a Firebase Console → Firestore → Logs y busca el error exacto


