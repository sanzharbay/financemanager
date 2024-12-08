package models;

public class Account {
    private String name, currency;
    private int amount,  autoIncome;

    public Account() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }
    /**
     * Subtracts an amount from account`s count.
     * @param amount A sample parameter
     */
    public void minusAmount(int amount) {
        this.amount -= amount;
    }
    public Account(String name, String currency, int amount, int autoIncome) {
        this.name = name;
        this.currency = currency;
        this.amount = amount;
        this.autoIncome = autoIncome;
    }
}
