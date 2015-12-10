package iqcarecompanion.core.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Teddy Odhiambo
 */
public class ResultsetToList {
    
    //Hide the implicit public constructor
    private ResultsetToList(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    /*
    * Pass in a resultset, and get back a list of hashmaps.
    * This is important when you are not sure which fields will
    * be returned by a generic select stmt
    */
    public static List<HashMap<String, Object>> resultSetToList(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<HashMap<String, Object>> list = new ArrayList<>(50);
        while (rs.next()){
           Map<String, Object> row = new HashMap<>(columns);
           for(int i=1; i<=columns; ++i){           
               row.put(md.getColumnName(i),rs.getObject(i));
           }
           list.add((HashMap<String, Object>) row);
        }
       return list;
    }
}
