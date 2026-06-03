public class Person {
    private String Name;
    private String CNIC;
    private String phoneNo;

    Person(){
        this.Name = "empty";
        this.CNIC = "empty";
        this.phoneNo = "empty";
    }

    Person(String Name, String CNIC, String phoneNo){
        this.Name = Name;
        this.CNIC = CNIC;
        this.phoneNo = phoneNo;
    }

    public String getName()
    {
        return this.Name;

    }

    public void setName(String x)
    {
        this.Name = x;

    }

    public String getCNIC()
    {
        return this.CNIC;

    }

    public void setCNIC(String y)
    {
        this.CNIC = y;

    }

    public String getPhoneNo()
    {
        return this.phoneNo;

    }

    public void setPhoneNo(String Phonenum)
    {
        this.phoneNo = Phonenum;

    }

    @Override
    public String toString(){
        return  ("\nThe Name of person is ;" + this.getName() + "\n The CNIC is :" + this.getCNIC() + "\n The phoneNo is :" + this.getPhoneNo());
    }
}
