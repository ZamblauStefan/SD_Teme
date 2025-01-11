# Folosim o imagine de bază cu Java JDK 17
FROM openjdk:17-jdk-slim

# Setăm directorul de lucru
WORKDIR /app

# Copiem fișierul jar generat de aplicație în container
COPY target/*.jar app.jar

# Copiere certificat și cheie în container
#COPY certs/monitor-app.p12 /certs/monitor-app.p12

# Expunem portul pe care rulează aplicația (8081 sau 8082)
#EXPOSE 8080
EXPOSE 8443

# Comanda pentru a porni aplicația
ENTRYPOINT ["java", "-jar", "app.jar"]


#FROM maven:3.9.0-eclipse-temurin-17 AS builder
#
#COPY ./src/ /root/src
#COPY ./pom.xml /root/
#COPY ./checkstyle.xml /root/
#WORKDIR /root
#RUN mvn package
#RUN java -Djarmode=layertools -jar /root/target/ds-2020-0.0.1-SNAPSHOT.jar list
#RUN java -Djarmode=layertools -jar /root/target/ds-2020-0.0.1-SNAPSHOT.jar extract
#RUN ls -l /root
#
#FROM eclipse-temurin:17-jdk
#
#ENV TZ=UTC
#
#COPY --from=builder /root/dependencies/ ./
#COPY --from=builder /root/snapshot-dependencies/ ./
#
#RUN sleep 10
#COPY --from=builder /root/spring-boot-loader/ ./
#COPY --from=builder /root/application/ ./
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher","-XX:+UseContainerSupport -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -Xms512m -Xmx512m -XX:+UseG1GC -XX:+UseSerialGC -Xss512k -XX:MaxRAM=72m"]


