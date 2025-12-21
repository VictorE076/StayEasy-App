package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PropertyImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imagePath;

  private boolean isMain;

  @ManyToOne
  @JoinColumn(name = "property_id")
  private Property property;

}
