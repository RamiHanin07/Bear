import org.apache.poi.xssf.usermodel.*;
import org.json.simple.*;
import java.io.*;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        port(4567);
        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                        response.header("Access-Control-Allow-Headers",
                                "content-type");

                        response.header("Access-Control-Allow-Methods",
                                "GET, POST");


                    return "OK";
                });

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));


        get("/low-stock", (request, response) -> {
            String name;
            double id;
            double stock;
            double capacity;
            int rows;
            String path = ".//resources/Inventory.xlsx";
            File file = new File(path);
            FileInputStream inputstream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            JSONArray array = new JSONArray();
            rows  = sheet.getLastRowNum();

            for(int i = 1; i < rows; i++){
                XSSFRow row = sheet.getRow(i);
                stock = row.getCell(1).getNumericCellValue();
                capacity = row.getCell(2).getNumericCellValue();

                if(stock / capacity <= .25){
                    //Items to be displayed:
                    name = row.getCell(0).toString();
                    id = row.getCell(3).getNumericCellValue();
                    JSONObject object = new JSONObject();
                    object.put("name", name);
                    object.put("id", id);
                    object.put("stock", stock);
                    object.put("capacity", capacity);
                    array.add(object);
                }
            }

            JSONObject finalObject = new JSONObject();
            finalObject.put("array", array);


            return finalObject;
        });

        post("/restock-cost", (request, response) -> {
            float totalCost = 0;
            int rows;
            float cost;
            int numSheets;
            JSONObject finalCost = new JSONObject();
            String path = ".//resources/Distributors.xlsx";
            File file = new File(path);
            FileInputStream inputstream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
            XSSFSheet sheet;

            //Generate a hashmap containing every product and the lowest possible price.
            //Using hashmap for speed when searching for products to purchase.
            HashMap<String, Float> Products = new HashMap<String, Float>();
            numSheets = workbook.getNumberOfSheets();
            for(int i = 0; i < numSheets; i++){
                sheet = workbook.getSheetAt(i);
                rows = sheet.getLastRowNum();
                //Iterate through every row in the sheet, skipping the top row (naming row)
                for(int j = 1; j < rows - 1; j++){
                    XSSFRow row = sheet.getRow(j);
                    String productName = row.getCell(0).toString();
                    cost = (float) row.getCell(2).getNumericCellValue();
                    if(!Products.containsKey(productName)) {
                        Products.put(productName, cost);
                    }
                    else{
                        //Getting cheapest cost from all distributors for calculation at end.
                        if(Products.get(productName) > cost){
                            Products.put(productName, cost);
                        }
                    }
                }
            }

            //Final calculations now that lowest cost for each item has been found and stored in hashmap.
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Integer> requestedProducts = mapper.readValue(request.body(), HashMap.class);
            for(String key : requestedProducts.keySet()){
                try {
                    totalCost += Products.get(key) * (requestedProducts.get(key));
                }catch(Exception e){
                    System.out.println("Item: " + key + " is not requesting an integer amount of items");
                }
            }
            finalCost.put("cost", totalCost);


            return finalCost;
        });

    }
}
