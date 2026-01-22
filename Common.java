package base;

import com.github.mkolisnyk.cucumber.reporting.CucumberDetailedResults;
import com.opencsv.CSVWriter;
import com.paulhammant.ngwebdriver.NgWebDriver;
import cucumber.api.Scenario;
import cucumber.runtime.ScenarioImpl;
import gherkin.formatter.model.Result;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.WebDriverManagerException;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.reducers.ReducingMethod;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;



/*
 *This class contains the common utility methods to perform the intermediate functions during end to end testing
 * such as DataSheet connection, getScreenshot, settter and getters
 * @Author: Janagan sjanagan@virtusa.com
 */

@SuppressWarnings("ALL")
public class Common {


 /*   @FindBy(id = "ajaxStatus")
    static WebElement ajaxLoopicons;*/

    private static final String configFilePath = "../Base/resources/config/Config.properties";
    //private static final String appConfigFilePath = "./resources/config/appConfig.properties";
//    private static final Logger log = LogManager.getLogger(Common.class);
    public static Properties prob;
    public static String defaultFileDownloadiDir = null;
    public static int chromeOptionImageBlocked = 0; //0 - default, 1 - allowed, 2 - blocked
    public static String userSleepMS = Config.userSleepMS;
    public static String appName = Config.appName;
    public final static String COLOR_RESET = "\u001B[0m";
    public final static String GREEN = "\u001B[32m";
    public final static String BLUE = "\u001B[34m";

    public final static String YELLOW = "\u001B[33m";
    //NgWebDriver ngwebdriver;
    static WebDriverWait wait;
    public static void main(String[] args) {
        Common c = new Common();
    }

    public Common() {
        //System.out.println("Base > Common loaded");
    }

    public static void sleep(long waitTimeMilli) {
        try {
            if (userSleepMS != null && !userSleepMS.trim().equalsIgnoreCase("")) {
                long lms = Long.parseLong(userSleepMS);
                System.out.println("User sleep for ms " + userSleepMS);
                Thread.sleep(lms);
            } else {
                System.out.println("Sleep with provided ms " + userSleepMS);
                Thread.sleep(waitTimeMilli);
            }
        } catch (InterruptedException e) {
            //Thread.currentThread().interrupt();
            //e.printStackTrace();
        }
    }

    private static void csleep(long waitTimeMilli) {
        try {
            Thread.sleep(waitTimeMilli);
        } catch (InterruptedException e) {
            //Thread.currentThread().interrupt();
        }
    }

    public static void driverSetting(String browserName) {
        browserName = browserName.trim().toUpperCase();
        if (browserName.contains("CHROME")) {
            System.out.println("webmanager driver exec setting Start");
            WebDriverManager.chromedriver().setup();
//            System.setProperty("webdriver.chrome.driver", Config.DRIVER_CHROME_PATH);
//            System.out.println("[Webdriver assinged from local]");
        } else if (browserName.contains("EDGE")) {
            try {
                WebDriverManager.edgedriver().setup();
                System.out.println("[Webdriver assinged from internet by webdriver manager]");
            } catch (WebDriverManagerException e) {
                System.setProperty("webdriver.edge.driver", Config.DRIVER_EDGE_PATH);
                System.out.println("[Webdriver assinged from local]");
            }
        }
    }

    public static void driverSetting(String browserName, String version) {
        browserName = browserName.trim().toUpperCase();
        if (browserName.contains("CHROME")) {
            System.out.println("driver exec setting");
            if (version == null || version.equalsIgnoreCase("")) {
                System.out.println("---> User NOT Specified CHROME Version -> So latest version will be picked up to install for driver");
                WebDriverManager.chromedriver().setup();
            } else {
                System.out.println("---> User Specified CHROME Version for Chrome Driver -> " + version);
                WebDriverManager.chromedriver().driverVersion(version).setup();
            }
        } else if (browserName.contains("EDGE")) {
            try {
                if (version == null || version.equalsIgnoreCase("")) {
                    System.out.println("---> User NOT Specified EDGE Version -> So latest version will be picked up to install for driver");
                    WebDriverManager.edgedriver().driverVersion(version).setup();
                } else {
                    System.out.println("---> User Specified Version for Edge Driver -> " + version);
                    WebDriverManager.edgedriver().setup();
                }
                System.out.println("[Webdriver assinged from internet by webdriver manager]");
            } catch (WebDriverManagerException e) {
                System.setProperty("webdriver.edge.driver", Config.DRIVER_EDGE_PATH);
                System.out.println("[Webdriver assinged from local]");
            }
        }
    }

    public static WebDriver getDriver(String browserName) throws Exception {
        WebDriver wd;
        browserName = browserName.trim().toUpperCase();
        //Properties pro = getConfig();
        if (browserName.contains("CHROME")) {
            System.out.println("........... CHROME DRIVER SETUP......................");
            ChromeOptions options = new ChromeOptions();
            Thread.sleep(500);//3000
            options.addArguments("--disable-web-security");
            options.addArguments("--no-proxy-server");
            options.setPageLoadStrategy(PageLoadStrategy.EAGER); // DOM ready, not full load//added 11/26 ram
//            ---------------------
            /*options.addArguments("user-data-dir=/path/to/custom/profile");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-application-cache");
            options.addArguments("profile.exit_type=Normal");
            options.addArguments("profile.exited_cleanly=true");*/
            options.addArguments("--disable-gpu"); // Windows/CI stability
            options.addArguments("--disable-renderer-backgrounding");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--no-sandbox");  // Linux CI
            options.addArguments("--remote-allow-origins=*");// If CORS/extension messages cause noise:
            options.addArguments("--disable-extensions");  //added 11/26 ram
            options.addArguments("--disable-features=PasswordCheck");
            options.addArguments("--disable-features=SafeBrowsing");
            options.addArguments("--disable-save-password-bubble");
            options.addArguments("--disable-incognito");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("enable-automation");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-browser-side-navigation");
            options.addArguments("--disable-gpu");
            options.addArguments("--enable-javascript");
            options.addArguments("--disable-notifications");
            options.addArguments("disable-features=NetworkService");
            options.addArguments("--dns-prefetch-disable");
            options.addArguments("--disable-extensions");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-features=PaintHolding");
            //options.addArguments("force-device-scale-factor=1.0");
            //options.addArguments("high-dpi-support=0.75");
            options.addArguments("--log-level=3");
            System.setProperty("webdriver.chrome.silentOutput", "true");

            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            options.addArguments("--disable-features=PasswordBreachDetection");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-infobars");
            options.addArguments("--enable-automation");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-features=RendererCodeIntegrity");
            options.addArguments("--renderer-process-limit=10");

            options.setPageLoadTimeout(Duration.ofSeconds(120));

           /* options.setExperimentalOption("prefs", Map.of(
                    "credentials_enable_service", false,
                    "profile.password_manager_enabled", false
            ));
*/
            Map<String, Object> prefs = new HashMap<String, Object>();
            //---------experimental options -------------------
            System.out.println("chromePrefStart");
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            prefs.put("profile.password_manager_leak_detection", false);
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put("download.prompt_for_download", "false");
            prefs.put("plugins.plugins_disabled", new String[]{"Adobe Flash Player", "Edge PDF Viewer"});
            prefs.put("plugins.always_open_pdf_externally", true);
            prefs.put("autofill.profile_enabled", false); // Disable autofill
            prefs.put("autofill.credit_card_enabled", false); // Disable credit card autofill
            prefs.put("plugins.plugins_disabled", new String[]{"Adobe Flash Player", "Chrome PDF Viewer"});
            prefs.put("plugins.always_open_pdf_externally", true);
            prefs.put("safebrowsing.enabled", false); // Disable safe browsing
            prefs.put("safebrowsing.disable_download_protection", true); // Disable download protection
            prefs.put("profile.default_content_setting_values.notifications", 2); // Disable notifications
            prefs.put("profile.default_content_setting_values.popups", 2); // Disable popups
            prefs.put("profile.managed_default_content_settings.local_network_access", 2);
            options.setExperimentalOption("prefs", prefs);
            options.addArguments("--disable-features=LocalNetworkAccessPermissionPrompt");
//            options.setExperimentalOption("prefs", prefs);
//            prefs.put("debuggerAddress", "localhost:61861");
//            prefs.put("profile.managed_default_content_settings.images", chromeOptionImageBlocked);
            if (Config.isDriverHeadless.contains("y")) {
                options.addArguments("--headless");
            }
            if (defaultFileDownloadiDir != null) {
                prefs.put("download.default_directory", System.getProperty("user.dir") + "\\" + defaultFileDownloadiDir);
                System.out.println("--> Default Download Directory changed to : " + System.getProperty("user.dir") + "\\" + defaultFileDownloadiDir);
            }
            //---------------------------------------------------------------
            System.out.println("........... CHROME OPTION ASSIGNED......................");
            wd = new ChromeDriver(options);

        } else if (browserName.contains("FIREFOX") || browserName.equals("FF")) {
            System.out.println("........... FIREFOX DRIVER SETUP......................");
            //WebDriverManager.firefoxdriver().setup();
            //System.setProperty("webdriver.firefox.marionette", "false");
            FirefoxProfile profile = new FirefoxProfile();
            if (defaultFileDownloadiDir != null) {
                profile.setPreference("browser.download.folderList", 2);
                profile.setPreference("browser.download.useDownloadDir", false);
                profile.setPreference("browser.download.dir", System.getProperty("user.dir") + "\\" + defaultFileDownloadiDir);
                //System.out.println("--> Default Download Directory: " + System.getProperty("user.dir") + "\\" + defaultFileDownloadiDir);
            } else {
                profile.setPreference("browser.download.folderList", 1);
                profile.setPreference("browser.download.useDownloadDir", true);
            }
            profile.setPreference("browser.download.manager.showWhenStarting", false);
            profile.setPreference("browser.download.manager.focusWhenStarting", false);
            profile.setPreference("browser.helperApps.alwaysAsk.force", false);
            profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
            profile.setPreference("browser.download.manager.closeWhenDone", true);
            profile.setPreference("browser.download.manager.showAlertOnComplete", false);
            profile.setPreference("browser.download.manager.useWindow", false);
            profile.setAcceptUntrustedCertificates(true);
            profile.setAssumeUntrustedCertificateIssuer(true);
            FirefoxOptions optionsF = new FirefoxOptions();
            optionsF.setProfile(profile);
            optionsF.setPageLoadStrategy(PageLoadStrategy.EAGER);
            //wd = new FirefoxDriver(optionsF);
            System.setProperty("webdriver.firefox.marionette", "false");
            try {
                wd = WebDriverManager.firefoxdriver().capabilities(optionsF).avoidFallback().avoidShutdownHook().create();
                System.out.println("[Webdriver assinged from internet by webdriver manager]");
            } catch (WebDriverManagerException e) {
                wd = null;
                System.setProperty("webdriver.firefox.driver", Config.DRIVER_FIREFOX_PATH);
                System.out.println("[Webdriver assinged from local]");
                wd = new FirefoxDriver(optionsF);
            }
        } else if (browserName.equals("IE") || browserName.contains("Explorer")) {
            System.out.println("........... IE DRIVER SETUP......................");
            //System.setProperty("webdriver.ie.driver", Config.DRIVER_IE_PATH);
            WebDriverManager.iedriver().setup();
            InternetExplorerOptions options = new InternetExplorerOptions();
            options.ignoreZoomSettings();
            //options.disableNativeEvents();
            options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
            System.out.println(">>>>>>> native event disabled in IE");
            options.enablePersistentHovering();
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            //options.destructivelyEnsureCleanSession();
            //options.introduceFlakinessByIgnoringSecurityDomains();
            if (defaultFileDownloadiDir != null) {
                options.setCapability("browser.download.dir", System.getProperty("user.dir") + "\\" + defaultFileDownloadiDir);
                //base.Common.setDownloadPathforIE(System.getProperty("user.dir")+"\\"+defaultFileDownloadiDir);
            }
            wd = new InternetExplorerDriver(options);
        } else if (browserName.contains("EDGE")) {
            System.out.println("........... EDGE OPTIONS SETUP ......................");
            EdgeOptions options = new EdgeOptions();
            //options.addArguments("start-maximized");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("enable-automation");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-browser-side-navigation");
            options.addArguments("--disable-gpu");
            options.addArguments("--enable-javascript");
            options.addArguments("--disable-notifications");
            options.addArguments("disable-features=NetworkService");
            options.addArguments("--dns-prefetch-disable");
            options.addArguments("--disable-extensions");
            options.addArguments("--ignore-certificate-errors");
            //options.addArguments("force-device-scale-factor=0.8");
            //options.addArguments("high-dpi-support=0.8");
            options.addArguments("--log-level=3");
            System.setProperty("webdriver.edge.silentOutput", "true");
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            Map<String, Object> prefs = new HashMap<String, Object>();
            //---------experimental options -------------------
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put("download.prompt_for_download", "false");
            prefs.put("plugins.plugins_disabled", new String[]{"Adobe Flash Player", "Edge PDF Viewer"});
            prefs.put("plugins.always_open_pdf_externally", true);
//            prefs.put("profile.managed_default_content_settings.images", 2);
            if (defaultFileDownloadiDir != null) {
                prefs.put("download.default_directory", System.getProperty("user.dir") + "\\" + defaultFileDownloadiDir);
                System.out.println("--> Default Download Directory: " + System.getProperty("user.dir") + "\\" + defaultFileDownloadiDir);
            }
            options.setExperimentalOption("prefs", prefs);
            wd = new EdgeDriver(options);
        } else {
            wd = null;
            throw new Exception("Browser type not specified or incorrect: " + browserName);
        }
        System.out.println("........... BROWSER and DRIVER CAPABILITIES and GENERAL ......................");
//        wd.manage().deleteAllCookies(); //-----09/30/2021 ram check this before commit
        wd.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        wd.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
//        Thread.sleep(3000);
        wd.manage().window().maximize();
        //wd.manage().window().fullscreen();
//        Thread.sleep(3000);
        Capabilities cap = ((RemoteWebDriver) wd).getCapabilities();
        Config.testbrowserName = cap.getBrowserName();
        Config.testbrowserVersion = cap.getBrowserVersion();
        System.out.println("Platform Name -> " + cap.getPlatformName().toString());
        //Config.testPlatform=String.valueOf(cap.getPlatform());
        try {
            if (browserName.equalsIgnoreCase("ff") || browserName.equalsIgnoreCase("firefox")) {
                Config.webdriverVersion = cap.getCapability("moz:geckodriverVersion").toString();
            } else {
                Map<String, String> brMap = (Map<String, String>) cap.getCapability(cap.getBrowserName());
                Config.webdriverVersion = brMap.get(cap.getBrowserName() + "driverVersion").split(" ")[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Config.testEnv = "Browser Name/Version " + Config.testbrowserName.toUpperCase() + "/" + Config.testbrowserVersion + "\n"
                + "WebDriver Version: " + Config.webdriverVersion + "\n"
                + "OS: " + base.Config.OS_NAME;
        Config.testbrowserAndVersion = Config.testbrowserName + " (" + Config.testbrowserVersion + ")";
        System.out.println("Test Environment: \n" + Config.testEnv);
        System.out.println(browserName + " >> Browser loaded");
        return wd;
    }

    public static void writeToFile(String filePath, String content, boolean isAppend) {
        try {
            File f = new File(filePath);
            f.createNewFile();
        } catch (Exception e) {
            System.out.println("File not exist issue or access issue: " + filePath);
        }
        try (FileWriter writer = new FileWriter(filePath, isAppend);
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(content);
            System.out.println("Write value on File [" + filePath + "] : " + content);
        } catch (IOException e) {
            System.err.format("writeToFile: Exception: %s%n", e);
        }
    }

    public static String convertToFullProductName(String productname) {
        String pFullName = productname.trim().toLowerCase();
        if (pFullName == null) {
            return "";
        } else if (pFullName.contains("ac") || pFullName.contains("accident")) {
            return "VAI";
        } else if (pFullName.contains("hi") || pFullName.contains("health")) {
            return "VHI";
        } else if (pFullName.contains("ci") || pFullName.contains("critical")) {
            return "VCI";
        } else {
            return productname;
        }
    }

    public static double getTotalExecutionTime(String start, String end, String unit) {
        double total = 0.0;
        try {
            long s = Long.valueOf(start);
            long e = Long.valueOf(end);
            System.out.println("Tracked startTime= " + start + " endTime= " + end);
            total = Double.valueOf((new DecimalFormat("#.#")).format((e - s)));
            if (unit.trim().equalsIgnoreCase("min") || unit.trim().equalsIgnoreCase("") || unit.trim().equalsIgnoreCase("m"))
                total = Double.valueOf((new DecimalFormat("#.#")).format(total / (1000 * 60)));
            else if (unit.trim().equalsIgnoreCase("s") || unit.trim().equalsIgnoreCase("sec"))
                total = Double.valueOf((new DecimalFormat("#.#")).format(total / 1000));
            System.out.println("Total TestExecution Time for Build>>> " + total);
        } catch (Exception e) {
            total = 0.0;
            System.out.println("Total TestExecution Time for Build with exception: " + total);
        }
        return total;
    }

    public static WebElement scrollonElement(WebDriver driver, WebElement element) {
        try {
            highLighterMethod(driver, element);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView();", element);
        } catch (Exception e) {
            System.out.println("Scroll in the element not supporting or working");
        }
        csleep(1000);
        return element;
    }

    public static WebElement scrollonElement(WebDriver driver, By locator) {
        try {
            highLighterMethod(driver, driver.findElement(locator));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView();", driver.findElement(locator));
        } catch (Exception e) {
            System.out.println("Scroll in the element not supporting or working");
        }
        csleep(1000);
        return driver.findElement(locator);
    }

    public static WebElement scrollonElement1(WebDriver driver, By locator) {
        try {
            highLighterMethod(driver, driver.findElement(locator));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(locator));
        } catch (Exception e) {
            System.out.println("Scroll in the element not supporting or working");
        }
        csleep(1000);
        return driver.findElement(locator);
    }

    public static void highLighterMethod(WebDriver driver, WebElement element) {
//        waitForPageLoaded(driver, 5); //10
//        System.out.println("Highlight element");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript
                    ("arguments[0].setAttribute('style', ' border: 2px dashed orange;');", element);  //changed 1pc and solid
        } catch (Exception e) {
//            System.out.println("Highlight in the element not supporting or working");
        }
        csleep(500);//1000
    }

    public static void highLighterMethod(WebDriver driver, By locator) {
//        waitForPageLoaded(driver, 10);
//        System.out.println("Highlight element");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript
                    ("arguments[0].setAttribute('style', ' border: 1.5px solid orange;');", locator);  //changed 1pc
        } catch (Exception e) {
//            System.out.println("Highlight in the element not supporting or working");
        }
        csleep(1000);
    }

    public static void scrollandhighLighterMethod(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView();", element);
        } catch (Exception e) {
            System.out.println("Scroll in the element not supporting or working");
        }
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript
                    ("arguments[0].setAttribute('style', ' border: 1px solid orange;');", element);
        } catch (Exception e) {
            System.out.println("Highlight in the element not supporting or working");
        }
