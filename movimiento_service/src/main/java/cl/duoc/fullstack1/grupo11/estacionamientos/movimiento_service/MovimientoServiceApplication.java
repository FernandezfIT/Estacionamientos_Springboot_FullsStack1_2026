package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MovimientoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovimientoServiceApplication.class, args);
	}

}
