FROM gradle:8.7-jdk17 AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle primero (mejora caché)
COPY build.gradle settings.gradle* ./
COPY gradle/ gradle/

# Descargar dependencias sin compilar (capa cacheada)
RUN gradle dependencies --no-daemon || true

# Copiar el resto del código fuente
COPY src/ src/

# Generar la distribución ejecutable
RUN gradle installDist --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar la distribución compilada desde la etapa anterior
COPY --from=build /app/build/install/Proyecto_FinalWeb/ .

# Exponer puertos HTTP y gRPC
EXPOSE 7000 50051


ENV APP_PORT=7000 \
    GRPC_PORT=50051 \
    MONGO_URL=mongodb://mongo:27017 \
    MONGO_DB=proyecto_final_web \
    JWT_SECRET=clave-super-secreta-cambiar-en-produccion-2026

ENTRYPOINT ["bin/Proyecto_FinalWeb"]