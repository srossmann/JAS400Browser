//******************************************************************************
//
//
//
//******************************************************************************

import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.Record;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rossmann
 */
public class AS400 {

    private Connection AS400Conn = null;
    public ResultSet rs_data = null;
    public String ResulSQLStatement = "";
    public String error = "";
//******************************************************************************
//
//
//
//******************************************************************************
    public boolean Connect(String IPAdresse, String User, String Passwort) {
        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            String dbDriver = "jdbc:as400://" + IPAdresse + ";naming=system;errors=full";
            AS400Conn = DriverManager.getConnection(dbDriver, User, Passwort);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e.getMessage(), "Verbindungsfehler", JOptionPane.WARNING_MESSAGE);
        }
        return true;
    }
//******************************************************************************
//
//
//
//******************************************************************************
    public String executeSQL(String SQLStatement) throws ClassNotFoundException, SQLException {

        Statement st;
        String rs = "";
        try {
            st = AS400Conn.createStatement();
            st.executeQuery(SQLStatement);

        } catch (Exception e) {
            rs = e.getMessage();
        }
        if (rs.equals("Cursor state not valid.")) {
            rs = "O.K.";
        }
        return rs;
    }
    
//******************************************************************************
//
//
//
//******************************************************************************
    private ResultSet getSQLResult(String SQLStatement) throws ClassNotFoundException, SQLException {

        Statement st;
        ResultSet rs = null;
        try {
            st = AS400Conn.createStatement();
            rs = st.executeQuery(SQLStatement);

        } catch (Exception ex) {
            error = ex.getMessage();
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
//******************************************************************************
//
//
//
//******************************************************************************

    public TabellenStructurTableModell getTabellenStrucktur(String Schema, String TabellenName) {
        ResultSet rs = null;
        TabellenStructurTableModell dtm = null;
        try {
//SELECT QSYS.QADBKFLD.DBKPOS,QSYS.QADBKFLD.DBKORD,QSYS2.SYSCOLUMNS.COLUMN_NAME,QSYS2.SYSCOLUMNS.DATA_TYPE,QSYS2.SYSCOLUMNS.LENGTH,QSYS2.SYSCOLUMNS.NUMERIC_SCALE,QSYS2.SYSCOLUMNS.COLUMN_HEADING,QSYS2.SYSCOLUMNS.TABLE_NAME FROM QSYS.QADBKFLDRIGHT OUTER JOIN QSYS2.SYSCOLUMNS ONQSYS2.SYSCOLUMNS.COLUMN_NAME = QSYS.QADBKFLD.DBKFLDAND QSYS.QADBKFLD.DBKFIL = QSYS2.SYSCOLUMNS.TABLE_NAME WHEREQSYS2.SYSCOLUMNS.TABLE_NAME = 'BDBE0104'

            String SQLStatement = String.format("SELECT SYSCOLUMNS.COLUMN_NAME,SYSCOLUMNS.DATA_TYPE,"
                    + "SYSCOLUMNS.LENGTH,"
                    + "SYSCOLUMNS.NUMERIC_SCALE,"
                    + "SYSCOLUMNS.COLUMN_HEADING,"
                    //+ "SYSCOLUMNS.TABLE_NAME, "
                    + "QADBKFLD.DBKPOS,"
                    + "QADBKFLD.DBKORD "
                    + "FROM QADBKFLD "
                    + "RIGHT OUTER JOIN SYSCOLUMNS ON "
                    + "SYSCOLUMNS.COLUMN_NAME = QADBKFLD.DBKFLD "
                    + "AND QADBKFLD.DBKFIL = SYSCOLUMNS.TABLE_NAME "
                    + "AND QADBKFLD.DBKLIB = SYSCOLUMNS.TABLE_SCHEMA WHERE "
                    + "SYSCOLUMNS.TABLE_SCHEMA = '%s' and "
                    + "SYSCOLUMNS.TABLE_NAME = '%s'", Schema.toUpperCase().trim(), TabellenName.toUpperCase().trim());

          //  String SQLStatement = String.format("SELECT COLUMN_NAME,DATA_TYPE,LENGTH,NUMERIC_SCALE,COLUMN_HEADING "
            //          + "FROM SYSCOLUMNS WHERE TABLE_NAME='%s'  ", TabellenName.toUpperCase().trim());
            rs = getSQLResult(SQLStatement);
            ResultSetMetaData metaData = rs.getMetaData();
            Vector<String> columnNames = new Vector<String>();
            columnNames.add("[x]");
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                vector.add(true);
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(rs.getObject(columnIndex));
                }
                data.add(vector);
            }
            dtm = new TabellenStructurTableModell(data, columnNames);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dtm;
    }

    public void druckeTabellenStrucktur(String Schema, String TabellenName) {
        try {
            HashMap map = new HashMap();
            map.put("SchemaName", Schema);
            map.put("SchemaBeschreibung", getSchemaBeschreibung(Schema));
            map.put("TabellenName", TabellenName);
            map.put("TabellenBeschreibung", getTabellenBeschreibung(Schema, TabellenName));

            Statement statement = AS400Conn.createStatement();
            String SQLStatement = String.format("SELECT SYSCOLUMNS.COLUMN_NAME,SYSCOLUMNS.DATA_TYPE,"
                    + "SYSCOLUMNS.LENGTH,"
                    + "SYSCOLUMNS.NUMERIC_SCALE,"
                    + "SYSCOLUMNS.COLUMN_HEADING,"
                    //+ "SYSCOLUMNS.TABLE_NAME, "
                    + "QADBKFLD.DBKPOS,"
                    + "QADBKFLD.DBKORD "
                    + "FROM QADBKFLD "
                    + "RIGHT OUTER JOIN SYSCOLUMNS ON "
                    + "SYSCOLUMNS.COLUMN_NAME = QADBKFLD.DBKFLD "
                    + "AND QADBKFLD.DBKFIL = SYSCOLUMNS.TABLE_NAME "
                    + "AND QADBKFLD.DBKLIB = SYSCOLUMNS.TABLE_SCHEMA WHERE "
                    + "SYSCOLUMNS.TABLE_SCHEMA = '%s' and "
                    + "SYSCOLUMNS.TABLE_NAME = '%s'", Schema.toUpperCase().trim(), TabellenName.toUpperCase().trim());

           // String SQLStatement = String.format("SELECT COLUMN_NAME,DATA_TYPE,LENGTH,NUMERIC_SCALE,COLUMN_HEADING "
            //         + "FROM SYSCOLUMNS WHERE TABLE_NAME='%s'  ", TabellenName.toUpperCase().trim());
            ResultSet resultSet = statement.executeQuery(SQLStatement);
            JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultSet);

//            JasperReport jasperReport = JasperCompileManager.compileReport("D:\\Entwicklung\\Rossmann\\Java\\Projekte\\JAS400Browser\\Reports\\TabellenStruckturReport_A4.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport("./Reports/TabellenStruckturReport_A4.jrxml");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, resultSetDataSource);

            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Reportfehler", JOptionPane.WARNING_MESSAGE);
        }

    }

