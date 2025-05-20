package com.kh.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
@ComponentScan(basePackages = {"com.kh.utils"})
public class UtilsConfig {

    @Autowired
    private Environment env;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", env.getProperty("cloudinary.cloudName"),
                "api_key", env.getProperty("cloudinary.apiKey"),
                "api_secret", env.getProperty("cloudinary.apiSecret"),
                "secure", true));
    }

    @Bean
    public jakarta.validation.Validator validator() {
        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }
}
