package com.aesemailclient.crypto;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SieveofEratosthenes {

	public SieveofEratosthenes() {
		// TODO Auto-generated constructor stub
	}
	
	public static List<Integer> generate(int min, int limit) {
		//int limit = 99999999; // Batas maksimum bil. prima yang dicari
        boolean[] bil_asal = new boolean[limit];
        for (int i = 0; i < limit; i++) {
            bil_asal[i] = true;
        }
        bil_asal[0] = false; // bilangan 0 bukan bilangan prima
        bil_asal[1] = false; // bilangan 1 bukan bilangan prima
 
        // Penerapan algoritma Sieve of Erathosthenes
        for (int i = 2; i <= Math.sqrt(limit); i++) {
            if (bil_asal[i]) {
                for (int j = i * i; j < limit; j = j + i) {
                    bil_asal[j] = false;
                }
            }
        }
 
        // Masukkan semua bilangan prima hasil pencarian ke dalam array "prima"
        List<Integer> prima = new ArrayList<Integer>();
        for (int i = 2; i < limit; i++) {
            if (bil_asal[i] && i >= min) {
                prima.add(i);
            }
        }
        System.out.println("Jumlah bilangan prima antara "+ min +" sampai " + limit
                + " adalah " + prima.size() + " buah");
        return prima;
	}
	
	public static List<Integer> generate() {
		return SieveofEratosthenes.generate(2, 99999999);
	}
	
	public static List<Integer> generate(int limit) {
		return SieveofEratosthenes.generate(2, limit);
	}
	
	public static List<Integer> generateRabinKey() {
		List<Integer> keys = new ArrayList<Integer>();
		List<Integer> prima = SieveofEratosthenes.generate(100, 1000);
		Random random = new Random();
		
		while (keys.size() != 2) {
			int temp = random.nextInt(prima.size());
			if (prima.get(temp) % 4 == 3) {
				if (!keys.contains(prima.get(temp))) {
					keys.add(prima.get(temp));
				}
			}
		}
		
		return keys;
	}

}
