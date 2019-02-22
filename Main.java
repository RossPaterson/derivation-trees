import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class Main {
    private static final int LIMIT = 10000;
    private final static char EMPTY = '\u03b5';

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("usage: lhs rhs ... [sentence]");
            System.exit(0);
        }

        final String sentence =
            args.length%2 == 1 ? args[args.length - 1] : null;

        Grammar g = new Grammar();
        for (int i = 1; i < args.length; i = i+2) {
            String lhs = args[i-1];
            for (String rhs : args[i].split("[|]", -1))
                g.addProduction(lhs, symList(rhs));
        }

        GrammarProperties properties = new GrammarProperties(g);

        String treeHeading;
        ArrayList<NonTerminalTree> trees;
        if (sentence != null) {
            Earley parser = new Earley(g);
            trees = new ArrayList<>();
            boolean full = parser.parse(symList(sentence), trees);
            if (! full)
                treeHeading = "Some of the derivations";
            else if (trees.isEmpty())
                treeHeading = "There are no derivations";
            else if (trees.size() == 1)
                treeHeading = "Derivation tree";
            else
                treeHeading = "Derivation trees";
            treeHeading = treeHeading + " for '" + sentence + "'";
        } else {
            int maxDepth = g.nonTerminals().size() + 9;
            Expansion lgges = new Expansion(g, LIMIT);
            boolean finite = false;
	    int last_size = 0;
            while (lgges.depth() < maxDepth) {
                if (! lgges.expand())
                    break;
                int size = lgges.size();
                if (size == last_size) {
                    finite = true;
                    break;
                }
                last_size = size;
            }
            trees = lgges.derivations(g.getStart());
            if (finite)
                treeHeading = "All derivation trees";
            else
                treeHeading = "Derivation trees of depth at most " + lgges.depth();
        }
        Collections.sort(trees, new NonTerminalTree.Ascending());

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"https://staff.city.ac.uk/~ross/IN1002/css/algorithms.css\"/>");
        out.println("<title>Derivation trees</title>");
        out.println("</head>");
        out.println("<!-- Java version " + System.getProperty("java.version") + " -->");
        out.println("<h1>Derivation trees</h1>");

        out.println("<h2>Grammar</h2>");
        showGrammar(out, g);

        if (! properties.getUnreachable().isEmpty() ||
            ! properties.getUnrealizable().isEmpty() ||
            ! properties.getCyclic().isEmpty()) {
            out.println("<p>This grammar has the following problems:");
            out.println("<ul>");
            report(out, properties.getUnreachable(), "unreachable from the start symbol " + g.getStart());
            report(out, properties.getUnrealizable(), "unrealizable (cannot generate any strings)");
            report(out, properties.getCyclic(),
                properties.infinitelyAmbiguous() ?
                    "cyclic, so some strings have infinitely many derivations" :
                    "cyclic");
            out.println("</ul>");
        }

        out.println("<h2>" + treeHeading + "</h2>");
        for (NonTerminalTree t : trees)
            t.drawSVG(out);

        out.println("</html>");
	out.close();
    }

    private static ArrayList<String> symList(String s) {
        ArrayList<String> exp = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (! Character.isWhitespace(c) && c != EMPTY)
                exp.add(String.valueOf(c));
        }
        return exp;
    }

    private static void showGrammar(PrintWriter out, Grammar g) {
        out.println("<ul class=\"plain\">");
        for (String lhs : g.nonTerminals()) {
            out.print("<li>");
            out.print(lhs);
            out.print(" &#x2192; ");
            boolean firstAlt = true;
            for (ArrayList<String> alt : g.expansions(lhs)) {
                if (firstAlt)
                    firstAlt = false;
                else
                    out.print(" | ");
                if (alt.size() == 0)
                    out.print("&#x03B5;");
                else {
                    boolean firstSym = true;
                    for (String s : alt) {
                        if (firstSym)
                            firstSym = false;
                        else
                            out.print(" ");
                        out.print(s);
                    }
                }
            }
            out.println();
        }
        out.println("</ul>");
    }

    private static void report(PrintWriter out, Set<String> s, String label) {
        if (! s.isEmpty()) {
            out.print("<li>");
            out.print(s.size() == 1 ? "Nonterminal " : "Nonterminals ");
            boolean comma = false;
            for (String nt : s) {
                if (comma)
                    out.print(", ");
                out.print(nt);
                comma = true;
            }
            out.print(s.size() == 1 ? " is " : " are ");
            out.print(label);
            out.println(".");
        }
    }
}
