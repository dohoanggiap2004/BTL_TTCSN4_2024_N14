import java.io.*;
import java.util.*;

public class GeneticAlgorithm {

    static String fileName = "src/items"; // Đường dẫn file chứa dữ liệu
    static double carrierLimit = 25.0; // Giới hạn trọng lượng (kg)
    static int populationSize = 10; // Kích thước quần thể
    static int generationSize = 100; // Số thế hệ
    static double mutationRate = 0.1; // Tỉ lệ đột biến

    // Lớp Item đại diện cho một đối tượng với id, trọng lượng, và giá trị
    static class Item {
        int id;
        double weight;
        double value;

        public Item(int id, double weight, double value) {
            this.id = id;
            this.weight = weight;
            this.value = value;
        }
    }

    // Đọc dữ liệu từ file và tạo danh sách Item
    static List<Item> readItemsFromFile(String fileName) throws IOException {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(" ");
                int id = Integer.parseInt(parts[0]);
                double weight = Double.parseDouble(parts[1]);
                double value = Double.parseDouble(parts[2]);
                items.add(new Item(id, weight, value));
            }
        }
        return items;
    }

    // Tạo một lời giải ngẫu nhiên
    static List<Integer> createRandomSolution(int size) {
        Random random = new Random();
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            solution.add(random.nextInt(2));
        }
        return solution;
    }

    // Kiểm tra lời giải có hợp lệ không
    static boolean validSolution(List<Item> items, List<Integer> solution, double limit) {
        double totalWeight = 0;
        for (int i = 0; i < solution.size(); i++) {
            if (solution.get(i) == 1) {
                totalWeight += items.get(i).weight;
            }
            if (totalWeight > limit) {
                return false;
            }
        }
        return true;
    }

    // Tính giá trị của lời giải
    static double calculateValue(List<Item> items, List<Integer> solution) {
        double totalValue = 0;
        for (int i = 0; i < solution.size(); i++) {
            if (solution.get(i) == 1) {
                totalValue += items.get(i).value;
            }
        }
        return totalValue;
    }

    // Tạo quần thể ban đầu
    static List<List<Integer>> initialPopulation(int popSize, List<Item> items, double limit) {
        List<List<Integer>> population = new ArrayList<>();
        Random random = new Random();
        while (population.size() < popSize) {
            List<Integer> solution = createRandomSolution(items.size());
            if (validSolution(items, solution, limit)) {
                if (!population.contains(solution)) {
                    population.add(solution);
                }
            }
        }
        return population;
    }

    // Chọn cá thể tốt nhất giữa hai cá thể ngẫu nhiên
    static List<Integer> tournamentSelection(List<List<Integer>> population, List<Item> items) {
        Random random = new Random();
        int ticket1 = random.nextInt(population.size());
        int ticket2 = random.nextInt(population.size());

        double value1 = calculateValue(items, population.get(ticket1));
        double value2 = calculateValue(items, population.get(ticket2));

        return value1 > value2 ? population.get(ticket1) : population.get(ticket2);
    }

    // Phép lai một điểm
    static List<Integer> crossover(List<Integer> parent1, List<Integer> parent2, List<Item> items, double limit) {
        Random random = new Random();
        int breakpoint = random.nextInt(parent1.size());
        List<Integer> child = new ArrayList<>();
        child.addAll(parent1.subList(0, breakpoint));
        child.addAll(parent2.subList(breakpoint, parent2.size()));

        if (validSolution(items, child, limit)) {
            return child;
        }
        return crossover(parent1, parent2, items, limit);
    }

    // Phép đột biến
    static List<Integer> mutation(List<Integer> chromosome, List<Item> items, double limit) {
        Random random = new Random();
        List<Integer> mutated = new ArrayList<>(chromosome);
        int index1 = random.nextInt(chromosome.size());
        int index2 = random.nextInt(chromosome.size());

        // Hoán đổi 2 gene
        Collections.swap(mutated, index1, index2);

        if (validSolution(items, mutated, limit)) {
            return mutated;
        }
        return mutation(chromosome, items, limit);
    }

    // Tạo thế hệ mới
    static List<List<Integer>> createGeneration(List<List<Integer>> population, double mutationRate, List<Item> items, double limit) {
        List<List<Integer>> newGeneration = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < population.size(); i++) {
            List<Integer> parent1 = tournamentSelection(population, items);
            List<Integer> parent2 = tournamentSelection(population, items);

            List<Integer> child = crossover(parent1, parent2, items, limit);

            if (random.nextDouble() < mutationRate) {
                child = mutation(child, items, limit);
            }
            newGeneration.add(child);
        }
        return newGeneration;
    }

    // Thuật toán
    static void geneticAlgorithm(double carrierLimit, int populationSize, int generationSize, double mutationRate, List<Item> items) {
        List<List<Integer>> population = initialPopulation(populationSize, items, carrierLimit);
        List<Double> valueList = new ArrayList<>();

        for (int generation = 0; generation < generationSize; generation++) {
            population = createGeneration(population, mutationRate, items, carrierLimit);

            // in ra các generation
            System.out.println("Generation " + generation + ":");
            for (List<Integer> chromosome : population) {
                System.out.println(chromosome + " -> Value: " + calculateValue(items, chromosome));
            }

            double bestValue = calculateValue(items, population.get(0));
            valueList.add(bestValue);
        }

        System.out.println("Best values in each generation: " + valueList);
    }

    public static void main(String[] args) throws IOException {
        List<Item> items = readItemsFromFile(fileName);
        geneticAlgorithm(carrierLimit, populationSize, generationSize, mutationRate, items);
    }
}
