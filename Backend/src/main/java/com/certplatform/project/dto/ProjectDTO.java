package com.certplatform.project.dto;

import com.certplatform.project.enums.Difficulty;
import com.certplatform.project.enums.ReviewMode;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

  @NotBlank
  private String title;

  private String description;

  private String requirementDocKey; // R2 key for requirement PDF

  @NotNull
  private Difficulty difficulty;

  @NotNull
  @Min(1)
  @JsonProperty("duration_days") // ✅ maps to frontend field
  private Integer durationDays;

  @NotNull
  @JsonProperty("review_mode") // ✅ maps to frontend field
  private ReviewMode reviewMode;

  @NotNull
  @Size(min = 1)
  @JsonProperty("skills_associated") // ✅ maps to frontend field
  private List<@NotBlank String> skillsAssociated;

  @NotNull
  @Size(min = 1)
  @JsonProperty("roles_associated") // ✅ maps to frontend field
  private List<@NotBlank String> rolesAssociated;
}
