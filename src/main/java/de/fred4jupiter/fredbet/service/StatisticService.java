package de.fred4jupiter.fredbet.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Statistic;
import de.fred4jupiter.fredbet.repository.CustomBetQuestionRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.StatisticRepository;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

@Service
public class StatisticService {

	private final StatisticRepository statisticRepository;

	private final CustomBetQuestionRepository customBetQuestionRepository;

	private final ExtraBetRepository extraBetRepository;

	private final RuntimeConfigurationService runtimeConfigurationService;

	@Autowired
	public StatisticService(StatisticRepository statisticRepository, RuntimeConfigurationService runtimeConfigurationService,
			ExtraBetRepository extraBetRepository, CustomBetQuestionRepository customBetQuestionRepository) {
		this.statisticRepository = statisticRepository;
		this.runtimeConfigurationService = runtimeConfigurationService;
		this.extraBetRepository = extraBetRepository;
		this.customBetQuestionRepository = customBetQuestionRepository;
	}

	public Country getFavouriteCountry() {
		return runtimeConfigurationService.loadRuntimeConfig().getFavouriteCountry();
	}

	public List<Statistic> createStatistic() {
		final List<Statistic> statisticList = statisticRepository.createStatistic();

		final Country favouriteCountry = getFavouriteCountry();
		final Map<String, Integer> favoriteCountryPointsPerUserMap = statisticRepository
				.sumPointsPerUserForFavoriteCountry(favouriteCountry);
		final Optional<Integer> maxFavoriteCountryPoints = favoriteCountryPointsPerUserMap.values().stream()
				.max(Comparator.comparing(i -> i));

		final Map<String, Integer> extraBetsPerUser = findExtraBetsPerUser();

		final Triple<Integer, Integer, Integer> triple = calculateMinMax(statisticList);
		final int minPoints = triple.getLeft();
		final int maxPoints = triple.getMiddle();
		final int maxGroupPoints = triple.getRight();

		for (Statistic statistic : statisticList) {
			statistic.setFavoriteCountry(favouriteCountry);

			final Integer favoriteCountryPoints = favoriteCountryPointsPerUserMap.get(statistic.getUsername());
			if (favoriteCountryPoints != null) {
				statistic.setPointsFavoriteCountry(favoriteCountryPoints);
				if (maxFavoriteCountryPoints.isPresent() && favoriteCountryPoints.equals(maxFavoriteCountryPoints.get())) {
					statistic.setMaxFavoriteCountryCandidate(true);
				}
			}

			statistic.setPointsForExtraBets(extraBetsPerUser.get(statistic.getUsername()));

			if (statistic.getSum().intValue() == minPoints) {
				statistic.setMinPointsCandidate(true);
			}
			if (statistic.getSum().intValue() == maxPoints) {
				statistic.setMaxPointsCandidate(true);
			}
			if (statistic.getPointsGroup().intValue() == maxGroupPoints) {
				statistic.setMaxGroupPointsCandidate(true);
			}
		}

		return statisticList;
	}

	private Triple<Integer, Integer, Integer> calculateMinMax(List<Statistic> statisticList) {
		int minPoints = Integer.MAX_VALUE;
		int maxPoints = 0;
		int maxGroupPoints = 0;

		for (Statistic statistic : statisticList) {
			if (statistic.getSum().intValue() < minPoints) {
				minPoints = statistic.getSum().intValue();
			}
			if (statistic.getSum().intValue() > maxPoints) {
				maxPoints = statistic.getSum().intValue();
			}

			if (statistic.getPointsGroup().intValue() > maxGroupPoints) {
				maxGroupPoints = statistic.getPointsGroup().intValue();
			}
		}

		return Triple.of(minPoints, maxPoints, maxGroupPoints);
	}

	private Map<String, Integer> findExtraBetsPerUser() {
		List<ExtraBet> extraBets = extraBetRepository.findAll();
		Map<String, Integer> userExtraBetPoints = new HashMap<>();

		extraBets.forEach(extraBet -> userExtraBetPoints.put(extraBet.getUserName(), extraBet.getPoints()));

		List<Object[]> customBets = customBetQuestionRepository.getBonusPoints();

		customBets.forEach(customBet -> {
				String username = (String) customBet[0];
				Integer currentPoints = userExtraBetPoints.getOrDefault(username, 0);
				userExtraBetPoints.put(username, currentPoints + ((BigDecimal) customBet[1]).intValue());
		});
		return userExtraBetPoints;
	}
}
