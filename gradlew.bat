@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem Resolve JAVA_HOME
set JAVA_EXE=java.exe
if defined JAVA_HOME set JAVA_EXE=%JAVA_HOME%\bin\java.exe

if not exist "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" (
    echo ERROR: gradle-wrapper.jar not found. Open this project in Android Studio once,
    echo it will provision the Gradle wrapper, or run "gradle wrapper --gradle-version 8.9".
    exit /b 1
)

set CLASSPATH=%DIRNAME%gradle\wrapper\gradle-wrapper.jar
"%JAVA_EXE%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
