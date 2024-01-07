package ecommerce.Enums;


public enum Category {
    SHIRT_XS("Shirt - XS"),
    SHIRT_S("Shirt - S"),
    SHIRT_M("Shirt - M"),
    SHIRT_L("Shirt - L"),
    SHIRT_XL("Shirt - XL"),
    SHIRT_XXL("Shirt - XXL"),

    PANTS_28_30("Pants - 28/30"),
    PANTS_30_32("Pants - 30/32"),
    PANTS_32_34("Pants - 32/34"),
    PANTS_34_36("Pants - 34/36"),
    PANTS_36_38("Pants - 36/38"),
    PANTS_38_40("Pants - 38/40"),

    DRESS_XS("Dress - XS"),
    DRESS_S("Dress - S"),
    DRESS_M("Dress - M"),
    DRESS_L("Dress - L"),
    DRESS_XL("Dress - XL"),
    DRESS_XXL("Dress - XXL"),

    JACKET_S("Jacket - S"),
    JACKET_M("Jacket - M"),
    JACKET_L("Jacket - L"),
    JACKET_XL("Jacket - XL"),
    JACKET_XXL("Jacket - XXL"),

    COAT_S("Coat - S"),
    COAT_M("Coat - M"),
    COAT_L("Coat - L"),
    COAT_XL("Coat - XL"),
    COAT_XXL("Coat - XXL"),

    HOODIE_XS("Hoodie - XS"),
    HOODIE_S("Hoodie - S"),
    HOODIE_M("Hoodie - M"),
    HOODIE_L("Hoodie - L"),
    HOODIE_XL("Hoodie - XL"),
    HOODIE_XXL("Hoodie - XXL"),

    SKIRT_XS("Skirt - XS"),
    SKIRT_S("Skirt - S"),
    SKIRT_M("Skirt - M"),
    SKIRT_L("Skirt - L"),
    SKIRT_XL("Skirt - XL"),
    SKIRT_XXL("Skirt - XXL"),

    SHORTS_XS("Shorts - XS"),
    SHORTS_S("Shorts - S"),
    SHORTS_M("Shorts - M"),
    SHORTS_L("Shorts - L"),
    SHORTS_XL("Shorts - XL"),
    SHORTS_XXL("Shorts - XXL"),

    BLOUSE_XS("Blouse - XS"),
    BLOUSE_S("Blouse - S"),
    BLOUSE_M("Blouse - M"),
    BLOUSE_L("Blouse - L"),
    BLOUSE_XL("Blouse - XL"),
    BLOUSE_XXL("Blouse - XXL"),

    OUTERWEAR_XS("Outerwear - XS"),
    OUTERWEAR_S("Outerwear - S"),
    OUTERWEAR_M("Outerwear - M"),
    OUTERWEAR_L("Outerwear - L"),
    OUTERWEAR_XL("Outerwear - XL"),
    OUTERWEAR_XXL("Outerwear - XXL"),

    UNDERWEAR_XS("Underwear - XS"),
    UNDERWEAR_S("Underwear - S"),
    UNDERWEAR_M("Underwear - M"),
    UNDERWEAR_L("Underwear - L"),
    UNDERWEAR_XL("Underwear - XL"),
    UNDERWEAR_XXL("Underwear - XXL"),

    ACTIVEWEAR_XS("Activewear - XS"),
    ACTIVEWEAR_S("Activewear - S"),
    ACTIVEWEAR_M("Activewear - M"),
    ACTIVEWEAR_L("Activewear - L"),
    ACTIVEWEAR_XL("Activewear - XL"),
    ACTIVEWEAR_XXL("Activewear - XXL"),

    SOCKS_XS("Socks - XS"),
    SOCKS_S("Socks - S"),
    SOCKS_M("Socks - M"),
    SOCKS_L("Socks - L"),
    SOCKS_XL("Socks - XL"),
    SOCKS_XXL("Socks - XXL");





    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

