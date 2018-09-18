package com.yoyo.conferenceservice.configuration.mongodb;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages ={"com.kanz.conferenceservice.repository.mongodb"})
public class MongoDBConfiguration extends AbstractMongoConfiguration {

    @Value("${mongodb.address}")
    private String mongoAddress;
    @Value("${mongodb.database}")
    private String mongoDatabase;
    @Value("${mongodb.port}")
    private int port;


    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(mongoAddress,port);
    }

    @Bean(name="mongoCustomConversions")
    @Override
    public CustomConversions customConversions(){
        return super.customConversions();
    }
}
