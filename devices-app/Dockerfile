# Folosim o imagine de bază cu Java JDK 17
FROM openjdk:17-jdk-slim

# Setăm directorul de lucru
WORKDIR /app

# Copiem fișierul jar generat de aplicație în container
COPY target/*.jar app.jar

# Expunem portul pe care rulează aplicația (8081 sau 8082)
EXPOSE 8082

# Comanda pentru a porni aplicația
ENTRYPOINT ["java", "-jar", "app.jar"]
