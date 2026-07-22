@echo off
chcp 65001 > nul
where mvn > nul 2>&1
if errorlevel 1 (
    echo Maven no esta disponible en PATH.
    echo Abra el proyecto en IntelliJ y ejecute Main.java, o configure Maven.
    pause
    exit /b 1
)
mvn clean javafx:run
if errorlevel 1 (
    echo.
    echo La aplicacion no pudo iniciar. Revise MySQL, Conexion.java y las dependencias Maven.
    pause
)
