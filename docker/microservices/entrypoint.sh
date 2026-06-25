#!/bin/bash

echo "========================================="
echo " Iniciando todos los microservicios..."
echo "========================================="

# 1. Eureka Server
echo "[1/12] Iniciando Eureka Server (puerto 8761)..."
java -jar /app/eureka_server.jar --server.port=8761 &
sleep 25

# 2. Usuario Service
echo "[2/12] Iniciando Usuario Service (puerto 8081)..."
java -jar /app/usuario_service.jar --server.port=8081 &
sleep 12

# 3. Vehiculo Service
echo "[3/12] Iniciando Vehiculo Service (puerto 8082)..."
java -jar /app/vehiculo_service.jar --server.port=8082 &
sleep 8

# 4. Plaza Service
echo "[4/12] Iniciando Plaza Service (puerto 8083)..."
java -jar /app/plaza_service.jar --server.port=8083 &
sleep 8

# 5. Movimiento Service
echo "[5/12] Iniciando Movimiento Service (puerto 8086)..."
java -jar /app/movimiento_service.jar --server.port=8086 &
sleep 8

# 6. Auth Service
echo "[6/12] Iniciando Auth Service (puerto 8080)..."
java -jar /app/auth_service.jar --server.port=8080 &
sleep 8

# 7. Reserva Service
echo "[7/12] Iniciando Reserva Service (puerto 8084)..."
java -jar /app/reserva_service.jar --server.port=8084 &
sleep 6

# 8. Liberacion Service
echo "[8/12] Iniciando Liberacion Service (puerto 8087)..."
java -jar /app/liberacion_service.jar --server.port=8087 &
sleep 6

# 9. Acceso Service
echo "[9/12] Iniciando Acceso Service (puerto 8085)..."
java -jar /app/acceso_service.jar --server.port=8085 &
sleep 6

# 10. Reporte Service
echo "[10/12] Iniciando Reporte Service (puerto 8089)..."
java -jar /app/reporte_service.jar --server.port=8089 &
sleep 6

# 11. Cierre Service
echo "[11/12] Iniciando Cierre Service (puerto 8088)..."
java -jar /app/cierre_service.jar --server.port=8088 &
sleep 6

# 12. API Gateway
echo "[12/12] Iniciando API Gateway (puerto 8090)..."
java -jar /app/api_gateway.jar --server.port=8090 &

echo "========================================="
echo " Todos los servicios iniciados."
echo " Monitoreando logs..."
echo "========================================="

tail -f /dev/null
