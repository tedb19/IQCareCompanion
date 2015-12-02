/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.PropertiesManager.getProperties;

/**
 *
 * @author Ted
 */
public class ConstantProperties extends PropertiesManager {
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
    
}
