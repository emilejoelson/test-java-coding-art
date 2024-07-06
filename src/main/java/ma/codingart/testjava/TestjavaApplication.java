package ma.codingart.testjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TestjavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestjavaApplication.class, args);
	}

}
