@Library('Katalog') _

withCredentials([
    string(credentialsId: 'my-secret', variable: 'MY_SECRET'),
    certificate(credentialsId: 'dev-client', keystoreVariable: 'CERT_FILE', passwordVariable: 'CERT_PASSWORD')]
]) {
    properties([
        parameters([
            tenantParam(CERT_FILE, CERT_PASSWORD),
            projectParam(MY_SECRET),
            environmentParam()
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
                echo "Tenant: ${params.Tenant}, Project: ${params.Project}, Environment: ${params.Environment}"
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
