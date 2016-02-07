
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rossmann
 */
public class SQLGenerate {

//    // Preparing to call updateSQL() function...to generate UPDATE SQL...
//Map<String, String> columnValueMappingForSet = new HashMap<String, String>();
//columnValueMappingForSet.put("FIRST_NAME", "'DEBOPAM'");
//columnValueMappingForSet.put("LAST_NAME", "'PAL'");
//columnValueMappingForSet.put("DESIGNATION", "'Software Developer'");
//columnValueMappingForSet.put("ORGANIZATION", "'NIC'");
//
//Map<String, String> columnValueMappingForCondition = new HashMap<String, String>();
//columnValueMappingForCondition.put("EMPLOYEE_NO", "201400002014");
//
//// Getting UPDATE SQL Query...
//String updateSQL = updateSQL("EMPLOYEE", columnValueMappingForSet, columnValueMappingForCondition);
//
//System.out.println(updateSQL);
//    
//    
//    
//    UPDATE EMPLOYEE
//SET
//ORGANIZATION='NIC',
//FIRST_NAME='DEBOPAM',
//DESIGNATION='Software Developer',
//LAST_NAME='PAL'
//WHERE
//EMPLOYEE_NO=201400002014
//    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * <h1>Get INSERT SQL Query</h1>
     * <p>
     * It is a generic function. It can be use for any DB Table</p>
     *
     * @author Debopam Pal, Software Developer, NIC, India.
     * @param tableName Table on which the INSERT Operation will be performed.
     * @param columnValueMappingForInsert List of Column & Value pair to Insert.
     * @return Final generated INSERT SQL Statement.
     */
    public static String insertSQL(String tableName, Map<String, String> columnValueMappingForInsert) {
        StringBuilder insertSQLBuilder = new StringBuilder();

        /**
         * Removing column that holds NULL value or Blank value...
         */
        if (!columnValueMappingForInsert.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForInsert.entrySet()) {
                if (entry.getValue() == null || entry.getValue().equals("")) {
                    columnValueMappingForInsert.remove(entry.getKey());
                }
            }
        }

        /* Making the INSERT Query... */
        insertSQLBuilder.append("INSERT INTO");
        insertSQLBuilder.append(" ").append(tableName);
        insertSQLBuilder.append("(");

        if (!columnValueMappingForInsert.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForInsert.entrySet()) {
                insertSQLBuilder.append(entry.getKey());
                insertSQLBuilder.append(",");
            }
        }

        insertSQLBuilder = new StringBuilder(insertSQLBuilder.subSequence(0, insertSQLBuilder.length() - 1));
        insertSQLBuilder.append(")");
        insertSQLBuilder.append(" VALUES");
        insertSQLBuilder.append("(");

        if (!columnValueMappingForInsert.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForInsert.entrySet()) {
                insertSQLBuilder.append(entry.getValue());
                insertSQLBuilder.append(",");
            }
        }

        insertSQLBuilder = new StringBuilder(insertSQLBuilder.subSequence(0, insertSQLBuilder.length() - 1));
        insertSQLBuilder.append(")");

        // Returning the generated INSERT SQL Query as a String...
        return insertSQLBuilder.toString();
    }

    /**
     * <h1>Get UPDATE SQL Query</h1>
     * <p>
     * It is a generic function. It can be use for any DB Table</p>
     *
     * @author Debopam Pal, Software Developer, NIC, India.
     * @param tableName Table on which the UPDATE Operation will be performed.
     * @param columnValueMappingForSet List of Column & Value pair to Update.
     * @param columnValueMappingForCondition List of Column & Value pair for
     * WHERE clause.
     * @return Final generated UPDATE SQL Statement.
     */
    public static String updateSQL(String tableName, Map<String, String> columnValueMappingForSet, Map<String, String> columnValueMappingForCondition) {
        StringBuilder updateQueryBuilder = new StringBuilder();

        /**
         * Removing column that holds NULL value or Blank value...
         */
        if (!columnValueMappingForSet.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForSet.entrySet()) {
                if (entry.getValue() == null || entry.getValue().equals("")) {
                    columnValueMappingForSet.remove(entry.getKey());
                }
            }
        }

        /**
         * Removing column that holds NULL value or Blank value...
         */
        if (!columnValueMappingForCondition.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForCondition.entrySet()) {
                if (entry.getValue() == null || entry.getValue().equals("")) {
                    columnValueMappingForCondition.remove(entry.getKey());
                }
            }
        }

        /* Making the UPDATE Query */
        updateQueryBuilder.append("UPDATE");
        updateQueryBuilder.append(" ").append(tableName);
        updateQueryBuilder.append(" SET");
        updateQueryBuilder.append(" ");

        if (!columnValueMappingForSet.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForSet.entrySet()) {
                updateQueryBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                updateQueryBuilder.append(",");
            }
        }

        updateQueryBuilder = new StringBuilder(updateQueryBuilder.subSequence(0, updateQueryBuilder.length() - 1));
        updateQueryBuilder.append(" WHERE");
        updateQueryBuilder.append(" ");

        if (!columnValueMappingForCondition.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForCondition.entrySet()) {
                updateQueryBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                updateQueryBuilder.append(",");
            }
        }

        updateQueryBuilder = new StringBuilder(updateQueryBuilder.subSequence(0, updateQueryBuilder.length() - 1));

        // Returning the generated UPDATE SQL Query as a String...
        return updateQueryBuilder.toString();
    }

    /**
     * <h1>Get DELETE SQL Query</h1>
     * <p>
     * It is a generic function. It can be use for any DB Table.</p>
     *
     * @author Debopam Pal, Software Developer, NIC, India.
     * @param tableName Table on which the DELETE Operation will be performed.
     * @param columnValueMappingForCondition List of Column & Value pair for
     * WHERE clause.
     * @return Final generated DELETE SQL Statement.
     */
    public static String deleteSQL(String tableName, Map<String, String> columnValueMappingForCondition) {
        StringBuilder deleteSQLBuilder = new StringBuilder();

        /**
         * Removing column that holds NULL value or Blank value...
         */
        if (!columnValueMappingForCondition.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForCondition.entrySet()) {
                if (entry.getValue() == null || entry.getValue().equals("")) {
                    columnValueMappingForCondition.remove(entry.getKey());
                }
            }
        }

        /* Making the DELETE Query */
        deleteSQLBuilder.append("DELETE FROM");
        deleteSQLBuilder.append(" ").append(tableName);
        deleteSQLBuilder.append(" WHERE");
        deleteSQLBuilder.append(" ");

        if (!columnValueMappingForCondition.isEmpty()) {
            for (Map.Entry<String, String> entry : columnValueMappingForCondition.entrySet()) {
                deleteSQLBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                deleteSQLBuilder.append(" AND ");
            }
        }

        deleteSQLBuilder = new StringBuilder(deleteSQLBuilder.subSequence(0, deleteSQLBuilder.length() - 5));

        // Returning the generated DELETE SQL Query as a String...
        return deleteSQLBuilder.toString();
    }

}
