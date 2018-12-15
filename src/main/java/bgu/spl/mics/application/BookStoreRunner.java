package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        String jsonFilename = args[0];
        //String jsonFilename = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/test.json";
        //String output4Customers = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/customersFile";
        //String output4Books = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/booksFile";
        //String output4Receipts = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/receiptsFile";
        //String output4MoneyReg="/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/MoneyRegFile";
        String output4Customers = args[1];
        String output4Books = args[2];
        String output4Receipts = args[3];
        String output4MoneyReg = args[4];

        Gson gson = new Gson();
        Models model = new Models();
        List<Thread> threadList = new ArrayList<>();
        //load from JSON (using GSON)
        try {
            FileReader reader = new FileReader(jsonFilename);
            model = gson.fromJson(reader
                    , Models.class);
        }catch (Exception e){System.out.println(e.getMessage());}

        //building resources
        Inventory.getInstance().load(model.getInitialInventory());
        ResourcesHolder.getInstance().load(model.getInitialResources());
        HashMap<Integer,Customer> customers2print = new HashMap<>();
        for (Models.JSON_Services.JSONC_Customer customer : model.getJson_services().getCustomers()) {
            Customer vanille_customer = new Customer(customer);
            vanille_customer.setCreditCard(customer.getCreditCard());
            customers2print.put(customer.getId(), vanille_customer);
        }
        //making threads and adding them to the threadList
        for(int i=1;i<=model.getJson_services().getSellingServicesCount();i++){
            Thread thread = new Thread(new SellingService(i,output4MoneyReg),"SellingService"+i);
            threadList.add(thread);
            thread.start();
        }
        for (int i=1;i<=model.getJson_services().getInventroyServicesCount();i++){
            Thread thread = new Thread(new InventoryService(i,output4Books),"InventoryService"+i);
            threadList.add(thread);
            thread.start();
        }
        for(int i=1;i<=model.getJson_services().getLogisticsServiceCount();i++){
           Thread thread = new Thread (new LogisticsService(i),"LogisticsService"+i);
           threadList.add(thread);
           thread.start();
        }
        for(int i=1;i<=model.getJson_services().getResourcesServicesCount();i++){
            Thread thread = new Thread(new ResourceService(i),"ResourcesService"+i);
            threadList.add(thread);
            thread.start();
        }
        for (int i=1;i<=model.getJson_services().getCustomers().length;i++){
            Models.JSON_Services.JSONC_Customer super_customer  = model.getJson_services().getCustomers()[i-1];
            Customer vanille_customer = new Customer(super_customer);
            vanille_customer.setCreditCard(super_customer.getCreditCard());
            customers2print.replace(vanille_customer.getId(),vanille_customer);
            Thread thread = new Thread(new APIService(i,Arrays.asList(super_customer.getOrderSchedules()),vanille_customer),"APIService"+i);
            threadList.add(thread);
            thread.start();
        }
        {
            Thread thread = new Thread(TimeService.getInstance(model.getJson_services().getTime().getSpeed(),
                    model.getJson_services().getTime().getDuration()), "TimeService");
            threadList.add(thread);
            thread.run();
        }

        try {
            for(Thread thread : threadList)
                thread.join(); //waits for the thread to die
        }catch (Exception e){e.printStackTrace();}

        //*****printing to file*******
        //Receipts:
        MoneyRegister.getInstance().printOrderReceipts(output4Receipts);
        //Books:
        Inventory.getInstance().printInventoryToFile(output4Books);
        //customers:
        new Printer<HashMap<Integer,Customer>>(output4Customers,customers2print).print();
        //MoneyRegister:
        new Printer<MoneyRegister>(output4MoneyReg,MoneyRegister.getInstance()).print();




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
}
