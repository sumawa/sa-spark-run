# SPARKRUN

### SparkRun is an ongoing project
  
	- a Spark Job Execution Service
	- launched via command line 
	- has two components (Runner for submitting jobs and Tracker for tracking jobs)

### Purpose of this project

    - It is a learning project, 
        - to explore dev approaches (BDD/TDD)
        - functional programming (effectful)
        - Using Blocker to manage blocking calls (DB/Third party APIs, etc)
        - Streaming
        
#### Following tech stack explored:	
##### Dev 
	        - Scala 2.12.0
	        - Cats 2.x (Effect - IO,ConcurrentEffect,ContextShift,Blocker,Timer etc.)
	        - fs2 (Streaming)
	        - pureConfig,Circe,http4s
	        
##### CI/CD

    - BDD: ScalaTest (or Cucumber maybe)
    - Property Based Testing (Scalacheck)
    - Maybe: SonarQube Scoverage

### SparkRun Spark Execution and Tracking Service

![alt text](sparkrun.png "SparkRun executor and Trakcing Service")

### SparkRun Design Goals

1. Launch via command line
2. Configurable via conf files and command line parameters
3. A "Job" is one unit of spark execution, representing a spark job.
5. There are two services "SparkRunner" and "Tracker"
4. SparkRunner service listens to a Job Source (SQL DB, FileSystem, Message Queues, others ...)
5. SparkRunner service detects pending jobs and submits in parallel via Spark Runner (or executor service)
6. Supports launching on multiple types of Servers (Standalone,Yarn,Kubernetes,...)
7. Tracker tracks multiple pending jobs 

### Upcoming enhancements

1. Kerberos support
2. Kubernets, Rabbit MQ, Kafka support
3. Use containers for integration testing.



	
