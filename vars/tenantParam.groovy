def call(String secret) {
  return [$class: 'ChoiceParameter',
    name: 'Tenant',
    script: [
      $class: 'GroovyScript',
      script: [
        classpath: [],
        sandbox: false,
        script: '''
          import groovy.json.JsonSlurper

          def response = new URL("http://host.docker.internal:3000/api/v1/tenants?secret=''' + secret + '''").text
          def json = new JsonSlurper().parseText(response)

          return json.collect { it.name }
        '''
      ],
      fallbackScript: [
        classpath: [],
        sandbox: false,
        script: 'return ["ERROR - could not load tenants"]'
      ]
    ]
  ]
}
