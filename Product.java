package tpfinal;

public class Product {
	
	private String name;
    private int stock;
    private double price;

    public Product(String name, int stock, double price) {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    public int reduceStock(int quantity) {
        this.stock -= quantity;
        
        return this.stock;
    }

    @Override
    public String toString() {
        return name + " (Stock: " + stock + ", Precio: $" + price + ")";
    }

}
