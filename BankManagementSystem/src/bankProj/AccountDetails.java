package bankProj;

public class AccountDetails {
    public static void display(Customer customer) {
        System.out.println("\n--------- ACCOUNT DETAILS ---------");
        System.out.println("Username       : " + customer.username);
        System.out.println("Name           : " + customer.name);
        System.out.println("Address        : " + customer.address);
        System.out.println("Phone Number   : " + customer.phone);
        System.out.println("Current Balance: " + String.format("%.2f", customer.balance));
        System.out.println("-----------------------------------");
    }
}
