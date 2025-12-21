package com.stayeasy.stayeasyspringangular.EntitatiJPA;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer rating;

  @Column(columnDefinition = "TEXT")
  private String comment;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "property_id")
  private Property property;

  private LocalDateTime createdAt;

}
