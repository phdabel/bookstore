package abelcorrea.com.br.bookstore.db;

public class ProductModel extends BaseModel {

    public long id;
    public String name;
    public Double price;
    public Integer quantity;
    public String supplierName;
    public String supplierPhone;

    public ProductModel(){}

    public ProductModel(String name, Double price, Integer quantity, String supplierName, String supplierPhone){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
    }

    public ProductModel(long id, String name, Double price, Integer quantity, String supplierName, String supplierPhone){
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
    }

}
