package cv.Driver;

import base.Common;
import com.github.mkolisnyk.cucumber.runner.RetryAcceptance;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import cv.Common.Config;
import cv.StepDefinition.Hooks;
import cv.StepDefinition.Steps;
import cv.Common.LegacyDBFunctions;
import net.masterthought.cucumber.Reportable;
import org.apache.commons.io.FileUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
//import cv.common.base.Common;


/*@ExtendedCucumberOptions(
        jsonReport = "testResult/cucumberReport/Cucumber.json",

        //retryCount = 2,
        detailedReport = true,
        detailedAggregatedReport = true,
        overviewReport = true,
        //coverageReport = true,
        //jsonUsageReport = "target/cucumber-usage.json",
        //usageReport = true,
        screenShotLocation = "testResult/cucumberReport/",
        toPDF = true,
        screenShotSize = "50%",
        //excludeCoverageTags = {"@flaky" },
        //includeCoverageTags = {"@passed" }
        outputFolder = "testResult/cucumberReport"
)*/

public class FeatureDriver {
    //public class FeatureDriver {
    //public static String OSName = System.getProperty("os.name")+" ("+System.getProperty("os.arch")+")";
//    static final Logger log = LogManager.getLogger(FeatureDriver.class);
    public static WebDriver driver = null;
    private static String cucumberFeature = Config.CUCUMBER_OPTION_FEATURES;
    private static final String cucumberGlue = Config.CUCUMBER_OPTION_GLUE;
    private static final String runFromTP = Config.RUN_FROM_TESTPLAN;
    private TestNGCucumberRunner testNGCucumberRunner;
    public static String env;
    public static String browser;
    public static String testPlan;
    private static final String testPlanRootdir = base.Config.TESTPLAN_ROOT_PATH;
    private static final String testPlanPath = base.Config.TESTPLAN_FILE_PATH;
    public static String startExeTimeDate;
    public static String startExeTimeStr;
    public static String endExeTimeDate;
    public static String featureName;
    private static String tcTags;
    private static final String excelReportPath = base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH;
    private static final String sheetNameTP = base.Config.RUNSHEET_FROM_TESTPLAN;
    static base.Common cn = new base.Common();
    private static String quitbrowser = "";
    //Properties con;
    public static ArrayList<String[]> resultData = new ArrayList<>();
    public static boolean openNewDriver = true;
    public static boolean requiredToLogin = true;
    public static String featureFilePath;
    public static LegacyDBFunctions legacyDB = new LegacyDBFunctions();
    public static HashMap<String, String> tcSheetMapsFromTD = new HashMap<>();
    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static String defaultFileDownloadiDir = null;
    public static Config config;
    public static String isMinimize;
    public static String isBRSize;
    public static String browserSize;
    public static int browserWidth;
    public static int browserHeight;
    protected final static String COLOR_RESET = "\u001B[0m";
    // Regular Colors
    protected static final String BLACK = "\033[0;30m";   // BLACK
    protected static final String RED = "\033[0;31m";     // RED
    protected static final String GREEN = "\033[0;32m";   // GREEN
    protected static final String YELLOW = "\033[0;33m";  // YELLOW
    protected static final String BLUE = "\033[0;34m";    // BLUE
    protected static final String PURPLE = "\033[0;35m";  // PURPLE
    protected static final String CYAN = "\033[0;36m";    // CYAN
    protected static final String WHITE = "\033[0;37m";   // WHITE
    protected static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    protected static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    protected static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    protected static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    protected static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    // Bold High Intensity
    protected static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    protected static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    protected static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    protected static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    protected static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    protected static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    protected static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    protected static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    //public static int loginloop=0;
    String prod = "";

    public FeatureDriver() {
        //PropertyConfigurator.configure(Config.LOG4J_CONFIG_PATH);
    }

    @RetryAcceptance
    public static boolean retryCheck(Throwable e) {
        // Does not allow re-run if error message contains "Configuration failed" phrase
        return !e.getMessage().contains("Configuration failed");
    }

