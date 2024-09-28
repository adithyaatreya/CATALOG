import org.json.JSONObject;
import org.json.JSONException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.math.BigInteger;
import Jama.Matrix;

public class PolynomialSolver {

    public static BigInteger decodeBaseValue(int base, String value) {
        // Converts a string 'value' from the given 'base' to a BigInteger.
        return new BigInteger(value, base);
    }

    public static double findConstantTerm(Map<Integer, String[]> roots) {
        List<Integer> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        // Process each root (x, y) pair
        for (Map.Entry<Integer, String[]> entry : roots.entrySet()) {
            int x = entry.getKey();
            int base = Integer.parseInt(entry.getValue()[0]);
            String yStr = entry.getValue()[1];
            BigInteger y = decodeBaseValue(base, yStr); // Use BigInteger
            xValues.add(x);
            yValues.add(y);
        }

        // Create the matrix for the system of equations (Vandermonde matrix)
        double[][] A = new double[xValues.size()][xValues.size()];
        double[] b = new double[yValues.size()];

        for (int i = 0; i < xValues.size(); i++) {
            b[i] = yValues.get(i).doubleValue(); // Convert BigInteger to double
            for (int j = 0; j < xValues.size(); j++) {
                A[i][j] = Math.pow(xValues.get(i), j);
            }
        }

        // Use Jama to solve the linear equations A * coeffs = b
        Matrix matrixA = new Matrix(A);
        Matrix matrixB = new Matrix(b, b.length);
        Matrix coeffs = matrixA.solve(matrixB);

        // The last coefficient is the constant term 'c'
        return coeffs.get(coeffs.getRowDimension() - 1, 0);
    }

    public static Map<Integer, String[]> parseRoots(JSONObject json) throws JSONException {
        Map<Integer, String[]> roots = new HashMap<>();

        // Extract root values from the JSON, ignoring "keys" entry
        for (String key : json.keySet()) {
            if (!key.equals("keys")) {
                JSONObject root = json.getJSONObject(key);
                String base = root.getString("base");
                String value = root.getString("value");
                roots.put(Integer.parseInt(key), new String[]{base, value});
            }
        }

        return roots;
    }

    public static void main(String[] args) {
        try {
            // Read JSON files (test_case_1.json and test_case_2.json)
            String content1 = new String(Files.readAllBytes(Paths.get("test_case_1.json")));
            String content2 = new String(Files.readAllBytes(Paths.get("test_case_2.json")));

            // Parse JSON content into JSONObject
            JSONObject testCase1 = new JSONObject(content1);
            JSONObject testCase2 = new JSONObject(content2);

            // Extract roots and solve for constant term for both test cases
            Map<Integer, String[]> roots1 = parseRoots(testCase1);
            Map<Integer, String[]> roots2 = parseRoots(testCase2);

            double constant1 = findConstantTerm(roots1);
            double constant2 = findConstantTerm(roots2);

            // Print the constant terms for both test cases
            System.out.println("The constant term for Test Case 1 is: " + constant1);
            System.out.println("The constant term for Test Case 2 is: " + constant2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



