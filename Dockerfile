# Bước 1: Build ứng dụng bằng Maven
FROM maven:3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Tạo image runtime gọn nhẹ
# ✅ SỬA DÒNG NÀY: Dùng Eclipse Temurin JRE trên nền Alpine
FROM eclipse-temurin:17-jre-alpine

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file JAR từ bước build
COPY --from=build /app/target/Automated-Application-Mangament-1.jar app.jar

# Expose cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]