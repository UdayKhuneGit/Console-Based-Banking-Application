package bankProj;

import java.util.*;
import java.text.*;


interface SavingsAccount {
    double rate = 0.04, limit = 10000, limit1 = 200;
    void deposit(double n, Date d);
    void withdraw(double n, Date d);
}

class Customer implements SavingsAccount {
    String username, password, name, address, phone;
    double balance;
    ArrayList<String> transactions;

    Customer(String username, String password, String name, String address, String phone, double balance, Date date) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.balance = balance;
        transactions = new ArrayList<>(5);
        addTransaction(String.format("Initial deposit - " + NumberFormat.getCurrencyInstance().format(balance) + " as on %1$tD at %1$tT.", date));
    }

    void update(Date date) {
        if (balance >= limit) {
            balance += rate * balance;
        } else {
            balance -= (int) (balance / 100.0);
        }
        addTransaction(String.format("Account updated. Balance - " + NumberFormat.getCurrencyInstance().format(balance) + " as on %1$tD at %1$tT.", date));
    }

    public void deposit(double amount, Date date) {
        balance += amount;
        addTransaction(String.format(NumberFormat.getCurrencyInstance().format(amount) + " credited to your account. Balance - " + NumberFormat.getCurrencyInstance().format(balance) + " as on %1$tD at %1$tT.", date));
    }

    public void withdraw(double amount, Date date) {
        if (amount > (balance - limit1)) {
            System.out.println("Insufficient balance.");
            return;
        }
        balance -= amount;
        addTransaction(String.format(NumberFormat.getCurrencyInstance().format(amount) + " debited from your account. Balance - " + NumberFormat.getCurrencyInstance().format(balance) + " as on %1$tD at %1$tT.", date));
    }

    private void addTransaction(String message) {
        transactions.add(0, message);
        if (transactions.size() > 5) {
            transactions.remove(5);
            transactions.trimToSize();
        }
    }
}

