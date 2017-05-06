/**
 * Created by banksters on 3/19/2017.
 */
public class LevenshteinEvaluation {

        ///////////////////////////
        // OG LEVENSHTEIN
        ///////////////////////////

        public static int levenshteinDistance (String lhs, String rhs) {
            int len0 = lhs.length() + 1;
            int len1 = rhs.length() + 1;

            // the array of distances
            int[] cost = new int[len0];
            int[] newcost = new int[len0];

            // initial cost of skipping prefix in String s0
            for (int i = 0; i < len0; i++)
                cost[i] = i;

            // dynamically computing the array of distances

            // transformation cost for each letter in s1
            for (int j = 1; j < len1; j++) {
                // initial cost of skipping prefix in String s1
                newcost[0] = j;

                // transformation cost for each letter in s0
                for (int i = 1; i < len0; i++) {
                    // matching current letters in both strings
                    int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                    // computing cost for each transformation
                    int cost_replace = cost[i - 1] + match;
                    int cost_insert = cost[i] + 1;
                    int cost_delete = newcost[i - 1] + 1;

                    // keep minimum cost
                    newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
                }

                // swap cost/newcost arrays
                int[] swap = cost;
                cost = newcost;
                newcost = swap;
            }

            // the distance is the cost for transforming all letters in both strings
            return cost[len0 - 1];
        }

    private static int alignmentScore = 0;
    private static String globalString1 = "";
    private static String globalString2 = "";

    ///////////////////////////
    // NEW LEVENSHTEIN
    ///////////////////////////

    // Insertions are "-"s in expert file
    // Deletions are "-"s in student file
    // Replacements are different characters on the same alignment

    public static String[] align(String a, String b) {

        alignmentScore = 0;

        int[][] T = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++)
            T[i][0] = i;

        for (int i = 0; i <= b.length(); i++)
            T[0][i] = i;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1))
                    T[i][j] = T[i - 1][j - 1];
                else
                    T[i][j] = Math.min(T[i - 1][j], T[i][j - 1]) + 1;
            }
        }

        StringBuilder aa = new StringBuilder(), bb = new StringBuilder();

        for (int i = a.length(), j = b.length(); i > 0 || j > 0; ) {
            if (i > 0 && T[i][j] == T[i - 1][j] + 1) {
                aa.append(a.charAt(--i));
                bb.append("-");
                alignmentScore++;
            } else if (j > 0 && T[i][j] == T[i][j - 1] + 1) {
                bb.append(b.charAt(--j));
                aa.append("-");
                alignmentScore++;
            } else if (i > 0 && j > 0 && T[i][j] == T[i - 1][j - 1]) {
                aa.append(a.charAt(--i));
                bb.append(b.charAt(--j));
            }
        }

        globalString1 = aa.reverse().toString();
        globalString2 = bb.reverse().toString();

        return new String[]{aa.reverse().toString(), bb.reverse().toString()};
    }

    public int getAlignmentScore()
    {
        return alignmentScore;
    }

    public String getGlobalString1()
    {
        return globalString1;
    }

    public String getGlobalString2()
    {
        return globalString2;
    }

}


