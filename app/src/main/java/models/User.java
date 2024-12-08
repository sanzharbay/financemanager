package models;

import com.example.financemanager.R;

import java.util.ArrayList;

import Utils.FirebaseUtil;
import Utils.Globals;

public class User {
    private int balance = 0;
    private String uid;
    private String tokenMessage;
    private String pinHash;
    private String encodedAvatar;
    private String phoneNumber;
    private String username;
    private String fullName;
    private final ArrayList<Account> accounts = new ArrayList<>();
    private final ArrayList<Category> categories = new ArrayList<>();
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public User() { }

    public String getEncodedAvatar() {
        return encodedAvatar;
    }

    public void setEncodedAvatar(String encodedAvatar) {
        this.encodedAvatar = encodedAvatar;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUID() {
        return uid;
    }
    public String getUsername() {
        return username;
    }


    public int getBalance() {
        return balance;
    }

    public void countBalance() {
        balance = 0;
        for (Account account : accounts){
            balance+=account.getAmount();
        }
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }


    public ArrayList<Category> getCategories() {
        return categories;
    }


    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }


    public String getTokenMessage() {
        return tokenMessage;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public void setTokenMessage(String tokenMessage) {
        this.tokenMessage = tokenMessage;
    }


    public User(String phoneNumber, String username, String uid, String fullName) {
        this.phoneNumber = "+7"+phoneNumber;
        this.username = username;
        this.uid = uid;
        this.fullName = fullName;
        defaultUser();
    }
    public void newTransaction(Transaction transaction){
        transactions.add(transaction);
        Globals.getInstance().getUser().accounts.get(transaction.getAccountID()).minusAmount(transaction.getAmount());
        countBalance();
        FirebaseUtil.allUserCollectionReference().child(uid).setValue(this);
    }
    public void newTransfer(Transfer transfer){
        accounts.get(0).minusAmount(transfer.getAmount());
        countBalance();
        FirebaseUtil.allUserCollectionReference().child(uid).setValue(this);
    }

    public void newNegTransfer(Transfer transfer){
        accounts.get(0).minusAmount(-transfer.getAmount());
        countBalance();
        FirebaseUtil.allUserCollectionReference().child(uid).setValue(this);
    }

    private void defaultUser() {
        String currency = "KZT";
        accounts.add(new Account("Main", currency, 0, 0));
        accounts.add(new Account("Deposit", currency, 0, 0));
        accounts.add(new Account("Cash", currency, 0, 0));
        categories.add(new Category(R.drawable._665941_suitcase_medical_healthcare_icon, "Health", R.color.black));
        categories.add(new Category(R.drawable._665027_basket_shopping_icon, "Grocery", R.color.black));
        categories.add(new Category(R.drawable._665470_graduation_cap_icon,"Education", R.color.black));
        categories.add(new Category(R.drawable._665630_money_bill_wave_finance_icon, "Leisure", R.color.black));
        categories.add(new Category(R.drawable._665553_gift_icon, "Gifts", R.color.black));
    }
}
