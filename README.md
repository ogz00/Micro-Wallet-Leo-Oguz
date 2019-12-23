# Micro-Wallet-Leo-Oguz
Docker Mysql SpringBoot Microservice Demo

### run with
```bash
git clone https://github.com/ogz00/Micro-Wallet-Leo-Oguz.git
docker-compose up
```

## Swagger Path
http://localhost:8080/api/v1/swagger-ui.html

## API Path
http://localhost:8080/api/v1/

## MYSQL Location
*jdbc:mysql://db:3306/microwallet_db*

For initialize test database from RESTApi with test curl requests 
```bash
chmod +x curl.sh
./curl.sh
```

### Manuel Run Main Application
```bash
cd mocrowallet/
mvn spring-boot:run
```

### Run Test Sources

Since test cases are includes DataJpaTest feature,please enable **h2db** configuration from *application.properties* file priorly

#### Docker installation
https://docs.docker.com/install/linux/docker-ce/centos/

#### Docker Compose Installations
https://docs.docker.com/compose/install/
