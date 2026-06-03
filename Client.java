import java.util.ArrayList;

public class Client {
    private String Id;
    private Person PersonDetails;
    private ArrayList<Account> AcList;
    private static int count = 0; // for account number generation and keeping track of accounts

    Client(Person PersonDetails)
    {
        this.Id = "cilent-" + count;  //
        this.PersonDetails = PersonDetails;
        this.AcList = new ArrayList<Account>();
        count = count + 1;
    }

    Client(String id, Person PersonDetails)
    {
        this.Id = id;
        this.PersonDetails = PersonDetails;
        this.AcList = new ArrayList<Account>();
        count = count + 1;  //  increment count here too
    }

    public String getId()
    {
        return this.Id;

    }

    public void setId(String id)
    {
        this.Id = id;

    }

    public Person getPersonDetails()
    {
        return this.PersonDetails;

    }

    public void setPersonDetails(Person personDetails)
    {
        this.PersonDetails = personDetails;

    }

    public ArrayList<Account> getACList()
    {
        return this.AcList;

    }

    public void setACList(ArrayList<Account> AcList)
    {
        this.AcList = AcList;

    }

    public static int getCount()
    {
        return count;

    }

    public static void setCount(int count)
    {
        Client.count = count;

    }

    public float totalAmount() {
        float total = 0;
        for (Account Acc : AcList){
            total = total + Acc.getAmount();
        }
        return total;
    }

    public void withdraw(float amount, String AccNo){
        for (Account Acc: AcList){
            if (Acc.getNumber().equals(AccNo)){
                Acc.withdraw(amount);
                return;
            }
        }
        System.out.println("Account not found at given num " + AccNo);
    }

    public void deposit(float amount, String AccNo){
        for (Account Acc: AcList){
            if (Acc.getNumber().equals(AccNo)){
                Acc.deposit(amount);
                return;
            }
        }
        System.out.println("Account not found at acc num " + AccNo);
    }

    public void AddAccount(Account x){
        AcList.add(x);
        System.out.println("\nAccount was successfully added to the list of accounts for this client " + this.Id);
    }

    public String toString(){
        StringBuilder Str = new StringBuilder();
        Str.append("The Client with ID " + Id + PersonDetails.toString()).append("\n Has Accounts: " );
        for (Account Acc: AcList){
            Str.append(" " + Acc.toString() + "\n");
        }
        Str.append("The Total amount of money its the clients accounts is " + totalAmount());
        return Str.toString();

    }


}