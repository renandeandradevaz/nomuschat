package nomuschat.seguranca;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptografia {

	private static final String hexDigits = "0123456789abcdef";

	public static byte[] digest(byte[] entrada, String metodoCriptografia) throws NoSuchAlgorithmException {

		MessageDigest messageDigest = MessageDigest.getInstance(metodoCriptografia);
		messageDigest.reset();

		return messageDigest.digest(entrada);
	}

	public static String byteArrayToHexString(byte[] arrayBytes) {

		StringBuffer bufffer = new StringBuffer();

		for (int counter = 0; counter < arrayBytes.length; counter++) {

			int j = (arrayBytes[counter]) & 0xFF;
			bufffer.append(hexDigits.charAt(j / 16));
			bufffer.append(hexDigits.charAt(j % 16));
		}

		return bufffer.toString();
	}

	public String criptografaSenha(String senha) {
		String senhaCriptografada = null;

		try {
			senhaCriptografada = byteArrayToHexString(digest(senha.getBytes(), "md5"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("N�o foi poss�vel criptografar a senha"); // modificar
		}

		return senhaCriptografada;
	}
}
