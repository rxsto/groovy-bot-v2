FROM openjdk:8

WORKDIR /opt/groovy
ENTRYPOINT  ["./wait-for-lavalink.sh", "java", "-jar", "/opt/groovy/groovybot.jar"]
