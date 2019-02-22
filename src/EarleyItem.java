import java.util.ArrayList;

// scanning right to left
public class EarleyItem {
    private final String nt;
    private final Cons<ParseTree> parsed;
    private final ArrayList<String> rhs;
    private final int pos; // position in rhs
    private final int finish;
    private final int cachedHash;

    // item at end of a rhs
    public EarleyItem(String nt, ArrayList<String> rhs, int finish) {
        this.nt = nt;
        this.parsed = null;
        this.rhs = rhs;
        this.pos = rhs.size();
        this.finish = finish;
        this.cachedHash = realHashCode();
    }

    // advance of item
    public EarleyItem(EarleyItem prev, ParseTree t) {
        if (prev.finished())
            throw new IllegalArgumentException("advancing at end");
        nt = prev.nt;
        parsed = new Cons<ParseTree>(t, prev.parsed);
        rhs = prev.rhs;
        pos = prev.pos - 1;
        finish = prev.finish;
        this.cachedHash = realHashCode();
    }

    public boolean equals(Object obj) {
        EarleyItem o = (EarleyItem)obj;
        return o != null &&
            finish == o.finish && pos == o.pos && nt.equals(o.nt) &&
            rhs.equals(o.rhs) &&
            (parsed == null ? o.parsed == null : parsed.equals(o.parsed));
    }

    public int hashCode() {
        return cachedHash;
    }

    private int realHashCode() {
        return 13*finish + 19*pos + 23*nt.hashCode() +
            29*rhs.hashCode() + (parsed == null ? 1 : 37*parsed.hashCode());
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append('(').append(nt).append(" -> ");
        for (int i = 0; i < pos; i++)
            s.append(rhs.get(i));
        s.append('.');
        for (ParseTree t : Cons.iterable(parsed))
            s.append(t.shortName());
        s.append(", ");
        s.append(finish);
        s.append(')');
        return s.toString();
    }

    public boolean finished() {
        return pos == 0;
    }

    public boolean finished(String nt) {
        return pos == 0 && this.nt.equals(nt);
    }

    public boolean match(String sym) {
        return pos > 0 && rhs.get(pos-1).equals(sym);
    }

    public String current() {
        if (finished())
            throw new IllegalStateException("current at end");
        return rhs.get(pos-1);
    }

    public NonTerminalTree complete() {
        if (! finished())
            throw new IllegalStateException("not complete");
        return new NonTerminalTree(nt, Cons.iterable(parsed));
    }

    public int start() {
        return finish;
    }

    public NonTerminalTree completeTop() {
        if (! finished())
            throw new IllegalStateException("current at end");
        if (parsed == null)
            throw new IllegalStateException("null list");
        if (parsed.tail != null)
            throw new IllegalStateException("non-singleton list");
        return (NonTerminalTree)parsed.head;
    }
}
