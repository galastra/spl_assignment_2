package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
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
        //String jsonFilename = args[0];
        String jsonFilename = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/test.json";
        String output4Customers = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/customersFile";
        String output4Books = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/booksFile";
        String output4Receipts = "/home/gal/IdeaProjects/SPL_assignment_2/src/main/java/bgu/spl/mics/application/receiptsFile";
        String output4MoneyReg="";
        //String output4Customers = args[1];
        //String output4books = args[2];
        //String output4receipts = args[3];

        Gson gson = new Gson();

        Models model = new Models();
        try {
            FileReader reader = new FileReader(jsonFilename);
            model = gson.fromJson(reader
                    , Models.class);
        }catch (Exception e){System.out.println(e.getMessage());}

        Inventory.getInstance().load(model.getInitialInventory());
        ResourcesHolder.getInstance().load(model.getInitialResources());
        List<OrderSchedule> schedules = new ArrayList<>();
        HashMap<Integer,Customer> vanille_customers = new HashMap<>();
        for (Models.JSON_Services.JSONC_Customer customer : model.getJson_services().getCustomers()){
            Customer vanille_customer = new Customer(customer);
            vanille_customer.setCreditCard(customer.getCreditCard());
            vanille_customers.put(customer.getId(),vanille_customer);
            schedules.addAll(Arrays.asList(customer.getOrderSchedules()));

        }

        for(int i=1;i<=model.getJson_services().getSellingServicesCount();i++){
            new Thread(new SellingService(i)).start();
        }
        for (int i=1;i<=model.getJson_services().getInventroyServicesCount();i++){
            new Thread(new InventoryService(i,output4Books)).start();
        }
        for(int i=1;i<=model.getJson_services().getLogisticsServiceCount();i++){
           new Thread (new LogisticsService(i)).start();
        }
        for(int i=1;i<=model.getJson_services().getResourcesServicesCount();i++){
            new Thread(new ResourceService(i)).start();
        }
        for (int i=1;i<=model.getJson_services().getCustomers().length;i++){
            new Thread(new APIService(i,schedules,vanille_customers.get(i-1))).start();
        }

        new Thread(TimeService.getInstance(model.getJson_services().getTime().getSpeed(),
                model.getJson_services().getTime().getDuration())).start();
        new Printer<HashMap<Integer,Customer>>(output4Customers,vanille_customers).print();







    }
}
