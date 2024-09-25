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

### Installing Java (on Ubuntu)

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

### Packaging Aristotle

```bash
git clone git@github.com:paion-data/aristotle.git
export NEO4J_URI=YOUR_NEO4J_URI
export NEO4J_USERNAME=YOUR_NEO4J_USERNAME
export NEO4J_PASSWORD=YOUR_NEO4J_PASSWORD
export NEO4J_DATABASE=YOUR_NEO4J_DATABASE
mvn clean package
```

[Aristotle] is built on [Springboot](https://spring.io/projects/spring-boot) and has a built-in web container, which we
used maven to package into a jar file.

### Running the JAR Package

```bash
java -jar target/Aristotle-1.0-SNAPSHOT.jar
```

The web service will run on port **8080**.

### Getting OpenAPI Documentation

You can access the OpenAPI documentation at **http://localhost:8080/doc.html**. This documentation is built using **Swagger 2** and enhanced with **Knife4J**.

[Aristotle]: https://aristotle-ws.com
