import java.util.*;

class Graph {
    final Map<String, List<Edge>> adjacencyList;
    final Map<String, List<String>> placesOfInterest;

    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.placesOfInterest = new HashMap<>();
    }

    public void addEdge(String source, String destination, int weight) {
        this.adjacencyList.putIfAbsent(source, new ArrayList<>());
        this.adjacencyList.putIfAbsent(destination, new ArrayList<>());
        this.adjacencyList.get(source).add(new Edge(destination, weight));
        this.adjacencyList.get(destination).add(new Edge(source, weight));
    }

    public void addPlaceOfInterest(String node, String place) {
        this.placesOfInterest.putIfAbsent(node, new ArrayList<>());
        this.placesOfInterest.get(node).add(place);
    }

    public List<PathWithDistance> findAllPaths(String start, String end) {
        List<PathWithDistance> paths = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        findPathsDfs(start, end, visited, currentPath, paths, 0);
        return paths;
    }

    private void findPathsDfs(String current, String end, Set<String> visited, List<String> currentPath, List<PathWithDistance> paths, int currentDistance) {
        visited.add(current);
        currentPath.add(current);

        if (current.equals(end)) {
            paths.add(new PathWithDistance(new ArrayList<>(currentPath), currentDistance));
        } else {
            for (Edge edge : adjacencyList.get(current)) {
                if (!visited.contains(edge.destination)) {
                    findPathsDfs(edge.destination, end, visited, currentPath, paths, currentDistance + edge.weight);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    public PathWithDistance shortestPath(String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));

        for (String vertex : adjacencyList.keySet()) {
            if (vertex.equals(start)) {
                distances.put(vertex, 0);
                priorityQueue.add(new Node(vertex, 0));
            } else {
                distances.put(vertex, Integer.MAX_VALUE);
                priorityQueue.add(new Node(vertex, Integer.MAX_VALUE));
            }
            previousNodes.put(vertex, null);
        }

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            String currentVertex = currentNode.vertex;

            if (currentVertex.equals(end)) {
                List<String> path = new ArrayList<>();
                int totalDistance = distances.get(currentVertex);
                while (previousNodes.get(currentVertex) != null) {
                    path.add(currentVertex);
                    currentVertex = previousNodes.get(currentVertex);
                }
                path.add(start);
                Collections.reverse(path);
                return new PathWithDistance(path, totalDistance);
            }

            for (Edge edge : adjacencyList.get(currentVertex)) {
                String neighbor = edge.destination;
                int newDist = distances.get(currentVertex) + edge.weight;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, currentVertex);
                    priorityQueue.add(new Node(neighbor, newDist));
                }
            }
        }

        return new PathWithDistance(Collections.emptyList(), 0); // Return empty path with 0 distance if no path is found
    }

    public PathWithDistance pathThroughInterestPoints(String start, List<String> interestPoints, String end) {
        List<String> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        List<PathWithDistance> validPaths = new ArrayList<>();

        currentPath.add(start);
        visited.add(start);

        findPathsThroughInterestsDfs(start, interestPoints, end, visited, currentPath, validPaths, 0, 0);

        // Return the first valid path found (not necessarily the shortest)
        if (!validPaths.isEmpty()) {
            return validPaths.get(0);
        }
        return new PathWithDistance(Collections.emptyList(), 0);
    }

    private void findPathsThroughInterestsDfs(String current, List<String> interestPoints, String end, Set<String> visited, List<String> currentPath, List<PathWithDistance> validPaths, int currentDistance, int interestIndex) {
        if (interestIndex < interestPoints.size() && current.equals(interestPoints.get(interestIndex))) {
            interestIndex++;
        }
        if (current.equals(end) && interestIndex == interestPoints.size()) {
            validPaths.add(new PathWithDistance(new ArrayList<>(currentPath), currentDistance));
            return;
        }
        for (Edge edge : adjacencyList.get(current)) {
            if (!visited.contains(edge.destination)) {
                visited.add(edge.destination);
                currentPath.add(edge.destination);
                findPathsThroughInterestsDfs(edge.destination, interestPoints, end, visited, currentPath, validPaths, currentDistance + edge.weight, interestIndex);
                currentPath.remove(currentPath.size() - 1);
                visited.remove(edge.destination);
            }
        }
    }

    public List<PathWithDistance> findPathsMaxDistance(String start, String end, int maxDistance) {
        List<PathWithDistance> paths = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        findPathsMaxDistanceDfs(start, end, visited, currentPath, paths, 0, maxDistance);
        return paths;
    }

    private void findPathsMaxDistanceDfs(String current, String end, Set<String> visited, List<String> currentPath, List<PathWithDistance> paths, int currentDistance, int maxDistance) {
        visited.add(current);
        currentPath.add(current);

        if (current.equals(end) && currentDistance <= maxDistance) {
            paths.add(new PathWithDistance(new ArrayList<>(currentPath), currentDistance));
        } else {
            for (Edge edge : adjacencyList.get(current)) {
                if (!visited.contains(edge.destination) && currentDistance + edge.weight <= maxDistance) {
                    findPathsMaxDistanceDfs(edge.destination, end, visited, currentPath, paths, currentDistance + edge.weight, maxDistance);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }

    public PathWithStops findPathWithMinStops(String start, String end) {
    List<PathWithStops> allPaths = new ArrayList<>();
    List<String> currentPath = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    findPathsMinStopsDfs(start, end, visited, currentPath, allPaths, 0);

    if (allPaths.isEmpty()) {
        return new PathWithStops(Collections.emptyList(), Integer.MAX_VALUE); // No path found
    }

    // Find the path with the minimum number of stops
    PathWithStops minStopsPath = allPaths.get(0);
    for (PathWithStops path : allPaths) {
        if (path.stops < minStopsPath.stops) {
            minStopsPath = path;
        }
    }

    return minStopsPath;
}

private void findPathsMinStopsDfs(String current, String end, Set<String> visited, List<String> currentPath, List<PathWithStops> paths, int stops) {
    visited.add(current);
    currentPath.add(current);

    if (current.equals(end)) {
        paths.add(new PathWithStops(new ArrayList<>(currentPath), stops));
    } else {
        for (Edge edge : adjacencyList.get(current)) {
            if (!visited.contains(edge.destination)) {
                findPathsMinStopsDfs(edge.destination, end, visited, currentPath, paths, stops + 1);
            }
        }
    }

    currentPath.remove(currentPath.size() - 1);
    visited.remove(current);
}

    public PathWithDistance pathAvoidingNodes(String start, String end, Set<String> nodesToAvoid) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));

        for (String vertex : adjacencyList.keySet()) {
            if (nodesToAvoid.contains(vertex)) continue;

            if (vertex.equals(start)) {
                distances.put(vertex, 0);
                priorityQueue.add(new Node(vertex, 0));
            } else {
                distances.put(vertex, Integer.MAX_VALUE);
                priorityQueue.add(new Node(vertex, Integer.MAX_VALUE));
            }
            previousNodes.put(vertex, null);
        }

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            String currentVertex = currentNode.vertex;

            if (currentVertex.equals(end)) {
                List<String> path = new ArrayList<>();
                int totalDistance = distances.get(currentVertex);
                while (previousNodes.get(currentVertex) != null) {
                    path.add(currentVertex);
                    currentVertex = previousNodes.get(currentVertex);
                }
                path.add(start);
                Collections.reverse(path);
                return new PathWithDistance(path, totalDistance);
            }

            for (Edge edge : adjacencyList.get(currentVertex)) {
                String neighbor = edge.destination;
                if (nodesToAvoid.contains(neighbor)) continue;

                int newDist = distances.get(currentVertex) + edge.weight;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, currentVertex);
                    priorityQueue.add(new Node(neighbor, newDist));
                }
            }
        }

        return new PathWithDistance(Collections.emptyList(), 0); // Return empty path with 0 distance if no path is found
    }

    void updateEdgeWeight(String source, String destination, int weight) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    void removeNode(String node) {
    throw new UnsupportedOperationException("Not supported yet."); 
}

static class Node {
    String vertex;
    int distance;

    Node(String vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
    }
}

static class Edge {
    String destination;
    int weight;

    Edge(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }
}

static class PathWithDistance {
    List<String> path;
    int distance;

    PathWithDistance(List<String> path, int distance) {
        this.path = path;
        this.distance = distance;
    }
}

static class PathWithStops {
    List<String> path;
    int stops;

    PathWithStops(List<String> path, int stops) {
        this.path = path;
        this.stops = stops;
    }
}

public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    Graph graph = new Graph();

    // Example edges
    graph.addEdge("Negombo", "Marawila", 4);
    graph.addEdge("Negombo", "Kuliyapitiya", 2);
    graph.addEdge("Marawila", "Kuliyapitiya", 1);
    graph.addEdge("Marawila", "Kurunagala", 5);
    graph.addEdge("Kuliyapitiya", "Kurunagala", 8);
    graph.addEdge("Kuliyapitiya", "Galagedara", 10);
    graph.addEdge("Kurunagala", "Galagedara", 2);
    graph.addEdge("Kurunagala", "Kandy", 6);
    graph.addEdge("Galagedara", "Kandy", 3);

}