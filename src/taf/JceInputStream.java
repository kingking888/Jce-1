package taf;

import taf.HexUtil;
import taf.JceStruct;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class JceInputStream {
    private ByteBuffer bs;
    protected String sServerEncoding = "GBK";

    public static class HeadData {
        public int tag;
        public byte type;

        public void clear() {
            this.type = (byte) 0;
            this.tag = 0;
        }
    }

    public JceInputStream(ByteBuffer byteBuffer) {
        this.bs = ByteBuffer.wrap(byteBuffer.array(), 0, byteBuffer.position());
    }

    public JceInputStream(byte[] bArr) {
        this.bs = ByteBuffer.wrap(bArr);
    }

    public JceInputStream(byte[] bArr, int i) {
        this.bs = ByteBuffer.wrap(bArr);
        this.bs.position(i);
    }

    private int peakHead(HeadData headData) {
        return readHead(headData, this.bs.duplicate());
    }

    private <T> T[] readArrayImpl(T t, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read < 0) {
                        throw new JceDecodeException("size invalid: " + read);
                    }
                    Object[] objArr = (Object[]) Array.newInstance(t.getClass(), read);
                    for (int i2 = 0; i2 < read; i2++) {
                        objArr[i2] = read((Object) t, 0, true);
                    }
                    return (T[]) objArr;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return null;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public static int readHead(HeadData headData, ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        headData.type = (byte) (b & 15);//0xF ???4????????????
        headData.tag = (b & 240) >> 4;//???4??????tag,
        if (headData.tag != 15) {//??????tag???15 ?????????????????????
            return 1;
        }
        headData.tag = byteBuffer.get() & 255;
        return 2;
    }

    private <K, V> Map<K, V> readMap(Map<K, V> map, Map<K, V> map2, int i, boolean z) {
        if (map2 == null || map2.isEmpty()) {
            return new HashMap();
        }
        Entry entry = (Entry) map2.entrySet().iterator().next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 8:
                    int read = read(0, 0, true);
                    if (read < 0) {
                        throw new JceDecodeException("size invalid: " + read);
                    }
                    for (int i2 = 0; i2 < read; i2++) {
                        map.put((K)read(key, 0, true), (V)read(value, 1, true));
                    }
                    return map;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return map;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    private void skip(int i) {
        this.bs.position(this.bs.position() + i);
    }

    private void skipField() {
        HeadData headData = new HeadData();
        readHead(headData);
        skipField(headData.type);
    }

    private void skipField(byte b) {
        int i = 0;
        int read;
        switch (b) {
            case (byte) 0:
                skip(1);
                return;
            case (byte) 1:
                skip(2);
                return;
            case (byte) 2:
                skip(4);
                return;
            case (byte) 3:
                skip(8);
                return;
            case (byte) 4:
                skip(4);
                return;
            case (byte) 5:
                skip(8);
                return;
            case (byte) 6:
                i = this.bs.get();
                if (i < 0) {
                    i += 256;
                }
                skip(i);
                return;
            case (byte) 7:
                skip(this.bs.getInt());
                return;
            case (byte) 8:
                read = read(0, 0, true);
                while (i < read * 2) {
                    skipField();
                    i++;
                }
                return;
            case (byte) 9:
                read = read(0, 0, true);
                while (i < read) {
                    skipField();
                    i++;
                }
                return;
            case (byte) 10:
                skipToStructEnd();
                return;
            case (byte) 11:
            case (byte) 12:
                return;
            case (byte) 13:
                HeadData headData = new HeadData();
                readHead(headData);
                if (headData.type != (byte) 0) {
                    throw new JceDecodeException("skipField with invalid type, type value: " + b + ", " + headData.type);
                }
                skip(read(0, 0, true));
                return;
            default:
                throw new JceDecodeException("invalid type.");
        }
    }

    /* Access modifiers changed, original: 0000 */
    public ByteBuffer getBs() {
        return this.bs;
    }

    public void reInit() {
        this.sServerEncoding = "GBK";
        this.bs.clear();
    }

    public byte read(byte b, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 0:
                    return this.bs.get();
                case (byte) 12:
                    return (byte) 0;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return b;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public double read(double d, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 4:
                    return (double) this.bs.getFloat();
                case (byte) 5:
                    return this.bs.getDouble();
                case (byte) 12:
                    return 0.0d;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return d;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public float read(float f, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 4:
                    return this.bs.getFloat();
                case (byte) 12:
                    return 0.0f;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return f;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public int read(int i, int i2, boolean z) {
        if (skipToTag(i2)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 0:
                    return this.bs.get();
                case (byte) 1:
                    return this.bs.getShort();
                case (byte) 2:
                    return this.bs.getInt();
                case (byte) 12:
                    return 0;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return i;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public long read(long j, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 0:
                    return (long) this.bs.get();
                case (byte) 1:
                    return (long) this.bs.getShort();
                case (byte) 2:
                    return (long) this.bs.getInt();
                case (byte) 3:
                    return this.bs.getLong();
                case (byte) 12:
                    return 0;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return j;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    /**
     *
     * @param jceStruct
     * @param i:tag
     * @param z:????????????
     * @return
     */
    public JceStruct read(JceStruct jceStruct, int i, boolean z) {
        JceStruct jceStruct2;
        if (skipToTag(i)) {
            try {
                jceStruct2 = (JceStruct) jceStruct.getClass().newInstance();
                if (jceStruct2 == null) {
                    return null;
                }
                HeadData headData = new HeadData();
                readHead(headData);
                if (headData.type != (byte) 10) {
                    throw new JceDecodeException("type mismatch.");
                }
                jceStruct2.readFrom(this);
                skipToStructEnd();
            } catch (Exception e) {
                throw new JceDecodeException(e.getMessage());
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        } else {
            jceStruct2 = null;
        }
        return jceStruct2;
    }

    public <T> Object read(T t, int i, boolean z) {
        if (t instanceof Byte) {
            return Byte.valueOf(read((byte) 0, i, z));
        }
        if (t instanceof Boolean) {
            return Boolean.valueOf(read(false, i, z));
        }
        if (t instanceof Short) {
            return Short.valueOf(read((short) 0, i, z));
        }
        if (t instanceof Integer) {
            return Integer.valueOf(read(0, i, z));
        }
        if (t instanceof Long) {
            return Long.valueOf(read(0L, i, z));
        }
        if (t instanceof Float) {
            return Float.valueOf(read(0.0f, i, z));
        }
        if (t instanceof Double) {
            return Double.valueOf(read(0.0d, i, z));
        }
        if (t instanceof String) {
            return readString(i, z);
        }
        if (t instanceof Map) {
            return readMap((Map) t, i, z);
        }
        if (t instanceof List) {
            return readArray((List) t, i, z);
        }
        if (t instanceof JceStruct) {
            return read((JceStruct) t, i, z);
        }
        if (t.getClass().isArray()) {
            return ((t instanceof byte[]) || (t instanceof Byte[])) ? read((byte[]) null, i, z) : t instanceof boolean[] ? read((boolean[]) null, i, z) : t instanceof short[] ? read((short[]) null, i, z) : t instanceof int[] ? read((int[]) null, i, z) : t instanceof long[] ? read((long[]) null, i, z) : t instanceof float[] ? read((float[]) null, i, z) : t instanceof double[] ? read((double[]) null, i, z) : readArray((Object[]) t, i, z);
        } else {
            throw new JceDecodeException("read object error: unsupport type.");
        }
    }

    public String read(String str, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            int i2;
            byte[] bArr;
            switch (headData.type) {
                case (byte) 6:
                    i2 = this.bs.get();
                    if (i2 < 0) {
                        i2 += 256;
                    }
                    bArr = new byte[i2];
                    this.bs.get(bArr);
                    try {
                        return new String(bArr, this.sServerEncoding);
                    } catch (UnsupportedEncodingException e) {
                        return new String(bArr);
                    }
                case (byte) 7:
                    i2 = this.bs.getInt();
                    if (i2 > 104857600 || i2 < 0 || i2 > this.bs.capacity()) {
                        throw new JceDecodeException("String too long: " + i2);
                    }
                    bArr = new byte[i2];
                    this.bs.get(bArr);
                    try {
                        return new String(bArr, this.sServerEncoding);
                    } catch (UnsupportedEncodingException e2) {
                        return new String(bArr);
                    }
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return str;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public short read(short s, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 0:
                    return (short) this.bs.get();
                case (byte) 1:
                    return this.bs.getShort();
                case (byte) 12:
                    return (short) 0;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return s;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public boolean read(boolean z, int i, boolean z2) {
        return read((byte) 0, i, z2) != (byte) 0;
    }

    public byte[] read(byte[] bArr, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            byte[] bArr2;
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read < 0 || read > this.bs.capacity()) {
                        throw new JceDecodeException("size invalid: " + read);
                    }
                    bArr2 = new byte[read];
                    for (int i2 = 0; i2 < read; i2++) {
                        bArr2[i2] = read(bArr2[0], 0, true);
                    }
                    return bArr2;
                case (byte) 13:
                    HeadData headData2 = new HeadData();
                    readHead(headData2);
                    if (headData2.type != (byte) 0) {
                        throw new JceDecodeException("type mismatch, tag: " + i + ", type: " + headData.type + ", " + headData2.type);
                    }
                    int read2 = read(0, 0, true);
                    if (read2 < 0 || read2 > this.bs.capacity()) {
                        throw new JceDecodeException("invalid size, tag: " + i + ", type: " + headData.type + ", " + headData2.type + ", size: " + read2);
                    }
                    bArr2 = new byte[read2];
                    this.bs.get(bArr2);
                    return bArr2;
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return null;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public double[] read(double[] dArr, int i, boolean z) {
        double[] dArr2 = null;
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        dArr2 = new double[read];
                        for (int i2 = 0; i2 < read; i2++) {
                            dArr2[i2] = read(dArr2[0], 0, true);
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return dArr2;
    }

    public float[] read(float[] fArr, int i, boolean z) {
        float[] fArr2 = null;
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        fArr2 = new float[read];
                        for (int i2 = 0; i2 < read; i2++) {
                            fArr2[i2] = read(fArr2[0], 0, true);
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return fArr2;
    }

    public int[] read(int[] iArr, int i, boolean z) {
        int[] iArr2 = null;
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        iArr2 = new int[read];
                        for (int i2 = 0; i2 < read; i2++) {
                            iArr2[i2] = read(iArr2[0], 0, true);
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return iArr2;
    }

    public long[] read(long[] jArr, int i, boolean z) {
        long[] jArr2 = null;
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        jArr2 = new long[read];
                        for (int i2 = 0; i2 < read; i2++) {
                            jArr2[i2] = read(jArr2[0], 0, true);
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return jArr2;
    }

    public JceStruct[] read(JceStruct[] jceStructArr, int i, boolean z) {
        return (JceStruct[]) readArray((Object[]) jceStructArr, i, z);
    }

    public String[] read(String[] strArr, int i, boolean z) {
        return (String[]) readArray((Object[]) strArr, i, z);
    }

    public short[] read(short[] sArr, int i, boolean z) {
        short[] sArr2 = null;
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        sArr2 = new short[read];
                        for (int i2 = 0; i2 < read; i2++) {
                            sArr2[i2] = read(sArr2[0], 0, true);
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return sArr2;
    }

    public boolean[] read(boolean[] zArr, int i, boolean z) {
        boolean[] zArr2 = null;
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        zArr2 = new boolean[read];
                        for (int i2 = 0; i2 < read; i2++) {
                            zArr2[i2] = read(zArr2[0], 0, true);
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return zArr2;
    }

    public <T> List<T> readArray(List<T> list, int i, boolean z) {
        int i2 = 0;
        if (list == null || list.isEmpty()) {
            return new ArrayList();
        }
        Object[] readArrayImpl = readArrayImpl(list.get(0), i, z);
        if (readArrayImpl == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        while (i2 < readArrayImpl.length) {
            arrayList.add(readArrayImpl[i2]);
            i2++;
        }
        return arrayList;
    }

    public <T> T[] readArray(T[] tArr, int i, boolean z) {
        if (tArr != null && tArr.length != 0) {
            return readArrayImpl(tArr[0], i, z);
        }
        throw new JceDecodeException("unable to get type of key and value.");
    }

    public String readByteString(String str, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            int i2;
            byte[] bArr;
            switch (headData.type) {
                case (byte) 6:
                    i2 = this.bs.get();
                    if (i2 < 0) {
                        i2 += 256;
                    }
                    bArr = new byte[i2];
                    this.bs.get(bArr);
                    return HexUtil.bytes2HexStr(bArr);
                case (byte) 7:
                    i2 = this.bs.getInt();
                    if (i2 > 104857600 || i2 < 0 || i2 > this.bs.capacity()) {
                        throw new JceDecodeException("String too long: " + i2);
                    }
                    bArr = new byte[i2];
                    this.bs.get(bArr);
                    return HexUtil.bytes2HexStr(bArr);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return str;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public void readHead(HeadData headData) {
        readHead(headData, this.bs);
    }

    public List readList(int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 9:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        for (int i2 = 0; i2 < read; i2++) {
                            headData = new HeadData();
                            readHead(headData);
                            switch (headData.type) {
                                case (byte) 0:
                                    skip(1);
                                    break;
                                case (byte) 1:
                                    skip(2);
                                    break;
                                case (byte) 2:
                                    skip(4);
                                    break;
                                case (byte) 3:
                                    skip(8);
                                    break;
                                case (byte) 4:
                                    skip(4);
                                    break;
                                case (byte) 5:
                                    skip(8);
                                    break;
                                case (byte) 6:
                                    int i3 = this.bs.get();
                                    if (i3 < 0) {
                                        i3 += 256;
                                    }
                                    skip(i3);
                                    break;
                                case (byte) 7:
                                    skip(this.bs.getInt());
                                    break;
                                case (byte) 8:
                                case (byte) 9:
                                    break;
                                case (byte) 10:
                                    try {
                                        JceStruct jceStruct = (JceStruct) Class.forName(JceStruct.class.getName()).getConstructor(new Class[0]).newInstance(new Object[0]);
                                        jceStruct.readFrom(this);
                                        skipToStructEnd();
                                        arrayList.add(jceStruct);
                                        break;
                                    } catch (Exception e) {
                                        throw new JceDecodeException("type mismatch." + e);
                                    }
                                case (byte) 12:
                                    arrayList.add(new Integer(0));
                                    break;
                                default:
                                    throw new JceDecodeException("type mismatch.");
                            }
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return arrayList;
    }

    public <K, V> HashMap<K, V> readMap(Map<K, V> map, int i, boolean z) {
        return (HashMap) readMap(new HashMap(), map, i, z);
    }

    public String readString(int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            int i2;
            byte[] bArr;
            switch (headData.type) {
                case (byte) 6:
                    i2 = this.bs.get();
                    if (i2 < 0) {
                        i2 += 256;
                    }
                    bArr = new byte[i2];
                    this.bs.get(bArr);
                    try {
                        return new String(bArr, this.sServerEncoding);
                    } catch (UnsupportedEncodingException e) {
                        return new String(bArr);
                    }
                case (byte) 7:
                    i2 = this.bs.getInt();
                    if (i2 > 104857600 || i2 < 0 || i2 > this.bs.capacity()) {
                        throw new JceDecodeException("String too long: " + i2);
                    }
                    bArr = new byte[i2];
                    this.bs.get(bArr);
                    try {
                        return new String(bArr, this.sServerEncoding);
                    } catch (UnsupportedEncodingException e2) {
                        return new String(bArr);
                    }
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (!z) {
            return null;
        } else {
            throw new JceDecodeException("require field not exist.");
        }
    }

    public Map<String, String> readStringMap(int i, boolean z) {
        HashMap hashMap = new HashMap();
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            switch (headData.type) {
                case (byte) 8:
                    int read = read(0, 0, true);
                    if (read >= 0) {
                        for (int i2 = 0; i2 < read; i2++) {
                            hashMap.put(readString(0, true), readString(1, true));
                        }
                        break;
                    }
                    throw new JceDecodeException("size invalid: " + read);
                default:
                    throw new JceDecodeException("type mismatch.");
            }
        } else if (z) {
            throw new JceDecodeException("require field not exist.");
        }
        return hashMap;
    }

    public int setServerEncoding(String str) {
        this.sServerEncoding = str;
        return 0;
    }

    public void skipToStructEnd() {
        HeadData headData = new HeadData();
        do {
            readHead(headData);
            skipField(headData.type);
        } while (headData.type != (byte) 11);
    }

//    public boolean skipToTag(int tag) {
//        try {
//            HeadData headData = new HeadData();
//            while (true) {
//                int len = peakHead(headData);
//                if (tag > headData.tag && headData.type != (byte) 11) {
//                    skip(len);
//                    skipField(headData.type);
//                }
//            }
//            return headData.type != (byte) 11 && tag == headData.tag;
//        } catch (JceDecodeException | BufferUnderflowException e) {
//            return false;
//        }
//    }

    /**
     *?????????byteBuffer tag??????
     * @param tag
     * @return
     */
    public boolean skipToTag(int tag) {
        try {
            HeadData headData = new HeadData();

            while (true) {
                int len =peakHead(headData);
                if(tag > headData.tag && headData.type != 0xb) {
                    skip(len);
                    skipField(headData.type);
                }
                else
                {
                    break;
                }
            }

            if(headData.type == 0xb || tag != headData.tag)
            {
                return false;
            }
            return true;
        } catch (JceDecodeException | BufferUnderflowException e) {
            return false;
        }
}

    public void warp(byte[] bArr) {
        wrap(bArr);
    }

    public void wrap(byte[] bArr) {
        this.bs = ByteBuffer.wrap(bArr);
    }

}