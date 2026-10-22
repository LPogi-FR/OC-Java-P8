package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;

class TestPerformance {

  /*
   * A note on performance improvements:
   *
   * The number of users generated for the high volume tests can be easily
   * adjusted via this method:
   *
   * InternalTestHelper.setInternalUserNumber(100000);
   *
   *
   * These tests can be modified to suit new solutions, just as long as the
   * performance metrics at the end of the tests remains consistent.
   *
   * These are performance metrics that we are trying to hit:
   *
   * highVolumeTrackLocation: 100,000 users within 15 minutes:
   * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
   * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   *
   * highVolumeGetRewards: 100,000 users within 20 minutes:
   * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
   * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   */

  //@Disabled
  @Test
  void highVolumeTrackLocation() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    // Users should be incremented up to 100,000, and test finishes within 15
    // minutes
    InternalTestHelper.setInternalUserNumber(100000);
    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

    List<User> allUsers;
    allUsers = tourGuideService.getAllUsers();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    for (User user : allUsers) {
      tourGuideService.trackUserLocation(user);
    }
    stopWatch.stop();
    tourGuideService.tracker.stopTracking();

    System.out.println(
      "highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."
    );
    assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

  //@Disabled
  @Test
  void highVolumeGetRewards() {
    GpsUtil gpsUtil = new GpsUtil();
    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

    /** Users should be incremented up to 100,000, and test finishes within 20 minutes*/
    InternalTestHelper.setInternalUserNumber(100000);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

    Attraction attraction = gpsUtil.getAttractions().getFirst();
    List<User> allUsers;
    allUsers = tourGuideService.getAllUsers();
    allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

    ExecutorService executor = Executors.newFixedThreadPool(200);
    /***
	// Création d'un pool de threads
	// Le nombre de threads est basé sur le nombre de cœurs CPU disponibles multiplié par 4
	// Cela permet d'exécuter plusieurs tâches en parallèle
	private final ExecutorService executorService =
			Executors.newFixedThreadPool(
					Runtime.getRuntime().availableProcessors() * 4
			);*/

    CompletableFuture
      .allOf(allUsers.stream().map(u -> rewardsService.calculateRewards(u, executor)).toArray(CompletableFuture[]::new))
      .join();
    executor.shutdown();

    // allUsers.forEach(u -> rewardsService.calculateRewards(u));

    for (User user : allUsers) {
      assertFalse(user.getUserRewards().isEmpty());
    }
    stopWatch.stop();
    tourGuideService.tracker.stopTracking();

    System.out.println(
      "highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."
    );
    assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }
}
