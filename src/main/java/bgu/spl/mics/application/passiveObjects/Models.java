package bgu.spl.mics.application.passiveObjects;

import com.google.gson.annotations.SerializedName;

/**
 * This is a class I made for Gson
 * It's a complex class that from it we transfer the data
 * as requested.
 * *This might not be the most optimal solution but it's our solution
 */

public class Models{

    //@SerializedName("initialInventory")
    private BookInventoryInfo[] initialInventory;
    //@SerializedName("initialResources")
    private InitialResources[] initialResources;
    @SerializedName("services")
    private JSON_Services json_services;

    public Models(){}

    public BookInventoryInfo[] getInitialInventory() {
        return initialInventory;
    }

    public DeliveryVehicle[] getInitialResources() {
        return initialResources[0].getVehicles();
    }

    public JSON_Services getJson_services() {
        return json_services;
    }

    public class InitialResources{
        @SerializedName("vehicles")
        private DeliveryVehicle[] vehicles;

        public DeliveryVehicle[] getVehicles() {
            return vehicles;
        }
    }

    public class JSON_Services{
        public class JSON_Time{
            @SerializedName("speed")
            private int speed;
            @SerializedName("duration")
            private int duration;

            public int getDuration() {
                return duration;
            }

            public int getSpeed() {
                return speed;
            }
        }

        public class CreditCard{
            private int number;
            private int amount;

            public int getAmount() {
                return amount;
            }

            public int getNumber() {
                return number;
            }
        }

        public class JSONC_Customer extends Customer{
            private OrderSchedule[] orderSchedule;
            private CreditCard creditCard;


            public OrderSchedule[] getOrderSchedules(){
                return orderSchedule;
            }

            public CreditCard getCreditCard() {
                return creditCard;
            }
        }

        @SerializedName("time")
        private JSON_Time time;
        @SerializedName("selling")
        private int SellingServicesCount;
        @SerializedName("inventoryService")
        private int InventroyServicesCount;
        @SerializedName("logistics")
        private int LogisticsServiceCount;
        @SerializedName("resourcesService")
        private int ResourcesServicesCount;
        @SerializedName("customers")
        private JSONC_Customer[] customers;

        public int getInventroyServicesCount() {
            return InventroyServicesCount;
        }

        public int getLogisticsServiceCount() {
            return LogisticsServiceCount;
        }

        public int getResourcesServicesCount() {
            return ResourcesServicesCount;
        }

        public int getSellingServicesCount() {
            return SellingServicesCount;
        }

        public JSONC_Customer[] getCustomers() {
            return customers;
        }

        public JSON_Time getTime() {
            return time;
        }
    }
}
