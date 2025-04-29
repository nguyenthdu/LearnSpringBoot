### Khi đã config biến môi trường trong properties thì khi chạy docker có thể sử dụng -e để truyền biến môi trường vào container
`docker run  --name identity-service -p 8080:8080 -e DBMS_URL=jdbc:mysql://172.17.0.2:3306/identify_service identity-service:0.0.1`
### Tạo network cho các container có thể giao tiếp với nhau
`docker network create identity-network`
### gắn mysql vào network
`docker run --name mysql-identity --network identity-network -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=identify_service -p 3306:3306 -d mysql`
### gắn identity-service vào network
`docker run --name identity-service --network identity-network -p 8080:8080 -e DBMS_URL=jdbc:mysql://mysql-identity:3306/identify_service -d identity-service:0.0.1`
## Đẩy lên docker hub
### build docker image
`docker build -t <account>/identity-service:0.0.1 .`
### push docker image
`docker image push <account>/identity-service:0.0.1`
### network
`docker network create identity-network`
### mysql
`docker run --name mysql-identity --network identity-network -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=identify_service -p 3306:3306 -d mysql`
### identity-service
`docker run --name identity-service --network identity-network -p 8080:8080 -e DBMS_URL=jdbc:mysql://mysql-identity:3306/identify_service -d <account>/identity-service:0.0.1`
