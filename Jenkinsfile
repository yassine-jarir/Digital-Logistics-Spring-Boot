pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    environment {
        SONAR_TOKEN = credentials('sonarqube-token')
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                sh 'chmod +x mvnw'
                sh './mvnw clean compile -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Running unit tests with test profile...'
                sh './mvnw test -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('JaCoCo Coverage Report') {
            steps {
                echo 'Generating JaCoCo coverage report...'
                sh './mvnw jacoco:report'
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/dto/**,**/mapper/**,**/config/**,**/entity/**,**/enums/**,**/*Application.class'
                    )
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "Running SonarQube analysis..."
                script {
                    withSonarQubeEnv('SonarQube-Server') {
                        sh "./mvnw sonar:sonar \
                           -Dsonar.projectKey=digitale-logistic \
                           -Dsonar.host.url=http://host.docker.internal:9000 \
                           -Dsonar.login=$SONAR_TOKEN"
                    }
                }
            }
            post {
                failure {
                    echo "⚠️ SonarQube analysis failed: script returned exit code 1"
                    echo "Continuing pipeline without SonarQube analysis..."
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo "Waiting for Quality Gate result..."
                script {
                    timeout(time: 3, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the application...'
                sh './mvnw package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

//         stage('Docker Build') {
//             when {
//     n            expression { fileExists('Dockerfile') }
//             }
//             steps {
//                 echo 'Building Docker image...'
//                 script {
//                     def appVersion = sh(
//                         script: './mvnw help:evaluate -Dexpression=project.version -q -DforceStdout',
//                         returnStdout: true
//                     ).trim()
//
//                     sh "docker build -t digitale-logistic:${appVersion} ."
//                     sh "docker tag digitale-logistic:${appVersion} digitale-logistic:latest"
//                 }
//             }
//         }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            cleanWs()
        }
    }
}
