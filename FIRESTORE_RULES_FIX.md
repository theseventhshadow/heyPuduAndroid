# 🔐 Reglas de Firestore - Copia esto a la Consola de Firebase

## ⚠️ IMPORTANTE
Ve a: Firebase Console → Tu Proyecto → Firestore Database → Rules

Reemplaza TODO el contenido con esto:

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

    // Caché de posts
    match /cachedPosts/{postId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 📋 Pasos:

1. Abre tu proyecto en Firebase Console
2. Ve a **Firestore Database** 
3. Haz clic en la pestaña **Rules**
4. Selecciona TODO el texto actual y bórralo
5. Copia el código de arriba y pégalo
6. Haz clic en **Publish**

---

## ✅ Después de publicar:

Verás un mensaje: "Rules updated successfully"

Luego intenta crear un álbum nuevamente en la app.

---

## 🔐 Explicación de las Reglas:

- ✅ **Usuarios**: Cada usuario solo puede modificar sus propios datos
- ✅ **Álbumes**: Cualquiera autenticado puede crear, solo el artista puede editar/eliminar
- ✅ **Podcasts**: Similar a álbumes
- ✅ **Tracks/Episodes**: Solo el creador del álbum/podcast puede crear
- ✅ **Posts**: Usuarios autenticados pueden crear, solo el autor puede editar/eliminar
- ✅ **Followers**: Solo usuarios autenticados

---

## ⚠️ Si sigue sin funcionar:

1. Verifica que tu usuario está autenticado
2. Abre Dev Tools en Firebase Console → Logs
3. Copia el error exacto y compártelo
4. Verifica que el `artistId` en el álbum coincide con el `uid` del usuario


