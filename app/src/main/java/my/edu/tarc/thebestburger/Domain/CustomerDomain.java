package my.edu.tarc.thebestburger.Domain;

public class CustomerDomain {
    String name, email, phoneno, password;

    public CustomerDomain() {
    }

    public CustomerDomain(String email, String name, String password, String phoneno) {
        this.name = name;
        this.email = email;
        this.phoneno = phoneno;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
