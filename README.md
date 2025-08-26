# Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**


# 🛠️ Configuración del Entorno Local

Este documento describe cómo levantar el proyecto en un entorno local de desarrollo. Incluye detalles sobre las dependencias, configuración de base de datos, variables de entorno, y otros requisitos necesarios.

---

## 📦 Requisitos Previos

- [Java 21](https://www.oracle.com/java/technologies/javase-downloads.html) (Se puede utilizar desde IntelliJ IDEA)
- [Docker & Docker Compose](https://www.docker.com/products/docker-desktop)
- [Postman](https://www.postman.com/downloads/) (opcional para pruebas API)
- [JMeter](https://jmeter.apache.org/download_jmeter.cgi) (opcional para pruebas de rendimiento)
- [VisualVM](https://visualvm.github.io/download.html) (opcional para monitorizar y visualizar información detallada del rendimiento de la JVM)
- IDE recomendado: IntelliJ IDEA

---

## 📁 Clonar el Repositorio

```bash
git clone https://github.com/afavendanor/pragma-ms-solicitudes.git
cd pragma-ms-solicitudes
```

## 🐘 Base de Datos

Se hace uso de una base de datos en PostgreSQL y PGAdmin4 para administrar y mantener la base de datos.

## 📁 Carpeta Local Enviroment

Este directorio contiene las herramientas y configuraciones necesarias para pruebas, ejecución local y automatización del entorno.

## 📁 Estructura

```bash
/java_reactivo/
├── local_enviroment/
│   ├── docker-compose.yml
│   ├── script_create_table.sql
│   ├── java_reactivo.postman_collection.json
│   └── java_reactivo_test_plan.jmx
```

### 📄 `docker-compose.yml`

Archivo de configuración que define los servicios necesarios para ejecutar el proyecto localmente, como bases de datos, servidores u otros contenedores auxiliares.

```bash
docker-compose up -d
```
> Asegúrate de tener Docker y Docker Compose instalados antes de ejecutarlo.


### 📄 `script_create_table.sql`

Script SQL para crear las tablas necesarias en la base de datos local.

Ejecutar en tu cliente SQL o en el contenedor:

```bash
psql -U tu_usuario -d tu_base_de_datos -f script_create_table.sql
```

### 📄 `java_reactivo.postman_collection.json`

Colección de Postman con todos los endpoints REST expuestos por el backend Java Reactivo. Úsala para validar el funcionamiento de los servicios una vez estén levantados.

#### Uso:
1. Abrir Postman
2. Importar la colección (java_reactivo.postman_collection.json)
3. Ejecutar las peticiones sobre http://localhost:8080 (o el puerto configurado)


### 📄 `java_reactivo_test_plan.jmx`

Archivo de prueba de carga para Apache JMeter, diseñado para simular múltiples usuarios y medir el rendimiento del servicio.

#### Uso:
1. Abrir JMeter
2. Cargar el archivo java_reactivo_test_plan.jmx
3. Configurar los parámetros si es necesario (puerto, número de usuarios, etc.)
4. Ejecutar la prueba y analizar los resultados

### 📝 Notas

- Verifica que los puertos usados no estén ocupados.
- Puedes crear un archivo .env si quieres gestionar variables externas para docker-compose.yml.
- Asegúrate de aplicar el script SQL antes de probar los endpoints si la DB está vacía.

## 👨‍💻 Autor y Contribuciones

Este proyecto es mantenido por **afavendanor**.

¿Tienes sugerencias, mejoras o encontraste un error? No dudes en contribuir o abrir un issue.

- 🛠 Para errores o bugs, abre un [issue](https://github.com/afavendanor/pragma-ms-solicitudes/issues)
- ✨ Para sugerencias o mejoras, crea un [pull request](https://github.com/afavendanor/pragma-ms-solicitudes/pulls)
- 💬 Para soporte o dudas, contáctame a través del canal interno.

> Las contribuciones son siempre bienvenidas.