FROM node:24.1.0 as frontend

WORKDIR /app
COPY frontend /app/frontend

WORKDIR /app/frontend/mangabaka

RUN npm install
RUN npm run build

FROM gradle:8.13-jdk17 AS backend

WORKDIR /app

COPY backend /app/backend
COPY --from=frontend /app/frontend/mangabaka/dist/ /app/backend/src/main/webapp

ARG POSTGRES_PASSWORD
ARG POSTGRES_USER
ARG POSTGRES_JDBC_URL

RUN echo "PG_PASSWORD=${POSTGRES_PASSWORD}" > /app/backend/.env && \
    echo "PG_USERNAME=${POSTGRES_USER}" >> /app/backend/.env && \
    echo "PG_JDBC_URL=${POSTGRES_JDBC_URL}" >> /app/backend/.env

WORKDIR /app/backend

RUN gradle clean build

FROM jetty:12.0.22-jdk17

COPY --from=backend /app/backend/.env /var/lib/jetty/.env
COPY --from=backend /app/backend/build/libs/backend.war /var/lib/jetty/webapps/ROOT.war

EXPOSE 9090

ENTRYPOINT ["java"]
CMD [ "-jar", "/usr/local/jetty/start.jar", "--module=ee10-deploy,ee10-webapp,http,ee10-annotations,ee10-servlet", "-Djetty.http.port=9090", "-Djetty.deploy.scanInterval=10"]
