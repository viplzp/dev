package org.jasig.cas.authentication.handler;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5 {
	public static String crypt(String str, boolean digit) {
		StringBuffer cryptString = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] hash = md.digest();

			for (int i = 0; i < hash.length; i++) {
				if (digit) {
					cryptString.append(byteDigit(hash[i]));
				} else {
					if ((0xff & hash[i]) < 0x10) {
						cryptString.append("0"
								+ Integer.toHexString((0xFF & hash[i])));
					} else {
						cryptString.append(Integer.toHexString(0xFF & hash[i]));
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return cryptString.toString();
	}

	public static String byteDigit(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X09];
		ob[1] = Digit[ib & 0X09];
		String s = new String(ob);
		return s;
	}

	public static String crypt(String str) {
		if (str == null || str.length() == 0) {
			throw new IllegalArgumentException(
					"String to encript cannot be null or zero length");
		}
		return crypt(str, false);
	}

	public static String cryptDigit(String str) {
		return crypt(str, true);
	}
}
