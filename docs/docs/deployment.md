---
sidebar_position: 4
title: Deployment
description: Aristotle deployment guide
---

[//]: # (Copyright 2024 Paion Data)

[//]: # (Licensed under the Apache License, Version 2.0 &#40;the "License"&#41;;)
[//]: # (you may not use this file except in compliance with the License.)
[//]: # (You may obtain a copy of the License at)

[//]: # (    http://www.apache.org/licenses/LICENSE-2.0)

[//]: # (Unless required by applicable law or agreed to in writing, software)
[//]: # (distributed under the License is distributed on an "AS IS" BASIS,)
[//]: # (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[//]: # (See the License for the specific language governing permissions and)
[//]: # (limitations under the License.)

This section discusses deploying [Aristotle] in production.

Prepare for Production Development
----------------------------------

:::note

We assume an Ubuntu 22.04+ server is used for deployment.

:::

### Installing JDK 17

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

If we see something similar after typing the command with the version flag below we're good to go

```bash
$ java -version
openjdk version "17.0.11" 2024-04-16
OpenJDK Runtime Environment (build 17.0.11+9-Ubuntu-120.04.2)
OpenJDK 64-Bit Server VM (build 17.0.11+9-Ubuntu-120.04.2, mixed mode, sharing)
```

### Installing Maven

```bash
sudo apt install maven
```

If we see something similar after typing the command with the version flag below we're good to go

```bash
$ mvn -version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.11, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-182-generic", arch: "amd64", family: "unix"
```

In the example, Maven is obviously using the correct JDK, so there is no need to set the JAVA_HOME environment variable
extra. However, if you want to explicitly set JAVA_HOME, or in some cases (for example, when there are multiple JDK
installations) make sure Maven always uses a specific JDK 17, You can add the following lines to your shell
configuration file (such as.bashrc,.zshrc, or.profile) :

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

Packaging Up Aristotle
----------------------

```bash
git clone https://github.com/paion-data/aristotle.git
cd aristotle

export NEO4J_URI=YOUR_NEO4J_URI
export NEO4J_USERNAME=YOUR_NEO4J_USERNAME
export NEO4J_PASSWORD=YOUR_NEO4J_PASSWORD
export NEO4J_DATABASE=YOUR_NEO4J_DATABASE

mvn clean package
```

[Aristotle] is built on [Springboot](https://spring.io/projects/spring-boot) and has a built-in web container, which we
used maven to package into a jar file.

Starting Aristotle
------------------

```bash
java -jar target/Aristotle-1.0-SNAPSHOT.jar
```

The web service will run on port **8080**.

### Getting OpenAPI Documentation

You can access the OpenAPI documentation at **http://localhost:8080/doc.html**. This documentation is built using
__Swagger 2__ and enhanced with __Knife4J__.

[Aristotle]: https://aristotle-ws.com

Troubleshooting
---------------

### Starting Aristotle throws the "Failed to execute CommandLineRunner" Error

For Neo4J database, Aristotle automatically
[creates couple of database constraints](https://github.com/paion-data/aristotle/blob/master/src/main/java/com/paiondata/aristotle/config/ConstraintInitializer.java)
at its very first contact to the database. If the [startup](#starting-aristotle) results in

```text
java.lang.IllegalStateException: Failed to execute CommandLineRunner
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:774) ~[spring-boot-2.7.3.jar!/:2.7.3]
	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:755) ~[spring-boot-2.7.3.jar!/:2.7.3]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) ~[spring-boot-2.7.3.jar!/:2.7.3]
	...
Caused by: org.neo4j.driver.exceptions.DatabaseException: Unable to create Constraint( name='constraint_1c8dc611', type='UNIQUENESS', schema=(:User {oidcid}) ):
Both Node(516397) and Node(517024) have the label `User` and property `oidcid` = 'user42fd5D645'. Note that only the first found violation is shown.
	at org.neo4j.driver.internal.util.Futures.blockingGet(Futures.java:111) ~[neo4j-java-driver-4.4.9.jar!/:4.4.9-e855bcc800deff6ddcf064c822314bb5c8d08c53]
	at org.neo4j.driver.internal.InternalTransaction.run(InternalTransaction.java:58) ~[neo4j-java-driver-4.4.9.jar!/:4.4.9-e855bcc800deff6ddcf064c822314bb5c8d08c53]
	at org.neo4j.driver.internal.AbstractQueryRunner.run(AbstractQueryRunner.java:34) ~[neo4j-java-driver-4.4.9.jar!/:4.4.9-e855bcc800deff6ddcf064c822314bb5c8d08c53]
	...
```

then it is very likely that the Neo4J database already contains some data that prevents the constraints from being
created. We recommend empty the database and start Aristotle again.
