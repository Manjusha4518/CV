package cv.Page;

import base.Common;
import cucumber.api.Scenario;
import cv.Common.Config;
import cv.StepDefinition.Hooks;
import org.jboss.aerogear.security.otp.Totp;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPage {
    public WebDriver driver;
    public Scenario sc;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20), Duration.ofSeconds(1));
    }

    @FindBy(xpath = "//*[contains(@class,'title')]")
    public static WebElement pageTitle;
    @FindBy(id = "username")
    public static WebElement username_txt;
    @FindBy(id = "password")
    public static WebElement password_txt;

    @FindBy(id = "userNameInput")
    public static WebElement Commonusername_txt;
    @FindBy(id = "userNameInput")
    public static List<WebElement> Commonusername_txt1;
    @FindBy(id = "passwordInput")
    public static WebElement Commonpassword_txt;
    @FindBy(id = "submitButton")
    public static WebElement submitButtonBtn;

    @FindBy(id = "Login")
    public static WebElement loginBtn;
    @FindBy(xpath = "//*[@class='column_header_right_noborder']")
    public static WebElement changePassword;
    @FindBy(xpath = "//a[@id='logoutForm:logout_link']")
    public WebElement Logout_lnk;
    //*[@id='oneHeader']/div[2]/span/div[2]/ul/li[9]/span/button/div/span[1]/div/span
    @FindBy(xpath = "//header//span[@class='uiImage']|//header/div[2]/span[1]/div[2]/ul[1]/li[8]/span[1]/button[1]/div[1]/span[1]/div[1]/span[1]")
    public WebElement oneHeader_IMG;
    @FindBy(xpath = "//a[@class='profile-link-label logout uiOutputURL' and contains(text(),'Log Out')]|//div[@class='profile-card-toplinks']/a[contains(text(),'Log Out')]")
    public WebElement Logout_link;

    @FindBy(xpath = "//label[text()='Sign in to one of the following sites:']")
    public WebElement signsites_rdbtn;
    @FindBy(xpath = "//select[@name='RelyingParty']")
    public WebElement AmazonWebServices_drdwn;
    @FindBy(xpath = "//select[@name='RelyingParty']")
    public WebElement SelectAmazonWebServices_drdwnoption;
    @FindBy(xpath = "//input[@name='SignInSubmit']")
    public WebElement SignIn_btn;
    @FindBy(xpath = "//input[@value='Sign in']")
    public List<WebElement> SIGNInlIST_btn;

    @FindBy(xpath = "//input[@name='SignInGo']")
    public WebElement SignGO_btn;

    By headerby = By.xpath("//*[@id='oneHeader']/div[1]/div");

    public void typeUserName(String username) {
        //Common.highLighterMethod(driver,username_txt);
        sc.write("Enter username");
        //username_txt.sendKeys(username);
//		Common.waitForPageLoaded(driver,20);
        Common.waitHighlightAndEnterData(driver, username_txt, username, sc, "UserName Text Box", 30, 2);
    }

    public void typePassword(String password) {
        //Common.highLighterMethod(driver,password_txt);
        sc.write("Enter Password");
        //password_txt.sendKeys(password);
        Common.waitHighlightAndEnterData(driver, password_txt, password, sc, "Password Text Box", 30, 2);
    }

    public void clickSignin() {
        //Common.highLighterMethod(driver,loginBtn);
        sc.write("Click LOGIN button");
        //loginBtn.click();
        Common.waitHighlightAndClick(driver, loginBtn, sc, "Login Button", 30, 2);
    }

    public void login(String username, String password) throws InterruptedException {
        sc.write("Load URL: " + Config.URL);
        driver.get(Config.URL);
//        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
        driver.manage().deleteAllCookies();
//        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        base.Common.takeScreenshot(sc, driver, "Launched CV URL");
        Hooks.message = Hooks.message + "CV Login Url: [" + Config.URL + "]\n";
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));//50
        sc.write("Login CV applcaition");
        sc.write("userName-->" + username);
        System.out.println("userPWD-->" + password);
        sc.write("userPWD--> xxxxx");
        typeUserName(username);
        typePassword(password);
        Hooks.message = Hooks.message + "CV Login username: [" + username + "]\n";
        base.Common.takeScreenshot(sc, driver, "Screen-Login");
        Common.waitHighlightAndClick(driver, loginBtn, sc, "Login Button", 30, 2);
        base.Common.takeScreenshot(sc, driver, "After Signing");
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));//50
        if (Config.ENV.equalsIgnoreCase("QA")) {
            Totp totp = new Totp("OTEMMSLMN4X5VIGLQWZDGHHWXVYLUGBB");
            String CurrentOtp = totp.now();
            System.out.println("CurrentOtp-->" + CurrentOtp);
            base.Common.EnterData(driver, By.xpath("(//label[text()='Verification Code']/following-sibling::div//input)[1]"),
                    CurrentOtp, sc, "verify", false, true, false, true, false, 2, 1);
            base.Common.Click(driver, By.xpath("//input[@id='save']"), sc, "searchPerson Link",
                    false, false, false, false, 4, 1);
        } else if (Config.ENV.equalsIgnoreCase("UAT")) {
            Totp totp = new Totp("NB7PD2O3MVYNMDCZDHETIU3NVCLEGFTL");
            String CurrentOtp = totp.now();
            System.out.println("CurrentOtp1-->" + CurrentOtp);
            List<WebElement> MFAPage = driver.findElements(By.xpath("//a[text()=\"Having Trouble?\"]"));
            if (MFAPage.size() > 0) {
                base.Common.Click(driver, By.xpath("//a[text()=\"Having Trouble?\"]"), sc, "searchPerson Link",
                        false, false, true, false, 4, 1);
                base.Common.Click(driver, By.xpath("//a[text()='Use a Different Verification Method']"), sc, "searchPerson Link",
                        false, false, true, false, 4, 1);
                base.Common.EnterData(driver, By.xpath("//label[text()='Verification Code']/following-sibling::div/input[@type='text']"),
                        CurrentOtp, sc, "verify", true, true, false, true, true, 2, 1);
                base.Common.Click(driver, By.xpath("//input[@id='save']"), sc, "searchPerson Link",
                        false, false, false, false, 4, 1);
            } else {
                System.out.println("ThisTimeLoginNotAskedMFA");
            }
        } else if (Config.ENV.equalsIgnoreCase("U100")) {
            Totp totp = new Totp("C4ZV34W6JIAS3MUDZVGX26A6XA4PZHSV"); //-salesforece U100
            String CurrentOtp = totp.now();
            System.out.println("CurrentOtp-->" + CurrentOtp);
            base.Common.EnterData(driver, By.xpath("//label[text()='Verification Code']/following-sibling::div/input[@type='text']"),
                    CurrentOtp, sc, "verify", true, true, false, true, false, 2, 1);
            base.Common.Click(driver, By.xpath("//input[@id='save']"), sc, "searchPerson Link",
                    false, false, false, false, 4, 1);
        }
    }

    public void loginTestURK() throws InterruptedException {
        driver.get("https://qa-js.reliancematrix.com/");
    }

    public void loinginCVSSO(String username, String password) throws InterruptedException {
        sc.write("Load URL: " + Config.URLadfs);
        driver.get(Config.URLadfs);
        base.Common.waitForPageLoaded(driver, 30);
        Hooks.message = Hooks.message + "CV SSO Url: [" + Config.URLadfs + "]\n";
        base.Common.waitHighlightAndClick(driver, signsites_rdbtn, sc, "Select option", 20, 2);
        base.Common.waitHighlightAndClick(driver, AmazonWebServices_drdwn, sc, "Select option", 20, 2);
        if (Config.ENV.equalsIgnoreCase("QA")) {
            base.Common.clickOptionByVisibleText(driver, SelectAmazonWebServices_drdwnoption, "Claim Vantage P2 QA");
        } else if (Config.ENV.equalsIgnoreCase("PREPROD")) {
            base.Common.clickOptionByVisibleText(driver, SelectAmazonWebServices_drdwnoption, "Claim Vantage PreProd");
        } else if (Config.ENV.equalsIgnoreCase("UAT")) {
            base.Common.clickOptionByVisibleText(driver, SelectAmazonWebServices_drdwnoption, "Claim Vantage P2 UAT");
        }
        base.Common.handleSFErrorPopup(driver);
        base.Common.handleTryagainPopup(driver);
        if (SignIn_btn.isDisplayed()) {
            base.Common.waitHighlightAndClick(driver, SignIn_btn, sc, "Save button", 20, 2);
        } else if (SignGO_btn.isDisplayed()) {
            base.Common.waitHighlightAndClick(driver, SignGO_btn, sc, "Save button", 20, 2);
        }
        int userCount = Commonusername_txt1.size();
        if (userCount > 0) {
            base.Common.ClearEnterTabInTextBox(driver, Commonusername_txt, username, sc, "username", 20, 2);
            base.Common.ClearEnterTabInTextBox(driver, Commonpassword_txt, password, sc, "password", 20, 2);
//            base.Common.waitForPageLoaded(driver, 30);
//            base.Common.waitHighlightAndClick(driver, submitButtonBtn, sc, "submitButtonBtn", 20, 2);
            By sbmitElmt = By.id("submitButton");
            WebElement submitButtonEl = wait.until(ExpectedConditions.elementToBeClickable(sbmitElmt));
            submitButtonEl.click();

        }
        for (int i = 1; i <= 3; i++) {
            base.Common.waitForPageLoaded(driver, 30);
            base.Common.handleSFErrorPopup(driver);
            base.Common.handleTryagainPopup(driver);
            List<WebElement> homeIcon = driver.findElements(By.xpath("//img[@class='icon noicon']"));
            System.out.println("homeIcon: " + homeIcon.size());
            if (homeIcon.size() > 0) {
                System.out.println("Homepage icon Found → no refresh needed");
                break;
            } else {
                System.out.println("Homepage icon found → refreshing page... Attempt: " + i);
                driver.navigate().refresh();
                Thread.sleep(3000);
            }
        }
        List<WebElement> AuthError = driver.findElements(By.xpath("//div[text()='For security reasons, we require additional information to verify your account']"));
        if (AuthError.size() > 0) {
            sc.write("SSO Authentication Error occurred");
            Hooks.message = Hooks.message + "SSO Authentication Error occurred\n";
            driver.navigate().refresh();
            Thread.sleep(2000);
        }
    }

    public void openHeaderImg() throws InterruptedException {
        base.Common.sleep(2000);
        Common.highLighterMethod(driver, oneHeader_IMG);
        base.Common.sleep(4000);
        oneHeader_IMG.click();
        base.Common.sleep(4000);
        Common.highLighterMethod(driver, Logout_link);
        base.Common.sleep(4000);
    }

    public void logout() throws InterruptedException {
        Common.highLighterMethod(driver, oneHeader_IMG);
        base.Common.sleep(8000);
        Common.waitHighlightAndClick(driver, oneHeader_IMG, sc, "Header Image", 30, 2);
        Common.waitForPageLoaded(driver, 20);
        base.Common.sleep(1000);
        Common.waitHighlightAndClick(driver, Logout_link, sc, "Logout Link", 20, 2);
        Common.takeScreenshot(sc, driver, "User logout");
        Common.waitForPageLoaded(driver, 20);
        base.Common.sleep(8000);
    }

    public void setRunningTCDisplayInPageHeader(String tcDetail) {
        WebElement headerDiv = driver.findElement(headerby);
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            //js.executeScript("document.getElementById('Div_Id').innerHTML="+ tcDetail);
            js.executeScript("document.evaluate('//div[1]/section/header/div[1]/div/span', document, null, 9, null).singleNodeValue.innerHTML=" + tcDetail);
        } catch (Exception e) {
            System.out.println("!!Header tc name not added!!");
        }
    }
}