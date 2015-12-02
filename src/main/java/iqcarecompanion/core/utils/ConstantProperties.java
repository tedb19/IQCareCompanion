package iqcarecompanion.core.utils;

import iqcarecompanion.core.jsonMapper.Event;
import static iqcarecompanion.core.utils.PropertiesManager.getProperties;
import java.util.List;

/**
 *
 * @author Teddy Odhiambo
 */
public class ConstantProperties extends PropertiesManager {
    /*
    * A collection of all the variables read from the properties file and the json file
    * whose values don't change at runtime
    */
    public static final String DB_DRIVER = getProperties().getProperty("db_driver");
    public static final String DB_PASSWORD = getProperties().getProperty("db_password");
    public static final String windowsAuthentication = getProperties().getProperty("windowsAuthentication");
    public static final String HOST = getProperties().getProperty("host");
    public static final String INSTANCE = getProperties().getProperty("instance");
    public static final String PORT = getProperties().getProperty("port");
    public static final String DB_USER = getProperties().getProperty("db_user");
    public static final String DB_NAME = getProperties().getProperty("db_name");
    public static final String SYMMETRIC_KEY = getProperties().getProperty("symmetric_key");
    public static final String ENCRYPTION_PASSWORD = getProperties().getProperty("enc_password");
    public static final String LAB_TESTS = getProperties().getProperty("labTests");
    public static final String APPLICATION_NAME = getProperties().getProperty("application_name");
    public static final String FACILITY_NAME = getProperties().getProperty("facility_name");
    public static final String MFL_CODE = getProperties().getProperty("mfl_code");
    public static final String CDS_NAME = getProperties().getProperty("cds_name");
    public static final String CDS_APPLICATION_NAME = getProperties().getProperty("cdsapplication_name");
    public static final String LOG_PREFIX = "(" + FACILITY_NAME + ":"+ APPLICATION_NAME + ")";
    public static final List<Event> SENTINEL_EVENTS = ResourceManager.readJSONFile();
    
}
