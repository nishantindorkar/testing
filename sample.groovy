pipeline {
    agent any
    stages {
        stage('git-pull') {
            steps { 
                echo 'git pull successful'
                sh 'env'
            }
        }
        stage('build') {
            steps { 
                echo 'build successful'
                sh 'ls'
            }
        }
        stage('testing') {
            steps { 
                echo 'testing successful'
                sh 'pwd'
            }
        }
        stage('deploying') {
            steps { 
                echo 'deploy completed'
            }
        }
    }
}
