/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.facade;

import iqcarecompanion.core.dao.LabResultDao;
import iqcarecompanion.core.dao.PersonDao;
import iqcarecompanion.core.services.LabService;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import java.sql.Connection;

/**
 *
 * @author Ted
 */
public class LabFacade {
    public static void mineLabEvents(){
        Connection connection = connectionInstance();
        LabResultDao labResultDao = new LabResultDao(connection, DB_NAME);
        PersonDao personDao = new PersonDao(connection, DB_NAME);
        LabService labService = new LabService(labResultDao, personDao);
        labService.mineLabResults();
    }
}
