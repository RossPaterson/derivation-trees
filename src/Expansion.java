import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Expansion {
    private final Grammar grammar;
    private final int limit;

    private Map<String, ArrayList<NonTerminalTree>> lgges;
    private int count;
    private int expandCount;

    /** Empty language for each nonterminal. */
    public Expansion(Grammar grammar, int limit) {
        this.grammar = grammar;
        this.limit = limit;

        count = 0;
        expandCount = 0;
        lgges = new HashMap<>();
        for (String nt : grammar.nonTerminals())
            lgges.put(nt, new ArrayList<NonTerminalTree>());
    }

    /** Given a set of trees up to depth n, update to trees up to depth n+1. */
    public final boolean expand() {
        Map<String, ArrayList<NonTerminalTree>> new_lgges = new HashMap<>();
        for (String nt : grammar.nonTerminals()) {
            ArrayList<NonTerminalTree> ts = new ArrayList<>();
            for (ArrayList<String> rhs : grammar.expansions(nt)) {
                ArrayList<Cons<ParseTree>> strs = new ArrayList<>();
                strs.add(null);
                for (int i = rhs.size() - 1; i >= 0; i--) {
                    String sym = rhs.get(i);
                    ArrayList<NonTerminalTree> exps = lgges.get(sym);
                    if (exps == null) { // terminal
                        ParseTree t = new TerminalTree(sym);
                        for (int j = 0; j < strs.size(); j++)
                            strs.set(j, new Cons<>(t, strs.get(j)));
                    } else {
                        ArrayList<Cons<ParseTree>> new_strs = new ArrayList<>();
                        for (ParseTree t : exps)
                            for (Cons<ParseTree> str : strs)
                                new_strs.add(new Cons<>(t, str));
                        strs = new_strs;
                    }
                }
                for (Cons<ParseTree> str : strs) {
                    NonTerminalTree t = new NonTerminalTree(nt, Cons.iterable(str));
                    ts.add(t);
                    count = count + t.height()*t.width();
                    if (count > limit)
                        return false;
                }
            }
            new_lgges.put(nt, ts);
        }
        lgges = new_lgges;
        expandCount++;
        return true;
    }

    public ArrayList<NonTerminalTree> derivations(String nt) {
        return lgges.get(nt);
    }

    public final int depth() {
        return expandCount;
    }

    public final int size() {
        int n = 0;
        for (String nt : grammar.nonTerminals())
            n = n + derivations(nt).size();
        return n;
    }
}
