package com.example.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * •¶Žš—ñ‚ÉSHA256‚ð‚©‚¯‚é
	 *
	 * @param orgStr •¶Žš—ñ
	 * @return SHA256‚ð‚©‚¯‚½•¶Žš—ñ
	 */
	public static String sha256(String str) {
		StringBuilder sb = null;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());
			byte[] cipher_byte = md.digest();
			 sb = new StringBuilder(2 * cipher_byte.length);
			for(byte b: cipher_byte) {
				sb.append(String.format("%02x", b&0xff) );
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
