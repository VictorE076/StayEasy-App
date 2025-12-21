package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Availability {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate availableFrom;
  private LocalDate availableTo;

  @ManyToOne
  @JoinColumn(name = "property_id")
  private Property property;

}
