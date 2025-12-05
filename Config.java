package cv.Common;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;

public class Config {

    static Properties pro;

    static {
        try {
            pro = base.Common.getConfig(base.Config.APPCONFIG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String RUN_FROM_TESTPLAN = pro.getProperty("runFromTestPlan");
    public static final String SUITE_APPNAME = pro.getProperty("suite.appName");
    public static final String CUCUMBER_OPTION_FEATURES = pro.getProperty("cucumber.option.features");
    public static final String CUCUMBER_OPTION_GLUE = pro.getProperty("cucumber.option.glue");
    public static final String driverPerFeature = pro.getProperty("opendriverPerFeature");
    public static final String CUCUMBER_OPTION_TAGS = pro.getProperty("cucumber.option.tags");
    //public static final String ENV=pro.getProperty("environment");
    public static String ENV;// FeatureDriver.env;
    public static String URL;
    public static String userName;
    public static String passWord;
    public static String URLadfs;
    public static String userNameExaminer;
    public static String passWordExaminer;
    public static String userNameTech;
    public static String passWordTech;
    public static String ClaimsMngusername;
    public static String ClaimsMngusernamePWD;
    public static String userNameClaimTechMgmt;
    public static String ClaimTechMgmtusernamePWD;

    public static String ClaimsFinacialMgrUN;


    public static String ClaimsFinacialMgrPWD;
    public static String Implementation1UN;
    public static String Implementation1PWD;
    public static String passWordTSSO;
    public static String SSOPassWord;

    public static String rsliadminusername;// = pro.getProperty("qa.rsliadminusername");
    public static String rsliadminpassword;// = pro.getProperty("qa.rsliadminpassword");
    public static String sysadminusername;//= pro.getProperty("qa.sysadminusername");
    public static String sysadminpassword;// = pro.getProperty("qa.sysadminpassword");
    public static String userNamereadonly;
    public static String passWordreadonly;
    public static String userNameClaimsSysAdmin;
    public static String passWordClaimsSysAdmin;
    public static String passWordClaimTechMgmt;

    public static String userNameRSLISystemAdmin;
    public static String passWordRSLISystemAdmin;

    public static String userSleepMS = pro.getProperty("user.sleep.ms");
    public static final long driverWaitTime = Long.parseLong(pro.getProperty("driver.wait.sec"));
    public static final long sleepTimeInterval = Long.parseLong(pro.getProperty("poolinterval"));
    public static final String CustomerDatafilePath_HI = pro.getProperty("customerDatafilepath_HI");
    public static final String CustomerDatafilePath_AC = pro.getProperty("customerDatafilepath_AC");
    public static final String CustomerDatafilePath_CI = pro.getProperty("customerDatafilepath_CI");
    public static final String CustomerDatafilePath_STD = pro.getProperty("customerDatafilepath_STD");
    public static final String CustomerDatafilePath_VPS = pro.getProperty("customerDatafilepath_VPS");
    public static final String CustomerDatafilePath_TDI = pro.getProperty("customerDatafilepath_TDI");
    public static final String CustomerDatafilePath_TDB = pro.getProperty("customerDatafilepath_TDB");
    public static final String CustomerDatafilePath_DBL = pro.getProperty("customerDatafilepath_DBL");
    public static final String CustomerDatafilePath_MAL = pro.getProperty("customerDatafilepath_MAL");
    public static final String CustomerDatafilePath_CTL = pro.getProperty("customerDatafilepath_CTL");
    public static final String CustomerDatafilePath_BACKLOG = pro.getProperty("customerDatafilepath_BACKLOG");
    public static final String CustomerDatafilePath_LTD = pro.getProperty("customerDatafilepath_LTD");
    public static final String CustomerDatafilePath_ASL = pro.getProperty("customerDatafilepath_ASL");
    public static final String CustomerDatafilePath_GL = pro.getProperty("customerDatafilepath_GL");
    public static final String CustomerDatafilePath_VPL = pro.getProperty("customerDatafilepath_VPL");
    public static final String CustomerDatafilePath_VAR = pro.getProperty("customerDatafilepath_VAR");
    public static final String CustomerDatafilePath_VG = pro.getProperty("customerDatafilepath_VG");

//    public static final String AgencyPACSDbData = pro.getProperty("AgencyPACSDbData");

    public static final String e2eEISPolicyDatafilePath_AC = pro.getProperty("dev.e2eCV.dynamicdatafoldersharedpathCV");
    public static final String DynamicDataTemplatefilePath = Config.pro.getProperty("dynamicDataTemplatepath");
    public static final String customerurlpart = pro.getProperty("urlCustomerpagepart");
    public static final String deletedynamicdatafile = pro.getProperty("deletedynamicdatafile");
    public static final String dataArchiveDirpath = pro.getProperty("dataArchiveDirpath");
    public static final String HOST_NAME;
    public static final String defaultdownloaddir = pro.getProperty("defaultdownloaddir");
    public static final String defaultdownloadArchivedir = pro.getProperty("defaultdownloadArchivedir");
    public static String PreviewofletterPDFFilePath = pro.getProperty("testResults.PreviewofletterPath");
    public static String urlhomesuffix = pro.getProperty("urlHomepart");
    public static String urllogoff = pro.getProperty("urlLogoff");
    public static String urlPolicySearch = pro.getProperty("urlPolicySearch");
    public static String urlbasePreProd = pro.getProperty("urlbasePreProd");
    public static String urlProdcutCreation = pro.getProperty("urlProdcutCreation");
    public static String urlPolicyCreation = pro.getProperty("urlPolicyCreation");
    public static String urlBenefitCreation = pro.getProperty("urlBenefitCreation");
    public static String chromeOption_ImageBlocked = pro.getProperty("chromeOption.ImageLoadBlocked");

    public static String ibmloginusername = pro.getProperty("ibmloginusername");
    public static String ibmloginpwd = pro.getProperty("ibmloginpwd");

    public static final String isDriverHeadless = pro.getProperty("driver.option.headless");

    public static final String CustomerDatafileName_HI = pro.getProperty("customerDatafilname_HI");
    public static final String CustomerDatafileName_AC = pro.getProperty("customerDatafilname_AC");
    public static final String CustomerDatafileName_CI = pro.getProperty("customerDatafilname_CI");

    public static final String CustomerDatafileName_STD = pro.getProperty("customerDatafilname_STD");

    public static final String CustomerDatafileName_VPS = pro.getProperty("customerDatafilname_VPS");
    public static final String CustomerDatafileName_TDI = pro.getProperty("customerDatafilname_TDI");
    public static final String CustomerDatafileName_DBL = pro.getProperty("customerDatafilname_DBL");
    public static final String CustomerDatafileName_MAL = pro.getProperty("customerDatafilname_MAL");
    public static final String CustomerDatafileName_TDB = pro.getProperty("customerDatafilname_TDB");
    public static final String CustomerDatafileName_CTL = pro.getProperty("customerDatafilname_CTL");
    public static final String CustomerDatafileName_ORL = pro.getProperty("customerDatafilname_ORL");
    public static final String CustomerDatafileName_BACKLOG = pro.getProperty("customerDatafilname_BACKLOG");
    public static final String CustomerDatafileName_LTD = pro.getProperty("customerDatafilname_LTD");
    public static final String CustomerDatafileName_ASL = pro.getProperty("customerDatafilname_ASL");
    public static final String CustomerDatafileName_VAR = pro.getProperty("customerDatafilname_VAR");
    public static final String CustomerDatafileName_VPL = pro.getProperty("customerDatafilname_VPL");
    public static final String CustomerDatafileName_VG = pro.getProperty("customerDatafilname_VG");
    public static final String CustomerDatafileName_GL = pro.getProperty("customerDatafilname_GL");

    public static final String dynamicdatafolderpath = pro.getProperty("e2e.dynamicdatafolderpath");
    public static String dynamicdatafoldersharedpath;

    public static String DATA_TABLE_FILE_PATH;
    public static String DATA_TABLE_FILE_NAME_QA = pro.getProperty("dataTableFileName_QA");
    public static String DATA_TABLE_FILE_NAME_UAT = pro.getProperty("dataTableFileName_UAT");
    public static String DATA_TABLE_FILE_NAME_PREPROD = pro.getProperty("dataTableFileName_PREPROD");
    public static String pageTimeOutSec = pro.getProperty("pageTimeOutSec");
    public static String pacsDBURLr;
    public static String pacsDBDriver;
    public static String pacsDBUN;
    public static String pacsDBPWD;
    public static String vueDBDriver;
    public static String vueDBUN;
    public static String vueDBPWD;
    public static String vueDBURLr;
    public static String apsDBDriver;
    public static String apsDBUN;
    public static String apsDBPWD;
    public static String apsDBURLr;
    public static String stacsDBDriver;
    public static String stacsDBUN;
    public static String stacsDBPWD;
    public static String stacsDBURLr;
    public static String stacsinterimDBDriver;
    public static String stacsinterimDBUN;
    public static String stacsinterimDBPWD;
    public static String stacsinterimDBURLr;

    public static String eisDBDriver;
    public static String eisDBUN;
    public static String eisDBPWD;
    public static String eisDBURLr;


    //static Logger log= LogManager.getLogger(Config.class);
    static {
        HOST_NAME = base.Config.HOST_NAME;//getLocalHost().getHostName();
    }

    public Config(String environment) throws Exception {
        if (chromeOption_ImageBlocked == null || chromeOption_ImageBlocked.trim().equalsIgnoreCase("") || chromeOption_ImageBlocked.trim().equalsIgnoreCase("1")) {
            base.Common.chromeOptionImageBlocked = 1;
        } else if (chromeOption_ImageBlocked.trim().equalsIgnoreCase("0")) {
            base.Common.chromeOptionImageBlocked = 0;
        } else {
            base.Common.chromeOptionImageBlocked = 2;
        }
        if (!pageTimeOutSec.isEmpty() && !pageTimeOutSec.equalsIgnoreCase("")) {
            base.Config.pageTimeOutSec = Long.parseLong(pageTimeOutSec);
        }
        ENV = environment;
        if (ENV.trim().equalsIgnoreCase("dev")) {
            DATA_TABLE_FILE_PATH = base.Config.DATATABLE_ROOT_PATH + DATA_TABLE_FILE_NAME_QA;
            System.out.println("Dev Data table path: " + DATA_TABLE_FILE_PATH);
            base.Config.DATATABLE_FILE_PATH = DATA_TABLE_FILE_PATH;
            URL = pro.getProperty("qa.url");
            userName = pro.getProperty("qa.username");
            passWord = pro.getProperty("qa.password");
            URLadfs = pro.getProperty("qa.adfsurl");
            userNameExaminer = pro.getProperty("qa.Examinerusername");
            passWordExaminer = pro.getProperty("qa.Examinerpassword");
            userNameTech = pro.getProperty("qa.Techusername");
            passWordTech = pro.getProperty("qa.Techpassword");
            rsliadminusername = pro.getProperty("qa.rsliadminusername");
            rsliadminpassword = pro.getProperty("qa.rsliadminpassword");
            sysadminusername = pro.getProperty("qa.RSLISystemAdminusername");
            sysadminpassword = pro.getProperty("qa.RSLISystemAdminpassword");
            userNamereadonly = pro.getProperty("qa.ReadOnlyusername");
            passWordreadonly = pro.getProperty("qa.ReadOnlypassword");
            dynamicdatafoldersharedpath = pro.getProperty("dev.e2e.dynamicdatafoldersharedpath");
        } else if (ENV.trim().equalsIgnoreCase("qa")) {
            DATA_TABLE_FILE_PATH = base.Config.DATATABLE_ROOT_PATH + DATA_TABLE_FILE_NAME_QA;
            System.out.println("qa Data table path: " + DATA_TABLE_FILE_PATH);
            base.Config.DATATABLE_FILE_PATH = DATA_TABLE_FILE_PATH;
            URL = pro.getProperty("qa.url");
            userName = pro.getProperty("qa.username");
            passWord = pro.getProperty("qa.password");
            URLadfs = pro.getProperty("qa.adfsurl");
            userNameExaminer = pro.getProperty("qa.Examinerusername");
            passWordExaminer = pro.getProperty("qa.Examinerpassword");
            rsliadminusername = pro.getProperty("qa.rsliadminusername");
            rsliadminpassword = pro.getProperty("qa.rsliadminpassword");
            sysadminusername = pro.getProperty("qa.RSLISystemAdminusername");
            sysadminpassword = pro.getProperty("qa.RSLISystemAdminpassword");
            userNamereadonly = pro.getProperty("qa.ReadOnlyusername");
            passWordreadonly = pro.getProperty("qa.ReadOnlypassword");
            userNameClaimsSysAdmin = pro.getProperty("qa.ClaimsSysAdminusername");
            passWordClaimsSysAdmin = pro.getProperty("qa.ClaimsSyspassword");
            userNameClaimTechMgmt = pro.getProperty("qa.ClaimTechMgmtusername");
            passWordClaimTechMgmt = pro.getProperty("qa.ClaimTechMgmtusernamePWD");
            Implementation1UN= pro.getProperty("Implementation1UN");
            Implementation1PWD = pro.getProperty("Implementation1PWD");

            ClaimsMngusername = pro.getProperty("qa.ClaimsMngusername");
            ClaimsMngusernamePWD = pro.getProperty("qa.ClaimsMngusernamePWD");
            passWordTSSO = pro.getProperty("qa.SSOpassword");
            userNameTech = pro.getProperty("qa.Techusername");
            passWordTech = pro.getProperty("qa.Techpassword");
            ClaimsFinacialMgrUN = pro.getProperty("uat.FinicalMgrusername");
            ClaimsFinacialMgrPWD = pro.getProperty("uat.FinicalMgrPwd");
            userNameRSLISystemAdmin = pro.getProperty("qa.RSLISystemAdminusername");
            passWordRSLISystemAdmin = pro.getProperty("qa.RSLISystemAdminpassword");
            dynamicdatafoldersharedpath = pro.getProperty("qa.e2e.dynamicdatafoldersharedpath");
            pacsDBDriver = pro.getProperty("qa.PACS.DB.driver");
            pacsDBUN = pro.getProperty("qa.PACS.DB.username");
            pacsDBPWD = pro.getProperty("qa.PACS.DB.password");
            pacsDBURLr = pro.getProperty("qa.PACS.DB.url");
            apsDBDriver = pro.getProperty("qa.APS.DB.driver");
            apsDBUN = pro.getProperty("qa.APS.DB.username");
            apsDBPWD = pro.getProperty("qa.APS.DB.password");
            apsDBURLr = pro.getProperty("qa.APS.DB.url");
            vueDBDriver = pro.getProperty("qa.VUE.DB.driver");
            vueDBUN = pro.getProperty("qa.VUE.DB.username");
            vueDBPWD = pro.getProperty("qa.VUE.DB.password");
            vueDBURLr = pro.getProperty("qa.VUE.DB.url");
            stacsinterimDBDriver = pro.getProperty("qa.STACSINTERIM.DB.driver");
            stacsinterimDBUN = pro.getProperty("qa.STACSINTERIM.DB.username");
            stacsinterimDBPWD = pro.getProperty("qa.STACSINTERIM.DB.password");
            stacsinterimDBURLr = pro.getProperty("qa.STACSINTERIM.DB.url");
            stacsDBDriver = pro.getProperty("qa.STACS.DB.driver");
            stacsDBUN = pro.getProperty("qa.STACS.DB.username");
            stacsDBPWD = pro.getProperty("qa.STACS.DB.password");
            stacsDBURLr = pro.getProperty("qa.STACS.DB.url");

        } else if (ENV.trim().equalsIgnoreCase("uat")) {
            DATA_TABLE_FILE_PATH = base.Config.DATATABLE_ROOT_PATH + DATA_TABLE_FILE_NAME_UAT;
            System.out.println("uat Data table path: " + DATA_TABLE_FILE_PATH);
            base.Config.DATATABLE_FILE_PATH = DATA_TABLE_FILE_PATH;
            URL = pro.getProperty("uat.url");
            userName = pro.getProperty("uat.username");
            passWord = pro.getProperty("uat.password");
            URLadfs = pro.getProperty("uat.adfsurl");
            rsliadminusername = pro.getProperty("uat.rsliadminusername");
            rsliadminpassword = pro.getProperty("uat.rsliadminpassword");
            sysadminusername = pro.getProperty("uat.sysadminusername");
            sysadminpassword = pro.getProperty("uat.sysadminpassword");
            ClaimsFinacialMgrUN = pro.getProperty("uat.FinicalMgrusername");
            ClaimsFinacialMgrPWD = pro.getProperty("uat.FinicalMgrPwd");
            userNameTech = pro.getProperty("qa.Techusername");
            passWordTech = pro.getProperty("qa.Techpassword");
            userNameExaminer = pro.getProperty("qa.Examinerusername");
            passWordExaminer = pro.getProperty("qa.Examinerpassword");
            userNameClaimsSysAdmin = pro.getProperty("qa.ClaimsSysAdminusername");
            passWordClaimsSysAdmin = pro.getProperty("qa.ClaimsSyspassword");
            pacsDBDriver = pro.getProperty("qa.PACS.DB.driver");
            pacsDBUN = pro.getProperty("uat.PACS.DB.username");
            pacsDBPWD = pro.getProperty("uat.PACS.DB.password");
            ClaimsMngusername = pro.getProperty("qa.ClaimsMngusername");
            ClaimsMngusernamePWD = pro.getProperty("qa.ClaimsMngusernamePWD");
            pacsDBURLr = pro.getProperty("uat.PACS.DB.url");
            userNameRSLISystemAdmin = pro.getProperty("qa.RSLISystemAdminusername");
            passWordRSLISystemAdmin = pro.getProperty("qa.RSLISystemAdminpassword");
            dynamicdatafoldersharedpath = pro.getProperty("uat.e2e.dynamicdatafoldersharedpath");
            Implementation1UN= pro.getProperty("Implementation1UN");
            Implementation1PWD = pro.getProperty("Implementation1PWD");
        } else if (ENV.trim().equalsIgnoreCase("stage")) {
            URL = pro.getProperty("stage.url");
            userName = pro.getProperty("stage.username");
            passWord = pro.getProperty("stage.password");
            rsliadminusername = pro.getProperty("stage.rsliadminusername");
            rsliadminpassword = pro.getProperty("stage.rsliadminpassword");
            sysadminusername = pro.getProperty("stage.sysadminusername");
            sysadminpassword = pro.getProperty("stage.sysadminpassword");
        } else if (ENV.trim().equalsIgnoreCase("preprod")) {
            DATA_TABLE_FILE_PATH = base.Config.DATATABLE_ROOT_PATH + DATA_TABLE_FILE_NAME_PREPROD;
            System.out.println("preprod Data table path: " + DATA_TABLE_FILE_PATH);
            base.Config.DATATABLE_FILE_PATH = DATA_TABLE_FILE_PATH;
            URL = pro.getProperty("preprod.url");
            URLadfs = pro.getProperty("preprod.ssourl");
            userName = pro.getProperty("preprod.username");
            passWord = pro.getProperty("preprod.password");
            sysadminusername = pro.getProperty("preprod.ssousername");
            SSOPassWord = pro.getProperty("SSOpassword");
            userNameRSLISystemAdmin = pro.getProperty("preprod.ssousername");
            dynamicdatafoldersharedpath = pro.getProperty("preprod.e2e.dynamicdatafoldersharedpath");
            userNameExaminer = pro.getProperty("qa.Examinerusername");
            passWordExaminer = pro.getProperty("qa.Examinerpassword");
            rsliadminusername = pro.getProperty("qa.rsliadminusername");
            rsliadminpassword = pro.getProperty("qa.rsliadminpassword");
            sysadminusername = pro.getProperty("qa.RSLISystemAdminusername");
            sysadminpassword = pro.getProperty("qa.RSLISystemAdminpassword");
            userNamereadonly = pro.getProperty("qa.ReadOnlyusername");
            passWordreadonly = pro.getProperty("qa.ReadOnlypassword");
            userNameClaimsSysAdmin = pro.getProperty("qa.ClaimsSysAdminusername");
            passWordClaimsSysAdmin = pro.getProperty("qa.ClaimsSyspassword");
            userNameClaimTechMgmt = pro.getProperty("qa.ClaimTechMgmtusername");
            passWordClaimTechMgmt = pro.getProperty("qa.ClaimTechMgmtusernamePWD");
            ClaimsMngusername = pro.getProperty("qa.ClaimsMngusername");
            ClaimsMngusernamePWD = pro.getProperty("qa.ClaimsMngusernamePWD");
            passWordTSSO = pro.getProperty("qa.SSOpassword");
            userNameTech = pro.getProperty("qa.Techusername");
            passWordTech = pro.getProperty("qa.Techpassword");
            userNameRSLISystemAdmin = pro.getProperty("qa.RSLISystemAdminusername");
            passWordRSLISystemAdmin = pro.getProperty("qa.RSLISystemAdminpassword");
            dynamicdatafoldersharedpath = pro.getProperty("preprod.e2e.dynamicdatafoldersharedpath");
            pacsDBDriver = pro.getProperty("preprod.PACS.DB.driver");
            pacsDBUN = pro.getProperty("preprod.PACS.DB.username");
            pacsDBPWD = pro.getProperty("preprod.PACS.DB.password");
            pacsDBURLr = pro.getProperty("preprod.PACS.DB.url");
            apsDBDriver = pro.getProperty("preprod.APS.DB.driver");
            apsDBUN = pro.getProperty("preprod.APS.DB.username");
            apsDBPWD = pro.getProperty("preprod.APS.DB.password");
            apsDBURLr = pro.getProperty("preprod.APS.DB.url");
            vueDBDriver = pro.getProperty("preprod.VUE.DB.driver");
            vueDBUN = pro.getProperty("preprod.VUE.DB.username");
            vueDBPWD = pro.getProperty("preprod.VUE.DB.password");
            vueDBURLr = pro.getProperty("preprod.VUE.DB.url");
            stacsinterimDBDriver = pro.getProperty("preprod.STACSINTERIM.DB.driver");
            stacsinterimDBUN = pro.getProperty("preprod.STACSINTERIM.DB.username");
            stacsinterimDBPWD = pro.getProperty("preprod.STACSINTERIM.DB.password");
            stacsinterimDBURLr = pro.getProperty("preprod.STACSINTERIM.DB.url");
            stacsDBDriver = pro.getProperty("preprod.STACS.DB.driver");
            stacsDBUN = pro.getProperty("preprod.STACS.DB.username");
            stacsDBPWD = pro.getProperty("preprod.STACS.DB.password");
            stacsDBURLr = pro.getProperty("preprod.STACS.DB.url");
        } else {
            URL = pro.getProperty("qa.url");
            userName = pro.getProperty("qa.username");
            passWord = pro.getProperty("qa.password");
        }
    }

    public static void main(String[] s) throws Exception {
        Config cf = new Config("QA");
        System.out.println(Config.RUN_FROM_TESTPLAN);
        Field[] fields = cf.getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName() + "=" + field.get(cf));
        }
        Enumeration<?> e = pro.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = pro.getProperty(key);
            System.out.println(key + "=" + value);
        }
    }
}
