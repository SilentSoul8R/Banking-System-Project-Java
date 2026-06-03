public class Account {
    private String number;
    private float amount;
    private Client ACholder;
    private static int count; // for customer id generation and keep track of count

    Account(Client acHolder) {
        this.number = "acc-" + count;
        this.amount = 0;
        this.ACholder = acHolder;
        count = count + 1;

    }

    public Account(float amount, Client acHolder) {
        this.number = "acc-" + count;
        this.amount = amount;
        this.ACholder = acHolder;
        count = count + 1;

    }

    public Account(String number, float amount, Client acHolder) {
        this.number = number;
        this.amount = amount;
        this.ACholder = acHolder;
        count = count + 1;
    }

    public String getNumber() {
        return number;

    }

    public void setNumber(String number) {
        this.number = number;

    }

    public float getAmount() {
        return amount;

    }

    public void setAmount(float amount) {
        this.amount = amount;

    }

    public Client getAcHolder() {
        return ACholder;

    }

    public void setAcHolder(Client acHolder) {
        this.ACholder = acHolder;

    }

    public static int getCount() {
        return count;

    }

    public static void setCount(int count) {
        Account.count = count;

    }

    public float withdraw(float amount) {
        if (amount <= 0) {
            System.out.println("Invalid Amount");
            return -1;
        } else if (amount > this.amount) {
            System.out.println("Insufficient funds");
            return -1;
        } else {
            this.amount = this.amount - amount;
            System.out.println(amount + " was withdrawn from the account");
            System.out.println("The remaining amount in the account is " + this.amount);
            return this.amount;
        }
    }

    public float deposit(float amount) {
        if (amount > 0) {
            this.amount = this.amount + amount;
            System.out.println(amount + " was deposited");
            System.out.println("Current balance " + this.amount);
            return this.amount;
        } else {
            System.out.println("Invalid deposit amount");
            return -1;
        }
    }

    @Override
    public String toString() {
        return ("Account number = '" + number + "',\n amount on this account is = " + amount + ",\n holder is = " + (ACholder != null ? ACholder.getId() : "None"));
    }

}