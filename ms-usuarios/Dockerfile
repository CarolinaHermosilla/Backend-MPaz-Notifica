# Etapa 1: se compila el proyecto con Maven dentro del contenedor.
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Etapa 2: la imagen final solo lleva el jar y un Java liviano.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacion /app/target/ms-usuarios.jar app.jar
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
