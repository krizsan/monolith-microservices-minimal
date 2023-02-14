# Monolith Application Prepared for Microservices
Example application showing an example of a monolith application that is developed as to facilitate extraction of microservices.
Minimal version implementing one single use case only.
This version of the application was developed using Java Jigsaw.

# Troubleshooting

## Running all tests in a module fails
When running all tests of a module in IntelliJ IDEA, all tests in the module fails.
An example of an error message that may occur is:
```
ClassFormatError accessible: module java.base does not "opens java.lang" to module spring.core
```
The solution is to add the "Do not use --module-path option" in the run configuration.