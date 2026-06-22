package com.stayeasy.stayeasyspringangular.Repository;

import com.stayeasy.stayeasyspringangular.EntitatiJPA.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {

  Optional<Amenity> findByNameIgnoreCase(String name);

}
