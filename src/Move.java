public class Move {
    private final int encoded;

    public Move(int from, int over, int to) {
        this.encoded = (from << 8) | (over << 4) | to;
    }

    public Move(int encoded) {
        this.encoded = encoded;
    }

    public int getEncoded() {
        return encoded;
    }

    public int getFrom() {
        return (encoded >> 8) & 0xF;
    }

    public int getOver() {
        return (encoded >> 4) & 0xF;
    }

    public int getTo() {
        return encoded & 0xF;
    }

    @Override
    public String toString() {
        return String.format("Move(from=%d, over=%d, to=%d)", getFrom(), getOver(), getTo());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Move && this.encoded == ((Move) obj).encoded;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(encoded);
    }
}
