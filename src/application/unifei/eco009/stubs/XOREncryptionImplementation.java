package application.unifei.eco009.stubs;

public class XOREncryptionImplementation {

    private XOREncryption XOREncryption;

    public XOREncryptionImplementation(XOREncryption XOREncryption) {
		this.XOREncryption = XOREncryption;
	}

    public String XOREncryption() {
		// ADD YOUR CODE HERE:
        String plaintext = XOREncryption.getPlaintext();
        char[] key = XOREncryption.getEncryptionKey().toCharArray();
        StringBuilder encrypted = new StringBuilder();

        for (int index = 0; index < plaintext.length(); index++) {
            encrypted.append((char) plaintext.charAt(index) ^ key[index % key.length]);
        }

        return encrypted.toString();
	}
}
