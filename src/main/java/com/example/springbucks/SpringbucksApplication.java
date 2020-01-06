package com.example.springbucks;

import com.example.springbucks.mapper.CoffeeMapper;
import com.example.springbucks.model.Coffee;
import com.example.springbucks.model.CoffeeExample;
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
import java.util.Date;
import java.util.List;

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
		//generateArtifacts();
		playWithArtifacts();
	}

	private void generateArtifacts() throws Exception{
		List<String> warnings = new ArrayList<>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(this.getClass().getResourceAsStream("/generatorConfig.xml"));
		log.info("config: {}",config);
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,callback,warnings);
		myBatisGenerator.generate(null);
	}

	private void playWithArtifacts(){
		Coffee quechao = new Coffee()
				.withName("quechao")
				.withPrice(Money.of(CurrencyUnit.of("CNY"),20.0))
				.withCreateTime(new Date())
				.withUpdateTime(new Date());

		coffeeMapper.insert(quechao);

		Coffee latte = new Coffee()
				.withName("latte")
				.withPrice(Money.of(CurrencyUnit.of("CNY"),20.0))
				.withCreateTime(new Date())
				.withUpdateTime(new Date());
		coffeeMapper.insert(latte);

		Coffee s = coffeeMapper.selectByPrimaryKey(1L);
		log.info("Coffee {} ",s);

		CoffeeExample example = new CoffeeExample();
		example.createCriteria().andNameEqualTo("quechao");
		List<Coffee> list = coffeeMapper.selectByExample(example);
		list.forEach(e->log.info("selectByExample: {}",e));

	}



}
