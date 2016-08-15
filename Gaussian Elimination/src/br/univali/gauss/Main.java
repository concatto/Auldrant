package br.univali.gauss;

public class Main {
	public static void main(String[] args) {
		double[][] matrix = {{4, 3, 4, -2}, {2, 5, 7, 1}, {3, 1, 6, 1}, {0, -3, 2, 3}};
		
		for (int it = 0; it < 3; it++) {
			double[] pivotRow = matrix[it];
			double pivot = pivotRow[it];
			if (pivot == 0) {
				System.out.println("Pivot is 0. Must swap rows. Terminating.");
				return;
			}
			for (int i = it + 1; i < matrix.length; i++) {
				double[] row = matrix[i];
				if (row[it] == 0) continue;
				double multiplier = row[it] / pivot;
				for (int j = 0; j < row.length; j++) {
					row[j] = row[j] - multiplier * pivotRow[j];
				}
			}
		}
		
		for (double[] row : matrix) {
			for (double value : row) {
				System.out.printf("%.3f, ", value);
			}
			System.out.println();
		}
		
		double determinant = 1;
		for (int i = 0; i < matrix.length; i++) {
			determinant *= matrix[i][i];
		}
		
		System.out.printf("Determinant: %.2f", determinant);
	}
}
