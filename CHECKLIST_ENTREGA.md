# Checklist final de entrega - AquaClean

## Proyecto

- [x] Paquetes `app`, `controller`, `dao`, `db`, `model`, `util` y vistas FXML.
- [x] `Persona` abstracta con atributos privados.
- [x] Herencia: `Usuario` y `Cliente` extienden `Persona`.
- [x] Polimorfismo: ambas clases sobrescriben `mostrarTipo()`.
- [x] Abstraccion: interfaz generica `ICRUD<T>`.
- [x] Conexion MySQL con patron Singleton.
- [x] Login con roles `ADMIN`, `CAJERO` y `REPORTES`.
- [x] Un unico `dashboard.fxml` reutilizable segun el rol.
- [x] CRUD de servicios, clientes, usuarios y ordenes.
- [x] TableView, busqueda, confirmaciones y validaciones.
- [x] Control de stock mediante transacciones SQL.
- [x] Impresion de comprobante.
- [x] Estadisticas, grafico y exportacion CSV.
- [x] Configuracion del sistema.
- [x] CSS, hover, logo y colores por rol.

## Archivos de entrega

- [x] `Poo_lavanderia.sql`.
- [x] `documentacion/Manual_Usuario_AquaClean.pdf` de 2 paginas.
- [x] `guion_video_demo.txt` para video de maximo 3 minutos.
- [x] `README.md`.
- [x] `launch4j.xml` e instrucciones para generar el `.exe`.
- [ ] Ejecutar la aplicacion en Windows y tomar capturas reales finales.
- [ ] Generar el `.jar` y el `.exe` en Windows.
- [ ] Colocar el `.exe` final dentro de `/dist`.
- [ ] Grabar el video de demostracion.
- [ ] Completar el nombre del segundo estudiante en el manual.
- [ ] Verificar repositorio publico y minimo 6 commits.

## Prueba final obligatoria

1. Ejecutar `Poo_lavanderia.sql`.
2. Revisar usuario, clave y puerto en `Conexion.java`.
3. Probar login con los tres roles.
4. Crear, editar y eliminar un servicio.
5. Crear cliente y orden; comprobar disminucion del stock.
6. Editar y eliminar orden; comprobar restauracion del stock.
7. Imprimir un comprobante o usar una impresora PDF.
8. Descargar el reporte CSV.
9. Cerrar sesion.
