package com.example.logic.service;

import com.example.logic.exceptions.MovieNotFound;
import com.example.logic.model.dto.CrewMember;
import com.example.logic.model.dto.MovieDto;
import com.example.logic.model.dto.RatedDto;
import com.example.logic.model.entity.NameBasic;
import com.example.logic.model.entity.TitleBasic;
import com.example.logic.model.repository.NameBasicRepository;
import com.example.logic.model.repository.TitleBasicRepoImpl;
import com.example.logic.model.repository.TitleBasicRepository;
import com.example.logic.model.repository.TitlePrincipalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.util.Pair.toMap;

@Slf4j
@AllArgsConstructor
@Service
public class MovieService {
    private static final String KEVIN_BACON_NAME = "Kevin Bacon";
    private TitleBasicRepository repository;
    private NameBasicRepository nameBasicRepository;
    private TitlePrincipalRepository titlePrincipalRepository;
    private TitleBasicRepoImpl impl;

    public Integer findDegreeOfSeparation(final String actorName) {
        if (isKevinBacon(actorName)) return null;

        // If you want to test the complexity of the mentioned algorithms, uncomment
        // the corresponding algorithm invocation.
        //
        // The tests show, that the:
        // 1. BFS can't reach level 3, or it takes too long on many cases.
        // 2. Bidirectional BFS reaches level 3 and sometimes 4, but may take long time.
        // 3. Smart BFS takes usually five times less time than bidirectional BFS and traverses
        //    much fewer nodes due to the filtering.
        //
        // A better approach might be to have a stored procedure in PostgreSQL that takes the required
        // data at once, but one might always think about the trade-offs and decide carefully what is better,
        // when and why, considering all the conditions possible.

//        return bfs(actorName);
//        return bbfs(actorName);
//        return smartBbfs(actorName);
        return refactoredSmartBbfs(actorName);
    }

    /**
     * Smart bidirectional breadth-first search.
     * <p>
     * It is smart in a sense that it attempts to consider several facts in its decision-making, when deciding which
     * paths to take of the graph. This is a heuristic-like approach, but since we can't know how far we are from the
     * target node, we can't provide a heuristic function, but we can (at least it is probabilistically better) remove
     * the paths we certainly don't have to traverse and expand. In some cases, it can greatly reduce the complexity,
     * but it depends on the problem and the data set.
     * <p>
     * This search function searches for:
     * <p>
     * 1. Only the movies which were released in the period of lifetime of Kevin Bacon. The movies that had been made
     * before he was born or after his death (he is alive, so we don't filter by the death date) are filtered out,
     * reducing the number of graph nodes we need to traverse and possibly expand later.
     * 2. Only the people who were alive at the same time as Kevin Bacon: the people who had died before he was born are
     * filtered out. We don't have the death date for Kevin Bacon, so we can't filter by it.
     * <p>
     * @param actorName The user's desired actor to start the search from.
     * @return [null] if the path doesn't exist, [0] if the actor chosen by the user is "Kevin Bacon" or
     * user to work on the same movie together directly, [1] or higher depending on the degree if the user
     * actor hasn't been working directly with Kevin Bacon but with someone else instead, who, in turn, used
     * to work with Kevin Bacon directly.
     */
    private Integer refactoredSmartBbfs(final String actorName) {
        final SearchNode endNode = SearchNode.ofActor(nameBasicRepository, KEVIN_BACON_NAME);
        final SearchNode startNode = SearchNode.ofActor(nameBasicRepository, actorName, endNode.getFirstMovieYear());

        int degree = 0;

        while (!startNode.isEmpty() && !endNode.isEmpty()) {
            // A step towards Kevin Bacon.
            if (startNode.intersects(endNode)) {
                return degree;
            }

            startNode.moveNextLevel(titlePrincipalRepository);

            ++degree;

            if (endNode.intersects(startNode)) {
                return degree;
            }

            // A step from Kevin Bacon towards the target actor.
            endNode.moveNextLevel(titlePrincipalRepository);

            ++degree;
        }

        return null;
    }

