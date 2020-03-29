package ru.progwards.java2.lessons.tests.test.calc;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.progwards.java2.lessons.tests.calc.SimpleCalculator;

public class CalculatorTest {

    @Test
    public void sumTest(){
        SimpleCalculator calculator =  Mockito.mock(SimpleCalculator.class);
        Mockito.when(calculator.sum(5,10)).thenReturn(15);
        int actual = calculator.sum(5,10);
        int expected = 15;

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void multTest(){
        SimpleCalculator calculator =  Mockito.mock(SimpleCalculator.class);
        Mockito.when(calculator.mult(5,10)).thenReturn(50);
        int actual = calculator.mult(5,10);
        int expected = 50;

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void divTest(){
        SimpleCalculator calculator =  Mockito.mock(SimpleCalculator.class);
        Mockito.when(calculator.div(5,10)).thenReturn(0);
        int actual = calculator.div(5,10);
        int expected = 0;

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void diffTest(){
        SimpleCalculator calculator =  Mockito.mock(SimpleCalculator.class);
        Mockito.when(calculator.diff(5,10)).thenReturn(-5);
        int actual = calculator.diff(5,10);
        int expected = -5;

        Assert.assertEquals(expected, actual);
    }
}
