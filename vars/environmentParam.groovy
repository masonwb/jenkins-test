def call() {
  // return choice(name: "Environment", choices: ["Dev", "Test", "Prod"])
  return [$class: 'CascadeChoiceParameter',
    name: 'Environment',
    referencedParameters: 'Tenant',
    classpath: [
      [$class: 'LibraryPath', library: 'Katalog', value: 'src']
    ],
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
    ]
  ]
}
