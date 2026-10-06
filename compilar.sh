#!/usr/bin/env bash
set -e

# Localizador de JDK/Javac
if command -v javac >/dev/null 2>&1; then
    JAVAC_CMD="javac"
elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC_CMD="$JAVA_HOME/bin/javac"
elif [ -x "/home/pedroqueiroz/.var/app/com.visualstudio.code/data/vscode/extensions/redhat.java-1.56.0-linux-x64/jre/21.0.12.1-linux-x86_64/bin/javac" ]; then
    JAVAC_CMD="/home/pedroqueiroz/.var/app/com.visualstudio.code/data/vscode/extensions/redhat.java-1.56.0-linux-x64/jre/21.0.12.1-linux-x86_64/bin/javac"
elif [ -x "/home/pedroqueiroz/.jdks/jbr-25.0.3/bin/javac" ]; then
    JAVAC_CMD="/home/pedroqueiroz/.jdks/jbr-25.0.3/bin/javac"
else
    echo "Erro: javac não encontrado no sistema."
    exit 1
fi

echo "==> Compilando projeto com: $JAVAC_CMD"
mkdir -p target/classes

SOURCES=$(find src/main/java src/test/java -name "*.java")
$JAVAC_CMD -d target/classes $SOURCES

echo "==> Compilação concluída com sucesso em target/classes!"

