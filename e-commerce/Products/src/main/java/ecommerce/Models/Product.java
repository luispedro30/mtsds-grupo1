package ecommerce.Models;

import ecommerce.Enums.Category;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@Accessors(chain = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private Category category;

    private double price;

    private Integer stockQuantity;

    private String imageUrl;

}
