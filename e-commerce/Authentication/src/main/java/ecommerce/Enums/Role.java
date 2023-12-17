package ecommerce.Enums;

public enum Role {
    USER ("user"),

    FORNECEDOR("fornecedor"),
    ADMIN ("admin");

    public String role;

    Role(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
