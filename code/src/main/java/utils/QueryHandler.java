package utils;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class QueryHandler {

    private List<List<String>> mustPhrases = new ArrayList<>();
    private List<List<String>> shouldPhrases = new ArrayList<>();
    private List<String> terms = new ArrayList<>();
    private String field;

    public QueryHandler(String field, String query) throws IOException {
        this.field = field;
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher m = p.matcher(query);
        while (m.find()) {
            List<String> phraseString = new ArrayList<>();
            String phrase = m.group().replace("\"", "").trim().toLowerCase();
            String[] phrases = phrase.split("\\s+");
            for (String phrase1 : phrases) {
                String[] phrase2 = phrase1.split("-");
                for (String phrase3 : phrase2) {
                    phraseString.add(phrase3);
                }
            }
            this.mustPhrases.add(phraseString);
        }
        query = m.replaceAll(" ").trim().toLowerCase();
        String[] terms = query.split("\\s+");
        for (String term : terms) {
            String[] t = term.split("-");
            if (t.length > 1) {
                List<String> phraseString = new ArrayList<>();
                for (String t1 : t) {
                    phraseString.add(t1);
                }
                this.shouldPhrases.add(phraseString);
            } else {
                this.terms.add(term);
            }
        }
    }

    public String getField() {
        return this.field;
    }

    @Override
    public String toString() {
        String mp = "";
        for (List<String> phrase : this.mustPhrases) {
            for (String t : phrase) {
                mp += t + " ";
            }
            mp += ",";
        }
        mp += "\n";

        String sp = "";
        for (List<String> phrase : this.shouldPhrases) {
            for (String t : phrase) {
                sp += t + " ";
            }
            sp += ",";
        }
        sp += "\n";


        String t = "";
        for (String term : this.terms) {
            t += term + ",";
        }
        return "Field: " + this.field + "\nMustPhrases: " + mp + "ShouldPhrases: " + sp + "Terms: " + t;
    }

    public Query getBooleanQuery() {
        BooleanQuery.Builder booleanBuilder = new BooleanQuery.Builder();

        for (List<String> phrases : this.mustPhrases) {
            PhraseQuery.Builder phraseBuilder = new PhraseQuery.Builder();
            for (String term : phrases) {
                phraseBuilder.add(new Term(this.field, term));
            }
            phraseBuilder.setSlop(0);
            booleanBuilder.add(phraseBuilder.build(), BooleanClause.Occur.MUST);
        }

        for (List<String> phrases : this.shouldPhrases) {
            PhraseQuery.Builder phraseBuilder = new PhraseQuery.Builder();
            for (String term : phrases) {
                phraseBuilder.add(new Term("text", term));
            }
            phraseBuilder.setSlop(0);
            booleanBuilder.add(phraseBuilder.build(), SHOULD);
        }

        for (String term : this.terms) {
            booleanBuilder.add(new TermQuery(new Term("text", term)), SHOULD);
        }

        return booleanBuilder.build();
    }

    public List<String> getTerms() {
        List<String> result = new ArrayList<String>();
        for (String term : this.terms) {
            result.add(term);
        }
        for (List<String> phrases : this.mustPhrases) {
            for (String phrase : phrases) {
                result.add(phrase);
            }
        }
        for (List<String> phrases : this.shouldPhrases) {
            for (String phrase : phrases) {
                result.add(phrase);
            }
        }
        return result;
    }
}
