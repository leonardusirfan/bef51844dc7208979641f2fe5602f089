package id.net.gmedia.selby.Keranjang;

public abstract class BaseListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_DIVIDER = 2;

    abstract public int getType();
}
