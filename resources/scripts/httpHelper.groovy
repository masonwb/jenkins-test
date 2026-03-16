import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials
import javax.net.ssl.HttpsURLConnection

class HttpHelper {
  StandardCertificateCredentials credentials

  HttpHelper(StandardCertificateCredentials credentials) {
    this.credentials = credentials
  }

  get(String url) {
    def ks = this.credentials.keyStore
    def password = this.credentials.password.plainText

    def kmf = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.defaultAlgorithm)
    kmf.init(ks, password.toCharArray())

    def trustAllCerts = [
      checkClientTrusted: { chain, authType -> },
      checkServerTrusted: { chain, authType -> },
      getAcceptedIssuers: { [] as java.security.cert.X509Certificate[] }
    ] as javax.net.ssl.X509TrustManager

    def sslContext = javax.net.ssl.SSLContext.getInstance('TLS')
    sslContext.init(kmf.keyManagers, [trustAllCerts] as javax.net.ssl.TrustManager[], null)

    def currentUrl = url
    def cookies = [:]

    for (int i = 0; i < 10; i++) {
      HttpsURLConnection conn = (HttpsURLConnection) new URL(currentUrl).openConnection()
      conn.SSLSocketFactory = sslContext.socketFactory
      conn.hostnameVerifier = { hostname, session -> true } as javax.net.ssl.HostnameVerifier
      conn.instanceFollowRedirects = false

      if (cookies) {
        conn.setRequestProperty('Cookie', cookies.collect { k, v -> "$k=$v" }.join('; '))
      }

      try {
        int status = conn.responseCode

        conn.headerFields.each { header, values ->
          if (header?.equalsIgnoreCase('Set-Cookie')) {
            values.each { raw ->
              def pair = raw.split(';')[0].trim()
              def eq = pair.indexOf('=')
              if (eq > 0) {
                cookies[pair[0..<eq]] = eq + 1 < pair.length() ? pair[(eq+1)..-1] : ''
              }
            }
          }
        }

        if (status in [301, 302, 303, 307, 308]) {
          def location = conn.getHeaderField('Location')
          currentUrl = location.startsWith('http') ? location : new URL(new URL(currentUrl), location).toString()
          continue
        }

        return (status < 400 ? conn.inputStream : conn.errorStream)?.text
      } finally {
        conn.disconnect()
      }
    }

    throw new Exception("Too many redirects for URL: ${url}")
  }
}
