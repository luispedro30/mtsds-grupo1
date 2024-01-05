package ecommerce.Repository;

import ecommerce.Model.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<EmailModel, UUID> {

    List<EmailModel>  findAll();
}
