@Library('Katalog') _

def folder = "Strychnine"
def credentialId = "dev-client"

properties([
    parameters([
        tenantParam(folder, credentialId),
        projectParam(folder, credentialId),
        environmentParam()
    ])
])

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
