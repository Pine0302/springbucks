package com.example.springbucks;

import com.example.springbucks.mapper.CoffeeMapper;
import com.example.springbucks.model.Coffee;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
@Slf4j
@MapperScan("com.example.springbucks.mapper")
public class SpringbucksApplication implements ApplicationRunner {
	@Autowired
	private CoffeeMapper coffeeMapper;

	public static void main(String[] args) {
		SpringApplication.run(SpringbucksApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception{
		generateArtifacts();
		/*Coffee c = Coffee.builder().name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"),20.0))
				.build();
		int count = coffeeMapper.save(c);
		log.info("Save {} Coffee: {}",count ,c);

		c = Coffee.builder().name("lattee")
				.price(Money.of(CurrencyUnit.of("CNY"),30.0))
				.build();
		count = coffeeMapper.save(c);
		log.info("Save {} Coffee: {}",count ,c);
		c = coffeeMapper.findById(c.getId());
		log.info("Find Coffee: {}",c);*/

	}

	private function generateArtifacts() throws Exception{
		List<String> warnings = new ArrayList<>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(this.getClass().getResourceAsStream("/generaatorConfig.xml"));
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,callback,warnings);
		myBatisGenerator.generate();

	}


}
