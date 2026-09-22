# Auth Server

Servidor de autorización OAuth 2.1/OIDC construido con Spring Boot 3.5 y Java 21. También funciona como Resource Server y emite JWT firmados para los servicios que consumen la plataforma.

## Requisitos

- Java 21
- PostgreSQL 14 o superior
- Maven Wrapper incluido (`./mvnw`)
- Docker, opcional para ejecutar la imagen

## Configuración local

El perfil `dev` usa estos valores por defecto:

| Propiedad | Valor |
| --- | --- |
| Base de datos | `db_test` |
| Usuario | `postgres` |
| Contraseña | `admin` |
| Puerto HTTP | `8080` |

Crea la base de datos antes de iniciar la aplicación:

```sql
CREATE DATABASE db_test;
```

La contraseña indicada corresponde únicamente a la configuración local existente en `application-dev.properties`. Para otros entornos, usa variables de entorno y no incluyas credenciales en el repositorio.

## Ejecución

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

La aplicación queda disponible en `http://localhost:8080/authServices`.

Flyway ejecuta las migraciones de `src/main/resources/db/migration` y Hibernate valida el esquema existente. El usuario inicial de desarrollo es `admin`; cambia las credenciales antes de usar este perfil fuera de un entorno local.

## Endpoints principales

Todos los endpoints usan el contexto `/authServices`:

| Endpoint | Uso |
| --- | --- |
| `/login` | Formulario de inicio de sesión |
| `/.well-known/openid-configuration` | Metadatos del proveedor OIDC |
| `/oauth2/authorize` | Flujo Authorization Code con PKCE |
| `/oauth2/token` | Emisión y renovación de tokens |
| `/oauth2/jwks` | Claves públicas para validar JWT |
| `/userinfo` | Información del usuario autenticado |
| `/actuator/health` | Estado de salud de la aplicación |

Ejemplo de comprobación:

```bash
curl http://localhost:8080/authServices/actuator/health
curl http://localhost:8080/authServices/.well-known/openid-configuration
```

## Clientes OAuth2

Los clientes se registran o actualizan al iniciar la aplicación:

| Cliente | Flujo | Características |
| --- | --- | --- |
| `MSC-WEB` | Authorization Code y Refresh Token | PKCE obligatorio; redirecciones locales y Postman |
| `MSC-WS` | Client Credentials | Scopes `openid`, `profile`, `email` |
| `MSA-WS` | Client Credentials | Scopes `openid`, `profile`, `email` |

Los secretos de los clientes de servicio se definen en `SecurityConstants` y deben gestionarse como secretos de despliegue, nunca como valores compartidos en documentación o código público.

## Perfiles y variables de entorno

### Producción

Inicia con:

```bash
java -jar target/auth-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

El perfil `prod` requiere:

| Variable | Descripción |
| --- | --- |
| `DATABASE_URL` | URL JDBC de PostgreSQL |
| `DATABASE_USERNAME` | Usuario de PostgreSQL |
| `DATABASE_PASSWORD` | Contraseña de PostgreSQL |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` | URL pública del issuer |
| `SERVER_PORT` | Puerto HTTP, opcional; por defecto `8080` |

El issuer debe incluir el contexto `/authServices` cuando la aplicación se publique con la configuración predeterminada, por ejemplo `https://auth.example.com/authServices`.

### Pruebas

El perfil `test` toma la conexión desde `SERVER_PORT`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` y `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`. En la configuración de pruebas el esquema se crea y elimina automáticamente.

## Pruebas y empaquetado

```bash
./mvnw test
./mvnw clean package
```

El artefacto generado se encuentra en `target/auth-server-0.0.1-SNAPSHOT.jar`.

## Docker

El `Dockerfile` espera que el JAR ya exista en `target/`:

```bash
./mvnw clean package -DskipTests
docker build -t auth-server .
docker run --rm -p 8080:8080 \
	-e SPRING_PROFILES_ACTIVE=prod \
	-e DATABASE_URL='jdbc:postgresql://host.docker.internal:5432/db_test' \
	-e DATABASE_USERNAME=postgres \
	-e DATABASE_PASSWORD=admin \
	-e SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:8080/authServices \
	auth-server
```

En producción reemplaza los valores de ejemplo y gestiona las credenciales mediante el mecanismo de secretos de tu plataforma.

## Estructura relevante

- `src/main/java`: configuración, seguridad, controladores, servicios y persistencia.
- `src/main/resources/db/migration`: migraciones Flyway para PostgreSQL.
- `src/main/resources/templates/login.html`: pantalla de inicio de sesión.
- `src/main/resources/application-*.properties`: configuración por perfil.
- `src/test`: pruebas automatizadas y migración compatible con H2.
