import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials
import hudson.security.ACL
import jenkins.model.Jenkins

class CredentialHelper {
  String folderName
  String credentialId

  CredentialHelper(String folderName, String credentialId) {
    this.folderName = folderName
    this.credentialId = credentialId
  }

  StandardCertificateCredentials getCredential() {
    def jenkins = Jenkins.instanceOrNull
    if (jenkins == null) throw new IllegalStateException("Jenkins instance unavailable")

    def folder = jenkins.getItemByFullName(this.folderName)
    if (folder == null) throw new IllegalArgumentException("Folder '${this.folderName}' not found")

    return CredentialsProvider.lookupCredentials(
      StandardCertificateCredentials,
      folder,
      ACL.SYSTEM
    ).find { it.id == this.credentialId }
  }
}