    @Parameters({"env", "testPlanName", "browserName", "quitbrowserperfeature", "tags", "minimize", "headless", "browserSize", "feature"})
    @BeforeSuite
    public void parentSetUp(@Optional("") String environment, @Optional("") String testPlanName, String browserName, @Optional("yes") String quitbrowserperfeature, @Optional("") String tagsFromUser, @Optional("") String minimize,
                            @Optional("no") String isDriverHeadless, @Optional("") String browserSize, @Optional("") String feature) throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ Before SUITE TestNG ]--------------------");
        System.out.println("    ---->User input: Execution Environment from POM parameter: " + environment);
        System.out.println("    ---->User input: Test Plan excel Name from POM parameter: " + testPlanName);
        System.out.println("    ---->User input: Browser Name from POM parameter: " + browserName);
        System.out.println("    ---->User input: Quit Browser Selection from POM parameter: " + quitbrowserperfeature);
        System.out.println("    ---->User input: Tags from POM parameter: " + tagsFromUser);
        System.out.println("    ---->User input: Browser Minimize Selection from POM parameter: " + minimize);
        System.out.println("    ---->User input: Headless browser selection from POM parameter: " + isDriverHeadless);
        System.out.println("    ---->User input: Browser Size from POM parameter: " + browserSize);
        System.out.println("    ---->User input: feature detail from POM parameter: " + feature);
        System.out.println("---------------------------------------------------------------------" + COLOR_RESET);
        env = environment;
        config = new Config(env);
        //legacyDB=new LegacyDBFunctions();
        Config.ENV = env;
        if (Config.userSleepMS != null && !Config.userSleepMS.equalsIgnoreCase(""))
            Common.userSleepMS = Config.userSleepMS;
        if (isDriverHeadless != null && isDriverHeadless.contains("y")) {
            base.Config.isDriverHeadless = isDriverHeadless;
        } else if (isDriverHeadless != null && isDriverHeadless.contains("n")) {
            base.Config.isDriverHeadless = "no";
        } else {
            if (Config.isDriverHeadless != null)
                base.Config.isDriverHeadless = Config.isDriverHeadless;
        }
        //For ajax load for EIS check
        if (Config.SUITE_APPNAME != null && !Config.SUITE_APPNAME.isEmpty())
            Common.appName = Config.SUITE_APPNAME;
