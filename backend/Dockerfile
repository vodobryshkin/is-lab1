FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY config ./config

RUN chmod +x gradlew

COPY src ./src

RUN ./gradlew clean war preparePostgresDriver --no-daemon

FROM quay.io/wildfly/wildfly:38.0.0.Final-jdk21

COPY --from=builder \
    --chown=jboss:root \
    /app/build/libs/backend-1.0-SNAPSHOT.war \
    /opt/jboss/wildfly/standalone/deployments/backend.war

COPY --from=builder /app/build/server/postgresql.jar /tmp/postgresql.jar
COPY server/configure-wildfly.cli /tmp/configure-wildfly.cli

RUN /opt/jboss/wildfly/bin/jboss-cli.sh \
        --file=/tmp/configure-wildfly.cli \
        -Dpostgres.driver.path=/tmp/postgresql.jar \
    && rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]