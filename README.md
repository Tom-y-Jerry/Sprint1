# Proyecto DACD - Sprint 1: Captura de Datos desde APIs Externas

Este proyecto forma parte del Sprint 1 de la asignatura **Desarrollo de Aplicaciones para Ciencia de Datos** del Grado en Ingeniería en Ciencia de Datos de la ULPGC.  
El objetivo es capturar información desde dos APIs externas, procesarla y almacenarla en una base de datos **SQLite** de forma periódica.

## Tecnologías utilizadas

- **Java 21**
- **SQLite**
- **OkHttp** y **Gson** para consumir y parsear las APIs
- **Maven** para la gestión del proyecto y dependencias
- **IntelliJ IDEA** como entorno de desarrollo

## Estructura del Proyecto

El proyecto está dividido en dos módulos:

### 1. TicketMaster Feeder
Consume datos de eventos en Madrid usando la API de TicketMaster:

- Extrae eventos con nombre, fecha y ciudad.
- Almacena la información en una tabla `eventos` en SQLite.
- Dos clases:
  - `TicketMasterAPI.java`: versión usando `HttpURLConnection`
  - `TicketMasterAPIFeeder.java`: versión moderna usando `OkHttp`

### 2. BlaBlaCar Feeder
Consume datos de paradas de autobuses usando la API de BlaBlaCar:

- Extrae información sobre paradas (id, nombre, coordenadas, dirección...).
- Almacena los datos en una tabla `stations` en SQLite.
- Dos clases:
  - `BlaBlaCarAPI.java`: versión con `HttpURLConnection`
  - `BlaBlaCarAPIFeeder.java`: versión con `OkHttp`

### 3. Configuración
- `ConfigReader.java` permite acceder a las claves API guardadas en `config.properties` para no exponerlas directamente en el código.

## Base de datos

Se usa una base de datos `data.db` con las siguientes tablas:

```sql
-- Para TicketMaster
CREATE TABLE IF NOT EXISTS eventos (
    id TEXT PRIMARY KEY,
    nombre TEXT,
    fecha TEXT,
    ciudad TEXT
);

-- Para BlaBlaCar
CREATE TABLE IF NOT EXISTS stations (
    id INTEGER PRIMARY KEY,
    carrier_id TEXT,
    short_name TEXT,
    long_name TEXT,
    time_zone TEXT,
    latitude REAL,
    longitude REAL,
    is_meta_gare BOOLEAN,
    address TEXT
);
```

## Configuración necesaria

Debes crear un archivo `config.properties` dentro de `resources/` con el siguiente contenido:

```
TICKETMASTER_API_KEY=TU_CLAVE_DE_API
BLABLACAR_API_KEY=TU_CLAVE_DE_API
```

## Ejecución

Desde IntelliJ o línea de comandos (si tienes Maven instalado):

```bash
mvn compile
mvn exec:java -Dexec.mainClass="es.ulpgc.dacd.TicketMasterAPIFeeder"
mvn exec:java -Dexec.mainClass="es.ulpgc.dacd.BlaBlaCarAPIFeeder"
```

## Estado del Proyecto

✅ Consumo correcto de las APIs  
✅ Persistencia en SQLite  
🔜 Pendiente: ejecución periódica con `ScheduledExecutorService`  
