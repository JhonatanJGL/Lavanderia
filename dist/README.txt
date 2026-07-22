Esta carpeta se completa al ejecutar:

    mvn clean package

Maven copiara las dependencias en dist/lib.
Luego copie un JRE/JDK 21 de Windows en dist/jre y use launch4j.xml.
El resultado final debe incluir:

    AquaClean.exe
    lib/
    jre/

No elimine lib ni jre: el ejecutable las necesita.
