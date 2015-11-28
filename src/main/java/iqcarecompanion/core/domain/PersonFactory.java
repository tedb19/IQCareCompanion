package iqcarecompanion.core.domain;

import iqcarecompanion.core.utils.DBConnector;
import iqcarecompanion.core.utils.ResourceManager;
import iqcarecompanion.core.utils.ResultsetToList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kemricdc.constants.IdentifierType;
import org.kemricdc.constants.MaritalStatus;
import org.kemricdc.entities.Person;
import org.kemricdc.entities.PersonIdentifier;

/**
 *
 * @author Teddy Odhiambo
 */
public class PersonFactory {

    final static Logger logger = Logger.getLogger(PersonFactory.class.getName());

    public static Person getPerson(int id, String symmetricKey, String dbName, String enc_password) throws SQLException {

        Person person = new Person();

        List rs = null;
        try {
            rs = getPatient(id, symmetricKey, dbName, enc_password);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if (rs != null && !rs.isEmpty()) {
            for (Object o : rs) {
                HashMap rc = (HashMap) o;

                person.setFirstName((String) rc.get("firstname_decrypted"));
                person.setMiddleName((String) rc.get("middlename_decrypted"));
                person.setLastName((String) rc.get("lastname_decrypted"));
                person.setBirthdate((Timestamp) rc.get("DOB"));
                person.setDateCreated((Timestamp) rc.get("CreateDate"));

                switch ((int) rc.get("Sex")) {
                    case 16:
                        person.setSex("MALE");
                        break;
                    case 17:
                        person.setSex("FEMALE");
                        break;
                    default:
                        person.setSex("UNKOWN");
                        break;
                }

                int maritalStatus = (int) (rc.get("MaritalStatus") != null ? rc.get("MaritalStatus") : 45);

                switch (maritalStatus) {
                    case 42:
                        person.setMaritalStatusType(MaritalStatus.SINGLE);
                        break;
                    case 43:
                    case 291:
                        person.setMaritalStatusType(MaritalStatus.MONOGAMOUS_MARRIED);
                        break;
                    case 44:
                        person.setMaritalStatusType(MaritalStatus.DIVORCED);
                        break;
                    case 189:
                        person.setMaritalStatusType(MaritalStatus.WIDOWED);
                        break;
                    case 45:
                        person.setMaritalStatusType(MaritalStatus.MISSING);
                        break;

                    case 290:
                        person.setMaritalStatusType(MaritalStatus.POLYGAMOUS_MARRIED);
                        break;
                    case 292:
                        person.setMaritalStatusType(MaritalStatus.COHABITING);
                        break;
                    default:
                        person.setMaritalStatusType(MaritalStatus.MISSING);
                        break;
                }

                Set<PersonIdentifier> identifiers = new HashSet<>();
                PersonIdentifier pid_number = new PersonIdentifier();
                pid_number.setIdentifier(rc.get("Ptn_Pk").toString());
                pid_number.setIdentifierType(IdentifierType.PID_NUMBER);
                identifiers.add(pid_number);

                PersonIdentifier pi = new PersonIdentifier();
                pi.setIdentifier((String) rc.get("IQNumber"));
                pi.setIdentifierType(IdentifierType.CCC_NUMBER);
                identifiers.add(pi);

                if ((String) rc.get("ANCNumber") != null && !((String) rc.get("ANCNumber")).equals("")) {
                    pi = new PersonIdentifier();
                    pi.setIdentifier((String) rc.get("ANCNumber"));
                    pi.setIdentifierType(IdentifierType.ANC_NUMBER);
                    identifiers.add(pi);
                }

                if ((String) rc.get("PMTCTNumber") != null && !((String) rc.get("PMTCTNumber")).equals("")) {
                    pi = new PersonIdentifier();
                    pi.setIdentifier((String) rc.get("PMTCTNumber"));
                    pi.setIdentifierType(IdentifierType.PMTCT_NUMBER);
                    identifiers.add(pi);
                }

                if ((String) rc.get("[ID/PassportNo]") != null && !((String) rc.get("[ID/PassportNo]")).equals("")) {
                    pi = new PersonIdentifier();
                    pi.setIdentifier((String) rc.get("[ID/PassportNo]"));
                    pi.setIdentifierType(IdentifierType.NATIONAL_ID);
                    identifiers.add(pi);
                }

                person.setPersonIdentifiers(identifiers);

            }
        } else {
            person = null;
        }
        return person;
    }

    private static List getPatient(int primary_key, String symmetricKey, String dbName, String enc_password) throws SQLException, IOException {

        Connection dbConnection;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        List results = null;

        StringBuilder sbSql = new StringBuilder();

        sbSql.append("Declare @SymKey varchar(400)\n")
                .append("USE ").append(dbName).append("\n")
                .append("Set @SymKey = 'Open symmetric key ")
                .append(symmetricKey).append(" decryption by password=''")
                .append(enc_password).append("'''\n")
                .append("exec(@SymKey)\n")
                .append("SELECT convert(varchar(50), decryptbykey(Emp.firstname)) AS firstname_decrypted,\n")
                .append("convert(varchar(50), decryptbykey(Emp.middlename)) AS middlename_decrypted ,\n")
                .append("convert(varchar(50), decryptbykey(Emp.lastname)) AS lastname_decrypted ,*\n")
                .append("FROM [").append(dbName).append("].[dbo].mst_Patient AS Emp WHERE [Ptn_Pk] = ?\n")
                .append("Close symmetric key ").append(symmetricKey);
        String sql = sbSql.toString();
        try {
            dbConnection = DBConnector.connectionInstance();

            preparedStatement = dbConnection.prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, primary_key);

            // execute select SQL stetement
            rs = preparedStatement.executeQuery();

            results = ResultsetToList.resultSetToList(rs);

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return results;

    }
}