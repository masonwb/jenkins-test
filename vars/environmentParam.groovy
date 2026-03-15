def call(String secret) {
  return [$class: 'CascadeChoiceParameter',
    name: 'Environment',
    referencedParameters: 'Tenant',
    script: [
      $class: 'GroovyScript',
      script: [
        classpath: [],
        sandbox: false,
        script: '''
          if (!Tenant) return ["Select a tenant first"]

          return [Tenant + " Dev", Tenant + " Test", Tenant + " Prod"]
        '''
      ],
      fallbackScript: [
        classpath: [],
        sandbox: false,
        script: 'return ["ERROR - could not load environments"]'
      ]
    ]
  ]
}
