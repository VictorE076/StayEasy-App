package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "house_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HouseRules {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private boolean smokingAllowed;
  private boolean petsAllowed;

  private LocalTime checkInTime;
  private LocalTime checkOutTime;

  @OneToOne
  @JoinColumn(name = "property_id")
  private Property property;

}
