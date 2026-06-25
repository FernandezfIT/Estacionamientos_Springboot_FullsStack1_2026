FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY eureka_server/pom.xml eureka_server/
COPY api_gateway/pom.xml api_gateway/
COPY auth_service/pom.xml auth_service/
COPY usuario_service/pom.xml usuario_service/
COPY vehiculo_service/pom.xml vehiculo_service/
COPY plaza_service/pom.xml plaza_service/
COPY reserva_service/pom.xml reserva_service/
COPY acceso_service/pom.xml acceso_service/
COPY movimiento_service/pom.xml movimiento_service/
COPY liberacion_service/pom.xml liberacion_service/
COPY cierre_service/pom.xml cierre_service/
COPY reporte_service/pom.xml reporte_service/

RUN for dir in eureka_server api_gateway auth_service usuario_service vehiculo_service plaza_service reserva_service acceso_service movimiento_service liberacion_service cierre_service reporte_service; do \
      (cd "$dir" && mvn -B dependency:go-offline) || true; \
    done

COPY eureka_server/src eureka_server/src
COPY api_gateway/src api_gateway/src
COPY auth_service/src auth_service/src
COPY usuario_service/src usuario_service/src
COPY vehiculo_service/src vehiculo_service/src
COPY plaza_service/src plaza_service/src
COPY reserva_service/src reserva_service/src
COPY acceso_service/src acceso_service/src
COPY movimiento_service/src movimiento_service/src
COPY liberacion_service/src liberacion_service/src
COPY cierre_service/src cierre_service/src
COPY reporte_service/src reporte_service/src

RUN for dir in eureka_server api_gateway auth_service usuario_service vehiculo_service plaza_service reserva_service acceso_service movimiento_service liberacion_service cierre_service reporte_service; do \
      (cd "$dir" && mvn -B clean package -DskipTests) || exit 1; \
    done

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/eureka_server/target/eureka_server-*.jar ./eureka_server.jar
COPY --from=build /app/api_gateway/target/api_gateway-*.jar ./api_gateway.jar
COPY --from=build /app/auth_service/target/auth_service-*.jar ./auth_service.jar
COPY --from=build /app/usuario_service/target/usuario_service-*.jar ./usuario_service.jar
COPY --from=build /app/vehiculo_service/target/vehiculo_service-*.jar ./vehiculo_service.jar
COPY --from=build /app/plaza_service/target/plaza_service-*.jar ./plaza_service.jar
COPY --from=build /app/reserva_service/target/reserva_service-*.jar ./reserva_service.jar
COPY --from=build /app/acceso_service/target/acceso_service-*.jar ./acceso_service.jar
COPY --from=build /app/movimiento_service/target/movimiento_service-*.jar ./movimiento_service.jar
COPY --from=build /app/liberacion_service/target/liberacion_service-*.jar ./liberacion_service.jar
COPY --from=build /app/cierre_service/target/cierre_service-*.jar ./cierre_service.jar
COPY --from=build /app/reporte_service/target/reporte_service-*.jar ./reporte_service.jar

COPY docker/microservices/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8761 8090 8080 8081 8082 8083 8084 8085 8086 8087 8088 8089

ENTRYPOINT ["/entrypoint.sh"]
