import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileName = "src/items"; // Đường dẫn file chứa dữ liệu
        double carrierLimit = 25.0; // Giới hạn trọng lượng
        int populationSize = 10; // Kích thước quần thể
        int generationSize = 100; // Số thế hệ
        double mutationRate = 0.1; // Tỉ lệ đột biến

        GeneticAlgorithm ga = new GeneticAlgorithm();
        List<Item> items = ga.readItemsFromFile(fileName);
        List<List<Integer>> population = ga.initialPopulation(items, populationSize, carrierLimit);

        // Danh sách lưu giá trị tốt nhất của mỗi thế hệ
        List<Double> bestValues = new ArrayList<>();

        for (int generation = 0; generation < generationSize; generation++) {
            population = ga.createGeneration(population, items, mutationRate, carrierLimit);

            double bestValue = 0;
            System.out.println("Generation " + generation + ":");
            for (List<Integer> chromosome : population) {
                double currentValue = ga.calculateValue(items, chromosome);
                System.out.println(chromosome + " -> Value: " + currentValue);
                if (currentValue > bestValue) {
                    bestValue = currentValue;
                }
            }

            // Lưu giá trị tốt nhất vào danh sách
            bestValues.add(bestValue);
            System.out.println("Thế hệ " + generation + " giá trị tốt nhất: " + bestValue);
        }

        // In ra toàn bộ mảng giá trị tốt nhất
        System.out.println("Mảng giá trị tốt nhất của mỗi thế hệ: " + bestValues);
    }
}
