# 📋 GUÍA VISUAL: Crear los 3 Índices de Firestore

## 🎯 Necesitas crear 3 índices:

1. ✅ **Albums** - para obtener álbumes por artista
2. ✅ **Podcasts** - para obtener podcasts por creador
3. ✅ **Public Releases** - para obtener lanzamientos públicos

---

## PASO 1: Abre Firebase Console

```
https://console.firebase.google.com
```

Selecciona tu proyecto "hey-pudu"

---

## PASO 2: Ve a Firestore Database

En el menú izquierdo:
```
Firestore Database
```

---

## PASO 3: Abre Indexes

En el menú izquierdo, debajo de "Data":
```
Indexes
```

O en la pestaña superior, junto a "Data"

---

## 🎵 ÍNDICE 1: Albums por Artista

### Haz clic en "+ Create Index"

**Campo Collection:**
```
albums
```

**Campo 1:**
```
Field Name: artistId
Type: Ascending
```

**Campo 2:**
```
Field Name: createdAt
Type: Descending
```

**Haz clic en "Create Index"**

---

## 🎙️ ÍNDICE 2: Podcasts por Creador

### Haz clic en "+ Create Index" (nuevamente)

**Campo Collection:**
```
podcasts
```

**Campo 1:**
```
Field Name: creatorId
Type: Ascending
```

**Campo 2:**
```
Field Name: createdAt
Type: Descending
```

**Haz clic en "Create Index"**

---

## 🌍 ÍNDICE 3: Lanzamientos Públicos

### Haz clic en "+ Create Index" (nuevamente)

**Campo Collection:**
```
albums
```

**Campo 1:**
```
Field Name: isPublished
Type: Ascending
```

**Campo 2:**
```
Field Name: createdAt
Type: Descending
```

**Haz clic en "Create Index"**

---

## ⏳ Esperando...

Verás algo así:

```
Index Name                Status
albums_artistId_createdAt Building    (espera)
podcasts_creatorId_crea... Building   (espera)
albums_isPublished_creat... Building   (espera)
```

Una vez que todos digan **Enabled** ✅:

1. Cierra la app completamente
2. Abre nuevamente
3. Ve a Perfil → Lanzamientos

**¡Debería funcionar!** ✓

---

## ❌ Si ves "Error: The query requires an index" nuevamente

Esto significa que los índices aún están creándose. **Espera más tiempo** (puede tardar hasta 10 minutos).

---

## 💡 Qué están haciendo estos índices:

- 🎵 **Índice 1**: Busca rápidamente todos los álbumes de un artista, ordenados por fecha (más reciente primero)
- 🎙️ **Índice 2**: Busca rápidamente todos los podcasts de un creador, ordenados por fecha (más reciente primero)
- 🌍 **Índice 3**: Busca rápidamente todos los lanzamientos públicos (álbumes y podcasts), ordenados por fecha

Sin estos índices, Firestore no sabe cómo hacer estas búsquedas eficientemente.


