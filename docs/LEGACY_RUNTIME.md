# Runtime legacy restaurado

Este documento describe la baseline ejecutable de AppNotas restaurada en 2026.
No es una guía de modernización ni sustituye la configuración segura de cada
entorno.

## Arquitectura

AppNotas conserva la arquitectura Java EE original:

- JSF y PrimeFaces para las vistas `*.htech`.
- Controladores CDI con scopes de vista y sesión.
- EJB stateless como capa de persistencia.
- JPA/EclipseLink con unidad de persistencia `notaPU`.
- Datasource JTA publicado como `jdbc/note`.
- MySQL como base de datos.

El artefacto es un WAR y su context root es
`/AppNotas-1.0-SNAPSHOT`.

## Modelo persistente

En una base vacía, la configuración JPA existente crea las cinco tablas del
modelo:

- `categoria`
- `nota`
- `persona`
- `telefono`
- `usuario`

La base debe existir antes del despliegue. El esquema se genera desde las
entidades JPA; este proyecto no contiene un sistema de migraciones y no debe
usarse el mecanismo de generación automática como sustituto de migraciones en
un entorno productivo con datos.

## Preparación portable del entorno

### Requisitos

- JDK 8 configurado como Java activo.
- Maven 3.9.16 o una versión compatible validada previamente.
- GlassFish 4.1.2.
- MySQL 5.7.44.
- MySQL Connector/J 5.1.49 disponible para el dominio GlassFish.

### Base y datasource

1. Crear `appnotas_legacy` en `<DB_HOST>:<DB_PORT>`.
2. Crear `<DB_USER>` con permisos limitados a esa base.
3. Copiar el driver JDBC compatible en `<GLASSFISH_DOMAIN>/lib` y reiniciar el
   dominio.
4. Crear un pool JDBC con:
   - datasource class: `com.mysql.jdbc.jdbc2.optional.MysqlDataSource`
   - server: `<DB_HOST>`
   - port: `<DB_PORT>`
   - database: `appnotas_legacy`
   - user: `<DB_USER>`
   - password: obtenida desde el mecanismo seguro del entorno
5. Publicar el pool como `jdbc/note`.
6. Verificar el ping del pool antes de desplegar.

No se deben escribir credenciales reales en archivos versionados ni en líneas
de comando conservadas por scripts o historiales.

### Build y despliegue

```bash
mvn clean package
```

Desplegar el WAR generado mediante la consola administrativa o una herramienta
equivalente del servidor:

```text
target/AppNotas-1.0-SNAPSHOT.war
```

URL base esperada:

```text
http://<APP_HOST>:<APP_PORT>/AppNotas-1.0-SNAPSHOT/
```

## Funcionalidad y seguridad

### Identidad y sesión

- El registro público crea únicamente usuarios Operario (`O`) activos.
- La contraseña se transforma con PBKDF2 antes de persistirse.
- El login rechaza credenciales inválidas y usuarios inactivos.
- La sesión conserva código de usuario, nombre de usuario y tipo; no conserva
  la entidad Persona completa.
- El logout invalida la sesión HTTP.

### Autorización

- `/protegido/*` requiere sesión autenticada.
- Las respuestas protegidas incluyen headers para evitar caché.
- `/protegido/admin/*` exige además `tipo=A` en servidor.
- Un Operario autenticado recibe una respuesta 403 al solicitar una ruta Admin.

### Categorías

Los usuarios autenticados pueden listar, crear y editar categorías. El campo
`estado` se utiliza para activar o desactivar; no se inventó hard-delete.

### Notas

Los usuarios autenticados pueden crear, listar, editar y eliminar sus propias
notas. El servidor:

- obtiene el código de Persona desde la sesión;
- resuelve la Persona y la categoría activa mediante JPA;
- nunca acepta el propietario desde el formulario;
- filtra listados por Persona;
- vuelve a comprobar ownership al consultar, editar o eliminar.

`comentarioAdmin` y `valorizacion` permanecen sin lógica funcional. El código
original no define significado, rango, estados ni flujo de revisión suficiente
para implementarlos de forma responsable.

## Verificación mínima

Después de desplegar sobre una base vacía:

1. Confirmar la creación de las cinco tablas.
2. Registrar un Operario desde la UI.
3. Confirmar que la credencial persistida usa PBKDF2 sin exponer su valor.
4. Validar login, sesión, acceso protegido y logout.
5. Crear una categoría activa.
6. Crear una nota y confirmar que pertenece a la Persona autenticada.
7. Confirmar que una solicitud anónima a `/protegido/*` es bloqueada.

Los datos de smoke test y los backups de base son locales y nunca deben
versionarse.

## Línea histórica

El tag `legacy-2020` y la rama `master` preservan la baseline original. El
trabajo de recuperación se realiza en `rescue/legacy-runtime`.

La versión candidata prevista es `v1.0.0-legacy-restored`; el tag y el release
deben crearse únicamente después de aprobar una revisión de release separada.
