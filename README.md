# 🏭 Reto: Gestión y Localización del Material del Taller de Informática

**🎓 C.F.G.S. Desarrollo de Aplicaciones Web - DAW1**
**🏫 IES Miguel Herrero Pereda | 📅 Curso 2025/2026**
**👥 Equipo 4**

---

## 📚 Índice

- [📝 Descripción del proyecto](#-descripción-del-proyecto)
- [👨‍💻 Miembros del equipo](#-miembros-del-equipo)
- [🎯 Resultados de aprendizaje por módulo](#-resultados-de-aprendizaje-por-módulo)
- [🛠️ Tecnologías utilizadas](#️-tecnologías-utilizadas)
- [🗄️ Base de datos](#️-base-de-datos)
- [☕ Aplicación Java](#-aplicación-java)
- [🌐 Sitio web de visualización](#-sitio-web-de-visualización)
- [🖥️ Infraestructura y despliegue](#️-infraestructura-y-despliegue)
- [📦 Otras entregas](#-otras-entregas)
- [⭐ Valoración del trabajo](#-valoración-del-trabajo)
- [💡 Mejoras propuestas](#-mejoras-propuestas)
- [📖 Bibliografía y webgrafía](#-bibliografía-y-webgrafía)

---

## 📝 Descripción del proyecto

El reto **"Gestión y Localización del Material del Taller de Informática"** consiste en desarrollar una aplicación de escritorio para inventariar, gestionar y localizar rápidamente el material disponible en el taller de Informática del IES Miguel Herrero Pereda.

El sistema se compone de:

- 🖥️ Una **aplicación de escritorio en Java** con interfaz Swing, perfiles de Administrador y Profesor, y generación de informes.
- 🗄️ Una **base de datos MySQL** alojada en un servidor virtualizado (MV1) con el inventario del taller.
- 🌐 Un **sitio web de visualización gráfica** alojado en un servidor virtualizado (MV2) que muestra la distribución física del taller.
- 🔒 **Infraestructura de dos capas** desplegada sobre máquinas virtuales con Ubuntu Server en VirtualBox.

### 📦 Módulos integrados

| Módulo | Abreviatura |
|--------|-------------|
| Programación | PROG |
| Bases de Datos | BD |
| Lenguajes de Marcas | LMSGI |
| Sistemas Informáticos | SI |
| Entornos de Desarrollo | ED |
| Itinerario Personal para la Empleabilidad I | IPE I |

---

## 👨‍💻 Miembros del equipo

| 👤 Nombre | 🎯 Rol principal |
|-----------|-----------------|
| Alejandro Fraile Muñoz | 🧭 Coordinador |
| Juan Camilo Vallejo Reyes | 🗄️ Portavoz |
| David Gómez Santiago | ☕ Secretario |
| Angel Gonzalez Cortavitarte | 🌐 Desarrollador Web |
| Mario zamanillo | ------- |

---

## 🎯 Resultados de aprendizaje por módulo

### 🗄️ Bases de Datos
- ✅ Diseño lógico normalizado (3FN) con diagrama E/R y modelo relacional.
- ✅ Implementación en MySQL con restricciones de integridad referencial.
- ✅ Script de creación con datos de prueba.
- ✅ Disparadores (triggers) para automatización de operaciones.

### ☕ Programación
- ✅ Aplicación de escritorio Java con Swing y NetBeans.
- ✅ Programación orientada a objetos con patrón Singleton y DAO.
- ✅ Conexión a base de datos MySQL mediante JDBC.
- ✅ Gestión de inventario (CRUD) con perfiles Administrador y Profesor.
- ✅ Importación y exportación de datos en CSV/Excel.
- ✅ Generación de informes en PDF/Excel.
- ✅ Código documentado con JavaDoc.

### 🌐 Lenguajes de Marcas (LMSGI)
- ✅ Sitio web estático con HTML5, CSS3 y JavaScript.
- ✅ Plano visual interactivo del taller con resaltado de ubicaciones.
- ✅ Diseño responsive adaptable a distintos dispositivos.
- ✅ Validación de código HTML y CSS.

### 💻 Sistemas Informáticos
- ✅ Despliegue de MV1 (Servidor de Datos): Ubuntu Server + MySQL + UFW.
- ✅ Despliegue de MV2 (Servidor de Servicios): Ubuntu Server + Apache + SSH/SFTP + UFW.
- ✅ Arquitectura de red de dos capas con red interna y adaptador puente.
- ✅ Documentación completa: Guía de despliegue y Manual de usuario.
- ✅ Comparativa y elección de licencia de software (Apache 2.0).

### 📊 Entornos de Desarrollo
- ✅ Diagrama de clases y diagrama de casos de uso.
- ✅ Control de versiones colaborativo con Git y GitHub.
- ✅ Gestión de tareas con GitHub Projects e Issues.

### 💼 IPE I
- ✅ Mapa profesional del equipo y zona de desarrollo próximo.
- ✅ Análisis de competencias personales y sociales.
- ✅ Entorno personal de aprendizaje vinculado al reto.
- ✅ Reflexión sobre empleabilidad y aprendizaje permanente.

---

## 🛠️ Tecnologías utilizadas

| 🔧 Tecnología | 📝 Uso en el proyecto |
|--------------|----------------------|
| **NetBeans** | IDE para el desarrollo de la aplicación Java con Swing |
| **Visual Studio Code** | IDE para desarrollo web (HTML, CSS, JS) y documentación Markdown |
| **Oracle VirtualBox** | Virtualización de los servidores Linux (MV1 y MV2) |
| **Ubuntu Server 24.04 LTS** | Sistema operativo de los servidores |
| **MySQL Server** | Motor de base de datos del inventario (MV1) |
| **MySQL Workbench** | Diseño y administración de la base de datos |
| **Apache HTTP Server** | Servidor web para el sitio de visualización (MV2) |
| **OpenSSH / SFTP** | Administración remota y transferencia de archivos |
| **UFW** | Firewall para restricción de accesos |
| **GitHub** | Repositorio colaborativo y control de versiones |
| **GitHub Desktop** | Cliente gráfico para gestión del repositorio |
| **GitHub Projects** | Tablero Kanban para seguimiento de tareas |
| **Draw.io**//**Diagrams.io** | Creación de diagramas E/R, clases, casos de uso y red |
| **Microsoft Teams** | Comunicación del equipo y entrega de tareas(ovas e Ipe I) |
| **Microsoft Word** | Elaboración de documentación |

---

## 🗄️ Base de datos

La base de datos **`Taller_Informatica`** almacena toda la información del inventario del taller. Incluye tablas para materiales, categorías, estados, ubicaciones, préstamos, usuarios y registro de movimientos.

- 📊 [Diagrama Entidad/Relación](/Documentacion/BD/Entidad_Relacion_Reto.png)
- 🔗 [Modelo Relacional](/Documentacion/BD/Modelo_Relacional.png)
- 📜 [Script SQL de creación](/Documentacion/BD/Script_Final.sql)

---

## ☕ Aplicación Java

Aplicación de escritorio con interfaz Swing para la gestión del inventario. Dispone de dos perfiles:

- 🔑 **Administrador**: acceso completo (CRUD, gestión de usuarios, todos los informes, importación/exportación).
- 👨‍🏫 **Profesor**: consulta del inventario, búsqueda y filtrado, localización visual y generación de listados.

- 📊 [Diagrama de clases](/Documentacion/ED/entrega-diagrama-clases)
- 👤 [Diagrama de casos de uso](/Documentacion/ED/Entrega-Diagrama-CasosdeUso)
- 💻 [Código fuente de la aplicación](/Documentacion/Proyecto_Java/RETA_2026_TALLER_INFORMATICA)

---

## 🌐 Sitio web de visualización

Sitio web con **HTML5**, **CSS3** y **JavaScript** que permite:

- 🗺️ Ver un plano interactivo del taller con distribución de armarios y baldas.
- 🔍 Buscar componentes y ver su ubicación exacta resaltada en el plano.
- 📋 Consultar información detallada de cada material (nombre, descripción, estado).
- 📱 Visualización adaptable a distintos dispositivos (responsive).
- 🖼️ Galería de imágenes reales de las zonas del taller.

---

## 🖥️ Infraestructura y despliegue

La infraestructura sigue una **arquitectura de dos capas** desplegada sobre máquinas virtuales en VirtualBox:

| 🖥️ Servidor | ⚙️ Función | 🐧 SO | 🔌 Servicios |
|------------|-----------|------|-------------|
| **MV1** | Servidor de Datos | Ubuntu Server | MySQL (puerto 3306) |
| **MV2** | Servidor de Servicios | Ubuntu Server | Apache (80), SSH/SFTP (22) |

### 🔐 Seguridad
- 🔥 Firewall UFW configurado en ambos servidores con principio de mínimo privilegio.
- 👤 Usuarios de MySQL con acceso limitado por IP y base de datos.
- 🔒 Conexiones remotas cifradas mediante SSH y SFTP.

### 📄 Documentación
- 🌐 [Diagrama de arquitectura de red](/Documentacion/SI/diagrama_red.png)
- 📘 [Guía de despliegue completa](/Documentacion/SI/guia_despliegue/GuiaDespliegue.pdf)
- 📕 [Manual de usuario](/Documentacion/SI/manual_usuario/Manuel_de_usuario.pdf)
- 📜 [Licencia del proyecto (Apache 2.0)](/LICENSE)

---

## 📦 Otras entregas

### 💼 IPE I
- 🗺️ [Mapa profesional del equipo](/Documentacion/IPE1/Equipo4-IPEI-Tarea1-MapaProfesional.pdf)

### 📝 Documentación del proyecto
- 📓 Cuadernos de trabajo diarios en GitHub Issues
- 📋 Tablero de tareas en GitHub Projects
- 📖 Wiki del repositorio (Contrato de equipo y Roles)

---

## ⭐ Valoración del trabajo

A lo largo del reto hemos aprendido a coordinar un proyecto multidisciplinar que integra múltiples módulos del ciclo. Destacamos como logros:

- 🔧 La correcta configuración de la infraestructura de servidores con VirtualBox, aplicando el principio de mínimo privilegio mediante UFW y usuarios limitados.
- 🗄️ La implementación de una base de datos normalizada con todas las categorías de material exigidas.
- ☕ El desarrollo de una aplicación Java funcional con patrones de diseño profesionales (Singleton, DAO).
- 🌐 La integración de un sitio web de visualización con resaltado dinámico de ubicaciones.
- 🤝 El trabajo colaborativo mediante Git, GitHub Projects y cuadernos de trabajo diarios.

---

## 💡 Mejoras propuestas

- 🔒 Implementar HTTPS en el servidor web mediante certificados SSL/TLS.
- 🤖 Automatizar el despliegue de las máquinas virtuales mediante Vagrant o Docker Compose.
- 🔐 Añadir autenticación de usuarios en el sitio web.
- 🔌 Desarrollar una API REST para separar completamente la capa de presentación de la de datos.
- 🧪 Mejorar la cobertura de pruebas unitarias en la aplicación Java.

---

## 📖 Bibliografía y webgrafía

- 🐧 Documentación oficial de Ubuntu Server: https://ubuntu.com/server/docs
- 🗄️ Documentación oficial de MySQL: https://dev.mysql.com/doc/
- 🌐 Documentación oficial de Apache HTTP Server: https://httpd.apache.org/docs/
- 📦 Documentación oficial de VirtualBox: https://www.virtualbox.org/manual/
- 🔥 Guía de UFW: https://help.ubuntu.com/community/UFW
- 🔐 Documentación de OpenSSH: https://www.openssh.com/manual.html
- 🔗 Documentación de Netplan: https://netplan.readthedocs.io/
- 📊 Diagrams.net: https://www.diagrams.net/
- 📜 Comparativa de licencias: https://choosealicense.com/licenses/

---

[⬆ Volver al índice](#-índice)
