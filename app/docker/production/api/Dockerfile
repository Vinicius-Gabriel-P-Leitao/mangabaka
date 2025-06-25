FROM gradle:8.13-jdk17 AS backend

WORKDIR /app

COPY backend /app/backend
WORKDIR /app/backend

RUN gradle clean build

FROM jetty:12.0.22-jdk17

COPY --from=backend /app/backend/build/libs/backend.war /var/lib/jetty/webapps/ROOT.war

EXPOSE 9090

ENTRYPOINT ["java"]
CMD [ "-jar", "/usr/local/jetty/start.jar", "--module=ee10-deploy,ee10-webapp,http,ee10-annotations,ee10-servlet", "-Djetty.http.port=9090", "-Djetty.deploy.scanInterval=10", "-Dlog4j.debug=true"]
