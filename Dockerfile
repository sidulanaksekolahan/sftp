# Menggunakan base image resmi Java 11 JRE dari Eclipse Temurin
FROM eclipse-temurin:11-jre

RUN apt-get update && \
    apt-get install -y sshpass && \
    rm -rf /var/lib/apt/lists/*

# Menentukan direktori kerja di dalam container
WORKDIR /app

# Menyalin file .jar hasil build Anda ke dalam container
COPY target/sftp-0.0.1-SNAPSHOT.jar app.jar

# Perintah untuk menjalankan aplikasi Java Anda
ENTRYPOINT ["java", "-jar", "app.jar"]