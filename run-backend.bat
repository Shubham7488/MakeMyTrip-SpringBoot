@echo off
REM Set Java 21 as the JDK to use
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo ============================================
echo Starting MakeMyTrip Backend (Spring Boot)
echo ============================================
echo.
echo Java Version:
java -version
echo.

REM Build the project
echo Building project...
call ./mvnw clean install

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo BUILD FAILED!
    pause
    exit /b 1
)

echo.
echo Build successful! Now running the application...
echo.

REM Run the application
call ./mvnw spring-boot:run

pause
