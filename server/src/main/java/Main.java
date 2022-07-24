import org.apache.poi.xssf.usermodel.*;
import org.json.simple.*;

import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws IOException {
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

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            String path = ".//resources/Inventory.xlsx";
            File file = new File(path);
            FileInputStream inputstream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            JSONArray array = new JSONArray();
            String name;
            double id;
            double stock;
            double capacity;
            int rows  = sheet.getLastRowNum();

            for(int i = 1; i < rows; i++){
                XSSFRow row = sheet.getRow(i);
                stock = row.getCell(1).getNumericCellValue();
                capacity = row.getCell(2).getNumericCellValue();

                if(stock / capacity <= .25){
                    //This needs to be displayed:
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

            //return the new JSONArray;
            return finalObject;
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            JSONObject finalCost = new JSONObject();
            String path = ".//resources/Distributors.xlsx";
            File file = new File(path);
            FileInputStream inputstream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
            XSSFSheet sheet;
            float totalCost = 0;

            HashMap<String, Float> Products = new HashMap<String, Float>();
            int numSheets = workbook.getNumberOfSheets();
            //Generate a hashmap containing every product and the lowest possible price.
            for(int i = 0; i < numSheets; i++){
                sheet = workbook.getSheetAt(i);
                int rows = sheet.getLastRowNum();
                //Iterate through every row in the sheet, skipping the top row (naming row)
                for(int j = 1; j < rows - 1; j++){
                    XSSFRow row = sheet.getRow(j);
                    String productName = row.getCell(0).toString();
                    float cost = (float) row.getCell(2).getNumericCellValue();
                    //Product is brand new
                    if(!Products.containsKey(productName)){
                        Products.put(productName, cost);
                    }//Product already exists
                    else{
                        //If there is a cheaper cost item
                        if(Products.get(productName) > cost){
                            Products.put(productName, cost);
                        }
                    }
                }
            }

            //Do calculation on total cost;
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(request.body());
            HashMap<String, Integer> requestedProducts = mapper.readValue(request.body(), HashMap.class);
            System.out.println(requestedProducts);
            System.out.println("test");
            for(String key : requestedProducts.keySet()){
                System.out.println("key: " + key);
                System.out.println("Products.get(key)" + Products.get(key));
                System.out.println("requestedProducts.get(key):" + requestedProducts.get(key));
                totalCost += Products.get(key) * (requestedProducts.get(key));
            }
            System.out.println("totalCost: " + totalCost);
            finalCost.put("cost", totalCost);
            return finalCost;
        });

    }
}
