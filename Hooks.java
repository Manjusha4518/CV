package cv.StepDefinition;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cv.Common.Config;
import cv.Driver.FeatureDriver;
import org.apache.commons.lang.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;

public class Hooks {
//    private static final Logger log = LogManager.getLogger(Hooks.class);

    //public static WebDriver driver;
    public static String scDataTag;
    base.Common cn = new base.Common();
    Properties con;
    public static String tagTCID = "";
    public static Scenario sc;
    public Date beforeScExeTimeDate;
    public Date afterScExeTimeDate;
    double scenarioExeDuration;
    public static List<HashMap<String, String>> dataMap;
    private String scenarioTagID = "";
    private String scenariorunID = "";
    public static int iterationID;
    private String status;
    private String scenarioName;
    int countGetIDSplit = 0;
    int iteration;
    public static int loginloop = 0;
    public static int steploop = 0;
    public static int quoteloop = 0;
    public static String featureFileName = "";
    //String[] sceanrioidsplit;
    public static String message;
    public static Boolean isTCIDColumnExist = false;
    public static Boolean isTCNameColumnExist = false;
    public static Boolean isReadDataKeyColumnExist = false;
    protected final static String COLOR_RESET = "\u001B[0m";
    protected final static String GREEN = "\u001B[32m";
    protected final static String BLUE = "\u001B[34m";

    protected final static String YELLOW = "\u001B[33m";
    protected final static String PURPLE = "\u001B[35m";
    protected static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    WebDriver wdH;

    public Hooks() {

    }

    @Before
    public void initializeTest(Scenario scenario) throws Exception {
        System.out.println(PURPLE_BOLD_BRIGHT + "**************************************************");
        System.out.println("           [ BEFORE Cucumber Scenario  ]");
        System.out.println("            Scenario Name  : " + scenario.getName());
        System.out.println("           Scenario Initial Status: " + scenario.getStatus());
        System.out.println("            Scenario Initial ID: " + scenario.getId());
        System.out.println("**************************************************" + COLOR_RESET);
        message = "";
        if (Config.driverPerFeature.isEmpty() || !Config.driverPerFeature.trim().contains("y")) {
            wdH = null;
            System.out.println(BLUE + "  ---> Driver open per scenario ----" + COLOR_RESET);
            wdH = cn.getDriver(FeatureDriver.browser);
            //driver = cn.getDriver(browser);
            wdH.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            if (FeatureDriver.isMinimize != null && (FeatureDriver.isMinimize.trim().equalsIgnoreCase("yes") || FeatureDriver.isMinimize.trim().equalsIgnoreCase("y")))
                wdH.manage().window().minimize();
            if (FeatureDriver.browserSize != null)
                wdH.manage().window().setSize(new Dimension(FeatureDriver.browserWidth, FeatureDriver.browserHeight));
            Steps.driver = wdH;
            //Steps.driver = driver;
            //FeatureDriver.openNewDriver=false;
        }
        //FeatureDriver.openNewDriver=false;
        sc = scenario;
        featureFileName = FeatureDriver.featureFilePath.split("\\.")[0];
        System.out.println("Feature file name path -> " + featureFileName);
        iterationID = -1;
        System.out.println("  ---> Tags in the scenario as it is: " + scenario.getSourceTagNames().toString());
        ArrayList<String> taglist = new ArrayList<>(scenario.getSourceTagNames());
        Collections.sort(taglist);
        System.out.println("  ---> Tags in the scenario after SORT: " + taglist.toString());
        for (String t : taglist) {
            System.out.println("Tag: " + t);
        }
        //scenarioTagID = scenario.getSourceTagNames().toArray()[scenario.getSourceTagNames().size()-1].toString().split("@")[1];
        scenarioTagID = taglist.get(0).split("@")[1].trim();
        scenarioName = scenario.getName();
        scenariorunID = scenario.getId();
        countGetIDSplit = StringUtils.countMatches(scenariorunID, ";");
        System.out.println("  ---> Scenario ID tag to connect DATATABLE: " + scenarioTagID);
        System.out.println("Data sheet: " + base.Config.DATATABLE_FILE_PATH);
        if (countGetIDSplit > 2) {
            dataMap = cn.getDataFromSheet(FeatureDriver.tcSheetMapsFromTD.get(scenarioTagID));
        } else {
            dataMap = null;
        }
//        dataMap = cn.getDataFromSheet(FeatureDriver.tcSheetMapsFromTD.get(scenarioTagID));
        //dataMap= cn.getDataFromSheet(scenarioTagID.trim());
        if (dataMap != null) {
            System.out.println("\nData map >>>>>>>>>> " + dataMap.size() + "\n");
            isTCIDColumnExist = (base.Common.checkColumnExistInDataSheet(dataMap, "TCID") ? true : false);
            isTCNameColumnExist = (base.Common.checkColumnExistInDataSheet(dataMap, "TCName") ? true : false);
            isReadDataKeyColumnExist = (base.Common.checkColumnExistInDataSheet(dataMap, "readDataKey") ? true : false);
        }
        if (countGetIDSplit > 2) {
            iteration = Integer.parseInt(scenariorunID.substring(scenariorunID.lastIndexOf(";") + 1).trim()) - 1;
            System.out.println("--> This is Iteration : " + iteration);
        }
        System.out.println("  ---> Scenario run iD: " + scenariorunID);
        beforeScExeTimeDate = new Date();
        System.out.println("---------------------- [ END ] -----------------------\n");
    }

