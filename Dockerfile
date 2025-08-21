# 1. Base image với JDK 21
FROM eclipse-temurin:21-jdk-alpine

# 2. Thư mục làm việc trong container
WORKDIR /app

# 3. Copy file pom.xml và build cache dependencies
COPY pom.xml .
RUN apk add --no-cache bash
RUN mkdir -p target
RUN echo "dependencies cached"

# 4. Copy toàn bộ source code
COPY src ./src

# 5. Build Spring Boot app
RUN ./mvnw clean package -DskipTests

# 6. Chỉ copy file jar ra để chạy
RUN cp target/*.jar app.jar

# 7. Port mà Spring Boot chạy
EXPOSE 8080

# 8. Command chạy app
ENTRYPOINT ["java","-jar","app.jar"]
