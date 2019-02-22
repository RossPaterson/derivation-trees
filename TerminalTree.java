import java.io.PrintWriter;
import java.util.ArrayList;

public class TerminalTree extends ParseTree {
    private static final String SYMBOL_COLOUR = "#0000cc";
    private static final String LINE_COLOUR = "#dddddd";

    private final String sym;

    public TerminalTree(String s) {
        sym = s;
    }

    public int height() {
        return 1;
    }

    public int width() {
        return 1;
    }

    public String shortName() {
        return sym;
    }

    public boolean equals(Object obj) {
        if (obj instanceof TerminalTree) {
            TerminalTree o = (TerminalTree)obj;
            return sym.equals(o.sym);
        }
        return false;
    }

    public int hashCode() {
        return sym.hashCode();
    }

    protected final int draw(SVG out, int x, int y, int levels) {
        // at current position in tree
        out.text(x, y, SYMBOL_COLOUR, sym);
        // directly below that and outside the box
        int ly = y + levels*VSEP - 10;
        out.text(x, ly, SYMBOL_COLOUR, sym);

        // grey line connecting them
        out.startLines(LINE_COLOUR);
        out.line(x, y + BOTTOM, x, ly - TOP);
        out.endLines();
        return x;
    }

    protected final void addSentence(StringBuffer s) {
        if (s.length() > 0)
            s.append(' ');
        s.append(sym);
    }
}
