package com.openclassrooms.tourguide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TourguideApplication {

  public static void main(String[] args) {
    SpringApplication.run(TourguideApplication.class, args);
  }
  /*
	// Création d'un pool de threads
	// Le nombre de threads est basé sur le nombre de cœurs CPU disponibles multiplié par 4
	// Cela permet d'exécuter plusieurs tâches en parallèle
	private final ExecutorService executorService =
			Executors.newFixedThreadPool(
					Runtime.getRuntime().availableProcessors() * 4
			);*/
}
