FROM amazoncorretto:21-alpine-jdk
LABEL authors="ilya"
RUN apk --no-cache add curl
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]