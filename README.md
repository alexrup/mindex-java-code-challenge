# How I Approached This Challenge:

### General Notes:

* I attempted to follow all instructions to the letter, as much as possible.
* I Debated whether to include the requested methods for Compensation and ReportingStructure all within the existing EmployeeController and EmployeeService. I opted to separate Compensation to its own controller and service, but left ReportingStructure methods in the EmployeeService and EmployeeController. I tried to strike a balance between not overly complicating this small application, while still trying to demonstrate software design best practices.

### Notes For Task 2:

* No "update" endpoint was requested in the instructions. I considered adding one, but opted to only include the 2 required for create/read. If desired, a PUT method could be added to update a given Compensation, similarly to how it's handled for an Employee.
* When creating a new Compensation, only 1 is allowed per employeeId. If you try to add multiple Compensations for the same Employee, it will throw a 500 after the first. This was technically not specified in the instructions, but seemed a reasonable restriction in testing.
* When creating a new Compensation, it will only be created if the Employee actually exists in the DB. If no matching Employee exists, it throws a 500. Again, this wasn't technically in the instructions but seemed like a reasonable restriction in testing.
* Dates are accepted in format "yyyy-MM-dd". I considered adding more exhaustive serialization/deserialization to support multiple date formats, time stamps, etc... In the end decided to keep it simple and handle the one format, and to simply let Jackson perform serialization/deserialization using the @JsonFormat annotation.




# Coding Challenge
## What's Provided
A simple [Spring Boot](https://projects.spring.io/spring-boot/) web application has been created and bootstrapped 
with data. The application contains information about all employees at a company. On application start-up, an in-memory 
Mongo database is bootstrapped with a serialized snapshot of the database. While the application runs, the data may be
accessed and mutated in the database without impacting the snapshot.

### How to Run
The application may be executed by running `gradlew bootRun`.

### How to Use
The following endpoints are available to use:
```
* CREATE
    * HTTP Method: POST 
    * URL: localhost:8080/employee
    * PAYLOAD: Employee
    * RESPONSE: Employee
* READ
    * HTTP Method: GET 
    * URL: localhost:8080/employee/{id}
    * RESPONSE: Employee
* UPDATE
    * HTTP Method: PUT 
    * URL: localhost:8080/employee/{id}
    * PAYLOAD: Employee
    * RESPONSE: Employee
```
The Employee has a JSON schema of:
```json
{
  "type":"Employee",
  "properties": {
    "employeeId": {
      "type": "string"
    },
    "firstName": {
      "type": "string"
    },
    "lastName": {
          "type": "string"
    },
    "position": {
          "type": "string"
    },
    "department": {
          "type": "string"
    },
    "directReports": {
      "type": "array",
      "items" : "string"
    }
  }
}
```
For all endpoints that require an "id" in the URL, this is the "employeeId" field.

## What to Implement
Clone or download the repository, do not fork it.

### Task 1
Create a new type, ReportingStructure, that has two properties: employee and numberOfReports.

For the field "numberOfReports", this should equal the total number of reports under a given employee. The number of 
reports is determined to be the number of directReports for an employee and all of their distinct reports. For example, 
given the following employee structure:
```
                    John Lennon
                /               \
         Paul McCartney         Ringo Starr
                               /        \
                          Pete Best     George Harrison
```
The numberOfReports for employee John Lennon (employeeId: 16a596ae-edd3-4847-99fe-c4518e82c86f) would be equal to 4. 

This new type should have a new REST endpoint created for it. This new endpoint should accept an employeeId and return 
the fully filled out ReportingStructure for the specified employeeId. The values should be computed on the fly and will 
not be persisted.

### Task 2
Create a new type, Compensation. A Compensation has the following fields: employee, salary, and effectiveDate. Create 
two new Compensation REST endpoints. One to create and one to read by employeeId. These should persist and query the 
Compensation from the persistence layer.

## Delivery
Please upload your results to a publicly accessible Git repo. Free ones are provided by Github and Bitbucket.
