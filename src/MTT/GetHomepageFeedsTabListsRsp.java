package MTT;

import taf.JceInputStream;
import taf.JceOutputStream;
import taf.JceStruct;
import java.util.ArrayList;

public final class GetHomepageFeedsTabListsRsp extends JceStruct {
    static ArrayList<HomepageFeedsItemData> h = new ArrayList();
    public int a = 0;
    public int b = 0;
    public ArrayList<HomepageFeedsItemData> c = null;
    public boolean d = false;
    public boolean e = false;
    public int f = 0;
    public boolean g = false;

    static {
        h.add(new HomepageFeedsItemData());
    }

    public void readFrom(JceInputStream jceInputStream) {
        this.a = jceInputStream.read(this.a, 0, true);
        this.b = jceInputStream.read(this.b, 1, true);
        this.c = (ArrayList) jceInputStream.read(h, 2, true);
        this.d = jceInputStream.read(this.d, 3, false);
        this.e = jceInputStream.read(this.e, 8, false);
        this.f = jceInputStream.read(this.f, 9, false);
        this.g = jceInputStream.read(this.g, 10, false);
    }

    public void writeTo(JceOutputStream jceOutputStream) {
        jceOutputStream.write(this.a, 0);
        jceOutputStream.write(this.b, 1);
        jceOutputStream.write(this.c, 2);
        jceOutputStream.write(this.d, 3);
        jceOutputStream.write(this.e, 8);
        jceOutputStream.write(this.f, 9);
        jceOutputStream.write(this.g, 10);
    }
}
