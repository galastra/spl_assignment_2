package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {

        String jsonFilename = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/test.json";
        String output4Customers = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/customersFile";
        String output4Books = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/booksFile";
        String output4Receipts = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/receiptsFile";
        String output4MoneyReg="/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/MoneyRegFile";

        /*
        String jsonFilename = args[0];
        String output4Customers = args[1];
        String output4Books = args[2];
        String output4Receipts = args[3];
        String output4MoneyReg = args[4];
        */

        Gson gson = new Gson();
        Models model = new Models();
        List<Thread> threadList = new ArrayList<>();

        //load from JSON (using GSON)
        try {
            FileReader reader = new FileReader(jsonFilename);
            model = gson.fromJson(reader, Models.class);
        }catch (Exception e){System.out.println(e.getMessage());}
        CountDownLatch countDownLatch = new CountDownLatch(model.getJson_services().getInventroyServicesCount()+
                model.getJson_services().getCustomers().length+
                model.getJson_services().getLogisticsServiceCount()+
                model.getJson_services().getResourcesServicesCount()+
                model.getJson_services().getSellingServicesCount());
        //building resources
        Inventory.getInstance().load(model.getInitialInventory());
        ResourcesHolder.getInstance().load(model.getInitialResources());
        HashMap<Integer,Customer> customers2print = new HashMap<>();
        for (Models.JSON_Services.JSONC_Customer customer : model.getJson_services().getCustomers()) {
            Customer vanille_customer = new Customer(customer);
            vanille_customer.setCreditCard(customer.getCreditCard());
            customers2print.put(customer.getId(), vanille_customer);
        }
        {
            Thread thread = new Thread(TimeService.getInstance(model.getJson_services().getTime().getSpeed(),
                    model.getJson_services().getTime().getDuration(),threadList.size(),countDownLatch), "TimeService");
            threadList.add(thread);
            thread.start();
        }
        //making threads and adding them to the threadList
        for(int i=1;i<=model.getJson_services().getSellingServicesCount();i++){
            Thread thread = new Thread(new SellingService(i,countDownLatch),"SellingService"+i);
            threadList.add(thread);
            thread.start();
        }
        for (int i=1;i<=model.getJson_services().getInventroyServicesCount();i++){
            Thread thread = new Thread(new InventoryService(i,countDownLatch),"InventoryService"+i);
            threadList.add(thread);
            thread.start();
        }
        for(int i=1;i<=model.getJson_services().getLogisticsServiceCount();i++){
           Thread thread = new Thread (new LogisticsService(i,countDownLatch),"LogisticsService"+i);
           threadList.add(thread);
           thread.start();
        }
        for(int i=1;i<=model.getJson_services().getResourcesServicesCount();i++){
            Thread thread = new Thread(new ResourceService(i,countDownLatch),"ResourcesService"+i);
            threadList.add(thread);
            thread.start();
        }
        for (int i=1;i<=model.getJson_services().getCustomers().length;i++){
            Models.JSON_Services.JSONC_Customer super_customer  = model.getJson_services().getCustomers()[i-1];
            Customer vanille_customer = new Customer(super_customer);
            vanille_customer.setCreditCard(super_customer.getCreditCard());
            customers2print.replace(vanille_customer.getId(),vanille_customer);
            Thread thread = new Thread(new APIService(i,Arrays.asList(super_customer.getOrderSchedules()),vanille_customer,countDownLatch),"APIService"+i);
            threadList.add(thread);
            thread.start();
        }


        try {
            for(Thread thread : threadList)
                thread.join(); //waits for the thread to die
        }catch (Exception e){e.printStackTrace();}

        /*
            //*****Serialization*******
            //Receipts:
            MoneyRegister.getInstance().printOrderReceipts(output4Receipts);
            //Books:
            Inventory.getInstance().printInventoryToFile(output4Books);
            //customers:
            new Printer<HashMap<Integer,Customer>>(output4Customers,customers2print).print();
            //MoneyRegister:
            new Printer<MoneyRegister>(output4MoneyReg,MoneyRegister.getInstance()).print();
        */

        //Zilber's:

        //int numOfTest = Integer.parseInt(args[0].replace(new File(args[0]).getParent(), "").replace("/", "").replace(".json", ""));
        //int numOfTest = Integer.parseInt(jsonFilename.replace(new File(jsonFilename).getParent(), "").replace("/", "").replace(".json", ""));
        int numOfTest = 6;
        //String dir = new File(args[1]).getParent() + "/" + numOfTest + " - ";
        String dir = new File(output4Customers).getParent() + "/" + numOfTest + " - ";
        Customer[] customers1 = customers2print.values().toArray(new Customer[0]);
        Arrays.sort(customers1, Comparator.comparing(Customer::getName));
        //String str_custs = Arrays.toString(customers1);
        String str_custs = customers2string(customers1);
        str_custs = str_custs.replaceAll(", ", "\n---------------------------\n").replace("[", "").replace("]", "");
        Print(str_custs, dir + "Customers");

        String str_books = books2string(model.getInitialInventory());
        str_books = str_books.replaceAll(", ", "\n---------------------------\n").replace("[", "").replace("]", "");
        Print(str_books, dir + "Books");

        List<OrderReceipt> receipts_lst = MoneyRegister.getInstance().getOrderReceipts();
        receipts_lst.sort(Comparator.comparing(OrderReceipt::getOrderId));
        receipts_lst.sort(Comparator.comparing(OrderReceipt::getOrderTick));
        OrderReceipt[] receipts = receipts_lst.toArray(new OrderReceipt[0]);
        String str_receipts = receipts2string(receipts);
        str_receipts = str_receipts.replaceAll(", ", "\n---------------------------\n").replace("[", "").replace("]", "");
        Print(str_receipts, dir + "Receipts");

        Print(MoneyRegister.getInstance().getTotalEarnings() + "", dir + "Total");


        /*
        //Deserialization:
        HashMap<Integer,Customer> customerHashMapDeSe = null;
        HashMap<String,Integer> booksDeSe = null;
        List<OrderReceipt> orderReceiptsDeSe = null;
        MoneyRegister moneyRegisterDeSe = null;
        try
        {
            //cusotmers:
            FileInputStream file = new FileInputStream(output4Customers);
            ObjectInputStream in = new ObjectInputStream(file);
            customerHashMapDeSe = (HashMap<Integer, Customer>) in.readObject();
            in.close();
            file.close();

            //books:
            file = new FileInputStream(output4Books);
            in = new ObjectInputStream(file);
            booksDeSe = (HashMap<String, Integer>) in.readObject();
            in.close();
            file.close();

            //receipts:
            file = new FileInputStream(output4Receipts);
            in = new ObjectInputStream(file);
            orderReceiptsDeSe = (List<OrderReceipt>) in.readObject();
            in.close();
            file.close();

            // moneyRegister:
            file = new FileInputStream(output4MoneyReg);
            in = new ObjectInputStream(file);
            moneyRegisterDeSe = (MoneyRegister) in.readObject();
            in.close();
            file.close();


        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
        */
    }


    //Zilber's special addition:

    public static String customers2string(Customer[] customers) {
        String str = "";
        for (Customer customer : customers)
            str += customer2string(customer) + "\n---------------------------\n";
        return str;
    }

    public static String customer2string(Customer customer) {
        String str = "id    : " + customer.getId() + "\n";
        str += "name  : " + customer.getName() + "\n";
        str += "addr  : " + customer.getAddress() + "\n";
        str += "dist  : " + customer.getDistance() + "\n";
        str += "card  : " + customer.getCreditNumber() + "\n";
        str += "money : " + customer.getAvailableCreditAmount();
        return str;
    }

    public static String books2string(BookInventoryInfo[] books) {
        String str = "";
        for (BookInventoryInfo book : books)
            str += book2string(book) + "\n---------------------------\n";
        return str;
    }

    public static String book2string(BookInventoryInfo book) {
        String str = "";
        str += "title  : " + book.getBookTitle() + "\n";
        str += "amount : " + book.getAmountInInventory() + "\n";
        str += "price  : " + book.getPrice();
        //str+="book title : "+book.getBookTitle();
        return str;
    }


    public static String receipts2string(OrderReceipt[] receipts) {
        String str = "";
        for (OrderReceipt receipt : receipts)
            str += receipt2string(receipt) + "\n---------------------------\n";
        return str;
    }
    public static String receipt2string(OrderReceipt receipt) {
        String str = "";
        str += "customer   : " + receipt.getCustomerId() + "\n";
        str += "order tick : " + receipt.getOrderTick() + "\n";
        str += "id         : " + receipt.getOrderId() + "\n";
        str += "price      : " + receipt.getPrice() + "\n";
        str += "seller     : " + receipt.getSeller();
        return str;
    }

    public static void Print(String str, String filename) {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
                out.print(str);
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName());
        }
    }




}
