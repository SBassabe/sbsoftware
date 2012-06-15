package it.sbsoft.utility;

import java.io.UnsupportedEncodingException;

public class CodeEncodeString
{
	private static final String [] DEFAULT_KEYS = {
		"ACB39201293948ABE4839201CD01210F",
		"B39201293948ABE483901210AC201CDF",
		"89ABC289120930812098980981098309",
		"AABBCCDD019201920384383728298109",		
		"1CDF0120A948ACB39201293BE4839201",
		"01210AC93948ABEB3920124839201CDF",
		"9201293948ABE4801210ACB339201CDF",
		"01210ACB39201293948ABE4839201CDF",
		"123219843895AFDE3920291038103839",
		"2012939ACB3948ABE483901210201CDF",
		"12939401210ACB39208ABE4839201CDF"
	};
	private static boolean updatedProps = false;
	
	private static final char[] hexDigits = {
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	};

	private static byte [][] keys = null;

	public CodeEncodeString() {
		init(CodeEncodeString.DEFAULT_KEYS);
	}

	public CodeEncodeString(String [] keystrs) {
		init(keystrs);
	}
	
		
	private static CodeEncodeString instance;

	/**
	 * 
	 * @return
	 */
	public static synchronized CodeEncodeString getInstance() {
		if (instance == null) {
			instance = new CodeEncodeString();
		}
		return instance;
	}
	
	

	private void init(String [] keystrs) {
		keys = new byte[keystrs.length][];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = fromString(keystrs[i]);
		}
	}

	private String toString(byte[] ba) {
		char[] buf = new char[ba.length * 2];
		int j = 0;
		int k;

		for (int i = 0; i < ba.length; i++) {
			k = ba[i];
			buf[j++] = hexDigits[(k >>> 4) & 0x0F];
			buf[j++] = hexDigits[ k        & 0x0F];
		}
		return new String(buf);
	}

	private static int fromDigit(char ch) {
		if (ch >= '0' && ch <= '9')
			return ch - '0';
		if (ch >= 'A' && ch <= 'F')
			return ch - 'A' + 10;
		if (ch >= 'a' && ch <= 'f')
			return ch - 'a' + 10;

		throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
	}

	private static byte[] fromString(String hex) {
		int len = hex.length();
		byte[] buf = new byte[((len + 1) / 2)];

		int i = 0, j = 0;
		if ((len % 2) == 1)
			buf[j++] = (byte) fromDigit(hex.charAt(i++));

		while (i < len) {
			buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) |
								fromDigit(hex.charAt(i++)));
		}
		return buf;
	}

	private byte encrypt(byte d, byte [] key) {
		byte e;

		e = d;
		for (int i = 0; i < key.length; i++) {
			e = (byte) ((int) e ^ (int) key[i]);
		}

		return e;
	}

	private static byte decrypt(byte e, byte [] key) {
		byte d;

		d = e;
		for (int i = key.length-1; i >= 0; i--) {
			d = (byte) ((int) d ^ (int) key[i]);
		}

		return d;
	}

	public String encrypt(String orig) {
		byte [] ect = null;
		int size;
		byte [] origBytes = null;
        
		try {
			origBytes = orig.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}

		ect = new byte[origBytes.length];
		for (int i = 0; i < origBytes.length; i += keys.length) {
			for (int j = 0; j < keys.length; j++) {
				if ((i+j) >= origBytes.length) {
					break;
				} else {
					ect[i+j] = encrypt(origBytes[i+j], keys[j]);
				}
			}
		}

		return toString(ect);
	}

	public String decrypt(String ectstr) {
		byte [] ect = null;
		int size;
		byte [] origBytes = null;
		String dctStr = null;

		ect = fromString(ectstr);
		origBytes = new byte[ect.length];
		for (int i = 0; i < origBytes.length; i += keys.length) {
			for (int j = 0; j < keys.length; j++) {
				if ((i+j) >= origBytes.length) {
					break;
				} else {
					origBytes[i+j] = decrypt(ect[i+j], keys[j]);
				}
			}
		}

		try {
			dctStr = new String(origBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
		return dctStr;
	}
	
	public static void main(String[] args){
		//args = new String[2];
		//args[0] = "-e";
		//args[1] = "calimero.nero@fetido.com";
		if (args==null || args.length!=2){
			System.err.println("Usage: CodeEncodeString [-option] pwd");
			System.err.println("Option: ");
			System.err.println("-e encrypt");
			System.err.println("-d decrypt");
		}else{
			String cmd = args[0];
			String elem = args[1];
			
			if (cmd.equalsIgnoreCase("-e")){
				System.out.println("La password criptata è: "+CodeEncodeString.getInstance().encrypt(elem));
				System.out.println("Controverifica: la password originale era " +CodeEncodeString.getInstance().decrypt(CodeEncodeString.getInstance().encrypt(elem)));
			}
	
			else if (cmd.equalsIgnoreCase("-d")){
				System.out.println("La password decriptata è: "+CodeEncodeString.getInstance().decrypt(elem)); 
				System.out.println("Controverifica: la password cifrata era:" +CodeEncodeString.getInstance().encrypt(CodeEncodeString.getInstance().decrypt(elem)));
			}
			else if (cmd.equalsIgnoreCase("-d"))
				System.out.println("Comando non supportato"); 
		}
	}

}

