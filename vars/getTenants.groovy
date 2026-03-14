/**
 * notifySlack.groovy
 * Send a Slack message: notifySlack(status: 'SUCCESS', channel: '#builds')
 */
def call() {
  return '''
    import groovy.json.JsonSlurper

    def response = new URL("http://host.docker.internal:3000/api/v1/tenants").text
    def json = new JsonSlurper().parseText(response)

    return json.collect { it.name }
  '''
}
