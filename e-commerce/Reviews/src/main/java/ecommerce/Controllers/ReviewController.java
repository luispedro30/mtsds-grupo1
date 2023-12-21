package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Review;
import ecommerce.Services.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    ResponseEntity<List<Review>> getAll(){
        List<Review> reviews =  reviewService.getAllReviews();
        if(!reviews.isEmpty()) {
            return new ResponseEntity<List<Review>>(
                    reviews,
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Review>>(
                HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    ResponseEntity<Review> getById(@PathVariable Integer id){
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    @PostMapping
    ResponseEntity<?> add(@RequestBody Review review, HttpServletRequest request){

        Review newReview;
        try {
            newReview = reviewService.addReview(review, request);
        } catch (ItemDoesNotExistException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (Exception e){
            System.out.println("Erro");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        newReview.setId(review.getId());
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);

    }
}
