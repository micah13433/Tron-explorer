package com.tron.explorer.util;

public class CheckUtil {
	
	public static boolean  isValidAddress(String address) {
		if(address.length() != 35) return false;		
		return true;
	}
}
