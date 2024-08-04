import java.util.*;

class Graph {
    final Map<String, List<Edge>> adjacencyList;    // Initialize adjacency list to store graph edges
    final Map<String, List<String>> placesOfInterest;    // Intialize map to store places of interest for each node

    // Constructor to initialize the adjacency list and map
    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.placesOfInterest = new HashMap<>();
    }

    // Method to add an edge between two nodes with a specified weight
    public void addEdge(String source, String destination, int weight) {
        this.adjacencyList.putIfAbsent(source, new ArrayList<>());
        this.adjacencyList.putIfAbsent(destination, new ArrayList<>());
        this.adjacencyList.get(source).add(new Edge(destination, weight));
        this.adjacencyList.get(destination).add(new Edge(source, weight));
    }

    // Method to add a place of interest to a specific node
    public void addPlaceOfInterest(String node, String place) {
        this.placesOfInterest.putIfAbsent(node, new ArrayList<>());
        this.placesOfInterest.get(node).add(place);
    }

    // Method to find all paths from start node to end node
    public List<PathWithDistance> findAllPaths(String start, String end) {
        List<PathWithDistance> paths = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        findPathsDfs(start, end, visited, currentPath, paths, 0);
        return paths;
    }

    // Method to perform DFS and find all paths
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

    // Method to find the shortest path from start node to end node
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

    // Method to find a path through interest points
    public PathWithDistance pathThroughInterestPoints(String start, List<String> interestPoints, String end) {
        List<String> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        List<PathWithDistance> validPaths = new ArrayList<>();

        currentPath.add(start);
        visited.add(start);

        findPathsThroughInterestsDfs(start, interestPoints, end, visited, currentPath, validPaths, 0, 0);

        // Return the first valid path found
        if (!validPaths.isEmpty()) {
            return validPaths.get(0);
        }
        return new PathWithDistance(Collections.emptyList(), 0);
    }

    // Method to perform DFS and find paths through specified places of interest
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

    // Method to find paths from start to end with a maximum distance
    public List<PathWithDistance> findPathsMaxDistance(String start, String end, int maxDistance) {
        List<PathWithDistance> paths = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        findPathsMaxDistanceDfs(start, end, visited, currentPath, paths, 0, maxDistance);
        return paths;
    }

    // Method to perform DFS and find paths with a maximum distance
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

    // Method to find the path with the minimum number of stops
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

// Method to perform DFS and find paths with minimum stops
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

    // Method to find a path avoiding specified nodes
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

// Class representing a node with a vertex and distance
static class Node {
    String vertex;
    int distance;

    Node(String vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
    }
}

// Class representing an edge with a destination node and weight
static class Edge {
    String destination;
    int weight;

    Edge(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }
}

// Class representing a path with a list of nodes and the total distance
static class PathWithDistance {
    List<String> path;
    int distance;

    PathWithDistance(List<String> path, int distance) {
        this.path = path;
        this.distance = distance;
    }
}


