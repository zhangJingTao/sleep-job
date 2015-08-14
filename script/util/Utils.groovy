package util

import java.sql.ResultSet
import java.sql.ResultSetMetaData

class Utils {
    public static Map CloneResultSet(ResultSet resultSet) {
        if (!resultSet) {
            return null
        }
        def result = [:]
        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        for(int i = 1; i <= count; ++i) {
            result.put(metaData.getColumnName(i).toLowerCase(),resultSet.getObject(i))
            result.put(metaData.getColumnName(i).toUpperCase(),resultSet.getObject(i))
        }
        return result
    }
}
