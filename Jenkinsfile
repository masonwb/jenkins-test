@Library('Katalog') _

withCredentials([
    string(credentialsId: 'my-secret', variable: 'MY_SECRET'),
]) {
    properties([
        parameters([
            tenantParam(MY_SECRET)
        ])
    ])
}

pipeline {
    agent any

    environment {
        IMAGE_NAME = 'my-registry/myapp'
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('say-hi') {
            steps {
                echo "${params.Tenant}"
            }
        }
    }

    post {
        success {
            echo "Success!"
        }
        failure {
            echo "Failure!"
        }
    }
}
