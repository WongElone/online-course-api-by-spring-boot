FROM openjdk:17-oracle
COPY target/online-course-api-0.1.0.jar online-course-api-0.1.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/online-course-api-0.1.0.jar"]