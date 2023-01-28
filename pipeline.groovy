pipeline {
    agent any
    stages {
        stage('print') {
            steps { 
                sh 'echo hello world'
            }
        }
    }
}