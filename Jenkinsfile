pipeline {
    agent any
    
    environment {
        PROD_SERVER = '192.168.17.153'       // IP السيرفر الإنتاجي
        PROD_USER = 'sinda'                  // اسم المستخدم SSH
        SONARQUBE_URL = 'http://192.168.17.144:9000'  // IP سيرفر SonarQube
        ZAP_URL = 'http://192.168.17.153:8090'        // IP ZAP
    }
    
    tools {
        maven 'Maven'
        jdk 'JDK11'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '===== Récupération du code ====='
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '===== Compilation ====='
                withEnv(["JAVA_HOME=${tool name: 'JDK11', type: 'jdk'}", "PATH=${tool name: 'JDK11', type: 'jdk'}/bin:${env.PATH}"]) {
                    sh 'mvn clean compile'
                }
            }
        }
        
        stage('Test') {
            steps {
                echo '===== Tests unitaires ====='
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('SAST - SonarQube') {
            steps {
                echo '===== Analyse SonarQube ====='
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=snake-game -Dsonar.host.url=${SONARQUBE_URL}'
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                echo '===== Quality Gate ====='
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '===== Création du WAR ====='
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '===== Build Docker Image ====='
                sh 'docker build -t snake-game:${BUILD_NUMBER} .'
                sh 'docker tag snake-game:${BUILD_NUMBER} snake-game:latest'
            }
        }
        
        stage('Deploy to VM_Prod') {
            steps {
                echo '===== Déploiement sur VM_Prod ====='
                sh '''
                    docker save snake-game:${BUILD_NUMBER} | gzip > snake-${BUILD_NUMBER}.tar.gz
                    scp -o StrictHostKeyChecking=no snake-${BUILD_NUMBER}.tar.gz ${PROD_USER}@${PROD_SERVER}:/tmp/
                    ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_SERVER} "
                        docker load < /tmp/snake-${BUILD_NUMBER}.tar.gz
                        docker stop snake-game 2>/dev/null || true
                        docker rm snake-game 2>/dev/null || true
                        docker run -d --name snake-game -p 8081:8080 --restart=always snake-game:${BUILD_NUMBER}
                        rm /tmp/snake-${BUILD_NUMBER}.tar.gz
                        sleep 5
                        docker ps | grep snake-game
                    "
                    rm snake-${BUILD_NUMBER}.tar.gz
                '''
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo '===== Vérification du déploiement ====='
                sh '''
                    sleep 10
                    curl -f http://${PROD_SERVER}:8081/snake-game/game || exit 1
                    echo "✅ Application déployée avec succès !"
                '''
            }
        }
        
        stage('DAST - ZAP Scan') {
            steps {
                echo '===== Scan de sécurité OWASP ZAP ====='
                sh '''
                    curl "${ZAP_URL}/JSON/spider/action/scan/?url=http://${PROD_SERVER}:8081/snake-game/game" || true
                    sleep 20
                    curl "${ZAP_URL}/JSON/ascan/action/scan/?url=http://${PROD_SERVER}:8081/snake-game/game" || true
                    sleep 30
                    curl "${ZAP_URL}/JSON/core/view/alerts" > zap-alerts.json || true
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'zap-alerts.json', allowEmptyArchive: true
                }
            }
        }
    }
    
    post {
        success {
            echo '===== ✅ Pipeline terminé avec succès ! ====='
            echo "🎮 Application : http://${PROD_SERVER}:8081/snake-game/game"
        }
        failure {
            echo '===== ❌ Pipeline échoué ====='
        }
    }
}
