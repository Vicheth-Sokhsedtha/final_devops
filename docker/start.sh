#!/bin/bash

# Generate self-signed SSL certificate for Nginx
mkdir -p /etc/nginx/ssl
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/server.key \
    -out /etc/nginx/ssl/server.crt \
    -subj "/C=KH/ST=PhnomPenh/L=PhnomPenh/O=IDCardSystem/CN=localhost" 2>/dev/null

# Remove default nginx site that might conflict, then start Nginx
rm -f /etc/nginx/sites-enabled/default
service nginx start

# Start SSH server
service ssh start

# Start Spring Boot app on port 8091 (Nginx on port 8060 proxies to this)
echo "Starting Spring Boot application on port 8091..."
java -jar /app/app.jar \
    --server.port=8091 \
    --spring.datasource.url=jdbc:mysql://127.0.0.1:3306/B-Vicheth_Sokhsedtha-db \
    --spring.datasource.username=root \
    --spring.datasource.password=Hello@123 \
    --spring.jpa.hibernate.ddl-auto=update \
    --photo.upload-dir=/app/uploads/photos

# Keep container running
tail -f /dev/null