def call(String folder, String credentialId) {
  return [$class: 'ChoiceParameter',
    name: 'Tenant',
    script: [
      $class: 'GroovyScript',
      script: [
        classpath: [],
        sandbox: false,
        script: '''
          import groovy.json.JsonSlurper
          import com.cloudbees.plugins.credentials.CredentialsProvider
          import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials
          import jenkins.model.Jenkins
          import org.acegisecurity.context.SecurityContextHolder
          import javax.net.ssl.HttpsURLConnection

          def folder = Jenkins.instance.getItemByFullName("''' + folder + '''")

          def creds = CredentialsProvider.lookupCredentials(
            StandardCertificateCredentials,
            folder,
            SecurityContextHolder.context.authentication,
            null
          ).find { it.id == "''' + credentialId + '''" }

          return creds.collect { it.id }

          def ks = creds.keyStore  // already a KeyStore, no need to load from bytes
          def password = creds.password.plainText

          def kmf = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.defaultAlgorithm)
          kmf.init(ks, password.toCharArray())

          def sslContext = javax.net.ssl.SSLContext.getInstance('TLS')
          sslContext.init(kmf.keyManagers, null, null)

          def currentUrl = "https://host.docker.internal:8443/api/v1/tenants"
          def cookies = [:]
          def maxRedirects = 10
          def result = null

          maxRedirects.times {
            if (result != null) return

            HttpsURLConnection conn = (HttpsURLConnection) new URL(currentUrl).openConnection()
            conn.SSLSocketFactory = sslContext.socketFactory
            conn.instanceFollowRedirects = false

            if (cookies) conn.setRequestProperty('Cookie', cookies.collect { k, v -> "$k=$v" }.join('; '))

            int status = conn.responseCode

            conn.headerFields.each { header, values ->
              if (header?.equalsIgnoreCase('Set-Cookie')) {
                values.each { raw ->
                  def pair = raw.split(';')[0].trim()
                  def eq = pair.indexOf('=')
                  if (eq > 0) cookies[pair[0..<eq]] = pair[(eq+1)..-1]
                }
              }
            }

            if (status in [301, 302, 303, 307, 308]) {
              def location = conn.getHeaderField('Location')
              currentUrl = location.startsWith('http') ? location : new URL(new URL(currentUrl), location).toString()
              return
            }

            def responseBody = (status < 400 ? conn.inputStream : conn.errorStream)?.text
            result = new JsonSlurper().parseText(responseBody).collect { it.name }
          }

          return result
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
