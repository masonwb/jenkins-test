def call() {
  def helpers = libraryResource('scripts/httpHelper.groovy')

  // return choice(name: "Environment", choices: ["Dev", "Test", "Prod"])
  return [$class: 'CascadeChoiceParameter',
    name: 'Environment',
    referencedParameters: 'Tenant',
    script: [
      $class: 'GroovyScript',
      script: [
        classpath: [],
        sandbox: false,
        script: helpers + '''
          def helloWorld = new HelloWorld()

          return [helloWorld.hello()]
        '''
      ],
    ]
  ]
}
