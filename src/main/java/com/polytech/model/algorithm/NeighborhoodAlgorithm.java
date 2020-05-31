package com.polytech.model.algorithm;

import com.polytech.model.Route;
import com.polytech.model.Solution;
import com.polytech.model.Stop;

import java.util.ArrayList;
import java.util.List;

public abstract class NeighborhoodAlgorithm extends CVRPAlgorithm {

    /**
     * Generates neighbors from three elementary transformations available in {@code Solution} : {@code swapTwoStops()},
     * {@code addStopToExistingRoute()} and {@code mergeTwoRoutes()}
     *
     * @param solution the solution from which we will generate the neighbors
     * @return the neighbors in list of solution
     */
    protected static List<Solution> getNeighbors(Solution solution, List<Stop> stopList) {

        List<Solution> neighbors = new ArrayList<>();

        for (Stop stop : stopList) {
            for (Route route : solution.getRouteList()) {

                // We add stop to an existing route
                if (!route.containsStop(stop)) {
                    Solution newSolution = new Solution(solution);
                    if (newSolution.addStopToExistingRoute(stop, route)) {
                        neighbors.add(newSolution);
                    }
                }

                for (Stop stop1 : route.getStopList()) {

                    // We swap stops
                    if (!stop.equals(stop1)) {
                        Solution newSolution1 = new Solution(solution);
                        if (newSolution1.swapTwoStops(stop, stop1)) {
                            neighbors.add(newSolution1);
                        }
                    }
                }
            }
        }

        // We merge two routes
        for (Route route : solution.getRouteList()) {
            for (Route route1 : solution.getRouteList()) {
                if (!route.equals(route1)) {
                    Solution newSolution = new Solution(solution);
                    if (newSolution.mergeTwoRoutes(route, route1)) {
                        neighbors.add(newSolution);
                    }
                }
            }
        }

        return neighbors;
    }
}
