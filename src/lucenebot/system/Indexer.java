package lucenebot.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

public class Indexer
{
    private final Directory indexDir;

    public Indexer(Directory indexDir)
    {
        this.indexDir = indexDir;
    }

    public void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException
    {
        Analyzer analyzer = new RomanianASCIIAnalyzer();

        IndexWriterConfig indexConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDir, indexConfig);
        File dir = new File(Settings.FILES_TO_INDEX_DIRECTORY);
        File[] files = dir.listFiles();
        for (File file : files)
        {
            String spath = file.getCanonicalPath();
            Path path = Paths.get(spath);
            String type = Files.probeContentType(path);
            
            Document document = new Document();
            
            switch(type)
            {
                case "text/plain":
                {
                    System.out.println("> Index file "+spath);

                    Reader content = new FileReader(file);

                    document.add(new Field(Settings.FIELD_PATH, spath, TextField.TYPE_STORED));
                    document.add(new Field(Settings.FIELD_CONTENTS, content, TextField.TYPE_NOT_STORED));
                
                    break;
                }
                    
                case "application/pdf":
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                case "application/msword":
                case "text/html":
                {
                    try {
                        String content = autoParse(spath);
                        
                        document.add(new Field(Settings.FIELD_PATH, spath, TextField.TYPE_STORED));
                        document.add(new Field(Settings.FIELD_CONTENTS, content, TextField.TYPE_NOT_STORED));
                        
                        System.out.println("> Index file "+spath);
                    } catch (SAXException | TikaException ex) {
                        System.out.println("> CAN'T index file "+spath);
                    }
                    break;
                }
                
                default:
                {
                    System.out.println("> UNSUPPORTED file "+spath+" ("+type+")");
                    break;
                }
            }
            
            indexWriter.addDocument(document);
        }
        //indexWriter.optimize();
        indexWriter.close();
    }
    
    public String autoParse(String path) throws IOException, SAXException, TikaException
    {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (InputStream stream = new FileInputStream(path))
        {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        }
        catch (org.xml.sax.SAXException ex)
        {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}