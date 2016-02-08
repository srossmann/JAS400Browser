
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rossmann
 */
//******************************************************************************
//
//
//
//******************************************************************************
public class MyRenderer extends JLabel implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        TableModel model = table.getModel();
        setOpaque(true);
        setBackground(row % 2 == 0 ? Color.WHITE : Color.getHSBColor(255, 255, 159));
        setForeground(Color.BLACK);
        setText((value != null) ? value.toString() : "");
        
        if (isSelected) {
             
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setBackground(Color.YELLOW);
        } else {
            setBorder(BorderFactory.createLineBorder(table.getBackground()));
        }
        return this;
    }
}
