package bgu.spl.mics.application.passiveObjects;

public class OrderSchedule {
    private String bookTitle;
    private int tick;
    private int orderId;

    public OrderSchedule(String _BookTitle,int _Tick,int _orderId){
        this.bookTitle=_BookTitle;
        this.tick=_Tick;
        this.orderId=_orderId;
    }

    public int getTick() {
        return tick;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int value){
        orderId = value;
    }
}
