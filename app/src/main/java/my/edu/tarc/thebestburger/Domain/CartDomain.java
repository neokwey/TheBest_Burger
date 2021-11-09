package my.edu.tarc.thebestburger.Domain;

public class CartDomain {
    private String product_ID,cart_ID,qty;

    public CartDomain(String cart_ID, String product_ID, String qty) {
        this.product_ID = product_ID;
        this.cart_ID = cart_ID;
        this.qty = qty;
    }

    public CartDomain() {
    }

    public String getProduct_ID() {
        return product_ID;
    }

    public void setProduct_ID(String product_ID) {
        this.product_ID = product_ID;
    }

    public String getCart_ID() {
        return cart_ID;
    }

    public void setCart_ID(String cart_ID) {
        this.cart_ID = cart_ID;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
