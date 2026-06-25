package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VehiculoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehiculoServiceApplication.class, args);
	}

}
