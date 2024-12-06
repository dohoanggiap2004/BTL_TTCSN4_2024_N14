import java.io.*;
import java.util.*;

public class GeneticAlgorithm implements GeneticAlgorithmInterface {
    @Override
    public List<Item> readItemsFromFile(String fileName) throws IOException {
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

    @Override
    public List<Integer> createRandomSolution(int size) {
        Random random = new Random();
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            solution.add(random.nextInt(2));
        }
        return solution;
    }

    @Override
    public boolean validSolution(List<Item> items, List<Integer> solution, double carrierLimit) {
        double totalWeight = 0;
        for (int i = 0; i < solution.size(); i++) {
            if (solution.get(i) == 1) {
                totalWeight += items.get(i).getWeight();
            }
            if (totalWeight > carrierLimit) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double calculateValue(List<Item> items, List<Integer> solution) {
        double totalValue = 0;
        for (int i = 0; i < solution.size(); i++) {
            if (solution.get(i) == 1) {
                totalValue += items.get(i).getValue();
            }
        }
        return totalValue;
    }

    @Override
    public List<List<Integer>> initialPopulation(List<Item> items, int populationSize, double carrierLimit) {
        List<List<Integer>> population = new ArrayList<>();
        while (population.size() < populationSize) {
            List<Integer> solution = createRandomSolution(items.size());
            if (validSolution(items, solution, carrierLimit) && !population.contains(solution)) {
                population.add(solution);
            }
        }
        return population;
    }

    @Override
    public List<List<Integer>> createGeneration(List<List<Integer>> population, List<Item> items, double mutationRate, double carrierLimit) {
        List<List<Integer>> newGeneration = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < population.size(); i++) {
            List<Integer> parent1 = tournamentSelection(population, items);
            List<Integer> parent2 = tournamentSelection(population, items);

            // Lai ghép (crossover) để tạo thế hệ mới
            List<Integer> child = crossover(parent1, parent2, items, carrierLimit);

            // Xác suất xảy ra đột biến (mutation)
            if (random.nextDouble() < mutationRate) {
                child = mutation(child, items, carrierLimit);
            }
            newGeneration.add(child);
        }
        return newGeneration;
    }

    private List<Integer> tournamentSelection(List<List<Integer>> population, List<Item> items) {
        Random random = new Random();
        int ticket1 = random.nextInt(population.size());
        int ticket2 = random.nextInt(population.size());

        // Chọn cá thể tốt hơn từ hai cá thể ngẫu nhiên
        double value1 = calculateValue(items, population.get(ticket1));
        double value2 = calculateValue(items, population.get(ticket2));

        return value1 > value2 ? population.get(ticket1) : population.get(ticket2);
    }

    private List<Integer> crossover(List<Integer> parent1, List<Integer> parent2, List<Item> items, double carrierLimit) {
        Random random = new Random();
        int breakpoint = random.nextInt(parent1.size()); // Điểm cắt để lai ghép
        List<Integer> child = new ArrayList<>();
        child.addAll(parent1.subList(0, breakpoint));
        child.addAll(parent2.subList(breakpoint, parent2.size()));

        // Kiểm tra nếu con hợp lệ thì trả về, ngược lại thực hiện lại lai ghép
        if (validSolution(items, child, carrierLimit)) {
            return child;
        }
        return crossover(parent1, parent2, items, carrierLimit);
    }

    private List<Integer> mutation(List<Integer> chromosome, List<Item> items, double carrierLimit) {
        Random random = new Random();
        List<Integer> mutated = new ArrayList<>(chromosome);
        int index1 = random.nextInt(chromosome.size());
        int index2 = random.nextInt(chromosome.size());

        // Thực hiện hoán đổi ngẫu nhiên hai gene
        Collections.swap(mutated, index1, index2);

        // Nếu cá thể sau đột biến hợp lệ thì trả về, ngược lại thực hiện lại đột biến
        if (validSolution(items, mutated, carrierLimit)) {
            return mutated;
        }
        return mutation(chromosome, items, carrierLimit);
    }
}