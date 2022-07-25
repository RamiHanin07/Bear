import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

public class RestockCostTest {
    @Test
    public void getRestockCost(){
        Main test = new Main();
        HashMap<String, Integer> requestedProducts = new HashMap<String, Integer>();
        requestedProducts.put("Good & Plenty", 2);
        requestedProducts.put("Twix", 3);
        requestedProducts.put("Starburst", 4);
        requestedProducts.put("Butterfinger", 5);

        HashMap<String, Float> Products = new HashMap<String, Float>();
        Products.put("Good & Plenty", (float) 0.18);
        Products.put("Twix", (float) 0.54);
        Products.put("Starburst", (float) 0.07);
        Products.put("Butterfinger", (float) 0.93);
        assertEquals( (float) 6.91, test.returnCost(requestedProducts, Products), 0.0001);
    }
}
