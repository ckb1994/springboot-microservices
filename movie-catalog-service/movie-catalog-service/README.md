<h2>Movie Microservice</h2>

Created 3 Spring boot applications.
<ol>
    <li>Movie Catalog service </li>
    <li>Movie Info service </li>
    <li>Rating data service </li>
</ol>

<h3> How To Make rest call from code </h3>
<ul>
    <li>Calling REST Api Programatically</li>
    <li>Using REST Client Library</li>
    <li>Spring Boot comes with client already in your classpath - RestTemplate</li>
</ul>

<h3>Using Rest Template</h3>
<b>Note : RestTemplate will be deprecated, 
webClient will be used in future.</b>

Movie movie = restTemplate.getForObject("http://localhost:8082/movies/"+rating.getMovieId(), Movie.class);

Create RestTemplate object
use getForObject methods of object with url to be hit and 
Expected response payload class with .class

<b>Remember:</b> There must be default constructor of that payload.
because, restTemplate first create object then 
set the parameters.

Beans in spring is singleton by default.

Create <b>A bean of RestTemplate</b> and consume it using @AutoWired

<h3>Using Web Client</h3>
This Web client is part of reactive programming space of
spring boot eco-system

Movie movie = webClientBuilder.build()
.get()
.uri("http://localhost:8082/movies/"+rating.getMovieId())
.retrieve()
.bodyToMono(Movie.class)
.block(); // Using block making sync call to Async

<h4>This is Asynchronous call</h4>
If you have selected Reactive web also while spring boot 
application creation then it will be available on class path
Otherwise add dependency in pom.xml


<h2> Returning List as response in one microservice to another not a good idea </h2>
<p>Create a Model and make a list in tht and return this object</p>


<h2>Service Discovery</h2>
Service Discovery is level of abstraction between client and server
<h4>Why Hard coded url are bad</h4>
<ul>
    <li>Changes require code change</li>
    <li>Dynamic URLs in the cloud</li>
    <li>Load Balancing</li>
    <li>Multiple Environment</li>
</ul>

<b>Client Side Service Discovery </b> : The discovery work being done by Client
<b>Server Side Service Discovery </b> : Client directly pass the message and service name to 
service discovery. Client doesn't discover the address/url of service.


<h3>Spring Cloud uses client side discovery</h3>
<h2> Technology to implement Service Discovery </h2>
<h4>Eureka</h4>
<h3>Netflix have given many open source libraries to microservices world</h3>
Eureka, zuul, Ribbon...etc


<b>Spring community hav made wrappers to use these above mentioned technologies</b>

Using Eureka,

                        Client (Eureka Client)  

                    Service discovery (Eureka Server)

                Service1      Service2      Service3
             (Eureka Client) (Eureka Client) (Eureka Client)

Service Discovery -> Eureka Server
Client, services -> Eureka Client

<b>Client -></b> consume the urls, 
<b>Services -></b> register themself

<h3>Steps</h3>
<ul>
    <li>Start up the Eureka Server</li>
    <li>Have microservices register (publish) using Eureka Client</li>
    <li>Have microservices locate (consume) using Eureka Client</li>
</ul>

<h3>How to create Eureka Server?</h3>
Same way we created spring boot application. Using start.spring.io
There are dependencies
1. Eureka Server ( for the services used as service discovery)
2. Eureka Discovery ( For client microservice)

Download spring boot application with Eureka Server dependency

Add @EnableEurekaServer annotation in main method class above
class.

Every Eureka server is client also. It also try to register itself.
To stop this user some property in application.properties

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

Actually, when multiple Eureka server is there then they register 
each other because if any one of them goes down, other will have 
all details.

In current project, There is only one Eureka server.

Add Eureka Client dependency to all3 microservices.
and provide spring cloud version in properties.(where java version is mentioned in pom.xml)

<h4>In properties tag :</h4>
<properties>
    <java.version>1.8</java.version>
    <spring-cloud.version>2020.0.0</spring-cloud.version>
</properties>

<h4>In dependencies tag : </h4>

<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
			<version>3.0.3</version>
</dependency>

<h4>Outside dependencies :</h4>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<h4>Add Repo also</h4>
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </repository>
</repositories>

<h3> Add Annotation </h3>
@EnableEurekaClient -> Add it in main method class

<h2>How does Eureka client knows the Eureka server and register</h2>
Currently we used default port for Eureka server.

