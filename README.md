# 📱 MODUS

Este es un proyecto desarrollado con **Kotlin Multiplatform** usando **Android Studio**, el cual permite compartir código entre plataformas como Android y JVM (u otras si están habilitadas). El objetivo del proyecto es desarrollar una aplicación multiplataforma que integre un **módulo de pagos periódicos**, **juegos de bingo en vivo** y un **catálogo virtual de ropa**, todo ello conectado a una base de datos alojada en **Supabase**. La aplicación está diseñada para fomentar el hábito del ahorro mediante la gamificación, permitiendo a los usuarios participar en dinámicas semanales y canjear recompensas, aprovechando la sincronización en tiempo real y los servicios backend provistos por Supabase.

---

## 🧰 Tecnologías utilizadas

Este proyecto utiliza una amplia variedad de tecnologías y librerías modernas, tanto del ecosistema de Kotlin Multiplatform como del entorno Android. A continuación se detallan las principales:

- **Kotlin Multiplatform (KMP)** – Para compartir lógica común entre Android, Web, JVM y más.
- **Android Studio** – IDE principal para desarrollo Android y KMP.
- **Gradle (Kotlin DSL)** – Herramienta de construcción y gestión de dependencias.
- **Jetpack Compose Multiplatform** – Framework moderno y declarativo de UI, utilizado tanto en Android como en otras plataformas.
- **Jetpack Compose Material 3** – Implementación del nuevo diseño Material You.
- **Compose Material Core (Wear OS)** – Soporte para interfaces adaptadas a dispositivos wearables.
- **Compose Voyager** – Navegación multiplataforma con soporte para Screen Models y transiciones.
- **Kotlin Coroutines** – Manejo asíncrono de tareas y concurrencia.
- **Kotlinx Serialization** – Serialización JSON multiplataforma.
- **SQLDelight** – ORM multiplataforma para acceso a bases de datos SQLite.
- **Supabase-Kotlin** – Librería multiplataforma para autenticación, base de datos (PostgREST), almacenamiento y comunicación en tiempo real.
- **Ktor** – Cliente HTTP multiplataforma para llamadas a APIs REST.
- **Coil Compose** – Carga de imágenes optimizada para Compose.
- **Kamel** – Carga y renderizado de imágenes multiplataforma.
- **Google Maps Compose** – Visualización y manipulación de mapas en Compose.
- **Google Play Services Location** – Acceso a la ubicación del dispositivo.
- **Lifecycle ViewModel y Runtime Compose** – Gestión del ciclo de vida y estado.
- **UCrop** – Herramienta para recorte de imágenes.
- **YouTube Player (Android)** – Integración de reproducción de videos y Chromecast.
- **Android Browser Helper** – Soporte para Trusted Web Activities y PWA desde Android.

---

### 📁 Estructura del proyecto

```text
project-root/
├── composeApp/                      # Módulo multiplataforma (Kotlin common)
│   ├── src/
|   |   ├── androidMain/             # Código específico para Android
│   |   ├── commonMain/              # Código común para todas las plataformas
|   |   |   ├── composeResources/    # Recursos multiplataforma
|   |   |   ├── kotlin/              # Núcleo de la aplicación
|   |   |   ├── sqldelight/          # Cofiguración del cache local
|   |   └──wasmJsMain/               # Código específico para WASM
│   └── build.gradle.kts             # Configuración del proyecto
```

---

### 🚀 Instalación

#### 1. Requisitos previos

Asegúrate de tener instalado lo siguiente:

- [Android Studio](https://developer.android.com/studio) **Giraffe o superior**
- **JDK 17**
- [Kotlin Multiplatform Plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- Git (para clonar el repositorio)

#### 2. Clonar el repositorio

```bash
git clone https://github.com/JeffersonH0501/PG_MODUS.git
cd PG_MODUS
```

#### 3. Abrir en Android Studio

 - Abre Android Studio.
 - Selecciona "Open Project" y elige la carpeta clonada.
 - Espera a que Gradle sincronice el proyecto (puede tardar algunos minutos).

#### 4. Ejecutar la app Android

 - Conecta un dispositivo físico o usa un emulador.
 - En la barra superior, selecciona el módulo androidApp y elige una variante de ejecución (app).
 - Haz clic en Run o presiona Shift + F10.




