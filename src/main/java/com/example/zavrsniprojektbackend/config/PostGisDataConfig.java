package com.example.zavrsniprojektbackend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PostGisDataConfig {

    @Value("${postgis.datasource.url}")
    private String url;

    @Value("${postgis.datasource.username}")
    private String username;

    @Value("${postgis.datasource.password}")
    private String password;

    @Value("${postgis.datasource.driver-class-name}")
    private String driverClassName;

    @Bean(name = "postGisDataSource")
    public DataSource postGisDataSource() {
        HikariDataSource dataSource = DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();

        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        return dataSource;
    }
}