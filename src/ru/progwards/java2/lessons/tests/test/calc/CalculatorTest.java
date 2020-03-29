package ru.progwards.java2.lessons.tests.test.calc;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.progwards.java2.lessons.tests.calc.SimpleCalculator;

public class CalculatorTest {

    @Test
    public void sumTest(){
        SimpleCalculator calculator =  Mockito.mock(SimpleCalculator.class);
        Mockito.when(calculator.sum(5,10)).thenReturn(100);
        int actual = calculator.sum(5,10);
        int expected = 100;

        Assert.assertEquals(expected, actual);
    }

}
