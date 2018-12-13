package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailableEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link //MoneyRegister} singleton of the store.
 * Handles {@link //BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
    private MoneyRegister moneyRegister;
    private int curr_tick;

    public SellingService() {
        super("Selling Service");
        moneyRegister = MoneyRegister.getInstance();
    }

    @Override
    protected void initialize() {
        System.out.println("Selling Service "+getName()+" started");
        curr_tick = 1;

        subscribeBroadcast(TickBroadcast.class,broad->{
            curr_tick = broad.getCurr_tick();
        });

        subscribeEvent(BookOrderEvent.class, ev-> {
            Future<Integer> futureBookPrice = (Future<Integer>) sendEvent(
                    new CheckAvailableEvent(ev.getBookTitle()));
            if (futureBookPrice == null)
                System.out.println("No Micro-Service has registered to handle CheckAvailableEvent events! The event cannot be processed");
            else {
                Integer priceResult = futureBookPrice.get(); //get the price from the InventoryService

                if (priceResult==-1 || ev.getCustomer().getAvailableCreditAmount()<priceResult){
                    System.out.println("not enough money or the book does not exist");
                    complete(ev,null);
                }
                else{
                    //Building the Receipt
                    int orderid = ev.getOrderId();
                    String seller = "Store"; //TODO: figure out what should be here
                    int customer = ev.getCustomer().getId();
                    String bookTitle = ev.getBookTitle();
                    int price = priceResult;
                    int issuedTick = 0; //TODO: what is this
                    int orderTick = ev.getTick();
                    int processTick = curr_tick; //TODO: what is this
                    OrderReceipt receipt = new OrderReceipt(orderid,seller,customer,bookTitle,price,issuedTick,orderTick,processTick);
                    //

                    Future<OrderResult> futureOrderResult = (Future<OrderResult>)sendEvent(new TakeBookEvent(bookTitle));
                    if (futureOrderResult == null){
                        System.out.println("No Micro-Service has registered to handle TakeBookEvent events! The event cannot be processed");
                    }
                    else{
                        OrderResult orderResult = futureOrderResult.get();

                        if (orderResult == OrderResult.SUCCESSFULLY_TAKEN){
                            moneyRegister.chargeCreditCard(ev.getCustomer(),priceResult);
                            complete(ev,receipt);
                        }
                        else {
                            complete(ev, null);
                            System.out.println("SellingService tried to get the book but in the meanwhile it has been taken by someone else");
                        }
                    }

                }
            }

        });


    }

}

