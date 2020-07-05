# Build
mvn clean package && docker build -t org.atwjsw/jakarta-ee-jumpstart .

# RUN

docker rm -f jakarta-ee-jumpstart || true && docker run -d -p 8080:8080 -p 4848:4848 --name jakarta-ee-jumpstart org.atwjsw/jakarta-ee-jumpstart 