@echo off
cd /d "%~dp0"
gradlew fatJar
if exist "build\libs\ASSIGNMENT6vs-0.0-DEV-fat.jar" (
    java -jar "build\libs\ASSIGNMENT6vs-0.0-DEV-fat.jar"
) else (
    echo Error: Fat jar not found. Make sure gradlew fatJar was successful.
)
pause
