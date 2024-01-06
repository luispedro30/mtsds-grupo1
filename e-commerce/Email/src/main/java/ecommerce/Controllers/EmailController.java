package ecommerce.Controllers;


import ecommerce.Dtos.EmailDto;
import ecommerce.Model.EmailModel;
import ecommerce.Service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class EmailController {

    @Autowired
    EmailService emailService;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EmailNotFoundException extends RuntimeException {
        public EmailNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException(String message) {
            super(message);
        }
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<String> handleEmailNotFoundException(EmailNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> handleInvalidEmailException(InvalidEmailException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }



    /**
     * Send email with provided details.
     *
     * @param emailDto Details for sending the email
     * @return ResponseEntity with the sent email details
     */
    @Operation(summary = "Send email")
    @PostMapping("/sending-email")
    public ResponseEntity<EmailModel> sendingEmail(@RequestBody @Valid EmailDto emailDto) {
        EmailModel emailModel = new EmailModel();
        BeanUtils.copyProperties(emailDto, emailModel);
        emailService.sendEmail(emailModel);
        return new ResponseEntity<>(emailModel, HttpStatus.CREATED);
    }

    /**
     * Retrieve all emails.
     *
     * @param pageable Pagination details
     * @return ResponseEntity with a page of email details
     */
    @Operation(summary = "Get all emails")
    @GetMapping("/emails")
    public ResponseEntity<Page<EmailModel>> getAllEmails(@PageableDefault(page = 0, size = 5, sort = "emailId", direction = Sort.Direction.DESC) Pageable pageable){
        return new ResponseEntity<>(emailService.findAll(pageable), HttpStatus.OK);
    }

    /**
     * Retrieve a specific email by its ID.
     *
     * @param emailId ID of the email to retrieve
     * @return ResponseEntity with the requested email details
     */
    @Operation(summary = "Get one email by ID")
    @GetMapping("/emails/{emailId}")
    public ResponseEntity<Object> getOneEmail(@PathVariable(value="emailId") UUID emailId){
        Optional<EmailModel> emailModelOptional = emailService.findById(emailId);
        if(!emailModelOptional.isPresent()) {
            throw new EmailNotFoundException("Email not found.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(emailModelOptional.get());
        }
    }
}
