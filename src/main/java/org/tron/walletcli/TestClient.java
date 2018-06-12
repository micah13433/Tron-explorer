package org.tron.walletcli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.api.GrpcAPI.AssetIssueList;
import org.tron.api.GrpcAPI.BlockList;
import org.tron.api.GrpcAPI.Node;
import org.tron.api.GrpcAPI.NodeList;
import org.tron.api.GrpcAPI.NumberMessage;
import org.tron.api.GrpcAPI.TransactionList;
import org.tron.api.GrpcAPI.WitnessList;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Transaction;
import org.tron.walletserver.WalletClient;

import com.tron.explorer.Constrain;
import com.tron.explorer.encrypt.Base58;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TestClient {

  private static final Logger logger = LoggerFactory.getLogger("TestClient");
  private static Client client = new Client();

  private void registerWallet(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("RegisterWallet need 1 parameter like following: ");
      System.out.println("RegisterWallet Password");
      return;
    }
    String password = parameters[0];

    if (client.registerWallet(password)) {
      logger.info("Register a wallet and store it successful !!");
    } else {
      logger.info("Register wallet failed !!");
    }
  }

  private void importWallet(String[] parameters) {
    if (parameters == null || parameters.length != 2) {
      System.out.println("ImportWallet need 2 parameter like following: ");
      System.out.println("ImportWallet Password PriKey");
      System.out.println("PriKey need Hex string format.");
      return;
    }
    String password = parameters[0];
    String priKey = parameters[1];

    if (client.importWallet(password, priKey)) {
      logger.info("Import a wallet and store it successful !!");
    } else {
      logger.info("Import a wallet failed !!");
    }
  }

  private void importwalletByBase64(String[] parameters) {
    if (parameters == null || parameters.length != 2) {
      System.out.println("ImportwalletByBase64 need 2 parameter like following: ");
      System.out.println("ImportwalletByBase64 Password PriKey");
      System.out.println("PriKey need base64 string format.");
      return;
    }
    String password = parameters[0];
    String priKey64 = parameters[1];
    Decoder decoder = Base64.getDecoder();
    String priKey = ByteArray.toHexString(decoder.decode(priKey64));

    if (client.importWallet(password, priKey)) {
      logger.info("Import a wallet and store it successful !!");
    } else {
      logger.info("Import a wallet failed !!");
    }
  }

  private void changePassword(String[] parameters) {
    if (parameters == null || parameters.length != 2) {
      System.out.println("ChangePassword need 2 parameter like following: ");
      System.out.println("ChangePassword OldPassword NewPassword ");
      return;
    }
    String oldPassword = parameters[0];
    String newPassword = parameters[1];

    if (client.changePassword(oldPassword, newPassword)) {
      logger.info("ChangePassword successful !!");
    } else {
      logger.info("ChangePassword failed !!");
    }
  }

  private void login(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("Login need 1 parameter like following: ");
      System.out.println("Login Password ");
      return;
    }
    String password = parameters[0];

    boolean result = client.login(password);
    if (result) {
      logger.info("Login successful !!!");
    } else {
      logger.info("Login failed !!!");
    }
  }

  private void logout() {
    client.logout();
    logger.info("Logout successful !!!");
  }

  private void backupWallet(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("BackupWallet need 1 parameter like following: ");
      System.out.println("BackupWallet Password ");
      return;
    }
    String password = parameters[0];

    String priKey = client.backupWallet(password);
    if (priKey != null) {
      logger.info("Backup a wallet successful !!");
      logger.info("priKey = " + priKey);
    }
  }

  private void backupWallet2Base64(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("BackupWallet2Base64 need 1 parameter like following: ");
      System.out.println("BackupWallet2Base64 Password ");
      return;
    }
    String password = parameters[0];
    String priKey = client.backupWallet(password);
    Encoder encoder = Base64.getEncoder();
    String priKey64 = encoder.encodeToString(ByteArray.fromHexString(priKey));
    if (priKey != null) {
      logger.info("Backup a wallet successful !!");
      logger.info("priKey = " + priKey64);
    }
  }

  private void getAddress() {
    String address = client.getAddress();
    if (address != null) {
      logger.info("GetAddress successful !!");
      logger.info("address = " + address);
    }
  }

  private void getBalance() {
    Account account = client.queryAccount();
    if (account == null) {
      logger.info("Get Balance failed !!!!");

    } else {
      long balance = account.getBalance();
      logger.info("Balance = " + balance);
    }
  }

  private void getAccount(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("GetAccount need 1 parameter like following: ");
      System.out.println("GetAccount Address ");
      return;
    }
    String address = parameters[0];
    byte[] addressBytes = WalletClient.decodeFromBase58Check(address);
    if (addressBytes == null) {
      return;
    }

    Account account = WalletClient.queryAccount(addressBytes);
    if (account == null) {
      logger.info("Get Account failed !!!!");
    } else {
      logger.info("\n" + Utils.printAccount(account));
    }
  }

  private void updateAccount(String[] parameters) {
    if (parameters == null || parameters.length != 2) {
      System.out.println("UpdateAccount need 2 parameter like following: ");
      System.out.println("UpdateAccount Password AccountName ");
      return;
    }

    String password = parameters[0];
    String accountName = parameters[1];
    byte[] accountNameBytes = ByteArray.fromString(accountName);

    boolean ret = client.updateAccount(password, accountNameBytes);
    if (ret) {
      logger.info("Update Account success !!!!");
    } else {
      logger.info("Update Account failed !!!!");
    }
  }

  private void getAssetIssueByAccount(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("GetAssetIssueByAccount need 1 parameter like following: ");
      System.out.println("GetAssetIssueByAccount Address ");
      return;
    }
    String address = parameters[0];
    byte[] addressBytes = WalletClient.decodeFromBase58Check(address);
    if (addressBytes == null) {
      return;
    }

    Optional<AssetIssueList> result = WalletClient.getAssetIssueByAccount(addressBytes);
    if (result.isPresent()) {
      AssetIssueList assetIssueList = result.get();
      logger.info(Utils.printAssetIssueList(assetIssueList));
    } else {
      logger.info("GetAssetIssueByAccount " + " failed !!");
    }
  }

  private void getAssetIssueByName(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("GetAssetIssueByName need 1 parameter like following: ");
      System.out.println("GetAssetIssueByName AssetName ");
      return;
    }
    String assetName = parameters[0];

    AssetIssueContract assetIssueContract = WalletClient.getAssetIssueByName(assetName);
    if (assetIssueContract != null) {
      logger.info("\n" + Utils.printAssetIssue(assetIssueContract));
    } else {
      logger.info("GetAssetIssueByName " + " failed !!");
    }
  }

  private void sendCoin(String[] parameters) {
    if (parameters == null || parameters.length != 3) {
      System.out.println("SendCoin need 3 parameter like following: ");
      System.out.println("SendCoin Password ToAddress Amount");
      return;
    }
    String password = parameters[0];
    String toAddress = parameters[1];
    String amountStr = parameters[2];
    long amount = new Long(amountStr);

    boolean result = client.sendCoin(password, toAddress, amount);
    if (result) {
      logger.info("Send " + amount + " drop to " + toAddress + " successful !!");
    } else {
      logger.info("Send " + amount + " drop to " + toAddress + " failed !!");
    }
  }

  private void testTransaction(String[] parameters) {
    if (parameters == null || (parameters.length != 4 && parameters.length != 5)) {
      System.out.println("testTransaction need 4 or 5 parameter like following: ");
      System.out.println("testTransaction Password ToAddress assertName times");
      System.out.println("testTransaction Password ToAddress assertName times interval");
      System.out.println("If needn't transferAsset, assertName input null");
      return;
    }
    String password = parameters[0];
    String toAddress = parameters[1];
    String assertName = parameters[2];
    String loopTime = parameters[3];
    int intervalInt = 0;//s
    if (parameters.length == 5) {
      String interval = parameters[4];
      intervalInt = Integer.parseInt(interval);//s
    }
    intervalInt *= 500; //ms
    long times = new Long(loopTime);

    for (int i = 1; i <= times; i++) {
      long amount = i;
      boolean result = client.sendCoin(password, toAddress, amount);
      if (result) {
        logger.info("Send " + amount + " drop to " + toAddress + " successful !!");
        if (intervalInt > 0) {
          try {
            Thread.sleep(intervalInt);
          } catch (Exception e) {
            e.printStackTrace();
            break;
          }
        }
      } else {
        logger.info("Send " + amount + " drop to " + toAddress + " failed !!");
        break;
      }

      if (!"null".equalsIgnoreCase(assertName)) {
        result = client.transferAsset(password, toAddress, assertName, amount);
        if (result) {
          logger
              .info("transferAsset " + amount + assertName + " to " + toAddress + " successful !!");
          if (intervalInt > 0) {
            try {
              Thread.sleep(intervalInt);
            } catch (Exception e) {
              e.printStackTrace();
              break;
            }
          }
        } else {
          logger.info("transferAsset " + amount + assertName + " to " + toAddress + " failed !!");
          break;
        }
      }
    }

  }

  private void transferAsset(String[] parameters) {
    if (parameters == null || parameters.length != 4) {
      System.out.println("TransferAsset need 4 parameter like following: ");
      System.out.println("TransferAsset Password ToAddress AssertName Amount");
      return;
    }
    String password = parameters[0];
    String toAddress = parameters[1];
    String assertName = parameters[2];
    String amountStr = parameters[3];
    long amount = new Long(amountStr);

    boolean result = client.transferAsset(password, toAddress, assertName, amount);
    if (result) {
      logger.info("TransferAsset " + amount + " to " + toAddress + " successful !!");
    } else {
      logger.info("TransferAsset " + amount + " to " + toAddress + " failed !!");
    }
  }

  private void participateAssetIssue(String[] parameters) {
    if (parameters == null || parameters.length != 4) {
      System.out.println("ParticipateAssetIssue need 4 parameter like following: ");
      System.out.println("ParticipateAssetIssue Password ToAddress AssertName Amount");
      return;
    }
    String password = parameters[0];
    String toAddress = parameters[1];
    String assertName = parameters[2];
    String amountStr = parameters[3];
    long amount = new Integer(amountStr);

    boolean result = client.participateAssetIssue(password, toAddress, assertName, amount);
    if (result) {
      logger.info("ParticipateAssetIssue " + assertName + " " + amount + " from " + toAddress
          + " successful !!");
    } else {
      logger.info("ParticipateAssetIssue " + assertName + " " + amount + " from " + toAddress
          + " failed !!");
    }
  }

  private void assetIssue(String[] parameters) {
    if (parameters == null || parameters.length < 9 || (parameters.length & 1) == 0) {
      System.out.println("Use assetIssue command you need like: ");
      System.out.println(
          "AssetIssue Password AssetName TotalSupply TrxNum AssetNum "
              + "StartDate EndDate Description Url "
              + "FrozenAmount0 FrozenDays0 ... FrozenAmountN FrozenDaysN");
      System.out
          .println("TrxNum and AssetNum represents the conversion ratio of the tron to the asset.");
      System.out.println("The StartDate and EndDate format should look like 2018-3-1 2018-3-21 .");
      return;
    }

    String password = parameters[0];
    String name = parameters[1];
    String totalSupplyStr = parameters[2];
    String trxNumStr = parameters[3];
    String icoNumStr = parameters[4];
    String startYyyyMmDd = parameters[5];
    String endYyyyMmDd = parameters[6];
    String description = parameters[7];
    String url = parameters[8];
    HashMap<String, String> frozenSupply = new HashMap<>();
    for (int i = 9; i < parameters.length; i += 2) {
      String amount = parameters[i];
      String days = parameters[i + 1];
      frozenSupply.put(days, amount);
    }

    long totalSupply = new Long(totalSupplyStr);
    int trxNum = new Integer(trxNumStr);
    int icoNum = new Integer(icoNumStr);
    Date startDate = Utils.strToDateLong(startYyyyMmDd);
    Date endDate = Utils.strToDateLong(endYyyyMmDd);
    long startTime = startDate.getTime();
    long endTime = endDate.getTime();

    boolean result = client
        .assetIssue(password, name, totalSupply, trxNum, icoNum, startTime, endTime,
            0, description, url, frozenSupply);
    if (result) {
      logger.info("AssetIssue " + name + " successful !!");
    } else {
      logger.info("AssetIssue " + name + " failed !!");
    }
  }

  private void createWitness(String[] parameters) {
    if (parameters == null || parameters.length != 2) {
      System.out.println("CreateWitness need 2 parameter like following: ");
      System.out.println("CreateWitness Password Url");
      return;
    }

    String password = parameters[0];
    String url = parameters[1];

    boolean result = client.createWitness(password, url);
    if (result) {
      logger.info("CreateWitness " + " successful !!");
    } else {
      logger.info("CreateWitness " + " failed !!");
    }
  }


