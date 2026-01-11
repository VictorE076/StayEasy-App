package com.stayeasy.stayeasyspringangular.Repository;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  List<Review> findByProperty_IdOrderByCreatedAtDesc(Long propertyId);

  Optional<Review> findByProperty_IdAndUser_Id(Long propertyId, Integer userId);

  @Query("select avg(r.rating) from Review r where r.property.id = :propertyId")
  Double averageRating(Long propertyId);

  @Query("select count(r) from Review r where r.property.id = :propertyId")
  Long countByProperty(Long propertyId);

}

