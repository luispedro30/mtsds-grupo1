package ecommerce.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User2Email {
    private String ownerRef;
    private String emailFrom;
    private String emailTo;
    private String subject;
    private String text;
}
