pipeline {
    agent any
    
    environment {
        PROD_SERVER = '192.168.17.153'                  // اسم VM الإنتاجي
        PROD_USER = 'sinda'                       // مستخدم SSH على VM
        SONARQUBE_URL = 'http://192.168.17.144:9000'  // رابط SonarQube من Jenkins container
        ZAP_URL = 'http://192.168.17.144:8090'        // رابط ZAP
    }
    
    tools {
        maven 'Maven'   // الاسم اللي ضبطته في Jenkins → Global Tool Configuration
        jdk 'JDK11'     // الاسم اللي ضبطته في Jenkins → Global Tool Configuration
    }
    
    stages {
        stage('Check Java & Maven') {
            steps {
                echo '===== Check Environment ====='
                sh 'echo JAVA_HOME=$JAVA_HOME'
                sh 'java -version'
                sh 'mvn -v'
            }
        }

        stage('Checkout') {
            steps {
                echo '===== Checkout Code ====='
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '===== Compile ====='
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo '===== Run Unit Tests ====='
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
                echo '===== SonarQube Analysis ====='
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
                echo '===== Create WAR ====='
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
                echo '===== Deploy to VM_Prod ====='
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
                echo '===== Verify Deployment ====='
                sh '''
                    sleep 10
                    curl -f http://${PROD_SERVER}:8081/snake-game/game || exit 1
                    echo "✅ Application deployed successfully!"
                '''
            }
        }
        
        stage('DAST - ZAP Scan') {
            steps {
                echo '===== OWASP ZAP Scan ====='
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
            echo '===== ✅ Pipeline Finished Successfully! ====='
            echo "🎮 Application URL: http://${PROD_SERVER}:8081/snake-game/game"
        }
        failure {
            echo '===== ❌ Pipeline Failed ====='
        }
    }
}