//******************************************************************************
//
//
//
//******************************************************************************
    public String getFeldbezeichnung(String Schema, String TabellenName, String FeldName) {
        ResultSet rs = null;
        String Bezeichnung = "";
        try {
            String SQLStatement = String.format("SELECT COLUMN_HEADING "
                    + "FROM SYSCOLUMNS WHERE TABLE_SCHEMA='%s' and TABLE_NAME='%s' and COLUMN_NAME='%s'",Schema.toUpperCase().trim(), TabellenName.toUpperCase().trim(), FeldName.toUpperCase().trim());
            rs = getSQLResult(SQLStatement);
            while (rs.next()) {
                Bezeichnung = (String) rs.getObject(1).toString();
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Bezeichnung;
    }

//******************************************************************************
//
//
//
//******************************************************************************
    public DefaultTableModel getLibrary(String Lib) {
        ResultSet rs = null;
        DefaultTableModel dtm = null;
        try {
            String SQLStatement = String.format("SELECT CAST(TABLE_TEXT as CHAR(50)) "
                    + "as TABLE_TEXT,TABLE_NAME FROM SYSTABLES where TABLE_SCHEMA='%s'", Lib.toUpperCase().trim());
            rs = getSQLResult(SQLStatement);
            ResultSetMetaData metaData = rs.getMetaData();
            Vector<String> columnNames = new Vector<String>();
            columnNames.add(metaData.getColumnName(2));
            columnNames.add(metaData.getColumnName(1));
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                vector.add(rs.getObject(2));
                vector.add(rs.getObject(1));
                data.add(vector);
            }
            dtm = new DefaultTableModel(data, columnNames);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dtm;
    }

//******************************************************************************
//
//
//
//******************************************************************************
    public String getTabellenBeschreibung(String Schema, String Tabelle) {
        String tb = "";
        ResultSet rs = null;
        try {
            String SQLStatement = String.format("SELECT CAST(TABLE_TEXT as CHAR(50)) "
                    + "as TABLE_TEXT FROM SYSTABLES where TABLE_SCHEMA='%s' and TABLE_NAME='%s'", Schema.toUpperCase().trim(), Tabelle.toUpperCase().trim());
            rs = getSQLResult(SQLStatement);
            while (rs.next()) {
                tb = (String) rs.getObject(1).toString();
            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tb;
    }

//******************************************************************************
//
//
//
//******************************************************************************
    public ResultSet getSchema() {
        ResultSet rs = null;

        try {
            String SQLStatement = String.format("SELECT SCHEMA_NAME,SCHEMA_TEXT  FROM SYSSCHEMAS ");
            rs = getSQLResult(SQLStatement);
//            while (rs.next()) {
//                String s = String.format("<html><b> %-10s </b><p> %s</html>", rs.getString(1), rs.getString(2));
//                 
//            }

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

//******************************************************************************
//
//
//
//******************************************************************************
    public String getSchemaBeschreibung(String Schema) {
        String sb = "";
        ResultSet rs = null;

        try {
            String SQLStatement = String.format("SELECT SCHEMA_TEXT  FROM SYSSCHEMAS where SCHEMA_NAME='" + Schema.toUpperCase().trim() + "'");
            rs = getSQLResult(SQLStatement);
            while (rs.next()) {
                sb = (String) rs.getObject(1).toString();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb;
    }

//******************************************************************************
//
//
//
//******************************************************************************
    public DefaultTableModel getTableData(String Schema, String Tabelle, Vector FName, String where, Boolean MaxDaten) {
        
        String FeldName = FName.toString();
        FeldName = FeldName.replace("[", " ");
        FeldName = FeldName.replace("]", " ");

        DefaultTableModel dtm = null;
        try {
            String SQLStatement = "";
            if (MaxDaten) {
                if (where.length() > 0) {
                    SQLStatement = String.format("SELECT " + FeldName + " FROM %s/%s where %s FETCH FIRST 100 ROWS ONLY", Schema.toUpperCase().trim(), Tabelle.toUpperCase().trim(), where);
                } else {
                    SQLStatement = String.format("SELECT " + FeldName + " FROM %s/%s FETCH FIRST 100 ROWS ONLY", Schema.toUpperCase().trim(), Tabelle.toUpperCase().trim());
                }
            } else {
                if (where.length() > 0) {
                    SQLStatement = String.format("SELECT " + FeldName + " FROM %s/%s where %s  ", Schema.toUpperCase().trim(), Tabelle.toUpperCase().trim(), where);
                } else {
                    SQLStatement = String.format("SELECT " + FeldName + " FROM %S/%s   ", Schema.toUpperCase().trim(), Tabelle.toUpperCase().trim());
                }
            }
            
            ResulSQLStatement = SQLStatement;
            rs_data = getSQLResult(SQLStatement);
            if (rs_data!=null) {
                dtm =  ConvertToMetaData(rs_data);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dtm;
    }

    private DefaultTableModel ConvertToMetaData(ResultSet rs) throws SQLException {
        DefaultTableModel dtm;
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        dtm = new DefaultTableModel(data, columnNames);
        return dtm;
    }
    
    public void Datensatzbearbeiten(JTable jTable3) {

        String sb = "";
        ResultSet rs = null;

        try {
            String SQLStatement = String.format("SELECT * from SQLSCHEMAS");
            rs = getSQLResult(SQLStatement);
            while (rs.next()) {
                sb = (String) rs.getObject(1).toString();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AS400.class.getName()).log(Level.SEVERE, null, ex);
        }

//        com.ibm.as400.access.AS400 myAS = new com.ibm.as400.access.AS400("172.16.0.11","rossmann","panama");
//           
//        
//        
//        com.ibm.as400.access.AS400File asf = new test123(myAS, "BKBK0100"); 
//          
//        asf.open();
    }

}

class test123 extends com.ibm.as400.access.AS400File {

    public test123(com.ibm.as400.access.AS400 as400, String string) {
    }

    @Override
    public Record[] readAll() throws AS400Exception, AS400SecurityException, InterruptedException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

//******************************************************************************
//
// DatenModell für die Anzeige det Tabellenstructuren
//
//******************************************************************************
class TabellenStructurTableModell extends DefaultTableModel {

    public TabellenStructurTableModell(Object rowData[][], Object columnNames[]) {
        super(rowData, columnNames);
    }

    public TabellenStructurTableModell(Vector Data, Vector columnNames) {
        super(Data, columnNames); //(Data, columnNames);
    }

    @Override
    public Class getColumnClass(int col) {
        if (col == 0) //second column accepts only Integer values
        {
            return Boolean.class;
        } else {
            return Object.class;  //other columns accept String values
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0) //first column will be uneditable
        {
            return true;
        } else {
            return false;
        }
    }
}
