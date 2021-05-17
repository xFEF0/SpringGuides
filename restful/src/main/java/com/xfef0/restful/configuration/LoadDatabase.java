package com.xfef0.restful.configuration;

import com.xfef0.restful.Status;
import com.xfef0.restful.entity.Employee;
import com.xfef0.restful.entity.Order;
import com.xfef0.restful.repository.EmployeeRepository;
import com.xfef0.restful.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository) {
        return args -> {
            employeeRepository.save(new Employee("Lionel", "Messi", "footballer"));
            employeeRepository.save(new Employee("Sergio", "Martinez", "boxer"));
            employeeRepository.findAll().forEach(employee -> LOG.info("Preloaded: " + employee));
        };
    }
}
