package com.stayeasy.stayeasyspringangular.DTO;

public record PremiumStatusDTO(
    boolean premium,
    String planName,
    String price,
    String message
) { }
