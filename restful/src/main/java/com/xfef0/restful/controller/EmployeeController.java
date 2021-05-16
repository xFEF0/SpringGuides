package com.xfef0.restful.controller;

import com.xfef0.restful.entity.Employee;
import com.xfef0.restful.exception.EmployeeNotFoundException;
import com.xfef0.restful.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/employees")
    public List<Employee> all() {
        return repository.findAll();
    }

    @PostMapping("/employees")
    public Employee newEmployee(@RequestBody Employee newEmployee) {
        return repository.save(newEmployee);
    }

    @GetMapping("/employee/{id}")
    public Employee one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @PutMapping("/employee/{id}")
    public Employee replaceEmployee(@RequestBody Employee newEmployee,
                                    @PathVariable Long id) {
        return repository.findById(id)
                .map(
                        employee -> {
                            employee.setName(newEmployee.getName());
                            employee.setRole(newEmployee.getRole());
                            return repository.save(employee);
                        }
                )
                .orElseGet(
                        () -> {
                            newEmployee.setId(id);
                            return repository.save(newEmployee);
                        }
                );
    }

    @DeleteMapping("/employee/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
