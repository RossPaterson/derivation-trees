import java.util.Iterator;

/** Simple immutable lists. */
public class Cons<T> {
    public final T head;
    public final Cons<T> tail;

    public Cons(T head, Cons<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    public boolean equals(Object obj) {
        Cons<?> o = (Cons<?>)obj;
        return o != null && head.equals(o.head) &&
            (tail == null ? o.tail == null : tail.equals(o.tail));
    }

    public int hashCode() {
        return head.hashCode() + 31*(tail == null ? 1 : tail.hashCode());
    }

    private static class ConsIterator<T> implements Iterator<T> {
        private Cons<T> list;

        public ConsIterator(Cons<T> list) {
            this.list = list;
        }

        public boolean hasNext() {
            return list != null;
        }

        public T next() {
            T x = list.head;
            list = list.tail;
            return x;
        }
    }

    private static class ConsIterable<T> implements Iterable<T> {
        private final Cons<T> list;

        public ConsIterable(Cons<T> list) {
            this.list = list;
        }

        public boolean equals(Object obj) {
            ConsIterable<?> o = (ConsIterable<?>)obj;
            return o != null &&
                (list == null ? o.list == null : list.equals(o.list));
        }

        public int hashCode() {
            return list == null ? 1 : list.hashCode();
        }

        public Iterator<T> iterator() {
            return new ConsIterator<T>(list);
        }
    }

    public static <T> Iterable<T> iterable(final Cons<T> list) {
        return new ConsIterable<T>(list);
    }
}
