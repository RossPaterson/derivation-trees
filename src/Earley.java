import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Earley {
    private static final String START = "Start";
    private static final int EXPANSION_LIMIT = 100;

    private final Grammar grammar;
    private ArrayList<Set<EarleyItem>> lastStates;

    public Earley(Grammar grammar) {
        this.grammar = grammar;
        this.lastStates = null;
    }

    public boolean parse(final ArrayList<String> input, ArrayList<NonTerminalTree> results) {
        ArrayList<Set<EarleyItem>> states = new ArrayList<>();
        for (int i = 0; i <= input.size(); i++)
		states.add(null);

        boolean truncated = false;
        for (int pos = input.size(); pos >= 0; pos--) {
            Queue<EarleyItem> queue = new ArrayDeque<>();
            if (pos == input.size()) {
                // initial state (starting from end of string)
                ArrayList<String> rhs0 = new ArrayList<>();
                rhs0.add(grammar.getStart());
                queue.add(new EarleyItem(START, rhs0, pos));
            } else {
                // scan a terminal symbol
                String nextSym = input.get(pos);
                if (grammar.expansions(nextSym) == null) { // terminal
                    TerminalTree t = new TerminalTree(nextSym);
                    for (EarleyItem item : states.get(pos+1))
                        if (item.match(nextSym))
                            queue.add(new EarleyItem(item, t));
                }
            }

            Set<EarleyItem> state = new HashSet<>();
            states.set(pos, state);
            Set<NonTerminalTree> empties = new HashSet<>();
            // guard against unlimited expansion
            while (! queue.isEmpty()) {
                if (state.size() > EXPANSION_LIMIT) {
                    truncated = true;
                    break;
                }
                EarleyItem item = queue.remove();
                if (! state.contains(item)) {
                    state.add(item);
                    // expand the item
                    if (item.finished()) {
                        // complete a production
                        NonTerminalTree t = item.complete();
                        String nt = t.nonTerminal();
                        final int end = item.start();
                        if (end == pos)
                            // null expansions need special treatment
                            empties.add(t);
                        for (EarleyItem prev : states.get(end))
                            if (prev.match(nt))
                                queue.add(new EarleyItem(prev, t));
                    } else {
                        // predict: expand a nonterminal
                        String nt = item.current();
                        Collection<ArrayList<String>> rhss =
                            grammar.expansions(nt);
                        if (rhss != null) {
                            for (ArrayList<String> rhs : rhss)
                                queue.add(new EarleyItem(nt, rhs, pos));
                            for (NonTerminalTree t : empties)
                                if (t.nonTerminal().equals(nt))
                                    queue.add(new EarleyItem(item, t));
                        }
                    }
                }
            }
        }

        results.clear();
        for (EarleyItem item : states.get(0))
            if (item.finished(START))
                results.add(item.completeTop());
        lastStates = states;
        return ! truncated;
    }

    public void printStates(PrintWriter out) {
        if (lastStates != null)
            for (int i = 0; i < lastStates.size(); i++) {
                out.println("State " + i + ":");
                for (EarleyItem item : lastStates.get(i))
                    out.println(item.toString());
                out.println();
            }
    }
}
