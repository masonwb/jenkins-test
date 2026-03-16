def call(String folder, String credentialId) {
  def credentialHelper = libraryResource('scripts/credentialHelper.groovy')
  def httpHelper = libraryResource('scripts/httpHelper.groovy')

  return [$class: 'ChoiceParameter',
    name: 'Tenant',
    script: [
      $class: 'GroovyScript',
      script: [
        classpath: [],
        sandbox: false,
        script: httpHelper + credentialHelper + '''
          import groovy.json.JsonSlurper
          import javax.net.ssl.HttpsURLConnection

          def credentialHelper = new CredentialHelper("''' + folder + '''", "''' + credentialId + '''")
          def creds = credentialHelper.getCredential()

          def httpClient = new HttpHelper(creds)
          def response = httpClient.get("https://localhost:8443/api/v1/tenants")

          return new JsonSlurper().parseText(responseBody).collect { it.name }
        '''
      ]
      // fallbackScript: [
      //   classpath: [],
      //   sandbox: false,
      //   script: 'return ["ERROR - could not load tenants"]'
      // ]
    ]
  ]
}
