FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN mkdir /opt/serving
EXPOSE 5001
EXPOSE 5432
ENTRYPOINT ["java","-jar","/app.jar"]
