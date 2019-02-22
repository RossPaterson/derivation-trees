import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** Statically computable properties of a grammar. */
public class GrammarProperties {
    private final Grammar grammar;
    private final Set<String> unreachable;
    private final Set<String> unrealizable;
    private final Set<String> nullable;
    private final Set<String> cyclic;

    public GrammarProperties(Grammar grammar) {
        this.grammar = grammar;
        this.unreachable = computeUnreachable();
        this.unrealizable = computeUnrealizable();
        this.nullable = computeNullable();
        this.cyclic = computeCyclic();
    }

    /** Nonterminals that cannot be reached from the start symbol. */
    public Set<String> getUnreachable() {
        return unreachable;
    }

    /** Nonterminals that do not generate any strings. */
    public Set<String> getUnrealizable() {
        return unrealizable;
    }

    /** Nonterminals that can generate the null string. */
    public Set<String> getNullable() {
        return nullable;
    }

    /** Nonterminals that can derive themselves. */
    public Set<String> getCyclic() {
        return cyclic;
    }

    /** Some strings have infinitely many derivations.
      This occurs if and only if a cyclic nonterminal is both reachable
      and realizable.
     */
    public boolean infinitelyAmbiguous() {
        for (String nt : cyclic)
            if (! unreachable.contains(nt) && ! unrealizable.contains(nt))
                return true;
        return false;
    }

    private final Set<String> computeUnreachable() {
        Set<String> reachable = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(grammar.getStart());
        while (! queue.isEmpty()) {
            String nt = queue.remove();
            if (! reachable.contains(nt)) {
                reachable.add(nt);
                for (ArrayList<String> rhs : grammar.expansions(nt))
                    for (String sym : rhs)
                        if (grammar.expansions(sym) != null)
                            queue.add(sym);
            }
        }
        return complement(reachable);
    }

    private final Set<String> computeUnrealizable() {
        Set<String> unrealizable = new HashSet<>(grammar.nonTerminals());
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : unrealizable) {
                boolean realizable = false;
                for (ArrayList<String> rhs : grammar.expansions(nt)) {
                    realizable = true;
                    for (String sym : rhs)
                        if (unrealizable.contains(sym)) {
                            realizable = false;
                            break;
                        }
                    if (realizable)
                        break;
                }
                if (realizable) {
                    unrealizable.remove(nt);
                    changed = true;
                    break;
                }
            }
        }
        return unrealizable;
    }

    private final Set<String> computeNullable() {
        Set<String> nullable = new HashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : grammar.nonTerminals())
                if (! nullable.contains(nt)) {
                    boolean empty = false;
                    for (ArrayList<String> rhs : grammar.expansions(nt)) {
                        empty = true;
                        for (String sym : rhs)
                            if (! nullable.contains(sym)) {
                                empty = false;
                                break;
                            }
                        if (empty)
                            break;
                    }
                    if (empty) {
                        nullable.add(nt);
                        changed = true;
                        break;
                    }
                }
        }
        return nullable;
    }

    private final Set<String> computeCyclic() {
        Map<String, Set<String>> trivialExpansion = new HashMap<>();
        for (String nt : grammar.nonTerminals()) {
            Set<String> s = new HashSet<String>();
            for (ArrayList<String> rhs : grammar.expansions(nt)) {
                int nonNullCount = 0;
                for (String sym : rhs)
                    if (! nullable.contains(sym))
                        nonNullCount++;
                if (nonNullCount == 0)
                    s.addAll(rhs);
                else if (nonNullCount == 1)
                    for (String sym : rhs)
                        if (grammar.expansions(sym) != null &&
                            ! nullable.contains(sym))
                            s.add(sym);
            }
            if (! s.isEmpty())
                trivialExpansion.put(nt, s);
        }

        // transitive closure
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : trivialExpansion.keySet()) {
                Set<String> exp = trivialExpansion.get(nt);
                ArrayList<String> expClone = new ArrayList<>(exp);
                for (String target : expClone)
                    if (trivialExpansion.containsKey(target))
                        exp.addAll(trivialExpansion.get(target));
                if (exp.size() > expClone.size())
                    changed = true;
            }
        }

        Set<String> cyclic = new HashSet<>();
        for (String nt : trivialExpansion.keySet())
            if (trivialExpansion.get(nt).contains(nt))
                cyclic.add(nt);
        return cyclic;
    }

    private final Set<String> complement(Set<String> s) {
        Set<String> rest = new HashSet<>();
        for (String nt : grammar.nonTerminals())
            if (! s.contains(nt))
                rest.add(nt);
        return rest;
    }

}
