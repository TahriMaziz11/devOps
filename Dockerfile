FROM openjdk:8
EXPOSE 8089
ADD /target/ExamThourayaS2.jar  ExamThourayaS2.jar
ENTRYPOINT ["java", "-jar", "ExamThourayaS2.jar"]