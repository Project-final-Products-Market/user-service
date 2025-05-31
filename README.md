# User Service - Gestión de Usuarios 👤

Microservicio para la gestión completa de usuarios en el ecosistema Products Market.

## 📋 Descripción

El User Service es el núcleo de la gestión de usuarios, responsable de manejar perfiles, autenticación básica, búsquedas avanzadas y la integración con otros servicios del marketplace.

## 🛠️ Stack Tecnológico

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **MySQL 8.0**
- **Spring Cloud Netflix Eureka**
- **RestTemplate**
- **Lombok**
- **JUnit 5 & Mockito**

## ⚡ Funcionalidades Principales

### ✅ Gestión Completa de Usuarios
- Registro de nuevos usuarios
- Actualización de perfiles
- Eliminación segura de cuentas
- Validación robusta de datos

### 🔍 Sistema de Búsquedas
- **Por ID**: Búsqueda directa y rápida
- **Por Email**: Identificador único secundario
- **Por Nombre**: Búsqueda con coincidencias parciales
- **Estadísticas**: Conteo total de usuarios

### 🔗 Integración con Order Service
- Consulta de órdenes por usuario
- Manejo de fallos gracioso
- Respuestas resilientes

### 📊 Validaciones y Controles
- **Email único** en todo el sistema
- **Formato válido** de direcciones de correo
- **Límites de caracteres** en campos de texto
- **Datos obligatorios** verificados

## 🌐 Endpoints de la API

### Operaciones CRUD Básicas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/users` | Obtener todos los usuarios |
| `GET` | `/api/users/{id}` | Obtener usuario por ID |
| `POST` | `/api/users` | Crear nuevo usuario |
| `PUT` | `/api/users/{id}` | Actualizar usuario |
| `DELETE` | `/api/users/{id}` | Eliminar usuario |

### Endpoints Gateway (Puerto 8087)

| Método | Endpoint Gateway                       | Descripción |
|--------|----------------------------------------|-------------|
| `GET` | `http://localhost:8087/api/users`      | Obtener todos los usuarios |
| `GET` | `http://localhost:8087/api/users/{id}` | Obtener usuario por ID |
| `POST` | `http://localhost:8087/api/users`      | Crear nuevo usuario |
| `PUT` | `http://localhost:8087/api/users/{id}` | Actualizar usuario |

### Endpoints Especializados

| Método | Endpoint Gateway                                     | Descripción |
|--------|------------------------------------------------------|-------------|
| `GET` | `http://localhost:8087/api/users/email/{email}`      | Buscar por email |
| `GET` | `http://localhost:8087/api/users/search?name={name}` | Buscar por nombre |
| `GET` | `http://localhost:8087/api/users/{id}/orders`        | Órdenes del usuario |
| `GET` | `http://localhost:8087/api/users/stats/total`        | Total de usuarios |

### Endpoints Administrativos

| Método | Endpoint Gateway                              | Descripción |
|--------|-----------------------------------------------|-------------|
| `GET` | `http://localhost:8087/api/users/stats/total` | Estadísticas de usuarios |

**Parámetros de búsqueda:**
- `name`: Nombre a buscar (coincidencias parciales)

**Respuesta de estadísticas incluye:**
- Total de usuarios registrados
- Timestamp de la consulta

## 📋 Modelo de Datos

```java
@Entity
public class User {
    private Long id;                    // ID único autogenerado
    private String name;                // Nombre completo (máx. 100 chars)
    private String email;               // Email único y válido
    private LocalDateTime createdAt;    // Fecha de registro
    private LocalDateTime updatedAt;    // Última modificación
}
```

## ⚙️ Configuración del Servicio

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/marketjosemsp
spring.datasource.username=root
spring.datasource.password=****
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

## 🚀 Orden de Ejecución

1. **Iniciar MySQL** en puerto 3306
2. **Iniciar Eureka Server** en puerto 8761
3. **Iniciar API Gateway** en puerto 8087
4. **Ejecutar User Service** en puerto 8081
5. **Iniciar Order Service** en puerto 8083 (opcional)

### Verificar funcionamiento:
- **Directo**: [http://localhost:8081/api/users](http://localhost:8081/api/users)
- **Gateway**: [http://localhost:8087/api/users](http://localhost:8087/api/users)

## 🔄 Comunicación entre Servicios

### 🛒 Order Service Integration
- Consulta órdenes por usuario
- Enriquece perfil con historial de compras

### 🌐 API Gateway Integration
- Enrutamiento automático de peticiones
- Balanceador de carga
- CORS y logging centralizado

## 🧠 Lógica de Negocio

### Sistema de Validaciones
```java
// Validación automática de email
if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) → UserValidationException

// Verificación de unicidad
if (emailExists) → UserAlreadyExistsException

// Control de longitud
if (name.length() > 100) → UserValidationException
```

### Estadísticas Automáticas
- Conteo total de usuarios
- Timestamps de consultas
- Métricas de uso

## 🚨 Manejo de Errores

### Casos Críticos Manejados
- **Usuario no encontrado** (404)
- **Email duplicado** (409 Conflict)
- **Datos inválidos** (400 Bad Request)
- **Servicio externo no disponible** (503)

### Tipos de Respuesta
- **Éxito**: Datos completos + metadata
- **Error**: Código específico + mensaje descriptivo
- **Servicio no disponible**: Degradación graceful

## 🛒 Casos de Uso del Marketplace

### Gestión de Usuarios
- Registro de nuevos compradores
- Actualización de perfiles de cliente
- Mantenimiento de datos únicos

### Integración Comercial
- Vinculación con historial de compras
- Análisis de patrones de compra
- Soporte para recomendaciones de productos

### Administración del Marketplace
- Seguimiento de usuarios activos
- Análisis de crecimiento de la base de usuarios
- Reportes de actividad comercial

---
## 👨‍ Autor

### **Jose Manuel Siguero Pérez**
### [Linkedin](https://www.linkedin.com/in/jose-manuel-siguero)

----
**Parte del Sistema de Microservicios Products Market**