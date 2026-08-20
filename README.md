# InfoMensajes

Aplicación móvil de mensajería en tiempo real desarrollada en Kotlin, con arquitectura MVVM e integración completa con Firebase.

<!-- Agrega aquí 2-4 capturas de pantalla o un GIF corto de la app en uso -->
<!-- ![screenshot](ruta/a/tu/imagen.png) -->

## Funcionalidades

- Registro e inicio de sesión (correo y Google Sign-In)
- Perfiles de usuario
- Chats en tiempo real con estados de online / offline / escribiendo...
- Notificaciones push y contador de mensajes no leídos (badges)
- Envío y visualización de imágenes
- Eliminación de mensajes

## Stack técnico

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=black)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)

- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Backend:** Firebase (Firestore, Realtime Database, Authentication, Storage, Cloud Functions)
- **Base de datos local:** SQLite

## Arquitectura

La app sigue el patrón MVVM: las Views (Activities/Fragments) observan los ViewModels, que exponen el estado mediante LiveData, mientras los Repositories gestionan el acceso a Firestore, Realtime Database y Storage.

## Instalación

1. Clona el repositorio:
   \`\`\`bash
   git clone https://github.com/samuel-armas/Chat-Kotlin.git
   \`\`\`
2. Ábrelo en Android Studio.
3. Agrega tu propio archivo `google-services.json` (Firebase) dentro de `app/`.
4. Sincroniza Gradle y ejecuta en un emulador o dispositivo físico.

## Autor

Rogelio Samuel Armas García — [LinkedIn](https://www.linkedin.com/in/rogelio-samuel-armas-garc%C3%ADa-492885307/?skipRedirect=true)
