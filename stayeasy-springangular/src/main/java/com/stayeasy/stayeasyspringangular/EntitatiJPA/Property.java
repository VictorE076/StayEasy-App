package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Property {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String city;
  private String address;

  private BigDecimal pricePerNight;
  private Integer maxGuests;

  @Enumerated(EnumType.STRING)
  private PropertyType propertyType;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User owner;

  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PropertyImage> images;

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviews;

  @ManyToMany
  @JoinTable(
    name = "property_amenities",
    joinColumns = @JoinColumn(name = "property_id"),
    inverseJoinColumns = @JoinColumn(name = "amenity_id")
  )
  private List<Amenity> amenities;
}
