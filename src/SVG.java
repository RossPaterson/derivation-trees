import java.io.PrintWriter;

public class SVG {
    private final PrintWriter out;

    public SVG(PrintWriter out) {
        this.out = out;
    }

    public void startTag(String tag) {
        out.print("<" + tag);
    }

    public void attribute(String name, int value) {
        out.print(" " + name + "=\"" + value + "\"");
    }

    public void attribute(String name, String value) {
        out.print(" " + name + "=\"" + value + "\"");
    }

    public void closeBracket() {
        out.print(">");
    }

    public void closeEmpty() {
        out.print("/>");
    }

    public void endTag(String tag) {
        out.print("</" + tag + ">");
    }

    public void startLines(String colour) {
        startTag("g");
        attribute("stroke", colour);
        attribute("stroke-width", 1);
        attribute("stroke-linecap", "round");
        closeBracket();
    }

    public void endLines() {
        endTag("g");
    }

    public void line(int x1, int y1, int x2, int y2) {
        startTag("line");
        attribute("x1", x1);
        attribute("y1", y1);
        attribute("x2", x2);
        attribute("y2", y2);
        closeEmpty();
    }

    public void text(int x, int y, String colour, String s) {
        startTag("text");
        attribute("x", x);
        attribute("y", y);
        attribute("text-anchor", "middle");
        attribute("fill", colour);
        closeBracket();
        out.print(s);
        endTag("text");
    }
}
