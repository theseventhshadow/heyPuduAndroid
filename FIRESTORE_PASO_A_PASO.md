# 📋 PASO A PASO: Actualizar Reglas de Firestore

## 1️⃣ Copia este código completo:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /posts/{postId} {
      allow read, write: if request.auth != null;
    }

    match /users/{userId} {
      allow read: if request.auth != null;
      allow create, delete: if request.auth != null && request.auth.uid == userId;
      allow update: if request.auth != null && (
        request.auth.uid == userId ||
        request.resource.data.diff(resource.data).affectedKeys().hasOnly(['following', 'followers'])
      );
    }

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

    match /cachedPosts/{postId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 2️⃣ Ve a Firebase Console:

```
https://console.firebase.google.com
```

---

## 3️⃣ Selecciona tu proyecto

![Busca "hey-pudu" o tu proyecto]

---

## 4️⃣ Ve a Firestore Database

En el menú izquierdo:
```
Firestore Database
```

---

## 5️⃣ Haz clic en "Rules"

Verás dos pestañas:
- Data
- **Rules** ← Haz clic aquí

---

## 6️⃣ Selecciona TODO el código actual

```
Ctrl+A  (Windows/Linux)
Cmd+A   (Mac)
```

---

## 7️⃣ Borra TODO

```
Delete o Backspace
```

---

## 8️⃣ Pega el código nuevo

```
Ctrl+V  (Windows/Linux)
Cmd+V   (Mac)
```

---

## 9️⃣ Haz clic en "Publish"

Botón azul arriba a la derecha

---

## ✅ Verás este mensaje:

```
✓ Rules updated successfully
```

---

## 🔄 Después en la App:

1. **Cierra completamente** la aplicación
2. **Abre nuevamente**
3. Ve a **Perfil**
4. Busca **Lanzamientos** o el botón **"+"**
5. Selecciona **"Álbum Musical"** o **"Podcast"**
6. **Crea tu primer álbum/podcast**

---

## ✨ ¡Listo!

Ahora deberías poder crear álbumes y podcasts sin errores de permisos.

Si aún da error, intenta esto:
1. Recarga la página de Firebase Console (F5)
2. Verifica que el código se puso correctamente
3. Espera 1 minuto completo antes de reintentar en la app


