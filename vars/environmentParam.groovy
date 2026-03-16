def call() {
  // return choice(name: "Environment", choices: ["Dev", "Test", "Prod"])
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
          import io.strychnine.HelloWorld

          def helloWorld = new HelloWorld()

          return [helloWorld.hello()]
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
