FROM airhacks/glassfish
COPY ./target/jakarta-ee-jumpstart.war ${DEPLOYMENT_DIR}
