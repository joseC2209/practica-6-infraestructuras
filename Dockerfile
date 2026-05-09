# Usamos la imagen oficial de Java 21 (ajusta si usas la 17)
FROM eclipse-temurin:21-jdk-alpine

# Creamos un volumen temporal (Spring Boot lo usa para Tomcat)
VOLUME /tmp

# Copiamos el JAR generado por Maven dentro del contenedor
COPY target/*.jar app.jar

# Le decimos al contenedor qué comando ejecutar al arrancar
ENTRYPOINT ["java","-jar","/app.jar"]