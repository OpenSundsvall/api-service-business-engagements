FROM openjdk:17.0.2-slim AS build

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:17.0.2-slim
COPY --from=build dependencies/ ./
COPY --from=build snapshot-dependencies/ ./
COPY --from=build spring-boot-loader/ ./
COPY --from=build application/ ./

ENTRYPOINT java $JAVA_OPTS -cp . org.springframework.boot.loader.JarLauncher