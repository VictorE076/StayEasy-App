package com.stayeasy.stayeasyspringangular.Repository;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

  List<Property> findByCity(String city);

  List<Property> findByCityAndPricePerNightLessThanEqual(
    String city, BigDecimal pricePerNight
  );

  List<Property> findByPricePerNightLessThanEqual(BigDecimal maxPrice);

  boolean existsByOwner_Id(Integer ownerId);

}
