package engine;

import bean.DocumentContent;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import utils.FileUtils;
import utils.LuceneUtils;
import utils.MyCollector;
import utils.QueryHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Search {
    // hurricane
    // mitch george
    // bill clinton israel
    // "newt gingrich" down
    // nba strike closed-door

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Please input your keyword:");
        String keywords = in.nextLine();
        in.close();

        LuceneUtils.getInstance();
        QueryHandler queryHandler = new QueryHandler("text", keywords);
        Query q = queryHandler.getBooleanQuery();
        MyCollector collector = new MyCollector(queryHandler);
        LuceneUtils.getSearcher().search(q, collector);
        List<ScoreDoc> scoreDocs = collector.getSortedScoreDocs(10);

        for (int i = 0; i < scoreDocs.size(); i++) {
            Document document = LuceneUtils.getSearcher().doc(scoreDocs.get(i).doc);
            String filePath = document.get("filePath");
            File file = new File(filePath);
            DocumentContent documentContent = new DocumentContent(FileUtils.getFileContent(file));
            String abstractText;
            if (documentContent.getText().length() < 400) {
                abstractText = documentContent.getText();
            } else {
                abstractText = documentContent.getText().substring(0, 400) + "....";
            }
            System.out.println((i + 1) + "\t[" + scoreDocs.get(i).score + "]\t" + documentContent.getDocNO() + "\n" + abstractText + "\n");
        }

    }

}
