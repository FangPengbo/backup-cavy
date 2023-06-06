package cc.focc.cavy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("cc.focc.cavy.mapper")
@SpringBootApplication
public class CavyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CavyApplication.class, args);
	}

}
