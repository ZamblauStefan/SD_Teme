# Folosim o imagine oficială de Node.js pentru a construi aplicația
FROM node:16 AS build

# Setăm directorul de lucru
WORKDIR /app

# Copiem fișierul package.json și instalăm dependențele
COPY package*.json ./
RUN npm install

# Copiem întregul cod sursă și construim aplicația
COPY . .
RUN npm run build

# Folosim o imagine mai mică pentru a servi aplicația finală
FROM nginx:alpine

# Copiem fișierele de build în Nginx
COPY --from=build /app/build /usr/share/nginx/html

# Expunem portul 3000
EXPOSE 3000

# Pornim Nginx
CMD ["nginx", "-g", "daemon off;"]
