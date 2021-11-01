import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class LinksProcessor {
    private final String URL;

    public LinksProcessor(String url) {
        this.URL = url;
    }

    private Document getHTML() {
        try {
            sleep(300);
            Document document = Jsoup.connect(URL)
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .referrer(URL)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .timeout(600000)
                    .get();
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public TreeSet<String> getLinks() throws Exception {

        String domain = getDomain();

        if (domain.equals("")) {
            CustomException e = new CustomException("Can't determine domain in getLinks method for url " + URL);
            throw e;
        }

        Elements links = Objects.requireNonNull(getHTML()).select("a[href]");
        String matchPath = "^[/](?:\\S[^\\#]+)";
        TreeSet<String> uniqueLinks = new TreeSet<>();

        for (Element e : links) {
            String path = e.attr("href");
            StringBuilder link = new StringBuilder();
            link.append(domain);
            link.append(path);
            if (path.matches(matchPath)) {
                uniqueLinks.add(link.toString());
            }
        }

        return  uniqueLinks;
    }

    private String getDomain(){
        Pattern pattern = Pattern.compile("^(https://[^/]+)");
        Matcher matcher = pattern.matcher(URL);
        if (matcher.find())
        {
            return matcher.group(1);
        } else {
            return "";
        }
    }

}