package com.certplatform.project.dto;

import com.certplatform.project.enums.Difficulty;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {
    
  @NotBlank
  private UUID id;
  
  @NotBlank
  private String title;

  @NotBlank
  private String url;

  private String description;

  @NotNull
  private Difficulty difficulty;

  @NotNull
  @Min(1)
  @JsonProperty("duration_days") // ✅ maps to frontend field
  private Integer durationDays;

  @Size(min = 1)
  @JsonProperty("skills_associated") // ✅ maps to frontend field
  private List<@NotBlank String> skillsAssociated;

  @NotNull
  @Size(min = 1)
  @JsonProperty("roles_associated") // ✅ maps to frontend field
  private List<@NotBlank String> rolesAssociated;
}
