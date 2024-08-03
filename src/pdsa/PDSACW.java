import java.util.*;

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Graph graph = new Graph();

        // Example edges
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 2);
        graph.addEdge("B", "C", 1);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 8);
        graph.addEdge("C", "E", 10);
        graph.addEdge("D", "E", 2);
        graph.addEdge("D", "Z", 6);
        graph.addEdge("E", "Z", 3);

        // Adding places of interest
        graph.addPlaceOfInterest("A", "Museum A");
        graph.addPlaceOfInterest("B", "Park B");
        graph.addPlaceOfInterest("C", "Cafe C");
        graph.addPlaceOfInterest("D", "Library D");
        graph.addPlaceOfInterest("E", "Gallery E");
        graph.addPlaceOfInterest("Z", "Monument Z");

        // Display all places of interest
        System.out.println("Places of interest:");
        for (Map.Entry<String, List<String>> entry : graph.placesOfInterest.entrySet()) {
            System.out.println(entry.getKey() + ": " + String.join(", ", entry.getValue()));
        }

        System.out.print("Enter source city: ");
        String source = scanner.nextLine();

        System.out.print("Enter destination city: ");
        String destination = scanner.nextLine();

        System.out.println("Enter places of interest to visit (separated by commas): ");
        String[] interests = scanner.nextLine().split(",");
        List<String> interestPoints = new ArrayList<>();
        for (String interest : interests) {
            interestPoints.add(interest.trim());
        }

        // Find all paths
        List<PathWithDistance> allPaths = graph.findAllPaths(source, destination);

        if (!allPaths.isEmpty()) {
            System.out.println("All possible paths from " + source + " to " + destination + " are:");
            for (int i = 0; i < allPaths.size(); i++) {
                PathWithDistance pathWithDistance = allPaths.get(i);
                System.out.println((i + 1) + ": " + String.join(" -> ", pathWithDistance.path) + " (Distance: " + pathWithDistance.distance + ")");
            }

            // Find the shortest path
            PathWithDistance shortestPath = graph.shortestPath(source, destination);
            System.out.println("Shortest path from " + source + " to " + destination + " is: " + String.join(" -> ", shortestPath.path) + " (Distance: " + shortestPath.distance + ")");

            // Find the path through selected places of interest
            PathWithDistance pathWithInterestPoints = graph.pathThroughInterestPoints(source, interestPoints, destination);
            if (!pathWithInterestPoints.path.isEmpty()) {
                System.out.println("Path from " + source + " to " + destination + " via places of interest is: " + String.join(" -> ", pathWithInterestPoints.path) + " (Distance: " + pathWithInterestPoints.distance + ")");
            } else {
                System.out.println("No path found through the specified places of interest.");
            }

            // Let the user select a path
            System.out.print("Select a path (1-" + allPaths.size() + "): ");
            int selectedIndex = scanner.nextInt() - 1;

            if (selectedIndex >= 0 && selectedIndex < allPaths.size()) {
                PathWithDistance selectedPath = allPaths.get(selectedIndex);
                System.out.println("You have selected the path: " + String.join(" -> ", selectedPath.path));
                System.out.println("Places you can visit along this path:");
                for (String node : selectedPath.path) {
                    System.out.println(node + ": " + String.join(", ", graph.placesOfInterest.getOrDefault(node, Collections.emptyList())));
                }
            } else {
                System.out.println("Invalid selection.");
            }

        } else {
            System.out.println("No path found from " + source + " to " + destination);
        }
    }
}