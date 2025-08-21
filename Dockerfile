# 1. Base image với JDK 21
FROM eclipse-temurin:21-jdk-alpine

# 2. Thư mục làm việc trong container
WORKDIR /app

# 3. Cài bash, copy Maven Wrapper
RUN apk add --no-cache bash
COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

# 4. Cho mvnw có quyền chạy
RUN chmod +x ./mvnw

# 5. Copy source code
COPY src ./src

# 6. Build Spring Boot app
RUN ./mvnw clean package -DskipTests

# 7. Copy file jar (tự tìm đúng file)
RUN cp target/*.jar app.jar

# 8. Port mà Spring Boot chạy
EXPOSE 8080

# 9. Command chạy app
ENTRYPOINT ["java","-jar","app.jar"]