//        Common.appName= "eis";
        System.out.println("URL: " + Config.URL);
        System.out.println("UN: " + Config.userName);
        System.out.println("PW: " + Config.passWord);
        //System.out.println("username1: "+config.URL);
        browser = browserName;
        testPlan = testPlanName;
        quitbrowser = quitbrowserperfeature;
        isMinimize = minimize;
        this.browserSize = browserSize;
        base.Common.closeExcel();
        base.Common.closeOpenBrowsers(browserName);
        cn.mkdir(base.Config.REPORT_DIR);
        cn.removedir(base.Config.SCREENSHOTSDIR);
        cn.mkdir(base.Config.SCREENSHOTSDIR);
        cn.moveToArchiveExecutionSummary();
        //-specific to CV------
        cn.removedir(Config.defaultdownloaddir);
        cn.mkdir(Config.defaultdownloaddir);
        cn.mkdir(Config.defaultdownloadArchivedir);
        System.out.println("----> Report CSV path: " + base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
        try {
            cn.createCSVResultTemplate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tcTags=base.Common.getTags(runFromTP,tagsFromUser,testPlanName,Config.CUCUMBER_OPTION_TAGS);
        //System.out.println("---> Tags collection : "+tcTags);
        System.out.println("User provided feature detail: " + feature);
        if (tagsFromUser != null && !tagsFromUser.trim().equalsIgnoreCase("")) {
            tcTags = base.Common.getTagsFormatted(tagsFromUser);
            System.out.println("--> User TAGS there, so user TAGS considered as first priority(Test Plan and Feature are skipped) : [" + tcTags + "]");
        } else {
            if (feature != null && !feature.trim().equalsIgnoreCase("")) {
                feature = feature.replaceAll(";", ":").replaceAll(",", ":");
                if (!feature.contains("\\.")) {
                    if (feature.contains(":")) {
                        String fe[] = feature.replaceFirst(":", "@").split("@");
                        cucumberFeature = cucumberFeature + "/" + fe[0] + ".feature" + ":" + fe[1];
                        System.out.println("--> User TAGS Not provided, Feature has LINE to execute : [" + feature + "]");
                    } else {
                        feature = feature.trim() + ".feature";
                        cucumberFeature = cucumberFeature + "/" + feature.trim();
                        System.out.println("--> User TAGS Not provided, Feature has NO LINE so Whole feature to execute : [" + feature + "]");
                    }
                } else {
                    cucumberFeature = cucumberFeature + "/" + feature.trim();
                    System.out.println("--> User TAGS Not provided, Feature value taken to execute : [" + feature + "]");
                }
                System.out.println("Feature detail to run: " + cucumberFeature);
            } else {
                tcTags = base.Common.getTags(runFromTP, tagsFromUser, testPlanName, Config.CUCUMBER_OPTION_TAGS);
                System.out.println("--> User TAGS and Feature Not provided, So this will read Test Plan to take TAGS to execute : [" + tcTags + "]");
            }
        }
        System.out.println("-------- SET CUCUMBER OPTIONS ---------------");
        System.out.println("Tags--> [" + tcTags + "]");
        System.out.println("Feature--> [" + cucumberFeature + "]");
        System.out.println("Testplan/Profile --> [" + testPlanName + "]");
        System.out.println("Glue--> [" + cucumberGlue + "]");
        System.out.println("---------------------------------------------");
        String tagOption = (tcTags != null && !tcTags.equalsIgnoreCase("")) ? "--tags " + tcTags + " " : "";
        System.setProperty("cucumber.options", cucumberFeature + " " +
                "-m " +
                "-s " +
                "--glue " + cucumberGlue + " " +
                tagOption +
                //"--tags "+tcTags+" "+
                "--plugin pretty:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/cucumber-pretty.txt " +
                //"--plugin html:"+Config.REPORT_CUCUMBERHTML_PATH+"/html " +
                "--plugin json:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/Cucumber.json " +
                "--plugin junit:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/cucumber-junit-results.xml " +
                "--plugin base.CustomFormatter " +
                "--plugin rerun:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/failed_features.txt"
        );
        Common.driverSetting(browser);
        //System.out.println("-------------- [ END ]--------------------");
        //  "-monochrome true "+
        //          "-strict true "+
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass(ITestContext context) throws Exception {
        //context.getCurrentXmlTest().getSuite().setDataProviderThreadCount(2);
        //context.getCurrentXmlTest().getSuite().setPreserveOrder(false);
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ Before Class Test NG ]--------------------" + COLOR_RESET);
        //browser=browserName.trim().toUpperCase();
        System.out.println("---> TestPlan name  from TestNG: " + testPlan);
        System.out.println("---> Browser Parameter from TestNG: " + browser);
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        if (tcTags != null && !tcTags.equalsIgnoreCase("")) {
            System.out.println("Tags provided so Get the TC sheets map for the tags from test data sheet");
            tcSheetMapsFromTD = cn.getTCSheetsMapFromTP(tcTags.replaceAll("@", "").split(","));
        } else {
            System.out.println("No tags provided so Get the whole TC sheets map from test data sheet");
            tcSheetMapsFromTD = cn.getTCSheetsMap();
        }
        Date Date = new Date();
        startExeTimeDate = sdf.format(Date);
        startExeTimeStr = String.valueOf((Date).getTime());
        try {
            if (!new File(Config.dynamicdatafolderpath).exists())
                (new File(Config.dynamicdatafolderpath)).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (testPlan.contains("VHI")) {
                prod = "VHI";
            } else if (testPlan.contains("VAI")) {
                prod = "VAI";
            } else if (testPlan.contains("VCI")) {
                prod = "VCI";
            } else if (testPlan.contains("STD")) {
                prod = "STD";
                System.out.println("ThisisSTD");
            } else if (testPlan.contains("SMP")) {
                prod = "SMP";
            } else if (testPlan.contains("VPS")) {
                prod = "VPS";
            } else if (testPlan.contains("ORL")) {
                prod = "ORL";
            } else if (testPlan.contains("BACKLOG")) {
                prod = "VPS";
            }
            System.out.println(BLUE + "--- E2E related dynamic datafile handling configuration---------" + COLOR_RESET);
            if (testPlan.contains("e2eAfter")) {
                File from = null;
                if (testPlan.contains("VHI")) {
                    //prod="VHI";
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_HI);
                } else if (testPlan.contains("VAI")) {
                    //prod="VAI";
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_AC);
                } else if (testPlan.contains("VCI")) {
                    //prod="VCI";
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_CI);
                } else if (testPlan.contains("STD")) {
                    System.out.println("ThisisSTDafterEIS");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_STD);
                } else if (testPlan.contains("VPS")) {
                    System.out.println("ThisisSTDafterEISVPS");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VPS);
                } else if (testPlan.contains("TDI")) {
                    System.out.println("ThisisSTDafterEITDI");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_TDI);
                } else if (testPlan.contains("DBL")) {
                    System.out.println("ThisisSTDafterEIDBL");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_DBL);
                } else if (testPlan.contains("MAL")) {
                    System.out.println("ThisisSTDafterEIMAL");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_MAL);
                } else if (testPlan.contains("TDB")) {
                    System.out.println("ThisisTDbafterEITDB");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_TDB);
                } else if (testPlan.contains("CTL")) {
                    System.out.println("ThisisSTDafterEICTL");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_CTL);
                } else if (testPlan.contains("ORL")) {
                    System.out.println("ThisisSTDafterEIORL");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_ORL);
                } else if (testPlan.contains("BACKLOG")) {
                    System.out.println("ThisisBACKLOGTestcases");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_BACKLOG);
                } else if (testPlan.contains("LTD")) {
                    System.out.println("ThisisLTDTestcases");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_LTD);
                } else if (testPlan.contains("ASL")) {
                    System.out.println("ThisisASLTestcases");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_ASL);
                } else if (testPlan.contains("GL")) {
                    System.out.println("ThisisGLTestcases");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_GL);
                } else if (testPlan.contains("VAR")) {
                    System.out.println("ThisisVARTestcases");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VAR);
                } else if (testPlan.contains("VG")) {
                    System.out.println("ThisisVGTestcases");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VG);
                } else if (testPlan.contains("VPL")) {
                    System.out.println("ThisisVPLTestcases");
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VPL);
                }
                File to = new File(Config.dynamicdatafolderpath);
                if (!from.exists())
                    throw new FileNotFoundException("Property File not fond: " + Config.dynamicdatafoldersharedpath);
                FileUtils.copyFileToDirectory(from, to);
                System.out.println("Downloaded dynamic data file shared to local File Name: " + from.getName() + " For Product: " + prod);
                //File from = new File(Config.dynamicdatafoldersharedpath);
                //File to = new File(Config.dynamicdatafolderpath);
                //FileUtils.copyDirectory(from,to);
                System.out.println("Dynamicdata property files moved from: " + from + " To: " + to);
            }
        } catch (Exception e) {
            System.out.println("Exceptionwhilemovefilesfromlocaltosharedfolder: " + e.getCause().toString());
        }
        System.out.println("Start time of Before Class: " + startExeTimeDate);
        //System.out.println("-------------- [ END ]--------------------");
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ Before Method Test NG ]--------------------" + COLOR_RESET);
        WebDriver wd;
        Hooks.loginloop = 0;
