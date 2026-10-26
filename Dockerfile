FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw \
    && ./mvnw -B -ntp dependency:go-offline

COPY src/ src/

RUN ./mvnw -B -ntp clean package -Dmaven.test.skip=true \
    && set -eux; \
       jar_file="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar')"; \
       test "$(printf '%s\n' "$jar_file" | sed '/^$/d' | wc -l)" -eq 1; \
       cp "$jar_file" /workspace/application.jar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

ENV PORT=10000 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=20.0 -XX:+ExitOnOutOfMemoryError"

RUN useradd --system --uid 10001 --create-home spring

COPY --from=build --chown=spring:spring /workspace/application.jar application.jar

USER spring

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "/app/application.jar"]
