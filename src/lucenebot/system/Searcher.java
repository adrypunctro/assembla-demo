package lucenebot.system;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;

public class Searcher
{
    private final Directory indexDir;

    public Searcher(Directory indexDir)
    {
        this.indexDir = indexDir;
    }

    public void search(String searchString) throws IOException, ParseException
    {
        VA_DEBUG.INFO("SEARCH FOR '" + searchString + "'", true);

        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Analyzer analyzer = new RomanianASCIIAnalyzer();
        QueryParser queryParser = new QueryParser(Settings.FIELD_CONTENTS, analyzer);
        Query query = queryParser.parse(searchString);
        ScoreDoc[] hits = indexSearcher.search(query, 100).scoreDocs;
        if (hits.length > 0) {
            VA_DEBUG.SUCCESS("> Number of hits: " + hits.length, true);
        }
        else {
            VA_DEBUG.ERROR("> Number of hits: " + hits.length, true);
        }
        
        //Iterator<Hit> it = hits.iterator();
        for (int i = 0; i < hits.length; i++) {
            Document document = indexSearcher.doc(hits[i].doc);
            String path = document.get(Settings.FIELD_PATH);
            VA_DEBUG.INFO("> Hit: " + path, true);
        }
        
        System.out.println();
    }
}