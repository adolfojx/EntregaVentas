@echo off
REM Compila y ejecuta el proyecto en Windows (doble clic sobre este archivo).
cd /d "%~dp0"
if not exist bin mkdir bin
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d bin @fuentes.txt
if errorlevel 1 (
  echo ERROR: no se pudo compilar. Verifique que Java JDK este instalado.
  del fuentes.txt
  pause
  exit /b 1
)
del fuentes.txt
echo ===== PASO 1: GenerateInfoFiles =====
java -cp bin co.edu.poli.proyecto.GenerateInfoFiles
echo.
echo ===== PASO 2: main =====
java -cp bin co.edu.poli.proyecto.main
echo.
pause
