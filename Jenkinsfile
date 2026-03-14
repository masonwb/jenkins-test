// example-Jenkinsfile
// Drop this in any project repo to use the shared library.

@Library('test-library') _  // the underscore imports all vars/ steps

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

    parameters([
        [$class: 'ChoiceParameter',
            name: 'Tenant',
            script: [
                $class: 'GroovyScript',
                script: [
                    classpath: [],
                    sandbox: true,
                    script: '''
                        @Library('test-library') _
                        return getTenants()
                    '''
                ]
            ]
        ]
    ])

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
