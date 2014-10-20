package org.rembx.jeeshop.configuration;

import javax.enterprise.inject.Produces;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: bantos
 * Date: 29/11/13
 * Time: 21:04
 */
public class TestProducer {
    
    public final static Calendar calendar = new GregorianCalendar(2013, 0, 9, 18, 1, 0);
    
    @Produces
    private Calendar calendar(){
        return calendar;
    }
}
