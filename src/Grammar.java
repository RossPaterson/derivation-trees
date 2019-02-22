import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** A context-free grammar. */
public class Grammar {
    private ArrayList<String> lhss;
    private Map<String, Collection<ArrayList<String>>> productions;

    public Grammar() {
        lhss = new ArrayList<>();
        productions = new HashMap<>();
    }

    /** Add a production to the grammar. */
    public void addProduction(String lhs, ArrayList<String> rhs) {
        Collection<ArrayList<String>> prods = productions.get(lhs);
        if (prods == null) {
            prods = new ArrayList<>();
            lhss.add(lhs);
        }
        prods.add(rhs);
        productions.put(lhs, prods);
    }

    /** Start symbol (the nonterminal on the left of the first production). */
    public String getStart() {
        return lhss.get(0);
    }

    /** Nonterminals of the grammar, in order of definition. */
    public Collection<String> nonTerminals() {
        return lhss;
    }

    /** Expansions of a nonterminal. */
    public Collection<ArrayList<String>> expansions(String nt) {
        return productions.get(nt);
    }
}
