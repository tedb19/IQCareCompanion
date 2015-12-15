
package iqcarecompanion.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class RegimenDao {

    private final Connection connection;
    private final  String dbName;
    
    public RegimenDao(Connection connection, String dbName){
        this.connection = connection;
        this.dbName = dbName;
    }

    public String getCurrentRegimen(int visitId) throws SQLException {

        String regimenType = "";
        StringBuilder sbSql = new StringBuilder();
        if(StringUtils.isNotEmpty(dbName)){
            sbSql.append("USE ").append(dbName).append("\n");
        }
        sbSql.append(" SELECT DISTINCT RegimenType")
                .append(" FROM ").append("dtl_RegimenMap WHERE Visit_Pk = ")
                .append(visitId).append(";");

        PreparedStatement preparedStatement = this.connection.prepareStatement(sbSql.toString());
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            regimenType = rs.getString("RegimenType");
        }
        
        return regimenType;
    }
}
