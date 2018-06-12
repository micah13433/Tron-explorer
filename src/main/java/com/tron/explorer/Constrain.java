package com.tron.explorer;


public class Constrain {
	public final static long TRON_ALL_NUM = 100000000000L;
	
	public final static int pageSize = 20;
	public final static int delegatePageSize = 27;

	public final static int ACCOUNTCREATECONTRACT = 0;
	public final static int TRANSFERCONTRACT = 1;
	public final static int TRANSFERASSETCONTRACT = 2;
	public final static int VOTEASSETCONTRACT = 3;
	public final static int VOTEWITNESSCONTRACT = 4;
	public final static int WITNESSCREATECONTRACT = 5;
	public final static int ASSETISSUECONTRACT = 6;
	public final static int DEPLOYCONTRACT = 7;
	public final static int WITNESSUPDATECONTRACT = 8;
	public final static int PARTICIPATEASSETISSUECONTRACT = 9;
	public final static int ACCOUNTUPDATECONTRACT = 10;
	public final static int FREEZEBALANCECONTRACT= 11;
	public final static int UNFREEZEBALANCECONTRACT = 12;
	public final static int WITHDRAWBALANCECONTRACT = 13;
	public final static int UNFREEZEASSETCONTRACT = 14;
	public final static int UPDATEASSETCONTRACT = 15;
	public final static int CUSTOMCONTRACT = 20;
	  
	public final static int INDEX_LIST_NUMBER = 10;
	public final static int ONE_TRX = 1000000;
	
	public final static byte ADD_PRE_FIX_BYTE = (byte) 0x41;;
	public final static int ADDRESS_SIZE = 21;
	public final static int BASE58CHECK_ADDRESS_SIZE = 34;
	public final static int TRANSACTION_HASH_SIZE = 64;
	
	public final static String LASTEST_VERSION = "1.0.0";
}
