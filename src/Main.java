import MTT.GetHomepageFeedsTabListsRsp;
import taf.JceResolve;

import java.io.File;
import java.io.FileInputStream;

public class Main {
    static public void main(String[] argv) throws Exception
    {
        File file = new File("datas");
        FileInputStream inputStream = new FileInputStream(file);
        byte [] bytes = new byte[(int)file.length()];
        inputStream.read(bytes);
        GetHomepageFeedsTabListsRsp HomePage = (GetHomepageFeedsTabListsRsp)JceResolve.Resolve(new GetHomepageFeedsTabListsRsp(),bytes);

        for(int i = 0 ; i<HomePage.c.size();i++){
            System.out.println((HomePage.c.get(i)).e);
        }

    }
}
