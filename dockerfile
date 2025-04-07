# 构建阶段：使用Maven镜像编译Java项目
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# 运行阶段：使用更小的JRE镜像
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# # 安全设置：创建非root用户
# RUN addgroup -S spring && adduser -S spring -G spring \
#     && chown -R spring:spring /app
# USER spring

# 从构建阶段复制打包好的jar文件
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# # 健康检查（每30秒检查一次服务是否存活）
# HEALTHCHECK --interval=30s --timeout=3s \
#     CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 设置JVM内存参数
ENV JAVA_OPTS="-Xmx512m -Xms256m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]