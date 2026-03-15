def call(String secret) {
  return [$class: 'CascadeChoiceParameter',
    name: 'Project',
    referencedParameters: 'Tenant',
    script: [
      $class: 'GroovyScript',
      script: [
        classpath: [],
        sandbox: false,
        script: '''
          import groovy.json.JsonSlurper

          if (!Tenant) return ["Select a tenant first"]

          def response = new URL("http://host.docker.internal:3000/api/v1/tenants/" + Tenant + "/projects").text
          def json = new JsonSlurper().parseText(response)

          return json.collect { it.name }
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
