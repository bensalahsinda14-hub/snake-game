# Snake Game - Projet DevOps

## Description
Application web Snake Game avec pipeline CI/CD complet.

## Technologies
- Java 11
- Maven
- Jenkins
- SonarQube
- Docker
- OWASP ZAP
- Prometheus + Grafana

## URLs
- Application: http://192.168.17.153:8081/snake-game/game
- Jenkins: http://192.168.17.144:8080
- SonarQube: http://192.168.17.144:9000

## Build
```bash
mvn clean package
docker build -t snake-game .
```
