package com.mindex.challenge;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallengeApplicationTests {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompensationService compensationService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testReportingStructure() {

        Employee lennon = employeeService.read("16a596ae-edd3-4847-99fe-c4518e82c86f");
        ReportingStructure lennonReportingStructure = employeeService.getReportingStructure("16a596ae-edd3-4847-99fe-c4518e82c86f");

        assertNotNull(lennon);
        assertNotNull(lennonReportingStructure);
        assertEquals(lennon.getEmployeeId(), lennonReportingStructure.getEmployee().getEmployeeId());
        assertEquals(lennonReportingStructure.getNumberOfReports(), 4);

        Employee ringo = employeeService.read("03aa1462-ffa9-4978-901b-7c001562cf6f");
        ReportingStructure ringoReportingStructure = employeeService.getReportingStructure("03aa1462-ffa9-4978-901b-7c001562cf6f");

        assertNotNull(ringo);
        assertNotNull(ringoReportingStructure);
        assertEquals(ringo.getEmployeeId(), ringoReportingStructure.getEmployee().getEmployeeId());
        assertEquals(ringoReportingStructure.getNumberOfReports(), 2);

        Employee paul = employeeService.read("b7839309-3348-463b-a7e3-5de1c168beb3");
        ReportingStructure paulReportingStructure = employeeService.getReportingStructure("b7839309-3348-463b-a7e3-5de1c168beb3");

        assertNotNull(paul);
        assertNotNull(paulReportingStructure);
        assertEquals(paul.getEmployeeId(), paulReportingStructure.getEmployee().getEmployeeId());
        assertEquals(paulReportingStructure.getNumberOfReports(), 0);

        Employee newGuy = new Employee();
        newGuy.setFirstName("Alex");
        newGuy.setLastName("Rupprecht");
        newGuy.setPosition("Software Engineer");
        newGuy = employeeService.create(newGuy);
        assertNotNull(newGuy);

        List<Employee> lennonReports = lennon.getDirectReports();
        lennonReports.add(newGuy);
        lennon.setDirectReports(lennonReports);
        employeeService.update(lennon);
        lennonReportingStructure = employeeService.getReportingStructure("16a596ae-edd3-4847-99fe-c4518e82c86f");
        assertEquals(lennonReportingStructure.getNumberOfReports(), 5);

        paul.setDirectReports(Collections.singletonList(newGuy));
        employeeService.update(paul);
        paulReportingStructure = employeeService.getReportingStructure("b7839309-3348-463b-a7e3-5de1c168beb3");
        assertEquals(paulReportingStructure.getNumberOfReports(), 1);

        // Test cycle detection.
        try {
            paul.setDirectReports(Collections.singletonList(lennon));
            employeeService.update(paul);
            lennonReportingStructure = employeeService.getReportingStructure("16a596ae-edd3-4847-99fe-c4518e82c86f");
            assertNull(lennonReportingStructure);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Invalid directReport employeeId: 16a596ae-edd3-4847-99fe-c4518e82c86f");
        }
    }


    @Test
    public void testCompensation() {

        Employee ringo = employeeService.read("03aa1462-ffa9-4978-901b-7c001562cf6f");
        Compensation ringoComp = new Compensation();
        ringoComp.setEmployee(ringo);
        ringoComp.setEffectiveDate(LocalDate.parse("2022-12-12"));
        ringoComp.setSalary(10000);

        ringoComp = compensationService.create(ringoComp);
        assertNotNull(ringoComp);
        assertEquals(ringoComp.getEmployee().getEmployeeId(), ringo.getEmployeeId());
        assertEquals(ringoComp.getEffectiveDate(), LocalDate.parse("2022-12-12"));
        assertEquals(ringoComp.getSalary(), 10000);


        // Test can't create multiple compensations for same employee:

        Employee lennon = employeeService.read("16a596ae-edd3-4847-99fe-c4518e82c86f");
        Compensation lennonComp1 = new Compensation();
        lennonComp1.setEmployee(lennon);
        lennonComp1.setEffectiveDate(LocalDate.parse("2022-12-12"));
        lennonComp1.setSalary(10000000);

        Compensation lennonComp2 = new Compensation();
        lennonComp2.setEmployee(lennon);
        lennonComp2.setEffectiveDate(LocalDate.parse("2022-12-12"));
        lennonComp2.setSalary(20000000);

        lennonComp1 = compensationService.create(lennonComp1);
        assertNotNull(lennonComp1);

        try {
            compensationService.create(lennonComp2);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Compensation for employee already exists: 16a596ae-edd3-4847-99fe-c4518e82c86f");
        }


        // Test can't create compensation for employees that don't exist:

        Employee tempEmployee = new Employee();
        tempEmployee.setEmployeeId("12345");
        Compensation compensation = new Compensation();
        compensation.setEmployee(tempEmployee);
        compensation.setEffectiveDate(LocalDate.parse("2022-12-12"));
        compensation.setSalary(10000000);

        try {
            compensationService.create(compensation);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Invalid employeeId: 12345");
        }
    }

}