//        Hooks.quoteloop=0;
        //openNewDriver=true;
        if (Config.driverPerFeature.trim().equalsIgnoreCase("yes") || Config.driverPerFeature.trim().equalsIgnoreCase("y")) {
            System.out.println(BLUE + "---->  Driver open per Feature/userstory ----" + COLOR_RESET);
            base.Common.defaultFileDownloadiDir = Config.defaultdownloaddir;
            //wd=cn.getDriver(browser);
            wd = base.Common.getDriver(browser);
            wd.manage().window().maximize();
            if (isMinimize != null && (isMinimize.trim().equalsIgnoreCase("yes") || isMinimize.trim().equalsIgnoreCase("y")))
                wd.manage().window().minimize();
            Steps.driver = wd;
            if (isBRSize != null && (isBRSize.trim().equalsIgnoreCase("yes") || isBRSize.trim().equalsIgnoreCase("y")))
                wd.manage().window().setSize(new Dimension(800, 500));
            Steps.driver = wd;
        }
        //System.out.println("-------------- [ END ]----------------------------------------------------------------");
    }

    @Test(groups = "CV", description = "CV Features", dataProvider = "features")
    public void feature(CucumberFeatureWrapper cucumberFeature) {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ TEST TestNG ] -------------------------------" + COLOR_RESET);
        featureName = cucumberFeature.getCucumberFeature().getGherkinFeature().getName();
        featureFilePath = cucumberFeature.getCucumberFeature().getPath();
        System.out.println("----- [Start Feature: " + featureName + "  - Filepath: " + featureFilePath + " ]---------");
        testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
        System.out.println("-------------- [ END TEST]----------------------------------------------------------------");
    }

    //@Parameters("browserName")
    @AfterMethod(alwaysRun = true)
    public void tearDownTest() {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ After Method Test NG ]--------------------" + COLOR_RESET);
        System.out.println(YELLOW + "Driver close required in feature level: " + quitbrowser + COLOR_RESET);
        try {
            //Close driver per feature
            if (quitbrowser.trim().contains("y")) {
                System.out.println(YELLOW_BOLD + "---->  Driver closed per Feature ----" + COLOR_RESET);
                //driver.quit();
                Steps.driver.quit();
                base.Common.sleep(3000);
            } else
                System.out.println("Driver is not closed as per parameter");
        } catch (Exception e) {
            System.out.println(RED + "---->  !!! EXCEPTION: Some problem in quiting browser ----" + COLOR_RESET);
        }
        try {
            //Close driver per feature
            if (quitbrowser.trim().contains("y")) {
                System.out.println(YELLOW_BOLD + "---->  Driver killed per Feature ----" + COLOR_RESET);
                base.Common.closeOpenBrowsers(browser);
                base.Common.sleep(2000);
            } else
                System.out.println("Driver is not killed as per parameter");
        } catch (Exception e) {
            System.out.println(RED + "---->  !!! EXCEPTION: Some problem in closing browser from kill task ----" + COLOR_RESET);
        }
        // System.out.println("--------------[ END ]-------------------------------------------------------------------------");
    }

    //@DataProvider(parallel = true)
    @DataProvider(name = "features")
    public Object[][] features() {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ DataProvider Test NG ]--------------------" + COLOR_RESET);
        //System.out.println("This is TestNG Data provider reading Feature file to get looped based on number of feature file");
        System.out.println("---->  DATAPROVIDER: This is @DataProvider start, execute each cucumber feature files in iterations");
        System.out.println("Total features: " + testNGCucumberRunner.provideFeatures().length);
        return testNGCucumberRunner.provideFeatures();
    }
    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ After Class Test NG ]--------------------" + COLOR_RESET);
        endExeTimeDate = sdf.format(new Date());
        System.out.println("Execution completed time: " + endExeTimeDate);
        String endTimestr = String.valueOf((new Date()).getTime());
        try {
            double duration = Common.getTotalExecutionTime(startExeTimeStr, endTimestr, "min");
            Common.writeToFile(base.Config.exedurationfilepath, String.valueOf(duration), false);
        } catch (Exception e) {
            System.out.println("Some issue on calculating duration or issue on wirting value to file: " + e.getMessage());
        }
        testNGCucumberRunner.finish();
        System.out.println("----- [END Feature: " + featureName + " ]---------");
        try {
        } catch (Exception e) {
            System.out.println("---->  !!! EXCEPTION: Some problem in closing browser from killtask ----");
        }
        try {
            if (testPlan.contains("e2eAfter")) {
                System.out.println("Uploading dynaic data folder from local to shared location");
                File from = null;
                String sharedExistingFilePath = "";
                File to = new File(Config.dynamicdatafoldersharedpath);
                if (testPlan.contains("VHI")) {
                    from = new File(Config.CustomerDatafilePath_HI);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_HI;
                } else if (testPlan.contains("VAI")) {
                    from = new File(Config.CustomerDatafilePath_AC);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_AC;
                } else if (testPlan.contains("VCI")) {
                    from = new File(Config.CustomerDatafilePath_CI);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_CI;
                } else if (testPlan.contains("STD")) {
                    from = new File(Config.CustomerDatafilePath_STD);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_STD;
                } else if (testPlan.contains("VPS")) {
                    from = new File(Config.CustomerDatafilePath_VPS);
                    System.out.println("VPSfrom-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_VPS;
                    System.out.println("sharedExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("TDI")) {
                    from = new File(Config.CustomerDatafilePath_TDI);
                    System.out.println("TDIfrom-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_TDI;
                    System.out.println("sharedExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("DBL")) {
                    from = new File(Config.CustomerDatafilePath_DBL);
                    System.out.println("DBLfrom-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_DBL;
                    System.out.println("sharedExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("MAL")) {
                    from = new File(Config.CustomerDatafilePath_MAL);
                    System.out.println("MALfrom-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_MAL;
                    System.out.println("sharedExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("TDB")) {
                    from = new File(Config.CustomerDatafilePath_TDB);
                    System.out.println("TDBfrom-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_TDB;
                    System.out.println("sharedExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("CTL")) {
                    from = new File(Config.CustomerDatafilePath_CTL);
                    System.out.println("CTLfrom-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_CTL;
                    System.out.println("sharedExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("BACKLOG")) {
                    System.out.println("YesThisIsNewBacklogTestCasesPath");
                    from = new File(Config.CustomerDatafilePath_BACKLOG);
                    System.out.println("BACKLOGPath-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_BACKLOG;
                    System.out.println("backlogsharedExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("LTD")) {
                    System.out.println("YesThisIsNewLTDTestCasesPath");
                    from = new File(Config.CustomerDatafilePath_LTD);
                    System.out.println("LTDPath-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD;
                    System.out.println("LTDExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.equalsIgnoreCase("ASL")) {
                    System.out.println("YesThisIsNewASLTestCasesPath");
                    from = new File(Config.CustomerDatafilePath_ASL);
                    System.out.println("ASLPath-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_ASL;
                    System.out.println("ASLExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("GL")) {
                    System.out.println("YesThisIsNewLGLestCasesPath");
                    from = new File(Config.CustomerDatafilePath_GL);
                    System.out.println("GLPath-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GL;
                    System.out.println("GLExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("VPL")) {
                    System.out.println("YesThisIsNewLVPLestCasesPath");
                    from = new File(Config.CustomerDatafilePath_VPL);
                    System.out.println("vplPath-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_VPL;
                    System.out.println("LTDExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("VAR")) {
                    System.out.println("YesThisIsNewLVARestCasesPath");
                    from = new File(Config.CustomerDatafilePath_VAR);
                    System.out.println("VARPath-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_VAR;
                    System.out.println("VARExistingFilePath-->"+sharedExistingFilePath);
                } else if (testPlan.contains("VG")) {
                    System.out.println("YesThisIsNewLVGestCasesPath");
                    from = new File(Config.CustomerDatafilePath_VG);
                    System.out.println("VGPath-->"+from);
                    sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_VG;
                    System.out.println("VGExistingFilePath-->"+sharedExistingFilePath);
                }
                DateFormat dateformat = new SimpleDateFormat("YYYYMMddHHmmss");
                File backupFile = new File(sharedExistingFilePath + "_" + dateformat.format(new Date()));
                FileUtils.copyFile(new File(sharedExistingFilePath), backupFile);
                base.Common.sleep(2000);
                FileUtils.copyFileToDirectory(from, to);
                System.out.println("Dynamicdata property files moved from: " + from + " To: " + to);
            }
        } catch (Exception e) {
            System.out.println("Eception while move files from lcoal to shared folder: " + e.getMessage());
        }
        System.out.println("-------------- END --------------------");
    }

    @AfterSuite(alwaysRun = true)
    public static void tearDownSuite() throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ After SUITE TestNG ]--------------------" + COLOR_RESET);
        System.out.println("----> Updating result in SUMMARY REPORT EXCEL");
        cn.csvWriterFromArrayList(resultData, base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
        System.out.println(BLUE + "Add result data into Cucumber report and Generate Report" + COLOR_RESET);
        Reportable results = cn.cucumberReportsHtml(base.Config.REPORT_CUCUMBERHTML_PATH, base.Config.REPORT_CUCUMBERHTML_PATH + "/Cucumber.json", "", Config.SUITE_APPNAME, "Sprint 2,3,4,5,6", base.Config.OS_NAME, base.Config.testbrowserAndVersion, "R1");
        try {
            System.out.println(
                    "Execution duration: " + results.getDuration() + "\n" +
                            "Total features: " + results.getFeatures() + "\n" +
                            "Passed Features: " + results.getPassedFeatures() + "\n" +
                            "Failed features: " + results.getFailedFeatures() + "\n" +
                            "Passed Scenarios: " + results.getPassedScenarios() + "\n" +
                            "Failed Scenarios: " + results.getFailedScenarios()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String cucumberHTLFilepath = (((new File(base.Config.REPORT_CUCUMBERHTML_PATH)).getAbsolutePath()) + "/cucumber-html-reports/overview-features.html").replaceAll("\\\\", "/");
            System.out.println(CYAN_BOLD_BRIGHT + "\n============< Cucumber Report >=========================== \n" + COLOR_RESET + "file:///" + cucumberHTLFilepath + CYAN_BOLD_BRIGHT + "\n==============================================================\n" + COLOR_RESET);
        } catch (Exception e) {
        }
        base.Common.sleep(1000);
        //cn.detailCucumberReports(base.Config.REPORT_CUCUMBERHTML_PATH,"testResult/cucumberReport/Cucumber.json");
        //cn.detailCucumberReports(base.Config.REPORT_CUCUMBERHTML_PATH,"testResult/cucumberReport/Cucumber.json","/cucumber-html-reports/embeddings/","");
        //System.out.println("-------------- [ END ]-----------------------------------------------------");
    }
}