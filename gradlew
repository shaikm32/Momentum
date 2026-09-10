#!/usr/bin/env sh
# Gradle startup script for POSIX sh.
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
if [ ! -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
  echo "ERROR: gradle-wrapper.jar not found. Open this project in Android Studio once to provision it,"
  echo "or run: gradle wrapper --gradle-version 8.9"
  exit 1
fi
JAVA_EXE="java"
if [ -n "$JAVA_HOME" ]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
fi
exec "$JAVA_EXE" -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
