FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/*.jar app.jar

# JVM 옵션을 환경변수로 설정
ENV JVM_OPTS="-Xms128m -Xmx300m \
              -XX:+UseG1GC \
              -XX:MaxGCPauseMillis=100 \
              -XX:ActiveProcessorCount=1 \
              -XX:ParallelGCThreads=1 \
              -XX:G1ConcRefinementThreads=1 \
              -Dserver.tomcat.threads.max=20"

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT sh -c "java $JVM_OPTS -jar app.jar"