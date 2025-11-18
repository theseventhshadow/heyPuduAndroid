# 🎵 SOLUCIÓN: Error al Crear Álbumes - PERMISSION_DENIED

## ❌ El Problema:

```
E  Error al crear álbum: PERMISSION_DENIED: Missing or insufficient permissions.
```

## ✅ La Solución:

Las reglas de Firestore no incluyen `albums` ni `podcasts`. Solo tienen `posts` y `users`.

---

## 🔧 QUÉ HACER:

### Paso 1: Abre Firebase Console
```
https://console.firebase.google.com → Tu Proyecto
```

### Paso 2: Ve a Firestore Database Rules
```
Firestore Database → Rules (pestaña)
```

### Paso 3: Reemplaza las reglas

**ELIMINA TODO y pega esto:**

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

### Paso 4: Publish
Botón azul "Publish" en la esquina superior derecha

### Paso 5: Espera y Reinicia
- Espera 30 segundos
- Cierra completamente la app
- Abre nuevamente
- Intenta crear un álbum

---

## 📱 En la App:

1. Ve a tu **Perfil**
2. Busca la pestaña de **Lanzamientos**
3. Haz clic en **"+"** o **"Crear Lanzamiento"**
4. Selecciona **"Álbum Musical"** o **"Podcast"**
5. Llena los campos
6. Selecciona una portada 📸
7. Haz clic en **"Crear"**

**¡Debería funcionar ahora!** ✓

---

## 🎯 Resumen de Cambios:

| Antes | Después |
|-------|---------|
| ❌ posts, users | ✅ posts, users, albums, podcasts, cachedPosts |
| ❌ Solo publicaciones de audio | ✅ Álbumes, Podcasts, Tracks, Episodes |
| ❌ Sin estructura de colecciones anidadas | ✅ Estructura completa y validada |

---

## ⚠️ Notas Importantes:

1. **Las reglas se actualizan al instante** en Firestore, pero puede haber un retraso de unos segundos
2. **Cada usuario solo puede editar sus propios álbumes/podcasts** (basado en `artistId` o `creatorId`)
3. **Cualquiera puede leer** álbumes y podcasts (lectura pública)
4. **Solo usuarios autenticados pueden crear** álbumes y podcasts
5. **Los tracks y episodes** solo pueden ser creados por el dueño del álbum/podcast

---

## 🆘 Si aún da error:

1. Abre Firefox o Chrome Dev Tools (F12)
2. Ve a **Network** o **Console**
3. Intenta crear el álbum nuevamente
4. Busca el error exacto
5. Comparte la captura de pantalla

---

## ✨ Una vez funcione:

¡Felicidades! Tu sistema de lanzamientos está listo:
- 📀 Crear y gestionar álbumes
- 🎙️ Crear y gestionar podcasts
- 📸 Subir portadas a Firebase Storage
- 📊 Ver estadísticas de cada lanzamiento


