package MTT;
import taf.*;

public final class HomepageFeedsItemData extends JceStruct {
    static byte[] j = new byte[1];
    public String a = "";
    public int b = 0;
    public int c = 0;
    public byte[] d = null;
    public String e = "";
    public String f = "";
    public String g = "";
    public int h = 0;
    public boolean i = false;

    static {
        j[0] = (byte) 0;
    }

    public void readFrom(JceInputStream jceInputStream) {
        this.a = jceInputStream.readString(0, true);
        this.b = jceInputStream.read(this.b, 1, true);
        this.c = jceInputStream.read(this.c, 2, true);
        this.d = jceInputStream.read(j, 3, true);
        this.e = jceInputStream.readString(4, false);
        this.f = jceInputStream.readString(5, false);
        this.g = jceInputStream.readString(7, false);
        this.h = jceInputStream.read(this.h, 8, false);
        this.i = jceInputStream.read(this.i, 9, false);
    }

    public void writeTo(JceOutputStream jceOutputStream) {
        jceOutputStream.write(this.a, 0);
        jceOutputStream.write(this.b, 1);
        jceOutputStream.write(this.c, 2);
        jceOutputStream.write(this.d, 3);
        if (this.e != null) {
            jceOutputStream.write(this.e, 4);
        }
        if (this.f != null) {
            jceOutputStream.write(this.f, 5);
        }
        if (this.g != null) {
            jceOutputStream.write(this.g, 7);
        }
        jceOutputStream.write(this.h, 8);
        jceOutputStream.write(this.i, 9);
    }
}