Other-wise we need to add in client application.properties file 
about Eureka Server host and port.

==========Now Configuration completed ========
Consuming service is Movie catalog service. It will ask 
Discovery service Eureka for other two services url

We have @Bean to get RestTemplate,
Add @LoadBalanced 

@Bean
@LoadBalanced
public RestTemplate getRestTemplate(){
    return new RestTemplate();
}

What Does <b>@LoadBalanced</b> do?( With out this, discovery will not happen)
1. It does service discovery
2. discover service in load balanced way

Calling via Eureka, not directly

So instead of calling http://localhost:port/endpoint

call using Application name, Take application name from application.properties file
or Eureka server. 

"http://localhost:8083/ratingdata/users/"+userId

changed to 

"http://rating-data-service:8083/ratingdata/users/"+userId



@LoadBalanced :- 
It is also doing client side load balancing. 

Let's try by running jar at different port using below command.


java -Dserver.port=8206 -jar movie-info-service-0.01-SNAPSHOT.jar


once you run this command on cmd from jar location then it will start new server on given 8206 port.

You can verify it from Eureka server UI.

Now Application will work same as previous. Here Load balancing happens.

Yes This load balancing algo is not efficient but it does the load balancing.



===========================================================
<h2>Fault Tolerance and Resilience</h2>

Calling external API : Connecting to MovieDb to get information about movie

Create account in movieDb website
https://www.themoviedb.org/

Generate Api key, It will be used while making call to this movieDb

There will be sample Api, Use it to get the information


Now Check the response after using external api

<h3>How to make this resilient</h3>
Current application is not fault tolerant and resilient.
We have not handled any error yet.

<h2>Issues with Microservices </h2>
<ul>
    <li><b>microservice instance goes down</b> :- Solution1 : Run multiple Instances</li>
    <li><b>microservice instance is slow</b> :-</li>
Why  one slow service affect another service even neither of both are not calling
each other??
Because Slower service holds threads and it blocks threads and fast service also 
become slow. number of threads calling slow service increase nd affects fast 
service by holding maximum of thread for longer time.
<br><br><b>How to solve this problem??</b>
<ul><li><b>Use Timeout :</b> Removing threads when they are taking too 
much time</li>
<li><b>Use Circuit Breaker pattern : </b>Detect and don't send req for a duration</li></ul>
<br><b> How to set Timeout on spring restTemplate</b>
<br>
<b>Note :</b> Any service calling to another service, can have a timeout. 
In our scenario, movieCatelog service calling two services and 
Movie info service calling external movieDb Service.

Added below restTemplate code with timeout

HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
clientHttpRequestFactory.setConnectTimeout(3000);
return new RestTemplate(clientHttpRequestFactory);

<h3>Does this solve the issue : ???  : No </h3> 
<b>What is Issue now ?? : </b> Actually thread will wait for 
given timeout even response from another microservice is ready. 
This way every thread wait for 3 sec(time out in above code) and it will 
block the process and it will again slow down.

<br><b> How to use Circuit Breaker pattern??</b>
<ul>
    <li>Detect something is wrong</li>
    <li>Take Temporary steps to avoid the situation getting worse</li>
    <li>Deactivate the <b>problem component</b> so that it does not affect downstream components</li>
</ul>

The service taking time, calling service will block further call to this 
service. It will give enough time to recover that service and then start calling to check
the response time and all.

<h4>When to break the circuit?? -> </h4> 
On single failure or single timeout?? <b>No, never</b>

Go with some mechanism to break the circuit.

<h4>How to handle the call when circuit is broken??</h4>
fallback mechanism. An alternative mechanism.

<h5>What all options to do (fallback) in circuit break time.</h5>
<ul>
<li>Throw a Error, But this is not good idea</li>
<li>Return a fallback "default" response</li>
<li>Save previous responses (cache) and use that when possible</li>
</ul>

<h4>Why circuit breakers</h4>
<ul>
    <li>Failing Fast</li>
    <li>Fallback functionality</li>
    <li>Automatic recovery</li>
</ul>

<h4>Circuit Breaker Pattern</h4>
When to Break circuit || what to do when circuit breaks || When to resume requests

<h2>Nothing to worry much, we have framework for Circuit Breaker -> Hystrix</h2>
<h4>What is Hystrix??</h4>
<ul>
    <li>Open Source Library originally created by Netflix</li>
    <li>Implements circuit breaker pattern so you don't have to do so</li>
    <li>Give it the configuration params and it does the work</li>
    <li>Works well with spring boot</li>
