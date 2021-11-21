package my.edu.tarc.thebestburger.Domain;

public class AddressDomain {
    private String addrID,addr1,addr2,lan,lon,name,phone;

    public AddressDomain() {
    }

    public AddressDomain(String addrID, String addr1, String addr2, String lan, String lon, String name, String phone) {
        this.addrID = addrID;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.lan = lan;
        this.lon = lon;
        this.name = name;
        this.phone = phone;
    }

    public String getAddrID() {
        return addrID;
    }

    public void setAddrID(String addrID) {
        this.addrID = addrID;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
