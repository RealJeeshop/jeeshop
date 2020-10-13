FROM jboss/wildfly:20.0.1.Final

RUN /opt/jboss/wildfly/bin/add-user.sh admin jeeshop --silent
ADD admin/target/jeeshop-admin.war /opt/jboss/wildfly/standalone/deployments/
ADD install/docker/wildfly/modules /opt/jboss/wildfly/modules/
ADD https://jdbc.postgresql.org/download/postgresql-42.2.16.jar /opt/jboss/wildfly/modules/org/postgresql/main/
ADD https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.21/mysql-connector-java-8.0.21.jar /opt/jboss/wildfly/modules/com/mysql/main/
ADD install/docker/wildfly/standalone/standalone.xml /opt/jboss/wildfly/standalone/configuration
ADD install/docker/wildfly/standalone/server.keystore /opt/jboss/wildfly/standalone/configuration
USER root
RUN chown jboss:jboss /opt/jboss/wildfly/modules/org/postgresql/main/*
RUN chown jboss:jboss /opt/jboss/wildfly/modules/com/mysql/main/*
USER jboss
RUN /opt/jboss/wildfly/bin/add-user.sh -u admin -p jeeshop123 --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
