# 🎰 BET4GRAD - Sistema de Gestión Estudiantil Gamificado

<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white" alt="Swing">
</div>

## 📋 Descripción

**BET4GRAD** es un innovador sistema de gestión estudiantil con temática de casino que gamifica la experiencia universitaria. Los estudiantes ganan puntos "Apunab" participando en actividades académicas y extracurriculares, creando un ambiente competitivo y motivador para el aprendizaje.

### 🎯 Características Principales

- 🎰 **Temática de Casino**: Interfaz atractiva inspirada en juegos de casino
- 💰 **Sistema de Puntos Apunab**: Moneda virtual para incentivar participación
- 🏆 **Gamificación**: Ranking estudiantil y sistema de recompensas
- 🎮 **Juegos Integrados**: Ruleta para apostar puntos Apunab
- 📊 **Dashboard Administrativo**: Gestión completa de usuarios y actividades
- 🌐 **Base de Datos en la Nube**: MySQL hospedado en Clever Cloud

## 🚀 Tecnologías Utilizadas

### Frontend
- **Java Swing**: Interfaz gráfica de usuario
- **FlatLaf**: Look and Feel moderno (tema oscuro)
- **Custom UI Components**: Componentes personalizados con efectos hover

### Backend
- **Java 11+**: Lenguaje de programación principal
- **JDBC**: Conectividad con base de datos
- **BCrypt**: Encriptación de contraseñas
- **JavaMail**: Sistema de recuperación de contraseñas

### Base de Datos
- **MySQL**: Sistema de gestión de base de datos
- **Clever Cloud**: Hosting en la nube

### Herramientas de Desarrollo
- **Maven**: Gestión de dependencias y build
- **NetBeans**: IDE de desarrollo
- **Git**: Control de versiones

## 📁 Estructura del Proyecto

```
src/main/java/com/unab/
├── Login/              # Sistema de autenticación
├── Home/               # Página principal con navegación
├── Admin/              # Dashboard administrativo
├── Profile/            # Perfil de usuario con estadísticas
├── Actividades/        # Gestión de eventos y actividades
├── Noticias/           # Sistema de noticias universitarias
├── Mapa/               # Ubicación de la universidad
├── Profesores/         # Directorio de profesores
├── Horario/            # Visualización de horarios académicos
├── Ranking/            # Ranking estudiantil por puntos
├── Ruleta/             # Juego de casino integrado
├── Tutorias/           # Sistema de tutorías académicas
├── database/           # Clases de acceso a datos
└── utils/              # Utilidades y helpers
```

## 🛠️ Instalación y Configuración

### Prerrequisitos

- Java 11 o superior
- Maven 3.6+
- Conexión a internet (para base de datos en la nube)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/bet4grad.git
   cd bet4grad
   ```

2. **Instalar dependencias**
   ```bash
   mvn clean install
   ```

3. **Configurar base de datos**
   - La aplicación está configurada para usar MySQL en Clever Cloud
   - Las credenciales están en `DatabaseConnection.java`
   - No requiere configuración adicional

4. **Ejecutar la aplicación**
   ```bash
   mvn exec:java -Dexec.mainClass="com.unab.Login.LoginFrame"
   ```

   O desde tu IDE:
   - Abrir el proyecto en NetBeans/IntelliJ/Eclipse
   - Ejecutar `LoginFrame.java`

## 👥 Usuarios del Sistema

### 🎓 Estudiantes
- **Registro y Login**: Crear cuenta y acceder al sistema
- **Perfil Personal**: Ver estadísticas detalladas y progreso académico
- **Actividades**: Registrarse en eventos y ganar puntos Apunab
- **Juegos**: Participar en la ruleta para apostar puntos
- **Información**: Acceder a noticias, horarios y directorio de profesores

### 👨‍💼 Administradores
- **Gestión de Usuarios**: CRUD completo de estudiantes
- **Gestión de Actividades**: Crear, editar y eliminar eventos
- **Control de Puntos**: Modificar puntos Apunab de usuarios
- **Estadísticas**: Dashboard con métricas del sistema

## 🎮 Funcionalidades Detalladas

### 🏠 Home - Navegación Principal
- Interfaz estilo casino con 8 módulos principales
- Navegación intuitiva con efectos visuales
- Acceso rápido a todas las funcionalidades

### 📅 Sistema de Actividades
- Registro automático en eventos universitarios
- Recompensas en puntos Apunab por participación
- Visualización de eventos con imágenes y descripciones
- Sistema de registro/desregistro dinámico

### 🎰 Ruleta Casino
- Juego de ruleta europea (37 números: 0-36)
- Múltiples tipos de apuesta:
  - Número específico (35x)
  - Rojo/Negro (1x)
  - Par/Impar (1x)
  - Primera/Segunda mitad (1x)
- Animaciones fluidas y efectos visuales
- Integración completa con sistema de puntos

### 🏆 Sistema de Ranking
- Top 15 estudiantes con más puntos Apunab
- Colores especiales para podio (oro, plata, bronce)
- Resaltado del usuario actual en el ranking
- Información sobre cómo ganar más puntos

### 👤 Perfil de Usuario
- Estadísticas detalladas de puntos Apunab:
  - Promedios semanales, mensuales, semestrales y anuales
  - Total de eventos participados
  - Progreso hacia graduación (100,000 puntos mínimos)
  - Estimaciones de tiempo para graduación
- Grid de 12 tarjetas informativas
- Botón de acceso a administración (solo admins)

### 📊 Dashboard Administrativo
- Gestión completa de usuarios con tabla interactiva
- Estadísticas en tiempo real del sistema
- CRUD de actividades con interfaz intuitiva
- Control granular de permisos y roles

## 🗄️ Base de Datos

### Tablas Principales

- **Usuarios**: Información de estudiantes y administradores
- **Roles**: Sistema de permisos (USER, ADMIN)
- **Eventos**: Actividades y eventos universitarios
- **EventoRegistros**: Relación usuarios-eventos
- **PasswordRecoveryTokens**: Tokens para recuperación de contraseñas
- **Tutorias**: Sistema de tutorías académicas

### Características de la BD
- Hospedada en Clever Cloud (MySQL)
- Conexiones seguras y encriptadas
- Respaldos automáticos
- Alta disponibilidad

## 🎨 Diseño y UX

### Paleta de Colores
- **Fondo Principal**: `#1A2A4A` (Azul oscuro)
- **Header**: `#0D1B33` (Azul más oscuro)
- **Acentos**: `#FFC107` (Dorado)
- **Paneles**: `#2A3C58` (Azul medio)

