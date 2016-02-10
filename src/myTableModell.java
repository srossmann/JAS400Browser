
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public abstract class myTableModell extends DefaultTableModel {

   
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    
     

}
