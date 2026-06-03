import java.util.ArrayList;

public class Bank{
    private String name;
    private ArrayList<Client> ClList;
    private ArrayList<Account> AcList;

    public Bank(String name) {
        this.name = name;
        this.ClList = new ArrayList<>();
        this.AcList = new ArrayList<>();
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<Client> getClList()
    {
        return ClList;
    }

    public void setClList(ArrayList<Client> clList)
    {
        this.ClList = clList;
    }

    public ArrayList<Account> getAcList()
    {
        return AcList;
    }

    public void setAcList(ArrayList<Account> acList)
    {
        this.AcList = acList;
    }

    public Client addClient(Person x){
        Client NewClient = new Client(x);
        ClList.add(NewClient);
        System.out.println("\n Client successfully added " + NewClient.getId());
        return NewClient;
    }

    public Account addAccount(String ID, float amount, Client x){
        if (!ClList.contains(x)){
            System.out.println("Client is not registered in the Bank");
            return null;
        }
        Account AccX = new Account(ID, amount, x);
        x.getACList().add(AccX);
        //  also add the account to the bank's account list
        AcList.add(AccX);
        System.out.println("Account Created :" + AccX.getNumber() + " for client :" + x.getId());
        return AccX;
    }

    public Account SearchAccount(String ID){
        for (Account Acc: AcList){
            if (Acc.getNumber().equals(ID))
            {
                return Acc;
            }
        }
        return null;
    }

    public boolean removeClient(String ID){
        for (int i = 0; i < ClList.size(); i++) {
            Client c = ClList.get(i);
            if (c.getId().equals(ID)) {
                ArrayList<Account> toRemove = new ArrayList<>();
                for (Account acc : AcList) {
                    if (acc.getAcHolder().getId().equals(ID)) {
                        toRemove.add(acc);
                    }
                }
                // removeall ik function hy jo remove karwata hy sab cheezay ik arraylist sy
                AcList.removeAll(toRemove);
                ClList.remove(i);
                System.out.println("Client with ID: " + ID + " and all their accounts removed.");
                return true;
            }
        }
        System.out.println("Client not found: " + ID);
        return false;
    }

    public float totalAmount(){
        float total = 0;
        for (Account Acc: AcList){
            total = total + Acc.getAmount();
        }
        return total;
    }

    public Client SearchCustomerDetail(String CNIC){
        for (Client c : ClList){
            if (c.getPersonDetails().getCNIC().equals(CNIC)){
                return c;
            }
        }
        return null;
    }

    public void displayBankDetails() {
        System.out.println("Bank : " + name );
        System.out.println("Total Clients : " + ClList.size());
        System.out.println("Total Accounts : " + AcList.size());
        System.out.println("Total Bank Balance : " + totalAmount());
        System.out.println("Client List :");
        for (Client c : ClList) {
            System.out.println("  " + c.getId() + " - " + c.getPersonDetails().getName());
        }
    }

}
