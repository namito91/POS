package tpfinal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

// TODO , agregar el campo stock a cada producto,
// TODO , generar aviso de productos con stock de menos de 5 unidades,
// TODO , agregar interfaz de venta?


public class InventarioApp {
    
	private JFrame frame;
    private JTable productTable;
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private final String filePath = "productos.txt";

    public static void main(String[] args) {
        
    	//asegura que la construcción y manipulación de la interfaz gráfica se ejecuten correctamente 
    	//en el hilo dedicado a Swing, mejorando la estabilidad y el rendimiento de la aplicación.
    	SwingUtilities.invokeLater(() -> new InventarioApp().createAndShowGUI());
    }
    
    
    public void createAndShowGUI() {
        
    	frame = new JFrame("Gestor de Productos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        
        /*
        JButton venderButton = new JButton("Vender");
        venderButton.addActionListener((ActionEvent e) -> {
        	VenderApp venderApp = new VenderApp();
        	venderApp.createAndShowGUI(); // Abrir la segunda ventana
        });
        */
        
        
        // Crear modelo de tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre","Categoria","Precio","Stock"}, 0);
        productTable = new JTable(tableModel);
        loadProductsFromFile();

        // Panel superior para búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Buscar");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Panel inferior para botones
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Agregar Producto");
        JButton updateButton = new JButton("Actualizar Producto");
        JButton deleteButton = new JButton("Eliminar Producto");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
       // buttonPanel.add(venderButton);

        // Acciones de los botones
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        searchButton.addActionListener(e -> searchProduct());

        // Agregar componentes al frame
        frame.add(new JScrollPane(productTable), BorderLayout.CENTER);
        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    
    private void loadProductsFromFile() {
        
    	try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            
    		String line;
            tableModel.setRowCount(0); // Limpiar la tabla
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                tableModel.addRow(data);
            }
       
    	} catch (IOException e) {
            System.out.println("Error al cargar los productos: " + e.getMessage());
        }
    }

    
    private void saveProductsToFile() {
        
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            
    		for (int i = 0; i < tableModel.getRowCount(); i++) {
                
    			String id = tableModel.getValueAt(i, 0).toString();
                String name = tableModel.getValueAt(i, 1).toString();
                String category = tableModel.getValueAt(i, 2).toString();
                String price = tableModel.getValueAt(i, 3).toString();
                String stock = tableModel.getValueAt(i, 4).toString();

                writer.write(id + "," + name + "," + category + "," + price + "," + stock);
                writer.newLine();
            }
        
    	} catch (IOException e) {
            System.out.println("Error al guardar los productos: " + e.getMessage());
        }
    }

    
    private void addProduct() {
        
    	String id = JOptionPane.showInputDialog(frame, "ID del producto:");
        String name = JOptionPane.showInputDialog(frame, "Nombre del producto:");
        String category = JOptionPane.showInputDialog(frame, "Categoria del producto:");
        String price = JOptionPane.showInputDialog(frame, "Precio del producto:");
         
        System.out.println("name : " + name);
        
        
        if (!id.isEmpty() && !name.isEmpty() && !category.isEmpty() && !price.isEmpty()) {
            
        	if(!validateIDandName(id, name)) {
        		
        		tableModel.addRow(new String[]{id, name,category, price});
                saveProductsToFile();
        	}else {
        		
        		JOptionPane.showMessageDialog(frame, "Producto ya existente.", "Información", JOptionPane.INFORMATION_MESSAGE);
        	}
        	
        	
        	
        }
    }
    
    
    private void updateProduct() {
        
    	int selectedRow = productTable.getSelectedRow();
        
    	if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
    	
        String id = JOptionPane.showInputDialog(frame, "Nuevo ID del producto:", tableModel.getValueAt(selectedRow, 0));
        String name = JOptionPane.showInputDialog(frame, "Nuevo nombre del producto:", tableModel.getValueAt(selectedRow, 1));
        String category = JOptionPane.showInputDialog(frame, "Nueva categoria del producto:", tableModel.getValueAt(selectedRow, 2));
        String price = JOptionPane.showInputDialog(frame, "Nuevo precio del producto:", tableModel.getValueAt(selectedRow, 3));
        
        
        if (!id.isEmpty() && !name.isEmpty() && !category.isEmpty() && !price.isEmpty()) {
            
        	// validar si ya existe un producto con el mismo ID o nombre
        	if(!validateIDandName(id, name)) {
        		
        		tableModel.setValueAt(id, selectedRow, 0);
                tableModel.setValueAt(name, selectedRow, 1);
                tableModel.setValueAt(category, selectedRow, 2);
                tableModel.setValueAt(price, selectedRow, 3);
                
                saveProductsToFile();
        	
        	}else {
        		
        		JOptionPane.showMessageDialog(frame, "Producto ya existente.", "Información", JOptionPane.INFORMATION_MESSAGE);
        	}
        	
        }
    }
    
    
    private void deleteProduct() {
        
    	int selectedRow = productTable.getSelectedRow();
        
    	if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Seleccione un producto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "¿Está seguro de eliminar este producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            saveProductsToFile();
        }
    }

    
    private void searchProduct() {
        
    	String searchQuery = searchField.getText().trim().toLowerCase();
        
    	if (searchQuery.isEmpty()) {
            
    		JOptionPane.showMessageDialog(frame, "Ingrese un término de búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            
        	String id = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String name = tableModel.getValueAt(i, 1).toString().toLowerCase();
            
            if (id.contains(searchQuery) || name.contains(searchQuery)) {
                productTable.setRowSelectionInterval(i, i);
                productTable.scrollRectToVisible(productTable.getCellRect(i, 0, true));
                return;
            }
        }
        	
        JOptionPane.showMessageDialog(frame, "Producto no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    private boolean searchProductByIDorName(String prdID, String prdName) {
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            
        	String id = tableModel.getValueAt(i, 0).toString().toLowerCase();
            String name = tableModel.getValueAt(i, 1).toString().toLowerCase();
            
            if (id.equals(prdID) || name.equals(prdName)) {
                
                return true;
            }
        }
        	
        JOptionPane.showMessageDialog(frame, "Producto no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
        
        return false;
    }
    
    
    public boolean validateIDandName(String id,String name) {
    	
    	boolean result = searchProductByIDorName(id,name);
    	
    	return result;
    }
}
