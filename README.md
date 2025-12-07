# heyPudú - Red Social de Audio

## Autor del Proyecto
Angelo Millan

## Enlace de Jira del proyecto
heypudu.atlassian.net

## Descripción General

heyPudú es una aplicación Android moderna enfocada en el compartir de contenido de audio. Los usuarios pueden grabar, subir y compartir publicaciones de audio con una comunidad de oyentes. La aplicación ofrece autenticación de usuarios, gestión de perfiles, reproducción de audio e interacción social.

## Información del Proyecto

- **Plataforma**: Android (Nativa)
- **Lenguaje**: Kotlin
- **Framework de UI**: Jetpack Compose
- **SDK Mínimo**: 26
- **SDK Objetivo**: 36
- **SDK de Compilación**: 36
- **Versión Java**: 11
- **Versión Actual**: 0.2

## Características Principales

### Autenticación y Gestión de Usuarios
- Registro e inicio de sesión basado en correo electrónico mediante Firebase Auth
- Sistema de verificación de correo electrónico
- Creación segura de perfil de usuario
- Persistencia de sesión de usuario
- Edición de perfil con subida de fotos
- Funcionalidad de seguimiento/dejar de seguir

### Gestión de Contenido de Audio
- Funcionalidad de grabación de audio con integración de micrófono
- Carga de audio a Firebase Storage
- Reproducción de audio con control de progreso
- Soporte para múltiples formatos de audio (MP4A)
- Almacenamiento en caché de audio para mejorar el rendimiento
- Controles de reproducción, pausa y búsqueda

### Características Sociales
- Creación y compartir de publicaciones de audio
- Sistema de "Pudús" (me gusta/like)
- Feed de publicaciones con desplazamiento infinito
- Visualización de perfiles de usuario
- Sistema de seguimiento de usuarios
- Rastreo de engagement en publicaciones
- Monitoreo de conteo de reproduciones

### Interfaz de Usuario
- Implementación de Material Design 3
- Jetpack Compose para UI declarativa
- Fondos con gradientes animados
- Interfaz de perfil basada en pestañas
- Navegación con cajón modal
- Diseños responsivos

### Gestión de Perfil
- Fotos de perfil de usuario con almacenamiento en la nube
- Personalización de nombre de usuario
- Estado de verificación de correo electrónico
- Contador de publicaciones
- Gestión de seguidores/Siguiendo
- Gestión de lanzamientos/álbumes

## Estructura Técnica

### Stack Tecnológico

**Servicios de Firebase**
- Firebase Analytics
- Firebase Authentication
- Firebase Firestore
- Firebase Storage

**Arquitectura Android**
- Jetpack Compose
- Material Design 3
- Navigation Component
- Lifecycle y ViewModel
- Room Database

**Librerías Kotlin**
- Coroutines
- Flow
- Serialization

### Patrón de Arquitectura

El proyecto implementa el patrón **MVVM (Model-View-ViewModel)**:

- **Model**: Modelos de datos en la capa de datos
- **View**: Pantallas composables en Jetpack Compose
- **ViewModel**: Gestión de estado en ViewModels

Además utiliza el patrón **Repository** como fuente única de verdad para operaciones de datos.

## Base de Datos

### Colecciones de Firestore

