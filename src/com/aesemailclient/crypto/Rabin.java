package com.aesemailclient.crypto;

import java.nio.ByteBuffer;

public class Rabin {
	public int plaintext;
	public int kongruen;
	public String[] charTable;

	public Rabin() {
		// TODO Auto-generated constructor stub
		this.plaintext = 0;
		this.kongruen = 0;
	}
	
	public void setKongruen(int kongruen) {
		this.kongruen = kongruen;
	}
	
	public Long Encrypt(int n, int text) {
		
		String plaintext = Integer.toString(text, 2);
		plaintext = plaintext + plaintext;
		Long m = Long.parseLong(plaintext, 2);
		Long kong = (m - mod(m, Long.parseLong(Integer.toString(n)))) / n;
		this.kongruen = Integer.parseInt(Long.toString(kong));
		
		Long cipher = ModularExponential(m, 2, n);
		return cipher;
	}
	
	public byte[] Encrypt(int n, String text)
	{
		byte[] cipherByte = null;
		if (text.length() % 2 == 0) {
			cipherByte = new byte[text.length() * 4];
		} else {
			cipherByte = new byte[(text.length() + 1) * 4];
		}
		int i = 0;
		int j = 0;
		while (i < text.length()) {
			String firstChar = Integer.toString((int)text.charAt(i), 2);
			i++;
			String secondChar = "";
			if (i == text.length()) {
				secondChar = "00000000";
			} else {
				secondChar = String.format("%8s", Integer.toString((int)text.charAt(i), 2)).replace(' ', '0');
			}
			i++;
			
			int plainInt = Integer.parseInt(firstChar + secondChar, 2);
			Long cipherInt = this.Encrypt(n, plainInt);
			byte[] cipher = ByteBuffer.allocate(4).putInt(Integer.parseInt(Long.toString(cipherInt))).array();
			byte[] kongruenByte = ByteBuffer.allocate(4).putInt(Integer.parseInt(Long.toString(this.kongruen))).array();

			for (int k = 0; k < 4; k++) {
				cipherByte[(j*8)+k] = cipher[k];
				cipherByte[(j*8+4)+k] = kongruenByte[k];
			}
			j++;
		}
		return cipherByte;
	}
	
	public int Decrypt(Long cipher, int p, int q){
		int[] euclidean = ExtendedEuclidean(p, q);
		int Yp = euclidean[1];
		int Yq = euclidean[2];
		int mp = 0;
		int mq = 0;
		int n = p * q;
		
		mp = Integer.parseInt(ModularExponential(cipher, (p+1)/4, p).toString());
		mq = Integer.parseInt(ModularExponential(cipher, (q+1)/4, q).toString());
		
		Long R = mod((Yp * p * mq) + (Yq * q * mp), n);
		Long S = mod((Yp * p * mq) - (Yq * q * mp), n);
		Long T = mod(-1*(Yp * p * mq) + (Yq * q * mp), n);
		Long U = mod(-1*(Yp * p * mq) - (Yq * q * mp), n);
		
		R = (this.kongruen * n) + R;
		S = (this.kongruen * n) + S;
		T = (this.kongruen * n) + T;
		U = (this.kongruen * n) + U;
		
		if (checkSimilarity(R)) {
			return this.plaintext;
		}else if (checkSimilarity(S)) {
			return this.plaintext;
		}else if (checkSimilarity(T)) {
			return this.plaintext;
		}else if (checkSimilarity(U)) {
			return this.plaintext;
		}
		
		return 0;
	}
	
	public String Decrypt(int p, int q, byte[] cipherByte) {
		String plainText = "";
		
		for (int i = 0; i < cipherByte.length / 8; i++) {
			byte[] cipher = new byte[4];
			byte[] kongruenByte = new byte[4];
			
			for (int j = 0; j < kongruenByte.length; j++) {
				cipher[j] = cipherByte[(i*8)+j];
				kongruenByte[j] = cipherByte[(i*8+4)+j];
			}
			
			int cipherInt = ByteBuffer.wrap(cipher).getInt();
			this.kongruen = ByteBuffer.wrap(kongruenByte).getInt();
			
			int plainInt = this.Decrypt((long)cipherInt, p, q);
			String plainBitString = Integer.toString(plainInt, 2);
			plainText = plainText + (char)Integer.parseInt(plainBitString.substring(0, plainBitString.length() - 8), 2)
					+ (char)Integer.parseInt(plainBitString.substring(plainBitString.length() - 8), 2);
		}
		return plainText;
	}

	private Long ModularExponential(Long base, int exponent, int mod)
	{
		Long temp = 1L;
		for (int i = 0; i < exponent; i++) {
			temp = mod((temp * base), (long) mod);
		}
		
		return temp;
	}
	
	private int[] ExtendedEuclidean(int a, int b){
		int [] result = new int[3];
		
		int r1 = a;
		int r2 = b;
		int s1 = 1;
		int s2 = 0;
		int t1 = 0;
		int t2 = 1;
		
		while (r2 > 0) {
			int q = (int)r1/r2;
			int r = r1 - q * r2;
			r1 = r2;
			r2 = r;
			
			int s = s1 - q * s2;
			s1 = s2;
			s2 = s;
			
			int t = t1 - q * t2;
			t1 = t2;
			t2 = t;
		}
		
		result[0] = r1;
		result[1] = s1;
		result[2] = t1;
		return result;
	}
	
	public Long mod(Long base, Long mod)
	{
		Long temp = base % mod;
		if(temp >= 0)
			return temp;
		else
			return (temp + mod);
	}
	
	public Long mod(int base, int mod) {
		return this.mod(Long.parseLong(Integer.toString(base)), Long.parseLong(Integer.toString(mod)));
	}
	
	public Boolean checkSimilarity(Long p)
	{
		String binary = Long.toString(p, 2);
		if ((binary.length() % 2) == 0) {
			if (binary.substring(0, binary.length() / 2).equals(binary.substring(binary.length() /2))) {
				this.plaintext = Integer.parseInt(binary.substring(0, binary.length() /2), 2);
				return true;
			}else{
				return false;
			}
		} else {
			return false;
		}
	}
}
