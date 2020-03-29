package ru.progwards.java2.lessons.tests.test.calculator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.progwards.java2.lessons.calculator.Calculator2;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class Calculator2Test {

    @Parameterized.Parameter(0)
    public String expression;
    @Parameterized.Parameter(1)
    public int result;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{{ "1+2", 3 },
                { "2*2", 4 }, {"5*5", 101}};
        return Arrays.asList(data);
    }

    @Test
    public void testCalculate() throws Exception {
//        int actual = Calculator2.calculate("1+1");
//        int expected = 2;

        Assert.assertEquals(result, Calculator2.calculate(expression));
    }
}
