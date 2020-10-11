package jonchaf.algorithms;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import cusack.hcg.database.PlayablePuzzle;
import cusack.hcg.games.weighted.WeightedInstance;
import cusack.hcg.games.weighted.travelingsalesman.TravelingSalesmanInstance;
import cusack.hcg.graph.Edge;
import cusack.hcg.graph.Vertex;
import cusack.hcg.graph.algorithm.standard.tests.DataSourceAbstractTest;
import cusack.hcg.model.PuzzleInstance;
import cusack.hcg.model.PuzzleInstanceFactory;
import jonchaf.algorithms.TSP;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TSPTests extends DataSourceAbstractTest {

	@Test
	public void test_K05() {
		verifyStuff(1325, 23);
	}

	@Test
	public void test_K09() {
		verifyStuff(1319, 16);
	}

	@Test
	public void test_K10() {
		verifyStuff(1320, 19);
	}

	@Test
	public void test_K12() {
		verifyStuff(1321, 19);
	}

	@Test
	public void test_K15() {
		verifyStuff(1322, 16);
	}

	@Test
	public void test_K20() {
		verifyStuff(1323,20);
	}

	@Test
	public void test_K25() {
		verifyStuff(1324); // optimal <= 44
	}

	@Test
	public void test_K30() {
		verifyStuff(1326); // optimal <= 31
	}

	@Test
	public void test_K35() {
		verifyStuff(1327); // optimal <= 80
	}

	@Test
	public void test_K40() {
		verifyStuff(1328); // optimal <= 62
	}

	@Test
	public void test_K45() {
		verifyStuff(1329); // optimal <= 73
	}

	@Test
	public void test_K50() {
		verifyStuff(1330); // optimal == 50
	}

	public void verifyStuff(int puzzle_id, int minCost) {
		System.out.println("------------------------------");
		System.out.println("Testing solution for puzzle " + puzzle_id);

		// Run the algorithm and get the result
		TSP tspInstance = new TSP();

		PlayablePuzzle pp = ds.getPuzzle(puzzle_id);
		PuzzleInstance pi = PuzzleInstanceFactory.createPuzzleInstance(pp);
		tspInstance.setProblemData((TravelingSalesmanInstance) pi);
		tspInstance.runAlgorithm();
		String results = tspInstance.getResult();

		System.out.println("Algorithm output:");
		System.out.println(results);
		checkSolution(puzzle_id, results);

		if (minCost != -1) {
			int cost = Integer.parseInt(results.split(":")[0]);
			if (cost == minCost) {
				System.out.println("*** You found an optimal solution! ***");
			} else if (cost > minCost) {
				System.out.println("*** Your solution is not optimal ***");
			} else {
				System.out.println(
						"*** Error in your solution? It seems to be better than the best possible which should be impossible. ***");
			}
		} else {
			System.out.println("Best solution unknown, so I cannot tell you how good your solution is.");
		}
	}

	public void verifyStuff(int puzzle_id) {
		verifyStuff(puzzle_id, -1);
	}

	public void checkSolution(int puzzleID, String solution) {
		PlayablePuzzle p = ds.getPuzzle(puzzleID);
		if (p == null) {
			System.out.println("Puzzle not found.");
		} else {
			PuzzleInstance pi = PuzzleInstanceFactory.createPuzzleInstance(p);
			if (pi instanceof WeightedInstance) {
				WeightedInstance wi = (WeightedInstance) pi;
				int n = wi.getNumberOfVertices();
				int solWeight = 0;
				int[] sol = new int[n];
				try {
					String parts[] = solution.split(":");
					solWeight = Integer.parseInt(parts[0]);

					// -------------------------------------------------------------
					// First check that the solution is a permutation of
					// [0...(n-1)]
					String verts[] = parts[1].split(" ");
					if (verts.length != n) {
						System.out.println("**** The list does not contain precisely " + n + " vertices.");
						return;
					}
					HashSet<Integer> vertSet = new HashSet<Integer>();
					int ind = 0;
					for (String s : verts) {
						int num = Integer.parseInt(s);
						sol[ind] = num;
						ind++;
						vertSet.add(num);
					}
					for (int i = 0; i < n; i++) {
						if (!vertSet.contains(Integer.valueOf(i))) {
							System.out.println("**** " + i + " is missing from the list");
							return;
						}
					}

					// -------------------------------------------------------------
					// Now verify that the cost they specify is actually the
					// cost.
					ArrayList<Vertex> vertices = wi.getVertices();
					int actualWeight = 0;
					for (int i = 0; i < n - 1; i++) {
						int w = wi.getWeightForEdge(Edge.createEdge(vertices.get(sol[i]), vertices.get(sol[i + 1])));
						actualWeight += w;
					}
					int w = wi.getWeightForEdge(Edge.createEdge(vertices.get(sol[n - 1]), vertices.get(sol[0])));
					actualWeight += w;
					if (actualWeight == solWeight) {
						System.out.println("Solution is consistent, but not necessarily optimal.");
					} else {
						System.out.println("**** The solution weight of " + solWeight + " is incorrect.  It should be "
								+ actualWeight);
					}
				} catch (NumberFormatException e) {
					System.out.println("**** Solution is not in the correct format.");
				}

			} else {
				System.out.println("**** Not a Weighted Graph!");
			}
		}
	}
}