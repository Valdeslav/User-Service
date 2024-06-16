call gradlew bootJar
cd docker
docker compose build
docker compose up -d
cd ..