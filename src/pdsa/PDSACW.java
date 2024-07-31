import java.util.*;
//package pdsa;

class Graph {
    private final Map<String, List<Edge>> adjacencyList;
    private final Map<String, List<String>> placesOfInterest;

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



}
public class PDSACW {
    
}
