package bgu.spl.mics.application.passiveObjects;

public class OrderSchedule {
    private String BookTitle;
    private int Tick;
    private int OrderId;

    public OrderSchedule(String _BookTitle,int _Tick,int _orderId){
        this.BookTitle=_BookTitle;
        this.Tick=_Tick;
        this.OrderId=_orderId;
    }

    public int getTick() {
        return Tick;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public int getOrderId() {
        return OrderId;
    }
}