// Class representing a path with a list of nodes and the number of stops
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
    graph.addEdge("Negombo", "Marawila", 28);
    graph.addEdge("Negombo", "Kuliyapitiya", 47);
    graph.addEdge("Marawila", "Kuliyapitiya", 30);
    graph.addEdge("Marawila", "Kurunagala", 69);
    graph.addEdge("Kuliyapitiya", "Kurunagala", 39);
    graph.addEdge("Kuliyapitiya", "Galagedara", 77);
    graph.addEdge("Kurunagala", "Galagedara", 38);
    graph.addEdge("Kurunagala", "Kandy", 43);
    graph.addEdge("Galagedara", "Kandy", 20);

    // Adding places of interest
    graph.addPlaceOfInterest("Negombo", "         Lagoon");
    graph.addPlaceOfInterest("Marawila", "        Hotel Amagi");
    graph.addPlaceOfInterest("Kuliyapitiya", "    Ancient clock tower");
    graph.addPlaceOfInterest("Kurunagala", "      Kurunagala lake");
    graph.addPlaceOfInterest("Galagedara", "      RIVER BANK resort");
    graph.addPlaceOfInterest("Knady", "           Sri Dalada Maligawa");
    
    while (true) {
        System.out.println(" ");
        System.out.println("----------------WELCOME-----------------");
        System.out.println("*** Select the option of your choice ***");
        System.out.println("----------------------------------------");
        System.out.println("1. Display all places on map");
        System.out.println("2. Find all paths from source to destination");
        System.out.println("3. Find shortest path from source to destination");
        System.out.println("4. Find path through selected places of interest");
        System.out.println("5. Find paths with maximum distance");
        System.out.println("6. Find the path with minimum number of stops");
        System.out.println("7. Find path avoiding certain places(cities)");
        System.out.println("8. Exit");
        System.out.println("0. Help");
        System.out.println(" ");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.println("-----------------");

        switch (choice) {
            case 1:
                System.out.println("Places of interest:");
                System.out.println(" ");
                System.out.printf("%-10s %-20s\n", "  City", "      Places of Interest");
                for (Map.Entry<String, List<String>> entry : graph.placesOfInterest.entrySet()) {
                    System.out.println(entry.getKey() + ": " + String.join(", ", entry.getValue()));
                }
                System.out.println(" ");
                System.out.println("* End of place list *");
                break;
                
            case 2:
                System.out.print("Enter source city: ");
                String source = scanner.nextLine();
                System.out.print("Enter destination city: ");
                String destination = scanner.nextLine();
                List<PathWithDistance> allPaths = graph.findAllPaths(source, destination);
                if (!allPaths.isEmpty()) {
                    System.out.println(" ");
                    System.out.println("All possible paths from " + source + " to " + destination + " are:");
                    System.out.println("-----------------------------------------------");
                    for (int i = 0; i < allPaths.size(); i++) {
                        PathWithDistance pathWithDistance = allPaths.get(i);
                        System.out.println((i + 1) + ": " + String.join(" -> ", pathWithDistance.path) + " (Distance: " + pathWithDistance.distance + ")");
                    }
                } else {
                    System.out.println(" ");
                    System.out.println("! No path found from " + source + " to " + destination);
                }
                System.out.println(" ");
                System.out.println("* End of paths list *");
                break;

            case 3:
                System.out.print("Enter source city: ");
                source = scanner.nextLine();
                System.out.print("Enter destination city: ");
                destination = scanner.nextLine();
                PathWithDistance shortestPath = graph.shortestPath(source, destination);
                System.out.println(" ");
                System.out.println("Shortest path from " + source + " to " + destination + " is: " + String.join(" -> ", shortestPath.path) + " (Distance: " + shortestPath.distance + ")");
                System.out.println(" ");
                System.out.println("* End of option *");
                break;

            case 4:
                System.out.print("Enter source city: ");
                source = scanner.nextLine();
                System.out.print("Enter destination city: ");
                destination = scanner.nextLine();
                System.out.println("Enter places of interest to visit (separated by commas): ");
                String[] interests = scanner.nextLine().split(",");
                List<String> interestPoints = new ArrayList<>();
                for (String interest : interests) {
                    interestPoints.add(interest.trim());
                }
                PathWithDistance pathWithInterestPoints = graph.pathThroughInterestPoints(source, interestPoints, destination);
                if (!pathWithInterestPoints.path.isEmpty()) {
                    System.out.println(" ");
                    System.out.println("Path from " + source + " to " + destination + " via places of interest is: " + String.join(" -> ", pathWithInterestPoints.path) + " (Distance: " + pathWithInterestPoints.distance + ")");
                } else {
                    System.out.println("No path found through the specified places of interest.");
                }
                System.out.println(" ");
                System.out.println("* End of paths list through your places of interest *");
                break;

            case 5:
                System.out.print("Enter source city: ");
                source = scanner.nextLine();
                System.out.print("Enter destination city: ");
                destination = scanner.nextLine();
                System.out.print("Enter maximum distance: ");
                int maxDistance = scanner.nextInt();
                List<PathWithDistance> maxDistancePaths = graph.findPathsMaxDistance(source, destination, maxDistance);
                if (!maxDistancePaths.isEmpty()) {
                    System.out.println(" ");
                    System.out.println("Paths from " + source + " to " + destination + " with a maximum distance of " + maxDistance + " are:");
                    for (int i = 0; i < maxDistancePaths.size(); i++) {
                        PathWithDistance pathWithDistance = maxDistancePaths.get(i);
                        System.out.println((i + 1) + ": " + String.join(" -> ", pathWithDistance.path) + " (Distance: " + pathWithDistance.distance + ")");
                    }
                } else {
                    System.out.println("No paths found within the specified distance.");
                }
                System.out.println(" ");
                System.out.println("* End of option *");
                break;
                
            case 6:
                System.out.print("Enter source city: ");
                source = scanner.nextLine();
                System.out.print("Enter destination city: ");
                destination = scanner.nextLine();
                PathWithStops minStopsPath = graph.findPathWithMinStops(source, destination);
                if (minStopsPath.stops < Integer.MAX_VALUE) {
                    System.out.println(" ");
                    System.out.println("Path from " + source + " to " + destination + " with minimum number of stops is: " + String.join(" -> ", minStopsPath.path) + " (Stops: " + minStopsPath.stops + ")");
                } else {
                    System.out.println("No path found with minimum number of stops.");
                }
                System.out.println(" ");
                System.out.println("* End of option *");
                break;
        
            case 7:
                System.out.print("Enter source city: ");
                source = scanner.nextLine();
                System.out.print("Enter destination city: ");
                destination = scanner.nextLine();
                System.out.print("Enter cities to avoid (separated by commas): ");
                String[] avoidNodes = scanner.nextLine().split(",");
                Set<String> nodesToAvoid = new HashSet<>();
                for (String node : avoidNodes) {
                    nodesToAvoid.add(node.trim());
                }
                PathWithDistance pathAvoidingNodes = graph.pathAvoidingNodes(source, destination, nodesToAvoid);
                if (!pathAvoidingNodes.path.isEmpty()) {
                    System.out.println(" ");
                    System.out.println("Path from " + source + " to " + destination + " avoiding " + nodesToAvoid + " is: " + String.join(" -> ", pathAvoidingNodes.path) + " (Distance: " + pathAvoidingNodes.distance + ")");
                } else {
                    System.out.println("No path found avoiding the specified places(cities).");
                }
                System.out.println(" ");
                System.out.println("* End of paths list avoiding your specified locations *");
                break;
                
            case 8:
                System.out.println("*** Thank you for using the Travel Planner by R & M ***");
                System.out.println("                Have a safe journey !");
                System.out.println("                     Good Bye !");
                return;
                
            case 0:
                System.out.println("Help Menu:");
                System.out.println(" ");
                System.out.println("1. Display all places of interest on the map -> Shows all nodes with their places of interest.");
                System.out.println("2. Find all paths from source to destination -> Displays all possible paths between two cities.");
                System.out.println("3. Find shortest path from source to destination -> Finds the shortest path between two cities.");
                System.out.println("4. Find path through selected places of interest -> Finds a path that passes through specific places.");
                System.out.println("5. Find paths with maximum distance -> Displays paths within a specified distance.");
                System.out.println("6. Find the path with minimum number of stops -> Finds the path with the fewest stops.");
                System.out.println("7. Find path avoiding certain places (cities) -> Finds a path that avoids specified cities.");
                System.out.println("8. Exit -> Exits the application.");
                System.out.println();
                break;

            default:
                System.out.println("Invalid choice. Please try again.");
        }
        System.out.println(" ");
        System.out.println("Press enter to continue...");
        scanner.nextLine(); // Wait for the user to hit enter
    }
}
}
    
