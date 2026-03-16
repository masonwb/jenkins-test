def call(String secret) {
  def JenkinsCertificateCredential = libraryResource('helpers/JenkinsCertificateCredential.groovy')
  def HttpClient = libraryResource('helpers/HttpClient.groovy')

  return [$class: 'CascadeChoiceParameter',
    name: 'Project',
    referencedParameters: 'Tenant',
    script: [
      $class: 'GroovyScript',
      script: [
        classpath: [],
        sandbox: false,
        script: HttpClient + JenkinsCertificateCredential + '''
          import groovy.json.JsonSlurper

          if (!Tenant) return ["Select a tenant first"]

          def credentials = new JenkinsCertificateCredential("''' + folder + '''", "''' + credentialId + '''").getCredentials()
          def httpClient = new HttpClient(credentials)
          def response = httpClient.get("http://host.docker.internal:3000/api/v1/tenants/${Tenant}/projects")

          return new JsonSlurper().parseText(response).collect { it.name }
        '''
      ],
      fallbackScript: [
        classpath: [],
        sandbox: false,
        script: 'return ["ERROR - could not load projects"]'
      ]
    ]
  ]
}
