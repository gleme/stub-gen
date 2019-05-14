package application.unifei.eco009.stubs;

public class CesarCipherImplementation {

    private CesarCipher cesarCipher;

    public CesarCipherImplementation(CesarCipher cesarCipher) {
		this.cesarCipher = cesarCipher;
	}
	public String cesarCipher() {
		// ADD YOUR CODE HERE:
        String plaintext = cesarCipher.getPlaintext();
        int key = cesarCipher.getKey();
        String encrypted = "";

        for (int index = 0; index < plaintext.length(); index++) {
            char c = (char)(plaintext.charAt(index) + key);
            if (c > 'z')
                encrypted += (char)(plaintext.charAt(index) - (26 - key));
            else
                encrypted += (char)(plaintext.charAt(index) + key);
        }

        return encrypted;
	}
}
