package bgu.spl.mics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    Future<Operation> future;

    @Before
    public void setUp() throws Exception{
        future = new Future<>();
    }


    @Test
    public void get() {
        //assume operation is not resolved
        Thread A=new Thread(new Runnable() {
            @Override
            public void run() {
                future.get();
            }
        });
        // A is waiting for operation to resolve
        A.run();
        Assert.assertEquals(A.getState(), Thread.State.WAITING);
        //resolve operation
        Operation resolved=new Operation();
        resolved.isDone=true;
        future.resolve(resolved);
        //now A is running
        Assert.assertEquals(A.getState(), Thread.State.RUNNABLE);//? why there is no running state
    }

    @Test
    public void resolve(){
            //assume operation is not resolved
            Operation ans=future.get(1000,TimeUnit.MILLISECONDS);
            Assert.assertEquals(ans,null);
            //resolve operation
            Operation resolved=new Operation();
            resolved.isDone=true;
            future.resolve(resolved);
            ans=future.get();
            Assert.assertEquals(ans.isDone,true);
    }

    @Test
    public void isDone() {
        //operation is not resolved
        Assert.assertEquals(false,future.isDone());
        //now we resolve the operation
        Operation resolved=new Operation();
        resolved.isDone=true;
        future.resolve(resolved);
        Assert.assertEquals(true,future.isDone());
    }

    @Test
    public void get1() {
        long timeout;
        TimeUnit unit;
        timeout=1000;
        unit=TimeUnit.MILLISECONDS;

        //given a unfinished operation
        Assert.assertEquals(future.get(timeout,unit),null);


        //given a finished operation;
        Thread A=new Thread(new Runnable() {
            @Override
            public void run() {
                Operation resolved=new Operation();
                resolved.isDone=true;
                future.resolve(resolved);
            }
        });
        Operation Ans=future.get(timeout,unit);
        //assume the resolved will happen within the time frame.
        A.run();
        Assert.assertEquals(Ans,future.get());
    }
}