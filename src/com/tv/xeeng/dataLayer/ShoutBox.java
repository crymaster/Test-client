/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.tv.xeeng.dataLayer;

public class ShoutBox {

	static {
		try {
			System.loadLibrary("shoutbox");
		} catch (Exception e) {
			System.err.println("Native code library failed to load. \n" + e);
			System.exit(1);
		}
	}

	public static String shoutbox_gen_hash(String salt, String user,
			String message) throws Exception {
		byte[] buff = new byte[100];
		shoutbox_gen_hash(salt, user, message, buff);
		return new String(buff, "US-ASCII");
	}

	public static int shoutbox_gen_hash(String salt, String user,
			String message, byte[] p_obuff) {
		return ShoutBoxJNI.shoutbox_gen_hash(salt, user, message, p_obuff);
	}

}