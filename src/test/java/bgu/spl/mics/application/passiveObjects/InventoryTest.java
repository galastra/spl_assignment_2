package bgu.spl.mics.application.passiveObjects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    Inventory inventory;
    @Before
    public void setUp(){
        BookInventoryInfo [] A=new BookInventoryInfo[2];
        A[0]=new BookInventoryInfo("Moby Dick",1,20);
        A[1]=new BookInventoryInfo("Economics 101",1,50);
        inventory=new Inventory();
        inventory.load(A);
    }

    @Test
    public void take() {
        Assert.assertEquals(inventory.take("Tarzan"),OrderResult.NOT_IN_STOCK);
        Assert.assertEquals(inventory.take("Moby Dick"),OrderResult.SUCCESSFULLY_TAKEN);
        Assert.assertEquals(inventory.take("Moby Dick"),OrderResult.NOT_IN_STOCK);
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        Assert.assertEquals(inventory.checkAvailabiltyAndGetPrice("Tarzan"),-1);
        Assert.assertEquals(inventory.checkAvailabiltyAndGetPrice("Economics 101"),50);
    }

    @Test
    public void getInstance(){
        Inventory temp=Inventory.getInstance();
        Assert.assertEquals(temp,inventory);
    }
}