package ecommerce.Controllers;

import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Review;
import ecommerce.Services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Retrieve all reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of reviews retrieved"),
            @ApiResponse(responseCode = "404", description = "No reviews found")
    })
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

    @Operation(summary = "Retrieve a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<Review> getById(@PathVariable Integer id){
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }


    @Operation(summary = "Add a new review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
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