    @After
    public void postTestProcess(Scenario scenario) throws IOException {
        System.out.println(PURPLE_BOLD_BRIGHT + "------- [ AFTER Cucumber Scenario ] -----------------------");
        System.out.println(" 		Scenario Tag ID: " + scenarioTagID);
        System.out.println(" 		Scenario Name: " + scenario.getName());
        System.out.println(" 		Scenario Final Status: " + scenario.getStatus());
        System.out.println(" 		Scenario ID: " + scenario.getId());
        System.out.println("----------------------------------------------------------------" + COLOR_RESET);
        status = base.Common.statusMapforCI(scenario.getStatus());
        afterScExeTimeDate = new Date();
        String[] scatti = base.Common.getscenarioAttribute(scenario);
        String duration;
        scenarioExeDuration = Double.valueOf((new DecimalFormat("#.##")).format((afterScExeTimeDate.getTime() - beforeScExeTimeDate.getTime()) / (double) 1000));
        duration = scatti[1];
        String failReason = "";
        String lastStepName = "";
        String screenShotfilePath = "";
        String fullSteps = "";
        System.out.println("Additional Message to update: " + message);
        String currURL = "";
        try {
            currURL = Steps.driver.getCurrentUrl();
        } catch (Exception e) {
        }
        sc.write("------------------\n URL at end point:[" + currURL + "]\n---------------------");
        if (!message.trim().equalsIgnoreCase("") || !message.isEmpty())
            message = "<font color='blue'>" + message + "====================\n" + "</font>";
        message = "<font color='BlueViolet'><b>[Started time: " + beforeScExeTimeDate + "]</b>" +
                "\n-------------------------------------------\nURL at End point:[" + currURL + "]\n-------------------------------------------\n</font>" + message;
        //sc.write("Execution Started time: " + beforeScExeTimeDate);

        if (scenario.isFailed()) {
            sc.write("Fail occurred time approx : [" + afterScExeTimeDate + "]");
            String reason = "";
            try {
                String fullSource = Steps.driver.getPageSource().replaceAll(" ", "").toUpperCase();
                if (fullSource.contains("502BADGATEWAY")) {
                    reason = "502 BAD GATEWAY ERROR";
                } else if (fullSource.contains("ERR_EMPTY_RESPONSE")) {
                    reason = "This PAGE IS NOT WORKING - ERR_EMPTY_RESPONSE ERROR";
                } else if (fullSource.contains("THISPAGEISN’TWORKING") || fullSource.contains("THISPAGEISN'TWORKING")) {
                    reason = "This PAGE IS NOT WORKING ERROR";
                } else if (fullSource.contains("HTTPERROR503")) {
                    reason = "This PAGE IS NOT WORKING ERROR HTTP ERROR 503";
                } else if (fullSource.contains("Requestedpagewasn'tfoundonserver".toUpperCase()) || fullSource.contains("URLNOLONGEREXISTS")) {
                    reason = "Requested page was NOT found on server ERROR";
                } else if (fullSource.contains("Thissitecan’tbereached".toUpperCase()) || fullSource.contains("Thissitecan'tbereached".toUpperCase())
                        || fullSource.contains("can'treachthispage".toUpperCase()) || fullSource.contains("ERR_TUNNEL_CONNECTION_FAILED")) {
                    reason = "THIS SITE or URL is INVALID ERROR. Check the URL";
                } else if (fullSource.contains("defaultbackend-404".toUpperCase())) {
                    reason = "Site is DOWN for deployment - default backend 404 ERROR. Check the URL";
                } else if (fullSource.contains("ERR_TOO_MANY_REDIRECTS")) {
                    reason = "Site is not working - ERR_TOO_MANY_REDIRECTS. Check the URL";
                } else if (fullSource.contains("Error(s)occurred,Status:500".toUpperCase()) || fullSource.contains("InternalServererroroccurred".toUpperCase())) {
                    reason = "Error(s) occurred Status 500";
                }
//                else if(fullSource.contains("EISfailedtosyncthequoteautomatically".toUpperCase())){
//                    reason="Something went wrong in EIS or EIS failed to sync quote automatically";
//                }
               /* if(!reason.equalsIgnoreCase(""))
                    reason="<font color='red'><b>"+reason+" [Aprox Time: "+afterScExeTimeDate+"]\n"+"</b></font>";*/
            } catch (Exception e) {
            } finally {
                if (!reason.equalsIgnoreCase(""))
                    reason = "<font color='red'><b>" + reason + " [Approx Time: " + afterScExeTimeDate + "]\n" + "</b></font>";
            }
            scatti[0] = scatti[0].replaceAll("org.openqa.selenium.", "").replaceAll("java.lang.", "").replaceAll("com.sun.proxy.\\$", "").replaceAll("org.testng.Assert.", "");
            reason = reason + (scatti[0].trim().replaceFirst("\\?.", "").replaceAll("<", "(").replaceAll(">", ")"));
            failReason = message + reason;
            lastStepName = scatti[2];
            try {
                screenShotfilePath = base.Common.takeScreenshot(scenario, Steps.driver, "[Scenario]:" + scenarioName + " [Status]:" + scenario.getStatus());
            } catch (Exception e) {
                screenShotfilePath = base.Common.takeScreenshotWindow(scenario, "[Scenario]:" + scenarioName + " [Status]:" + scenario.getStatus());
            }
            System.out.println("  ---> Failed Last step Name : " + lastStepName);
            Reporter.log(scenario.getName() + " - " + scenario.getStatus() + " at Step " + lastStepName);
        } else {
            System.out.println("  ---> Passed Last step Name : " + lastStepName);
            screenShotfilePath = base.Common.takeScreenshot(scenario, Steps.driver, "[Scenario]:" + scenarioName + " [Status]:" + scenario.getStatus());
            Reporter.log(scenario.getName() + " - " + scenario.getStatus());
            failReason = message;
            //lastStepName=base.ThreadLocalStepDefinitionMatch.get().getStepName();
            //FeatureDriver.openNewDriver=false;
            //FeatureDriver.requiredToLogin=false;
        }
        message = "";
        fullSteps = scatti[3];
        System.out.println("");
        System.out.println("-------------------- STEPS " + scenarioTagID + " ----------------");
        System.out.println(fullSteps);
        System.out.println("---------------------------------------------------");
        System.out.println("");
        if (Config.driverPerFeature.isEmpty() || !Config.driverPerFeature.trim().contains("y")) {
            try {
                System.out.println(BLUE + "  --->  Driver Closed per scenario ----" + COLOR_RESET);
//				Steps.driver.quit();
//				driver.quit();
                System.out.println("openNewDriver: " + FeatureDriver.openNewDriver + "  Hence: Driver Closed");
                //base.Common.closeOpenBrowsers(FeatureDriver.browser);
                System.out.println("  ---> Browser closed");
            } catch (Exception e) {
                System.out.println("  ---> Driver Quit: " + e.getMessage());
            }
        }
        if (countGetIDSplit > 2) {
            //iteration=Integer.parseInt(sceanrioidsplit[2].trim())-1;
            if (dataMap != null) {
                //if(dataMap.size()>1)
                //scenarioTagID = scenario.getSourceTagNames().toArray()[scenario.getSourceTagNames().size()-1].toString().split("@")[1];
                scenario.write("Info: This is an Outline Scenario, iterated from [DATA SHEET], run Iteration# " + scenarioTagID);
                if (iteration > 1)
                    scenarioName = scenarioName + " " + iteration;
                if (iterationID > 1) {
                    scenarioTagID = scenarioTagID + "-" + iterationID;
                }
            } else {
                //iteration=Integer.parseInt(scenariorunID.substring(scenariorunID.lastIndexOf(";") + 1).trim())-1;
                // scenarioTagID = scenario.getSourceTagNames().toArray()[scenario.getSourceTagNames().size()-1].toString().split("@")[1];
                scenario.write("Info: This is an Outline Scenario. iterated from Feature [DATA TABLE] (NOT from DATASHEET), run Iteration# " + scenarioTagID);
                if (iteration > 1) {
                    scenarioName = scenarioName + " " + iteration;
                    scenarioTagID = scenarioTagID + "-" + iteration;
                }
            }
        } else {
            //scenarioTagID = scenario.getSourceTagNames().toArray()[scenario.getSourceTagNames().size()-1].toString().split("@")[1];
            if (dataMap != null) {
                scenario.write("Info: " + scenarioTagID + " : This is not an Outline Scenario. Data read from datasheet");
            } else {
                scenario.write("Info: " + scenarioTagID + " : This is not an Outline Scenario. No iteration, Data read not from datasheet, data directly in feature steps or No data required");
            }
        }

        //System.out.println("  ---> Last tag string " + scenariorunID.substring(scenariorunID.lastIndexOf(';') + 1).trim());
        if (FeatureDriver.testPlan.contains("e2e")) {
            System.out.println(YELLOW + "\n-------------------\nThis is E2E and Test Plan name is --> " + FeatureDriver.testPlan + COLOR_RESET);
            String scPre = "", scPo = "";
            if (!Steps.tcid.trim().equals(""))
                scPre = Steps.tcid + "_";
            if (!Steps.tcname.trim().equals("")) {
                if (iteration != 1)
                    scPo = Steps.tcname + " " + iteration + "\n[" + scenarioName + "]";
                else
                    scPo = Steps.tcname + "\n[" + scenarioName + "]";
            } else {
                scPo = scenarioName;
            }
            scenarioName = scPre.trim() + scPo.trim();
            System.out.println(YELLOW + "-------------------\n" + COLOR_RESET);
        }
        String feature = featureFileName + "\n[" + FeatureDriver.featureName + "]";
        FeatureDriver.resultData.add(new String[]{FeatureDriver.startExeTimeDate, base.Config.testEnv, Config.HOST_NAME, Config.SUITE_APPNAME, feature, scenarioName, scenarioTagID, status, failReason, fullSteps, screenShotfilePath, base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH, base.Config.REPORT_CUCUMBERHTML_PATH, duration});
        System.out.println("---------------  END ---------------------------------------------------\n");
        ;
    }
}