//  private void listAccounts() {
//    Optional<AccountList> result = client.listAccounts();
//    if (result.isPresent()) {
//      AccountList accountList = result.get();
//      logger.info(Utils.printAccountList(accountList));
//    } else {
//      logger.info("List accounts " + " failed !!");
//    }
//  }

  private void updateWitness(String[] parameters) {
    if (parameters == null || parameters.length != 2) {
      System.out.println("updateWitness need 2 parameter like following: ");
      System.out.println("updateWitness Password Url");
      return;
    }

    String password = parameters[0];
    String url = parameters[1];

    boolean result = client.updateWitness(password, url);
    if (result) {
      logger.info("updateWitness " + " successful !!");
    } else {
      logger.info("updateWitness " + " failed !!");
    }
  }


  private void listWitnesses() {
    Optional<WitnessList> result = client.listWitnesses();
    if (result.isPresent()) {
      WitnessList witnessList = result.get();
      logger.info(Utils.printWitnessList(witnessList));
    } else {
      logger.info("List witnesses " + " failed !!");
    }
  }

  private void getAssetIssueList() {
    Optional<AssetIssueList> result = client.getAssetIssueList();
    if (result.isPresent()) {
      AssetIssueList assetIssueList = result.get();
      logger.info(Utils.printAssetIssueList(assetIssueList));
    } else {
      logger.info("GetAssetIssueList " + " failed !!");
    }
  }

  private void listNodes() {
    Optional<NodeList> result = client.listNodes();
    if (result.isPresent()) {
      NodeList nodeList = result.get();
      List<Node> list = nodeList.getNodesList();
      for (int i = 0; i < list.size(); i++) {
        Node node = list.get(i);
        logger.info("IP::" + ByteArray.toStr(node.getAddress().getHost().toByteArray()));
        logger.info("Port::" + node.getAddress().getPort());
      }
    } else {
      logger.info("GetAssetIssueList " + " failed !!");
    }
  }

  private void GetBlock(String[] parameters) {
    long blockNum = -1;

    if (parameters == null || parameters.length == 0) {
      System.out.println("Get current block !!!!");
    } else {
      if (parameters.length != 1) {
        System.out.println("Get block too many paramters !!!");
        System.out.println("You can get current block like:");
        System.out.println("Getblock");
        System.out.println("Or get block by number like:");
        System.out.println("Getblock BlockNum");
      }
      blockNum = Long.parseLong(parameters[0]);
    }
    Block block = client.GetBlock(blockNum);
    if (block == null) {
      logger.info("No block for num : " + blockNum);
      return;
    }
    logger.info(Utils.printBlock(block));
  }

  private void voteWitness(String[] parameters) {
    if (parameters == null || parameters.length < 3 || (parameters.length & 1) != 1) {
      System.out.println("Use VoteWitness command you need like: ");
      System.out.println("VoteWitness Password Address0 Count0 ... AddressN CountN");
      return;
    }

    String password = parameters[0];
    HashMap<String, String> witness = new HashMap<String, String>();
    for (int i = 1; i < parameters.length; i += 2) {
      String address = parameters[i];
      String countStr = parameters[i + 1];
      witness.put(address, countStr);
    }

    boolean result = client.voteWitness(password, witness);
    if (result) {
      logger.info("VoteWitness " + " successful !!");
    } else {
      logger.info("VoteWitness " + " failed !!");
    }
  }

  private void freezeBalance(String[] parameters) {
    if (parameters == null || parameters.length != 3) {
      System.out.println("Use freezeBalance command you need like: ");
      System.out.println("freezeBalance Password frozen_balance frozen_duration ");
      return;
    }

    String password = parameters[0];
    long frozen_balance = Long.parseLong(parameters[1]);
    long frozen_duration = Long.parseLong(parameters[2]);

    boolean result = client.freezeBalance(password, frozen_balance, frozen_duration);
    if (result) {
      logger.info("freezeBalance " + " successful !!");
    } else {
      logger.info("freezeBalance " + " failed !!");
    }
  }

  private void unfreezeBalance(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("Use unfreezeBalance command you need like: ");
      System.out.println("unfreezeBalance Password ");
      return;
    }
    String password = parameters[0];

    boolean result = client.unfreezeBalance(password);
    if (result) {
      logger.info("unfreezeBalance " + " successful !!");
    } else {
      logger.info("unfreezeBalance " + " failed !!");
    }
  }

  private void unfreezeAsset(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("Use unfreezeAsset command you need like: ");
      System.out.println("unfreezeAsset Password ");
      return;
    }
    String password = parameters[0];

    boolean result = client.unfreezeAsset(password);
    if (result) {
      logger.info("unfreezeAsset " + " successful !!");
    } else {
      logger.info("unfreezeAsset " + " failed !!");
    }
  }

  private void withdrawBalance(String[] parameters) {
    if (parameters == null || parameters.length != 1) {
      System.out.println("Use withdrawBalance command you need like: ");
      System.out.println("withdrawBalance Password ");
      return;
    }
    String password = parameters[0];

    boolean result = client.withdrawBalance(password);
    if (result) {
      logger.info("withdrawBalance " + " successful !!");
    } else {
      logger.info("withdrawBalance " + " failed !!");
    }
  }

  private void getTotalTransaction() {
    try {
      NumberMessage totalTransition = client.getTotalTransaction();
      logger.info("The num of total transactions is : " + totalTransition.getNum());

    } catch (Exception e) {
      logger.info("GetTotalTransaction " + " failed !!");
    }
  }

  private void getNextMaintenanceTime() {
    try {
      NumberMessage nextMaintenanceTime = client.getNextMaintenanceTime();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String date = formatter.format(nextMaintenanceTime.getNum());
      logger.info("Next maintenance time is : " + date);

    } catch (Exception e) {
      logger.info("GetNextMaintenanceTime " + " failed !!");
    }
  }

