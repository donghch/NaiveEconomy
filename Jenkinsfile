pipeline {
    agent any
    stages {
        stage ("Build") {
            steps {
                sh "mvn -B -DskipTests clean assembly:single package"
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
