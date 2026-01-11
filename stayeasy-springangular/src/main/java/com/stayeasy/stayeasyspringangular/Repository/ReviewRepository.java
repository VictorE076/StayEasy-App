package com.stayeasy.stayeasyspringangular.Repository;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
  List<Review> findByPropertyId(Long propertyId);
}
