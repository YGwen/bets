package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class BetStats {
    private final AllBetsCommand command;

    BetStats(AllBetsCommand command) {
        this.command = command;
    }
//
//    public List<Data> sumOfPredictions() {
//        Comparator<Data> score = Comparator.comparing(Data::getValue, Comparator.reverseOrder());
//        Comparator<Data> scoreAndLabel = score.thenComparing(Data::getLabel, Comparator.reverseOrder());
//
//        BiFunction<Integer, Integer, String> toLabel = (a, b) -> String.format("%s:%s", a, b);
//
//
//        Match match = command.getMatch();
//        String matchResult = match.hasResultSet() ? toLabel.apply(match.getGoalsTeamOne(), match.getGoalsTeamTwo()) : "";
//
//        return command.getAllBetsForMatch().stream()
//                .filter(b -> b.getGoalsTeamOne() != null && b.getGoalsTeamTwo() != null)
//                .map(b -> toLabel.apply(b.getGoalsTeamOne(), b.getGoalsTeamTwo()))
//                .map(label -> new Label(label, label.equals(matchResult)))
//                .collect(groupingBy(Function.identity(), Collectors.counting()))
//                .entrySet().stream()
//                .map(en -> new Data(en.getKey(), en.getValue()))
//                .sorted(scoreAndLabel)
//                .collect(Collectors.toList());
//    }

    public List<Data> sumOfWinners() {
        Comparator<Data> score = Comparator.comparing(Data::getValue, Comparator.reverseOrder());
        Comparator<Data> scoreAndLabel = score.thenComparing(Data::getLabel, Comparator.reverseOrder());

        Country w = command.getMatch().getWinner();
        boolean undecidedResult = command.getMatch().isUndecidedResult();

        Function<Bet, Label> winner = bet ->
            bet.isTeamOneWinner() ? new Label(command.getTeamNameOne(), bet.getMatch().getCountryOne().equals(w))
            : bet.isTeamTwoWinner() ? new Label(command.getTeamNameTwo(), bet.getMatch().getCountryTwo().equals(w))
            : new Label(command.getNullMatchName(), undecidedResult);

        BigDecimal numOfBets = BigDecimal.valueOf(command.getAllBetsForMatch().size());

        return command.getAllBetsForMatch().stream()
                .filter(b -> b.getGoalsTeamOne() != null && b.getGoalsTeamTwo() != null)
                .map(winner)
                .collect(groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(en -> new Data(en.getKey(), BigDecimal.valueOf(en.getValue()).divide(numOfBets, 2, RoundingMode.HALF_EVEN)))
                .sorted(scoreAndLabel)
                .collect(Collectors.toList());
    }

    public static final class Label {
        private final String label;
        private final boolean highlight;
        public Label(String label, boolean highlight) {
            this.label = label;
            this.highlight = highlight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Label label1 = (Label) o;
            return highlight == label1.highlight &&
                    Objects.equals(label, label1.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, highlight);
        }
    }

    public static final class Data {
        private final Label label;
        private final BigDecimal value;

        public Data(Label label, BigDecimal value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label.label;
        }

        public boolean isHighlight() {
            return label.highlight;
        }

        public BigDecimal getValue() {
            return value;
        }
    }

}
