package org.ikigaidigital.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeDepositConfiguration {
    @Bean
    public TimeDepositCalculator timeDepositCalculator() {
        return new TimeDepositCalculator();
    }
}
