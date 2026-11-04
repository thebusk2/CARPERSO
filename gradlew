#!/usr/bin/env sh
#
# Gradle start up script for POSIX compatible shells.
#
APP_HOME="${APP_HOME:-$(cd "$(dirname "$0")" && pwd)}"
APP_NAME="Gradle"
APP_BASE_NAME="$(basename "$0")"

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVACMD="java"
else
    # Try Android Studio JBR
    AS_JAVA="/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/java"
    if [ -f "$AS_JAVA" ]; then
        JAVACMD="$AS_JAVA"
    else
        echo "ERROR: JAVA_HOME is not set and 'java' is not on the PATH."
        exit 1
    fi
fi

exec "$JAVACMD" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
