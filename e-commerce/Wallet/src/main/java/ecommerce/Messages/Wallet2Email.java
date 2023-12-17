package ecommerce.Messages;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Wallet2Email {
    private String ownerRef;
    private String emailFrom;
    private String emailTo;
    private String subject;
    private String text;
}
