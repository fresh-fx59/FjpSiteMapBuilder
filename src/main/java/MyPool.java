import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class MyPool {
    private final String URL;
    private final String PATH2FILE;
    private Map<String, HashSet<String>> result = Collections.synchronizedMap(new HashMap<>());
    private final int MAXRESULTSIZE;

    public MyPool(String linksToParse, String path2File, int maxResultSize) {
        this.URL = linksToParse;
        this.PATH2FILE = path2File;
        this.MAXRESULTSIZE = maxResultSize;
    }

    public Map<String, HashSet<String>> returnLinks() {
        ForkJoinPool pool = new ForkJoinPool();
        MyFork myFork = new MyFork(URL, PATH2FILE, result, 0, MAXRESULTSIZE, -1);
        pool.invoke(myFork);
        Map<String, HashSet<String>> result = myFork.getResult();
        pool.shutdown();
        return result;
    }
}
