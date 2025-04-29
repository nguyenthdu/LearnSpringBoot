#Stage 1: build
FROM maven:3.9.8-amazoncorretto-21 AS build
#copy source
WORKDIR /app
COPY pom.xml .
COPY src ./src

#Build
RUN mvn package -DskipTests

# cần stage 2 vì stage là maven sẽ có nhiều file không cần thiết ảnh hưởng đến kích thước image
#Stage 2: run
FROM amazoncorretto:21

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

#run
ENTRYPOINT ["java", "-jar", "app.jar"]

