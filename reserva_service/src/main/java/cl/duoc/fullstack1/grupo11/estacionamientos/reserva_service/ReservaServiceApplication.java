package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ReservaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservaServiceApplication.class, args);
	}

}
