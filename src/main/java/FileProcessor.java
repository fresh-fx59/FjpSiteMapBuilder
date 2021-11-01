import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileProcessor {
    private final String PATH;
    private final String PATH2FILEINITIAL;
    private final String ROOTLINK;
    private String spaces = "";
    private int spaceCount = 0;
    private List<String> formattedResult = new ArrayList<>();
    private HashSet<String> linksAdded = new HashSet<>();
    private final Map<String, HashSet<String>> INITIALLINKS;

    public FileProcessor(String PATH, String path2FileInitial, String rootLink, Map<String, HashSet<String>> initialLinks) {
        this.PATH = PATH;
        this.PATH2FILEINITIAL = path2FileInitial;
        this.ROOTLINK = rootLink;
        this.INITIALLINKS = initialLinks;
    }

    public synchronized long getLines() {
        Path path = Paths.get(PATH);

        long lines = 0;
        try {
            lines = Files.lines(path).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public synchronized void addLinksToFileLinksInitial () {
        try {
            FileWriter fw = new FileWriter(PATH2FILEINITIAL, true);
            BufferedWriter bw = new BufferedWriter(fw);
            INITIALLINKS.forEach((hash, map) -> {
                try {
                    bw.write(hash + "\n");
                    map.forEach(m -> {
                        try {
                            bw.write(m + "\n");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addLinks(List<String> links) {
        try {
            FileWriter fw = new FileWriter(PATH, true);
            BufferedWriter bw = new BufferedWriter(fw);
            links.forEach(str -> {
                try {
                    bw.write(str + "\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean hasLink(String link) {
        TreeSet<String> links = new TreeSet<>();
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(PATH));
            for (;;) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else {
                    links.add(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return links.contains(link);
    }

    public void addLinksToFileFinal() {
        try {
            Files.write(Paths.get(PATH), formatResult(ROOTLINK));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private List<String> formatResult(String link) {
        HashSet<String> temp = INITIALLINKS.get(link);
        spaces = " ".repeat(Math.max(0, spaceCount));
        String spacesMinus1 = " ".repeat(Math.max(0, spaceCount - 1));
        spaceCount++;

        if (temp != null) {
            if (formattedResult.size() == 0) {
                formattedResult.add(spacesMinus1 + link);
            }
            temp.forEach(l -> {
                if (!linksAdded.contains(l)) {
                    linksAdded.add(l);
                    formattedResult.add(spaces + l);
                    formatResult(l);
                } else {
                    if (spaceCount > 1) spaceCount--;
                    return;
                }
            });
            return formattedResult;
        } else {
            return formattedResult;
        }
    }
}