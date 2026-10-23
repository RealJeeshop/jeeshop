FROM eclipse-temurin:25-jre-noble

ENV LANG=C.UTF-8 \
    PORT=8080 \
    DATA_DIR=/jeeshop

RUN install -d -o 1001 -g 0 -m 0770 /deployments /jeeshop

COPY --chown=1001:0 admin/target/quarkus-app/lib/ /deployments/lib/
COPY --chown=1001:0 admin/target/quarkus-app/*.jar /deployments/
COPY --chown=1001:0 admin/target/quarkus-app/app/ /deployments/app/
COPY --chown=1001:0 admin/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE ${PORT} 5005
USER 1001:0

ENTRYPOINT ["sh", "-c", "if [ \"${JAVA_ENABLE_DEBUG:-false}\" = \"true\" ]; then export JAVA_TOOL_OPTIONS=\"${JAVA_TOOL_OPTIONS:+$JAVA_TOOL_OPTIONS }-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005\"; fi; exec java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Dquarkus.http.port=${PORT} -jar /deployments/quarkus-run.jar"]
