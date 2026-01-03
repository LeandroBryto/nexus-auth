FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# ==========================================
# ESTÁGIO 2: RUNTIME
# ==========================================
FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.source="nexus-auth" \
      org.opencontainers.image.description="API de Autenticação Nexus"

ENV TZ=America/Sao_Paulo \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=80 -Dfile.encoding=UTF-8 -Duser.timezone=America/Sao_Paulo"

RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

# Copia o JAR gerado no estágio 1
COPY --from=build /app/target/*.jar app.jar

# Porta do Sentinel (8081)
EXPOSE 8084

# Comando de inicialização otimizado
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]