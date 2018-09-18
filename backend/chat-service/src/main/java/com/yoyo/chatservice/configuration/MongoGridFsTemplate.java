package com.yoyo.chatservice.configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages ={"com.kanz.chatservice.repository.mongo"})
public class MongoGridFsTemplate extends AbstractMongoConfiguration{

    @Value("${mongo.address}")
    private String mongoAddress;
    @Value("${mongo.database}")
    private String mongoDatabase;
    @Value("${mongo.port}")
    private int port;

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception{
        return new GridFsTemplate(mongoDbFactory(),mappingMongoConverter());
    }

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
    public CustomConversions customConversions() {
        return super.customConversions();
    }
}
