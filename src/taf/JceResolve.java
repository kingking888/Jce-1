package taf;

public class JceResolve {
    public static Object Resolve(Object obj,byte[] array)
    {
        JceInputStream ji = new JceInputStream(array);
        ji.setServerEncoding("UTF-8");
        return  ji.read(obj,0,true);
    }
}