### Características de Diseño
- Tema oscuro moderno con FlatLaf
- Efectos hover en todos los elementos interactivos
- Iconos emoji para mejor UX
- Diseño responsive dentro de las ventanas
- Animaciones suaves en transiciones

## 📈 Métricas y Estadísticas

### Sistema de Puntos Apunab
- **Graduación**: Mínimo 100,000 puntos requeridos
- **Actividades**: Puntos variables según importancia del evento
- **Ruleta**: Sistema de apuestas con riesgo/recompensa
- **Ranking**: Competencia sana entre estudiantes

### Estadísticas Disponibles
- Promedios temporales de puntos ganados
- Participación en eventos
- Progreso hacia graduación
- Ranking relativo entre estudiantes

## 🔐 Seguridad

- **Encriptación de Contraseñas**: BCrypt con salt
- **Recuperación Segura**: Tokens temporales para reset de contraseña
- **Validación de Entrada**: Sanitización de datos de usuario
- **Control de Acceso**: Sistema de roles y permisos
- **Conexión Segura**: SSL/TLS para base de datos

## 🚀 Roadmap y Mejoras Futuras

### Corto Plazo
- [ ] Aplicación móvil (Android/iOS)
- [ ] Sistema de notificaciones push
- [ ] Chat en tiempo real entre estudiantes
- [ ] Más juegos de casino (Blackjack, Poker)

### Mediano Plazo
- [ ] Migración a arquitectura web (Spring Boot + React)
- [ ] API REST para integraciones externas
- [ ] Dashboard analytics avanzado
- [ ] Sistema de calificaciones integrado

### Largo Plazo
- [ ] Arquitectura de microservicios
- [ ] Inteligencia artificial para recomendaciones
- [ ] Sistema multi-universidad




## 📝 Licencia

Este proyecto está Copyright © 2025 Daniel Lozano

Todos los derechos reservados.

Este código fuente, incluyendo todos los archivos y recursos relacionados, es propiedad del autor. No se permite su copia, distribución, modificación ni reutilización sin autorización explícita por escrito del titular de derechos.

Este repositorio es de uso personal y educativo únicamente.

Contacto: danielflozanos25@gmail.com

## 👨‍💻 Autor

**Daniel Lozano**
- GitHub: [Malcova34](https://github.com/Malcova34)
- Email: daniefllozanos25@gmail.com
- LinkedIn: [Tu Perfil](https://www.linkedin.com/in/daniel-fernando-lozano-silva-6889a52b3/)

## 🙏 Agradecimientos

- Universidad Autónoma de Bucaramanga (UNAB)
- Clever Cloud por el hosting de base de datos
- Comunidad de desarrolladores Java
- FlatLaf por el excelente Look and Feel

## 📸 Screenshots

### Pantalla de Login
![Login]

### Dashboard Principal
![Home]

### Sistema de Actividades
![Actividades]

### Ruleta Casino
![Ruleta]

### Panel Administrativo
![Admin]

---

<div align="center">
  <p>Hecho con ❤️ para la comunidad estudiantil de la UNAB</p>
  <p>🎰 <strong>BET4GRAD</strong> - Donde el aprendizaje se encuentra con la diversión 🎓</p>
</div> 