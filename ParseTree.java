import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class ParseTree {
    protected final static int HSEP = 30;
    protected final static int VSEP = 45;
    protected final static int STRIP_HEIGHT = 30;
    protected final static int TOP = 15;
    protected final static int BOTTOM = 5;

    public abstract int height();
    public abstract int width();
    public abstract String shortName();

    protected abstract void addSentence(StringBuffer s);

    protected abstract int draw(SVG out, int x, int y, int levels);
}
