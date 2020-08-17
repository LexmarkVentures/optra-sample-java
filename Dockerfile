
FROM openjdk:latest
COPY build/libs/optra-sample-java-all.jar /
WORKDIR /
CMD ["java", "-classpath", ".:optra-sample-java-all.jar", "-Dorg.slf4j.simpleLogger.logFile=System.out", "com.lexmark.optra.Skill"]

