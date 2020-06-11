import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CalculatorTest {

    @Parameterized.Parameter(0)
    public String expression;
    @Parameterized.Parameter(1)
    public int result;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{{ "1+2", 3 },
                { "2*2", 4 }, {"5*5", 25}};
        return Arrays.asList(data);
    }

    @Test
    public void testCalculate() throws Exception {
        Assert.assertEquals(result, Calculator.calculate(expression));
    }
}
