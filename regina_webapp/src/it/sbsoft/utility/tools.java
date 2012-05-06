package it.sbsoft.utility;

import net.sf.json.*;

public class tools {

	public JSONArray convertResultSetToJSON(java.sql.ResultSet rs){

        JSONArray json = new JSONArray();

        try {

             java.sql.ResultSetMetaData rsmd = rs.getMetaData();

             while(rs.next()){
                 int numColumns = rsmd.getColumnCount();
                 JSONObject obj = new JSONObject();

                 for (int i=1; i<numColumns+1; i++) {

                     String column_name = rsmd.getColumnName(i);

                     if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
                         obj.put(column_name, rs.getArray(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
                         obj.put(column_name, rs.getInt(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
                         obj.put(column_name, rs.getBoolean(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
                         obj.put(column_name, rs.getBlob(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
                         obj.put(column_name, rs.getDouble(column_name)); 
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
                         obj.put(column_name, rs.getFloat(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
                         obj.put(column_name, rs.getInt(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
                         obj.put(column_name, rs.getNString(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
                         obj.put(column_name, rs.getString(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
                         obj.put(column_name, rs.getInt(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
                         obj.put(column_name, rs.getInt(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
                         obj.put(column_name, rs.getDate(column_name));
                     }
                     else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
                        obj.put(column_name, rs.getTimestamp(column_name));   
                     }
                     else{
                         obj.put(column_name, rs.getObject(column_name));
                     } 

                    }//end foreach
                 json.add(obj);

             }//end while




        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return json;
    }
	
}
