package crawler;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.instrument.Instrumentation;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.net.URL;
import java.net.MalformedURLException;

public class Functions
{

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width)
    {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

    private static boolean isInCycle(ArrayList<String> urls) throws IsInCycleException // if all of domens are the same
    {
        ArrayList<URL> myUrls = new ArrayList<URL>(urls.size() );

        for (int i = 0; i <= urls.size() - 1; i++){
            try{
                myUrls.add((new URL(urls.get(i))) );
            }
            catch (MalformedURLException e)
            {
                throw new IsInCycleException();
            }
        }
        
        String domen = myUrls.get(0).getHost();

        for (int i = urls.size() - 1; i >= 1; i--)
            if (myUrls.get(i).getHost() != domen)
                return false;

        return true;
    }

    private static int saveInBinaryFile(String filename, int id, Document doc)
    {
        String title;
        ArrayList<String> links;

        Element el = doc.select("title").first();
        title = el.text();
        Elements linksElements = doc.select("a[href]");
        links = new ArrayList<String>(linksElements.size());

        for (int i = 0; i <= linksElements.size() - 1; i++)
            links.add(linksElements.get(i).attr("abs:href"));

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename, true)))
        {
            WebPage page = new WebPage(id, title, links);
            oos.writeObject(page);
            oos.close();

            return 0;
        }
        catch(Exception e){
            return -1;
        }
    }

    public static void indexing(String fileName) throws IOException, InterruptedException

    {
        int id = 0;
        ArrayList<String> baseLinks = new ArrayList<String>(7);

        baseLinks.add("https://www.rbc.ru/");
        baseLinks.add("https://www.theguardian.com/international");
        baseLinks.add("https://www.nbcnews.com/");
        baseLinks.add("https://www.wsj.com/europe");
        baseLinks.add("https://www.entrepreneur.com/");
        baseLinks.add("https://www.allrecipes.com/");
        baseLinks.add("https://en.wikipedia.org/wiki/Kaspersky_Lab");

        ArrayList<String> listOfLinks = new ArrayList<String>();

        Random randomGenerator = new Random();
        String url = baseLinks.get(randomGenerator.nextInt(baseLinks.size()) );
        String homeUrl = url;
        print("Fetching %s...", url);

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        randomGenerator = new Random();
        
        id++;
        saveInBinaryFile(fileName, id, doc);

        System.out.println(links.get(randomGenerator.nextInt(links.size()) ).attr("abs:href"));

        while (true)
        {
            System.out.println("");
            listOfLinks.add(url);

            url = links.get(randomGenerator.nextInt(links.size()) ).attr("abs:href");

            if (!(url.contains("http")))
                url = homeUrl + url;

            print("Fetching %s...", url);

            try
            {
                doc = Jsoup.connect(url).get();

                id++;
                saveInBinaryFile(fileName, id, doc);
            }
            catch (Exception e)
            {
                System.out.println("-------------------------Exception----------------------");
                randomGenerator = new Random();
                url = baseLinks.get(randomGenerator.nextInt(baseLinks.size()) );
                doc = Jsoup.connect(url).get();

                id++;
                saveInBinaryFile(fileName, id, doc);
            }

            links = doc.select("a[href]");
            randomGenerator = new Random();

            if (links.size() == 0)
            {
                randomGenerator = new Random();
                url = baseLinks.get(randomGenerator.nextInt(baseLinks.size()) );
                doc = Jsoup.connect(url).get();
                links = doc.select("a[href]");

                id++;
                saveInBinaryFile(fileName, id, doc);
            }
            else
            {
                System.out.println(listOfLinks.size());
                System.out.println(links.get(randomGenerator.nextInt(links.size()) ).attr("abs:href"));
            }

            // If the crawler got in a cycle

            if (listOfLinks.size() > 5)
            {
                ArrayList<String> lastLinks = new ArrayList<String>(5);

                for (int i = 0; i <= 4; i++)
                    lastLinks.add(listOfLinks.get(listOfLinks.size() - (-i + 5)) );

                try{
                    if (isInCycle(lastLinks))
                    {
                        System.out.println("-------------------Got in a cycle-------------------");
                        randomGenerator = new Random();
                        url = baseLinks.get(randomGenerator.nextInt(baseLinks.size()) );
                        doc = Jsoup.connect(url).get();
                        links = doc.select("a[href]");

                        id++;
                        saveInBinaryFile(fileName, id, doc);
                    }
                }
                catch (IsInCycleException e)
                {
                    randomGenerator = new Random();
                    url = baseLinks.get(randomGenerator.nextInt(baseLinks.size()) );
                    doc = Jsoup.connect(url).get();
                    links = doc.select("a[href]");

                    id++;
                    saveInBinaryFile(fileName, id, doc);
                }
            }

            homeUrl = url;
        }
    }

}
