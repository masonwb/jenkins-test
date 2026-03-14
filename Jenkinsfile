@Library('test-library') _

def getTenantList() {
  return '''
    return ["a", "b", "c"]
  '''
}

properties([
    parameters([
        [$class: 'ChoiceParameter',
            name: 'Tenant',
            script: [
                $class: 'GroovyScript',
                script: [
                    classpath: [],
                    sandbox: false,
                    script: getTenantList()
                ]
            ]
        ]
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
                echo 'hi'
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
