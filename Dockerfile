# Imagen base con JDK 17
FROM eclipse-temurin:17-jdk

# Directorio de trabajo
WORKDIR /app

# Copiar el archivo gradlew y los scripts de gradle
COPY gradlew .
COPY gradle gradle

# Copiar los archivos de build
COPY build.gradle settings.gradle ./

# Convertir los scripts a formato Unix (para evitar problemas con CRLF)
RUN apt-get update && apt-get install -y dos2unix && dos2unix gradlew && chmod +x gradlew

# Copiar el código fuente
COPY src src
COPY frontend frontend

# Construir la aplicación (sin tests)
RUN ./gradlew build -x test

# Puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "build/libs/practica-vaadin-0.0.1-SNAPSHOT.jar"]