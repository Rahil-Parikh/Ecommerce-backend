FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY keystore.p12 /home/keystore.p12 
RUN pwd
EXPOSE 5001
EXPOSE 5432
ENTRYPOINT ["java","-jar","/app.jar"]