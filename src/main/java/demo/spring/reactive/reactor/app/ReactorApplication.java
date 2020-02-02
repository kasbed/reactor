package demo.spring.reactive.reactor.app;

import java.time.Duration;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import demo.spring.reactive.reactor.app.model.Comment;
import demo.spring.reactive.reactor.app.model.User;
import demo.spring.reactive.reactor.app.model.UserComment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ReactorApplication implements CommandLineRunner { // Implementing CommandLineRunner, turns our app into
																// commandline-app

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
		basicExampleCombineFlatMap();
		basicExmapleZipWith();
		basicExampleRange();
		basicExampleInterval();
		basicExampleDelay();
		basicExampleInfiniteInterval();
		basicExampleFluxCreate();
		basicExampleBackPressure();
	}

	// A basic iteration flux example
	public void basicExampleIteration() throws Exception {
		// Flux.just creates a flux with the arguments
		// Flux<String> names = Flux.just("Jhon McClaine", "Maria Hollyday", "Jax
		// Wildfire", "Emily Clarson", "Bruce Lee", "Bruce Willis");
		Flux<String> names = Flux.fromIterable(Arrays.asList("Jhon McClaine", "Maria Hollyday", "Jax Wildfire",
				"Emily Clarson", "Bruce Lee", "Bruce Willis"));

		Flux<User> users = names.map(e -> new User(e.split(" ")[0], e.split(" ")[1]))
				.filter(e -> e.getFirstName().equalsIgnoreCase("bruce")).doOnNext(e -> {
					if (e == null)
						throw new RuntimeException("Error!");
				}).map(e -> {
					return e;
				});

		users.subscribe( // Subscribing to Flux.
				e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {
					@Override
					public void run() {
						log.info("Finished");
					}
				});
	}

	// A basic flatMap flux example
	public void basicExampleFlatMap() throws Exception {
		Flux.fromIterable(Arrays.asList("Jhon McClaine", "Maria Hollyday", "Jax Wildfire", "Emily Clarson", "Bruce Lee",
				"Bruce Willis")).map(e -> new User(e.split(" ")[0], e.split(" ")[1])).flatMap(e -> {
					if (e.getFirstName().equalsIgnoreCase("bruce"))
						return Mono.just(e);
					return Mono.empty();
				}).subscribe(e -> log.info(e.toString()));
	}

	// A basic flux to String example
	public void basicExampleFluxToString() throws Exception {
		Flux.fromIterable(
				Arrays.asList(new User("Jhon", "McClaine"), new User("Maria", "Hollyday"), new User("Jax", "Wildfire"),
						new User("Emily", "Clarson"), new User("Bruce", "Lee"), new User("Bruce", "Willis")))
				.map(e -> e.toString()).flatMap(e -> {
					if (e.contains(StringUtils.capitalize("bruce")))
						return Mono.just(e);
					return Mono.empty();
				}).subscribe(System.out::println);
	}

	// A basic collect list example
	public void basicExampleCollectList() throws Exception {
		Flux.fromIterable(
				Arrays.asList(new User("Jhon", "McClaine"), new User("Maria", "Hollyday"), new User("Jax", "Wildfire"),
						new User("Emily", "Clarson"), new User("Bruce", "Lee"), new User("Bruce", "Willis")))
				.collectList().subscribe(e -> e.forEach(System.out::println));
	}

	// A basic flatMap combine flux example
	public void basicExampleCombineFlatMap() throws Exception {
		Mono<User> usuarioMono = Mono.fromCallable(() -> new User("Jhon", "McClaine"));
		Mono<Comment> commentMono = Mono
				.fromCallable(() -> new Comment(Arrays.asList("YipiKaye", "Motherfucker", "You shall no pass")));

		usuarioMono.flatMap(u -> commentMono.map(c -> new UserComment(u, c))).subscribe(System.out::println);
	}

	// A basic zipWith combine flux example
	public void basicExmapleZipWith() throws Exception {
		Mono<User> usuarioMono = Mono.fromCallable(() -> new User("Jhon", "Gandalf"));
		Mono<Comment> commentMono = Mono
				.fromCallable(() -> new Comment(Arrays.asList("YipiKaye", "Motherfucker", "You shall no pass")));

		usuarioMono.zipWith(commentMono, (u, c) -> new UserComment(u, c)).subscribe(System.out::println); // Combine

		usuarioMono.zipWith(commentMono).map(t -> new UserComment(t.getT1(), t.getT2())).subscribe(System.out::println); // Create
																															// Tuple
	}

	// A basic Range flux example
	public void basicExampleRange() throws Exception {
		Flux.just(1, 2, 3, 4).map(i -> (i * 2))
				.zipWith(Flux.range(0, 4), (o, t) -> String.format("First: %d, Second : %d", o, t))
				.subscribe(System.out::println);
	}

	// A basic interval example
	public void basicExampleInterval() throws Exception {
		Flux.range(1, 12).zipWith(Flux.interval(Duration.ofSeconds(1)), (r, i) -> r).doOnNext(System.out::println)
				.blockLast(); // Block the flux, no parallel work
	}

	// A basic interval example
	public void basicExampleDelay() throws Exception {
		Flux.range(1, 12).delayElements(Duration.ofSeconds(1)).doOnNext(System.out::println).blockLast();
	}

	// A basic infinite interval example
	public void basicExampleInfiniteInterval() throws Exception {

		CountDownLatch cdl = new CountDownLatch(1);
		Flux.interval(Duration.ofSeconds(1)).flatMap(i -> {
			if (i > 5)
				return Flux.error(new InterruptedException("Only five elements allowed"));
			return Flux.just(i);
		}).map(i -> "I ->" + i).retry(2) // Retry the flux operation
				.doOnNext(System.out::println).doOnTerminate(cdl::countDown) // On finish flux event
				.subscribe();

		cdl.await();
	}

	// A basic flux create example
	public void basicExampleFluxCreate() throws Exception {
		Flux.create(emitter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				private int counter = 0;

				@Override
				public void run() {
					if (counter > 10) {
						timer.cancel();
						emitter.complete();
					}
					emitter.next(++counter);
				}
			}, 1000, 1000);
		}).doOnNext(System.out::println).doOnComplete(() -> log.info("Finished")).subscribe();
	}

	/// Backpressure control
	public void basicExampleBackPressure() throws Exception {
		Flux.range(1, 10).log()
		.limitRate(2) //This is the same as override subscriber
		.subscribe(new Subscriber<Integer>() {

			private Subscription s;
			private int limit = 5;
			private int consumed = 0;

			@Override
			public void onSubscribe(Subscription s) {
				this.s=s;
				s.request(limit);
			}
			@Override
			public void onNext(Integer t) {
				log.info(t.toString());
				consumed++;
				if(consumed == limit) {
					consumed = 0;
					s.request(limit);
				}
			}
			@Override
			public void onError(Throwable t) {
				log.error(t.getMessage());
			}
			@Override
			public void onComplete() {}			
		});
	}
} 
