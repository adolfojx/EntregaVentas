#!/bin/bash
# Compila y ejecuta el proyecto en Mac o Linux:  bash ejecutar.sh
cd "$(dirname "$0")" || exit 1
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name '*.java') || { echo "ERROR: no se pudo compilar. Verifique que Java JDK este instalado."; exit 1; }
echo "===== PASO 1: GenerateInfoFiles ====="
java -cp bin co.edu.poli.proyecto.GenerateInfoFiles
echo
echo "===== PASO 2: main ====="
java -cp bin co.edu.poli.proyecto.main
