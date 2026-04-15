pipeline {
    agent any

    environment {
        MAVEN_OPTS = "-Dmaven.test.failure.ignore=true"
    }

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/guru4ec-dev/portable-selenium-grid-framework.git'
            }
        }

        stage('Start Selenium Grid') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker-compose up --scale chrome=3 -d'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean test -Dexecution=remote'
            }
        }

        stage('Generate Report') {
            steps {
                sh 'mvn allure:report'
            }
        }

        stage('Archive Results') {
            steps {
                archiveArtifacts artifacts: '**/target/**', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            sh 'docker-compose down'
        }
        success {
            echo 'Tests Passed ✅'
        }
        failure {
            echo 'Tests Failed ❌'
        }
    }
}
