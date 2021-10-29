package my.edu.tarc.thebestburger.Domain;

public class ProductDomain {
    private String product_ID;
    private String product_Name;
    private String product_Cat;
    private String product_Photo;
    private String product_Desc;
    private String product_Price;

    public ProductDomain(String product_Cat, String product_Desc, String product_ID, String product_Name, String product_Photo, String product_Price) {
        this.product_ID = product_ID;
        this.product_Name = product_Name;
        this.product_Cat = product_Cat;
        this.product_Photo = product_Photo;
        this.product_Desc = product_Desc;
        this.product_Price = product_Price;
    }

    public ProductDomain() {
    }

    public String getProduct_ID() {
        return product_ID;
    }

    public void setProduct_ID(String product_ID) {
        this.product_ID = product_ID;
    }

    public String getProduct_Name() {
        return product_Name;
    }

    public void setProduct_Name(String product_Name) {
        this.product_Name = product_Name;
    }

    public String getProduct_Cat() {
        return product_Cat;
    }

    public void setProduct_Cat(String product_Cat) {
        this.product_Cat = product_Cat;
    }

    public String getProduct_Photo() {
        return product_Photo;
    }

    public void setProduct_Photo(String product_Photo) {
        this.product_Photo = product_Photo;
    }

    public String getProduct_Desc() {
        return product_Desc;
    }

    public void setProduct_Desc(String product_Desc) {
        this.product_Desc = product_Desc;
    }

    public String getProduct_Price() {
        return product_Price;
    }

    public void setProduct_Price(String product_Price) {
        this.product_Price = product_Price;
    }
}
