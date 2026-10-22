package com.openclassrooms.tourguide.assembler;

import com.openclassrooms.tourguide.model.NearAttractionModel;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NearAttractionAssembler {

  private RewardsService rewardsService;

  /**
   * Build response to client
   * @param visitedLocation VisitedLocation
   * @param attractionList List<Attraction>
   * @param user User
   * @return NearAttractionModel
   */
  public NearAttractionModel toModelNearAttraction(
    VisitedLocation visitedLocation,
    List<Attraction> attractionList,
    User user
  ) {
    List<NearAttractionModel.AttractionModel> attractionModelList = new ArrayList<>();
    for (Attraction attraction : attractionList) {
      attractionModelList.add(
        NearAttractionModel.AttractionModel
          .builder()
          .name(attraction.attractionName)
          .location(new Location(attraction.latitude, attraction.longitude))
          .distance(
            rewardsService.getDistance(
              visitedLocation.location,
              new Location(attraction.latitude, attraction.longitude)
            )
          )
          .rewardPoint(rewardsService.getRewardPoints(attraction, user))
          .build()
      );
    }

    return NearAttractionModel
      .builder()
      .visitedLocation(visitedLocation)
      .attractionModelList(attractionModelList)
      .build();
  }
}
