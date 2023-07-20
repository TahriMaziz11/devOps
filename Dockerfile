FROM openjdk:8
EXPOSE 8089
ADD /target/Exam.jar  Exam.jar
ENTRYPOINT ["java", "-jar", "Exam.jar"]