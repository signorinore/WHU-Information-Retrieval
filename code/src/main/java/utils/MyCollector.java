package utils;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.SimpleCollector;

import java.io.IOException;
import java.util.*;

public class MyCollector extends SimpleCollector {

    private List<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>();
    private List<String> terms;
    private List<Integer> df = new ArrayList<Integer>();
    private Map<Integer, Float> docs = new HashMap<Integer, Float>();
    private String field;
    private LuceneUtils luceneUtils = LuceneUtils.getInstance();

    public MyCollector(QueryHandler queryHandler) {
        this.terms = queryHandler.getTerms();
        this.field = queryHandler.getField();
        for (String term : terms) {
            try {
                this.df.add(LuceneUtils.getDocFreq(new Term(this.field, term)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void collect(int doc) throws IOException {
        if (docs.containsKey(doc)) {
            return;
        }
        float score = 0;
        for (int i = 0; i < this.terms.size(); i++) {
            score += LuceneUtils.termScore(doc, this.field, this.terms.get(i), this.df.get(i));
        }
        this.docs.put(doc, score);
    }

    public List<ScoreDoc> getSortedScoreDocs() {
        setScoreDocs();
        return this.scoreDocs;
    }

    public List<ScoreDoc> getSortedScoreDocs(int n) {
        setScoreDocs();
        System.out.println("TotalHits: " + this.scoreDocs.size());
        if (n <= this.scoreDocs.size()) {
            return this.scoreDocs.subList(0, n);
        } else {
            return this.scoreDocs;
        }
    }

    private void setScoreDocs() {
        for (Integer i : docs.keySet()) {
            this.scoreDocs.add(new ScoreDoc(i, docs.get(i)));
        }
        Collections.sort(scoreDocs, new Comparator<ScoreDoc>() {
            @Override
            public int compare(ScoreDoc o1, ScoreDoc o2) {
                if (o1.score < o2.score) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    @Override
    public ScoreMode scoreMode() {
        return ScoreMode.COMPLETE_NO_SCORES;
    }
}
