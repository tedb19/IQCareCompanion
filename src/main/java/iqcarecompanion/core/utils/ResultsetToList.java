package iqcarecompanion.core.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Teddy Odhiambo
 */
public class ResultsetToList {
    /*
    * Pass in a resultset, and get back a list of hashmaps.
    * This is important when you are not sure which fields will
    * be returned by a generic select stmt
    */
    public static List<HashMap<String, Object>> resultSetToList(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList<HashMap<String, Object>> list = new ArrayList<>(50);
        while (rs.next()){
           HashMap<String, Object> row = new HashMap<>(columns);
           for(int i=1; i<=columns; ++i){           
               row.put(md.getColumnName(i),rs.getObject(i));
           }
           list.add(row);
        }
       return list;
    }
}
