package com.aveng.vnapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author apaydin
 */
@Configuration
@EnableJpaRepositories("com.aveng.vnapp.repository")
@EnableTransactionManagement
public class DatabaseConfiguration {

}
