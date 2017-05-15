package lucenebot.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.Normalizer;
import java.util.regex.Pattern;

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
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.RomanianStemmer;

public final class RomanianASCIIAnalyzer
    extends StopwordAnalyzerBase
{
    public String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
    @Override
    protected TokenStreamComponents createComponents(String arg0)
    {
        CharArraySet stopWordsSet = null;
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new ASCIIFoldingFilter(source);
        result = new LowerCaseFilter(result);

        try 
        {
            //Path path = Paths.get(Settings.STOPWORD_FILE);
            //stopWordsSet = loadStopwordSet(path);
            
            stopWordsSet = new CharArraySet(0, true);
            if (Files.isReadable(Paths.get(Settings.STOPWORD_FILE)))
            {
                try(BufferedReader fileReader = new BufferedReader(new FileReader(Settings.STOPWORD_FILE))) {
                    StringBuilder sb = new StringBuilder();
                    String input = fileReader.readLine();

                    while (input != null) {
                        stopWordsSet.add(deAccent(input));
                        input = fileReader.readLine();
                    }
                }
            }
        }
        catch (IOException ex)
        {
            VA_DEBUG.ERROR("RomanianASCIIAnalyzer", ex.getMessage());
            stopWordsSet = RomanianAnalyzer.getDefaultStopSet();
        }
        
        if(!stopWordsSet.isEmpty())
        {
            result = new StopFilter(result, stopWordsSet);
        }
        else {
            VA_DEBUG.ERROR("RomanianASCIIAnalyzer", "stopWordsSet is empty.");
        }
        
        result = new SnowballFilter(result, new RomanianStemmer());
        return new TokenStreamComponents(source, result);
    }
}
