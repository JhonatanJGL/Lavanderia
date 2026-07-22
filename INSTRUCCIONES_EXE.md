# Generar AquaClean.exe en Windows

## 1. Crear el JAR y las librerias

Desde la terminal de IntelliJ, ubicada en la raiz del proyecto, ejecute:

```bash
mvn clean package
```

Este comando crea:

```text
target/lavanderia-1.0.0.jar
dist/lib/                 dependencias de JavaFX y MySQL
```

## 2. Agregar un runtime de Java

Copie un JRE/JDK 21 de Windows dentro de:

```text
dist/jre
```

La estructura debe quedar:

```text
dist
├── jre
├── lib
└── AquaClean.exe          se creara en el paso siguiente
```

## 3. Crear el EXE con Launch4j

1. Abra Launch4j.
2. Cargue el archivo `launch4j.xml` de este proyecto.
3. Presione **Build wrapper**.
4. El resultado se guardara como `dist/AquaClean.exe`.

El archivo `AquaClean.ico` ya esta configurado como icono del ejecutable.

## 4. Prueba obligatoria

Copie la carpeta `dist` completa a otra ubicacion y ejecute `AquaClean.exe` sin abrir IntelliJ. MySQL debe estar iniciado y la base `Poo_lavanderia` debe existir.

No basta con entregar solo el EXE: conserve junto a el las carpetas `lib` y `jre`.

> El EXE debe generarse en Windows porque JavaFX utiliza componentes nativos de Windows. Este paquete incluye la configuracion, pero no un binario precompilado.
