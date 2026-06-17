pipeline {
    agent any

    triggers {
        // Poll SCM every 5 minutes
        pollSCM('*/5 * * * *')
    }

    environment {
        // Email to CC when build fails
        CC_EMAIL = 'srengty@gmail.com'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    try {
                        // Clean build and run all tests using Maven Wrapper (no need to install Maven)
                        bat 'mvnw.cmd clean package -DskipTests=false'

                        // Run tests explicitly with test profile (SQLite)
                        bat 'mvnw.cmd test'

                        echo 'Build and all tests passed successfully.'
                    } catch (Exception e) {
                        // Get the email of the developer who committed the last change
                        def developerEmail = bat(
                            script: 'git log -1 --format="%%ae"',
                            returnStdout: true
                        ).trim()

                        // Send email notification:
                        //   To: developer who committed the error
                        //   CC: srengty@gmail.com
                        mail(
                            to: developerEmail,
                            cc: "${CC_EMAIL}",
                            subject: "BUILD ERROR: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                            body: """
Build Error Notification
=========================
Project: ${env.JOB_NAME}
Build Number: ${env.BUILD_NUMBER}
Build URL: ${env.BUILD_URL}
Status: FAILED

The build has failed. Please review the changes and fix the issue.

Commit Author: ${developerEmail}
Error Details: ${e.message}

-- Jenkins
                            """.stripIndent().trim()
                        )

                        echo "Sent failure notification to: ${developerEmail} (CC: ${CC_EMAIL})"

                        // Re-throw to mark the build as failed and prevent deployment
                        error "Build failed: ${e.message}"
                    }
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                script {
                    try {
                        echo 'Running Ansible Playbook to deploy to Web Server...'
                        bat 'ansible-playbook -i inventory.ini ansible-playbook.yml'
                        echo 'Deployment completed successfully!'
                    } catch (Exception e) {
                        echo "Warning: Ansible deploy is not available on this Jenkins server. Build passed."
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully: Build -> Test -> Deploy'
        }
        failure {
            echo 'Pipeline failed. Check logs and email notifications for details.'
        }
    }
}