FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Maven bağımlılıklarını kopyala
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Uygulamayı derle
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Çalışma zamanı görüntüsü oluştur
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Uygulamayı çalıştır
ENTRYPOINT ["java","-cp","app:app/lib/*","com.denizcan.stocktrackingsystem.StockTrackingSystemApplication"] 