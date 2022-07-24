import org.apache.poi.xssf.usermodel.*;
import org.json.simple.*;

import java.io.*;
import java.io.IOException;
import java.util.HashMap;

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
            System.out.println("rows: " + rows);

            for(int i = 1; i < rows; i++){
                XSSFRow row = sheet.getRow(i);
                stock = row.getCell(1).getNumericCellValue();
                capacity = row.getCell(2).getNumericCellValue();

                System.out.println("row: " + i);
                System.out.println("name: " + row.getCell(0).toString());
                System.out.println("stock: " + stock);
                System.out.println("capacity: " + capacity);
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
            //Testing json in react
            JSONObject testObject = new JSONObject();
            testObject.put("name", "Snickers");
            testObject.put("array", array);

            //return the new JSONArray;
            return testObject;
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            String path = ".//resources/Distributors.xlsx";
            File file = new File(path);
            FileInputStream inputstream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
            XSSFSheet sheet;
            float totalCost = 0;

            HashMap<String, Float> Products = new HashMap<String, Float>();
            int numSheets = workbook.getNumberOfSheets();
            System.out.println(numSheets);
            //Generate a hashmap containing every product and the lowest possible price.
            for(int i = 0; i < numSheets; i++){
                System.out.println("New round: " + i);
                sheet = workbook.getSheetAt(i);
                int rows = sheet.getLastRowNum();
                System.out.println("total rows:" + rows);
                System.out.println("minus 1 + " + (rows - 1));
                //Iterate through every row in the sheet, skipping the top row (naming row)
                for(int j = 1; j < rows - 1; j++){
                    XSSFRow row = sheet.getRow(j);
                    System.out.println("Row: " + j);
                    String productName = row.getCell(0).toString();
                    System.out.println(productName);
                    float cost = (float) row.getCell(2).getNumericCellValue();
                    System.out.println(cost);
                    //Product is brand new
                    if(!Products.containsKey(productName)){
                        System.out.println("Adding: " + productName);
                        Products.put(productName, cost);
                    }//Product already exists
                    else{
                        //If there is a cheaper cost item
                        if(Products.get(productName) > cost){
                            System.out.println("Updating: " + productName);
                            System.out.println("Cost before:" + Products.get(productName));
                            System.out.println("Cost now: " + cost);
                            Products.put(productName, cost);
                        }
                    }
                }
            }

            //Do calculation on total cost;
            JSONObject testObject = new JSONObject();
            testObject.put("name", "Snickers");
            testObject.put("purchase", 5);
            System.out.println("purchase:"  + testObject.get("purchase"));
            totalCost += Products.get(testObject.get("name")) * (int) testObject.get("purchase");
            return totalCost;
        });

    }
}
