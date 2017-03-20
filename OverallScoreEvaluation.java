/*
http://www.cs.utexas.edu/~mobios/cs329e/rosetta/src/SmithWaterman.java
 */

/**
Smith-Waterman Algorithm Java implementation
 */
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class OverallScoreEvaluation {
    private final double scoreThreshold = 19.9;
    
    // Input strings
    private String str1, str2;
    
    // Length of input strings
    private int length1, length2;
    
    // Score matrix
    // True scores should be divided by normalization factor
    private double[][] score;
    
    // int to count number of differences with each alignment
    private int numDifferences;
    
    // Normalization factor
    // To get true score, divide integer score used in computation 
    // by normalization factor.
    static final double NORM_FACTOR = 1.0;
    
    // Similarity function constants
    // Amplified by normalization factor to be integers
    static final int MATCH_SCORE = 10;
    static final int MISMATCH_SCORE = -8;
    static final int INDEL_SCORE = -9;
    
    // Constants of directions
    // Multiple directions sorted by bits.
    // Zero direction is starting point.
    static final int DR_LEFT = 1;   // 0001
    static final int DR_UP = 2;     // 0010
    static final int DR_DIAG = 4;   // 0100
    static final int DR_ZERO = 8;   // 1000
    
    // Directions pointing to cells that give max score at current cell.
    // First index = column index
    // Second index = row index
    private int[][] prevCells;
    
    // constructor for 2 strings to compare
    public OverallScoreEvaluation(String str1_in, String str2_in)
    {
        str1 = str1_in;
        str2 = str2_in;
        length1 = str1.length();
        length2 = str2.length();
        
        score = new double[length1+1][length2+1];
        prevCells = new int[length1+1][length2+1];
        numDifferences = 0;
        
        buildMatrix();
    }
    
    // Build score matrix using dynamix programming
    private void buildMatrix()
    {
        if (INDEL_SCORE >= 0)
            throw new Error("Indel score must be negative");
        
        int i; // length of prefix substring of str1
        int j; // length of prefix substring of str2
        
        // base case
        score[0][0] = 0;
        prevCells[0][0] = DR_ZERO; // starting point
        
        // initialize first row to zeros
        for (i = 1; i <= length1; i++){
            score[i][0] = 0;
            prevCells[i][0] = DR_ZERO;
        }
        
        // initialize first column to zeros
        for (j = 1; j <= length2; j++){
            score[0][j] = 0;
            prevCells[0][j] = DR_ZERO;
        }
        
        // continue with rest of matrix
        for (i = 1; i <= length1; i++) {
            for (j = 1; j <= length2; j++) {
                // Calculate possible scores based on previous indeces
                double diagScore = score[i-1][j-1] + similarity(i,j);
                double upScore = score[i][j-1] + similarity(0,j);
                double leftScore = score[i-1][j] + similarity(i, 0);
                
                // Pick highest score to store into current indeces
                score[i][j] = Math.max(diagScore, Math.max(upScore, Math.max(leftScore, 0)));
                
                // Find directions that give maximum scores
                // bitwise OR operator used to record multiple directions
                if (diagScore == score[i][j])
                    prevCells[i][j] |= DR_DIAG;
                if (leftScore == score[i][j])
                    prevCells[i][j] |= DR_LEFT;
                if (upScore == score[i][j])
                    prevCells[i][j] |= DR_UP;
                if (0 == score[i][j])
                    prevCells[i][j] |= DR_ZERO;
            }
        }
    }
    
    // Output dynamic programming matrix
    public void printDPMatrix() {
        System.out.print("      ");
        for (int j = 1; j <= length2; j++) {
            System.out.print("      " + str2.charAt(j - 1));
        }
        System.out.println();
        for (int i = 0; i <= length1; i++) {
            if (i > 0) {
                System.out.print(str1.charAt(i - 1) + " ");
            } else {
                System.out.print("  ");
            }
            for (int j = 0; j <= length2; j++) {
                double scoreOutput = score[i][j] / NORM_FACTOR;
                System.out.format("| %03.0f |", scoreOutput);
            }
            System.out.println();
            //System.out.println("\n--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }
    
    /*
    Compute similarity score of substitution
    Position of first character is 1
    Position of 0 represents a gap
    i = position of character in str1
    j = position of character in str2
    Returns cost of substitution of character in str1 by the one in str2
    */
    private double similarity(int i, int j)
    {
        // Gap detected
        if (i == 0 || j == 0)
            return INDEL_SCORE;
        // Match detected
        else if (str1.charAt(i-1) == str2.charAt(j-1))
            return MATCH_SCORE;
        // Mismatch detected
        else
            return MISMATCH_SCORE;
    }
    
    // Get max value in score matrix
    private double getMaxScore()
    {
        double maxScore = 0;
        
        // skip first row and column
        for (int i = 0; i <= length1; i++)
            for (int j = 0; j <= length2; j++)
                if (score[i][j] > maxScore)
                    maxScore = score[i][j];
        
        return maxScore;
    }
    
    // Get alignment score between two input strings
    public double getAlignmentScore()
    {
        return getMaxScore() / NORM_FACTOR;
    }
    
    // Print local alignment with maximum score
    private void printAlignments()
    {
        // find cell with maximum score
        double maxScore = getMaxScore();
        // skip first row and column
        for (int i = 1; i <= length1; i++)
            for (int j = 1; j <= length2; j++)
                if (score[i][j] == maxScore)
                {
                    numDifferences = 0;
                    printAlignments(i,j,"","");
                }
    }
    
    /*
    Print local alignments ending in (i, j) cell
    aligned1 and aligned2 = suffixes for final aligned strings found in backtracking before calling this function
    Note: strings replicated at each recursive call; use buffers/stacks to improve efficiency
    */
    private void printAlignments(int i, int j, String aligned1, String aligned2) {
        // Output current alignment strings
        System.out.println("aligned1: " + aligned1);
        System.out.println("alighed2: " + aligned2 + "\n");
        
        // check for mismatch in chars with aligned strings
        if (aligned1 != "" && aligned2 != "")
            if (aligned1.charAt(0) != aligned2.charAt(0))
                numDifferences++;
        
        // reached starting point; print alignments
        if ((prevCells[i][j] & DR_ZERO) > 0) {
            //System.out.println(aligned1);
            //System.out.println(aligned2);
            System.out.println("Score: " + numDifferences);
            System.out.println("--------------------------");
            numDifferences = 0;
            return;
        }
        
        // find out which direction to backtrack
        if ((prevCells[i][j] & DR_LEFT) > 0) {
            printAlignments(i-1, j, str1.charAt(i-1) + aligned1, "_" + aligned2);
        }
        if ((prevCells[i][j] & DR_UP) > 0) {
            printAlignments(i, j-1, "_" + aligned1, str2.charAt(j-1) + aligned2);
        }
        if ((prevCells[i][j] & DR_DIAG) > 0)
            printAlignments(i-1, j-1, str1.charAt(i-1) + aligned1, str2.charAt(j-1) + aligned2);
    }
    
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter string 1 (space delimited): ");
        String string1 = keyboard.nextLine();
        System.out.print("Enter string 2 (space delimited): ");
        String string2 = keyboard.nextLine();
        
        OverallScoreEvaluation scoreEvaluator = new OverallScoreEvaluation(string1, string2);
        
        // Output maximum alignment score
        System.out.println("\nThe maximum alignment score is: " + scoreEvaluator.getAlignmentScore());
        
        // Output dynamic programming scoring matrix
        System.out.println("The dynamic programming distance matrix is: ");
    	scoreEvaluator.printDPMatrix();
        
        // Output all alignments with maximum score
        System.out.println("\nThe alignments with the maximum score are: \n");
        scoreEvaluator.printAlignments();
    }
}
