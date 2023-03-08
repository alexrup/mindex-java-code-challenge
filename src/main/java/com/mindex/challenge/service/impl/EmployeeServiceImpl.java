package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        LOG.debug("Calculating ReportingStructure for employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        // Store directReport Employees in a Set to ensure uniqueness of directReport Employees.
        Set<Employee> directReportsSet = new HashSet<>();

        // Perform non-recursive DFS to find all Employees in directReports field:
        Stack<Employee> stack = new Stack<>();
        stack.push(employee);

        while (!stack.isEmpty()) {
            Employee current = stack.pop();
            if (current.getDirectReports() != null) {
                for (Employee e : current.getDirectReports()) {
                    // Look up each directReport Employee by ID, because directReports field might only contain employeeId Strings.
                    Employee reportEmployee = employeeRepository.findByEmployeeId(e.getEmployeeId());
                    if (reportEmployee != null) {
                        directReportsSet.add(reportEmployee);
                        stack.push(reportEmployee);
                    } else {
                        throw new RuntimeException("Invalid directReport employeeId: " + id);
                    }
                }
            }
        }

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(directReportsSet.size());

        return reportingStructure;
    }
}
