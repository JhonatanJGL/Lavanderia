@echo off
chcp 65001 > nul
where mvn > nul 2>&1
if errorlevel 1 (
    echo Maven no esta disponible en PATH.
    pause
    exit /b 1
)
mvn clean package
if errorlevel 1 (
    echo No se pudo crear el JAR.
    pause
    exit /b 1
)
echo.
echo JAR creado en target\lavanderia-1.0.0.jar
echo Dependencias copiadas en dist\lib
echo Continue con INSTRUCCIONES_EXE.md
pause
