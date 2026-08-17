# Core
Dependencia Java diseñada para centralizar y simplificar funcionalidades comunes en el desarrollo de aplicaciones.

Proporciona un conjunto de utilidades y herramientas reutilizables, incluyendo Dependency Injection (DI), ORM/SQL, acceso y gestión de datos, y otros componentes esenciales para construir aplicaciones Java de forma más rápida, modular y mantenible.

## Características
- Dependency Injection (DI)
- ORM SQL y JSON
- Acceso y gestión de datos
- Utilidades comunes para proyectos Java
- Arquitectura modular y reutilizable
- Instalación

Pendiente de definir el sistema de distribución e instalación.

## Uso

### Dependency Injection (DI)

El módulo de inyección de dependencias permite gestionar automáticamente la creación y resolución de dependencias entre clases.

#### Requisitos básicos:
1. Anotate tu clase con `@Singleton` para que sea gestionada por el contenedor
2. Define las dependencias a través del constructor o mediante inyección de campos

#### Ejemplo básico:

```java
import io.github.adsa06.di.Singleton;
import io.github.adsa06.di.Inject;

// Servicio simple
@Singleton
public class DatabaseService {
    public void connect() {
        System.out.println("Conectado a BD");
    }
}

// Servicio que depende de DatabaseService
@Singleton
public class UserService {
    private final DatabaseService db;
    
    // Constructor injection
    public UserService(DatabaseService db) {
        this.db = db;
    }
    
    public void getUser(int id) {
        db.connect();
        System.out.println("Obteniendo usuario " + id);
    }
}

// Inyección de campos
@Singleton
public class UserController {
    @Inject
    private UserService userService;
    
    public void handleRequest(int userId) {
        userService.getUser(userId);
    }
}
```

#### Uso:

```java
// Crear el inyector
Injector injector = new Injector();

// Obtener instancias (singleton)
UserController controller = injector.getInstance(UserController.class);
UserController controller2 = injector.getInstance(UserController.class);

// Ambas referencias apuntan al mismo objeto
assert controller == controller2;
```

#### Características:
- **Singletons automáticos**: Todas las clases anotadas con `@Singleton` se crean una sola vez
- **Inyección por constructor**: Resuelve automáticamente las dependencias del constructor
- **Inyección de campos**: Usa `@Inject` para inyectar dependencias en atributos
- **Resolución de dependencias circulares por campos**: Permite ciclos a través de inyección de campos
- **Instancias registradas manualmente**: Usa `registerInstance()` para registrar instancias existentes

## Licencia

Este proyecto está licenciado bajo la **Licencia Apache 2.0**.

Consulta el archivo [LICENSE](LICENSE) para más detalles.