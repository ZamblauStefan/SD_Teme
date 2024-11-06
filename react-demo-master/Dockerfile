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

# Copiem configurația nginx personalizată
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expunem portul 3000
EXPOSE 3000

# Pornim Nginx
CMD ["nginx", "-g", "daemon off;"]


#FROM node:20.18.0 as builder
#WORKDIR /app
#COPY package*.json ./
#RUN npm install
#COPY . .
#RUN npm run build
#
#
#FROM nginx:alpine
#
#COPY --from=build /app/build /usr/share/nginx/html
#
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#
##RUN apk --no-cache add curl
##RUN curl -L https://github.com/a8m/envsubst/releases/download/v1.2.0/envsubst-`uname -s`-`uname -m` -o envsubst && \
##    chmod +x envsubst && \
##    mv envsubst /usr/local/bin
#
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]
#
##COPY --from=builder /app/nginx.conf /etc/nginx/nginx.template
##CMD ["/bin/sh", "-c", "envsubst < /etc/nginx/nginx.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
##COPY --from=builder /app/build/ /usr/share/nginx/html
