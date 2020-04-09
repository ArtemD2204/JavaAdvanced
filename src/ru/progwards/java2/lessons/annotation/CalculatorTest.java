package ru.progwards.java2.lessons.annotation;

import org.junit.Assert;
import org.mockito.Mockito;
import ru.progwards.java2.lessons.tests.calc.SimpleCalculator;

public class CalculatorTest {

    private SimpleCalculator calculator;

    @Before
    public void init() {
        calculator =  Mockito.mock(SimpleCalculator.class);
        System.out.println("Before invoked");
    }

    @Test(priority = 1)
    public void sumTest(){
        Mockito.when(calculator.sum(5,10)).thenReturn(15);
        int actual = calculator.sum(5,10);
        int expected = 15;

        Assert.assertEquals(expected, actual);
        System.out.println("Test 1 invoked");
    }

    @Test(priority = 2)
    public void multTest(){
        Mockito.when(calculator.mult(5,10)).thenReturn(50);
        int actual = calculator.mult(5,10);
        int expected = 50;

        Assert.assertEquals(expected, actual);
        System.out.println("Test 2 invoked");
    }

    @Test(priority = 3)
    public void divTest(){
        Mockito.when(calculator.div(5,10)).thenReturn(0);
        int actual = calculator.div(5,10);
        int expected = 0;

        Assert.assertEquals(expected, actual);
        System.out.println("Test 3 invoked");
    }

    @Test(priority = 4)
    public void diffTest(){
        Mockito.when(calculator.diff(5,10)).thenReturn(-5);
        int actual = calculator.diff(5,10);
        int expected = -5;

        Assert.assertEquals(expected, actual);
        System.out.println("Test 4 invoked");
    }

    @After
    void afterMethod() {
        calculator = null;
        System.out.println("After invoked");
    }

    public static void main(String[] args) throws Exception {
        JTest jTest = new JTest();
        jTest.run("ru.progwards.java2.lessons.annotation.CalculatorTest");
    }
}
