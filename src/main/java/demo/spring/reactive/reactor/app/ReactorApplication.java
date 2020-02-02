package demo.spring.reactive.reactor.app;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import demo.spring.reactive.reactor.app.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ReactorApplication implements CommandLineRunner{ //Implementing CommandLineRunner, turns our app into commandline-app

	private static Logger log = LoggerFactory.getLogger(ReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		basicExampleIteration();
		basicExampleFlatMap();
		basicExampleFluxToString();
		basicExampleCollectList();
	}
	
	// A basic iteration flux example
	public void basicExampleIteration() throws Exception {
		//Flux.just creates a flux with the arguments 
		//Flux<String> names = Flux.just("Jhon McClaine", "Maria Hollyday", "Jax Wildfire", "Emily Clarson", "Bruce Lee", "Bruce Willis");
		Flux<String> names = Flux.fromIterable(Arrays.asList("Jhon McClaine", "Maria Hollyday", "Jax Wildfire", "Emily Clarson", "Bruce Lee", "Bruce Willis"));
		
		Flux<User> users = names.map(e -> new User(e.split(" ")[0],e.split(" ")[1]))
			.filter(e -> e.getFirstName().equalsIgnoreCase("bruce"))
			.doOnNext(e-> {
				if(e == null)
					throw new RuntimeException("Error!");				
			}).map(e -> {return e;});

		users.subscribe( //Subscribing to Flux.
			e -> log.info(e.toString()), 
			error -> log.error(error.getMessage()),
			new Runnable(){
				@Override public void run() {log.info("Finished");}
			}
		);
	}

	// A basic flatMap flux example
	public void basicExampleFlatMap() throws Exception {
		Flux.fromIterable(Arrays.asList("Jhon McClaine", "Maria Hollyday", "Jax Wildfire", "Emily Clarson", "Bruce Lee", "Bruce Willis"))
			.map(e -> new User(e.split(" ")[0],e.split(" ")[1]))
			.flatMap(e ->{if (e.getFirstName().equalsIgnoreCase("bruce")) return Mono.just(e); return Mono.empty();})
			.subscribe(e -> log.info(e.toString()));
	}

	// A basic flux to String example
	public void basicExampleFluxToString() throws Exception {
		Flux.fromIterable(Arrays.asList(new User("Jhon", "McClaine"), new User("Maria", "Hollyday"), new User("Jax", "Wildfire"), new User("Emily", "Clarson"), new User("Bruce", "Lee"), new User("Bruce", "Willis")))
			.map(e->e.toString())
			.flatMap(e-> { 
				if(e.contains(StringUtils.capitalize("bruce"))) return Mono.just(e); return Mono.empty();
			})
			.subscribe(System.out::println);
	}

	// A basic collect list example
	public void basicExampleCollectList() throws Exception {
		Flux.fromIterable(Arrays.asList(new User("Jhon", "McClaine"), new User("Maria", "Hollyday"), new User("Jax", "Wildfire"), new User("Emily", "Clarson"), new User("Bruce", "Lee"), new User("Bruce", "Willis")))
			.collectList()
			.subscribe(e -> e.forEach(System.out::println));
	}

}
