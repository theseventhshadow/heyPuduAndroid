# 🔍 SOLUCIÓN: Error de Índice en Firestore

## ❌ El Problema:

```
FAILED_PRECONDITION: The query requires an index.
```

Firestore necesita **3 índices compuestos** para las consultas de lanzamientos:

1. **Albums**: `artistId` + `createdAt`
2. **Podcasts**: `creatorId` + `createdAt`
3. **Lanzamientos Públicos**: `isPublished` + `createdAt`

---

## ✅ LA SOLUCIÓN:

### Opción 1: Crear los Índices Automáticamente (Recomendado)

Cada vez que hagas una consulta desde la app, Firebase te mostrará un link:

```
https://console.firebase.google.com/v1/r/project/hey--pudu/firestore/indexes?create_composite=...
```

**Simplemente:**
1. Haz clic en el link
2. Haz clic en "Create Index"
3. Espera 2-5 minutos

---

### Opción 2: Crear Manualmente Todos los Índices

#### Índice 1: Albums

1. **Ve a Firebase Console → Firestore Database → Indexes**
2. **Haz clic en "Create Index"**
3. **Llena así:**
   - **Collection ID:** `albums`
   - **Campo 1:** `artistId` (Ascending ↑)
   - **Campo 2:** `createdAt` (Descending ↓)
4. **Haz clic en "Create Index"**

#### Índice 2: Podcasts

1. **Haz clic en "Create Index"** nuevamente
2. **Llena así:**
   - **Collection ID:** `podcasts`
   - **Campo 1:** `creatorId` (Ascending ↑)
   - **Campo 2:** `createdAt` (Descending ↓)
3. **Haz clic en "Create Index"**

#### Índice 3: Lanzamientos Públicos

1. **Haz clic en "Create Index"** nuevamente
2. **Llena así:**
   - **Collection ID:** `albums`
   - **Campo 1:** `isPublished` (Ascending ↑)
   - **Campo 2:** `createdAt` (Descending ↓)
3. **Haz clic en "Create Index"**

---

## ⏳ Esperar a que los índices se creen

Verás el estado:
- 🟡 `Building` → espera
- 🟢 `Enabled` → listo

Generalmente tarda **2-5 minutos**.

---

## 🔄 En la App:

1. **Espera a que todos los índices estén "Enabled"** ✅
2. **Cierra la app completamente**
3. **Abre nuevamente**
4. **Ve a Perfil → Lanzamientos**

**¡Ahora debería cargar los álbumes y podcasts!** ✓

---

## 📋 Tabla de Índices Necesarios:

| Colección | Campo 1 | Orden | Campo 2 | Orden | Uso |
|-----------|---------|-------|---------|-------|-----|
| **albums** | `artistId` | ↑ | `createdAt` | ↓ | Obtener álbumes del usuario |
| **podcasts** | `creatorId` | ↑ | `createdAt` | ↓ | Obtener podcasts del usuario |
| **albums** | `isPublished` | ↑ | `createdAt` | ↓ | Obtener lanzamientos públicos |

---

## 🆘 Troubleshooting:

### Si ves el error nuevamente:
1. Abre Firebase Console
2. Ve a **Firestore Database → Indexes**
3. Verifica que los 3 índices estén **Enabled** ✅
4. Si alguno sigue `Building`, espera más
5. Reinicia la app

### Si no aparece el botón "Create Index":
1. Abre: `https://console.firebase.google.com`
2. Selecciona tu proyecto "hey-pudu"
3. Ve a **Firestore Database**
4. En el menú izquierdo, busca **Indexes** (puede estar debajo de "Data")

### Si los índices tardan mucho:
- Esto es normal, Firebase puede tardar hasta 10 minutos en casos especiales
- No cierres la consola, solo deja que se complete
- Una vez esté "Enabled", funcionará inmediatamente




