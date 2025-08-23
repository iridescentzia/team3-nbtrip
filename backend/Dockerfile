FROM tomcat:9.0-jre17-temurin

# 기본 앱 제거
RUN rm -rf /usr/local/tomcat/webapps/*

# Gradle 산출물 WAR → ROOT.war로 배치
COPY build/libs/*SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
ENV CATALINA_OPTS="-Xms256m -Xmx512m"