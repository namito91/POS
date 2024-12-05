package tpfinal;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class VentasGUI extends JFrame {
	
	static List<Venta> ventas;

	private JTable ventasTable;
    private DefaultTableModel tableModel;

    public VentasGUI(List<Venta> ventas) {
        
    	this.ventas = ventas;
    	
    	// Configuración de la ventana
        setTitle("Listado de Ventas");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear modelo de tabla
        String[] columnNames = {"Fecha", "Monto"};
        tableModel = new DefaultTableModel(columnNames, 0);
        ventasTable = new JTable(tableModel);

        // Llenar la tabla con datos de la lista de ventas
        for (Venta venta : ventas) {
            Object[] row = {venta.getFecha(), venta.getMonto()};
            tableModel.addRow(row);
        }

        // Agregar tabla a un scroll pane
        JScrollPane scrollPane = new JScrollPane(ventasTable);

        // Diseño de la ventana
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Hacer visible la ventana
        setVisible(true);
    }
    
    public static void main(String[] args) {

        // Crear y mostrar la interfaz
        SwingUtilities.invokeLater(() -> new VentasGUI(ventas));
    }
}