**users/**
- Email del usuario
- Nombre de usuario
- URL de foto de perfil
- Estado de verificación de correo
- Lista de seguidores y usuarios que sigue
- Fecha de creación

**posts/**
- ID del autor
- Título y descripción
- URL del audio
- Duración del audio
- Contador de reproducciones
- Lista de usuarios que dieron me gusta
- Comentarios
- Fecha de creación

**albums/**
- ID del artista
- Título y descripción
- URL de portada
- Lista de publicaciones
- Fecha de creación

### Base de Datos Local (Room)

**CachedPost** - Almacenamiento de contenido offline
- Información del post
- Ruta local del archivo de audio
- Datos del autor
- Métricas de engagement

## Flujo de la Aplicación

### Flujo de Autenticación
1. Usuario inicia la aplicación
2. Se muestra pantalla de bienvenida
3. El usuario se registra o inicia sesión
4. Verificación de correo electrónico
5. Creación del perfil
6. Acceso al feed principal

### Flujo de Creación de Contenido
1. Usuario toca el botón de grabar
2. Se verifica el permiso del micrófono
3. Se abre la interfaz de grabación de audio
4. Se graba el mensaje de audio
5. Se agrega título y descripción
6. Se sube a Firebase Storage y Firestore
7. La publicación aparece en el feed

### Flujo de Reproducción
1. Usuario ve una publicación en el feed
2. Toca el botón de reproducción
3. El audio se descarga (si no está en caché)
4. Se incrementa el contador de reproducciones
5. El usuario puede buscar, pausar y reanudar
6. Se utiliza audio en caché para reproducciones posteriores

## Requisitos del Sistema

### Dependencias
- Android Studio (versión más reciente)
- JDK 11 o superior
- Gradle 8.0 o superior

### Permisos Requeridos
- `INTERNET`: Conectividad de red para operaciones de Firebase
- `RECORD_AUDIO`: Funcionalidad de grabación de audio
- `CAMERA`: Captura de foto de perfil
- `READ_EXTERNAL_STORAGE`: Acceso a la galería

## Instalación y Ejecución

### Pasos de Configuración

1. Clonar el repositorio
2. Abrir en Android Studio
3. Asegurar que `local.properties` contiene la ruta válida del SDK
4. Colocar `google-services.json` en el directorio `app/`
5. Sincronizar dependencias de Gradle

### Compilación

```bash
./gradlew build          # Compilar APK de debug
./gradlew assembleDebug  # Ensamblar APK de debug
```

### Instalación en Dispositivo

1. Conectar dispositivo Android (API 26+)
2. Instalar usando Android Studio o ejecutar:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Problemas Conocidos y Limitaciones

- Soporte de formato de audio limitado a MP4A
- Características de álbumes en desarrollo
- Sistema de comentarios en implementación
- Mensajería directa no disponible

## Estado de Desarrollo

**Versión Actual**: 0.2
**Estado**: En desarrollo activo

### Características Completadas
- Autenticación de usuario y gestión de perfil
- Grabación y reproducción de audio
- Creación y compartir de publicaciones
- Características sociales básicas (seguir, me gusta)
- Perfiles de usuario con capacidad de edición

### En Progreso
- Refinamiento de interfaz de gestión de álbumes
- Controles de reproducción de audio mejorados
- Implementación del sistema de comentarios
- Notificaciones push

### Características Planeadas
- Mensajería directa entre usuarios
- Funcionalidad de búsqueda avanzada
- Creación y compartir de listas de reproducción
- Algoritmo de recomendación
- Soporte para series de podcasts

## Seguridad

### Reglas de Firebase

Las operaciones en Firebase están protegidas mediante reglas de seguridad que garantizan:

- Los datos del usuario solo pueden ser leídos por usuarios autenticados
- Cada usuario solo puede modificar su propio perfil
- Las publicaciones de audio son accesibles para todos los usuarios autenticados
- Solo el autor puede eliminar sus propias publicaciones

### Datos Sensibles

- Tokens de autenticación gestionados por Firebase
- `google-services.json` para configuración
- Almacenamiento seguro de preferencias de usuario

## Testing

### Framework de Testing

El proyecto implementa pruebas unitarias automatizadas con las siguientes librerías:

- **JUnit 4**: Framework principal para pruebas unitarias
- **Google Truth**: Librería de aserciones fluida que proporciona mensajes de error más claros y legibles

### Estructura de Pruebas

Las pruebas se organizan en el directorio `src/test/`:

- **RetrofitClientTest**: Validación de la configuración del cliente HTTP y endpoints
- **TestDataFactoryTest**: Pruebas de creación de datos de prueba para modelos (Post, Comment, Album, Podcast)
- **NewsApiServiceTest**: Pruebas del servicio de integración con NewsAPI

### Ejecución de Pruebas

```bash
# Ejecutar todas las pruebas unitarias
./gradlew test
```

### Cobertura Actual

- Validación de configuración de Retrofit
- Pruebas de modelos de datos (Post, Comment, Album, Podcast)
- Verificación de endpoints de API (NewsAPI)

---

## Integración con NewsAPI

### Descripción

heyPudú utiliza **NewsAPI** para enriquecer el feed de usuarios con contenido informativo relevante. Esta integración permite mostrar artículos de noticias contextuales que complementan el contenido de audio de la comunidad. Para tener una UI congruente es que las noticias son mostradas como tarjetas, con un botón de "Leer más" que redirige al navegador del dispositivo.

### Configuración

**Endpoint Base**: `https://newsapi.org/`

- Plan gratuito limitado a 100 solicitudes por día
- Algunas fuentes pueden tener restricciones de acceso

---

## Licencia

Este proyecto es de propiedad privada.

## Información de Versión

### Versión 0.2 (Actual)
- Lanzamiento de nueva version con algunos cambios
- APK disponible, firmada
- Funcionalidad principal de compartir audio
- Sistema de autenticación de usuario
- Implementación de características sociales
- Optimización de almacenamiento en caché de audio
- Integración con NewsAPI
- Suite de pruebas unitarias con JUnit y Truth

- App firmada en Android Studio
<img width="1600" height="900" alt="2" src="https://github.com/user-attachments/assets/ac4c2959-906e-4c7c-8b22-ce8507b252fc" />


---

Primera versión del README: Noviembre 2025
Segunda version del README: Diciembre 2025
