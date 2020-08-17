
@echo off

echo Creating sample skill...

IF NOT EXIST "env.bat" (
  echo No env.bat file found. Please create one. See README.md for details.
  EXIT /B
)

echo Enabling docker buildx.
set DOCKER_CLI_EXPERIMENTAL=enabled

echo Setting necessary environment variables.
call env.bat

echo Compiling your skill.
call gradlew.bat shadowJar

echo Logging into to the docker registry.
docker login -u %registry_username% -p %registry_password% %registry%

echo Calling docker buildx.
docker buildx build --platform linux/amd64,linux/arm64 --no-cache -t %registry%/%skill_name%:%skill_tag% --push .