//  private void getAssetIssueListByTimestamp(String[] parameters) {
//    long timeStamp = -1;
//    if (parameters == null || parameters.length == 0) {
//      System.out.println("no time input, use current time");
//      timeStamp = System.currentTimeMillis();
//    } else {
//      if (parameters.length != 2) {
//        System.out.println("You can GetAssetIssueListByTimestamp like:");
//        System.out.println("GetAssetIssueListByTimestamp yyyy-mm-dd hh:mm:ss");
//        return;
//      } else {
//        timeStamp = Timestamp.valueOf(parameters[0] + " " + parameters[1]).getTime();
//      }
//    }
//    Optional<AssetIssueList> result = WalletClient.getAssetIssueListByTimestamp(timeStamp);
//    if (result.isPresent()) {
//      AssetIssueList assetIssueList = result.get();
//      logger.info(Utils.printAssetIssueList(assetIssueList));
//    } else {
//      logger.info("GetAssetIssueListByTimestamp " + " failed !!");
//    }
//  }

  private void getTransactionById(String[] parameters) {
    String txid = "";
    if (parameters == null || parameters.length != 1) {
      System.out.println("getTransactionById needs 1 parameters, transaction id");
      return;
    } else {
      txid = parameters[0];
    }
    Optional<Transaction> result = WalletClient.getTransactionById(txid);
    if (result.isPresent()) {
      Transaction transaction = result.get();
      logger.info(Utils.printTransaction(transaction));
    } else {
      logger.info("getTransactionById " + " failed !!");
    }
  }

  private void getBlockById(String[] parameters) {
    String blockID = "";
    if (parameters == null || parameters.length != 1) {
      System.out.println("getBlockById needs 1 parameters, block id which is hex format");
      return;
    } else {
      blockID = parameters[0];
    }
    Optional<Block> result = WalletClient.getBlockById(blockID);
    if (result.isPresent()) {
      Block block = result.get();
      logger.info(Utils.printBlock(block));
    } else {
      logger.info("getBlockById " + " failed !!");
    }
  }

  private void getBlockByLimitNext(String[] parameters) {
    long start = 0;
    long end = 0;
    if (parameters == null || parameters.length != 2) {
      System.out.println("GetBlockByLimitNext needs 2 parameters, start block id and end block id");
      return;
    } else {
      start = Long.parseLong(parameters[0]);
      end = Long.parseLong(parameters[1]);
    }
    Optional<BlockList> result = WalletClient.getBlockByLimitNext(start, end);
    if (result.isPresent()) {
      BlockList blockList = result.get();
      logger.info(Utils.printBlockList(blockList));
    } else {
      logger.info("GetBlockByLimitNext " + " failed !!");
    }
  }

  private void getBlockByLatestNum(String[] parameters) {
    long num = 0;
    if (parameters == null || parameters.length != 1) {
      System.out.println("getBlockByLatestNum needs 1 parameters, block num");
      return;
    } else {
      num = Long.parseLong(parameters[0]);
    }
    Optional<BlockList> result = WalletClient.getBlockByLatestNum(num);
    if (result.isPresent()) {
      BlockList blockList = result.get();
      logger.info(Utils.printBlockList(blockList));
    } else {
      logger.info("getBlockByLatestNum " + " failed !!");
    }
  }

  private void help() {
    System.out.println("You can enter the following command: ");

    System.out.println("RegisterWallet");
    System.out.println("ImportWallet");
    System.out.println("ImportWalletByBase64");
    System.out.println("ChangePassword");
    System.out.println("Login");
    System.out.println("Logout");
    System.out.println("BackupWallet");
    System.out.println("BackupWallet2Base64");
    System.out.println("GetAddress");
    System.out.println("GetBalance");
    System.out.println("GetAccount");
    System.out.println("GetAssetIssueByAccount");
    System.out.println("GetAssetIssueByName");
    System.out.println("SendCoin");
    System.out.println("TransferAsset");
    System.out.println("ParticipateAssetIssue");
    System.out.println("AssetIssue");
    System.out.println("CreateWitness");
    System.out.println("UpdateWitness");
    System.out.println("VoteWitness");
    System.out.println("ListWitnesses");
    System.out.println("ListAssetIssue");
    System.out.println("ListNodes");
    System.out.println("GetBlock");
    System.out.println("GetTotalTransaction");
    System.out.println("GetAssetIssueListByTimestamp");
    System.out.println("GetTotalTransaction");
    System.out.println("GetNextMaintenanceTime");
    System.out.println("GetTransactionsByTimestamp");
    System.out.println("GetTransactionById");
    System.out.println("GetTransactionsFromThis");
    System.out.println("GetTransactionsToThis");
    System.out.println("GetBlockById");
    System.out.println("GetBlockByLimitNext");
    System.out.println("GetBlockByLatestNum");
    System.out.println("FreezeBalance");
    System.out.println("UnfreezeBalance");
    System.out.println("WithdrawBalance");
    System.out.println("UpdateAccount");
    System.out.println("UnfreezeAsset");
    System.out.println("Exit or Quit");

    System.out.println("Input any one of then, you will get more tips.");
  }

  private void run() {
    Scanner in = new Scanner(System.in);
    while (true) {
      try {
        String cmdLine = in.nextLine().trim();
        String[] cmdArray = cmdLine.split("\\s+");
        // split on trim() string will always return at the minimum: [""]
        String cmd = cmdArray[0];
        if ("".equals(cmd)) {
          continue;
        }
        String[] parameters = Arrays.copyOfRange(cmdArray, 1, cmdArray.length);
        String cmdLowerCase = cmd.toLowerCase();

        switch (cmdLowerCase) {
          case "help": {
            help();
            break;
          }
          case "registerwallet": {
            registerWallet(parameters);
            break;
          }
          case "importwallet": {
            importWallet(parameters);
            break;
          }
          case "importwalletbybase64": {
            importwalletByBase64(parameters);
            break;
          }
          case "changepassword": {
            changePassword(parameters);
            break;
          }
          case "login": {
            login(parameters);
            break;
          }
          case "logout": {
            logout();
            break;
          }
          case "backupwallet": {
            backupWallet(parameters);
            break;
          }
          case "backupwallet2base64": {
            backupWallet2Base64(parameters);
            break;
          }
          case "getaddress": {
            getAddress();
            break;
          }
          case "getbalance": {
            getBalance();
            break;
          }
          case "getaccount": {
            getAccount(parameters);
            break;
          }
          case "updateaccount": {
            updateAccount(parameters);
            break;
          }
          case "getassetissuebyaccount": {
            getAssetIssueByAccount(parameters);
            break;
          }
          case "getassetissuebyname": {
            getAssetIssueByName(parameters);
            break;
          }
          case "sendcoin": {
            sendCoin(parameters);
            break;
          }
          case "testtransaction": {
            testTransaction(parameters);
            break;
          }
          case "transferasset": {
            transferAsset(parameters);
            break;
          }
          case "participateassetissue": {
            participateAssetIssue(parameters);
            break;
          }
          case "assetissue": {
            assetIssue(parameters);
            break;
          }
          case "createwitness": {
            createWitness(parameters);
            break;
          }
          case "updatewitness": {
            updateWitness(parameters);
            break;
          }
          case "votewitness": {
            voteWitness(parameters);
            break;
          }
          case "freezebalance": {
            freezeBalance(parameters);
            break;
          }
          case "unfreezebalance": {
            unfreezeBalance(parameters);
            break;
          }
          case "unfreezeasset": {
            unfreezeAsset(parameters);
            break;
          }
          case "withdrawbalance": {
            withdrawBalance(parameters);
            break;
          }
//          case "listaccounts": {
//            listAccounts();
//            break;
//          }
          case "listwitnesses": {
            listWitnesses();
            break;
          }
          case "listassetissue": {
            getAssetIssueList();
            break;
          }
          case "listnodes": {
            listNodes();
            break;
          }
          case "getblock": {
            GetBlock(parameters);
            break;
          }
          case "gettotaltransaction": {
            getTotalTransaction();
            break;
          }
          case "getnextmaintenancetime": {
            getNextMaintenanceTime();
            break;
          }
//          case "getassetissuelistbytimestamp": {
//            getAssetIssueListByTimestamp(parameters);
//            break;
//          }
          case "gettransactionbyid": {
            getTransactionById(parameters);
            break;
          }
          case "getblockbyid": {
            getBlockById(parameters);
            break;
          }
          case "getblockbylimitnext": {
            getBlockByLimitNext(parameters);
            break;
          }
          case "getblockbylatestnum": {
            getBlockByLatestNum(parameters);
            break;
          }
          case "exit":
          case "quit": {
            System.out.println("Exit !!!");
            return;
          }
          default: {
            System.out.println("Invalid cmd: " + cmd);
            help();
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }

  }

  public static void main(String[] args) {
    TestClient cli = new TestClient();
    NumberMessage totalTransition = client.getTotalTransaction();
    System.out.println(totalTransition.getNum());
    Block block = client.GetBlock(12345);
    System.out.println(block.getTransactionsCount() + "--" + Base58.encodeChecked(block.getBlockHeader().getRawData().getWitnessAddress().toByteArray()));
       
     
  }
}