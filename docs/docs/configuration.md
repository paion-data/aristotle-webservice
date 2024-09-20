---
sidebar_position: 3
title: Configuration
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

The configurations in this page can be set from several sources in the following order:

1. the [operating system's environment variables]; for instance, an environment variable can be set with
   `export NEO4J_URI="bolt://db:7687"`
2. the [Java system properties]; for example, a Java system property can be set using
   `System.setProperty("NEO4J_URI", "bolt://db:7687")`
3. a **.yaml** file placed under CLASSPATH. This file can be put under `src/main/resources` source directory with
   contents, for example, `uri: bolt://db:7687`

Core Properties
---------------

:::note

The following configurations can be placed in the properties file called **application.yaml**

:::

- **username**: Persistence DB username (needs have both Read and Write permissions).
- **password**: The persistence DB user password.
- **uri**: The persistence DB URL, such as "bolt://db:7687".
- **database**: The persistence DB database name:

There are two editions of self-managed Neo4j to choose from, the Community Edition (CE) and the Enterprise Edition (EE). The Enterprise Edition includes all that Community Edition offers, plus extra enterprise requirements such as backups, clustering, and failover capabilities.

After Neo4j is updated to 4.× and the service is started, two libraries are available by default, as shown in the following figure. The directory is also changed to data/databases/, where the neo4j database is the default library after login. [Official introduction](https://neo4j.com/docs/operations-manual/4.0/introduction/):

- **system**: System database, containing metadata for database management systems and security configurations;
- **neo4j**: The default database, a single database of user data. Its default name is neo4j.

If you are using the EE and have created multiple databases, you need to modify the `database` to the name of the database you created.

[Java system properties]: https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html

[operating system's environment variables]: https://docs.oracle.com/javase/tutorial/essential/environment/env.html
