package models;

public class Transaction {
    private Category category;
    private int accountID;
    private int amount;
    private long time;


    public Transaction(int amount, int account, Category category, long time) {
        this.amount = amount;
        this.accountID = account;
        this.category = category;
        this.time = time;
    }

    public Transaction() {
    }

    public int getAmount() {
        return amount;
    }

    public long getTime() {
        return time;
    }


    public int getAccountID() {
        return accountID;
    }

    public Category getCategory() {
        return category;
    }
}
