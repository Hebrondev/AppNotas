# AppNotas

AppNotas es una aplicación web Java EE para registrar usuarios, administrar
categorías y gestionar notas personales. El proyecto fue creado originalmente
en 2020 y su runtime legacy fue restaurado y validado de forma controlada en
2026.

## Funcionalidades actuales

- Registro público de usuarios de tipo Operario (`O`).
- Login y logout con sesión HTTP.
- Contraseñas almacenadas mediante PBKDF2.
- Rechazo de usuarios inactivos.
- Protección de rutas bajo `/protegido/*` y prevención de caché.
- Separación de usuarios Admin (`A`) y Operario (`O`).
- Acceso a `/protegido/admin/*` autorizado únicamente para Admin.
- Creación, edición y activación/desactivación de categorías.
- Creación, listado, edición y eliminación de notas propias.
- Asociación de cada nota con la Persona autenticada y validación de ownership
  en servidor.

## Runtime validado

- Java 8
- Maven 3.9.16
- GlassFish 4.1.2
- MySQL 5.7.44
- Java EE 7, JSF, PrimeFaces 8, EJB y JPA/EclipseLink

Las versiones anteriores forman la baseline validada. Una modernización debe
realizarse en una fase separada.

## Build

Con Java 8 y Maven disponibles:

```bash
mvn clean package
```

El WAR se genera como:

```text
target/AppNotas-1.0-SNAPSHOT.war
```

## Configuración mínima

1. Crear una base MySQL vacía llamada `appnotas_legacy`.
2. Crear un usuario de base de datos con privilegios mínimos sobre esa base.
3. Instalar MySQL Connector/J 5.1.49 en el directorio de librerías del dominio
   GlassFish.
4. Configurar un connection pool de GlassFish contra `appnotas_legacy`.
5. Exponer ese pool mediante el recurso JNDI `jdbc/note`.
6. Desplegar `target/AppNotas-1.0-SNAPSHOT.war`.

La aplicación queda disponible en:

```text
http://<APP_HOST>:<APP_PORT>/AppNotas-1.0-SNAPSHOT/
```

Las credenciales de MySQL y la configuración específica de cada entorno deben
mantenerse fuera del repositorio. No se deben versionar contraseñas, backups ni
archivos de datos.

La guía técnica detallada está en
[docs/LEGACY_RUNTIME.md](docs/LEGACY_RUNTIME.md).

## Historia preservada

- `legacy-2020`: tag que preserva el commit original.
- `master`: baseline original sin modificar.
- `rescue/legacy-runtime`: restauración funcional y de seguridad.

## Alcance pendiente

El modelo original contiene `comentarioAdmin` y `valorizacion` en `Nota`, pero
no define su semántica, rango ni flujo funcional. No se implementó lógica para
esos campos con el fin de evitar inventar reglas de negocio.
