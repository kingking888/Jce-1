package taf;

public class JceSynchronizedPool {
    public static JceSynchronizedPool sInstance = null;
    private int accCountOut = 0;
    private int accCountOutHit = 0;
    private int mMaxPoolSize = 0;
    private int mOutputBufferNumber = 0;
    private final JceOutputStream[] mOutputPool;
    public int mOutputPoolAvailableSize;

    private JceSynchronizedPool(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("The max pool size must be > 0");
        }
        this.mMaxPoolSize = i;
        this.mOutputPool = new JceOutputStream[i];
    }

    private JceOutputStream getAOutBuffer() {
        int i = this.mOutputPoolAvailableSize - 1;
        JceOutputStream jceOutputStream = this.mOutputPool[i];
        this.mOutputPool[i] = null;
        this.mOutputPoolAvailableSize--;
        return jceOutputStream;
    }

    public static synchronized JceSynchronizedPool getInstance() {
        JceSynchronizedPool jceSynchronizedPool;
        synchronized (JceSynchronizedPool.class) {
            if (sInstance == null) {
                sInstance = new JceSynchronizedPool(4);
            }
            jceSynchronizedPool = sInstance;
        }
        return jceSynchronizedPool;
    }

    private boolean isInOutPool(JceOutputStream jceOutputStream) {
        for (int i = 0; i < this.mOutputPoolAvailableSize; i++) {
            if (this.mOutputPool[i] == jceOutputStream) {
                return true;
            }
        }
        return false;
    }

    public JceOutputStream acquireout() {
        JceOutputStream aOutBuffer;
        synchronized (this.mOutputPool) {
            if (this.accCountOut == 0 || this.accCountOut % 20 == 0) {
            }
            this.accCountOut++;
            if (this.mOutputPoolAvailableSize > 0) {
                this.accCountOutHit++;
                aOutBuffer = getAOutBuffer();
            } else if (this.mOutputBufferNumber < this.mMaxPoolSize) {
                this.accCountOutHit++;
                this.mOutputPool[this.mOutputPoolAvailableSize] = new JceOutputStream();
                this.mOutputPoolAvailableSize++;
                this.mOutputBufferNumber++;
                aOutBuffer = getAOutBuffer();
            } else {
                aOutBuffer = new JceOutputStream();
            }
        }
        return aOutBuffer;
    }

    public boolean releaseOut(JceOutputStream jceOutputStream) {
        synchronized (this.mOutputPool) {
            if (isInOutPool(jceOutputStream)) {
                throw new IllegalStateException("Already in the pool!");
            } else if (jceOutputStream.getByteBuffer().capacity() > 65536) {
            } else if (this.mOutputPoolAvailableSize < this.mOutputPool.length) {
                jceOutputStream.reInit();
                this.mOutputPool[this.mOutputPoolAvailableSize] = jceOutputStream;
                this.mOutputPoolAvailableSize++;
            }
        }
        return true;
    }
}