@echo off
chcp 65001 >nul
cd /d "%~dp0"

set CP=libs\gson\*;libs\okhttp\*

if not exist out\ch\tbz\bank\software\Main.class (
    echo [Kompiliere...]
    if not exist out mkdir out
    javac -encoding UTF-8 -d out -cp "%CP%" bank-software-mvn\src\main\java\ch\tbz\bank\software\*.java
    if errorlevel 1 (
        echo.
        echo Kompilieren fehlgeschlagen.
        pause
        exit /b 1
    )
)

echo.
java -Dfile.encoding=UTF-8 -Dstdin.encoding=UTF-8 -cp "out;%CP%" ch.tbz.bank.software.Main
echo.
pause
