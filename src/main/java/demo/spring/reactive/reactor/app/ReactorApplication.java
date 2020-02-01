package demo.spring.reactive.reactor.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactorApplication implements CommandLineRunner{ //Implementing CommandLineRunner, turns our app into commandline-app

	private static Logger log = LoggerFactory.getLogger(ReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Flux.just creates a flux with the arguments 
		Flux<String> names = Flux.just("Jhon", "Maria", "Antoni", "Emily")
			.doOnNext(e-> {
				if(e.isEmpty())
					throw new RuntimeException("Error!");
				System.out.println(e);
			});
		names.subscribe(
			e -> log.info(e), 
			error -> log.error(error.getMessage()),
			new Runnable(){
				@Override public void run() {log.info("Finished");}
			}
		); //Subscribing to Flux.
	}

}
