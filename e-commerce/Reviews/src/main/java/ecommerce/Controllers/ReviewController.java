package ecommerce.Controllers;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.CustomInternalServerException;
import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Exceptions.NoReviewsFoundException;
import ecommerce.Infra.RestExceptionHandler;
import ecommerce.Models.Review;
import ecommerce.Services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
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

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    private static Marker marker = MarkerFactory.getMarker("review-service");

    private static Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Operation(summary = "Retrieve all reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of reviews retrieved"),
            @ApiResponse(responseCode = "404", description = "No reviews found")
    })
    @GetMapping
    public ResponseEntity<List<Review>> getAll() {
        try {
            List<Review> reviews = reviewService.getAllReviews();
            if (!reviews.isEmpty()) {
                logger.info("getAll() request received ... 200 OK");
                return new ResponseEntity<>(reviews, HttpStatus.OK);
            } else {
                logger.info("getAll() request received ... 404 Not Found");
                throw new NoReviewsFoundException("No reviews found");
            }
        } catch (NoReviewsFoundException ex) {
            logger.warn("No reviews found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new CustomInternalServerException("Internal server error occurred");
        }
    }


    @Operation(summary = "Retrieve a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Review> getById(@PathVariable Integer id) throws ItemDoesNotExistException {
        try {
            Review review = reviewService.getReviewById(id);
            logger.info("getById() request received ... pending");
            if (review != null) {
                logger.info("getById() request received ... 200 {}", review);
                return new ResponseEntity<>(review, HttpStatus.OK);
            } else {
                logger.info("getById() request received ... 404 Not Found");
                throw new ItemDoesNotExistException(String.valueOf(id), "getById()");
            }
        } catch (ItemDoesNotExistException ex) {
            logger.error("Item does not exist: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new ItemDoesNotExistException(String.valueOf(id), "getById()");
        }
    }



    @Operation(summary = "Add a new review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Review review, HttpServletRequest request) {
        try {
            Integer reviewId = review.getId();
            if (reviewId != null && reviewService.getReviewById(reviewId) != null) {
                logger.info("Review with ID {} already exists", reviewId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Review with ID " + reviewId + " already exists");
            }

            Review newReview = reviewService.addReview(review, request);
            newReview.setId(review.getId());
            logger.info("New review added with ID {}", review.getId());
            return new ResponseEntity<>(newReview, HttpStatus.CREATED);
        } catch (ItemDoesNotExistException e) {
            logger.error("User does not exist: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (AlreadyExistingException e) {
            logger.error("Review already exists: {}", e.getMessage());
            return restExceptionHandler.alreadyExistingExceptionHandler(e);
        } catch (Exception e) {
            logger.error("Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Update a review by ID for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{id}/user/{userId}")
    public ResponseEntity<Review> updateByIdForUser(@PathVariable Integer id,
                                                    @PathVariable Integer userId,
                                                    @RequestBody Review updatedReview,
                                                    HttpServletRequest request) throws ItemDoesNotExistException {
        try {
            Review existingReview = reviewService.getReviewById(id);
            if (existingReview == null || !existingReview.getUserId().equals(userId)) {
                logger.info("Review not found with ID {} or User ID {} doesn't match the review's User ID", id, userId);
                throw new ItemDoesNotExistException(String.valueOf(id), "updateByIdForUser()");
            }

            Review updated = reviewService.updateReviewByIdForUser(id, userId, updatedReview, request);
            if (updated != null) {
                logger.info("Review updated with ID {} for User ID {}", id, userId);
                return ResponseEntity.ok(updated);
            } else {
                logger.info("Review not found with ID {} for User ID {}", id, userId);
                throw new ItemDoesNotExistException(String.valueOf(id), "updateByIdForUser()");
            }
        } catch (ItemDoesNotExistException ex) {
            logger.error("Review not found or User ID doesn't match: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred: {}", ex.getMessage());
            throw new CustomInternalServerException("Internal server error occurred");
        }
    }


}
