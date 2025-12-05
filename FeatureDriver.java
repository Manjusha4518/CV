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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FeatureDriver {

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
    protected static final String BLACK = "\033[0;30m";
    protected static final String RED = "\033[0;31m";
    protected static final String GREEN = "\033[0;32m";
    protected static final String YELLOW = "\033[0;33m";
    protected static final String BLUE = "\033[0;34m";
    protected static final String PURPLE = "\033[0;35m";
    protected static final String CYAN = "\033[0;36m";
    protected static final String WHITE = "\033[0;37m";
    protected static final String GREEN_BOLD = "\033[1;32m";
    protected static final String YELLOW_BOLD = "\033[1;33m";
    protected static final String BLUE_BOLD = "\033[1;34m";
    protected static final String PURPLE_BOLD = "\033[1;35m";
    protected static final String CYAN_BOLD = "\033[1;36m";
    protected static final String BLACK_BOLD_BRIGHT = "\033[1;90m";
    protected static final String RED_BOLD_BRIGHT = "\033[1;91m";
    protected static final String GREEN_BOLD_BRIGHT = "\033[1;92m";
    protected static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";
    protected static final String BLUE_BOLD_BRIGHT = "\033[1;94m";
    protected static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";
    protected static final String CYAN_BOLD_BRIGHT = "\033[1;96m";
    protected static final String WHITE_BOLD_BRIGHT = "\033[1;97m";

    String prod = "";

    public FeatureDriver() {}

    @RetryAcceptance
    public static boolean retryCheck(Throwable e) {
        return !e.getMessage().contains("Configuration failed");
    }

    @Parameters({"env", "testPlanName", "browserName", "quitbrowserperfeature", "tags",
            "minimize", "headless", "browserSize", "feature"})
    @BeforeSuite
    public void parentSetUp(@Optional("") String environment, @Optional("") String testPlanName,
                            String browserName, @Optional("yes") String quitbrowserperfeature, @Optional("") String tagsFromUser,
                            @Optional("") String minimize, @Optional("no") String isDriverHeadless,
                            @Optional("") String browserSize, @Optional("") String feature) throws Exception {

        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ Before SUITE TestNG ]--------------------");

        env = environment;
        config = new Config(env);
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

        cn.removedir(Config.defaultdownloaddir);
        cn.mkdir(Config.defaultdownloaddir);
        cn.mkdir(Config.defaultdownloadArchivedir);

        cn.createCSVResultTemplate();
        System.out.println("----> Report CSV path: " + base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);

        System.out.println("User provided feature detail: " + feature);

        if (tagsFromUser != null && !tagsFromUser.trim().equalsIgnoreCase("")) {
            tcTags = base.Common.getTagsFormatted(tagsFromUser);
        } else {
            if (feature != null && !feature.trim().equalsIgnoreCase("")) {
                feature = feature.replaceAll(";", ":").replaceAll(",", ":");
                if (!feature.contains("\\.")) {
                    if (feature.contains(":")) {
                        String fe[] = feature.replaceFirst(":", "@").split("@");
                        cucumberFeature = cucumberFeature + "/" + fe[0] + ".feature" + ":" + fe[1];
                    } else {
                        feature = feature.trim() + ".feature";
                        cucumberFeature = cucumberFeature + "/" + feature.trim();
                    }
                } else {
                    cucumberFeature = cucumberFeature + "/" + feature.trim();
                }
            } else {
                tcTags = base.Common.getTags(runFromTP, tagsFromUser, testPlanName, Config.CUCUMBER_OPTION_TAGS);
            }
        }

        String tagOption = (tcTags != null && !tcTags.equalsIgnoreCase("")) ? "--tags " + tcTags + " " : "";

        System.setProperty("cucumber.options",
                cucumberFeature + " " +
                        "-m " +
                        "-s " +
                        "--glue " + cucumberGlue + " " +
                        tagOption +
                        "--plugin pretty:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/cucumber-pretty.txt " +
                        "--plugin json:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/Cucumber.json " +
                        "--plugin junit:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/cucumber-junit-results.xml " +
                        "--plugin base.CustomFormatter " +
                        "--plugin rerun:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/failed_features.txt"
        );

        Common.driverSetting(browser);
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass(ITestContext context) throws Exception {

        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());

        if (tcTags != null && !tcTags.equalsIgnoreCase("")) {
            tcSheetMapsFromTD = cn.getTCSheetsMapFromTP(tcTags.replaceAll("@", "").split(","));
        } else {
            tcSheetMapsFromTD = cn.getTCSheetsMap();
        }

        Date Date = new Date();
        startExeTimeDate = sdf.format(Date);
        startExeTimeStr = String.valueOf((Date).getTime());

        if (!new File(Config.dynamicdatafolderpath).exists())
            (new File(Config.dynamicdatafolderpath)).mkdirs();

        if (testPlan.contains("VHI")) prod = "VHI";
        else if (testPlan.contains("VAI")) prod = "VAI";
        else if (testPlan.contains("VCI")) prod = "VCI";
        else if (testPlan.contains("STD")) prod = "STD";
        else if (testPlan.contains("SMP")) prod = "SMP";
        else if (testPlan.contains("VPS")) prod = "VPS";
        else if (testPlan.contains("ORL")) prod = "ORL";
        else if (testPlan.contains("BACKLOG")) prod = "VPS";

        System.out.println(BLUE + "--- E2E related dynamic datafile handling configuration---------" + COLOR_RESET);

        try {
            if (testPlan.contains("e2eAfter")) {
                File from = null;

                if (testPlan.contains("VHI"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_HI);
                else if (testPlan.contains("VAI"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_AC);
                else if (testPlan.contains("VCI"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_CI);
                else if (testPlan.contains("STD"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_STD);
                else if (testPlan.contains("VPS"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VPS);
                else if (testPlan.contains("TDI"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_TDI);
                else if (testPlan.contains("DBL"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_DBL);
                else if (testPlan.contains("MAL"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_MAL);
                else if (testPlan.contains("TDB"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_TDB);
                else if (testPlan.contains("CTL"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_CTL);
                else if (testPlan.contains("ORL"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_ORL);
                else if (testPlan.contains("BACKLOG"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_BACKLOG);
                else if (testPlan.contains("LTD"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_LTD);
                else if (testPlan.contains("ASL"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_ASL);
                else if (testPlan.contains("GL"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_GL);
                else if (testPlan.contains("VAR"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VAR);
                else if (testPlan.contains("VG"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VG);
                else if (testPlan.contains("VPL"))
                    from = new File(Config.dynamicdatafoldersharedpath + "/" + Config.CustomerDatafileName_VPL);

                File to = new File(Config.dynamicdatafolderpath);

                if (!from.exists())
                    throw new FileNotFoundException("Property File not found: " + Config.dynamicdatafoldersharedpath);

                FileUtils.copyFileToDirectory(from, to);

                System.out.println("Downloaded dynamic data file shared to local File Name: " +
                        from.getName() + " For Product: " + prod);
            }
        } catch (Exception e) {
            System.out.println("Exception while moving files: " + e.getMessage());
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        WebDriver wd;
        Hooks.loginloop = 0;

        if (Config.driverPerFeature.trim().equalsIgnoreCase("yes") ||
                Config.driverPerFeature.trim().equalsIgnoreCase("y")) {

            base.Common.defaultFileDownloadiDir = Config.defaultdownloaddir;

            wd = base.Common.getDriver(browser);
            wd.manage().window().maximize();

            if (isMinimize != null && (isMinimize.trim().equalsIgnoreCase("yes") ||
                    isMinimize.trim().equalsIgnoreCase("y")))
                wd.manage().window().minimize();

            Steps.driver = wd;

            if (isBRSize != null &&
                    (isBRSize.trim().equalsIgnoreCase("yes") ||
                            isBRSize.trim().equalsIgnoreCase("y"))) {
                wd.manage().window().setSize(new Dimension(800, 500));
            }
        }
    }

    @Test(groups = "CV", description = "CV Features", dataProvider = "features")
    public void feature(CucumberFeatureWrapper cucumberFeature) {
        featureName = cucumberFeature.getCucumberFeature().getGherkinFeature().getName();
        featureFilePath = cucumberFeature.getCucumberFeature().getPath();

        testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTest() {
        try {
            if (quitbrowser.trim().contains("y")) {
                Steps.driver.quit();
                base.Common.sleep(3000);
            }
        } catch (Exception e) {
            System.out.println("Error closing browser");
        }

        try {
            if (quitbrowser.trim().contains("y")) {
                base.Common.closeOpenBrowsers(browser);
                base.Common.sleep(2000);
            }
        } catch (Exception e) {
            System.out.println("Error killing browser");
        }
    }

    @DataProvider(name = "features")
    public Object[][] features() {
        return testNGCucumberRunner.provideFeatures();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {

        endExeTimeDate = sdf.format(new Date());

        String endTimestr = String.valueOf((new Date()).getTime());
        try {
            double duration = Common.getTotalExecutionTime(startExeTimeStr, endTimestr, "min");
            Common.writeToFile(base.Config.exedurationfilepath, String.valueOf(duration), false);
        } catch (Exception e) {
            System.out.println("Error writing duration");
        }

        testNGCucumberRunner.finish();

        try {
            if (testPlan.contains("e2eAfter")) {

                System.out.println("Uploading dynamic data folder from local to shared...");

                File from = null;
                String sharedExistingFilePath = "";
                File to = new File(Config.dynamicdatafoldersharedpath);

                if (testPlan.contains("VHI")) {
                    from = new File(Config.CustomerDatafilePath_HI);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_HI;
                } else if (testPlan.contains("VAI")) {
                    from = new File(Config.CustomerDatafilePath_AC);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_AC;
                } else if (testPlan.contains("VCI")) {
                    from = new File(Config.CustomerDatafilePath_CI);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_CI;
                } else if (testPlan.contains("STD")) {
                    from = new File(Config.CustomerDatafilePath_STD);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_STD;
                } else if (testPlan.contains("VPS")) {
                    from = new File(Config.CustomerDatafilePath_VPS);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_VPS;
                } else if (testPlan.contains("TDI")) {
                    from = new File(Config.CustomerDatafilePath_TDI);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_TDI;
                } else if (testPlan.contains("DBL")) {
                    from = new File(Config.CustomerDatafilePath_DBL);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_DBL;
                } else if (testPlan.contains("MAL")) {
                    from = new File(Config.CustomerDatafilePath_MAL);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_MAL;
                } else if (testPlan.contains("TDB")) {
                    from = new File(Config.CustomerDatafilePath_TDB);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_TDB;
                } else if (testPlan.contains("CTL")) {
                    from = new File(Config.CustomerDatafilePath_CTL);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_CTL;
                } else if (testPlan.contains("BACKLOG")) {
                    from = new File(Config.CustomerDatafilePath_BACKLOG);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_BACKLOG;
                } else if (testPlan.contains("LTD")) {
                    from = new File(Config.CustomerDatafilePath_LTD);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_LTD;
                } else if (testPlan.contains("ASL")) {
                    from = new File(Config.CustomerDatafilePath_ASL);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_ASL;
                } else if (testPlan.contains("GL")) {
                    from = new File(Config.CustomerDatafilePath_GL);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_GL;
                } else if (testPlan.contains("VAR")) {
                    from = new File(Config.CustomerDatafilePath_VAR);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_VAR;
                } else if (testPlan.contains("VG")) {
                    from = new File(Config.CustomerDatafilePath_VG);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_VG;
                } else if (testPlan.contains("VPL")) {
                    from = new File(Config.CustomerDatafilePath_VPL);
                    sharedExistingFilePath = to + "/" + Config.CustomerDatafileName_VPL;
                }

                DateFormat dateformat = new SimpleDateFormat("YYYYMMddHHmmss");
                File backupFile = new File(sharedExistingFilePath + "_" + dateformat.format(new Date()));
                FileUtils.copyFile(new File(sharedExistingFilePath), backupFile);

                base.Common.sleep(2000);
                FileUtils.copyFileToDirectory(from, to);
            }
        } catch (Exception e) {
            System.out.println("Error moving dynamic files: " + e.getMessage());
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void tearDownSuite() throws Exception {

        cn.csvWriterFromArrayList(resultData, base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);

        Reportable results = cn.cucumberReportsHtml(
                base.Config.REPORT_CUCUMBERHTML_PATH,
                base.Config.REPORT_CUCUMBERHTML_PATH + "/Cucumber.json",
                "",
                Config.SUITE_APPNAME,
                "Sprint 2,3,4,5,6",
                base.Config.OS_NAME,
                base.Config.testbrowserAndVersion,
                "R1");

        try {
            String cucumberHTLFilepath =
                    (new File(base.Config.REPORT_CUCUMBERHTML_PATH)
                            .getAbsolutePath() + "/cucumber-html-reports/overview-features.html").replaceAll("\\\\", "/");

            System.out.println(
                    CYAN_BOLD_BRIGHT + "\n============< Cucumber Report >=========================== \n"
                            + COLOR_RESET + "file:///" + cucumberHTLFilepath
                            + CYAN_BOLD_BRIGHT + "\n==============================================================\n"
                            + COLOR_RESET);
        } catch (Exception e) {}
    }

    // 🔥🔥🔥 ADDED FIX — SAFE WRITE METHOD 🔥🔥🔥
    public static void writeDynamicData(String filePath, String key, String value) {
        Properties props = new Properties();
        File file = new File(filePath);

        try {
            // 1. Load existing data
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                props.load(in);
                in.close();
            }

            // 2. Add or update key-value
            props.setProperty(key, value);

            // 3. Save back merged data (no overwrite, no deletion)
            FileOutputStream out = new FileOutputStream(file);
            props.store(out, "Updated Dynamic Data");
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}  // END CLASS
