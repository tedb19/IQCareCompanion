package iqcarecompanion.core.dao;

import hapimodule.core.constants.IdentifierType;
import hapimodule.core.constants.MaritalStatus;
import hapimodule.core.entities.Person;
import hapimodule.core.entities.PersonIdentifier;
import static iqcarecompanion.core.utils.ConstantProperties.ENCRYPTION_PASSWORD;
import static iqcarecompanion.core.utils.ConstantProperties.SYMMETRIC_KEY;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class PersonDao {

    private final Connection connection;
    private final String dbName;
    
    public PersonDao(Connection connection, String dbName){
        this.connection = connection;
        this.dbName = dbName;
    }

    public String getPersonRs(int primaryKey) throws SQLException{
        StringBuilder sbSql = new StringBuilder();
        sbSql.append("Declare @SymKey varchar(400)\n")
                .append("USE ").append(dbName).append("\n")
                .append("Set @SymKey = 'Open symmetric key ")
                .append(SYMMETRIC_KEY).append(" decryption by password=''")
                .append(ENCRYPTION_PASSWORD).append("'''\n")
                .append("exec(@SymKey)\n")
                .append("SELECT convert(varchar(50), decryptbykey(Emp.firstname)) AS firstname_decrypted,\n")
                .append("convert(varchar(50), decryptbykey(Emp.middlename)) AS middlename_decrypted ,\n")
                .append("convert(varchar(50), decryptbykey(Emp.lastname)) AS lastname_decrypted ,*\n")
                .append("FROM [").append(dbName).append("].[dbo].mst_Patient AS Emp WHERE [Ptn_Pk] = ")
                .append(primaryKey)
                .append("\nClose symmetric key ").append(SYMMETRIC_KEY);
        return sbSql.toString();
    }
    
    public Person getPerson(int primaryKey) throws SQLException {

        String sql = getPersonRs(primaryKey);
        Person person = null;
        PreparedStatement preparedStatement = this.connection.prepareStatement(sql,
                TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);
        ResultSet rs = preparedStatement.executeQuery();
        
        while(rs.next()){
            person = new Person();
            person.setFirstName(rs.getString("firstname_decrypted"));
            person.setMiddleName(rs.getString("middlename_decrypted"));
            person.setLastName(rs.getString("lastname_decrypted"));
            person.setBirthdate(rs.getTimestamp("DOB"));
            person.setDateCreated(rs.getTimestamp("CreateDate"));

            switch (rs.getInt("Sex")) {
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

            int maritalStatus = (rs.getInt("MaritalStatus") != 0 ? rs.getInt("MaritalStatus") : 45);

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
            //Add the PID_NUMBER identifier
            PersonIdentifier pidNumber = new PersonIdentifier();
            pidNumber.setIdentifier(Integer.toString(rs.getInt("Ptn_Pk")));
            pidNumber.setIdentifierType(IdentifierType.PID_NUMBER);
            identifiers.add(pidNumber);

            //Add the CCC_NUMBER identifier
            PersonIdentifier cccNumber = new PersonIdentifier();
            cccNumber.setIdentifier(rs.getString("IQNumber"));
            cccNumber.setIdentifierType(IdentifierType.CCC_NUMBER);
            identifiers.add(cccNumber);

            if (StringUtils.isNotEmpty(rs.getString("ANCNumber"))) {
                PersonIdentifier ancNumber = new PersonIdentifier();
                ancNumber.setIdentifier(rs.getString("ANCNumber"));
                ancNumber.setIdentifierType(IdentifierType.ANC_NUMBER);
                identifiers.add(ancNumber);
            }

            if (StringUtils.isNotEmpty(rs.getString("PMTCTNumber"))) {
                PersonIdentifier pmtctNumber = new PersonIdentifier();
                pmtctNumber.setIdentifier(rs.getString("PMTCTNumber"));
                pmtctNumber.setIdentifierType(IdentifierType.PMTCT_NUMBER);
                identifiers.add(pmtctNumber);
            }

            if (StringUtils.isNotEmpty(rs.getString("ID/PassportNo"))) {
                PersonIdentifier nationalIDNumber = new PersonIdentifier();
                nationalIDNumber.setIdentifier(rs.getString("ID/PassportNo"));
                nationalIDNumber.setIdentifierType(IdentifierType.NATIONAL_ID);
                identifiers.add(nationalIDNumber);
            }
            person.setPersonIdentifiers(identifiers);
        }

        return person;
    }
}
