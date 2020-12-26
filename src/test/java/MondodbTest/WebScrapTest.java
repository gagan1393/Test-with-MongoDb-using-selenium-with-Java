package MondodbTest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.github.bonigarcia.wdm.WebDriverManager;



public class WebScrapTest {
	
	WebDriver driver;
	MongoCollection<Document> webCollection;
	
	@BeforeSuite
	public void connectMongodb()
	{
		Logger mongologger = Logger.getLogger("org.mongodb.driver");
		 MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		 MongoDatabase database = mongoClient.getDatabase("automationdb");
		 
		 //create collection
		 webCollection = database.getCollection("web");
	}
	
	@BeforeTest
	public void setup()
	{
		WebDriverManager.chromedriver().setup();
		ChromeOptions opt = new ChromeOptions();
		opt.addArguments("--headless");
		driver = new ChromeDriver(opt);
	}
	
	@DataProvider
	public Object[][] getWebData()
	{
		return new Object[][] {
			
			{"https://www.amazon.in/"},
			{"https://www.flipkart.com/"},
			{"https://www.snapdeal.com/"}
			
		};
		
	}
	
	@Test(dataProvider = "getWebData")
	public void webScrapTest(String appURL)
	{
		driver.get(appURL);
		String url = driver.getCurrentUrl();
		String title = driver.getTitle();
		int linksSize = driver.findElements(By.tagName("a")).size();
		int imgaeSize = driver.findElements(By.tagName("img")).size();
		List<WebElement> linkslist = driver.findElements(By.tagName("a"));
		List<String> linksAttrlist = new ArrayList<String>();
		
		List<WebElement> Imageslist = driver.findElements(By.tagName("img"));
		List<String> Imagesrclist = new ArrayList<String>();
		
		Document d1 = new Document();
		d1.append("url", url);
		d1.append("Title", title);
		d1.append("Links", linksSize);
		d1.append("Images", imgaeSize);
		
		for(WebElement ele : linkslist) {
			String hrefvalue = ele.getAttribute("href");
			linksAttrlist.add(hrefvalue);
			
		}
		
		for(WebElement ele : Imageslist) {
			String srcvalue = ele.getAttribute("src");
			Imagesrclist.add(srcvalue);
			
		}
		
		d1.append("LinksAttribute", linksAttrlist);
		d1.append("SrcValue", Imagesrclist);
		
		List<Document> docslist = new ArrayList<Document>();
		docslist.add(d1);
		
		webCollection.insertMany(docslist);
	}
	
	
	
	

	@AfterTest
	public void teardown()
	{
		driver.quit();
	}
}
