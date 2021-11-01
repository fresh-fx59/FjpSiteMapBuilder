import java.util.HashSet;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        final String URL = "https://lenta.ru/";
        final String PATH = "/Users/a/Desktop/JAVA/github/FjpSiteMapBuilder/src/main/Data/";
        final String PATH2FILE = PATH + "links.txt";//unique links with indents
        final String PATH2FILEINITIAL = PATH + "linksInitial.txt";//all links
        final int MAXRESULTSIZE = 20;//relative variable, not links count. Used to limit result size on big sites

        MyPool pool = new MyPool(URL, PATH2FILE, MAXRESULTSIZE);
        Map<String, HashSet<String>> linksFromSite = pool.returnLinks();
        FileProcessor fileProcessor = new FileProcessor(PATH2FILE, PATH2FILEINITIAL, URL, linksFromSite);
        fileProcessor.addLinksToFileLinksInitial();
        fileProcessor.addLinksToFileFinal();

    }
}
