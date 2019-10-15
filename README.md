## Technical Assignment

### Goal
Implement an application that will evaluate an *HTML resource* referenced by a URI. The application should return a list with all the links within the *resource page* and the result of attempting to reach them.

### Technical Details

Create an HTTP server implementing a REST API with one endpoint that:
* Receive as input the URI reference to an HTML resource
* Output in JSON format a list with all links found in the resource and the result of attempting to reach each of them. In case of failure, add the HTTP error code and a succinct error message.

### Example

For a given input *URL="https://anexample.com"*, the respective HTML resource is:

```html
<html>
    <body>
        <a href="https://www.dailymail.co.uk/home/index.html">link1</a>
        <a href="https://disney.fandom.com/wiki/Potatoland">link2</a>
    </body>
</html>
```

The service response should be something like:

```json
[{
	"uri": "https://www.dailymail.co.uk/home/index.html",
	"reachable": true,
	"error_code": null,
	"error_message": null
 },
 {
	"uri": "https://disney.fandom.com/wiki/Potatoland",
	"reachable": false,
	"error_code": 404,
	"error_message": "Not Found"
 }]
```

### Specifications
* Implement using Java and building setup with prefered build tool. Also, feel free to use prefered framework or library to build the application.
* Defining how the REST API receives the input is up to you, the output should be as described above.
* Add support for locally running the application with Docker. 
* Extend this README file explaining how to run your application with docker and how to use the endpoint.


### Evaluation
The following will be taken into account when evaluating your solution:
* Clean code and project structure.
* Relevant test coverage for the solution.
* Application performance when handling the request.
* Any corner cases are covered on the solution.

Thank you very much for taking the time to work on this assignment. Have fun coding!

----
### Execution Details: 

Input should be given as request parameter in service path, as example: 
```html
# GET method with URI as parameter
http://127.0.0.1/wrapper?uri=http://www.google.com.br
```
If necessary to check some execution details, please add "debug=true" as parameter too, it's optional parameter. Check below:
```html
# GET method with URI and debug as parameteres
http://127.0.0.1/wrapper?uri=http://www.google.com.br&debug=true
```
Output will be presented as requested in json format.

In order to build, test and execute as a docker container, just follow below commands:
```cmd
# Clone this project
git clone https://github.com/Kreditech-Recruiting/technical-challenge-developer-fernando.git
```
---
**To perform tests in project:**

PS_1: Maven auxiliary shells(mvnw/mvnw.cmd) were added to source project for convenience. 
PS_2: Execution time based on computer: Mac Mini - Intel Core i3 (3,6 GHz)

Run all tests developed, Unit and Integration tests, execution time: ~ 4 min.
```cmd
./mvnw clean compile test
```
Run just unit tests, execution time: ~ 1.5 min
```cmd
./mvnw test -Dtest=WrapperUnitTests
```
Run just integration tests,execution time: ~ 4 min
```cmd
./mvnw test -Dtest=WrapperIntegrationTests  
```
----
Generate docker package:
```cmd
./mvnw clean package spring-boot:repackage
```
Load and start service as docker container:
```cmd
docker container run -p 8080:8080 -d --name wrapper com.yamanaka/htmlwrapper:1.0 
docker start wrapper
```
Check the service using docker image IP, port 8080 and "/wrapper" context.

To stop and remove image:
```cmd
docker stop wrapper
docker rm wrapper
```


