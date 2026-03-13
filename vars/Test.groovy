/**
 * notifySlack.groovy
 * Send a Slack message: notifySlack(status: 'SUCCESS', channel: '#builds')
 */
def call(Map config = [:]) {
    String status  = config.get('status', 'INFO')
    String channel = config.get('channel', '#ci-notifications')
    String message = config.get('message', "Build ${env.JOB_NAME} #${env.BUILD_NUMBER}: ${status}")

    String color = [SUCCESS: 'good', FAILURE: 'danger', UNSTABLE: 'warning'].get(status, '#439FE0')

    // slackSend(channel: channel, color: color, message: message)
    echo channel color message
}
