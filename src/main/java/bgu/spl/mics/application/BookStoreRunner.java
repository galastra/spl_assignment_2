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
        String jsonFilename = args[0];
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
            model = gson.fromJson(reader, Models.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        CountDownLatch countDownLatch = new CountDownLatch(model.getJson_services().getInventroyServicesCount() +
                model.getJson_services().getCustomers().length +
                model.getJson_services().getLogisticsServiceCount() +
                model.getJson_services().getResourcesServicesCount() +
                model.getJson_services().getSellingServicesCount());
        //building resources
        Inventory.getInstance().load(model.getInitialInventory());
        ResourcesHolder.getInstance().load(model.getInitialResources());
        HashMap<Integer, Customer> customers2print = new HashMap<>();
        for (Models.JSON_Services.JSONC_Customer customer : model.getJson_services().getCustomers()) {
            Customer vanille_customer = new Customer(customer);
            vanille_customer.setCreditCard(customer.getCreditCard());
            customers2print.put(customer.getId(), vanille_customer);
        }
        {
            Thread thread = new Thread(TimeService.getInstance(model.getJson_services().getTime().getSpeed(),
                    model.getJson_services().getTime().getDuration(), threadList.size(), countDownLatch), "TimeService");
            threadList.add(thread);
            thread.start();
        }
        //making threads and adding them to the threadList
        for (int i = 1; i <= model.getJson_services().getSellingServicesCount(); i++) {
            Thread thread = new Thread(new SellingService(i, countDownLatch), "SellingService" + i);
            threadList.add(thread);
            thread.start();
        }
        for (int i = 1; i <= model.getJson_services().getInventroyServicesCount(); i++) {
            Thread thread = new Thread(new InventoryService(i, countDownLatch), "InventoryService" + i);
            threadList.add(thread);
            thread.start();
        }
        for (int i = 1; i <= model.getJson_services().getLogisticsServiceCount(); i++) {
            Thread thread = new Thread(new LogisticsService(i, countDownLatch), "LogisticsService" + i);
            threadList.add(thread);
            thread.start();
        }
        for (int i = 1; i <= model.getJson_services().getResourcesServicesCount(); i++) {
            Thread thread = new Thread(new ResourceService(i, countDownLatch), "ResourcesService" + i);
            threadList.add(thread);
            thread.start();
        }
        for (int i = 1; i <= model.getJson_services().getCustomers().length; i++) {
            Models.JSON_Services.JSONC_Customer super_customer = model.getJson_services().getCustomers()[i - 1];
            Customer vanille_customer = new Customer(super_customer);
            vanille_customer.setCreditCard(super_customer.getCreditCard());
            customers2print.replace(vanille_customer.getId(), vanille_customer);
            Thread thread = new Thread(new APIService(i, Arrays.asList(super_customer.getOrderSchedules()), vanille_customer, countDownLatch), "APIService" + i);
            threadList.add(thread);
            thread.start();
        }

        try {
            for (Thread thread : threadList)
                thread.join(); //waits for the thread to die
        } catch (Exception e) {
            e.printStackTrace();
        }

        //*****Serialization*******
        //Receipts:
        MoneyRegister.getInstance().printOrderReceipts(output4Receipts);
        //Books:
        Inventory.getInstance().printInventoryToFile(output4Books);
        //customers:
        new Printer<HashMap<Integer, Customer>>(output4Customers, customers2print).print();
        //MoneyRegister:
        new Printer<MoneyRegister>(output4MoneyReg, MoneyRegister.getInstance()).print();
    }


}
