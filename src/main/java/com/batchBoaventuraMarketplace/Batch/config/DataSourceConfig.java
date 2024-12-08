package com.batchBoaventuraMarketplace.Batch.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        Dotenv dotenv = Dotenv.load();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dotenv.get("DB_URL"));
        dataSource.setUsername(dotenv.get("DB_USERNAME"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));

        return dataSource;
    }
}