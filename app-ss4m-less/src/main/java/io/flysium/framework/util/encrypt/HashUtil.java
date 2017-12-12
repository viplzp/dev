package io.flysium.framework.util.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 哈希加密算法工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class HashUtil {

	private static Logger logger = LoggerFactory.getLogger(HashUtil.class);

	public static final String ENC_MD5 = "MD5";
	public static final String ENC_SHA1 = "SHA-1";
	public static final String ENC_SHA256 = "SHA-256";/// 推荐使用

	private HashUtil() {
	}

	/**
	 * MD5加密算法
	 * 
	 * @param strSrc
	 *            明文字符串
	 * @return
	 */
	public static String md5(String strSrc) {
		return hash(strSrc, ENC_MD5);
	}

	/**
	 * SHA-256哈希加密算法
	 * 
	 * @param strSrc
	 *            明文字符串
	 * @return
	 */
	public static String sha256(String strSrc) {
		return hash(strSrc, ENC_SHA256);
	}

	/**
	 * SHA-256哈希加密算法
	 * 
	 * @param strSrc
	 *            明文字符串
	 * @param salt
	 *            盐
	 * @return
	 */
	public static String sha256(String strSrc, String salt) {
		return hash(strSrc, ENC_SHA256, salt);
	}

	/**
	 * 哈希+盐 加密算法
	 * 
	 * @param strSrc
	 *            明文字符串
	 * @param encName
	 *            哈希加密算法名称
	 * @param salt
	 *            盐
	 * @return
	 */
	private static String hash(String strSrc, String encName, String salt) {
		return hash(strSrc + salt, encName);
	}

	/**
	 * 哈希加密算法
	 * 
	 * @param strSrc
	 *            明文字符串
	 * @param encName
	 *            哈希加密算法名称
	 * @return
	 */
	private static String hash(String strSrc, String encName) {
		String encNameTemp = (encName == null || "".equals(encName)) ? ENC_SHA256 : encName;
		MessageDigest md = null;
		String strDes = null;

		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance(encNameTemp);
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return strDes;
	}

	private static String bytes2Hex(byte[] bts) {
		StringBuilder des = new StringBuilder();
		for (int i = 0; i < bts.length; i++) {
			String tmp = Integer.toHexString(bts[i] & 0xFF);
			if (tmp.length() == 1) {
				des.append('0');
			}
			des.append(tmp);
		}
		return des.toString();
	}

}
