package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        // Check to make sure Employee actually exists first.
        Employee employee = employeeRepository.findByEmployeeId(compensation.getEmployee().getEmployeeId());
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + compensation.getEmployee().getEmployeeId());
        }

        // Check to make sure Employee doesn't already have a Compensation.
        Compensation existing = compensationRepository.findByEmployeeEmployeeId(employee.getEmployeeId());
        if (existing != null) {
            throw new RuntimeException("Compensation for employee already exists: " + compensation.getEmployee().getEmployeeId());
        }

        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Creating employee with id [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployeeEmployeeId(employeeId);
        if (compensation == null) {
            throw new RuntimeException("Invalid compensation: " + employeeId);
        }

        return compensation;
    }

}
