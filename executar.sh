#!/usr/bin/env bash
set -e

# Localizador de Java JRE
if command -v java >/dev/null 2>&1; then
    JAVA_CMD="java"
elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
elif [ -x "/home/pedroqueiroz/.var/app/com.visualstudio.code/data/vscode/extensions/redhat.java-1.56.0-linux-x64/jre/21.0.12.1-linux-x86_64/bin/java" ]; then
    JAVA_CMD="/home/pedroqueiroz/.var/app/com.visualstudio.code/data/vscode/extensions/redhat.java-1.56.0-linux-x64/jre/21.0.12.1-linux-x86_64/bin/java"
elif [ -x "/home/pedroqueiroz/.jdks/jbr-25.0.3/bin/java" ]; then
    JAVA_CMD="/home/pedroqueiroz/.jdks/jbr-25.0.3/bin/java"
else
    echo "Erro: java não encontrado no sistema."
    exit 1
fi

if [ ! -d "target/classes" ]; then
    echo "==> Binários não encontrados. Executando compilação..."
    ./compilar.sh
fi

echo "==> Iniciando o Sistema de Matrículas Universitárias..."
$JAVA_CMD -cp target/classes br.pucminas.matriculas.Main

