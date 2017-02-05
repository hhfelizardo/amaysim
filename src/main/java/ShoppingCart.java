package main.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;

import main.java.interfaces.Cart;
import main.java.values.Product;

public class ShoppingCart implements Cart{

	HashMap<String, Integer> itemsMap = new HashMap<>();
	HashMap<String, Product> productsMap = new HashMap<>();
	Properties prop;
	boolean withPromo = false;
	
	public ShoppingCart(String newPricingRules) throws Exception {
		// load products from products.csv
		loadProducts();
		getPropValues(newPricingRules);
	}
	
	public static void main(String[] args) throws Exception {

		String newPricingRules = "pricing_rules.properties";
		ShoppingCart cart = new ShoppingCart(newPricingRules);
		
		// Scenario#1
		String[] dataArr = cart.prop.get("SCENE1").toString().split(",");
		System.out.println("Scenario#1");
		cart.processData(dataArr);
	
		// Scenario#2
		cart = new ShoppingCart(newPricingRules);
		String[] dataArr2 = cart.prop.get("SCENE2").toString().split(",");
		System.out.println("Scenario#2");
		cart.processData(dataArr2);
		
		// Scenario#3
		cart = new ShoppingCart(newPricingRules);
		String[] dataArr3 = cart.prop.get("SCENE3").toString().split(",");
		System.out.println("Scenario#3");
		cart.processData(dataArr3);		
		
		// Scenario#4
		cart = new ShoppingCart(newPricingRules);
		String[] dataArr4 = cart.prop.get("SCENE4").toString().split(",");
		System.out.println("Scenario#4");
		cart.processData(dataArr4);
	}
	
	public void processData(String[] dataArr) {
		String code, promo;
		String[] itemArr;
		for (String data : dataArr) {
			itemArr = data.split("<p>");
			code = itemArr[0];
			
			if (itemArr.length == 1) {
				add(code);
			} else {
				promo = itemArr[1];
				add(code, promo);
			}
		}
		
		total();
		items();
	}
	
	public void add(String code) {
		int count = itemsMap.get(code) == null ? 0 : itemsMap.get(code).intValue();
		itemsMap.put(code, count + 1);
	}
	
	public void add(String code, String promo) {
		int count = itemsMap.get(code) == null ? 0 : itemsMap.get(code).intValue();
		itemsMap.put(code, count + 1);
		
		if (prop.getProperty("RULES_ON").equals(prop.getProperty("RULE4")) 
				&& prop.getProperty("RULE4_PROMO_CODE").equals(promo)) {
			withPromo = true;
		}
	}
	