//        csleep(1000);//1000
    }

    public static void scrollMethod(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView();", element);
        } catch (Exception e) {
            System.out.println("Scroll in the element not supporting or working");
        }
        csleep(500);//1000
    }

    public static void scrollElement(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        } catch (Exception e) {
            System.out.println("Scroll not supported");
        }
        csleep(500);
    }

    public static void scrollElement(WebDriver driver, By locator) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", driver.findElement(locator));
        } catch (Exception e) {
            System.out.println("Scroll not supported");
        }
        csleep(500);
    }

    public static void highLighterMethod(WebDriver driver, WebElement element, String colorName) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript
                    ("arguments[0].setAttribute('style', ' border: 1px solid " + colorName + ";');", element);
        } catch (Exception e) {
            System.out.println("Highlight in the element not supporting or working");
        }
        csleep(1000);
    }

    public static void SCREENSHOTSDIR(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript
                    ("arguments[0].setAttribute('style', ' border: 1px solid orange;');", element);
        } catch (Exception e) {
            System.out.println("Highlight in the element not supporting or working");
        }
        csleep(1000);
    }

    public static void setDownloadPathforIE(String path) {
        String path1 = "\"" + path + "\"";
        System.out.println("Default download path: " + path1);
        String cmd1 = "REG ADD \"HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\Main\" /F /V \"Default Download Directory\" /T REG_SZ /D " + path1;
        try {
            Runtime.getRuntime().exec(cmd1);
        } catch (Exception e) {
            System.out.println("Coulnd't change the registry for default directory for IE: " + path);
        }
    }

    //This method to use for Angular JS application
    public static NgWebDriver getNGWebDriver(WebDriver driver) {
        JavascriptExecutor jsDriver = (JavascriptExecutor) driver;
        return new NgWebDriver(jsDriver);
    }

    //Click the element
    public static void jsClick(WebDriver driver, WebElement element) {
//        highLighterMethod(driver,element);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public static void jsDoubleClick(WebDriver driver, WebElement element) {
//        highLighterMethod(driver,element);
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].doubleClick();", element);
//        ((JavascriptExecutor) driver).executeScript(
//                "arguments[0].scrollIntoView();", element);
    }

    public static void EnterDataByJS(WebDriver driver, String data, WebElement element) {
        waitUntilRefreshedAndClickable(driver, element, 20, 2);
        highLighterMethod(driver, element);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value= '" + data + "';", element);
    }

    public static ArrayList getContentFromPDFfileOrUrl(String urlpathpdf) throws Exception {
        System.out.println("url path :--> " + urlpathpdf);
        PDFTextStripper pdfStripper = null;
        PDDocument document = null;
        COSDocument cosDoc = null;
        String parsedText = null;
        ArrayList data = new ArrayList();
        int pc = 0;
        if (urlpathpdf.toLowerCase().startsWith("http://") || urlpathpdf.toLowerCase().startsWith("https://")) {
            BufferedInputStream fileToParse = null;
            InputStream input = null;
            try {
                URL url = new URL(urlpathpdf);
                System.out.println("Get stream from url and assing to buffer!!!");
                input = url.openStream();
                fileToParse = new BufferedInputStream(input);
                PDFParser parser = new PDFParser(new RandomAccessReadBufferedFile(urlpathpdf));
                parser.parse();
                cosDoc = parser.parse().getDocument();//getDocument();
                pdfStripper = new PDFTextStripper();
                System.out.println("Load input stream!!!");
                document = new PDDocument(cosDoc);
                pc = document.getNumberOfPages();
                parsedText = pdfStripper.getText(document);
                System.out.println("+++++++++++++++++");
                System.out.println("PDF content captured into string");
                System.out.println("PDF page count: " + pc);
                //System.out.println(parsedText);
                System.out.println("+++++++++++++++++");
            } catch (MalformedURLException e2) {
                System.err.println("URL string could not be parsed " + e2.getMessage());
            } catch (IOException e) {
                System.err.println("Unable to open PDF Parser. " + e.getMessage());
                try {
                    if (cosDoc != null)
                        cosDoc.close();
                    if (document != null)
                        document.close();
                } catch (Exception e1) {
                    e.printStackTrace();
                }
            } finally {
                if (fileToParse != null)
                    fileToParse.close();
                if (input != null)
                    input.close();
            }
        } else {
            File pdffile = new File(urlpathpdf);
            try {
                PDFParser parser = new PDFParser(new RandomAccessReadBufferedFile(pdffile));
                if (parser.isLenient())
                    cosDoc = parser.parse(false).getDocument();//getDocument();
                else
                    cosDoc = parser.parse().getDocument();//getDocument();
                System.out.println("Load input stream!!!");
                document = new PDDocument(cosDoc);
                pc = document.getNumberOfPages();
                System.out.println("Number of pages: " + document.getNumberOfPages());
                parsedText = new PDFTextStripper().getText(document);
                System.out.println("+++++++++++++++++");
                System.out.println("PDF content captured into string");
                System.out.println("PDF page count: " + document.getNumberOfPages());
                //System.out.println(parsedText);
                System.out.println("+++++++++++++++++");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        }
        data.add(0, parsedText);
        data.add(1, pc);
        return data;
    }

    public static PDDocument getDocumentFromPDF(String urlpathpdf) throws Exception {
        System.out.println("url path :--> " + urlpathpdf);
//        PDFTextStripper pdfStripper = null;
        PDDocument document = null;
        COSDocument cosDoc = null;
        String parsedText = null;
//        ArrayList data = new ArrayList();
        int pc = 0;
        if (urlpathpdf.toLowerCase().startsWith("http://") || urlpathpdf.toLowerCase().startsWith("https://")) {
            BufferedInputStream fileToParse = null;
            InputStream input = null;
            try {
                URL url = new URL(urlpathpdf);
                System.out.println("Get stream from url and assing to buffer!!!");
                input = url.openStream();
                fileToParse = new BufferedInputStream(input);
                PDFParser parser = new PDFParser(new RandomAccessReadBufferedFile(urlpathpdf));
                if (parser.isLenient())
                    cosDoc = parser.parse(false).getDocument();//getDocument();
                else
                    cosDoc = parser.parse().getDocument();//getDocument();
//                pdfStripper = new PDFTextStripper();
                System.out.println("Load input stream!!!");
                document = new PDDocument(cosDoc);
                pc = document.getNumberOfPages();
                /*parsedText = pdfStripper.getText(document);
                System.out.println("+++++++++++++++++");
                System.out.println("PDF content captured into string");*/
                System.out.println("PDF page count: " + pc);
                //System.out.println(parsedText);
                System.out.println("+++++++++++++++++");
            } catch (MalformedURLException e2) {
                System.err.println("URL string could not be parsed " + e2.getMessage());
            } catch (IOException e) {
                System.err.println("Unable to open PDF Parser. " + e.getMessage());
                try {
                    if (cosDoc != null)
                        cosDoc.close();
                   /* if (document != null)
                        document.close();*/
                } catch (Exception e1) {
                    e.printStackTrace();
                }
            } finally {
                if (fileToParse != null)
                    fileToParse.close();
                if (input != null)
                    input.close();
            }
        } else {
            File pdffile = new File(urlpathpdf);
            try {
                PDFParser parser = new PDFParser(new RandomAccessReadBufferedFile(pdffile));
                if (parser.isLenient())
                    cosDoc = parser.parse(false).getDocument();//getDocument();
                else
                    cosDoc = parser.parse().getDocument();//getDocument();
                System.out.println("Load input stream!!!");
                document = new PDDocument(cosDoc);
                pc = document.getNumberOfPages();
                System.out.println("Number of pages: " + pc);
                /*parsedText = new PDFTextStripper().getText(document);
                System.out.println("+++++++++++++++++");
                System.out.println("PDF content captured into string");
                System.out.println("PDF page count: " + document.getNumberOfPages());*/
                //System.out.println(parsedText);
                System.out.println("+++++++++++++++++");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                /*if (document != null) {
                    document.close();
                }*/
            }
        }
//        data.add(0, parsedText);
//        data.add(1, pc);
        return document;
    }

    public static ArrayList getContentFromPDFfileOrUrl(PDDocument pdd, int startPageNo, int endPageNo) throws Exception {
        ArrayList data = new ArrayList();
        int pc = 0;
        if (pdd != null) {
            pc = pdd.getNumberOfPages();
            System.out.println("Number of pages: " + pc);
            PDFTextStripper pdfStripper = null;
            pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(startPageNo);
            pdfStripper.setEndPage(endPageNo);
            String parsedText = pdfStripper.getText(pdd);
//            System.out.println("parsedText-->"+parsedText);
            data.add(0, parsedText);
            data.add(1, pc);
            return data;
        } else {
            throw new Exception("PDF document NOT been captured content");
        }
    }

    private static String generateImageFromPDF(String pdffilPath, String imgFilepath) throws IOException {
        //PDDocument document = PDDocument.load(new File(filename));
        //PDFRenderer pdfRenderer = new PDFRenderer(document);
        try {
            PDDocument document = Loader.loadPDF(new File(pdffilPath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bi = pdfRenderer.renderImageWithDPI(0, 300);
            ImageIO.write(bi, "png", new File(imgFilepath));

       /* for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(
                    page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(
                    bim, String.format("src/output/pdf-%d.%s", page + 1, extension), 300);
        }*/
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            imgFilepath = null;
        }
        return imgFilepath;
    }

    public static String takeScreenshotWindow(Scenario sc, String detail) {
        String screenShotfilePath = "";
        Date dt = new Date();
        String timeStamp = new SimpleDateFormat("MMddyy_HHmmss").format(dt);
        try {
            sc.write("Screenshot--> " + detail.trim() + " [Time--> " + dt + "]");
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos1);
            byte[] screenshot = baos1.toByteArray();
            baos1.flush();
            baos1.close();
            if (base.Config.screenshotSizeRatio != 0)
                screenshot = resizeImageByte(screenshot, base.Config.screenshotSizeRatio, detail);
            sc.embed(screenshot, "image/png");  // Stick it in the report
            // if (sc.getStatus().equalsIgnoreCase("failed")) {
            screenShotfilePath = base.Config.SCREENSHOTSDIR + timeStamp + "_" + sc.getStatus() + ".png";
            InputStream is = new ByteArrayInputStream(screenshot);
            BufferedImage newBi = ImageIO.read(is);
            ImageIO.write((RenderedImage) newBi, "png", new File(screenShotfilePath));
            // }
        } catch (Exception e) {
            System.out.println("Screenshot exception : " + e.getMessage());
        }
        return screenShotfilePath;
    }

    public static String takeScreenshot(Scenario sc, WebDriver driver, String detail) {
        Date dt = new Date();
        String timeStamp = new SimpleDateFormat("MMddyy_HHmmss").format(dt);
        String screenShotfilePath = "";
        try {
            byte[] screenshot;
            if (driver == null) {
                sc.write("Driver is not available(NULL), Taking ROBOT screenshot : " + detail.trim() + " Time: " + dt);
                BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                screenshot = baos.toByteArray();
            } else {
                sc.write("Screenshot--> " + detail.trim() + " [Time--> " + dt + "]");
                //System.out.println(detail);
                screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            }
            if (Config.screenshotSizeRatio != 0)
                screenshot = resizeImageByte(screenshot, Config.screenshotSizeRatio, detail.trim());
            //LogSysOut(log, "info", "Screenshots taken for sceanrio " + sc.getName());
            sc.embed(screenshot, "image/png");  // Stick it in the report
            // if (sc.getStatus().equalsIgnoreCase("failed")) {
            ByteArrayInputStream input_stream = new ByteArrayInputStream(screenshot);
            BufferedImage final_buffered_image = ImageIO.read(input_stream);
            input_stream.close();
            screenShotfilePath = Config.SCREENSHOTSDIR + timeStamp + "_" + sc.getStatus() + ".png";
            ImageIO.write(final_buffered_image, "png", new File(screenShotfilePath));
            //}
        } catch (WebDriverException PlatformNotSupport) {
            // LogSysOut(log, "error", "Screenshot: " + PlatformNotSupport.getMessage());
            System.out.println("Screenshot exception: " + PlatformNotSupport.getMessage());
        } catch (ClassCastException cEx) {
            // cEx.printStackTrace();
            // LogSysOut(log, "error", "Screenshot: " + cEx.getMessage());
            System.out.println("Screenshot exception : " + cEx.getMessage());
        } catch (Exception e) {
            //Common.LogSysOut(log, "WARN", "Screenshot: " + e.getMessage());
            System.out.println("Screenshot exception : " + e.getMessage());
        }
        csleep(50);
        System.out.println("Screenshot Path name to report in execution file: " + screenShotfilePath);
        return screenShotfilePath;
    }

    private static byte[] resizeImageByte(byte[] img, double percentage, String detail) throws IOException {
        ByteArrayInputStream input_stream = new ByteArrayInputStream(img);
        BufferedImage imgBI = ImageIO.read(input_stream);
        int width = (int) (imgBI.getWidth() * (percentage));
        int height = (int) (imgBI.getHeight() * (percentage));
        Image tmp = imgBI.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        input_stream.close();
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        if (!detail.equalsIgnoreCase("")) {
            Graphics2D g = resized.createGraphics();
            g.setFont(new Font("verdana", Font.BOLD, 10));
            g.setColor(Color.MAGENTA);
            float x = resized.getWidth() / 6;
            float y = 30;
            // g.drawString(detail.toUpperCase(), x, 30);
            int msglength = detail.toUpperCase().length();
            if (msglength > x) {
                x = resized.getWidth() / 10;
            }
            AttributedString as1 = new AttributedString(detail.toUpperCase());
            as1.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL, 0, detail.toUpperCase().length());
            as1.addAttribute(TextAttribute.BACKGROUND, Color.CYAN);
            as1.addAttribute(TextAttribute.SIZE, 16);
            g.drawString(as1.getIterator(), x, y);
            g.dispose();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "png", baos);
        byte[] imageInByte = baos.toByteArray();
        baos.flush();
        baos.close();
        return imageInByte;
    }

    public static Properties getConfig() throws IOException {
        Properties con = new Properties();
        FileInputStream confile = new FileInputStream(configFilePath);
        con.load(confile);
        //Properties con2 = new Properties();
        //FileInputStream confile2 = new FileInputStream(appConfigFilePath);
        //con2.load(confile2);
        //con.putAll(con2);
        confile.close();
        //confile2.close();
        System.out.println("Config loaded: " + configFilePath);
        return con;
    }

    public static Properties getConfig(String filePath) throws IOException {
        Properties con = new Properties();
        FileInputStream confile = new FileInputStream(filePath);
        con.load(confile);
        //roperties con2 = new Properties();
        //FileInputStream confile2 = new FileInputStream(appConfigFilePath);
        //con2.load(confile2);
        //con.putAll(con2);
        confile.close();
        //confile2.close();
        System.out.println("Config loaded: " + filePath);
        return con;
    }

    /*	public static Properties getObject() throws IOException {

            Properties con = new Properties();
            FileInputStream confile = new FileInputStream(Config.OBJECTREPO_FILE_PATH);
            con.load(confile);
            confile.close();
            System.out.println("Config loaded: "+Config.OBJECTREPO_FILE_PATH);
            return con;

        }*/
/*	public static final String getObject(String filedName){
		try {
			Properties con = new Properties();
			FileInputStream confile = new FileInputStream(Config.OBJECTREPO_FILE_PATH);
			con.load(confile);
			confile.close();
			System.out.println("Config loaded: " + Config.OBJECTREPO_FILE_PATH);

			return con.getProperty(filedName);
		}catch(Exception e){
			return "ERROR";
		}

	}*/
    public void mkdir(String dirName) {
        try {
            System.out.println("Create directory if not exist : " + dirName);
            FileUtils.forceMkdir(new File("./" + dirName.trim()));
            //new File("./" + dirName.trim()).mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removedir(String dirName) {
        try {
            System.out.println("Directory to delete : " + dirName);
            if ((new File("./" + dirName.trim())) != null || (new File("./" + dirName.trim())).exists())
                FileUtils.deleteDirectory(new File("./" + dirName.trim()));
            csleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void LogSysOut(Logger Log, String Loglevel, String logmsg) {
        try {
            String LL = Loglevel.trim().toUpperCase();
            if (LL.equals("INFO")) {
                Log.info(logmsg);
                System.out.println(logmsg);
            } else if (LL.equals("ERROR")) {
                Log.error(logmsg);
                System.out.println(logmsg);
            } else if (LL.equals("DEBUG")) {
                Log.debug(logmsg);
                System.out.println(logmsg);
            } else if (LL.equals("WARN")) {
                Log.warn(logmsg);
                System.out.println(logmsg);
            } else {
                Log.info(logmsg);
                System.out.println(logmsg);
            }
        } catch (Exception e) {
            System.out.println("Log SKIPD");
        }
    }*/

    public static void closeExcel() {
        try {
//            LogSysOut(log, "info", "Close all the EXCEL instance if anything opened already");
            Runtime.getRuntime().exec("cmd /c taskkill /f /t /im excel.exe");
            csleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void killAllProcess(String processName) {
        try {
//            LogSysOut(log, "info", "Kill the process :" + processName);
            Process p = Runtime.getRuntime().exec("cmd /c taskkill /f /t /im " + processName);
            p.waitFor();
            csleep(1000);
//            LogSysOut(log, "info", "Killed the process: " + processName);
        } catch (Exception e) {
//            LogSysOut(log, "info", "Error running command: '" + (processName) + "'\n" + e.getMessage());
        }
    }

    public static void closeOpenBrowsers(String browsername) throws IOException {
        try {
//            LogSysOut(log, "info", "Close all the " + browsername + " instance if anything opened already");
            if (browsername.trim().equalsIgnoreCase("chrome")) {
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im chromedriver.exe");
                //Runtime.getRuntime().exec("cmd /c taskkill /f /t /im chrome.exe");
            } else if (browsername.trim().equalsIgnoreCase("edge")) {
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im msedgedriver.exe");
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im msedge.exe");
            } else if (browsername.trim().equalsIgnoreCase("ff") || browsername.trim().equalsIgnoreCase("firefox")) {
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im geckodriver.exe");
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im firefox.exe");
            }
            csleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getscenarioAttribute(Scenario scenario) {
        List<String> stepNames = CustomFormatter.stepNames;
        String[] attibutes = new String[4];
        String errmsg = "";
        long dur = 0;
        Field field = FieldUtils.getField(((ScenarioImpl) scenario).getClass(), "stepResults", true);
        field.setAccessible(true);
        int stepindex = 0;
        String failedStepName = "";
        String reason = "";
        boolean onefail = true;
        String appendsteps = "";
        try {
            ArrayList<Result> results = (ArrayList<Result>) field.get(scenario);
            System.out.println("Result size(number of sceanrio steps): " + results.size());
            for (Result result : results) {
                stepindex = stepindex + 1;
                //Sum the duration
                if (result.getDuration() != null)
                    dur = dur + result.getDuration();
                if (result.getStatus().equalsIgnoreCase("FAILED") && onefail) {
                    //System.out.println("Step status: " + result.getStatus());
                    // System.out.println("Step getError Msg : " + result.getErrorMessage());
                    if (result.getError() != null) {
                        if (!result.getError().equals("")) {
                            String[] lineerrors = result.getError().toString().split("\n");
                            //System.out.println("last line: "+lineerrors[lineerrors.length-1]);
                            reason = lineerrors[0];
                            System.out.println("Reason : " + reason);
                            int s = result.getError().getStackTrace().length;
                            //System.out.println("Result stack array size: "+s);
                            failedStepName = result.getError().getStackTrace()[s - 1].getMethodName();
                            //System.out.println("Step Name: "+result.getError().getStackTrace()[s-1].getMethodName());
                            errmsg = "[REASON] " + reason;
                            //"[STEP  ] " + failedStepName + "\n" +
                            StringBuilder lines = new StringBuilder();
                            int temp = 0;
                            StackTraceElement ste = null;
                            //for(int i=(s-1);i<s;i--) {
                            for (int i = 0; i < s; i++) {
                                if (i < 4) {
                                    ste = result.getError().getStackTrace()[s - 1 - i];
                                    lines.append(ste.getClassName()).append(".").append(ste.getMethodName()).append(".").append("(").append(ste.getFileName()).append(":").append(ste.getLineNumber()).append(")").append("\n");
                                }
                            }
                            //failedStepName=lineerrors[lineerrors.length-1].replaceFirst("at","").replaceFirst("/*.","").trim();
                            //failedStepName=""+stepindex;
                            //System.out.println("Formed line: "+lines);
                            errmsg = errmsg + "\n" + "[LOG] " + lines;
                            //System.out.println("Total error msg: "+errmsg);
                            //errmsg = "[step ]--> " + failedStepName + "\n" +"[Detail]--> " + result.getErrorMessage() + "\n" + errmsg;
                        }
                    }
                    onefail = false;
                }
            }
            if (stepNames != null) {
                String temp;
                boolean flag = false;
                for (int i = 0; i < stepNames.size(); i++) {
                    if (failedStepName.trim().endsWith(stepNames.get(i).trim())) {
                        temp = "Step " + (i + 1) + "." + stepNames.get(i) + " ... " + "[FAILED]";
                        flag = true;
                    } else {
                        if (!flag) {
                            temp = "Step " + (i + 1) + "." + stepNames.get(i) + " ... " + "[PASSED]";
                        } else {
                            temp = "Step " + (i + 1) + "." + stepNames.get(i) + " ... " + "[SKIPPED]";
                        }
                    }
                    stepNames.set(i, temp);
                    appendsteps = appendsteps + temp + "\n";
                }
                System.out.println("------- steps array with results ------");
                //stepNames.forEach(System.out::println);
                //System.out.println("Steps detail: "+appendsteps);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            LogSysOut(log, "error", "---> Error while read cucumber result :" + e.getMessage());
            errmsg = e.getMessage();
        }
        //System.out.println(errmsg);
        String durStr = String.valueOf(Double.valueOf((new DecimalFormat("#.##")).format(dur / 1000000000)));
        attibutes[0] = errmsg;
        attibutes[1] = durStr;
        attibutes[2] = failedStepName;
        attibutes[3] = appendsteps;
        return attibutes;
    }

/*    public static String scenarioFailReason(Scenario scenario) {
        StringBuilder errmsg = new StringBuilder();
        Field field = FieldUtils.getField(((ScenarioImpl) scenario).getClass(), "stepResults", true);
        field.setAccessible(true);
        int stepindex = 0;
        try {
            ArrayList<Result> results = (ArrayList<Result>) field.get(scenario);
            System.out.println("Result size: " + results.size());
            for (Result result : results) {
                stepindex = stepindex + 1;
                if (result.getStatus().equalsIgnoreCase("FAILED")) {
                    //System.out.println("Step status: " + result.getStatus());
                    // System.out.println("Step getError Msg : " + result.getErrorMessage());

                    if (result.getError() != null) {
                        if (!result.getError().equals(""))
                            errmsg.insert(0, "[step #]--> " + stepindex + "\n" +
                                    "[Detail]--> " + result.getErrorMessage() + "\n");
                    }
                }

            }
        } catch (Exception e) {
            //LogSysOut(log, "error", "Error while logging error:" + e.getMessage());
            System.out.println("Error while logging error:" + e.getMessage());
            errmsg = new StringBuilder(e.getMessage());
        }
        return errmsg.toString();
    }*/

/*    public String getTCIDofScenario(Scenario sc) {
        String tagname = "";
        boolean tcTag = false;
        int matchTag = 0;
        String scDataTag = "";
        LogSysOut(log, "info", "Scenario Data Tag Size : " + sc.getSourceTagNames().size());
        for (int i = 0; i < sc.getSourceTagNames().size(); i++) {

            tagname = sc.getSourceTagNames().toArray()[i].toString();
            tcTag = tagname.trim().toUpperCase().startsWith("@TC");
            if (tcTag) {
                matchTag = i + 1;
                scDataTag = tagname.replace("@", "");
                Common.LogSysOut(log, "info", "Found tc tag @ " + matchTag + " st/nd/th/rd position tag as TCID: " + scDataTag + "  for Scenario: \"" + sc.getName() + "\"");
                break;
            }


        }
        if (!tcTag) {
            Common.LogSysOut(log, "WARN", "No TAG FOUND with TCID, please assing the Test Case ID for Scenario : " + sc.getName());
        }
        return scDataTag;
    }*/

 /*   public List<HashMap<String, String>> getDataFromSheet(Scenario sc) throws IOException {
        List<HashMap<String, String>> scenarioData = null;
        FileInputStream fis = new FileInputStream(new File(Config.DATATABLE_FILE_PATH));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet(Config.DATATABLE_MAPSHEET_NAME);
        String sheetName = null;


        for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
            XSSFRow row = sheet.getRow(rowNumber);
            XSSFCell scNameFromMapSheet = row.getCell(1);
            XSSFCell tcSheetNameFromMapSheet = row.getCell(2);
            //LogSysOut(log,"INFO","Scenario name to match in datasheet: "+sc.getName());
            if (scNameFromMapSheet.getStringCellValue().trim().equalsIgnoreCase(sc.getName().trim())) {
                // scenarionNo = (int) sheet.getRow(rowNumber).getCell(1).getNumericCellValue();
                System.out.println("TCMap cell value for scenario name: " + scNameFromMapSheet.getStringCellValue());
                System.out.println("Found DataSheet name is: " + stringCellValueTCMAP(tcSheetNameFromMapSheet));
                sheetName = stringCellValueTCMAP(tcSheetNameFromMapSheet);
                System.out.println("DataSheet name is: " + sheetName);
                break;
            }

        }


        if (existSheetName(workbook, sheetName)) {

            XSSFSheet sheet1 = workbook.getSheet(sheetName);
            scenarioData = data(sheet1);
        }
        workbook.close();
        fis.close();
        return scenarioData;
        //return dataMap;
    }*/

    public HashMap<String, String> getTCSheetsMapFromTP(String[] tagsArray) throws Exception {
        HashMap<String, String> tcsMap = new HashMap<>();
        System.out.println("Total Tags from tagArray " + tagsArray.length);
        ZipSecureFile.setMinInflateRatio(0);
        FileInputStream fis = new FileInputStream(new File(Config.DATATABLE_FILE_PATH));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet(Config.DATATABLE_MAPSHEET_NAME);
        System.out.println("Last row number in the TestData: " + sheet.getLastRowNum());
        String sheetName = "";
        if (tagsArray == null || tagsArray.length == 0) {
            throw new Exception("There are NO test cases(TAGS) provided to run, please check the TP or user parameter for tags");
        } else {
            for (int a = 0; a < tagsArray.length; a++) {
                System.out.println("Check for Map Data sheet name for Tag: " + tagsArray[a]);
                boolean matchTC = false;
                for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
                    //System.out.println("TestData row# " + rowNumber);
                    XSSFRow row = sheet.getRow(rowNumber);
                    //System.out.println("TestData row# data:  " + row.getCell(0));
                    XSSFCell tcIDFromMapSheet;
                    String tcIDString;
                    try {
                        tcIDString = stringCellValue(row.getCell(0)).split("\\.")[0].trim();
                        //System.out.println("tag: "+tagsArray[a]+" Cell: "+tcIDString);
                        if (tcIDString.equalsIgnoreCase(tagsArray[a].trim())) {
                            // scenarionNo = (int) sheet.getRow(rowNumber).getCell(1).getNumericCellValue();
                            System.out.println("TCID found in data MAP sheet in  Row#: " + rowNumber + " For TCID(cellvalue): " + tcIDString);
                            XSSFCell tcSheetNameFromMapSheet = row.getCell(2);
                            sheetName = stringCellValueTCMAP(tcSheetNameFromMapSheet);
                            //System.out.println("DataSheet name is: " + sheetName);
                            if (sheetName.trim().equalsIgnoreCase("")) {
                                System.out.println("TCID: [" + tcIDString + "]  is in datasheet but NO mapping sheet name in the column, Please check and update the data table mapping");
                                //throw new Exception("TCID is in datasheet but no mapping sheet, Please check and update the data table mapping");
                            }
                            tcsMap.put(tcIDString, sheetName);
                            matchTC = true;
                            break;
                        }
                    } catch (NullPointerException ee) {
                        System.out.println("TestData Sheet has Null row or Data - NULL row while check TC map at sheet: " + sheetName + " Row# " + rowNumber);
                        sheetName = null;
                        System.out.println("Exception: " + ee.getMessage());
                        break;
                    }
                }
                if (matchTC) {
//                    System.out.println("TC Match found and added into HashMap: " + tagsArray[a]);
                } else {
                    System.out.println("TC Match NOT found due to test case not required data from data table or missing to add TC in data table: " + tagsArray[a]);
                }
            }
        }
        workbook.close();
        fis.close();
        String emptyMap = "";
        for (Map.Entry<String, String> entry : tcsMap.entrySet()) {
            //System.out.println(entry.getKey() + " : " + entry.getValue());
            if (entry.getValue().equalsIgnoreCase("")) {
                emptyMap = emptyMap + "," + entry.getKey();
            }
        }
        ;
        if (emptyMap.startsWith(","))
            emptyMap.replaceFirst(",", "");
        if (!emptyMap.equalsIgnoreCase(""))
            Assert.fail("These TCIDs: " + emptyMap + " are in datasheet but DO NOT have mapping sheet name in the column, Please check and update the data table mapping and rerun");
        return tcsMap;
        //return dataMap;
    }

    public HashMap<String, String> getTCSheetsMap() throws Exception {
        HashMap<String, String> tcsMap = new HashMap<>();
        FileInputStream fis = new FileInputStream(new File(Config.DATATABLE_FILE_PATH));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet(Config.DATATABLE_MAPSHEET_NAME);
        System.out.println("Last row number in the TestData: " + sheet.getLastRowNum());
        String sheetName = "";
        for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
            //System.out.println("TestData row# " + rowNumber);
            XSSFRow row = sheet.getRow(rowNumber);
            //System.out.println("TestData row# data:  " + row.getCell(0));
            XSSFCell tcIDFromMapSheet;
            String tcIDString;
            try {
                tcIDString = stringCellValue(row.getCell(0)).split("\\.")[0].trim();
                //System.out.println("TCID found in data MAP sheet in  Row#: " + rowNumber + " For TCID(cellvalue): " + tcIDString);
                XSSFCell tcSheetNameFromMapSheet = row.getCell(2);
                sheetName = stringCellValueTCMAP(tcSheetNameFromMapSheet);
                //System.out.println("DataSheet name is: " + sheetName);
                if (sheetName.trim().equalsIgnoreCase("")) {
                    System.out.println("TCID: [" + tcIDString + "]  in datasheet has NO mapping sheet name in the column, Please check and update the data table mapping");
                    //throw new Exception("TCID is in datasheet but no mapping sheet, Please check and update the data table mapping");
                }
                tcsMap.put(tcIDString, sheetName);
            } catch (NullPointerException ee) {
                System.out.println("TestData Sheet has Null row or Data - NULL row while check TC map at sheet: " + sheetName + " Row# " + rowNumber);
                sheetName = null;
                System.out.println("Exception: " + ee.getMessage());
                break;
            }
        }
        workbook.close();
        fis.close();
        String emptyMap = "";
        for (Map.Entry<String, String> entry : tcsMap.entrySet()) {
            // System.out.println(entry.getKey() + " : " + entry.getValue());
            if (entry.getValue().equalsIgnoreCase("")) {
                emptyMap = emptyMap + "," + entry.getKey();
            }
        }
        ;
        if (emptyMap.startsWith(","))
            emptyMap.replaceFirst(",", "");
        if (!emptyMap.equalsIgnoreCase(""))
            Assert.fail("These TCIDs: " + emptyMap + " are in datasheet but DO NOT have mapping sheet name in the column, Please check and update the data table mapping and rerun");
        return tcsMap;
        //return dataMap;
    }

    /*public List<HashMap<String, String>> getDataFromSheet(String testCaseID) throws IOException {
       List<HashMap<String, String>> tcData = null;
       FileInputStream fis = new FileInputStream(new File(Config.DATATABLE_FILE_PATH));
       XSSFWorkbook workbook = new XSSFWorkbook(fis);
       XSSFSheet sheet = workbook.getSheet(Config.DATATABLE_MAPSHEET_NAME);
       String sheetName = null;
       System.out.println("Check the map for TCID: " + testCaseID);
       System.out.println("Last row number: " + sheet.getLastRowNum());

       for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
           //System.out.println("TestData row# " + rowNumber);
           XSSFRow row = sheet.getRow(rowNumber);
           //System.out.println("TestData row# data:  " + row.getCell(0));
           XSSFCell tcIDFromMapSheet;
           try{
               tcIDFromMapSheet = row.getCell(0);
           }catch(NullPointerException ne){
               tcIDFromMapSheet=null;
           }


           String tcIDString = stringCellValue(tcIDFromMapSheet).split("\\.")[0];
           //LogSysOut(log,"INFO","Scenario name to match in datasheet: "+sc.getName());
           try {
               if (tcIDString.equalsIgnoreCase(testCaseID.trim())) {
                   // scenarionNo = (int) sheet.getRow(rowNumber).getCell(1).getNumericCellValue();
                   System.out.println("TCID found in data MAP sheet in  Row#: "+rowNumber+" For TCID(cellvalue): "+ tcIDString);
                   XSSFCell tcSheetNameFromMapSheet = row.getCell(2);
                   sheetName = stringCellValueTCMAP(tcSheetNameFromMapSheet);
                   System.out.println("DataSheet name is: " + sheetName);
                   break;
               }
           } catch (NullPointerException ee) {
               sheetName = null;
               System.out.println("getDataFromSheet HashMapping: No Data - NULL");
               break;
           }

       }


       if (sheetName != null && existSheetName(workbook, sheetName)) {

           XSSFSheet sheet1 = workbook.getSheet(sheetName);
           tcData = data(sheet1);
           System.out.println("getDataFromSheet HashMapping: Number of rows for iteration: [" + tcData.size() + " ]");
       } else {
           tcData = null;
           System.out.println("getDataFromSheet HashMapping: No Data from sheet - NULL");
       }
       workbook.close();
       fis.close();

       return tcData;
       //return dataMap;
   }
*/
    public List<HashMap<String, String>> getDataFromSheet(String testDataSheetName) throws IOException {
        List<HashMap<String, String>> tcData = null;
        FileInputStream fis = new FileInputStream(new File(Config.DATATABLE_FILE_PATH));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        //XSSFSheet sheet = workbook.getSheet(Config.DATATABLE_MAPSHEET_NAME);
        //String sheetName = null;
        System.out.println("TestCase Mapped sheetname: " + testDataSheetName);
        //System.out.println("Last row number: " + sheet.getLastRowNum());
        if (testDataSheetName != null) {
            if (existSheetName(workbook, testDataSheetName)) {
                XSSFSheet sheet1 = workbook.getSheet(testDataSheetName);
                tcData = data(sheet1);
                System.out.println("TestCase Mapped sheet: Number of rows for iteration: [" + (tcData.size() - 1) + " ]");
                //System.out.println("Test Data COlumn sin teh sheet: "+tcData.get(0));
            } else {
                Assert.fail("Mapped data sheet [" + testDataSheetName + "] is NOT available or missing ");
            }
        } else {
            tcData = null;
            System.out.println("Datasheet not required or No Data from sheet - NULL");
        }
        workbook.close();
        fis.close();
        return tcData;
        //return dataMap;
    }

    public void moveToArchiveExecutionSummary() {
        System.out.println("*****START method moveToArchiveExecutionSummary()******");
        try {
            File summaryReport = new File(Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
            System.out.println("Summary report excel if already exist : " + summaryReport.getPath());
            if (summaryReport.exists()) {
                File archiveDir = new File(summaryReport.getParent() + "/archives");
                archiveDir.mkdir();
                System.out.println("Archive folder created if not exsit: " + archiveDir.getPath());
                //System.out.println(summaryReport.lastModified());
                String timeStamp = new SimpleDateFormat("MMddyy_HHmmss").format(new Date(summaryReport.lastModified()));
                summaryReport.renameTo(new File(archiveDir.getPath() + "/" + timeStamp + "_" + summaryReport.getName()));
                //System.out.println(summaryReport.getPath());
                summaryReport.delete();
                csleep(2000);
            }
        } catch (Exception e) {
            System.out.println(e.getCause().toString());
            e.printStackTrace();
        }
        System.out.println("*****END method moveToArchiveExecutionSummary()******");
    }

    public static void createExcelResultTemplate() throws Exception {
        closeExcel();
        InputStream sourceLocation = Common.class.getResourceAsStream("/resultTemplate.xlsx");
        File targetLocation = new File(Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
        System.out.println("Source central excel template name: " + sourceLocation);
        System.out.println("File to copy path: " + targetLocation.getAbsolutePath());
        FileUtils.copyInputStreamToFile(sourceLocation, targetLocation);
        try {
            sourceLocation.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Successfully created excel execution result from Template :" + targetLocation.getPath());
    }

    public static void copyFile(String sourceFromFilePath, String targetTofilePath) {
        try {
            File sourceFile = new File(sourceFromFilePath);
            System.out.println("File to copy : " + sourceFromFilePath);
            if (sourceFile.exists()) {
                //sourceFile.renameTo(new File(targetTofilePath));
                FileUtils.copyFile(sourceFile, new File(targetTofilePath));
                //FileUtils.copyFileToDirectory(sourceFile, to)
                csleep(2000);
                System.out.println("File successfully copied to : " + targetTofilePath);
            }
        } catch (Exception e) {
            System.out.println("Issue when copy the file: " + e.getCause().toString());
        }
    }

    public static void deleteFile(String filePath) {
        try {
            System.out.println("Trying to delete file if exist :" + filePath);
            File f = new File(filePath);
            if (f.exists() && f.isFile()) {
                f.delete();
                System.out.println("Successfully Deleted file :" + filePath);
                csleep(2000);
            }
        } catch (Exception e) {
            System.out.println("Error on delete file :" + filePath + " : " + e.getMessage());
        }
    }

    public static void createCSVResultTemplate() throws Exception {
        //closeExcel();
        InputStream sourceLocation = Common.class.getResourceAsStream("/templates/resultTemplate.csv");
        File targetLocation = new File(Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
        System.out.println("Source central excel template name: " + sourceLocation);
        System.out.println("File to copy path: " + targetLocation.getAbsolutePath());
        FileUtils.copyInputStreamToFile(sourceLocation, targetLocation);
        try {
            sourceLocation.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Sccessfully created csv execution result from Template :" + targetLocation.getPath());
    }

    public static void createCSVResultTemplate(String filePath) throws Exception {
        //closeExcel();
        InputStream sourceLocation = Common.class.getResourceAsStream("/templates/resultTemplate.csv");
        File targetLocation = new File(filePath);
        System.out.println("Source central excel template name: " + sourceLocation);
        System.out.println("File to copy path: " + targetLocation.getAbsolutePath());
        FileUtils.copyInputStreamToFile(sourceLocation, targetLocation);
        try {
            sourceLocation.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Sccessfully created csv execution result from Template :" + targetLocation.getPath());
    }

    public void csvWriterFromArrayList(ArrayList<String[]> alData, String csvFilePath) throws IOException {
        CSVWriter writer = null;
        writer = new CSVWriter(new FileWriter(csvFilePath, true));
        for (String[] a : alData) {
            writer.writeNext(a);
        }
        writer.close();
    }

    public void csvWriterFromArrayList1(String[] alData, String csvFilePath) throws IOException {
        CSVWriter writer = null;
        writer = new CSVWriter(new FileWriter(csvFilePath, true));
        writer.writeNext(alData);
        writer.close();
    }

 /*   public void csvWriterFromArrayList(String filePath,String rowkey,String columnHeadername) throws FileNotFoundException {
           CSVReader cvr=new CSVReader(new FileReader(filePath));
           //cvr.getParser().

        CSVParser csvp=new CSVParser(new FileReader(filePath),CSVFormat.DEFAULT.withDelimiter(',').withHeader().)
             try(
                    BufferedReader br = new BufferedReader(new FileReader(filePath));

                    CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withHeader().parse(new FileReader(filePath));
            ) {

                for(CSVRecord record : parser) {
                    if(record.)
                    System.out.println(record.get(columnHeadername));
                }
            } catch (Exception e) {
                System.out.println(e);
            }

      }*/

    public void updateResultInExecutionSummaryReport(XSSFSheet sheet, String[] resultData) throws IOException {
        //FileInputStream fis = new FileInputStream(ExcelReportPath);
        //XSSFWorkbook workbook = new XSSFWorkbook(fis);
        //Sheet sheet = workbook.getSheetAt(0);
        //CreationHelper createHelper = workbook.getCreationHelper();
        //Create Cell Style for formatting Date
        //CellStyle dateCellStyle = workbook.createCellStyle();
        //dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd HH:mm:ss"));
        int rowCount = sheet.getLastRowNum();
        XSSFRow row = sheet.createRow(++rowCount);
        for (int i = 0; i < resultData.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(resultData[i]);
            cell.getCellStyle().setWrapText(true);
            //sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, 20);
        }
    }

    public void updateResultInExecutionSummaryReport(File ExcelReportPath, String[] resultData) throws IOException {
        FileInputStream fis = new FileInputStream(ExcelReportPath);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFCreationHelper createHelper = workbook.getCreationHelper();
        // Create Cell Style for formatting Date
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd HH:mm:ss"));
        int rowCount = sheet.getLastRowNum();
        XSSFRow row = sheet.createRow(++rowCount);
        for (int i = 0; i < resultData.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(resultData[i]);
            sheet.autoSizeColumn(i);
        }
        // Write the output to a file
        fis.close();
        FileOutputStream fos = new FileOutputStream(ExcelReportPath);
        workbook.write(fos);
        fos.close();
        // Closing the workbook
        workbook.close();
    }

    public void updateResultInExecutionSummaryReport(File ExcelReportPath, ArrayList<String[]> resultData) throws IOException {
        FileInputStream fis = new FileInputStream(ExcelReportPath);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFCreationHelper createHelper = workbook.getCreationHelper();
        // Create Cell Style for formatting Date
        //XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        //dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd HH:mm:ss"));
        //int rowCount = sheet.getLastRowNum();
        int rowCount = sheet.getLastRowNum() + 1;
        //Row row = sheet.createRow(++rowCount);
        for (int a = 0; a < resultData.size(); a++) {
            //int rowCount = sheet.getLastRowNum()+1;
            XSSFRow row = sheet.createRow(rowCount);
            for (int i = 0; i < resultData.get(a).length; i++) {
                XSSFCell cell = row.createCell(i);
                cell.setCellValue(resultData.get(a)[i]);
                sheet.autoSizeColumn(i);
            }
            rowCount++;
        }
        // Write the output to a file
        fis.close();
        FileOutputStream fos = new FileOutputStream(ExcelReportPath);
        workbook.write(fos);
        fos.close();
        // Closing the workbook
        workbook.close();
    }

    public static List<HashMap<String, String>> dataOLD(XSSFSheet sheet) {
        List<HashMap<String, String>> mydata = new ArrayList<>();
        String currentValuInString = "";
        try {
            XSSFRow HeaderRow = sheet.getRow(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow currentRow = sheet.getRow(i);
                HashMap<String, String> currentHash = new HashMap<String, String>();
                for (int j = 0; j < currentRow.getPhysicalNumberOfCells(); j++) {
                    XSSFCell currentCell = currentRow.getCell(j);
                    currentValuInString = stringCellValue(currentCell);
                    currentHash.put(HeaderRow.getCell(j).getStringCellValue(), currentValuInString);
                }
                mydata.add(currentHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Data Map from sheet" + "[" + sheet.getSheetName() + "]");
        //System.out.println("Data Map from sheet" + "[" + sheet.getSheetName() + "]" + " : " + mydata);
        return mydata;
    }

    public static List<HashMap<String, String>> data(XSSFSheet sheet) {
        List<HashMap<String, String>> mydata = new ArrayList<>();
        String currentValuInString = "";
        try {
            XSSFRow HeaderRow = sheet.getRow(0);
            mydata.add(null); //Intentionally adding null hashmap for the 0th index in the list
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow currentRow = sheet.getRow(i);
                HashMap<String, String> currentHash = new HashMap<String, String>();
                for (int j = 0; j < currentRow.getPhysicalNumberOfCells(); j++) {
                    XSSFCell currentCell = currentRow.getCell(j);
                    currentValuInString = stringCellValue(currentCell);
                    currentHash.put(HeaderRow.getCell(j).getStringCellValue(), currentValuInString);
                }
                mydata.add(currentHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Data Map from sheet" + "[" + sheet.getSheetName() + "]");
        //System.out.println("Data Map from sheet" + "[" + sheet.getSheetName() + "]" + " : " + mydata);
        return mydata;
    }

    public static List<HashMap<String, String>> getDataFromSheetWithFilter(XSSFSheet sheet, int filterColNum, String filterValue) {
        List<HashMap<String, String>> mydata = new ArrayList<>();
        String currentValuInString = "";
        try {
            XSSFRow HeaderRow = sheet.getRow(0);
            //HashMap<String,String> currentHash = new HashMap<String,String>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow currentRow = sheet.getRow(i);
                //HashMap<String,String> currentHash = new HashMap<String,String>();
                System.out.println("filtervalue: " + currentRow.getCell(filterColNum));
                if (currentRow.getCell(filterColNum).toString().trim().equalsIgnoreCase(filterValue.trim())) {
                    HashMap<String, String> currentHash = new HashMap<String, String>();
                    for (int j = 0; j < currentRow.getPhysicalNumberOfCells(); j++) {
                        XSSFCell currentCell = currentRow.getCell(j);
                        currentValuInString = stringCellValue(currentCell);
                        System.out.println("hasmap header: " + HeaderRow.getCell(j).getStringCellValue() + "  value: " + currentValuInString);
                        currentHash.put(HeaderRow.getCell(j).getStringCellValue(), currentValuInString);
                    }
                    mydata.add(currentHash);
                }
                //mydata.add(currentHash);
            }
            //mydata.add(currentHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("getDataFromSheetWithFilter Mappings are: " + mydata);
        return mydata;
    }

    public static List<HashMap<String, String>> getDataFromSheetWithFilter(XSSFSheet sheet, String filterColName, String filterValue) {
        List<HashMap<String, String>> mydata = new ArrayList<>();
        String currentValuInString = "";
        try {
            int filterColNum = 0;
            XSSFRow HeaderRow = sheet.getRow(0);
            for (int k = 0; k < HeaderRow.getPhysicalNumberOfCells(); k++) {
                if (HeaderRow.getCell(k).toString().trim().equalsIgnoreCase(filterColName.trim())) {
                    filterColNum = k;
                    break;
                }
            }
            //HashMap<String,String> currentHash = new HashMap<String,String>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow currentRow = sheet.getRow(i);
                //HashMap<String,String> currentHash = new HashMap<String,String>();
                System.out.println("filtervalue: " + currentRow.getCell(filterColNum));
                if (currentRow.getCell(filterColNum).toString().trim().equalsIgnoreCase(filterValue.trim())) {
                    HashMap<String, String> currentHash = new HashMap<String, String>();
                    for (int j = 0; j < currentRow.getPhysicalNumberOfCells(); j++) {
                        XSSFCell currentCell = currentRow.getCell(j);
                        currentValuInString = stringCellValue(currentCell);
                        System.out.println("hasmap header: " + HeaderRow.getCell(j).getStringCellValue() + "  value: " + currentValuInString);
                        currentHash.put(HeaderRow.getCell(j).getStringCellValue(), currentValuInString);
                    }
                    mydata.add(currentHash);
                }
                //mydata.add(currentHash);
            }
            //mydata.add(currentHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("getDataFromSheetWithFilter Mappings are: " + mydata);
        return mydata;
    }

/*    public Object[][] getIterativeRunData() throws IOException, InvalidFormatException {
        ArrayList al;
        Object[][] runData = null;
        String yesRun = "";
        int xRun = 0;
        List<HashMap<String, String>> selectedTCData = new ArrayList<>();
        XSSFWorkbook tPworkbook = new XSSFWorkbook(new File(Config.TESTPLAN_FILE_PATH));
        XSSFSheet tcRunsheet = tPworkbook.getSheet(Config.RUNSHEET_FROM_TESTPLAN);//getSheetAt(0);
        //selectedTCData=getDataFromSheetWithFilter(tcRunsheet,3,"yes");
        selectedTCData = getDataFromSheetWithFilter(tcRunsheet, "SelectToRun", "yes");
        xRun = selectedTCData.size();
        System.out.println("selectedTCData count: " + xRun + " " + selectedTCData.get(0).get("Test/ScenarioID"));
        int arrayLimit = 0;
        for (int x = 0; x < xRun; x++) {
            int a;
            if (getDataFromSheet(selectedTCData.get(x).get("Test/ScenarioID")) == null) {
                a = 1;
            } else {
                a = getDataFromSheet(selectedTCData.get(x).get("Test/ScenarioID")).size();
            }

            arrayLimit = arrayLimit + a;
        }
        System.out.println("Array limit: " + arrayLimit);
        runData = new Object[arrayLimit][5];

        int iteration = 0;
        for (int x = 0; x < xRun; x++) {
            System.out.println("getData from hashmap: " + x + " " + selectedTCData.get(x).get("Test/ScenarioID"));
            List<HashMap<String, String>> testData = getDataFromSheet(selectedTCData.get(x).get("Test/ScenarioID"));
            //System.out.println("getData from hashmap: "+x+" "+selectedTCData.get(x).get("Test/ScenarioID"));
            //List<HashMap<String, String>> testData=getDataFromSheet("JUSATC2");
            if (testData == null) {
                runData[iteration][0] = selectedTCData.get(x).get("Test/ScenarioID");
                runData[iteration][1] = selectedTCData.get(x).get("Test Method Name");
                runData[iteration][2] = selectedTCData.get(x).get("Manual Test Case Ref/Detail");
                runData[iteration][3] = null;
                runData[iteration][4] = 0;
                iteration++;
            } else {
                int a = testData.size();
                System.out.println("testdata loop: " + a);
                for (int y = 0; y < a; y++) {
                    runData[iteration][0] = selectedTCData.get(x).get("Test/ScenarioID");
                    //System.out.println("iteration data: " + runData[iteration][0]);
                    runData[iteration][1] = selectedTCData.get(x).get("Test Method Name");
                    runData[iteration][2] = selectedTCData.get(x).get("Manual Test Case Ref/Detail");
                    runData[iteration][3] = testData;
                    runData[iteration][4] = y;
                    iteration++;
                }
            }
        }
        tPworkbook.close();
        return runData;
    }*/

    public static List<String> getAlldataFromColumn(String ExcelPath, String sheetName, String filterColHeader) throws IOException, InvalidFormatException {
        FileInputStream filestream = new FileInputStream(new File(ExcelPath));
        XSSFWorkbook workbook = new XSSFWorkbook(filestream);
        XSSFSheet sheet = workbook.getSheet(sheetName);//getSheetAt(0);
        List<String> columnData = new ArrayList<String>();
        String isSelectToRun = "";
        int filterColIndex = -1;
/*		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			if (stringCellValue(sheet.getRow(0).getCell(i)).trim().equalsIgnoreCase(filter)) {
				filterRowIndex = i;
				break;
			}
		}*/
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            if (stringCellValue(sheet.getRow(0).getCell(i)).trim().equalsIgnoreCase(filterColHeader)) {
                filterColIndex = i;
                break;
            }
        }
        System.out.println("Column Header [" + filterColHeader + "] found at the column # " + filterColIndex);
        try {
            int rowsCount = sheet.getPhysicalNumberOfRows();
            int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
            System.out.println("Row count: " + rowsCount + " Column Count :" + columnCount);
            //for (int i = 1; i < rowsCount; i++) {
            for (Row r : sheet) {
                //XSSFRow currentRow = sheet.getRow(i);
                isSelectToRun = stringCellValue((XSSFCell) r.getCell(filterColIndex));
                if (isSelectToRun.equalsIgnoreCase("Yes")) {
                    System.out.println("Selected sceanrio in test plan: " + stringCellValue((XSSFCell) r.getCell(0)));
                    columnData.add(stringCellValue((XSSFCell) r.getCell(0)));
                }
            }
            //workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        workbook.close();
        filestream.close();
        return columnData;
    }

    private boolean existSheetName(XSSFWorkbook workbook, String sheetname) {
        boolean sheetExist = false;
        if (workbook.getNumberOfSheets() != 0) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (workbook.getSheetName(i).equals(sheetname)) {
                    sheetExist = true;
                    break;
                }
            }
            if (!sheetExist) {
                System.out.println("WARN: NO sheets with name : " + sheetname + " in the workbook : " + workbook);
            }
        } else {
            System.out.println("WARN: 0 sheets in the workbook : " + workbook);
        }
        return sheetExist;
    }

    private static String stringCellValueTCMAP(XSSFCell cell) throws IOException {
        String strValueofCell = "";
        strValueofCell = stringCellValue(cell);
        if (strValueofCell.contains(".")) {
            strValueofCell = strValueofCell.substring(0, strValueofCell.indexOf("."));
        }
        return strValueofCell;
    }

    private static String stringCellValue(XSSFCell cell) throws IOException {
        //System.out.println("Read the cell data format and convert and return the value with STRING format **** ");
        String strValueofCell = "";
        if (cell == null) {
            strValueofCell = "";
        }
        //LogSysOut(log,"INFO","Cell value before convert: "+cell.toString());
        CellType cellType = cell.getCellType();
        //LogSysOut(log,"INFO","Cell Type: "+cellType);
        switch (cellType) {
            case BLANK:
                strValueofCell = "";
                break;
            case BOOLEAN:
                strValueofCell = Boolean.toString(cell.getBooleanCellValue());
                break;
            case ERROR:
                strValueofCell = "*error*";
                break;
            case NUMERIC:
                strValueofCell = String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
                strValueofCell = cell.getStringCellValue();
                break;
            default:
                strValueofCell = cell.toString();
                break;
        }
        //LogSysOut(log,"INFO","Cell value After convert: "+strValueofCell);
        return strValueofCell;
    }

    public static String getTagsFromTestPlan(String testPlanPath, String sheetName) throws IOException, InvalidFormatException {
        List<String> runTagsFromTestPlan;
        StringBuilder tagString = new StringBuilder();
        runTagsFromTestPlan = getAlldataFromColumn(testPlanPath, sheetName, "Run");
        //String prefix = "--tags ";
        int countTag = runTagsFromTestPlan.size();
        System.out.println("---> Total selected sceanrios to add to tag: " + countTag);
        String temp = "";
        if (countTag == 1) {
            //System.out.println(runTagsFromTestPlan.get(0));
            //System.out.println(runTagsFromTestPlan.get(0).split("\\.")[0]);
            //tagString = new StringBuilder("@" + runTagsFromTestPlan.get(0).split("\\.")[0]);
            tagString = new StringBuilder("@" + runTagsFromTestPlan.get(0).split("\\.")[0].trim());
        } else {
            for (int i = 0; i < countTag; i++) {
                if (i != (countTag - 1))
                    tagString.append("@").append(runTagsFromTestPlan.get(i).split("\\.")[0].trim()).append(",");
                else
                    tagString.append("@").append(runTagsFromTestPlan.get(i).split("\\.")[0].trim());
            }
        }
        //tagString.insert(0, prefix);
        return tagString.toString().trim();
    }

/*    public static String getTagsFromTestPlan(String testPlanPath, String sheetName) throws IOException, InvalidFormatException {
        List<String> runTagsFromTestPlan;
        StringBuilder tagString = new StringBuilder();
        runTagsFromTestPlan = getAlldataFromColumn(testPlanPath, sheetName, "Run");
        String prefix = "--tags ";
        int countTag = runTagsFromTestPlan.size();
        System.out.println("---> Total selected sceanrios to add to tag: " + countTag);
        String temp = "";
        if (countTag == 1) {
            //System.out.println(runTagsFromTestPlan.get(0));
            //System.out.println(runTagsFromTestPlan.get(0).split("\\.")[0]);
            //tagString = new StringBuilder("@" + runTagsFromTestPlan.get(0).split("\\.")[0]);
            tagString = new StringBuilder("@" + runTagsFromTestPlan.get(0).trim());
        } else {
            for (int i = 0; i < countTag; i++) {
                if (i != (countTag - 1))
                    tagString.append("@").append(runTagsFromTestPlan.get(i).trim()).append(",");
                    //tagString.append("@").append(runTagsFromTestPlan.get(i).split("\\.")[0]).append(",");
                else
                    tagString.append("@").append(runTagsFromTestPlan.get(i).trim());
                //tagString.append("@").append(runTagsFromTestPlan.get(i).split("\\.")[0]);
            }
        }
        tagString.insert(0, prefix);
        return tagString.toString();
    }*/

    public static String getTags(String runFromTP, String tagsFromUser, String testPlanNameFromUser, String tagsFromConfigFile) throws IOException, InvalidFormatException {
        String tcTags;
        if (tagsFromUser != null && !tagsFromUser.equalsIgnoreCase("")) {
           /* System.out.println("User provided tags -->" + tagsFromUser);
            if (tagsFromUser.trim().startsWith(",")) {
                tagsFromUser = tagsFromUser.trim().replaceFirst(",", "");
            } else if (tagsFromUser.trim().endsWith(",")) {
                tagsFromUser = tagsFromUser.trim() + "###";
                tagsFromUser = tagsFromUser.replace(",###", "");

            }
            tagsFromUser = tagsFromUser.trim().replaceAll("@", "").replaceAll(" ", "");
            tagsFromUser = ("@" + tagsFromUser).replaceAll(",", ",@"); // Ex: @1234, @4567
            System.out.println("---> Formated user tags -->" + tagsFromUser);
            //tcTags="--tags "+tags;
            tcTags = tagsFromUser;*/
            tcTags = getTagsFormatted(tagsFromUser);
        } else {
            if (runFromTP.equalsIgnoreCase("yes") || runFromTP.equalsIgnoreCase("Y")) {
                System.out.println("---> RunFromTP option = Yes");
                if (!testPlanNameFromUser.isEmpty() && !testPlanNameFromUser.trim().equalsIgnoreCase("")) {
                    System.out.println("---> TestPlanName: [" + testPlanNameFromUser + "]  SheetName: [" + Config.RUNSHEET_FROM_TESTPLAN + "]");
                    tcTags = getTagsFromTestPlan(Config.TESTPLAN_ROOT_PATH + "/" + testPlanNameFromUser + ".xlsx", Config.RUNSHEET_FROM_TESTPLAN); //--tags @JUSATC1
                } else {
                    System.out.println("---> TestPlanName(default): " + Config.TESTPLAN_FILE_PATH + "  SheetName: " + Config.RUNSHEET_FROM_TESTPLAN);
                    tcTags = getTagsFromTestPlan(Config.TESTPLAN_FILE_PATH, Config.RUNSHEET_FROM_TESTPLAN); //--tags @JUSATC1
                }
            } else {
                System.out.println("---> RunFromTP option = No, take tags from config file");
                tcTags = tagsFromConfigFile.trim();
                //tcTags = "--tags " + Config.CUCUMBER_OPTION_TAGS.trim();
            }
        }
        return tcTags;
    }

    public static String getTagsFormatted(String tagsFromUser) throws IOException, InvalidFormatException {
        String tcTags;
        if (tagsFromUser != null && !tagsFromUser.equalsIgnoreCase("")) {
            System.out.println("User provided tags -->" + tagsFromUser);
            if (tagsFromUser.trim().startsWith(",")) {
                tagsFromUser = tagsFromUser.trim().replaceFirst(",", "");
            } else if (tagsFromUser.trim().endsWith(",")) {
                tagsFromUser = tagsFromUser.trim() + "###";
                tagsFromUser = tagsFromUser.replace(",###", "");
            }
            tagsFromUser = tagsFromUser.trim().replaceAll("@", "").replaceAll(" ", "");
            tagsFromUser = ("@" + tagsFromUser).replaceAll(",", ",@"); // Ex: @1234, @4567
            System.out.println("---> Formated user tags -->" + tagsFromUser);
            //tcTags="--tags "+tags;
            tcTags = tagsFromUser;
        } else {
            tcTags = "";
        }
        return tcTags;
    }

/*    public static String getTagsToMultiRunFromTestPlan(String testPlanPath, String sheetName) throws IOException, InvalidFormatException {
        List<String> runTagsFromTestPlan;
        StringBuilder tagString = new StringBuilder();
        runTagsFromTestPlan = getAlldataFromColumn(testPlanPath, sheetName, "Run");
        String prefix = "--tags ";
        int countTag = runTagsFromTestPlan.size();
        System.out.println("---> Total selected sceanrios to add to tag: " + countTag);
        String temp = "";
        if (countTag == 1) {
            //System.out.println(runTagsFromTestPlan.get(0));
            //System.out.println(runTagsFromTestPlan.get(0).split("\\.")[0]);
            //tagString = "@" + runTagsFromTestPlan.get(0).split("\\.")[0];
            tagString = new StringBuilder("--tags @" + runTagsFromTestPlan.get(0).trim() + " ");
        } else {
            for (int i = 0; i < countTag; i++) {
                //if (i != (countTag - 1))
                tagString.append("--tags @").append(runTagsFromTestPlan.get(i).trim()).append(" ");
                //tagString = tagString + "@" + runTagsFromTestPlan.get(i).split("\\.")[0] + ",";
                //else
                //tagString = tagString + "@" + runTagsFromTestPlan.get(i).split("\\.")[0];
            }
        }
        // tagString = prefix + tagString;
        // tagString = tagString;
        return tagString.toString();
    }*/

/*	public static void takeScreenshot(WebDriver driver) throws Exception {
		String timeStamp;
		File screenShotName;
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//The below method will save the screen shot in d drive with name "screenshot.png"
		timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		screenShotName = new File(Config.SCREENSHOTS_PATH+"/"+timeStamp+".png");
		FileUtils.copyFile(scrFile, screenShotName);

		String filePath = screenShotName.getAbsolutePath();
		String path = "<img src=\"file://" + filePath + "\" alt=\"\"/>";
		Reporter.log(path);

	}*/
/*	public static String takeScreenshot(WebDriver driver,String imageName) {
		try {
			String timeStamp;
			File screenShotName;
			String fileName;
			String basdDir=System.getProperty("user.dir");
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			fileName=basdDir+"/"+Config.SCREENSHOTS_PATH + "/" + imageName + "_" + timeStamp + ".png";
			screenShotName = new File(fileName);
			FileUtils.copyFile(scrFile, screenShotName);
			return fileName;
		}catch(Exception e){
			return e.getMessage();
		}

	}*/

    public static String takeScreenshotBase64(WebDriver driver) {
        try {
            return "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

/*	public static String takeScreenshotHTMLReport(ExtentTest htmlLogger,WebDriver driver){
		//DateFormat dateFormatScreenshot = new SimpleDateFormat("MMddyy_HH_mm_ss");
		//return htmlLogger.addScreenCapture(Common.takeScreenshot(Cases.driver, TestCaseDriver.testCaseID+"_"+TestCaseDriver.testCaseName+dateFormatScreenshot.format(new Date())));
		return htmlLogger.addScreenCapture(Common.takeScreenshot(driver, TestCaseDriver.testCaseID+"_"+TestCaseDriver.testCaseName));

	}*/

    public void cleanSheet(XSSFSheet sheet, int startRowNumberToDelete) {
        int numberOfRows = sheet.getPhysicalNumberOfRows();
        System.out.println("StartRowToDelete " + startRowNumberToDelete + " User Rows count: " + numberOfRows);
        if (numberOfRows >= startRowNumberToDelete) {
            for (int i = startRowNumberToDelete; i <= sheet.getLastRowNum(); i++) {
                if (sheet.getRow(i) != null) {
                    sheet.removeRow(sheet.getRow(i));
                } else {
                    System.out.println("Info: clean sheet='" + sheet.getSheetName() + "' ... skip line: " + i);
                }
            }
        } else {
            System.out.println("Info: clean sheet='" + sheet.getSheetName() + "' ... is empty");
        }
    }

    public static String statusMapforCI(String status) {
        String tempStatus = status.trim().toUpperCase();
        if (tempStatus.startsWith("PASS")) {
            tempStatus = "PASS";
        } else if (tempStatus.startsWith("SUCCESS")) {
            tempStatus = "PASS";
        } else if (tempStatus.startsWith("TRUE")) {
            tempStatus = "PASS";
        } else if (tempStatus.startsWith("FAIL")) {
            tempStatus = "FAIL";
        } else if (tempStatus.startsWith("FALSE")) {
            tempStatus = "FAIL";
        } else if (tempStatus.startsWith("PENDING")) {
            tempStatus = "FAIL";
        } else if (tempStatus.startsWith("UNDEFINED")) {
            tempStatus = "FAIL";
        } else if (tempStatus.startsWith("SKIP")) {
            tempStatus = "SKIP";
        } else {
            tempStatus = "NOT EXECUTED";
        }
        return tempStatus;
    }

    public static boolean isElementNOTPresent(List<WebElement> elementList) throws InterruptedException {
        int count = elementList.size();
        return count == 0;
    }

    public static boolean isElementPresent(List<WebElement> elementList) throws InterruptedException {
        int count = elementList.size();
        return count > 0;
    }

    public static void waitUntilVisibiityOf(WebDriverWait wait, WebElement we, long threadWaitmillisec) {
        try {
            csleep(2000);
            wait.until(ExpectedConditions.visibilityOf(we));
            System.out.println("Waited until Visibility of element");
        } catch (Exception e) {
            System.out.println("Going to use Thread wait due to timeout exception on until Visibility of element");
            try {
                csleep(threadWaitmillisec);
            } catch (Exception et) {
            }
        }
        try {
            System.out.println("Element displyed: " + we.isDisplayed());
        } catch (Exception ee) {
            csleep(threadWaitmillisec);
        }
    }

    public static void waitUntilVisibiityOf(WebDriver driver, WebDriverWait wait, WebElement we, long threadWaitmillisec) {
        try {
            getNGWebDriver(driver).waitForAngularRequestsToFinish();
        } catch (Exception e) {
        }
        waitUntilVisibiityOf(wait, we, threadWaitmillisec);
    }

    public static void waitUntilClickable(WebDriverWait wait, WebElement we, long threadWaitmillisec) {
        try {
            csleep(4000);
            wait.until(ExpectedConditions.elementToBeClickable(we));
            //System.out.println("Waited until Clickable of element");
        } catch (Exception e) {
            System.out.println("Going to use Thread wait due to timeout exception on until Clickable of element");
            try {
                csleep(threadWaitmillisec);
            } catch (Exception et) {
            }
        }
        try {
            System.out.println("Element enabled: " + we.isEnabled());
        } catch (Exception ee) {
            csleep(threadWaitmillisec);
        }
    }

    public static void waitUntilClickable(WebDriver driver, WebDriverWait wait, WebElement we, long threadWaitmillisec) {
        try {
            getNGWebDriver(driver).waitForAngularRequestsToFinish();
        } catch (Exception e) {
        }
        waitUntilClickable(wait, we, threadWaitmillisec);
    }

    public static void waitUntilPresence(WebDriverWait wait, By locator, long threadWaitmillisec) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            System.out.println("Waited until element Presence in location : [" + locator + "]");
        } catch (Exception e) {
            System.out.println("Going to use Thread wait due to timeout exception on until Presence of element");
            try {
                csleep(threadWaitmillisec);
            } catch (Exception et) {
            }
        }
    }

    public static void waitUntilPresence(WebDriver driver, WebDriverWait wait, By locator, long threadWaitmillisec) {
        try {
            getNGWebDriver(driver).waitForAngularRequestsToFinish();
        } catch (Exception e) {
        }
        waitUntilPresence(wait, locator, threadWaitmillisec);
    }

    public static boolean isElementDisply(WebDriver driver, By locator, Scenario sc, boolean isScroll, boolean isHighlight, String objectName, long threadWaitSec) {
        try {
            if (!waitUntilElementIsPresent(driver, locator, threadWaitSec, 1)) {
                WebElement we = driver.findElement(locator);
                if (we.isDisplayed()) {
                    if (isScroll)
                        scrollMethod(driver, we);
                    if (isHighlight)
                        highLighterMethod(driver, we);
                    return true;
                } else return false;
            } else return true;
        } catch (NoSuchElementException nse) {
            System.out.println("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            writeconsule(sc, "Element [" + objectName + "] not presence in the page");
            return false;
        } catch (Exception e) {
            System.out.println("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            writeconsule(sc, "Element [" + objectName + "] presence but not displayed or visible in the page");
            return false;
        }
    }

    public static boolean isElementDisply(WebDriver driver, By locator, Scenario sc, String objectName, long threadWaitSec) {
        try {
            if (!waitUntilElementIsPresent(driver, locator, threadWaitSec, 1)) {
                WebElement we = driver.findElement(locator);
                if (we.isDisplayed())
                    return true;
                else return false;
            } else return true;
        } catch (NoSuchElementException nse) {
            System.out.println("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            writeconsule(sc, "Element [" + objectName + "] not presence in the page");
            return false;
        } catch (Exception e) {
            System.out.println("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            writeconsule(sc, "Element [" + objectName + "] presence but not displayed or visible in the page");
            return false;
        }
    }

    public static boolean isElementDisply(WebElement we, Scenario sc, String objectName, long threadWaitSec) {
       /* try {
            csleep(threadWaitSec*1000);
        } catch (Exception et) {
        }*/
        try {
            if (!waitUntilElementIsPresent(we, threadWaitSec, 1)) {
                if (we.isDisplayed())
                    return true;
                else return false;
            } else return true;
        } catch (NoSuchElementException nse) {
            System.out.println("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            writeconsule(sc, "Element [" + objectName + "] not presence in the page");
            return false;
        } catch (Exception e) {
            System.out.println("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            writeconsule(sc, "Element [" + objectName + "] presence but not displayed or visible in the page");
            return false;
        }
    }

    public static boolean isElementSelected(WebDriver driver, WebElement we, Scenario sc, String objectName, long threadWaitSec) throws Exception {
      /*  try {
            csleep(threadWaitSec*1000);
        } catch (Exception et) {
        }*/
        waitForPageLoaded(driver, 5);//30
        waitUntilElementIsPresent(we, threadWaitSec, 1);
        try {
            waitUntilRefreshedAndClickable(driver, we, 3, 1);
            if (we.isEnabled() && we.isSelected()) {
                writeconsule(sc, "Element  : [" + objectName + "]  Enabled and Selected");
                return true;
            } else {
                writeconsule(sc, "Element  : [" + objectName + "]  Field Disabled / NOT Selected");
                return false;
            }
        } catch (NoSuchElementException nse) {
            writeconsule(sc, "Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            throw new Exception("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
        } catch (Exception e) {
            writeconsule(sc, "Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            throw new Exception("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
        }
    }

    public static boolean isElementSelected(WebDriver driver, By locator, Scenario sc, String objectName, long threadWaitSec) throws Exception {
        /*try {
            csleep(threadWaitSec*1000);
        } catch (Exception et) {
        }*/
        waitUntilElementIsPresent(driver, locator, threadWaitSec, 1);
        try {
            WebElement we = driver.findElement(locator);
            return isElementSelected(driver, we, sc, objectName, 2);
        } catch (NoSuchElementException nse) {
            writeconsule(sc, "Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            throw new Exception("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
        } catch (Exception e) {
            writeconsule(sc, "Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            throw new Exception("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
        }
    }

    public static boolean isElementEnabled(WebDriver driver, By locator, String objectName, long threadWaitSec) throws Exception {
     /*   try {
            csleep(threadWaitSec * 1000);
        } catch (Exception et) {
        }*/
        waitUntilElementIsPresent(driver, locator, threadWaitSec, 1);
        try {
            WebElement we = driver.findElement(locator);
            if (we.isEnabled()) {
                System.out.println("Element  : [" + objectName + "] Enabled");
                return true;
            } else return false;
        } catch (NoSuchElementException nse) {
            System.out.println("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            throw new Exception("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
        } catch (Exception e) {
            System.out.println("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            throw new Exception("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
        }
    }

    public static boolean isElementDisabled(WebDriver driver, By locator, String objectName, boolean isScroll, boolean isHighlight, long threadWaitSec) throws Exception {
        waitUntilElementIsPresent(driver, locator, threadWaitSec, 1);
        try {
            WebElement we = driver.findElement(locator);
            if (!we.isEnabled()) {
                if (isScroll)
                    scrollMethod(driver, we);
                if (isHighlight)
                    highLighterMethod(driver, we);
                System.out.println("ElementPresentAnd:[" + objectName + "] NOT Enabled");
                return true;
            } else return false;
        /*} catch (NoSuchElementException nse) {
            System.out.println("Element [" + objectName + "] present and not enabled in the page :" + nse.getMessage());
            throw new Exception("Element [" + objectName + "] present and not enabled in the page :" + nse.getMessage());*/
        } catch (Exception e) {
            System.out.println("Element [" + objectName + "] presence but enabled displayed or visible in the page " + e.getMessage());
            throw new Exception("Element [" + objectName + "] presence but enabled visible in the page " + e.getMessage());
        }
    }

    public static boolean isElemenetSwitched(WebDriver driver, WebElement we, Scenario sc, String objectName, boolean objectValue, long threadWaitmillsec) throws Exception {
        try {
            csleep(threadWaitmillsec);
        } catch (Exception et) {
        }
        try {
            waitUntilRefreshedAndClickable(driver, we, 3, 1);
            if (we.isEnabled() && !objectValue) {
                writeconsule(sc, "Element  : [" + objectName + "]  Enabled and Switched");
                return true;
            } else {
                writeconsule(sc, "Element  : [" + objectName + "]  Field Disabled / NOT Switched");
                return false;
            }
        } catch (NoSuchElementException nse) {
            writeconsule(sc, "Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            throw new Exception("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
        } catch (Exception e) {
            writeconsule(sc, "Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            throw new Exception("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
        }
    }

    public static boolean isElementEnabled(WebElement we, String objectName, long threadWaitSec) throws Exception {
        try {
            csleep(threadWaitSec * 1000);
        } catch (Exception et) {
        }
        try {
            if (we.isEnabled()) {
                System.out.println("Element  : [" + objectName + "] Enabled");
                return true;
            } else return false;
        } catch (NoSuchElementException nse) {
            System.out.println("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            throw new Exception("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
        } catch (Exception e) {
            System.out.println("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            throw new Exception("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
        }
    }

    public static boolean waitForElementClickable(WebDriverWait wait, WebElement we, String objectName, long threadWaitSec) throws Exception {
        try {
            csleep(threadWaitSec * 1000);
        } catch (Exception et) {
        }
        try {
            wait.until(ExpectedConditions.elementToBeClickable(we));
            System.out.println("Element  : [" + objectName + "] Clickable");
            return true;
        } catch (NoSuchElementException nse) {
            System.out.println("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
            throw new Exception("Element [" + objectName + "] not presence in the page :" + nse.getMessage());
        } catch (Exception e) {
            System.out.println("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
            throw new Exception("Element [" + objectName + "] presence but not displayed or visible in the page " + e.getMessage());
        }
    }

    public static boolean isNGElementDisply(WebDriver driver, Scenario sc, WebElement we, String objectName, long threadWaitSec) {
        try {
            getNGWebDriver(driver).waitForAngularRequestsToFinish();
        } catch (Exception e) {
        }
        return isElementDisply(we, sc, objectName, threadWaitSec);
    }

    public static void detailCucumberReports(String reportOutputDir, String jsonFilePath, String screenShotDirRelativeFromReportDir, String screeSize) throws Exception {
        CucumberDetailedResults results = new CucumberDetailedResults();
        results.setOutputDirectory(reportOutputDir);
        results.setSourceFile(jsonFilePath);
        results.setScreenShotLocation(screenShotDirRelativeFromReportDir);
        //System.out.println("Screenshot path: "+results.getScreenShotLocation());
        results.setOutputName("Detailed-Report");
        results.setPdfPageSize("2");
        if (screeSize != null && !screeSize.equalsIgnoreCase(""))
            results.setScreenShotWidth(screeSize);
        results.execute(false);
    }

    public static Reportable cucumberReportsHtml(String reportOutputDir, String jsonFilePath, String jsonFilePath2, String projectname, String builddetail, String platform, String browser, String repoBranchName) {
        //File reportOutputDirectory = new File(Config.REPORT_CUCUMBERHTML_PATH);
        File reportOutputDirectory = new File(reportOutputDir);
        List<String> jsonFiles = new ArrayList<>();
        //jsonFiles.add(Config.REPORT_CUCUMBERHTML_PATH+"/Cucumber.json");
        jsonFiles.add(jsonFilePath);
        if (!jsonFilePath2.equalsIgnoreCase(""))
            jsonFiles.add(jsonFilePath2);
        Configuration configuration = new Configuration(reportOutputDirectory, projectname);
// optional configuration - check javadoc for details
        configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
// do not make scenario failed when step has status SKIPPED
        configuration.setNotFailingStatuses(Collections.singleton(Status.SKIPPED));
        configuration.setBuildNumber(builddetail);
        //configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
        configuration.addReducingMethod(ReducingMethod.SKIP_EMPTY_JSON_FILES);
        configuration.addReducingMethod(ReducingMethod.HIDE_EMPTY_HOOKS);
        //configuration.addReducingMethod(ReducingMethod.MERGE_FEATURES_WITH_RETEST);
// addidtional metadata presented on main page
        configuration.addClassifications("Platform", platform);
        configuration.addClassifications("Browser", browser);
        configuration.addClassifications("Azure Repo Branch", repoBranchName);
// optionally add metadata presented on main page via properties file
        //List<String> classificationFiles = new ArrayList<>();
        //classificationFiles.add("properties-1.properties");
        //classificationFiles.add("properties-2.properties");
        //configuration.addClassificationFiles(classificationFiles);
// optionally specify qualifiers for each of the report json files
        //configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);
        //configuration.setQualifier("cucumber-report-1", "First report");
        //configuration.setQualifier("cucumber-report-2", "Second report");
        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        return reportBuilder.generateReports();
// and here validate 'result' to decide what to do if report has failed
    }

    public static void fnHighlight(WebDriver driver, WebElement element) throws InterruptedException {
        JavascriptExecutor Js = (JavascriptExecutor) driver;
        Js.executeScript("arguments[0].style.border='3px grove orange'", element);
        csleep(1000);
        Js.executeScript("arguments[0].style.border=''", element);
    }

    public void keyPressOnElement(Keys ks, WebElement locator) throws InterruptedException {
        locator.sendKeys(ks);
        csleep(3000);
    }

    public static String readPropertFile(String filename, String key) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("Property file [" + filename + "] not exist so created!!");
            f.createNewFile();
        }
        prob = new Properties();
        //InputStream input = new FileInputStream(Config.CustomerDatafilePath);
        InputStream input = new FileInputStream(filename);
        prob.load(input);
        String data = prob.getProperty(key);
        input.close();
        System.out.println("Data from property file --> " + key + "    " + data);
        System.out.println("Propfilename-->" + filename);
        return data;
    }

    public static boolean readPropertFile1(String filename, String key) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("Property file [" + filename + "] not exist so created!!");
            f.createNewFile();
        }
        prob = new Properties();
        //InputStream input = new FileInputStream(Config.CustomerDatafilePath);
        InputStream input = new FileInputStream(filename);
        prob.load(input);
        String data = prob.getProperty(key);
        input.close();
        System.out.println("Data from property file --> " + key + "    " + data);
        System.out.println("Propfilename-->" + filename);
        return data.isEmpty();
    }

 /*   public static String readPropertFile(String filename, String Key,String tcid) throws IOException {
        if(!tcid.trim().equalsIgnoreCase("")){
            Key=tcid+"-"+Key;
        }
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        prob = new Properties();
        //InputStream input = new FileInputStream(Config.CustomerDatafilePath);
        InputStream input = new FileInputStream(filename);
        prob.load(input);
        String data = prob.getProperty(Key);
        input.close();
        return data;
    }*/

    public static String readLineDataFromFile(String filename, String key, String splitEndStringwithComma) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String value = "";
        try {
            String line;
            while ((line = br.readLine()) != null) {
                //value="";
                System.out.println(line);
                if (splitEndStringwithComma != null && !splitEndStringwithComma.equalsIgnoreCase("")) {
                    if (line.startsWith(key) && line.endsWith(splitEndStringwithComma)) {
                        value = line.replace(key, "").replace(splitEndStringwithComma, "").replace("=", "").replace(",", "").trim();
                        System.out.println(value);
                    }
                } else
                    value = line.replace(key, "").replace("=", "").trim();
            }
        } finally {
            br.close();
        }
        System.out.println("Value from data file: " + value);
        return value;
    }

 /*   public static String readLineDataFromFile(String filename, String Key, String splitEndStringwithComma,String tcid) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String value = "";

        if(!tcid.trim().equalsIgnoreCase("")){
            Key=tcid+"-"+Key;
        }

        try {
            String line;
            while ((line = br.readLine()) != null) {
                //value="";
                System.out.println(line);
                if (splitEndStringwithComma != null && !splitEndStringwithComma.equalsIgnoreCase("")) {
                    if (line.startsWith(Key) && line.endsWith(splitEndStringwithComma)) {
                        value = line.replace(Key, "").replace(splitEndStringwithComma, "").replace("=", "").replace(",", "").trim();
                        System.out.println(value);

                    }
                } else
                    value = line.replace(Key, "").replace("=", "").trim();
            }

        } finally {
            br.close();
        }
        System.out.println("Value from data file: " + value);
        return value;
    }*/

    public static void writePropertFile(String filename, String Key, String Value) throws IOException {
        prob = new Properties();
//		PropertiesConfiguration conf = new PropertiesConfigurationguration(Config.CustomerIDfilePath);
        //OutputStream output = new FileOutputStream(Config.CustomerDatafilePath, true);
        OutputStream output = new FileOutputStream(filename, true);
        System.out.println("writing the data into file --> " + Key + "    " + Value);
        prob.setProperty(Key, Value);
        //conf.setProperty(Key,Value);
        //prob.store(output, "Adding: " + Key + " with value  " + Value);
        prob.store(output, null);
        output.close();
        //System.out.println("Data updated in file and closed the file !!!!!");
    }







/*    public static void writePropertFile(String filename, String Key, String Value,String tcid) throws IOException {
        prob = new Properties();
        if(!tcid.trim().equalsIgnoreCase("")){
            Key=tcid+"-"+Key;
        }
//		PropertiesConfiguration conf = new PropertiesConfigurationguration(Config.CustomerIDfilePath);
        //OutputStream output = new FileOutputStream(Config.CustomerDatafilePath, true);
        OutputStream output = new FileOutputStream(filename, true);
        System.out.println("writing the data into file --> " + Key + "    " + Value);
        prob.setProperty(Key, Value);
        //conf.setProperty(Key,Value);
        //prob.store(output, "Adding: " + Key + " with value  " + Value);
        prob.store(output, null);
        output.close();
        System.out.println("Data updated in file and closed the file !!!!!");
    }*/

    public static void UpdatePropertFile(String filename, String Key, String Value) throws IOException, ConfigurationException {
//		prob= new Properties();
        //PropertiesConfiguration conf = new PropertiesConfiguration(Config.CustomerDatafilePath);
        PropertiesConfiguration conf = new PropertiesConfiguration(filename);
//		OutputStream output = new FileOutputStream(Config.CustomerDatafilePath,true);
//		prob.setProperty(Key,Value);
        conf.setProperty(Key, Value);
        conf.save();
//		prob.store(output,"updated "+Key+" with value  "+Value);
//		output.close();
    }

/*    public static void UpdatePropertFile(String filename, String Key, String Value,String tcid) throws IOException, ConfigurationException {
//		prob= new Properties();
        //PropertiesConfiguration conf = new PropertiesConfiguration(Config.CustomerDatafilePath);
        PropertiesConfiguration conf = new PropertiesConfiguration(filename);
//		OutputStream output = new FileOutputStream(Config.CustomerDatafilePath,true);
//		prob.setProperty(Key,Value);
        if(!tcid.trim().equalsIgnoreCase("")){
            Key=tcid+"-"+Key;
        }
        conf.setProperty(Key, Value);
        conf.save();
//		prob.store(output,"updated "+Key+" with value  "+Value);
//		output.close();
    }*/

    public static void cleanPropertyFIle(String filePath) throws IOException {
        prob = new Properties();
        InputStream input = new FileInputStream(filePath);
        prob.load(input);
        prob.clear();
    }

    public static void clickOptionByName(WebDriver driver, List<WebElement> options, WebElement optionLabel, String ExpectedValue) throws InterruptedException {
        //Common.highLighterMethod(driver, optionLabel);
        boolean check = false;
        for (WebElement sample : options) {
//            Common.highLighterMethod(driver, sample);
            csleep(500);
            if (sample.getText().trim().equalsIgnoreCase(ExpectedValue.trim())) {
                //Common.scrollandhighLighterMethod(driver, sample);
                sample.click();
                csleep(500);//2000
                check = true;
                break;
            }
        }
        if (!check) {
            Assert.fail("Drop down item value: [" + ExpectedValue + "] Not found in the list");
        }
        waitForPageLoaded(driver, 10);//30
    }

    public static void clickOptionByNameandHighLight(WebDriver driver, List<WebElement> options, WebElement element, String ExpectedValue) throws InterruptedException {
//        Common.scrollonElement(driver, element);
//        Common.highLighterMethod(driver, element);
        boolean check = false;
        for (WebElement sample : options) {
//            Common.highLighterMethod(driver, sample);
            csleep(500);
            if (sample.getText().trim().equalsIgnoreCase(ExpectedValue.trim())) {
//                Common.scrollonElement(driver, sample);
//                Common.highLighterMethod(driver, sample);
//                Common.highLighterMethod(driver, sample);
                sample.click();
                check = true;
                break;
            }
        }
        if (!check) {
            Assert.fail("Drop down item value: [" + ExpectedValue + "] Not found in the list");
        }
        waitForPageLoaded(driver, 10);
    }

    public static void clickOptionByNameandHighLight1(WebDriver driver, List<WebElement> options, WebElement element, String ExpectedValue) throws InterruptedException {
        Common.scrollonElement(driver, element);
        Common.highLighterMethod(driver, element);
        boolean check = false;
        for (WebElement sample : options) {
//            Common.highLighterMethod(driver, sample);
            csleep(500);
            if (sample.getText().trim().equalsIgnoreCase(ExpectedValue.trim())) {
//                Common.scrollonElement(driver, sample);
//                Common.highLighterMethod(driver, sample);
//                Common.highLighterMethod(driver, sample);
                sample.click();
                check = true;
                break;
            }
        }
        if (!check) {
            Assert.fail("Drop down item value: [" + ExpectedValue + "] Not found in the list");
        }
        waitForPageLoaded(driver, 10);
    }

    public static void clickOptionByNameandHighLight(WebDriver driver, List<WebElement> options, String ExpectedValue) throws InterruptedException {
        boolean check = false;
        for (WebElement sample : options) {
            Common.highLighterMethod(driver, sample);
            csleep(500);
            if (sample.getText().trim().equalsIgnoreCase(ExpectedValue.trim())) {
                Common.scrollonElement(driver, sample);
                sample.click();
                check = true;
                break;
            }
        }
        if (!check) {
            Assert.fail("Drop down item value: [" + ExpectedValue + "] Not found in the list");
        }
        waitForPageLoaded(driver, 10);
    }

    public static void verifydropdownvalueispresnt(WebDriver driver, List<WebElement> options, WebElement element, String ExpectedValue) throws InterruptedException {
        boolean check = false;
        for (WebElement sample : options) {
            csleep(500);
            if (sample.getText().trim().equalsIgnoreCase(ExpectedValue.trim())) {
                Common.highLighterMethod(driver, sample);
                System.out.println("Dropdown value -->" + sample.getText());
                check = true;
                break;
            }
        }
        if (!check) {
            Assert.fail("Drop down item value: [" + ExpectedValue + "] Not found in the list");
        }
        waitForPageLoaded(driver, 15);//30
    }

    public static void verifykOptionByName(WebDriver driver, List<WebElement> options, WebElement element, String ExpectedValue, String objectname) throws InterruptedException {
        boolean check = false;
        for (int i = 0; i < options.size(); i++) {
//            LoopTool.Equals[] alloptions = new LoopTool.Equals[0];
            Object[] alloptions = new Object[0];
            if (alloptions[i].equals(ExpectedValue)) {
                boolean found = true;
                break;
            }
        }
        if (!check) {
            Assert.fail(objectname + " Drop down item value: [" + ExpectedValue + "] Not found in the list");
        }
        waitForPageLoaded(driver, 15);//30
    }

    public static void clickOptionByTagName(WebDriver driver, List<WebElement> options,
                                            WebElement element, String TagName, String ExpectedValue) throws InterruptedException {
//        Common.scrollonElement(driver, element);
        boolean check = false;
        Common.highLighterMethod(driver, element);
        for (WebElement sample : options) {
            csleep(500);
            if (sample.getAttribute(TagName).equalsIgnoreCase(ExpectedValue.trim())) {
                Common.highLighterMethod(driver, sample);
                waitUntilRefreshedAndClickable(driver, sample, 30, 2);
                csleep(500);
                sample.click();
                check = true;
                break;
            }
        }
        if (!check) {
            Assert.fail("Drop down item value: [" + ExpectedValue + "] Not found in the list");
        }
        base.Common.waitForPageLoaded(driver, 15);//30
    }

    public static void clickOptionByValue(WebDriver driver, WebElement listBox, String option) {
        System.out.println("selecting option --> " + option + " from webElement--> " + listBox);
        try {
            waitUntilRefreshedAndClickable(driver, listBox, 30, 2);
            Common.scrollonElement(driver, listBox);
            Common.highLighterMethod(driver, listBox);
            Select select = new Select(listBox);
            select.selectByValue(option);
            base.Common.csleep(1000);
            base.Common.waitForPageLoaded(driver, 15);//30
            System.out.println(" option selected at the value of " + option);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementClickInterceptedException e) {
            System.out.println("List dropdown NOT found");
            Assert.fail("List dropdown NOT FOUND due to " + e.getMessage());
        }
    }

    public static void clickOptionByValue(WebDriver driver, WebElement listBox, String option, String objectName) {
        System.out.println("selecting option --> " + option + " from webElement--> " + objectName);
        try {
            waitUntilRefreshedAndClickable(driver, listBox, 30, 2);
            Common.scrollonElement(driver, listBox);
            Common.highLighterMethod(driver, listBox);
            Select select = new Select(listBox);
            select.selectByValue(option);
            base.Common.csleep(1000);
            base.Common.waitForPageLoaded(driver, 15);//30
            System.out.println(" option selected at the value of " + option);
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            System.out.println(objectName + " dropdown NOT found");
            Assert.fail(objectName + " dropdown NOT FOUND due to " + e.getMessage());
        } catch (ElementClickInterceptedException e) {
            try {
                waitUntilRefreshedAndClickable(driver, listBox, 30, 2);
                Common.scrollonElement(driver, listBox);
                Common.highLighterMethod(driver, listBox);
                Select select = new Select(listBox);
                select.selectByValue(option);
                base.Common.csleep(1000);
                base.Common.waitForPageLoaded(driver, 15);//30
                System.out.println(" Intercepted Exceptiong, option selected at the value of " + option);
            } catch (NoSuchElementException | StaleElementReferenceException | ElementClickInterceptedException e1) {
                System.out.println(objectName + " dropdown NOT found");
                Assert.fail(objectName + " dropdown NOT FOUND due to " + e1.getMessage());
            }
        }
    }

    public static void writeconsule(Scenario sc, String comment) {
        sc.write(comment);
        System.out.println(comment);
    }

    public static String getSystemDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        String today = formatter.format(date);
        System.out.println("Today's date --> " + today);
        return today;
    }

    public static String getDateWithFormat(Date date, String dateformat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateformat);
        //Date date = new Date();
        String dtFormat = formatter.format(date);
        System.out.println("Date with format --> " + dtFormat);
        return dtFormat;
    }

    public static void clickOptionByVisibleText(WebDriver driver, WebElement listBox, String option) {
        try {
            System.out.println("selecting option --> " + option + " from webElements List");
            waitUntilRefreshedAndClickable(driver, listBox, 10, 2);//30
//            Common.scrollonElement(driver, listBox);
//            Common.highLighterMethod(driver, listBox);
            Select select = new Select(listBox);
            select.selectByVisibleText(option);
//            csleep(1000);
            Common.waitForPageLoaded(driver, 15);//30
            System.out.println(" option selected at the value of [" + option + "]");
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            System.out.println("Dropdown NOT found");
            Assert.fail("Dropdown NOT FOUND due to " + e.getMessage());
        } catch (ElementClickInterceptedException e) {
            System.out.println("Exception  --> ElementClickInterceptedException while click ListBox");
            try {
                System.out.println("selecting option --> " + option + " from webElements List");
                waitUntilRefreshedAndClickable(driver, listBox, 10, 2);//30
//                Common.scrollonElement(driver, listBox);
//                Common.highLighterMethod(driver, listBox);
                Select select = new Select(listBox);
                select.selectByVisibleText(option);
//                csleep(1000);
                Common.waitForPageLoaded(driver, 15);//30)
                System.out.println(" option selected at the value of [" + option + "]");
            } catch (NoSuchElementException | StaleElementReferenceException | ElementClickInterceptedException e1) {
                System.out.println(" dropdown NOT found");
                Assert.fail(" dropdown NOT FOUND due to " + e1.getMessage());
            }
        }
    }

    public static void clickOptionByVisibleText(WebDriver driver, WebElement listBox, String option, String componentName) {
        System.out.println("selecting option --> " + option + " from webElement--> " + componentName);
        waitUntilRefreshedAndClickable(driver, listBox, 30, 2);
        Common.scrollonElement(driver, listBox);
        Common.highLighterMethod(driver, listBox);
        Select select = new Select(listBox);
        select.selectByVisibleText(option);
        csleep(1000);
        Common.waitForPageLoaded(driver, 15);//30
        System.out.println(" option selected at the value of [" + option + "]");
    }

    public static void clickOptionByIndex(WebDriver driver, WebElement listBox, int index) {
        System.out.println("selecting option's index is--> " + index + " from webElement List");
        waitUntilRefreshedAndClickable(driver, listBox, 30, 2);
        Common.scrollonElement(driver, listBox);
        Common.highLighterMethod(driver, listBox);
        Select select = new Select(listBox);
        select.selectByIndex(index);
        csleep(1000);
        Common.waitForPageLoaded(driver, 15);//30
        System.out.println(" option selected at the index of [" + index + "]");
    }

    public static void selectOptionByIndex(WebDriver driver, WebElement listBox, int index) {
        System.out.println("selecting option's index is--> " + index + " from webElement List");
        Common.highLighterMethod(driver, listBox);
        Select select = new Select(listBox);
        select.selectByIndex(index);
        csleep(1000);
        Common.waitForPageLoaded(driver, 15);//30
        System.out.println(" option selected at the index of [" + index + "]");
    }

    public void enterIfElementPresent(WebDriver driver, List<WebElement> elementList, WebElement element, String value) throws InterruptedException {
        int count = elementList.size();
        if (count != 0) {
            csleep(4000);
            base.Common.scrollonElement(driver, element);
            Common.highLighterMethod(driver, element);
            element.sendKeys(value);
        } else {
            System.out.println(">>>>>>>>>Element " + element + " is Absent<<<<<<<<");
        }
    }

    public static FluentWait<WebDriver> getCustomFluentWait(WebDriver driver, long waitTimeSec, long pollIntervalTimeSec) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(waitTimeSec))
                .pollingEvery(Duration.ofSeconds(pollIntervalTimeSec))
                .ignoring(NoSuchElementException.class, ElementNotInteractableException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public static String generateUniqueIDwithPrefix(String prefixString) {
        DateFormat dateformat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        System.out.println("Unique ID" + prefixString + "-" + dateformat.format(date));
        return prefixString + "-" + dateformat.format(date);
    }

    public static String getIncrementedDateFromCurrent(int addDays, String strDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, addDays);  // number of days to add
        return (String) (dateFormat.format(c.getTime()));
    }

    //		getCustomFluentWait(10,2).until(ExpectedConditions.elementToBeClickable(Company_Name_Txt));
    public static void waitUntilRefreshedAndClickable(WebDriver driver, WebElement we, long waitTimeSec, long pollIntervalTimeSec) {
//        waitForPageLoaded(driver, 5);//10
        try {
            long temp = waitTimeSec / pollIntervalTimeSec;
            getCustomFluentWait(driver, waitTimeSec, pollIntervalTimeSec)
                    .until(ExpectedConditions
                            .refreshed(ExpectedConditions
                                    .elementToBeClickable(we)));
            for (long a = 0; a < waitTimeSec; a = a + temp) {
                try {
                    if (we.isDisplayed() && we.isEnabled())
                        break;
                    else {
                        csleep(pollIntervalTimeSec * 1000); //1000
                    }
                } catch (Exception e) {
                    System.out.println("Object not displyed: " + e.getCause().toString());
                    csleep(pollIntervalTimeSec * 1000);//1000
                }
            }
        } catch (Exception e) {
            System.out.println("Exception on elementToBeClickable [" + e.toString() + "] so going to wait for statndard csleep wait");
            if (waitTimeSec > 5) {
                System.out.println("Wait for 5 sec");
                csleep(1000);
            } else
                csleep(waitTimeSec * 1000);
            try {
                System.out.println("Element Enabled: " + we.isEnabled());
                System.out.println("Element Displayed: " + we.isDisplayed());
            } catch (Exception ee) {
                //csleep(waitTimeSec * 1000);
                System.out.println("Wait for 2 sec");
//                csleep(1000);
            }
        }
//        csleep(1000);
    }

    public static void waitUntilRefreshedAndElementPresent(WebDriver driver, WebElement we, long waitTimeSec, long pollIntervalTimeSec, int loop) {
        waitForPageLoaded(driver, waitTimeSec);
        //JSWaiter.setDriver(driver);
        //JSWaiter.waitAllRequest();
        try {
            for (long a = 0; a < loop; a++) {
                System.out.println("Check for display");
                try {
                    if (we.isDisplayed())
                        break;
                    else {
                        csleep(pollIntervalTimeSec * 1000);
                    }
                } catch (Exception e) {
                    driver.navigate().refresh();
                    csleep(pollIntervalTimeSec * 1000);
                    waitForPageLoaded(driver, waitTimeSec);
                    System.out.println(e.getCause().toString());
                    //csleep(pollIntervalTimeSec*1000);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception on elementToBeClickable [" + e.toString() + "] so going to wait for statndard csleep wait");
            try {
                System.out.println("Element Enabled: " + we.isEnabled());
                System.out.println("Element Displayed: " + we.isDisplayed());
            } catch (Exception ee) {
                csleep(waitTimeSec * 1000);
            }
        }
        csleep(1000);
    }

    public static void waitHighlightAndClick(WebDriver driver, WebElement we, long waitTimeSec, long pollIntervalTimeSec) {
        if (appName != null && appName.trim().contains("cv")) {
            handleSFErrorPopup(driver);
        }
        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        csleep(500);
        scrollandhighLighterMethod(driver, we);
        csleep(500);
        System.out.println("Click the element");
        we.click();
        csleep(1500);
        //this line will only work for EIS
        waitForPageLoaded(driver, 10);//30
    }

    public static void navigateToURL(WebDriver driver, String url) {
        waitForPageLoaded(driver, 15);//30
        driver.navigate().to(url.trim());
        waitForPageLoaded(driver, 15);//30
    }

    public static void waitHighlightAndClick(WebDriver driver, By locator, Scenario sc, boolean isScroll, boolean isHighlight, String objectName, long waitTimeSec, long pollIntervalTimeSec) {
        sc.write("Click " + objectName);
        WebElement we = driver.findElement(locator);
        try {
            waitUntilRefreshedAndClickable(driver, driver.findElement(locator), waitTimeSec, pollIntervalTimeSec);
            if (isScroll)
                scrollMethod(driver, we);
            if (isHighlight)
                highLighterMethod(driver, we);
            driver.findElement(locator).click();
            csleep(500);//2000
            sc.write("Clicked " + objectName);
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            sc.write(objectName + " NOT found");
            Assert.fail(objectName + " NOT FOUND due to " + e.getMessage());
        } catch (ElementNotInteractableException e) {
            System.out.println("Trying Secound Time To Click(Interactable)--> " + objectName);
            try {
                sc.write("Click Again " + objectName);
                csleep(1000); //10000
                waitUntilRefreshedAndClickable(driver, driver.findElement(locator), waitTimeSec, pollIntervalTimeSec);
                if (isScroll)
                    scrollMethod(driver, we);
                if (isHighlight)
                    highLighterMethod(driver, we);
                driver.findElement(locator).click();
                sc.write("Clicked Again Due to Interactable exception: " + objectName);
                waitForPageLoaded(driver, 10);//90
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
                sc.write(objectName + " NOT found");
                Assert.fail(objectName + " NOT FOUND due to " + e1.getMessage());
            }
        }
//        waitForPageLoaded(driver, 10);//90
        //sc.write("Clicked " + objectName);
    }

    public static void waitHighlightAndClick(WebDriver driver, WebElement we, Scenario sc, String objectName, long waitTimeSec, long pollIntervalTimeSec) {
        waitForPageLoaded(driver, 5);//90
        sc.write("Click " + objectName);
        try {
            waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            scrollandhighLighterMethod(driver, we);
            csleep(500);
            we.click();
            //focusAndClick(driver,we);
            csleep(500);//2000
            sc.write("Clicked " + objectName);
//            waitForPageLoaded(driver, 10);//90
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            sc.write(objectName + " NOT found");
            Assert.fail(objectName + " NOT FOUND due to " + e.getMessage());
        } catch (ElementNotInteractableException e) {
            System.out.println("Trying Secound Time To Click(Interactable)--> " + objectName);
            try {
//                waitForPageLoaded(driver, 10);//90
                sc.write("Click Again " + objectName);
//                csleep(1000); //10000
                waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
                scrollandhighLighterMethod(driver, we);
                //csleep(500);
                we.click();
                //focusAndClick(driver,we);
//                csleep(1000); //10000
                sc.write("Clicked Again Due to Interactable exception: " + objectName);
                waitForPageLoaded(driver, 10);//90
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
                sc.write(objectName + " NOT found");
                Assert.fail(objectName + " NOT FOUND due to " + e1.getMessage());
            }
        }
        waitForPageLoaded(driver, 5);//90
        //sc.write("Clicked " + objectName);
    }

    public static void ajaxEISLoadWait(WebDriver driver, long waitTimeSec, long pollIntervalTimeSec) {
        //this method is only for EIS
        //System.out.println("Appname -->" + appName.trim());
        // if (appName != null && appName.trim().equalsIgnoreCase("eis")) {
        //System.out.println("EIS application --> Calling Ajax check" );
        //waitForPageLoaded(driver, 90);
        long temp = waitTimeSec / pollIntervalTimeSec;
//            List<WebElement> ajaxLoopicons=driver.findElements(By.xpath("//span[@id='ajaxStatus' and text()='on']"));
        try {
            WebElement ajaxLoopicons = driver.findElement(By.id("ajaxStatus"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
            wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
            for (long a = 0; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                //                System.out.println("Check for display");
                try {
                    if (ajaxLoopicons.getText().trim().equalsIgnoreCase("on")) {
                        //System.out.println("Ajax Loop Object displyed wait for " + pollIntervalTimeSec + " Sec");
                        csleep(pollIntervalTimeSec * 1000);
                    } else {
                        //System.out.println("Ajax Loop Object not displyed for this event");
                        //if(a>0)
                        break;
                    }
                } catch (Exception e) {
                    //System.out.println("Ajax Loop Object not displyed: " + e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
        }
        // }
    }

    public static void ajaxCVLoadWait(WebDriver driver, long waitTimeSec, long pollIntervalTimeSec) {
        long temp = waitTimeSec / pollIntervalTimeSec;
        try {
            //WebElement ajaxLoopicons = driver.findElement(By.xpath("//lightning-spinner[@class='slds-spinner_container']"));
            int objCount = 0;
            //objCount=driver.findElements(By.xpath("//lightning-spinner[@class='slds-spinner_container']")).size();
            //WebDriverWait wait = new WebDriverWait(driver, 50);
            //wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
            for (long a = 0; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                //System.out.println("Check for spinning icon display");
                objCount = driver.findElements(By.xpath("//lightning-spinner[@class='slds-spinner_container']")).size();
                try {
                    if (objCount > 0) {
                        //System.out.println("Ajax Loop Object displyed wait for " + pollIntervalTimeSec + " Sec");
                        csleep(pollIntervalTimeSec * 1000);
                    } else {
                        //System.out.println("Ajax Loop Object not displyed for this event");
                        //if(a>0)
                        break;
                    }
                } catch (Exception e) {
                    //System.out.println("Ajax Loop Object not displyed: " + e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception Object not exist: " + e.getMessage());
        }
        // }
    }

    public static void eisLoadWait(WebDriver driver, long waitTimeSec, long pollIntervalTimeSec) {
        long temp = waitTimeSec / pollIntervalTimeSec;
        try {
            int objCount = 0;
            for (long a = 0; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                System.out.println("Check for Loading text display");
                objCount = driver.findElements(By.xpath("//span[text()='Loading...']")).size();
                try {
                    if (objCount > 0) {
                        csleep(pollIntervalTimeSec * 10000);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Loop Object not displyed: " + e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception Object not exist: " + e.getMessage());
        }
        // }
    }

    public static void waitForSyncSpingIconPageLoaded(WebDriver driver, long waitTimeSec, long pollIntervalTimeSec) {
        long temp = waitTimeSec / pollIntervalTimeSec;
        try {
            int objCount = 0;
            for (long a = 0; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                objCount = driver.findElements(By.xpath("//span[text()='Sync Policy/Quote']/..//i[@aria-label='icon: loading']")).size();
                try {
                    if (objCount > 0) {
                        csleep(pollIntervalTimeSec * 1000);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception Object not exist: " + e.getMessage());
        }
    }

    public static void ibmloginLoadWait(WebDriver driver, long waitTimeSec, long pollIntervalTimeSec) {
        long temp = waitTimeSec / pollIntervalTimeSec;
        try {
            int objCount = 0;
            for (long a = 0; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                objCount = driver.findElements(By.xpath("//div[text()='Logging in...']")).size();
                try {
                    if (objCount > 0) {
                        csleep(pollIntervalTimeSec * 1000);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception Object not exist: " + e.getMessage());
        }
    }

    public static void waitHighlightAndClickNoscroll(WebDriver driver, WebElement we, Scenario sc, String objectName, long waitTimeSec, long pollIntervalTimeSec) {
        waitForPageLoaded(driver, 180);
        sc.write("Start Click " + objectName);
        try {
            waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
//            scrollandhighLighterMethod(driver,we);
            highLighterMethod(driver, we);
            csleep(500);
            we.click();
            //focusAndClick(driver,we);
            csleep(1000);
            sc.write("Clicked " + objectName);
            waitForPageLoaded(driver, 15);//30
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            sc.write(objectName + " Not found");
            Assert.fail(objectName + " NOT FOUND due to " + e.getMessage());
        } catch (ElementNotInteractableException e) {
            System.out.println("Trying Secound Time To Click(Interactable)--> " + objectName);
            try {
//                waitForPageLoaded(driver, 90);
                sc.write("Click " + objectName);
                csleep(10000);
                waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
//                scrollandhighLighterMethod(driver, we);
                //csleep(500);
                we.click();
                //focusAndClick(driver,we);
                csleep(10000);
                sc.write("Clicked Again Due to Interactable exception: " + objectName);
//                waitForPageLoaded(driver, 90);
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
                sc.write(objectName + " NOT found");
                Assert.fail(objectName + " NOT FOUND due to " + e1.getMessage());
            }
        }
        sc.write("Clicked " + objectName);
    }

    public static boolean waitForJSandJQueryToLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    // no jQuery present
//                    System.out.println("No JQ present");
                    return true;
                }
            }
        };
        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((JavascriptExecutor) driver).executeScript("return document.readyState")
                            .toString().equals("complete");
                } catch (Exception e) {
                    System.out.println("No JS present");
                    // no jQuery present
                    return true;
                }
            }
        };
        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }

    public static void waitUntilJSReady(WebDriver driver) {
        csleep(1000);
        try {
            WebDriverWait jsWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor jsExec = (JavascriptExecutor) driver;
            ExpectedCondition<Boolean> jsLoad = webDriver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").toString().equals("complete");
            boolean jsReady = jsExec.executeScript("return document.readyState").toString().equals("complete");
            if (!jsReady) {
                jsWait.until(jsLoad);
            }
        } catch (WebDriverException ignored) {
        }
    }

    public static void waitUntilJQReady(WebDriver driver) {
        csleep(1000);
        WebDriverWait jsWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
        if (jQueryDefined) {
            csleep(100);
            waitForJQueryLoad(driver);
            csleep(100);
        }
    }

    private static void waitForJQueryLoad(WebDriver driver) {
        csleep(1000);
        WebDriverWait jsWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        try {
            ExpectedCondition<Boolean> jQueryLoad = webDriver -> ((Long) ((JavascriptExecutor) driver)
                    .executeScript("return jQuery.active") == 0);
            boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");
            if (!jqueryReady) {
                jsWait.until(jQueryLoad);
            }
        } catch (WebDriverException ignored) {
        }
    }
//    public static void waitAndClick(WebDriver driver, WebElement we, Scenario sc, String ObjectName,boolean isJSClick,boolean isScroll, boolean isHighlight,long waitTimeSec, long pollIntervalTimeSec) {
//        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
//        waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
//        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
//        sc.write("User going to click " + ObjectName);
//        try {
//            if (isScroll) {
//                scrollMethod(driver, we);
//            }
//            if(isHighlight){
//                highLighterMethod(driver, we);
//            }
//            csleep(2000);
//            System.out.println("Click element");
//            if(isJSClick){
//                focusAndClick(driver, we);
//            }else {
//                we.click();
//            }
//            csleep(1000);
//            sc.write("User Clicked in " + ObjectName);
//        } catch (NoSuchElementException | StaleElementReferenceException e) {
//            sc.write(ObjectName + " Not found");
//            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
//        }catch (ElementNotInteractableException e) {
//            System.out.println("Trying Secound Time To Click(Interactable)--> " + ObjectName);
//            try {
//                waitForPageLoaded(driver, 90);
//                sc.write("Click Again " + ObjectName);
//                if (isScroll) {
//                    scrollMethod(driver, we);
//                }
//                if(isHighlight){
//                    highLighterMethod(driver, we);
//                }
//                csleep(2000);
//                System.out.println("Click element");
//                if(isJSClick){
//                    focusAndClick(driver, we);
//                }else {
//                    we.click();
//                }
//                csleep(1000);
//                sc.write("User Clicked in " + ObjectName);;
//            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
//                sc.write(ObjectName + " NOT found");
//                Assert.fail(ObjectName + " NOT FOUND due to " + e1.getMessage());
//            }
//        }
//        waitForPageLoaded(driver, 30);
//    }

    public static void waitHighlightClearAndEnterData(WebDriver driver, WebElement we, String enterData, Scenario sc, String ObjectName, boolean isScroll, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));//(30))
//        waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
//        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User enter [" + enterData + "] in " + ObjectName);
        try {
            if (isScroll) {
                scrollandhighLighterMethod(driver, we);
            } else {
                highLighterMethod(driver, we);
            }
            csleep(500);//2000
            we.clear();
            System.out.println("Enter data in the element");
            we.sendKeys(enterData);
            csleep(500);
            sc.write("User enter [" + enterData + "] in " + ObjectName);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
//        waitForPageLoaded(driver, 10);//50
    }

    public static void waitHighlightAndEnterData(WebDriver driver, WebElement we, String enterData, Scenario sc, String ObjectName, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User enter [" + enterData + "] in " + ObjectName);
        try {
            highLighterMethod(driver, we);
//            csleep(2000);
            System.out.println("Enter data in the element");
            we.sendKeys(enterData);
            csleep(500);//1000
            sc.write("User entered [" + enterData + "] in " + ObjectName);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
//        waitForPageLoaded(driver, 30);
    }

    public static void waitHighlightClickAndEnterData(WebDriver driver, WebElement we, String enterData, Scenario sc, String ObjectName, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        //JSWaiter.setDriver(driver);
        //JSWaiter.waitAllRequest();
        waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User Click and enter [" + enterData + "]" + " in " + ObjectName);
        try {
            highLighterMethod(driver, we);
            we.click();
            csleep(1000);
//            we.clear();
            csleep(1000);
            //System.out.println("Enter data in the element");
            we.sendKeys(enterData);
//            base.Common.enterStringbyJSexecutor(driver, "0537975", we);
            csleep(2000);
            sc.write("User Entered [" + enterData + "]" + " in " + ObjectName);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
//        waitForPageLoaded(driver, 30);
    }

    public static void waitHighlightClickClearAndEnterData(WebDriver driver, WebElement we, String enterData, Scenario sc, String ObjectName, boolean isScroll, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
        //waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User Click, Clear and Enter [" + enterData + "]" + " in " + ObjectName);
        try {
            if (isScroll) {
                scrollandhighLighterMethod(driver, we);
            } else {
                highLighterMethod(driver, we);
            }
            highLighterMethod(driver, we);
            waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            we.click();
            csleep(500);
            we.clear();
            csleep(1000);
            we.sendKeys(enterData);
            csleep(2000);
            sc.write("User Entered [" + enterData + "]" + " in " + ObjectName);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
//        waitForPageLoaded(driver, 30);
    }

    public static void waitHighlightAndEnterData(WebDriver driver, WebElement we, String enterData, long waitTimeSec, long pollIntervalTimeSec) {
        waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
        highLighterMethod(driver, we);
//        csleep(1000);
        System.out.println("Enter data in the element");
        we.sendKeys(enterData);
        waitForPageLoaded(driver, 10);//30
    }

    public static void waitHighlightAndJsClick(WebDriver driver, WebElement we, long waitTimeSec, long pollIntervalTimeSec) {
        if (appName != null && appName.trim().contains("cv")) {
            handleSFErrorPopup(driver);
        }
        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        highLighterMethod(driver, we);
//        csleep(1000);
        System.out.println("Click the element");
        jsClick(driver, we);
//        waitForPageLoaded(driver, 20);
        if (appName != null && appName.trim().contains("cv")) {
            handleSFErrorPopup(driver);
        }
    }

    public static void waitHighlightAndJsClick(WebDriver driver, WebElement we, Scenario sc, String objectName, long waitTimeSec, long pollIntervalTimeSec) {
        try {
            waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            highLighterMethod(driver, we);
            csleep(1000);
            System.out.println("Click the element");
            jsClick(driver, we);
            waitForPageLoaded(driver, 20);
            sc.write("User Clicked: " + objectName);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(objectName + " NOT found");
            Assert.fail(objectName + " NOT FOUND due to " + e.getMessage());
        }
    }
//    public static void waitHighLightJSClick(WebDriver driver, WebElement we, long waitTimeSec, long pollIntervalTimeSec) {
//        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
//        highLighterMethod(driver, we);
//        csleep(1000);
//        System.out.println("Click the element");
//        jsClick(driver, we);
//        waitForPageLoaded(driver, 20);
//    }

    public static void waitAndJsClick(WebDriver driver, WebElement we, Scenario sc, String objectName, long waitTimeSec, long pollIntervalTimeSec) {
        try {
            waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            highLighterMethod(driver, we);
            csleep(1000);
            System.out.println("Click the element");
            jsClick(driver, we);
            waitForPageLoaded(driver, 15);//30
            sc.write("User Clicked: " + objectName);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(objectName + " NOT found");
            Assert.fail(objectName + " NOT FOUND due to " + e.getMessage());
        }
    }

    public static String waitAndGetText(WebDriver driver, WebElement we, Scenario sc, String ObjectName, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));//30
        String text = null;
        //waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User get data from " + ObjectName);
        try {
            Common.waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
            try {
                Common.waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            } catch (Exception e) {
                System.out.println("Element is not clickable");
            }
            highLighterMethod(driver, we);
//            csleep(2000);
            System.out.println("Get data in the element");
            text = we.getText();
//            csleep(1000);
            System.out.println("Text from object [" + ObjectName + "] -> " + text);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
        waitForPageLoaded(driver, 10);//30
        return text;
    }

    public static String waitAndGetText(WebDriver driver, WebElement we, Scenario sc, String ObjectName, boolean isScroll, boolean isHighlight, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
        String text = null;
        sc.write("User get data from " + ObjectName);
        try {
            Common.waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
            try {
                Common.waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            } catch (Exception e) {
                System.out.println("Element is not clickable");
            }
            if (isScroll) {
                scrollMethod(driver, we);
            }
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
//            csleep(2000);
            System.out.println("Get data in the element");
            text = we.getText();
//            csleep(1000);
            System.out.println("Text from object [" + ObjectName + "] -> " + text);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
        return text;
    }

    public static String waitAndGetAttribute(WebDriver driver, WebElement we, String attributeName, Scenario sc, String ObjectName, boolean isScroll, boolean isHighlight, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        String getValue = null;
        sc.write("User get data from " + ObjectName);
        try {
            Common.waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
            try {
                Common.waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            } catch (Exception e) {
                System.out.println("Element is not clickable");
            }
            if (isScroll) {
                scrollMethod(driver, we);
            }
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
            csleep(2000);
            System.out.println("Get Attribute Value in the element");
            getValue = we.getAttribute(attributeName);
            csleep(1000);
            System.out.println("Attribute[" + attributeName + "] Value from object [" + ObjectName + "] -> " + getValue);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
        waitForPageLoaded(driver, 15);//30
        return getValue;
    }

    public static void enterDate(WebDriver driver, By locator, int addDays, Scenario sc) throws InterruptedException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, addDays);  // number of days to add
        String ExpDate = (String) (dateFormat.format(c.getTime()));
        WebElement we = driver.findElement(locator);
        base.Common.EnterData(driver, we, ExpDate + Keys.TAB, sc, "Date", false, false, false, true, false, 20, 2);
        base.Common.sleep(2000);
    }

    public static String waitAndGetText(WebDriver driver, By locator, Scenario sc, String ObjectName, boolean isScroll, boolean isHighlight, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        String text = null;
        sc.write("User get data from " + ObjectName);
        try {
            WebElement we = driver.findElement(locator);
            Common.waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
            try {
                Common.waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            } catch (Exception e) {
                System.out.println("Element is not clickable");
            }
            if (isScroll) {
                scrollMethod(driver, we);
            }
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
            csleep(2000);
            System.out.println("Get data in the element");
            text = we.getText();
            csleep(1000);
            System.out.println("Text from object [" + ObjectName + "] -> " + text);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
        waitForPageLoaded(driver, 10);//30
        return text;
    }

    public static String waitAndGetAttribute(WebDriver driver, By locator, String attributeName, Scenario sc, String ObjectName, boolean isScroll, boolean isHighlight, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));//30
        String getValue = null;
        sc.write("User get data from " + ObjectName);
        try {
            waitForPageLoaded(driver, 5);//30
            waitUntilElementIsPresent(driver, locator, waitTimeSec, pollIntervalTimeSec);
            WebElement we = driver.findElement(locator);
            Common.waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
            try {
                Common.waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
            } catch (Exception e) {
                System.out.println("Element is not clickable");
            }
//            highLighterMethod(driver, we);
            if (isScroll) {
                scrollMethod(driver, we);
            }
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
            csleep(2000);
            System.out.println("Get Attribute Value in the element");
            getValue = we.getAttribute(attributeName);
            csleep(1000);
            System.out.println("Attribute[" + attributeName + "] Value from object [" + ObjectName + "] -> " + getValue);
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        }
        waitForPageLoaded(driver, 5);//30
        return getValue;
    }

    public static WebElement waitForElementVisible(WebDriver driver, WebElement we, long waitTimeSec, long pollIntervalTimeSec) {
        waitForPageLoaded(driver, 10);//60
        //JSWaiter.setDriver(driver);
        //JSWaiter.waitAllRequest();
        try {
            long temp = waitTimeSec / pollIntervalTimeSec;
            for (long a = 0; a < waitTimeSec; a = a + temp) {
                System.out.println("Check for display");
                try {
                    if (we.isDisplayed())
                        break;
                    else {
                        csleep(pollIntervalTimeSec * 1000);
                    }
                } catch (Exception e) {
                    System.out.println(e.getCause().toString());
                    csleep(pollIntervalTimeSec * 500);
                }
            }
            getCustomFluentWait(driver, waitTimeSec, pollIntervalTimeSec)
                    .until(ExpectedConditions
                            .refreshed(ExpectedConditions
                                    .visibilityOf(we)));
        } catch (Exception e) {
            System.out.println("Exception in visibilityOf element [" + e.getMessage() + "]");
            System.out.println("Going to use Thread wait due to timeout exception on until visibility of element");
            if (waitTimeSec > 5) {
                System.out.println("Wait for 5sec");
                csleep(5000);
            } else
                csleep(waitTimeSec * 500);
        }
//        csleep(500);
        return we;
    }

    public static List<WebElement> waitForAllElementsVisible(WebDriver driver, List<WebElement> wes, long waitTimeSec, long pollIntervalTimeSec) {
        waitForPageLoaded(driver, 15); //60
        //JSWaiter.setDriver(driver);
        //JSWaiter.waitAllRequest();
        try {
            long temp = waitTimeSec / pollIntervalTimeSec;
            for (long a = 0; a < waitTimeSec; a = a + temp) {
                System.out.println("Check for display");
                try {
                    if (wes.get(0).isDisplayed())
                        break;
                    else {
                        csleep(pollIntervalTimeSec * 1000);
                    }
                } catch (Exception e) {
                    System.out.println(e.getCause().toString());
                    csleep(pollIntervalTimeSec * 500);
                }
            }
            getCustomFluentWait(driver, waitTimeSec, pollIntervalTimeSec)
                    .until(ExpectedConditions
                            .refreshed(ExpectedConditions
                                    .visibilityOfAllElements(wes)));
        } catch (Exception e) {
            System.out.println("Exception in visibilityOf element [" + e.getMessage() + "]");
            System.out.println("Going to use Thread wait due to timeout exception on until visibility of element");
            if (waitTimeSec > 5) {
                System.out.println("Wait for 5sec");
                csleep(5000);
            } else
                csleep(waitTimeSec * 500);
        }
        csleep(500);
        return wes;
    }

    public static void waitForPageLoaded(WebDriver driver, long waitTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(waitTimeSec));
    }

    public static void waitForPageLoad(WebDriver driver, int timeout) {
        new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(webDriver ->
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
                        && ((Long) ((JavascriptExecutor) webDriver).
                        executeScript("return window.performance.getEntriesByType('resource').length")) > 0);
    }

    public static void waitForPageLoadedOld(WebDriver driver, long waitTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(waitTimeSec));
//        csleep(500);//1000
//        Boolean jSjQCompleted = false;
        //this line will only work for EIS
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        /*System.out.println("ApplicationName > " + appName);
        if (appName != null && appName.trim().contains("eis")) {
            sleep(500);
            List<WebElement> loadingSpinIcon = driver.findElements(By.xpath
                    ("//div[@class='ant-spin-nested-loading']//span[@class='ant-spin-dot ant-spin-dot-spin']|//div[@class='loading']//span[contains(@class,'nested-loading')]"));
            System.out.println("Loooking for Page still loading Spin Icon appear or Not");
            waitForLoadIconDisappear(driver, loadingSpinIcon, 3);
        }*/
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
    }

    public static void waitForPageLoadedcopy(WebDriver driver, long waitTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(waitTimeSec));
        csleep(500);//1000
        Boolean jSjQCompleted = false;
        try {
            // jSjQCompleted = waitForJSandJQueryToLoad(driver);
        } catch (Exception error) {
            //System.out.println("Timeout for JS Load Request to complete: " + (waitTimeSec));
        }
        //this line will only work for EIS
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        System.out.println("ApplicationName > " + appName);
        if (appName != null && appName.trim().contains("eis")) {
            //ajaxEISLoadWait(driver, 50, 2);
            sleep(500);
            List<WebElement> loadingSpinIcon = driver.findElements(By.xpath
                    ("//div[@class='ant-spin-nested-loading']//span[@class='ant-spin-dot ant-spin-dot-spin']|//div[@class='loading']//span[contains(@class,'nested-loading')]"));
            System.out.println("Loooking for Page still loading Spin Icon appear or Not");
            waitForLoadIconDisappear(driver, loadingSpinIcon, 3);
        }
      /*  else if(appName != null && appName.trim().equalsIgnoreCase("cv")){
            ajaxCVLoadWait(driver,50, 2);
        }*/
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
    }

    public static void waitForLoadIconDisappear(WebDriver driver, List<WebElement> elements, int totalWaitTimeinSec) {
        int increment = 0;
        try {
            while (isElementPresent(elements)) {
                System.out.println("Page still loading");
                sleep(1000);
                increment++;
                if (increment >= totalWaitTimeinSec) {
                    break;
                }
            }
        } catch (InterruptedException | ElementNotInteractableException e) {
            waitForLoadIconDisappear(driver, elements, 2);
        } catch (Exception e) {
        }
    }

    public static void enterDataJSexecutor(WebDriver driver, Object data, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value= '" + data + "';", element);
    }

    public static void updatePropertyFile(String filepath, String key, String orginalValue, String replaceValue) {
        try {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(filepath), StandardCharsets.UTF_8));
            boolean ischang = false;
            String fullKey = key.trim() + "=" + orginalValue.trim();
            String replaceStr = key.trim() + "=" + replaceValue.trim();
            System.out.println("File line update: File :" + filepath + " orginal: " + fullKey + " Replaced: " + replaceStr);
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).trim().equals(fullKey)) {
                    fileContent.set(i, replaceStr);
                    ischang = true;
                    System.out.println("Found the key and replaced with value : " + replaceStr);
                    break;
                }
            }
            if (ischang) {
                Files.write(Paths.get(filepath), fileContent, StandardCharsets.UTF_8);
                System.out.println("File updated");
            } else {
                System.out.println("File not updated or nothing to update!!");
            }
        } catch (Exception e) {
        }
    }

/*    public static void updatePropertyFile(String filepath, String key, String orginalValue, String replaceValue,String tcid) {

        if(!tcid.trim().equalsIgnoreCase("")){
            key=tcid+"-"+key;
        }
        try {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(filepath), StandardCharsets.UTF_8));
            boolean ischang = false;
            String fullKey = key.trim() + "=" + orginalValue.trim();
            String replaceStr = key.trim() + "=" + replaceValue.trim();
            System.out.println("File line update: File :" + filepath + " orginal: " + fullKey + " Replaced: " + replaceStr);
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).trim().equals(fullKey)) {
                    fileContent.set(i, replaceStr);
                    ischang = true;
                    System.out.println("Found the key and replaced with value : " + replaceStr);
                    break;
                }
            }
            if (ischang) {
                Files.write(Paths.get(filepath), fileContent, StandardCharsets.UTF_8);
                System.out.println("File updated");
            } else {
                System.out.println("File not updated or nothing to update!!");
            }
        } catch (Exception e) {

        }
    }*/

    public static void verifyTextboxdataByValue(WebDriver driver, WebElement Textbox, String data) {
        base.Common.waitUntilRefreshedAndClickable(driver, Textbox, 20, 2);
        base.Common.scrollandhighLighterMethod(driver, Textbox);
        String ActSpMinBenefit = Textbox.getAttribute("value");
        System.out.println("Default text box value is --> " + ActSpMinBenefit);
        Assert.assertEquals(ActSpMinBenefit, data, "Text box value does not matched");
    }

    public static void verifyDefaultDropDownselectvalue(WebDriver driver, WebElement dropdown, String Expdata) {
        base.Common.waitUntilRefreshedAndClickable(driver, dropdown, 20, 2);
        base.Common.scrollandhighLighterMethod(driver, dropdown);
        Select select = new Select(dropdown);
        WebElement option = select.getFirstSelectedOption();
        String SelectedText = option.getText();
        Assert.assertEquals(SelectedText, Expdata, "Default value doest not match");
        System.out.println("Default text box value is --> " + SelectedText);
    }

    public static boolean verifyDefaultDropDownselectvaluecheck(WebDriver driver, WebElement dropdown, String Expdata) {
        base.Common.waitUntilRefreshedAndClickable(driver, dropdown, 20, 2);
        base.Common.scrollandhighLighterMethod(driver, dropdown);
        Select select = new Select(dropdown);
        WebElement option = select.getFirstSelectedOption();
        String SelectedText = option.getText();
        Assert.assertEquals(SelectedText, Expdata, "Default value doest not match");
        System.out.println("Default text box value is --> " + SelectedText);
        return false;
    }

    public static void clickOnElementinTablebyText(WebDriver driver, List<WebElement> Row, List<WebElement> Column, String Text) {
        System.out.println("CLick on the matching text search result row ");
        boolean matchfound = false;
        for (int i = 1; i <= Row.size(); i++) {
            for (WebElement element : Column) {
                if (element.getText().trim().equalsIgnoreCase(Text.trim())) {
                    System.out.println("Matched for text : " + element.getText());
                    waitUntilRefreshedAndClickable(driver, element, 20, 2);
                    scrollandhighLighterMethod(driver, element);
                    System.out.println("Result matched row clicked");
                    matchfound = true;
                    element.click();
                    break;
                }
            }
        }
        Assert.assertTrue(matchfound, "Matched row not found for the text: " + Text);
    }

    /* public static String getCellDataFromTable(WebDriver driver, List<WebElement> rows, List<WebElement> columns, int row, int col) {
         int rowCount=rows.size();
         int colCount=columns.size();
         //WebElement cell=driver.findElement(By.xpath("//*[@id='policyDataGatherForm:dataGatherView_ListGroupClassGroupCoverRelationship_SubGroupsRating_data']/tr["+1+"]/td["+2"]")
         if(rowCount>0) {
             WebElement cell=rows.get(row-1).findElements(By.tagName("td")).get(col);
             return cell.getText();
         }else{
             System.out.println("No table presented");
             return null;
         }
     }*/
    public static String getCellDataFromTable(WebDriver driver, String xpathOfRows, int row, int col) {
        waitForPageLoaded(driver, 20);
        int rowCount = driver.findElements(By.xpath(xpathOfRows)).size();
        if (rowCount > 0) {
            WebElement cell = driver.findElement(By.xpath(xpathOfRows + "[" + row + "]/td[" + col + "]"));
            scrollandhighLighterMethod(driver, cell);
            return cell.getText();
        } else {
            System.out.println("No table presented");
            return null;
        }
    }

    public static void verifyLabelText(WebDriver driver, WebElement wb, Scenario sc, String expLabel) {
        scrollandhighLighterMethod(driver, wb);
        base.Common.waitForPageLoaded(driver, 2000);
        Assert.assertTrue(wb.isDisplayed());
        String actLabel = wb.getText().replaceAll("\\s{2,}", " ").trim();
        sc.write("Actual Label --> " + actLabel + " Expected label is --> " + expLabel);
        highLighterMethod(driver, wb);
        Assert.assertEquals(actLabel, expLabel, "label text does not match");
        takeScreenshot(sc, driver, "Label text verification");
    }

    public static void getCellWithTwoCondition(WebDriver driver, List<WebElement> Row, List<WebElement> Column,
                                               String UWApproval, String ID, String xpathOfOverride) throws InterruptedException {
        System.out.println("CLick on the matching text search result row ");
        boolean matchfound = false;
        for (int i = 1; i <= Row.size(); i++) {
            for (WebElement element : Column) {
                csleep(4000);
//                if (element.getText().trim().equalsIgnoreCase(UWApproval.trim())) {
                for (int j = i; j <= Row.size(); j++) {
                    for (WebElement wb : Column) {
                        if (element.getText().trim().equalsIgnoreCase(ID.trim())) {
                            System.out.println("Quote ID -->" + ID);
                            WebElement cell = driver.findElement(By.xpath(xpathOfOverride + "[" + i + "]/td[" + j + "]"));
                            System.out.println("Matched for text : " + element.getText());
                            waitUntilRefreshedAndClickable(driver, element, 20, 2);
                            scrollandhighLighterMethod(driver, element);
                            matchfound = true;
                            element.click();
                            break;
                        }
                    }
//                    }
                }
            }
        }
        Assert.assertTrue(matchfound, "Matched row not found for the text: " + UWApproval + " and" + ID);
    }

    public static void ClearEnterTabInTextBox(WebDriver driver, WebElement txtbox, String data, Scenario sc, String objectName, long waitTimeSec, long pollIntervalTimeSec) {
        waitUntilRefreshedAndClickable(driver, txtbox, waitTimeSec, pollIntervalTimeSec);
        scrollandhighLighterMethod(driver, txtbox);
        try {
            try {
                txtbox.click();
                txtbox.clear();
                writeconsule(sc, "Text box clicked and cleared");
                csleep(1000);
            } catch (Exception e) {
            }
            txtbox.sendKeys(Keys.CONTROL, Keys.chord("a")); //select all text in textbox
            txtbox.sendKeys(Keys.BACK_SPACE); //delete it
            txtbox.sendKeys(data);
            txtbox.sendKeys(Keys.TAB);//enter new text
            csleep(2000);
            sc.write("Data entered in the textbox --> " + objectName + " Data -->" + data);
        } catch (Exception e) {
            sc.write(objectName + " NOT found");
            Assert.fail(objectName + " NOT FOUND due to " + e.getMessage());
            waitForPageLoaded(driver, 15);//30
        }
        waitForPageLoaded(driver, 15);//30
    }

    public static void setClipboardData(String string) {
        StringSelection stringSelection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    public static void uploadFile(String fileLocation) {
        try {
            setClipboardData(fileLocation);
            Robot robot = new Robot();
            robot.setAutoDelay(5000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.setAutoDelay(2000);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.setAutoDelay(2000);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.setAutoDelay(2000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static boolean checkColumnExistInDataSheet(List<HashMap<String, String>> datamap, String columnName) {
        // Iterator<Map.Entry<String, String>>   iterator = datamap.get(0).entrySet().iterator();
        Iterator<Map.Entry<String, String>> iterator = datamap.get(1).entrySet().iterator();
        // flag to store result
        boolean isKeyPresent = false;
        int colindex = 1;
        // Iterate over the HashMap
        while (iterator.hasNext()) {
            // Get the entry at this iteration
            Map.Entry<String, String>
                    entry
                    = iterator.next();
            // Check if this key is the required key
            //System.out.println("Column Name: "+entry.getKey());
            if (columnName.trim().equalsIgnoreCase(entry.getKey().trim())) {
                System.out.println("Found Matching Column Name: " + entry.getKey() + " at the index : " + colindex);
                isKeyPresent = true;
                break;
            }
            colindex++;
        }
        // Print the result
        System.out.println("Does key "
                + columnName
                + " exists: "
                + isKeyPresent);
        return isKeyPresent;
    }

    public static boolean checkColumnExistInDataSheet(HashMap<String, String> datamap, String columnNameKey) {
        Iterator<Map.Entry<String, String>>
                iterator = datamap.entrySet().iterator();
        // flag to store result
        boolean isKeyPresent = false;
        int colindex = 1;
        // Iterate over the HashMap
        while (iterator.hasNext()) {
            // Get the entry at this iteration
            Map.Entry<String, String>
                    entry
                    = iterator.next();
            // Check if this key is the required key
            //System.out.println("Column Name: "+entry.getKey());
            if (columnNameKey.trim().equalsIgnoreCase(entry.getKey().trim())) {
                System.out.println("Found Matching Column Name: " + entry.getKey() + " at the index : " + colindex);
                isKeyPresent = true;
                break;
            }
            colindex++;
        }
        // Print the result
        System.out.println("Does key "
                + columnNameKey
                + " exists: "
                + isKeyPresent);
        return isKeyPresent;
    }

    public static String addNodaytoGivenDate(String oldDate, int nodaystoadd) {
        System.out.println("Date before Addition: " + oldDate);
        //Specifying date format that matches the given date
        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
        Calendar c = Calendar.getInstance();
        try {
            //Setting the date to the given date
            c.setTime(sdf.parse(oldDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Number of Days to add
        c.add(Calendar.DAY_OF_MONTH, nodaystoadd);
        //Date after adding the days to the given date
        String newDate = sdf.format(c.getTime());
        //Displaying the new Date after addition of Days
        System.out.println("Date after Addition: " + newDate);
        return newDate;
    }

    public static Alert isAlertExist(WebDriver wd) {
        try {
            WebDriverWait w = new WebDriverWait(wd, Duration.ofSeconds(2));
            Alert a = w.until(ExpectedConditions.alertIsPresent());
            System.out.println("Alert exists");
            return a;
        } catch (Exception e) {
            System.out.println("Alert not exists: " + e.getMessage());
            return null;
        }
    }

    public static String acceptAndGetTextIfAlertExist(WebDriver wd) {
        try {
            Alert alert = isAlertExist(wd);
            if (alert != null) {
                wd.switchTo().alert();
                csleep(500);
                String msg = alert.getText();
                System.out.println("Alert text: " + msg);
                alert.accept();
                System.out.println("Alert clicked accepted");
                //csleep(5000);
                waitForPageLoaded(wd, 20);
                return msg;
            } else {
                System.out.println("Alert not exists");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    public static String accpetAlertandGetMessage(WebDriver wd) {
        Alert alert1 = wd.switchTo().alert(); // switch to alert
        String alertMessage = alert1.getText(); // capture alert message
        System.out.println("alertMessage-->" + alertMessage); // Print Alert Message
        alert1.accept(); // accept alert
        System.out.println("Alert clicked accepted");
        csleep(2000);
        waitForPageLoaded(wd, 15);//30
        return alertMessage;
    }

    public static String dismissAlertandGetMessage(WebDriver wd) {
        Alert alert1 = wd.switchTo().alert(); // switch to alert
        String alertMessage = alert1.getText(); // capture alert message
        System.out.println(alertMessage); // Print Alert Message
        alert1.dismiss();// accept dismissed
        System.out.println("Alert clicked dismissed or cancelled");
        csleep(2000);
        return alertMessage;
    }

    public static String verifyAlertMessage(WebDriver wd, String expectedMessage) throws Exception {
        if (isAlertExist(wd) != null) {
            String altmsg = wd.switchTo().alert().getText();
            System.out.println("Alert error message-->" + altmsg);
            Assert.assertEquals(altmsg.trim().toUpperCase(), expectedMessage.trim().toUpperCase());
            return altmsg;
        } else {
            throw new Exception("Alert not appeared while click Preview button");
        }
    }

    public static String verifyMultipleAsserts(ArrayList<String[]> collection, Scenario sc) throws Exception {
        String pmesg = "";
        String fmesg = "";
        try {
            for (String[] vdata : collection) {
                if (vdata.length != 4) {
                    throw new Exception("String Array size [" + vdata.length + "] is not meeting the expected size[3], pelase check the data that you assinged ");
                }
                vdata[2] = vdata[2].trim().toLowerCase();
                if (vdata[2].equalsIgnoreCase("e") | vdata[2].equalsIgnoreCase("equal")) {
                    if (vdata[0].trim().toLowerCase().equalsIgnoreCase(vdata[1].trim().toLowerCase())) {
                        pmesg = pmesg + vdata[3] + ": [" + vdata[0] + "]\n";
                    } else {
                        fmesg = fmesg + vdata[3] + " NOT matching: Actual [" + vdata[0] + "] Expected [" + vdata[1] + "]\n";
                    }
                } else if (vdata[2].equalsIgnoreCase("c") | vdata[2].contains("contain")) {
                    if (vdata[0].trim().toLowerCase().contains(vdata[1].trim().toLowerCase())) {
                        pmesg = pmesg + vdata[3] + ": [" + vdata[0] + "]\n";
                    } else {
                        fmesg = fmesg + vdata[3] + ":[" + vdata[0] + "] NOT contain [" + vdata[1] + "]\n";
                    }
                }
            }
            if (!fmesg.equalsIgnoreCase("")) {
                sc.write(pmesg);
                Assert.fail("Verification Failed with :\n" + fmesg);
            }
        } finally {
            return pmesg;
        }
    }

    public static String verifyMultipleAsserts(HashMap<String, Boolean> result, Scenario sc) {
        String pmesg = "";
        String fmesg = "";
        try {
            for (Map.Entry<String, Boolean> set : result.entrySet()) {
                if (set.getValue()) {
                    pmesg = pmesg + "Value Matched for : [" + set.getKey() + "]\n";
                } else {
                    fmesg = fmesg + "Value NOT Matched for : [" + set.getKey() + "]\n";
                }
            }
            if (!fmesg.equalsIgnoreCase("")) {
                sc.write(pmesg);
                System.out.println(fmesg);
                Assert.fail("Verification Failed with :\n" + fmesg);
            }
        } finally {
            System.out.println(pmesg);
            sc.write(pmesg);
            return pmesg;
        }
    }

    public static boolean isTextPresentInPage(WebDriver driver, String text) {
        waitForPageLoaded(driver, 15);//30
        if (driver.getPageSource().toLowerCase().contains(text.toLowerCase()))
            return true;
        else
            return false;
    }

    public static void waitUntilElementNotPresent(WebDriver driver, WebElement we, long waitTimeSec, long pollIntervalTimeSec) {
        csleep(1000);
        try {
            for (long a = pollIntervalTimeSec; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                System.out.println("Check for display");
                if (we.isDisplayed())
                    csleep(pollIntervalTimeSec * 1000);
                else {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception on elementToBeClickable [" + e.toString() + "] so going to wait for statndard csleep wait");
            csleep(waitTimeSec * 1000);
        }
    }

    public static boolean waitUntilElementIsPresent(WebElement we, long waitTimeSec, long pollIntervalTimeSec) {
//        csleep(2000);
        boolean exist = false;
        try {
            for (long a = 0; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                System.out.println("Check for display");
                try {
                    if (we.isDisplayed()) {
                        csleep(pollIntervalTimeSec * 200);//1000
                        exist = true;
                        break;
                    } else {
                    }
                } catch (Exception e) {
                    System.out.println("Exception on elementToBeClickable so going to wait");
                    csleep(pollIntervalTimeSec * 200);//1000
                }
            }
        } catch (Exception e) {
            System.out.println("Exception on Wiat Element Is Present [" + e.toString() + "]");
            //csleep(waitTimeSec * 1000);
        }
        return exist;
    }

    public static boolean waitUntilElementIsPresent(WebDriver driver, By locator, long waitTimeSec, long pollIntervalTimeSec) {
        waitForPageLoaded(driver, 10);//20
        csleep(500);//2000
        boolean exist = false;
        try {
            for (long a = 0; a < waitTimeSec; a = a + pollIntervalTimeSec) {
                System.out.println("Check for display");
                try {
                    WebElement we = driver.findElement(locator);
                    if (we.isDisplayed()) {
                        csleep(pollIntervalTimeSec * 1000);
                        exist = true;
                        break;
                    } else {
                    }
                } catch (Exception e) {
                    System.out.println("Exception on elementToBeClickable so going to wait");
                    csleep(pollIntervalTimeSec * 1000);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception on Wiat Element Is Present [" + e.toString() + "]");
            //csleep(waitTimeSec * 1000);
        }
        return exist;
    }

    public static void Click(WebDriver driver, WebElement we, Scenario sc, String ObjectName, boolean isJSClick, boolean isScroll,
                             boolean isHighlight, boolean isDoubleClick, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
        //waitForElementVisible(driver, we, waitTimeSec, pollIntervalTimeSec);
//        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User going to click " + ObjectName);
        try {
            if (isScroll) {
                scrollMethod(driver, we);
            }
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
            csleep(500);//2000
            System.out.println("Click element");
            if (isJSClick) {
                System.out.println("Calling JSExecuter for Click " + ObjectName);
                if (isDoubleClick)
                    jsDoubleClick(driver, we);
                else
                    jsClick(driver, we);
            } else {
                if (isDoubleClick)
                    jsDoubleClick(driver, we);
                else
                    we.click();
            }
            csleep(500);//2000
            sc.write("User Clicked in " + ObjectName);
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        } catch (ElementNotInteractableException e) {
            System.out.println("Trying Secound Time To Click(Interactable)--> " + ObjectName);
            try {
                waitForPageLoaded(driver, 5);//20
                waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
                sc.write("Click Again(2nd time) due to Element Not Interactable Exception -> " + ObjectName);
                if (isScroll) {
                    scrollMethod(driver, we);
                }
                if (isHighlight) {
                    highLighterMethod(driver, we);
                }
                csleep(1000);
                System.out.println("Click element");
                if (isJSClick) {
                    if (isDoubleClick)
                        jsDoubleClick(driver, we);
                    else
                        jsClick(driver, we);
                } else {
                    if (isDoubleClick)
                        jsDoubleClick(driver, we);
                    else
                        we.click();
                }
                csleep(500);//2000
                sc.write("User Clicked again 2nd time in " + ObjectName);
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
                sc.write(ObjectName + " NOT found");
                Assert.fail(ObjectName + " NOT FOUND due to " + e1.getMessage());
            }
        } catch (TimeoutException e) {
            Assert.fail("Unable to Click button: " + ObjectName + " due to Page still loading beyond the wait time: ");
        } catch (Exception otherExc) {
            sc.write("Unable to perform the click event in " + ObjectName + " Due to common exception ");
            Assert.fail("Unable to perform the click event in " + ObjectName + " Due to : " + otherExc.getMessage());
        }
        waitForPageLoaded(driver, 10);
    }

    /*----------------------------------------------------------------------------------------------------------------------
    Function Name: Click
    Description:
    User can enter element name which stored in POM or he can enter direct in while calling function as showing in below Usage
    Parameters
    param element - webdriver, locator [name,id,xpath] ,Scenario, elementname.....
    param text - String value to be entered after clearing.
    Usage
    base.Common.Click(driver, unassinglink, sc, "Un Assign Agent Link", false, false, true, false, 4, 1);
    Notes:
      Uses
    Author: Rameswar Rao Ramanadham
    Last updated - Date
    ---------------------------------------------------------------------------------------------------------------------------*/
    public static void Click(WebDriver driver, By locator, Scenario sc, String objectName, boolean isJSClick, boolean isScroll, boolean isHighlight, boolean isDoubleClick, long waitTimeSec, long pollTimeSec) {
        waitUntilElementIsPresent(driver, locator, waitTimeSec, 5);
        try {
            Click(driver, driver.findElement(locator), sc, objectName, isJSClick, isScroll, isHighlight, isDoubleClick, waitTimeSec, pollTimeSec);
        } catch (Exception e) {
            Assert.fail(objectName + " NOT found or loded yet due to -> " + e.getMessage());
        }
        waitForPageLoaded(driver, 5);

    }

    public static void ClickAndTab(WebDriver driver, By locator, Scenario sc, String objectName, boolean isJSClick, boolean isScroll, boolean isHighlight, boolean isDoubleClick, long waitTimeSec, long pollTimeSec) {
        waitUntilElementIsPresent(driver, locator, waitTimeSec, 1);
        try {
            Click(driver, driver.findElement(locator), sc, objectName, isJSClick, isScroll, isHighlight, isDoubleClick, waitTimeSec, pollTimeSec);
        } catch (Exception e) {
            Assert.fail(objectName + " NOT found or loded yet due to -> " + e.getMessage());
        }
    }

    public static void ClickTAB(WebDriver driver, By locator, Scenario sc, String objectName, boolean isJSClick, boolean isScroll, boolean isHighlight, boolean isDoubleClick, long waitTimeSec, long pollTimeSec) {
        waitUntilElementIsPresent(driver, locator, waitTimeSec, 1);
        try {
            System.out.println("NowClickOnEnter");
            driver.findElement(locator).sendKeys(Keys.ENTER);
            System.out.println("NowClickOnEnter");
            driver.findElement(locator).sendKeys(Keys.TAB);
        } catch (Exception e) {
            Assert.fail(objectName + " NOT found or loded yet due to -> " + e.getMessage());
        }
    }

    public static void clearDataInField(WebDriver driver, WebElement we, Scenario sc, boolean isHighlight, String objectName) throws InterruptedException {
        base.Common.waitForElementVisible(driver, we, 4, 2);
        if (base.Common.isElementDisply(we, sc, objectName, 3)) {
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
            we.sendKeys(Keys.CLEAR);
            we.sendKeys(Keys.chord(Keys.CONTROL, "A"));
        }
    }

    public static void EnterData(WebDriver driver, WebElement we, Object enterData, Scenario sc,
                                 String ObjectName, boolean isScroll, boolean isHighlight, boolean Click,
                                 boolean isClear, boolean isJSEnterData, long waitTimeSec, long pollIntervalTimeSec) {
        if (appName != null && appName.trim().contains("cv")) {
            handleSFErrorPopup(driver);
        }
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));//20
        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User enter [" + enterData + "] in " + ObjectName);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            if (isScroll) {
                scrollMethod(driver, we);
            }
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
            csleep(500);
            if (Click)
                we.click();
            System.out.println("clicking the field");
            csleep(1000);
            if (isClear)
                csleep(500);
            we.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            js.executeScript("arguments[0].value = '';", we);
            csleep(500);
            System.out.println("Enter data in the element");
            if (isJSEnterData)
                enterDataJSexecutor(driver, enterData, we);
            else
                we.sendKeys(enterData.toString());
            csleep(500);
            sc.write("User enter [" + enterData + "] in " + ObjectName);
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        } catch (ElementNotInteractableException e) {
            System.out.println("Trying Secound Time To Click(Interactable)--> " + ObjectName);
            try {
                waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
                sc.write("Enter Data Again(2nd time) " + ObjectName);
                if (isScroll) {
                    scrollMethod(driver, we);
                }
                if (isHighlight) {
                    highLighterMethod(driver, we);
                }
                if (Click)
                    we.click();
                System.out.println("clicking the field(2nd time)");
                if (isClear)
                    we.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                we.clear();
                if (isJSEnterData) {
                    System.out.println("Enter data in the element using JS");
                    enterDataJSexecutor(driver, enterData, we);
                } else {
                    System.out.println("Enter data in the element");
                    we.sendKeys(enterData.toString());
                }
                sc.write("User Enterted Data [" + enterData + "] Again 2nd time in " + ObjectName);
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
                sc.write(ObjectName + " NOT found");
                Assert.fail(ObjectName + " NOT FOUND due to " + e1.getMessage());
            }
        } catch (TimeoutException e) {
            Assert.fail("Unable to enter data in TextBox: " + ObjectName + " due to Page still loading beyond the wait time: ");
        } catch (Exception otherExc) {
            sc.write("Unable to perform the data entry in " + ObjectName + " Due to common exception ");
            Assert.fail("Unable to perform the data entry in " + ObjectName + " Due to : " + otherExc.getMessage());
        }
        base.Common.waitForPageLoad(driver, 10);
    }

    public static void EnterData(WebDriver driver, By locator, Object enterData, Scenario sc,
                                 String ObjectName, boolean isScroll, boolean isHighlight, boolean Click,
                                 boolean isClear, boolean isJSEnterData, long waitTimeSec, long pollIntervalTimeSec) {
        waitUntilElementIsPresent(driver, locator, waitTimeSec, 1);
        try {
            EnterData(driver, driver.findElement(locator), enterData, sc, ObjectName, isScroll, isHighlight, Click, isClear, isJSEnterData,
                    waitTimeSec, pollIntervalTimeSec);
        } catch (Exception e) {
            Assert.fail(ObjectName + " NOT found or loded yet due to -> " + e.getMessage());
        }
    }

    public static void clickOptionByName(WebDriver driver, List<WebElement> options, WebElement optionLabel, String ExpectedOptionValue, Scenario sc, String ObjectName) throws InterruptedException {
        try {
            Common.highLighterMethod(driver, optionLabel);
            boolean check = false;
            for (WebElement sample : options) {
                csleep(500);
                if (sample.getText().trim().equalsIgnoreCase(ExpectedOptionValue.trim())) {
                    Common.scrollandhighLighterMethod(driver, sample);
                    sample.click();
                    csleep(1000);
                    check = true;
                    break;
                }
            }
            if (!check) {
                sc.write("[" + ObjectName + "] Drop down List item value: [" + ExpectedOptionValue + "] Not Found");
                Assert.fail("[" + ObjectName + "] Drop down List item value: [" + ExpectedOptionValue + "] Not Found");
            }
//            waitForPageLoaded(driver, 15);//30
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
            sc.write(ObjectName + " Dropdown NOT found");
            Assert.fail(ObjectName + " Dropdown NOT FOUND due to " + e.getMessage());
        }
//        waitForPageLoaded(driver, 15);//30
    }

    public static void focusAndClick(WebDriver driver, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
        if (executor.executeScript
                ("return document.readyState").toString().equals("complete")) {
            System.out.println("Page loaded properly.");
        }
        /*else {
            Common.sleep(3000);
        }*/
    }

    public static void pageRefresh(WebDriver driver) {
//        sleep(3000);
//        waitForPageLoaded(driver, 15);//60
        try {
            driver.navigate().refresh();
            waitForPageLoaded(driver, 15);//60
            sleep(3000);
        } catch (Exception e) {
            System.out.println("Something happned during refresh page: [" + e.getMessage() + "]");
        }
    }

    public static void waitAndClick(WebDriver driver, WebElement we, Scenario sc, String ObjectName, boolean isJSClick, boolean isScroll, boolean isHighlight, long waitTimeSec, long pollTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Config.pageTimeOutSec));
        waitForElementVisible(driver, we, waitTimeSec, pollTimeSec);
        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollTimeSec);
        sc.write("User going to click " + ObjectName);
        try {
            if (isScroll) {
                scrollMethod(driver, we);
            }
            if (isHighlight) {
                highLighterMethod(driver, we);
            }
            csleep(2000);
            System.out.println("Click element");
            if (isJSClick) {
                focusAndClick(driver, we);
            } else {
                we.click();
            }
            csleep(1000);
            sc.write("User Clicked in " + ObjectName);
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            sc.write(ObjectName + " Not found");
            Assert.fail(ObjectName + " NOT FOUND due to " + e.getMessage());
        } catch (ElementNotInteractableException e) {
            System.out.println("Trying Secound Time To Click(Interactable)--> " + ObjectName);
            try {
//                waitForPageLoaded(driver, 90);
                sc.write("Click Again due to Interactable Exception for Object:  " + ObjectName);
                if (isScroll) {
                    scrollMethod(driver, we);
                }
                if (isHighlight) {
                    highLighterMethod(driver, we);
                }
                csleep(2000);
                System.out.println("Click element");
                if (isJSClick) {
                    focusAndClick(driver, we);
                } else {
                    we.click();
                }
                csleep(1000);
                sc.write("User Clicked in " + ObjectName);
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
                sc.write(ObjectName + " NOT found");
                Assert.fail(ObjectName + " NOT FOUND due to " + e1.getMessage());
            } catch (TimeoutException e2) {
                Assert.fail("Unable to Click button: " + ObjectName + " due to Page still loading beyond the wait time: " + Config.pageTimeOutSec);
            }
        } catch (TimeoutException e2) {
            Assert.fail("Unable to Click button: " + ObjectName + " due to Page still loading beyond the wait time: " + Config.pageTimeOutSec);
        } catch (Exception eall) {
            Assert.fail("Unable to Click button: " + ObjectName + " due to : " + eall.getMessage());
        }
//        waitForPageLoaded(driver, 15);//30
    }

    public static void waitAndClick(WebDriver driver, By locator, Scenario sc, String ObjectName, boolean isJSClick, boolean isScroll, boolean isHighlight, long waitTimeSec, long pollTimeSec) {
        List<WebElement> lwe = driver.findElements(locator);
        try {
            if (lwe.size() > 0) {
                waitAndClick(driver, lwe.get(0), sc, ObjectName, isJSClick, isScroll, isHighlight, waitTimeSec, pollTimeSec);
            } else
                Assert.fail(ObjectName + " NOT found or loded yet");
        } catch (Exception e) {
            Assert.fail(ObjectName + " NOT found or loded yet due to -> " + e.getMessage());
        }
    }

    public static void enterStringbyJSexecutor(WebDriver driver, String data, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value= '" + data + "';", element);
    }

    public static boolean waitUntilelementPresent
            (WebDriver drive, WebElement element, long waitTimeSec, long maxLoop, String objectName) throws InterruptedException {
        System.out.println("--------waitUntilElementPresent Method ---------");
        System.out.println("WaitUntilElementPresent event for Object -> " + objectName);
        System.out.println("element name Input--> " + element);
        System.out.println("Image name full path --> " + element);
        int i = 0;
        long totaltime = 0;
        while (element.isDisplayed()) {
            TimeUnit.SECONDS.sleep(waitTimeSec);
            totaltime = totaltime + waitTimeSec;
            if (i == maxLoop)
                break;
            else
                i++;
        }
        System.out.println("Total Wait Time -->" + totaltime);
        if (!element.isDisplayed()) {
            System.out.println("Object [" + objectName + "] Foundin duration[" + totaltime + "]");
            return true;
        } else {
            System.out.println("Object [" + objectName + "] NOT Found in duration[" + totaltime + "]");
            return false;
        }
    }

    public static void SendKyesEnter(WebDriver driver, WebElement we, Scenario sc, String ObjectName, boolean isHighlight, long waitTimeSec, long pollIntervalTimeSec) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        waitUntilRefreshedAndClickable(driver, we, waitTimeSec, pollIntervalTimeSec);
        sc.write("User going to click " + ObjectName);
        we.sendKeys(Keys.ENTER);
        if (isHighlight) {
            highLighterMethod(driver, we);
        }
        csleep(2000);
        System.out.println("Click element");
    }

    public static void datefunction(WebDriver driver, WebElement element, Scenario sc, int addDays) throws InterruptedException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, addDays);  // number of days to add
        String ExpDate = (String) (dateFormat.format(c.getTime()));
        sc.write("enter the Date field --> " + ExpDate);
        base.Common.EnterData(driver, element, ExpDate + Keys.ENTER + Keys.TAB, sc, "Date", false, false, false, true, false, 20, 2);
        base.Common.sleep(2000);
    }

    public static void clickOnTableByCellTextLink(WebDriver driver, List<WebElement> Row, List<WebElement> Column, String cellText, Boolean SkipHeaderRow, Scenario sc, String ObjectName) {
//        waitForPageLoaded(driver, 90);
        System.out.println("CLick on the matching text search result row in table");
        boolean matchfound = false;
        int skip = 0;
        if (SkipHeaderRow) {
            skip = 1;
            System.out.println("Table Header row not counted - Skiped in below loop");
        }
        try {
            System.out.println("Table Rows count:  " + Row.size());
            System.out.println("Table column count:  " + Column.size());
            for (int i = skip; i < Row.size(); i++) {
                for (WebElement element : Column) {
                    if (element.getText().trim().equalsIgnoreCase(cellText.trim())) {
                        System.out.println("Matched for text : " + element.getText());
                        waitUntilRefreshedAndClickable(driver, element, 30, 2);
                        scrollandhighLighterMethod(driver, element);
                        System.out.println("Result matched row clicked");
                        matchfound = true;
                        element.click();
                        sc.write("User Clicked in " + ObjectName + " in cell text link " + cellText);
                        break;
                    }
                }
                if (matchfound)
                    break;
            }
            Assert.assertTrue(matchfound, "Matched text [" + cellText + "] not found in the table cell for Object [ " + ObjectName + "]");
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            sc.write(ObjectName + " NOT Found");
            Assert.fail(ObjectName + " NOT Found due to " + e.getMessage());
        } catch (ElementNotInteractableException e) {
            System.out.println("Trying Secound Time To Click(Interactable)--> " + ObjectName);
            try {
//                waitForPageLoaded(driver, 90);
                sc.write("Click Again 2nd time " + ObjectName);
                csleep(2000);
                System.out.println("Click element");
                matchfound = false;
                for (int i = skip; i < Row.size(); i++) {
                    for (WebElement element : Column) {
                        if (element.getText().trim().equalsIgnoreCase(cellText.trim())) {
                            System.out.println("Matched for text : " + element.getText());
                            waitUntilRefreshedAndClickable(driver, element, 30, 2);
                            scrollandhighLighterMethod(driver, element);
                            System.out.println("Result matched row clicked");
                            matchfound = true;
                            element.click();
                            sc.write("User Clicked in " + ObjectName + " in cell text link " + cellText);
                            break;
                        }
                    }
                    if (matchfound)
                        break;
                }
                Assert.assertTrue(matchfound, "Matched text [" + cellText + "] not found in the table cell for Object [ " + ObjectName + "]");
                csleep(1000);
                sc.write("User Clicked in " + ObjectName);
                ;
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e1) {
                sc.write(ObjectName + " NOT Found");
                Assert.fail(ObjectName + " NOT Found due to " + e1.getMessage());
            } catch (TimeoutException e2) {
                Assert.fail("Unable to Click Table Cell Text: " + ObjectName + " due to Page still loading beyond the wait time: " + Config.pageTimeOutSec);
            }
        } catch (TimeoutException e2) {
            Assert.fail("Unable to Click Table Cell Text: " + ObjectName + " due to Page still loading beyond the wait time: " + Config.pageTimeOutSec);
        } catch (Exception eall) {
            Assert.fail("Unable to Click Table Cell Text: " + ObjectName + " due to : " + eall.getMessage());
        }
//        waitForPageLoaded(driver, 90);
    }

    public static void selectDrpDwnValue(WebDriver driver, By locator, Scenario sc, String value, String drpDwnName, boolean scroll, boolean jsCLick) throws InterruptedException {
        Common.waitForElementVisible(driver, (WebElement) locator, 4, 2);
//        Scenario sc = null;
        if (!value.equalsIgnoreCase("")) {
            Common.Click(driver, locator, sc, drpDwnName, jsCLick, scroll, true, false, 2, 1);
            Thread.sleep(1000);
            List<WebElement> valueList = driver.findElements(By.xpath("//li[text()='" + value + "']"));
            System.out.println("dropdownValueforLI -->" + valueList);
            Thread.sleep(4000);
            Common.clickOptionByName(driver, valueList, (WebElement) locator, value);
            Common.takeScreenshot(sc, driver, "Value: " + value + " in the Drop Down: " + drpDwnName);
            Common.writeconsule(sc, "Value: " + value + " in the Drop Down: " + drpDwnName);
        } else {
            Common.writeconsule(sc, "Value from " + drpDwnName + " drop down is not required for this test");
        }
    }

    public static void selectDrpDwnValue(WebDriver driver, WebElement drpDwn, Scenario sc, String value, String drpDwnName, boolean scroll, boolean jsCLick) throws InterruptedException {
        Common.waitForElementVisible(driver, drpDwn, 4, 2);
//        Scenario sc = null;
        if (!value.equalsIgnoreCase("")) {
            Common.Click(driver, drpDwn, sc, drpDwnName, jsCLick, scroll, true, false, 2, 1);
            Thread.sleep(1000);
            List<WebElement> valueList = driver.findElements(By.xpath("//li[text()='" + value + "']"));
            System.out.println("dropdownValueforLI -->" + valueList);
            Thread.sleep(4000);
            Common.clickOptionByName(driver, valueList, drpDwn, value);
            Common.takeScreenshot(sc, driver, "Value: " + value + " in the Drop Down: " + drpDwnName);
            Common.writeconsule(sc, "Value: " + value + " in the Drop Down: " + drpDwnName);
        } else {
            Common.writeconsule(sc, "Value from " + drpDwnName + " drop down is not required for this test");
        }
    }

    public static void enterDate(WebDriver driver, WebElement element, int addDays, Scenario sc) throws InterruptedException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, addDays);  // number of days to add
        String ExpDate = (String) (dateFormat.format(c.getTime()));
        sc.write("enter the Date field --> " + ExpDate);
        base.Common.EnterData(driver, element, ExpDate + Keys.TAB, sc, "Date", false, false, false, true, false, 20, 2);
        base.Common.sleep(2000);
    }

    public static void enterDate(WebDriver driver, WebElement element, boolean isScroll, boolean isHighlight, boolean Click,
                                 int addDays, Scenario sc) throws InterruptedException {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, addDays);  // number of days to add
        String ExpDate = (String) (dateFormat.format(c.getTime()));
        sc.write("enter the Date field --> " + ExpDate);
        base.Common.EnterData(driver, element, ExpDate + Keys.ENTER, sc, "Date", false, true,
                false, true, false, 20, 2);
        base.Common.sleep(2000);
    }

 /*   public void selectDrpDwnValue(WebElement drpDwn, String value, String drpDwnName, boolean scroll, boolean jSclick) throws InterruptedException {
        base.Common.Click(driver, drpDwn, sc, drpDwnName, false, scroll, true, false, 2, 1);
        WebElement option = driver.findElement(By.xpath("//li[text()='" + value + "']"));
        base.Common.Click(driver, option, sc, value, jSclick, false, true, false, 2, 1);
        base.Common.takeScreenshot(sc, driver, "Value: " + value + " in the Drop Down: " + drpDwnName);
        base.Common.writeconsule(sc, "Value: " + value + " in the Drop Down: " + drpDwnName);
    }*/
/*    public static void clickOnElementinTablebyText(WebDriver driver, List<WebElement> Row, List<WebElement> Column, String Text,String a) {
        System.out.println("CLick on the matching text search result row ");
        boolean matchfound = false;
        for (int i = 1; i <= Row.size(); i++) {
            for (WebElement element : Column) {
                if (element.getText().trim().equalsIgnoreCase(Text.trim())) {
                    System.out.println("Matched for text : " + element.getText());
                    waitUntilRefreshedAndClickable(driver, element, 20, 2);
                    scrollandhighLighterMethod(driver, element);
                    System.out.println("Result matched row clicked");
                    matchfound = true;
                    element.click();
                    break;
                }
            }
        }
        Assert.assertTrue(matchfound, "Matched row not found for the text: " + Text);
    }*/

    /* public static String getCellDataFromTable(WebDriver driver, List<WebElement> rows, List<WebElement> columns, int row, int col) {
         int rowCount=rows.size();
         int colCount=columns.size();
         //WebElement cell=driver.findElement(By.xpath("//*[@id='policyDataGatherForm:dataGatherView_ListGroupClassGroupCoverRelationship_SubGroupsRating_data']/tr["+1+"]/td["+2"]")
         if(rowCount>0) {
             WebElement cell=rows.get(row-1).findElements(By.tagName("td")).get(col);
             return cell.getText();
         }else{
             System.out.println("No table presented");
             return null;
         }
     }*/

    public static void scrollToTopOfPage(WebDriver driver) {
        try {
            Actions a = new Actions(driver);
            a.sendKeys(Keys.HOME).build().perform();
        } catch (Exception e) {
            System.out.println("Scroll in the element not supporting or working");
        }
        csleep(1000);
    }

    public static void handleSFErrorPopup(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            List<WebElement> popup = driver.findElements(By.xpath("//h1[text()='Sorry to interrupt']"));
            if (!popup.isEmpty()) {
                WebElement oKBtn =
                        wait.until((ExpectedConditions.elementToBeClickable(By.xpath
                                ("//h1[text()='Sorry to interrupt']/ancestor::div//button[@data-aura-class='uiButton--default uiButton--neutral uiButton forceActionButton']"))));
                oKBtn.click();
                System.out.println("SF error pop-up detected and closed");
            }
        } catch (Exception e) {
            System.out.println("SF error pop-up NOT detected");
        }
        base.Common.waitForPageLoaded(driver, 30);
    }

    public static void waitForElementOrRefresh(WebDriver driver, By locator, int waitSeconds) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            System.out.println("Element appeared. Proceeding...");
        } catch (TimeoutException e) {
            System.out.println("Element not found. Refreshing page...");
            // Let the browser finish whatever it's doing
            Thread.sleep(1500);
            try {
                ((JavascriptExecutor) driver).executeScript("location.reload()");
            } catch (Exception ex) {
                System.out.println("JS refresh failed, retrying...");
                Thread.sleep(2000);
                ((JavascriptExecutor) driver).executeScript("location.reload()");
            }
        }
    }

    public static void safeRefresh(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        // Refresh using WebDriver (much more stable than JS)
        driver.navigate().refresh();
        // Wait for DOM to be ready
        wait.until(webDriver ->
                ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
    }

    public static void handleTryagainPopup(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            List<WebElement> popup = driver.findElements(By.xpath("//div[text()='Check your Internet connection and try again.']"));
            if (!popup.isEmpty()) {
                highLighterMethod(driver, By.xpath("//div[text()='Check your Internet connection and try again.']"));
                WebElement tryAgain =
                        wait.until((ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Check your Internet connection and try again.']/../..//button[@data-aura-class='uiButton']"))));
                tryAgain.click();
                System.out.println("tryAgain  pop-up detected and closed");
            }
        } catch (Exception e) {
            System.out.println("tryAgain error pop-up NOT detected");
        }
        base.Common.waitForPageLoaded(driver, 30);
    }

    public static Map<String, String> getRuleResultMap() {
        WebDriver driver = null;
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,1000)");
        List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("//article[@aria-label='Eligibility Rule Results']//table//tbody//tr")));
        Map<String, String> ruleResultMap = new HashMap<>();
        for (WebElement row : rows) {
            try {
                String rule = row.findElement(By.xpath(".//th[@data-label='Rule']//span")).getText().trim();
                String result = row.findElement(By.xpath(".//td[@data-label='Result']//span")).getText().trim();
                ruleResultMap.put(rule, result); // Store all rules and results in the map
            } catch (Exception e) {
                System.out.println("Skipping row due to missing element: " + e.getMessage());
            }
        }
        return ruleResultMap;
    }

    public static void verifyRule(Map<String, String> ruleResultMap, String expectedRule, String expectedResult) {
        for (Map.Entry<String, String> entry : ruleResultMap.entrySet()) {
            if (entry.getKey().toLowerCase().contains(expectedRule.toLowerCase())) {
                String actualResult = entry.getValue();
                if (actualResult.equalsIgnoreCase(expectedResult)) {
                    System.out.println("PASS: '" + expectedRule + "' => " + actualResult);
                } else {
                    System.out.println("FAIL: '" + expectedRule + "' expected: " + expectedResult + ", found: " + actualResult);
                    Assert.assertEquals(actualResult, expectedResult, "Rule or status is different");
                }
                return;
            }
        }
        System.out.println("Rule '" + expectedRule + "' not found!");
        Assert.fail("Rule not found");
    }

}

