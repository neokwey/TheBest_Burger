package my.edu.tarc.thebestburger.Domain;

public class PopularDomain {
    private String product_ID;
    private String product_Name;
    private String product_Photo;
    private String product_Price;

    public PopularDomain(String product_ID, String product_Name, String product_Photo, String product_Price) {
        this.product_ID = product_ID;
        this.product_Name = product_Name;
        this.product_Photo = product_Photo;
        this.product_Price = product_Price;
    }

    public PopularDomain() {
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

    public String getProduct_Photo() {
        return product_Photo;
    }

    public void setProduct_Photo(String product_Photo) {
        this.product_Photo = product_Photo;
    }

    public String getProduct_Price() {
        return product_Price;
    }

    public void setProduct_Price(String product_Price) {
        this.product_Price = product_Price;
    }

}
