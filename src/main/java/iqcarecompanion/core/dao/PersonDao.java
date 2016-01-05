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

    public String getPersonSql(int primaryKey) throws SQLException{
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

        String sql = getPersonSql(primaryKey);
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

            int maritalStatus = rs.getInt("MaritalStatus") != 0 ? rs.getInt("MaritalStatus") : 45;

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

            //Add identifiers
            PersonIdentifier pidNumber = setPersonIdentifier(IdentifierType.PID_NUMBER, Integer.toString(rs.getInt("Ptn_Pk")));
            PersonIdentifier cccNumber = setPersonIdentifier(IdentifierType.CCC_NUMBER, rs.getString("IQNumber"));
            PersonIdentifier ancNumber = setPersonIdentifier(IdentifierType.ANC_NUMBER, rs.getString("ANCNumber"));
            PersonIdentifier pmtctNumber = setPersonIdentifier(IdentifierType.PMTCT_NUMBER, rs.getString("PMTCTNumber"));
            PersonIdentifier nationalIDNumber = setPersonIdentifier(IdentifierType.NATIONAL_ID, rs.getString("ID/PassportNo"));
            
            Set<PersonIdentifier> identifiers = new HashSet<>();
            identifiers.add(pidNumber);
            identifiers.add(cccNumber);
            identifiers.add(ancNumber);
            identifiers.add(pmtctNumber);
            identifiers.add(nationalIDNumber);
           
            //remove any null entry
            identifiers.remove(null);
            person.setPersonIdentifiers(identifiers);
        }

        return person;
    }
    
    public PersonIdentifier setPersonIdentifier(IdentifierType identifierType, String identifierValue){
        if(StringUtils.isEmpty(identifierValue)){
            return null;
        }
        PersonIdentifier personIdentifier = new PersonIdentifier();
        personIdentifier.setIdentifier(identifierValue);
        personIdentifier.setIdentifierType(identifierType);
        return personIdentifier;
    } 
}
