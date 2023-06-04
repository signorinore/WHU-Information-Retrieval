package utils;

import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class LuceneUtils {

    private static LuceneUtils singleton;
    private static IndexReader reader;
    private static IndexSearcher searcher;
    private static long totalFileSize;

    private LuceneUtils() {}

    public static LuceneUtils getInstance() {
        if (singleton == null) {
            synchronized (LuceneUtils.class) {
                if (singleton == null) {
                    singleton = new LuceneUtils();
                    try {
                        Directory directory = FSDirectory.open(Paths.get(FileUtils.getProjectPath() + "\\index"));
                        reader = DirectoryReader.open(directory);
                        searcher = new IndexSearcher(reader);
                        totalFileSize = FileUtils.getFileSize(new File(FileUtils.getProjectPath() + "\\tdt3"));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }
        return singleton;
    }

    public static IndexSearcher getSearcher() {
        return searcher;
    }

    public static int getTermFreq(int docId, Term term) throws IOException {
        BytesRef termBytes = term.bytes();
        Terms terms = reader.getTermVector(docId, "text");
        if (terms == null) {
            return 0;
        }
        PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader, "text", termBytes);
        TermsEnum termsEnum = terms.iterator();
        BytesRef termBytes1;
        while((termBytes1 = termsEnum.next()) != null){
            if (term.text().matches(termBytes1.utf8ToString())) {
                PostingsEnum docPosEnum = termsEnum.postings(postingsEnum);
                docPosEnum.nextDoc();
                return docPosEnum.freq();
            }
        }
        return 0;
    }

    public static int getDocFreq(Term term) throws IOException {
        Terms terms = MultiTerms.getTerms(reader, "text");
        TermsEnum termsEnum = terms.iterator();
        BytesRef br;
        while ((br = termsEnum.next()) != null) {
            if (term.text().matches(br.utf8ToString())) {
                return termsEnum.docFreq();
            }
        }
        return 0;
    }

    public static int getDocNum() {
        return reader.maxDoc();
    }

    public static float termScore(int docId, String field, String term, int df) throws IOException {
        Term term1 = new Term(field, term);
        float tf = getTermFreq(docId, term1);
        float idf = (float) Math.log((getDocNum() - df + 0.5) / (df + 0.5));
        float K = (float) (0.5 + 1.5 * Integer.parseInt(searcher.doc(docId).get("fileSize")) * getDocNum() / totalFileSize);
        float R = 3 * tf / (tf + K);
        return idf * R;
    }

    public static void main(String[] args) throws IOException {
        LuceneUtils luceneUtils = LuceneUtils.getInstance();
        Term term = new Term("text", "hurricane");
        int df = getDocFreq(term);
        System.out.println(df);
        System.out.println(getDocNum());
    }


}