</ul>
<h3>Adding Hystrix to spring boot microservice</h3>
<ul>
    <li>Add a Maven spring-cloud-starter-netflix-hystrix dependency</li>
    <li>Add @EnableCircuitBreaker annotations to the application class</li>
    <li>Add @HystrixCommand to methods that need circuit breaker</li>
    <li>Configure Hystrix behaviour</li>
</ul>
Got Our scenario, Adding Circuit breaker to movieCatelog service and 
killing movie info service to check the behaviour in case of circuit break.

1. Adding dependencies
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-hystrix -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>

2. @EnableCircuitBreaker in MovieCatalogServiceApplication class

3. @HystrixCommand in MovieCatelogResource class, above getCatalog method

4. Configure Hystrix Behaviour
    Change point 3 with fallback parameter

@HystrixCommand(fallbackMethod = "getFallBackCatelog")

and create a method with same name as fallBackMethod parameter value and same signature 
of original method(getCatalog).

//This method will get call when circuit breaks
public List<CatalogItem> getFallBackCatelog(@PathVariable("userId") String userId){
    return Arrays.asList(new CatalogItem("No Movies","",0));
}

//Fall back methods should be simple hard coded because if it also fails
then we need another fallback for this.


<h3>Testing it</h3>

Killed the movie info service and run the movie catelog service. Now circuit breaks
and fallback method got call.


<h3>How does Hystrix do all these stuff??</h3>
Using Hystrix proxy class that monitor all things, and redirect fallbackmethods


<h3>Now making change in scenario, In present scenario either of two microservice 
fails then circuit is breaking and same fallbackmethod is getting call</h3>

Enhancing th code and splitting both

splitted to two methods and used @HystrixCommand(fallbackMethod = "fallBackMethodName")
to them.

Still there is error. :- The Fallback does not get picked up at all -> Why??

Because of the proxy class.
proxy class - A wrapper around the instance of the API class


If two methods are in same class, hystrix is not going to work. 

<h2>Sorry, I did not understand well for this. Check out this video : https://www.youtube.com/watch?v=1EIb-4ipWFk&list=PLtp5ZM1VZTVFgsli3mFszXHkymTkk7WMN&index=18 </h2>

Solution is to keep those method in another class and do the need full.

Creating two service class, one for movie info and one for rating.

normal and fallback both methods should be in same service class.

Working well, tried by stopping rating service once and movie info service once.

<h3>Configuring Hystrix parameters</h3>

@HystrixCommand(fallbackMethod = "getFallbackUserRating",
    commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
    }
)

The values used in properties are subject to discuss as per requirement

<h4>Hystrix Dashboard</h4>
All details in visual format

Steps
1. Add dependencies (dashboard and actuator)
   <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-hystrix-dashboard -->
   	<dependency>
   		<groupId>org.springframework.cloud</groupId>
   		<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
   		<version>2.2.0.RELEASE</version>
   	</dependency>

   	<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator -->
   	<dependency>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-actuator</artifactId>
   		<version>2.4.5</version>
   	</dependency>

2. Add @EnableHystrixDashboard in catalog application class

3. Add below line in applicaiton.properties file
   management.endpoints.web.exposure.include=hystrix.stream
   
Restart server and open url
localhost:8081/hystrix

paster actuator url in text field
http://localhost:8081/actuator/hystrix.stream

open 
localhost:8081/catalog/username and click multiple time 4-5 times and switch 
to localhost:8081/hystrix and see the data


<h3>Bulkhead Pattern</h3>
Compartment like pattern 
More details : https://www.youtube.com/watch?v=Kh3HxWk8YF4&list=PLtp5ZM1VZTVFgsli3mFszXHkymTkk7WMN&index=22

</ul>



<h2>Microservice Configuration</h2>
Examples
<ul>
    <li>Database connections</li>
    <li>Credentials</li>
    <li>Feature flags</li>
    <li>Business Logic configuration parameters</li>
    <li>Scenario Testing</li>
    <li>Spring Boot configuration</li>
</ul>

<h4>For only config changes, It is not required to do build, test the application etc. We can do
it directly without impacting anything</h4>

<h3>Goal</h3>
<ul>
<li>Externalized</li>
<li>Environment specific</li>
<li>Consistent</li>
<li>Version History</li>
<li>Real-time Management</li>
</ul>