    /**
     * Smart bidirectional breadth-first search.
     * <p>
     * It is smart in a sense that it attempts to consider several facts in its decision-making, when deciding which
     * paths to take of the graph. This is a heuristic-like approach, but since we can't know how far we are from the
     * target node, we can't provide a heuristic function, but we can (at least it is probabilistically better) remove
     * the paths we certainly don't have to traverse and expand. In some cases, it can greatly reduce the complexity,
     * but it depends on the problem and the data set.
     * <p>
     * This search function searches for:
     * <p>
     * 1. Only the movies which were released in the period of lifetime of Kevin Bacon. The movies that had been made
     * before he was born or after his death (he is alive, so we don't filter by the death date) are filtered out,
     * reducing the number of graph nodes we need to traverse and possibly expand later.
     * 2. Only the people who were alive at the same time as Kevin Bacon: the people who had died before he was born are
     * filtered out. We don't have the death date for Kevin Bacon, so we can't filter by it.
     * <p>
     * @param actorName The user's desired actor to start the search from.
     * @return [null] if the path doesn't exist, [0] if the actor chosen by the user is "Kevin Bacon" or
     * user to work on the same movie together directly, [1] or higher depending on the degree if the user
     * actor hasn't been working directly with Kevin Bacon but with someone else instead, who, in turn, used
     * to work with Kevin Bacon directly.
     */
    private Integer smartBbfs(final String actorName) {
        final List<NameBasic> kevinBacons = nameBasicRepository.findByPrimaryname(KEVIN_BACON_NAME);

        final HashSet<String> endNodeMovieIds = new HashSet<>();
        final HashSet<String> endNodePrincipalIds = new HashSet<>();

        Optional<Integer> firstMovieYear = Optional.empty();

        for (final NameBasic n : kevinBacons) {
            endNodeMovieIds.addAll(splitImdbString(n.getKnownfortitles()));
            endNodePrincipalIds.add(n.getNconst());

            if (n.getBirthyear() != null) {
                if (firstMovieYear.isEmpty()) {
                    firstMovieYear = Optional.of(n.getBirthyear());
                } else {
                    firstMovieYear = firstMovieYear.map(y -> Math.min(y, n.getBirthyear()));
                }
            }
        }

        final Optional<Integer> firstMovieYearFinal = firstMovieYear;

        final List<NameBasic> actorsWithUserName = nameBasicRepository
                .findByPrimarynameAndAliveSince(actorName, firstMovieYearFinal.orElse(Integer.MIN_VALUE));

        final HashSet<String> startNodeMovieIds = new HashSet<>();
        final HashSet<String> startNodePrincipalIds = new HashSet<>();

        actorsWithUserName.forEach(n -> {
            startNodeMovieIds.addAll(splitImdbString(n.getKnownfortitles()));
            startNodePrincipalIds.add(n.getNconst());
        });

        int degree = 0;

        while (!startNodeMovieIds.isEmpty() && !endNodeMovieIds.isEmpty()) {
            // A step towards Kevin Bacon
            if (startNodeMovieIds.stream().anyMatch(endNodeMovieIds::contains)) {
                return degree;
            }

            startNodeMovieIds.clear();

            if (firstMovieYearFinal.isPresent()) {
                titlePrincipalRepository.getMoviesWhereIdsWereActorsAndSinceYear(startNodePrincipalIds, firstMovieYearFinal.get()).forEach(v -> {
                    startNodeMovieIds.add((String) v.get(0));
                });
            } else {
                titlePrincipalRepository.getMoviesWhereIdsWereActors(startNodePrincipalIds).forEach(v -> {
                    startNodeMovieIds.add((String) v.get(0));
                });
            }

            startNodePrincipalIds.clear();

            if (firstMovieYearFinal.isPresent()) {
                startNodePrincipalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIdsAndDeadAfterYear(startNodeMovieIds, firstMovieYearFinal.get()));
            } else {
                startNodePrincipalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIds(startNodeMovieIds));
            }

            ++degree;

            // A step towards the target actor, from Kevin Bacon
            if (endNodeMovieIds.stream().anyMatch(startNodeMovieIds::contains)) {
                return degree;
            }

            endNodeMovieIds.clear();

            if (firstMovieYearFinal.isPresent()) {
                titlePrincipalRepository.getMoviesWhereIdsWereActorsAndSinceYear(endNodePrincipalIds, firstMovieYearFinal.get()).forEach(v -> {
                    endNodeMovieIds.add((String) v.get(0));
                });
            } else {
                titlePrincipalRepository.getMoviesWhereIdsWereActors(endNodePrincipalIds).forEach(v -> {
                    endNodeMovieIds.add((String) v.get(0));
                });
            }

