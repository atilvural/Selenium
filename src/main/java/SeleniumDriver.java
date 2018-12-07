import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SeleniumDriver {
    public static void main(String[] args){

        System.setProperty("webdriver.gecko.driver", "/home/atl/Desktop/geckodriver");

        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        driver.get("https://www.tikobahis26.com/sportsbook/upcoming?sport_id=1");

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[class='close']")));
        WebElement element = driver.findElement(By.cssSelector("button[class='close']"));
        element.click();

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("ul[class='list sports']")));
        WebElement sports = driver.findElement(By.cssSelector("ul[class='list sports']"));
        List<WebElement> links = sports.findElements(By.tagName("li"));

        //Basket, Futbol gibi kategori secimi
        for (int i = 0; i < links.size(); i++)
        {
            links.get(i).click();
            String leagueURL = driver.getCurrentUrl();
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("ul[class='list categories']")));
            WebElement leagues = driver.findElement(By.cssSelector("ul[class='list categories']"));
            List<WebElement> leagueList =leagues.findElements(By.tagName("li"));
            //Avrupa, Turkiye gibi ulke ulke dolasmak
            for(int j = 1; j < leagueList.size(); j++) {
                WebElement leagueElement = leagueList.get(j);
                if (!leagueElement.getAttribute("style").equals("display: none;")){
                    leagueElement.click();

                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("table[class='table']")));
                    WebElement matches = driver.findElement(By.cssSelector("table[class='table']"));
                    List<WebElement> matchList = matches.findElements(By.tagName("tr"));

                    //Secilen ulkenin maclari
                    for (int k = 0; k < matchList.size(); k++) {
                        ClickWithJS(driver, matchList.get(k));

                        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[class='match-detail']")));
                        WebElement matchElement = driver.findElement(By.cssSelector("div[class='match-detail']"));
                        String time = matchElement.findElement(By.cssSelector("div[class='time'")).getText();

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            Date date = formatter.parse(time);
                            if(isToday(date)) {
                                String home = matchElement.findElement(By.cssSelector("span[class='home'")).getText();
                                String away = matchElement.findElement(By.cssSelector("span[class='away'")).getText();

                                List<WebElement> matchOddsList = matchElement.findElements(By.cssSelector("div[class='oddMain'"));
                                System.out.println(time + " " + home + " - " + away + " " + matchOddsList.size());

                                //Herbir odd'dan oranları cekmek
                                for(int l = 0; l < matchOddsList.size(); l++){
                                    WebElement oddTitleElement = matchOddsList.get(l).findElement(By.cssSelector("div[class='title']"));
                                    String oddName = oddTitleElement.findElement(By.cssSelector("span[class='marketname']")).getText();
                                    System.out.print(oddName + " ");

                                    WebElement oddDiv = matchOddsList.get(l).findElement(By.cssSelector("div[class='content collapse in'"));
                                    List<WebElement> odds = oddDiv.findElements(By.cssSelector("div[class='col-odd'"));

                                    for(int m = 0; m < odds.size(); m++){
                                        String outcomeName = odds.get(m).findElement(By.cssSelector("span[class='outcomeName']")).getText();
                                        String odd = odds.get(m).findElement(By.cssSelector("span[class='oddName']")).getText();

                                        System.out.print(outcomeName + "(" + odd + ") ");
                                    }
                                }
                                System.out.println();

                            }else{
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //Bir onceki taba donup kalınan j'den devam edilmeli
                    WebElement backBtn = driver.findElement(By.cssSelector("a[class='back-list']"));
                    ClickWithJS(driver, backBtn);
                    links.get(i).click();
                }
            }
        }
    }
    private static void ClickWithJS(WebDriver driver, WebElement element){
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("arguments[0].click();", element);
    }
    private static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
