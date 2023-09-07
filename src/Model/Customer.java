package Model;

public class Customer {
    private String ID;
    private String name;
    private String address;
    private double salary;

    public Customer() {
    }

    public Customer(String ID, String name, String address, double salary) {
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.salary = salary;
    }

    public Customer(String code, String description, int unitPrice, double qtyOnHand) {
    }


    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getSalary() {
        return salary;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", salary=" + salary +
                '}';
    }
}
