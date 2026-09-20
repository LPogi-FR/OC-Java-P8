package com.openclassrooms.tourguide.model;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class NearAttractionModel {

  //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
  //  Return a new JSON object that contains:
  // Name of Tourist attraction,
  // Tourist attractions lat/long,
  // The user's location lat/long,
  // The distance in miles between the user's location and each of the attractions.
  // The reward points for visiting each Attraction.
  //    Note: Attraction reward points can be gathered from RewardsCentral
  private VisitedLocation visitedLocation;
  private List<AttractionModel> attractionModelList;

  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  public static class AttractionModel {

    private String name;
    private Location location;
    private Double distance;
    private int rewardPoint;
  }
}
