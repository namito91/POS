package tpfinal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// TODO,  registrar las ventas y el monto de cada operacion

// TODO , generar reporte de ventas , con la fecha de cada venta , y el monto total vendido ese dia,


public class VenderApp {
    
	// inventario cargado desde productos.txt
    private List<Product> inventory = new ArrayList<>();
    private List<Venta> ventas = new ArrayList<>();
    
    private DefaultTableModel salesTableModel;
    private JTable salesTable;
    private JTextField searchField, quantityField;
    private JLabel totalLabel;
    private double totalSales = 0.0;
   
    private final String filePath = "productos.txt";
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VenderApp().createAndShowGUI());
    }
    

    // se carga la UI
    public void createAndShowGUI() {
    	
    	// Cargar el inventario desde el archivo
        loadInventoryFromFile();

        JFrame frame = new JFrame("Vender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // navegar hacia pantalla de inventario
        JButton inventarioButton = new JButton("Ver inventario");
       
        inventarioButton.addActionListener((ActionEvent e) -> {
        	InventarioApp inventarioApp = new InventarioApp();
        	inventarioApp.createAndShowGUI(); 
        });
        
        

        // navegar hacia pantalla de ventas
        JButton verVentasButton = new JButton("Ver ventas");
        
        verVentasButton.addActionListener((ActionEvent e) -> {
        	VentasGUI ventasGUI = new VentasGUI(ventas);
        	
        });
        
        
        
        // Panel para buscar productos
        JPanel searchPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel searchLabel = new JLabel("Buscar Producto:");
        searchField = new JTextField();
        JButton searchButton = new JButton("Buscar");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(new JLabel()); // Espacio vacío
        searchPanel.add(searchButton);

        // Tabla de ventas
        salesTableModel = new DefaultTableModel(new String[]{"Producto", "Cantidad", "Precio Unitario", "Subtotal"}, 0);
        salesTable = new JTable(salesTableModel);

        // Panel para agregar productos al carrito
        JPanel addProductPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel quantityLabel = new JLabel("Cantidad:");
        quantityField = new JTextField();
        JButton addProductButton = new JButton("Agregar al Carrito");
        addProductPanel.add(quantityLabel);
        addProductPanel.add(quantityField);
        addProductPanel.add(new JLabel()); // Espacio vacío
        addProductPanel.add(addProductButton);

        // Panel para mostrar el total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: $0.00");
        JButton sellButton = new JButton("Vender");
        
        totalPanel.add(verVentasButton);
        totalPanel.add(inventarioButton);
        totalPanel.add(totalLabel);
        totalPanel.add(sellButton);
        
        
        // Agregar acciones a los botones
        searchButton.addActionListener(this::searchProduct);
        addProductButton.addActionListener(this::addProductToSales);
        sellButton.addActionListener(this::sellProducts);
       
        
        
        // Layout principal
        frame.setLayout(new BorderLayout());
        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(salesTable), BorderLayout.CENTER);
        frame.add(addProductPanel, BorderLayout.WEST);
        frame.add(totalPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }

    
    
    // método para cargar productos desde el archivo productos.txt
    private void loadInventoryFromFile() {
        
    	File file = new File("productos.txt");
        
    	if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "El archivo productos.txt no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    	
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            
        	String line;
            
        	while ((line = br.readLine()) != null) {
                
        		String[] parts = line.split(",");
                
        		if (parts.length == 5) {
                    
        			String name = parts[1].trim();
                    String category = parts[2].trim(); // No usado en este ejemplo, pero puede ser útil
                    double price = Double.parseDouble(parts[3].trim());
                    int stock = Integer.parseInt(parts[4].trim());
                    inventory.add(new Product(name, stock, price));
                }
            }
        
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al leer productos.txt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // método para buscar un producto
    private void searchProduct(ActionEvent e) {
        
    	// user input
    	String searchQuery = searchField.getText().trim().toLowerCase();
        
    	if (searchQuery.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un producto para buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (Product product : inventory) {
            if (product.getName().toLowerCase().contains(searchQuery)) {
                JOptionPane.showMessageDialog(null, "Producto encontrado: " + product, "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "Producto no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    // método para agregar un producto al carrito
    private void addProductToSales(ActionEvent e) {
        
        String searchQuery = searchField.getText().trim().toLowerCase();
        String quantityText = quantityField.getText().trim();
        
        // valida que el campo de busqueda y el campo de cantidad no esten vacios,
        if (searchQuery.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un producto y una cantidad.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            
        	// castea a int , el campo cantidad
        	int quantity = Integer.parseInt(quantityText);

            for (Product product : inventory) {
                
            	// valida si el input de usuario coincide con un producto existente,
            	if (product.getName().toLowerCase().contains(searchQuery)) {
                    
            		// valida el stock del producto seleccionado,
            		if (product.getStock() < quantity) {
                        JOptionPane.showMessageDialog(null, "Stock insuficiente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
            		
            		// se agrega el producto al carrito
                    double subtotal = quantity * product.getPrice();
                    
                    // agrega una nueva fila al carrito , con el producto agregado,
                    salesTableModel.addRow(new Object[]{product.getName(), quantity, product.getPrice(), subtotal});
                    totalSales += subtotal;
                    totalLabel.setText("Total: $" + String.format("%.2f", totalSales));
                    
                    return;
                }
            }

            JOptionPane.showMessageDialog(null, "Producto no encontrado en el inventario.", "Información", JOptionPane.INFORMATION_MESSAGE);
        
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Ingrese una cantidad válida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // método para simular una venta
    private void sellProducts(ActionEvent e) {
        
    	
    	double ventas = 0;
    	double precio;
    	
    	
    	// valida si hay productos en el carrito,
    	if (salesTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay productos en el carrito.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
    	
    	
        for (int i = 0; i < salesTableModel.getRowCount(); i++) {
            
        	// toma el nombre y la cantidad de los productos en el carrito,
        	String productName = salesTableModel.getValueAt(i, 0).toString();
            int quantitySold = Integer.parseInt(salesTableModel.getValueAt(i, 1).toString());
            
            
            for (Product product : inventory) {
                
            	if (product.getName().equals(productName)) {
                    
            		// se actualiza el stock del producto, tanto en el file , como en inventario
                	int stock = product.reduceStock(quantitySold);
                    
                	updateProductStockInFile(productName,stock);
                	
                	
                	precio = product.getPrice() * quantitySold;
                	ventas += precio;
                	
                    break;
                }
            }
            
            
            
            
           // System.out.println("venta 11 : " + VenderApp.ventas.get(0).getMonto());
           
        }
        
     // Obtener fecha y hora actuales
        LocalDateTime fechaHoraActual = LocalDateTime.now();

        // Formatear fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHoraFormateada = fechaHoraActual.format(formatter);
        
        // registrar el total de esta venta del dia
        this.ventas.add(new Venta(ventas, fechaHoraFormateada));
        
        JOptionPane.showMessageDialog(null, "Venta realizada por un total de: $" + String.format("%.2f", totalSales), "Éxito", JOptionPane.INFORMATION_MESSAGE);
        totalSales = 0.0;
        totalLabel.setText("Total: $0.00");
        salesTableModel.setRowCount(0); // Limpiar el carrito
    }
    
    
    // TODO , analizar este metodo !!!
    public void updateProductStockInFile(String productName, Integer stock) {
        
    	File file = new File("productos.txt");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "El archivo productos.txt no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            
        	StringBuilder updatedContent = new StringBuilder();
            String line;
            boolean productFound = false;

            // Leer línea por línea y actualizar el stock
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");

                if (columns.length >= 5 && columns[1].trim().equalsIgnoreCase(productName)) {
                    // Actualizar el stock
                    columns[4] = stock.toString();
                    productFound = true;
                }

                // Reconstruir la línea
                updatedContent.append(String.join(",", columns)).append(System.lineSeparator());
            }

            if (!productFound) {
                JOptionPane.showMessageDialog(null, "Producto no encontrado en el archivo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Escribir el contenido actualizado en el archivo
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(updatedContent.toString());
            }

          //  JOptionPane.showMessageDialog(null, "Stock actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo: " + e.getMessage());
        }
    }

    
    
    public void verVentas(ActionEvent e) {
    	
    	if(ventas != null && ventas.size() > 0) {
    		
    		for(int i = 0 ; i < ventas.size();i++) {
        		
        		System.out.println("venta " + i + " : " + ventas.get(i).getMonto());
        	}
    	}
    	
    	
    	
    }
   
    
    
    
    
}

