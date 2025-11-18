# ✅ SOLUCIÓN RÁPIDA: Error de Índice Firestore

## 🎯 El Problema:

```
E  Error al obtener álbumes: FAILED_PRECONDITION: The query requires an index.
```

## 🔧 La Solución:

Necesitas crear **3 índices compuestos** en Firestore.

---

## 🚀 MÁS RÁPIDO - Opción Automática:

Cuando vuelvas a intentar cargar los álbumes en la app, Firebase te mostrará un link:

```
https://console.firebase.google.com/...
```

**Solo haz clic en el link y Firebase creará el índice automáticamente.**

---

## 📋 Si prefieres hacerlo manualmente:

Sigue: **`CREAR_INDICES_PASO_A_PASO.md`** ← Lee este archivo

Te muestra exactamente dónde hacer clic y qué escribir en cada campo.

---

## ⏳ Después de crear los índices:

1. Espera 2-5 minutos (verifica que estén "Enabled" en Firestore Console)
2. Cierra la app completamente
3. Abre nuevamente
4. Ve a **Perfil → Lanzamientos**

**¡Debe funcionar! ✓**

---

## 🎯 Resumen:

| Índice | Colección | Campo 1 | Campo 2 | Estado |
|--------|-----------|---------|---------|--------|
| 1️⃣ | albums | artistId ↑ | createdAt ↓ | Building → Enabled |
| 2️⃣ | podcasts | creatorId ↑ | createdAt ↓ | Building → Enabled |
| 3️⃣ | albums | isPublished ↑ | createdAt ↓ | Building → Enabled |

---

## 📚 Más Información:

- **`FIRESTORE_INDEX_FIX.md`** - Explicación detallada
- **`CREAR_INDICES_PASO_A_PASO.md`** - Guía visual paso a paso


