
import java.io.IOException;
import java.util.List;

public interface GeneticAlgorithmInterface {
    List<Item> readItemsFromFile(String fileName) throws IOException;
    List<Integer> createRandomSolution(int size);
    boolean validSolution(List<Item> items, List<Integer> solution, double carrierLimit);
    double calculateValue(List<Item> items, List<Integer> solution);
    List<List<Integer>> initialPopulation(List<Item> items, int populationSize, double carrierLimit);
    List<List<Integer>> createGeneration(List<List<Integer>> population, List<Item> items, double mutationRate, double carrierLimit);
}
