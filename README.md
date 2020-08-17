
# Optra Java-based sample skill

This is a sample Java-based skill you can build and run on your Optra Compute device.


# Prerequisites

## Java Development Kit (JDK)

The JDK is required to build this example. There are several ways to install the JDK, depending on your operating system. Find downloads here: https://jdk.java.net/15.

<hr>

## Docker


**Windows** and **Mac**: We've tested this project with [Docker Desktop](https://www.docker.com/products/docker-desktop). Once installed, verify that your version is at 19.03.0 or later.

**Linux**: Follow the instructions here: https://docs.docker.com/engine/install/ubuntu/

Once you have docker installed, you can verify your installation by running docker <em>hello world</em>.

```> docker run hello-world```

<hr>

## Docker buildx


To build multi-architecture docker images (for example, those that can be run on ARM-based devices), you'll need docker buildx. To enable buildx, you'll need to do the following:

**Windows**: 

```set DOCKER_CLI_EXPERIMENTAL=enabled```

**Mac** and **Linux**: 

```export DOCKER_CLI_EXPERIMENTAL=enabled```

Once docker buildx is enabled, you'll need to create a custom buildx builder:

- ```> docker buildx create --name [your-builder-name]```
- ```> docker buildx use [your-builder-name]```
- ```> docker buildx inspect --bootstrap```
- ```> docker buildx inspect [your-builder-name]```


> Linux users may need to run the following:

- ```> docker run --rm --privileged docker/binfmt:a7996909642ee92942dcd6cff44b9b95f08dad64```
---

You should then see that your builder is running and supports the necessary platforms:

```
Name:   optrabuilder
Driver: docker-container

Nodes:
Name:      optrabuilder0
Endpoint:  unix:///var/run/docker.sock
Status:    running
Platforms: linux/amd64, linux/386, linux/arm64, linux/ppc64le, linux/s390x, linux/arm/v7, linux/arm/v6, linux/riscv64
```

## Build and run
<hr>

Create an ```env.sh``` (Mac/Linux) or ```env.bat``` (Windows) in the project directory. The contents of this file will set environment variables necessary for the build. Here is an example:

```
#!/bin/bash

# In order to push your skill to your container registry,
# you'll need to define the following variables.

export registry=mycontainerregistry.example.io
export registry_username=myregistryusername
export registry_password=myregistrypassword

# Here you can name your skill and provide it with a tag.
export skill_name=java-sample-skill
export skill_tag=0.0.1
```

After you've verified your environment variables, you can run the build by running:

```>./docker-build.sh``` (Mac or Linux)

```> docker-build.bat``` (Windows)

The first time your run the build, gradle will be downloaded, along with all of the project dependencies.


