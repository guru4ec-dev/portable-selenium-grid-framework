pipeline {
    agent any

    stages {

        stage('Parallel Tests') {
            parallel {

                stage('Chrome') {
                    steps {
                        bat 'mvn clean test -Dbrowser=chrome'
                    }
                }

                stage('Firefox') {
                    steps {
                        bat 'mvn clean test -Dbrowser=firefox'
                    }
                }

            }
        }

    }
}
