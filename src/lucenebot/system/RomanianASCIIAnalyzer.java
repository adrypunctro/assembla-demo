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
//    private final CharArraySet stemExclusionSet;
//
//    public static CharArraySet getDefaultStopSet()
//    {
//        return DefaultSetHolder.DEFAULT_STOP_SET;
//    }

//    private static class DefaultSetHolder
//    {
//        static final CharArraySet DEFAULT_STOP_SET;
//        static {
//            try {
//                DEFAULT_STOP_SET = loadStopwordSet(false, RomanianAnalyzer.class, Settings.STOPWORD_FILE, Settings.STOPWORDS_COMMENT);
//            } catch (IOException ex) {
//                    throw new RuntimeException("Unable to load default stopword set");
//            }
//        }
//    }

//    public RomanianASCIIAnalyzer()
//    {
//        this(DefaultSetHolder.DEFAULT_STOP_SET);
//    }
//
//    public RomanianASCIIAnalyzer(CharArraySet stopwords)
//    {
//        this(stopwords, CharArraySet.EMPTY_SET);
//    }
//
//    public RomanianASCIIAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet)
//    {
//        super(stopwords);
//        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
//    }
    
    @Override
    protected TokenStreamComponents createComponents(String arg0)
    {
        CharArraySet StopWordsSet = null;
        final Tokenizer source = new StandardTokenizer();
        //TokenStream result = new StandardFilter(source);
        TokenStream result = new ASCIIFoldingFilter(source);
        result = new LowerCaseFilter(result);
        //result = new StopFilter(result, stopwords);
        try 
        {
            Path path = Paths.get(Settings.STOPWORD_FILE);
            StopWordsSet = loadStopwordSet(path);
        }
        catch (IOException ex)
        {
            VA_DEBUG.ERROR("RomanianASCIIAnalyzer", "Stopword file not found!");
            StopWordsSet = RomanianAnalyzer.getDefaultStopSet();
        }

        if(StopWordsSet != null)
        {
            result = new StopFilter(result, StopWordsSet);
        }
        //if(!stemExclusionSet.isEmpty()) {
        //    result = new SetKeywordMarkerFilter(result, stemExclusionSet);
        //}
        
        result = new SnowballFilter(result, new RomanianStemmer());
        //result = new ASCIIFoldingFilter(result);

        return new TokenStreamComponents(source, result);
    }
}
