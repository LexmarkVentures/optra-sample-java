
# Shell script to build and deploy our skill to a container registry.

# Docker buildx requires this.
export DOCKER_CLI_EXPERIMENTAL=enabled

if [ ! -f "env.sh" ]; then
  echo "No env.sh file found. Please create one. See README.md for details."
  exit
fi

# Bring in registry information and skill name and tag.
source ./env.sh

if [ -z "$registry" ]
  then
    echo "You're missing the container registry URL. We cannot proceed without that."
    exit
fi

if [ -z "$registry_username" ]
  then
    echo "Missing container registry username."
    exit
fi

if [ -z "$registry_password" ]
  then
    echo "Missing container registry password."
    exit
fi

if [ -z "$skill_name" ]
  then
    echo "Missing skill name."
    exit
fi

if [ -z "$skill_tag" ]
  then
    echo "Missing skill tag."
    exit
fi

# Remove whatever's in the build directory.
rm -rf build

# Build the application as a single JAR (with all dependencies).
./gradlew shadowJar


# Docker grade-based approach:
# Jump around the fact that as a non sudo user I can't delete the build directory.
# docker run --network host --rm -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:6.7.1-jdk8 gradle shadowJar
# cp build/libs/*.jar .
# docker run --network host --rm -v "$PWD":/home/gradle/project -w /home/gradle/project gradle:6.7.1-jdk8 rm -rf build

# Log in to the registry, build the image, and push it to the repository.
docker login -u $registry_username -p $registry_password $registry

# Kick off buildx and push the results to the container registry.
# For multi-stage docker build: -f MultiStageDockerfile

docker buildx build --platform linux/amd64,linux/arm64 --no-cache -t $registry/$skill_name:$skill_tag --push .
