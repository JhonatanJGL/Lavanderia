# Guia rapida para la defensa individual

## Los cuatro pilares de POO

### Encapsulamiento
Los atributos de las clases modelo son privados. Se accede a ellos mediante getters y setters. Ejemplo: `Persona`, `Servicio` y `OrdenLavado`.

### Herencia
`Usuario` y `Cliente` heredan los atributos y metodos de la clase abstracta `Persona` mediante `extends Persona`.

### Polimorfismo
`Usuario` y `Cliente` implementan de manera diferente el metodo heredado `mostrarTipo()` usando `@Override`.

### Abstraccion
`Persona` define un metodo abstracto y `ICRUD<T>` establece el contrato `insertar`, `listar`, `buscar`, `actualizar` y `eliminar` para los DAO.

## Patron Singleton
`Conexion` tiene constructor privado, una unica instancia estatica y el metodo `getInstancia()`. De esta forma toda la aplicacion reutiliza una sola conexion activa.

## Flujo del login

1. `LoginController` recibe usuario y contrasena.
2. `UsuarioDAO.autenticar()` consulta MySQL con `PreparedStatement`.
3. Si existe, se crea un objeto `Usuario` completo.
4. El objeto se envia a `DashboardController.setUsuario()`.
5. El dashboard muestra u oculta botones segun el rol.

## Dashboard reutilizable
Solo existe `dashboard.fxml`. El metodo `aplicarPermisos()` cambia la visibilidad de los modulos y la clase CSS segun `ADMIN`, `CAJERO` o `REPORTES`.

## CRUD y base de datos
Cada controlador valida la informacion y llama al DAO. Los DAO usan `PreparedStatement` para evitar concatenar datos en SQL. `executeQuery()` se usa en SELECT y `executeUpdate()` en INSERT, UPDATE y DELETE.

## Transaccion de orden y stock
`OrdenLavadoDAO` desactiva `autoCommit`, descuenta o devuelve stock, guarda la orden y confirma con `commit()`. Si ocurre un error ejecuta `rollback()` para evitar datos incompletos.

## Preguntas probables

- **Por que `Persona` es abstracta?** Porque representa una base comun y no tiene sentido crear una persona generica sin tipo.
- **Por que usar una interfaz generica?** Para que todos los DAO respeten las mismas operaciones sin repetir el contrato.
- **Diferencia entre `executeQuery` y `executeUpdate`:** el primero devuelve `ResultSet`; el segundo devuelve filas afectadas.
- **Por que usar clave foranea?** Para mantener integridad entre usuarios, clientes, servicios y ordenes.
- **Como se impide un duplicado?** Se consulta antes de insertar y tambien existen restricciones `UNIQUE` en MySQL.
- **Como se restringen los roles?** El dashboard recibe el objeto usuario y configura `visible` y `managed` de cada boton.
- **Que pasa si no hay stock?** El UPDATE de stock exige `stock >= cantidad`; si no afecta filas, lanza excepcion y hace rollback.
