package com.example.logic.service;

import com.example.logic.model.entity.NameBasic;
import com.example.logic.model.repository.NameBasicRepository;
import com.example.logic.model.repository.TitlePrincipalRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
class SearchNode {
    private HashSet<String> movieIds;
    private HashSet<String> principalIds;
    private Optional<Integer> firstMovieYear;

    public static SearchNode ofActor(final NameBasicRepository nameBasicRepository, final String actorName, final Optional<Integer> aliveSinceYear) {
        final List<NameBasic> actorsByName = nameBasicRepository.findByPrimarynameAndAliveSince(actorName, aliveSinceYear.orElse(Integer.MIN_VALUE));

        final HashSet<String> endNodeMovieIds = new HashSet<>();
        final HashSet<String> endNodePrincipalIds = new HashSet<>();

        Optional<Integer> firstMovieYear = Optional.empty();

        for (final NameBasic n : actorsByName) {
            endNodeMovieIds.addAll(MovieService.splitImdbString(n.getKnownfortitles()));
            endNodePrincipalIds.add(n.getNconst());

            if (n.getBirthyear() != null) {
                if (firstMovieYear.isEmpty()) {
                    firstMovieYear = Optional.of(n.getBirthyear());
                } else {
                    firstMovieYear = firstMovieYear.map(y -> Math.min(y, n.getBirthyear()));
                }
            }
        }

        return new SearchNode(endNodeMovieIds, endNodePrincipalIds, firstMovieYear);
    }

    public static SearchNode ofActor(final NameBasicRepository nameBasicRepository, final String actorName) {
        return ofActor(nameBasicRepository, actorName, Optional.empty());
    }

    public boolean isEmpty() {
        return movieIds.isEmpty();
    }

    public boolean intersects(final SearchNode node) {
        return movieIds.stream().anyMatch(node.movieIds::contains);
    }

    public void moveNextLevel(final TitlePrincipalRepository titlePrincipalRepository) {
        if (firstMovieYear.isPresent()) {
            titlePrincipalRepository.getMoviesWhereIdsWereActorsAndSinceYear(principalIds, firstMovieYear.get()).forEach(v -> {
                movieIds.add((String) v.get(0));
            });
        } else {
            titlePrincipalRepository.getMoviesWhereIdsWereActors(principalIds).forEach(v -> {
                movieIds.add((String) v.get(0));
            });
        }

        principalIds.clear();

        if (firstMovieYear.isPresent()) {
            principalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIdsAndDeadAfterYear(movieIds, firstMovieYear.get()));
        } else {
            principalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIds(movieIds));
        }
    }
}