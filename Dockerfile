# Etapa de build: compila y empaqueta el jar con Maven + JDK 21.
# Se separa de la etapa de runtime para no arrastrar el toolchain de build a la imagen final.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Se copia primero el pom y se bajan dependencias: aprovecha la cache de capas de Docker
# (si el pom no cambia, no se re-descargan las dependencias en cada build).
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# Etapa de runtime: solo JRE + el jar. Imagen final chica y sin herramientas de build.
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd -r -u 1001 appuser
COPY --from=build /app/target/nexo-transfer-api-0.1.0.jar app.jar
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --retries=5 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
