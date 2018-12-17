package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private int durationInMillis;
    private CountDownLatch countDownLatch;

    public SellingService(int id,CountDownLatch countDownLatch) {
        super("Selling Service "+id);
        moneyRegister = MoneyRegister.getInstance();
        durationInMillis = 0;
        this.countDownLatch = countDownLatch;
    }

    private int getCurr_tick(){return curr_tick;}

    @Override
    protected void initialize() {
        curr_tick = 0;

        subscribeBroadcast(TickBroadcast.class,broad->{
            curr_tick = broad.getCurr_tick();
            durationInMillis = broad.getDuration();
        });

        subscribeBroadcast(LastTickBroadcast.class,brod->{
            terminate();
        });

        subscribeEvent(BookOrderEvent.class, ev-> {
            int processTick = curr_tick;
            Future<Integer> futureBookPrice = (Future<Integer>) sendEvent(
                    new CheckAvailableEvent(ev.getBookTitle()));
            if (futureBookPrice != null) {
                Integer priceResult = futureBookPrice.get(durationInMillis, TimeUnit.MILLISECONDS); //get the price from the InventoryService

                if (priceResult==null || priceResult==-1 || ev.getCustomer().getAvailableCreditAmount()<priceResult){
                    complete(ev,null);
                }
                else{
                    //Building the Receipt
                    int orderid = ev.getOrderId();
                    String seller = getName();
                    int customer = ev.getCustomer().getId();
                    String bookTitle = ev.getBookTitle();
                    int price = priceResult;
                    //int issuedTick will be resolved in the future
                    int orderTick = ev.getTick();
                    //int processTick has been resolved
                    //

                    synchronized (ev.getCustomer()) {//billing and taking a book should be in synch
                        Future<OrderResult> futureOrderResult = (Future<OrderResult>) sendEvent(new TakeBookEvent(bookTitle));
                        if (futureOrderResult != null) {
                            OrderResult orderResult = futureOrderResult.get(durationInMillis, TimeUnit.MILLISECONDS);

                            if (ev.getCustomer().getAvailableCreditAmount() >= priceResult && orderResult == OrderResult.SUCCESSFULLY_TAKEN) {
                                moneyRegister.chargeCreditCard(ev.getCustomer(), priceResult);
                                int issuedTick = getCurr_tick();
                                OrderReceipt receipt = new OrderReceipt(orderid, seller, customer, bookTitle, price, issuedTick, orderTick, processTick);
                                complete(ev, receipt);
                                moneyRegister.file(receipt);
                                sendEvent(new DeliveryEvent(ev.getCustomer().getDistance(), ev.getCustomer().getAddress()));
                            } else {
                                complete(ev, null);
                            }
                        }
                    }
                }
            }

        });
        countDownLatch.countDown();
     }

}

