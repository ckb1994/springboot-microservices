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


