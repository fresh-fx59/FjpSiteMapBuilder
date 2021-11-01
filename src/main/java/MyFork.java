import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MyFork extends RecursiveTask<Map<String, HashSet<String>>> {

    private final String link2Parse;
    private final LinksProcessor linksProcessor;
    private final String PATH2FILE;
    private int spaceCount;
    private Map<String, HashSet<String>> result;
    private final int maxResultSize;
    private int resultSize;


    public Map<String, HashSet<String>> getResult() {
        return result;
    }

    public MyFork(String link2Parse, String path2File, Map<String, HashSet<String>> result, int spaceCount,
                  int maxResultSize, int resultSize) {
        this.PATH2FILE = path2File;
        this.link2Parse = link2Parse;
        this.spaceCount = spaceCount;
        this.result = result;
        linksProcessor = new LinksProcessor(link2Parse);
        this.maxResultSize = maxResultSize;
        this.resultSize = resultSize;
    }

    @Override
    protected Map<String, HashSet<String>> compute() {

        final String parentLink = link2Parse;

        while ((result.size() < maxResultSize) &&
                (result.size() != resultSize)
                ) {
            if (result.get(parentLink) != null)  {
                break;
            }
            HashSet<String> tempResult = new HashSet<>();
            try {
                tempResult.addAll(linksProcessor.getLinks());
            } catch (Exception e) {
                e.printStackTrace();
            }
            resultSize = result.size();
            result.put(parentLink, tempResult);

            List<MyFork> forks = createForksMultithread(new ArrayList<>(tempResult));
            forks.forEach(ForkJoinTask::join);
        }

        return result;
    }

    private List<MyFork> createForksMultithread(List<String> tempResult) {
        final int tempResultSize = tempResult.size();
        List<MyFork> forks = new ArrayList<>();
        final int threadsCount = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < tempResultSize; ) {
            int diff = i;
            for (int j = 0; j < threadsCount; j++) {
                diff = i + j;
                if (diff < tempResultSize) {
                    String link = tempResult.get(diff).trim();
                    forks.add(new MyFork(link, PATH2FILE, result, spaceCount, maxResultSize, resultSize));
                }
            }
            i = ++diff;
        }
        forks.forEach(ForkJoinTask::fork);

        return forks;
    }

}