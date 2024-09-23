---
sidebar_position: 2
title: Setup
---

This section discusses the one-time setup in order to develop [Aristotle].

Prepare for Local Development
-----------------------------

### Installing Java & Maven (on Mac)

```bash
brew update
brew install openjdk@17
```

At the end of the last command prompt, something like the below will show up:

```bash
For the system Java wrappers to find this JDK, symlink it with
  sudo ln -sfn ...openjdk@17/libexec/openjdk.jdk .../JavaVirtualMachines/openjdk-17.jdk

openjdk@17 is keg-only, which means it was not symlinked into /usr/local,
because this is an alternate version of another formula.

If you need to have openjdk@17 first in your PATH, run:
  echo 'export PATH=".../openjdk@17/bin:$PATH"' >> .../.bash_profile

For compilers to find openjdk@17 you may need to set:
  export CPPFLAGS="-I.../openjdk@17/include"
```

Make sure to execute the `sudo ln -sfn`, `echo 'export PATH=...`, and the `export CPPFLAGS=` commands above

:::tip

Maven uses a separate JDK version, which can be seen via `mvn -v`. If it's not JDK 17, we should have Maven point
to our JDK 17 using [JAVA_HOME](https://stackoverflow.com/a/2503679):

```bash
$ /usr/libexec/java_home
/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

$ export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

:::

If we see something similar after typing the command with the version flag below we're good to go

```bash
$ java --version
openjdk 17.0.10 2021-01-19
OpenJDK Runtime Environment (build 17.0.10+9)
OpenJDK 64-Bit Server VM (build 17.0.10+9, mixed mode)
```

### Installing Docker Engine

<!-- markdown-link-check-disable -->
[Aristotle] has [Docker-based integration tests];
Docker can be installed by following its
[official instructions](https://docs.docker.com/desktop/install/mac-install/)
<!-- markdown-link-check-enable -->

Getting Source Code
-------------------

```bash
git clone git@github.com:paion-data/aristotle.git
cd aristotle
```

[Aristotle]: https://github.com/paion-data/aristotle/

[Docker-based integration tests]: https://github.com/paion-data/aristotle/blob/master/src/test/groovy/com/paiondata/aristotle/DockerComposeITSpec.groovy