public class BankManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Customer customer;
        String username, password;
        double amount;
        int choice;

        outer:
        while (true) {
            System.out.println("\n-------------------");
            System.out.println("BANK    OF     JAVA");
            System.out.println("-------------------\n");
            System.out.println("1. Register account.");
            System.out.println("2. Login.");
            System.out.println("3. Update accounts.");
            System.out.println("4. Exit.");
            System.out.print("\nEnter your choice : ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter name : ");
                    String name = sc.nextLine();
                    System.out.print("Enter address : ");
                    String address = sc.nextLine();
                    System.out.print("Enter contact number : ");
                    String phone = sc.nextLine();
                    System.out.println("Set username : ");
                    username = sc.next();
                    while (DatabaseHandler.usernameExists(username)) {
                        System.out.println("Username already exists. Set again : ");
                        username = sc.next();
                    }
                    System.out.println("Set a password (minimum 8 chars; minimum 1 digit, 1 lowercase, 1 uppercase, 1 special character[!@#$%^&*_]) :");
                    password = sc.next();
                    sc.nextLine();
                    while (!password.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_]).{8,}")) {
                        System.out.println("Invalid password condition. Set again :");
                        password = sc.next();
                    }
                    System.out.print("Enter initial deposit : ");
                    while (!sc.hasNextDouble()) {
                        System.out.println("Invalid amount. Enter again :");
                        sc.nextLine();
                    }
                    amount = sc.nextDouble();
                    sc.nextLine();
                    customer = new Customer(username, password, name, address, phone, amount, new Date());
                    DatabaseHandler.insertCustomer(customer);
                    break;

                case 2:
                    System.out.println("Enter username : ");
                    username = sc.next();
                    sc.nextLine();
                    System.out.println("Enter password : ");
                    password = sc.next();
                    sc.nextLine();
                    customer = DatabaseHandler.getCustomer(username, password);
                    if (customer != null && customer.password.equals(password)) {
                        while (true) {
                            System.out.println("\n-------------------");
                            System.out.println("W  E  L  C  O  M  E");
                            System.out.println("-------------------\n");
                            System.out.println("1. Deposit.");
                            System.out.println("2. Transfer.");
                            System.out.println("3. Last 5 transactions.");
                            System.out.println("4. User information.");
                            System.out.println("5. Account Details.");
                            System.out.println("6. Log out.");
                            System.out.print("\nEnter your choice : ");
                            choice = sc.nextInt();
                            sc.nextLine();
                            switch (choice) {
                                case 1:
                                    System.out.print("Enter amount : ");
                                    while (!sc.hasNextDouble()) {
                                        System.out.println("Invalid amount. Enter again :");
                                        sc.nextLine();
                                    }
                                    amount = sc.nextDouble();
                                    sc.nextLine();
                                    customer.deposit(amount, new Date());
                                    DatabaseHandler.updateBalance(customer.username, customer.balance);
                                    break;

                                case 2:
                                    System.out.print("Enter payee username : ");
                                    String payeeUsername = sc.next();
                                    sc.nextLine();
                                    System.out.println("Enter amount : ");
                                    while (!sc.hasNextDouble()) {
                                        System.out.println("Invalid amount. Enter again :");
                                        sc.nextLine();
                                    }
                                    amount = sc.nextDouble();
                                    sc.nextLine();
                                    if (amount > 300000) {
                                        System.out.println("Transfer limit exceeded. Contact bank manager.");
                                        break;
                                    }

                                    Customer payee = DatabaseHandler.getCustomer(payeeUsername, "dummy"); // password not checked
                                    if (payee != null) {
                                        customer.withdraw(amount, new Date());
                                        payee.deposit(amount, new Date());
                                        DatabaseHandler.updateBalance(customer.username, customer.balance);
                                        DatabaseHandler.updateBalance(payee.username, payee.balance);
                                    } else {
                                        System.out.println("Username doesn't exist.");
                                    }
                                    break;

                                case 3:
                                    for (String transaction : customer.transactions) {
                                        System.out.println(transaction);
                                    }
                                    break;

                                case 4:
                                    System.out.println("Accountholder name : " + customer.name);
                                    System.out.println("Accountholder address : " + customer.address);
                                    System.out.println("Accountholder contact : " + customer.phone);
                                    break;

                                case 5:
                                    AccountDetails.display(customer);
                                    break;

                                case 6:
                                    continue outer;

                                default:
                                    System.out.println("Wrong choice !");
                            }
                        }
                    } else {
                        System.out.println("Wrong username/password.");
                    }
                    break;

                case 3:
                    System.out.print("Enter username: ");
                    username = sc.next();
                    sc.nextLine();
                    customer = DatabaseHandler.getCustomer(username, "dummy"); // dummy password
                    if (customer != null) {
                        while (true) {
                            System.out.println("\n--- Update Menu ---");
                            System.out.println("1. Apply interest/charges");
                            System.out.println("2. Update name");
                            System.out.println("3. Update address");
                            System.out.println("4. Update contact number");
                            System.out.println("5. Change password");
                            System.out.println("6. Back to main menu");
                            System.out.print("Enter your choice: ");
                            int updateChoice = sc.nextInt();
                            sc.nextLine();
                            switch (updateChoice) {
                                case 1:
                                    customer.update(new Date());
                                    DatabaseHandler.updateBalance(customer.username, customer.balance);
                                    System.out.println("Account updated with interest/charges.");
                                    break;
                                case 2:
                                    System.out.print("Enter new name: ");
                                    String newName = sc.nextLine();
                                    customer.name = newName;
                                    DatabaseHandler.updateField(customer.username, "name", newName);
                                    System.out.println("Name updated successfully.");
                                    break;
                                case 3:
                                    System.out.print("Enter new address: ");
                                    String newAddress = sc.nextLine();
                                    customer.address = newAddress;
                                    DatabaseHandler.updateField(customer.username, "address", newAddress);
                                    System.out.println("Address updated successfully.");
                                    break;
                                case 4:
                                    System.out.print("Enter new phone number: ");
                                    String newPhone = sc.nextLine();
                                    customer.phone = newPhone;
                                    DatabaseHandler.updateField(customer.username, "phone", newPhone);
                                    System.out.println("Contact number updated successfully.");
                                    break;
                                case 5:
                                    System.out.print("Enter new password: ");
                                    String newPassword = sc.nextLine();
                                    while (!newPassword.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_]).{8,}")) {
                                        System.out.println("Invalid password. Try again:");
                                        newPassword = sc.nextLine();
                                    }
                                    customer.password = newPassword;
                                    DatabaseHandler.updateField(customer.username, "password", newPassword);
                                    System.out.println("Password updated successfully.");
                                    break;
                                case 6:
                                    break;
                                default:
                                    System.out.println("Invalid choice!");
                            }
                            if (updateChoice == 6) break;
                        }
                    } else {
                        System.out.println("Username doesn't exist.");
                    }
                    break;


                default:
                    System.out.println("Wrong choice !");
            }
        }
    }
}
