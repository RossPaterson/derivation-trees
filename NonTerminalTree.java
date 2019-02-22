import java.io.PrintWriter;
import java.util.Comparator;

public class NonTerminalTree extends ParseTree {
    private static final String SYMBOL_COLOUR = "#cc0000";
    private static final String LINE_COLOUR = "black";
    private static final String NULL_COLOUR = "#aaaaaa";
    private static final String NULL_SYMBOL = "&#x03B5;";

    private final String sym;
    private final Iterable<ParseTree> children;

    // derived values
    private final int ht;
    private final int wd;
    private final String sentence;

    public NonTerminalTree(String sym, Iterable<ParseTree> children) {
        this.sym = sym;
        this.children = children;

        int h = 1;
        int w = 0;
        for (ParseTree t : children) {
            h = Math.max(h, t.height());
            w = w + t.width();
        }
        ht = h+1;
        wd = Math.max(1, w);

        StringBuffer buff = new StringBuffer();
        addSentence(buff);
        sentence = buff.toString();
    }

    public int height() {
        return ht;
    }

    public int width() {
        return wd;
    }

    public String shortName() {
        return sym;
    }

    public String nonTerminal() {
        return sym;
    }

    protected final void addSentence(StringBuffer s) {
        for (ParseTree t : children)
            t.addSentence(s);
    }

    public boolean equals(Object obj) {
        if (obj instanceof NonTerminalTree) {
            NonTerminalTree o = (NonTerminalTree)obj;
            return sym.equals(o.sym) && children.equals(o.children);
        }
        return false;
    }

    public int hashCode() {
        return sym.hashCode()*7 + children.hashCode();
    }

    protected final int draw(SVG out, int x, int y, int levels) {
        int rx;
        final int ty = y + VSEP;
        int trx[] = new int[100];
        int n = 0;
        int tx = x;
        for (ParseTree t : children) {
            trx[n] = t.draw(out, tx, ty, levels-1);
            n++;
            tx = tx + t.width()*HSEP;
        }
        if (n == 0) {
            rx = x;
        } else {
            // rx is median of subtree root x-coordinates
            rx = (trx[(n-1)/2] + trx[n/2])/2;
        }
        out.text(rx, y, SYMBOL_COLOUR, sym);
        if (n == 0) {
            out.text(x, ty, NULL_COLOUR, NULL_SYMBOL);
            out.startLines(NULL_COLOUR);
            out.line(x, y+BOTTOM, x, ty-TOP);
            out.endLines();
        } else {
            out.startLines(LINE_COLOUR);
            tx = x;
            int i = 0;
            for (ParseTree t : children) {
                out.line(rx, y+BOTTOM, trx[i], ty-TOP);
                i++;
                tx = tx + t.width()*HSEP;
            }
            out.endLines();
        }
        return rx;
    }

    public void drawSVG(PrintWriter out) {
        SVG svg = new SVG(out);
        svg.startTag("svg");
        svg.attribute("width", width()*HSEP);
        svg.attribute("height", height()*VSEP + STRIP_HEIGHT);
        svg.attribute("xmlns", "http://www.w3.org/2000/svg");
        svg.attribute("version", "1.1");
        svg.attribute("font-family", "sans-serif");
        svg.attribute("font-size", 15);
        svg.closeBracket();
        svg.startTag("rect");
        svg.attribute("width", width()*HSEP);
        svg.attribute("height", height()*VSEP);
        svg.attribute("fill", "#fff7db");
        svg.closeEmpty();
        svg.startTag("rect");
        svg.attribute("y", height()*VSEP);
        svg.attribute("width", width()*HSEP);
        svg.attribute("height", STRIP_HEIGHT);
        svg.attribute("fill", "#f0e6bc");
        svg.closeEmpty();
        int rootX = draw(svg, HSEP/2, 30, height());
        svg.endTag("svg");
        out.println();
    }

    public static class Ascending implements Comparator<NonTerminalTree> {
        /** Trees are ordered first by length, then by generated sentence. */
        public final int compare(NonTerminalTree a, NonTerminalTree b) {
            if (a.sentence.length() != b.sentence.length())
                return a.sentence.length() - b.sentence.length();
            if (! a.sentence.equals(b.sentence))
                return a.sentence.compareTo(b.sentence);
            return a.height() - b.height();
        }
    }
}