	public void total() {
		
		double total = 0;
		int unli1GBCount = itemsMap.get(prop.get("CODE_1GB")) == null ? 0 : itemsMap.get(prop.get("CODE_1GB")).intValue();
		double unli1GBPrice= productsMap.get(prop.get("CODE_1GB")).getPrice();
		
		int unli2GBCount = itemsMap.get(prop.get("CODE_2GB")) == null ? 0 : itemsMap.get(prop.get("CODE_2GB")).intValue();
		double unli2GBPrice= productsMap.get(prop.get("CODE_2GB")).getPrice();
		
		int unli5GBCount = itemsMap.get(prop.get("CODE_5GB")) == null ? 0 : itemsMap.get(prop.get("CODE_5GB")).intValue();
		double unli5GBPrice= productsMap.get(prop.get("CODE_5GB")).getPrice();
		
		int dP1GBCount = itemsMap.get(prop.get("CODE_1GBDP")) == null ? 0 : itemsMap.get(prop.get("CODE_1GBDP")).intValue();
		double dP1GBPrice= productsMap.get(prop.get("CODE_1GBDP")).getPrice();
		
		if (prop.getProperty("RULES_ON").equals(prop.getProperty("RULE1"))) {
			
			total += ((unli1GBCount / Integer.valueOf(prop.get("RULE1_ITEMS").toString())) 
					* (Integer.valueOf(prop.get("RULE1_ITEMS").toString()) - Integer.valueOf(prop.get("RULE1_FREE").toString()))) 
					* unli1GBPrice;
			
			total += (unli1GBCount % Integer.valueOf(prop.get("RULE1_ITEMS").toString())) * unli1GBPrice;
		} else {
			total += unli1GBCount * unli1GBPrice;
		}
		
		if (prop.getProperty("RULES_ON").equals(prop.getProperty("RULE2"))) {
			
			if (unli5GBCount > Integer.valueOf(prop.get("RULE2_ITEMS").toString())) {
				total += unli5GBCount * Double.valueOf(prop.get("RULE2_PRICE").toString());
			} else {
				total += unli5GBCount * unli5GBPrice;
			}
		} else {
			total += unli5GBCount * unli5GBPrice;
		}
		

		total += unli2GBCount * unli2GBPrice;
		if (prop.getProperty("RULES_ON").equals(prop.getProperty("RULE3"))) {
			
			int addCnt = (unli2GBCount / Integer.valueOf(prop.get("RULE3_ITEMS").toString())) 
					* Integer.valueOf(prop.get("RULE3_FREE").toString());
			
			itemsMap.put(prop.get("CODE_1GBDP").toString(), dP1GBCount + addCnt);
		}
		
		total += dP1GBCount * dP1GBPrice;
		
		if (withPromo) {
			double discount = (100.00 - Integer.valueOf(prop.get("RULE4_PROMO_DISCOUNT").toString())) / 100.00;
			total *= discount;
		}
		
		DecimalFormat df = new DecimalFormat("####0.00");
		total = Double.valueOf( df.format(total));
		
		System.out.println("Expected Cart Total: $" + total);
	}
	
	public void items() {
		
		int unli1GBCount = itemsMap.get(prop.get("CODE_1GB")) == null ? 0 : itemsMap.get(prop.get("CODE_1GB")).intValue();	
		int unli2GBCount = itemsMap.get(prop.get("CODE_2GB")) == null ? 0 : itemsMap.get(prop.get("CODE_2GB")).intValue();
		int unli5GBCount = itemsMap.get(prop.get("CODE_5GB")) == null ? 0 : itemsMap.get(prop.get("CODE_5GB")).intValue();
		int dP1GBCount = itemsMap.get(prop.get("CODE_1GBDP")) == null ? 0 : itemsMap.get(prop.get("CODE_1GBDP")).intValue();
		
		System.out.println("Expected Cart Items");
		
		if (unli1GBCount > 0)
		System.out.println(unli1GBCount + "x " + productsMap.get(prop.get("CODE_1GB")).getName());
		
		if (unli2GBCount > 0)
		System.out.println(unli2GBCount + "x " + productsMap.get(prop.get("CODE_2GB")).getName());
		
		if (unli5GBCount > 0)
		System.out.println(unli5GBCount + "x " + productsMap.get(prop.get("CODE_5GB")).getName());
		
		if (dP1GBCount > 0)
		System.out.println(dP1GBCount + "x " + productsMap.get(prop.get("CODE_1GBDP")).getName());
	}
	
	private void loadProducts() {
		productsMap = new HashMap<>();
		URL url = ShoppingCart.class.getClassLoader().getResource("main/resources/products.csv");
		String csvFile = url.getPath();
		String line = "";
		String cvsSplitBy = ",";
		String[] lineArr;
		Product product;
		
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {
				lineArr = line.split(cvsSplitBy);
				product = new Product();
				product.setCode(lineArr[0]);
				product.setName(lineArr[1]);
				product.setPrice(Double.valueOf(lineArr[2]));
				productsMap.put(product.getCode(), product);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getPropValues(String newPricingRules) throws Exception {
		prop = new Properties();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("main/resources/" + newPricingRules);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + newPricingRules + "' not found in the classpath");
		}
	}
}
