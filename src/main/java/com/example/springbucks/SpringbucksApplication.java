package com.example.springbucks;

import com.example.springbucks.converter.MoneyReadConverter;
import com.example.springbucks.mapper.CoffeeMapper;
import com.example.springbucks.mapper.CoffeeMapperPageHelper;
import com.example.springbucks.model.Coffee;
import com.example.springbucks.model.CoffeeExample;
import com.example.springbucks.model.MongoCoffee;
import com.github.pagehelper.PageInfo;
import com.mongodb.Mongo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

@SpringBootApplication
@Slf4j
@MapperScan("com.example.springbucks.mapper")
public class SpringbucksApplication implements ApplicationRunner {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private CoffeeMapper coffeeMapper;

	@Autowired
	private CoffeeMapperPageHelper coffeeMapperPageHelper;

	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private JedisPoolConfig jedisPoolConfig;

	public static void main(String[] args) {
		SpringApplication.run(SpringbucksApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception{
		//generateArtifacts();
		//playWithArtifacts();
		//palyWithPageHelper();
	//	playWithMongoCoffee();
		playWithJedis();
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

	/**
	 * mysql insert&query
	 */
	private void playWithArtifacts(){
		Coffee quechao = new Coffee()
				.withName("quechao")
				.withPrice(Money.of(CurrencyUnit.of("CNY"),20.0))
				.withCreateTime(new Date())
				.withUpdateTime(new Date());
		Integer i;
		i = coffeeMapper.insert(quechao);
		log.info("第一次插入返回： {} ",i);
		Coffee latte = new Coffee()
				.withName("latte")
				.withPrice(Money.of(CurrencyUnit.of("CNY"),20.0))
				.withCreateTime(new Date())
				.withUpdateTime(new Date());
		i = coffeeMapper.insert(latte);
		log.info("第二次插入返回： {} ",i);
		log.info("返回值与插入条数有关，与插入的id值无关 ");
		Coffee s = coffeeMapper.selectByPrimaryKey(1L);
		log.info("Coffee {} ",s);

		CoffeeExample example = new CoffeeExample();
		example.createCriteria().andNameEqualTo("quechao");
		List<Coffee> list = coffeeMapper.selectByExample(example);
		list.forEach(e->log.info("selectByExample: {}",e));

	}

	/**
	 * pageHelper
	 */
	private void palyWithPageHelper(){
		List<Coffee> list1 = coffeeMapperPageHelper.findAllWithRowBounds(new RowBounds(1,3));
		list1.forEach(e->log.info("coffee_list1: {}",e));
		List<Coffee> list2 = coffeeMapperPageHelper.findAllWithRowBounds(new RowBounds(2,3));
		list2.forEach(e->log.info("coffee_list2: {}",e));
		List<Coffee> list3 = coffeeMapperPageHelper.findAllWithRowBounds(new RowBounds(1,0));
		list3.forEach(e->log.info("coffee_list3: {}",e));
		List<Coffee> list4 = coffeeMapperPageHelper.findAllWithParam(2,1);
		list4.forEach(e->log.info("coffee_list4: {}",e));
		PageInfo page = new PageInfo(list4);
		log.info("pageinfo: {}",page);
	}

	@Bean
	public MongoCustomConversions mongoCustomerConversions(){
		return new MongoCustomConversions(Arrays.asList(new MoneyReadConverter()));
	}


	public void playWithMongoCoffee(){
		MongoCoffee espresso = MongoCoffee.builder()
				.name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"), 20.0))
				.createTime(new Date())
				.updateTime(new Date())
				.build();
		MongoCoffee saved = mongoTemplate.save(espresso);
		log.info("saved coffee {} ", saved);

		List<MongoCoffee> list = mongoTemplate.find(
				Query.query(Criteria.where("name").is("latte")), MongoCoffee.class);
		log.info("find {} coffee.", list.size());
		list.forEach(c -> log.info("coffee:{}", c));
	}

	public void playWithJedis(){
		log.info(jedisPoolConfig.toString());

		CoffeeExample example = new CoffeeExample();
		example.createCriteria().andNameEqualTo("latte");
		List<Coffee> coffeeList = coffeeMapper.selectByExample(example);

		try (Jedis jedis = jedisPool.getResource()) {
			coffeeList.forEach(c->{
				log.info("list : {}",c);
				jedis.hset("coffee-menu",
						c.getName(),Long.toString(c.getPrice().getAmountMinorLong()));
			});
			Map<String,String> menu = jedis.hgetAll("coffee-menu");
			String lattePrice = jedis.hget("coffee-menu","latte");
			log.info("Menu : {}",menu);
			log.info("lattePrice : {}",lattePrice);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}


	@Bean
	@ConfigurationProperties("redis")
	public JedisPoolConfig jedisPoolConfig(){
		return new JedisPoolConfig();
	}

	@Bean(destroyMethod = "close")
	public JedisPool jedisPool(@Value("${redis.host}") String host){
		return new JedisPool( (jedisPoolConfig()),host);
	}
}
