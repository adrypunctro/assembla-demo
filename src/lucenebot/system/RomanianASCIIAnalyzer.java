package lucenebot.system;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.RomanianStemmer;

public final class RomanianASCIIAnalyzer
    extends StopwordAnalyzerBase
{
    @Override
    protected TokenStreamComponents createComponents(String arg0)
    {
        CharArraySet stopWordsSet = null;
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new ASCIIFoldingFilter(source);
        result = new LowerCaseFilter(result);

        try 
        {
            Path path = Paths.get(Settings.STOPWORD_FILE);
            stopWordsSet = loadStopwordSet(path);
        }
        catch (IOException ex)
        {
            VA_DEBUG.ERROR("RomanianASCIIAnalyzer", ex.getMessage());
            stopWordsSet = RomanianAnalyzer.getDefaultStopSet();
        }

        if(stopWordsSet != null)
        {
            result = new StopFilter(result, stopWordsSet);
        }
        
        result = new SnowballFilter(result, new RomanianStemmer());
        return new TokenStreamComponents(source, result);
    }
}