            endNodePrincipalIds.clear();

            if (firstMovieYearFinal.isPresent()) {
                endNodePrincipalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIdsAndDeadAfterYear(endNodeMovieIds, firstMovieYearFinal.get()));
            } else {
                endNodePrincipalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIds(endNodeMovieIds));
            }

            ++degree;
        }

        return null;
    }

    private static boolean isKevinBacon(String actorName) {
        return actorName == null || actorName.isBlank() || actorName.trim().equalsIgnoreCase(KEVIN_BACON_NAME);
    }

    /**
     * Bidirectional Breadth-First Search from the actor named by the user to "Kevin Bacon".
     * @param actorName The user's desired actor to start the search from.
     * @return [null] if the path doesn't exist, [0] if the actor chosen by the user is "Kevin Bacon" or
     * user to work on the same movie together directly, [1] or higher depending on the degree if the user
     * actor hasn't been working directly with Kevin Bacon but with someone else instead, who, in turn, used
     * to work with Kevin Bacon directly.
     */
    private Integer bbfs(final String actorName) {
        final List<NameBasic> kevinBacons = nameBasicRepository.findByPrimaryname(KEVIN_BACON_NAME);

        final HashSet<String> endNodeMovieIds = new HashSet<>();
        final HashSet<String> endNodePrincipalIds = new HashSet<>();

        kevinBacons.forEach(n -> {
            endNodeMovieIds.addAll(splitImdbString(n.getKnownfortitles()));
            endNodePrincipalIds.add(n.getNconst());
        });

        final List<NameBasic> actorsWithUserName = nameBasicRepository.findByPrimaryname(actorName);
        final HashSet<String> startNodeMovieIds = new HashSet<>();
        final HashSet<String> startNodePrincipalIds = new HashSet<>();

        actorsWithUserName.forEach(n -> {
            startNodeMovieIds.addAll(splitImdbString(n.getKnownfortitles()));
            startNodePrincipalIds.add(n.getNconst());
        });

        int degree = 0;

        while (!startNodeMovieIds.isEmpty() && !endNodeMovieIds.isEmpty()) {
            // A step towards Kevin Bacon
            if (startNodeMovieIds.stream().anyMatch(endNodeMovieIds::contains)) {
                return degree;
            }

            startNodeMovieIds.clear();

            titlePrincipalRepository.getMoviesWhereIdsWereActors(startNodePrincipalIds).forEach(v -> {
                startNodeMovieIds.add((String) v.get(0));
            });

            startNodePrincipalIds.clear();
            startNodePrincipalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIds(startNodeMovieIds));

            ++degree;

            // A step towards the target actor, from Kevin Bacon
            if (endNodeMovieIds.stream().anyMatch(startNodeMovieIds::contains)) {
                return degree;
            }

            endNodeMovieIds.clear();

            titlePrincipalRepository.getMoviesWhereIdsWereActors(endNodePrincipalIds).forEach(v -> {
                endNodeMovieIds.add((String) v.get(0));
            });

            endNodePrincipalIds.clear();
            endNodePrincipalIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIds(endNodeMovieIds));

            ++degree;
        }

        return null;
    }

    /**
     * Breadth-First Search from the user actor to Kevin Bacon.
     * @param actorName The actor name to start the search from.
     * @return [null] if the path doesn't exist, [0] if the actor chosen by the user is "Kevin Bacon" or
     * user to work on the same movie together directly, [1] or higher depending on the degree if the user
     * actor hasn't been working directly with Kevin Bacon but with someone else instead, who, in turn, used
     * to work with Kevin Bacon directly.
     */
    private Integer bfs(final String actorName) {
        final List<NameBasic> kevinBacons = nameBasicRepository.findByPrimaryname(KEVIN_BACON_NAME);

        final HashSet<String> kevinBaconsMovieIds = new HashSet<>();
        final HashSet<String> kevinBaconsPrincipalsIds = new HashSet<>();

        kevinBacons.forEach(n -> {
            kevinBaconsMovieIds.addAll(splitImdbString(n.getKnownfortitles()));
            kevinBaconsPrincipalsIds.add(n.getNconst());
        });

        final List<NameBasic> actorsWithUserName = nameBasicRepository.findByPrimaryname(actorName);
        final HashSet<String> movieIds = new HashSet<>();
        final HashSet<String> principalsIds = new HashSet<>();

        actorsWithUserName.forEach(n -> {
            movieIds.addAll(splitImdbString(n.getKnownfortitles()));
            principalsIds.add(n.getNconst());
        });

        int degree = 0;

        while (!movieIds.isEmpty()) {
            if (movieIds.stream().anyMatch(kevinBaconsMovieIds::contains)) {
                return degree;
            }

            movieIds.clear();

            titlePrincipalRepository.getMoviesWhereIdsWereActors(principalsIds).forEach(v -> {
                movieIds.add((String) v.get(0));
            });

            principalsIds.clear();
            principalsIds.addAll(titlePrincipalRepository.getActorsFromTheMoviesByIds(movieIds));

            ++degree;
        }

        return null;
    }


    public List<RatedDto> getTopRatedMovie(final String genre, final int offset, final int limit) {
        final List<TitleBasic> topRated = impl.getTopRated(genre, limit, offset);
        final List<RatedDto> result = new ArrayList<>();
        for (TitleBasic titleBasic : topRated) {
            RatedDto build = RatedDto.builder()
                    .originalTitle(titleBasic.getOriginaltitle())
                    .primaryTitle(titleBasic.getPrimarytitle())
                    .averagerating(titleBasic.getRating().getAveragerating())
                    .genres(titleBasic.getGenres())
                    .build();
            result.add(build);
        }

        return result;
    }

    public List<MovieDto> getMovieDto(final String movieName) {
        final List<TitleBasic> titleBasic = repository.findByNameWithCrewAndPrincipal(movieName);

        if (!titleBasic.isEmpty()) {
            final List<MovieDto> moviesWithCrew = titleBasic.stream()
                    .map(titleInfo -> {

                        final List<CrewMember> directors = splitImdbString(titleInfo.getCrew().getDirectors())
                                .stream()
                                .map(e -> new CrewMember("", "Director", null, e))
                                .toList();
                        final List<CrewMember> writers = splitImdbString(titleInfo.getCrew().getWriters())
                                .stream()
                                .map(e -> new CrewMember("", "Writer", null, e))
                                .toList();
                        final List<CrewMember> others = titleInfo.getPrincipals()
                                .stream()
                                .map(e -> new CrewMember("", e.getJob(), e.getCharacters(), e.getNconst()))
                                .toList();

                        List<CrewMember> crewMembers = new ArrayList<>();
                        crewMembers.addAll(directors);
                        crewMembers.addAll(writers);
                        crewMembers.addAll(others);

                        return MovieDto.builder()
                                .startYear(titleInfo.getStartyear())
                                .endYear(titleInfo.getEndyear())
                                .minutes(titleInfo.getRuntimeminutes())
                                .primaryTitle(titleInfo.getPrimarytitle())
                                .originalTitle(titleInfo.getOriginaltitle())
                                .genres(splitImdbString(titleInfo.getGenres()))
                                .crew(crewMembers)
                                .build();
                    }).toList();

            final var crewMemberIds = moviesWithCrew.stream().flatMap(movie -> movie.getCrew().stream()).map(CrewMember::getId).collect(Collectors.toSet());
            final Map<String, String> primaryNamesByIds = nameBasicRepository.findPrimaryNamesByIds(crewMemberIds).stream().map(v -> Pair.of((String) v.get(0), (String) v.get(1))).collect(toMap());

            moviesWithCrew.stream().flatMap(movie -> movie.getCrew().stream()).forEach(crewMember -> crewMember.setName(primaryNamesByIds.get(crewMember.getId())));

            return moviesWithCrew;
        } else {
            log.warn("Movie not found with title: " + movieName);
            throw new MovieNotFound("Movie with title " + movieName + " not found");
        }
    }


    private static List<String> splitString(final String string, final String delimeter) {
        if (string == null) {
            return List.of();
        }

        return Arrays.stream(string.split(delimeter)).map(String::trim).toList();
    }

    /**
     * Splits the string by commas.
     *
     * @param string The string to split.
     * @return A list of strings split by commas.
     */
    static List<String> splitImdbString(final String string) {
        return splitString(string, ",");
    }
}
