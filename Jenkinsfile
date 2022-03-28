pipeline {
    agent none

    triggers { pollSCM('H/2 * * * *') }

    options {
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10'))
    }

    environment {
        REPO = ''
        BRANCH = ''
        APP_NAME = ''
        APP_PORT = ''
        SANDBOX_PORT = ''
        STAGING_PORT = ''
        PRODUCTION_PORT = ''
        CHILD_JOB_NAME = ''
    }

    stages {
        stage("Set parameters") {
            agent {
                label 'JENKINS'
            }

            steps {
                script {
                    pom = readMavenPom file: 'pom.xml'

                    REPO = "$GIT_URL"
                    BRANCH = env.GIT_BRANCH.replace("origin/", "")
                    APP_NAME = pom.getProperties().getProperty('docker.image.name')
                    APP_PORT = pom.getProperties().getProperty('exposed.app.port')
                    SANDBOX_PORT = pom.getProperties().getProperty('published.container.port.sandbox')
                    TEST_PORT = pom.getProperties().getProperty('published.container.port.test')
                    PRODUCTION_PORT = pom.getProperties().getProperty('published.container.port.production')
                    CHILD_JOB_NAME = "$JOB_NAME"
                }
            }
            post {
                cleanup {
                    cleanWs(deleteDirs: true, disableDeferredWipeout: true)
                }
            }
        }
        stage("Build") {
            agent none

            steps {
                build job: 'Generic-Pipeline', parameters: [
                    string(name: 'REPO', value: "$REPO"),
                    string(name: 'BRANCH', value: "$BRANCH"),
                    string(name: 'APP_NAME', value: "$APP_NAME"),
                    string(name: 'APP_PORT', value: "$APP_PORT"),
                    string(name: 'SANDBOX_PORT', value: "$SANDBOX_PORT"),
                    string(name: 'TEST_PORT', value: "$TEST_PORT"),
                    string(name: 'PRODUCTION_PORT', value: "$PRODUCTION_PORT"),
                    string(name: 'CHILD_JOB_NAME', value: "$CHILD_JOB_NAME")]
            }
        }
    }
}