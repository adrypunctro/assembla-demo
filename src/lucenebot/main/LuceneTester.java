package lucenebot.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lucenebot.system.DirWatcher;
import org.apache.lucene.store.Directory;

import lucenebot.system.Indexer;
import lucenebot.system.Searcher;
import lucenebot.system.Settings;
import lucenebot.system.VA_DEBUG;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class LuceneTester
{
    private static final AtomicBoolean reindex = new AtomicBoolean(false);
    private static final Lock processLock = new ReentrantLock();
    private static final Condition condVar = processLock.newCondition();
    
    private static Directory indexDir;

    private static Indexer  indexer;
    private static Searcher searcher;
    
    private static void reindex()
    {
        VA_DEBUG.INFO("Indexer", "Reindex...");
        try {
            indexer.createIndex();
        } catch (Exception ex) {}

        reindex.set(false);
        processLock.lock();
        try {
            condVar.signal();
        }
        finally { processLock.unlock(); }
    }
    
    public static void main(String[] args) throws Exception
    {
        indexDir = new RAMDirectory();
        //indexDir = FSDirectory.open(Paths.get(Settings.INDEX_DIRECTORY));
        
        indexer  = new Indexer(indexDir);
        searcher = new Searcher(indexDir);
    

        // First time
        reindex.set(true);
        reindex();
        
        TimerTask task = new DirWatcher(Settings.FILES_TO_INDEX_DIRECTORY) {
            @Override
            protected void onChange( File file, String action ) {
                reindex();
            }
        };
        
        Timer timer = new Timer();
        // repeat the check every second
        timer.schedule( task , new Date(), 1000 );
        
        String cursor = "Root";
        while(true)
        {
            if (reindex.get() == true)
            {
                processLock.lock();
                try {
                    VA_DEBUG.INFO("Sercher", "Wait reindex...");
                    condVar.await();
                }
                finally { processLock.unlock(); }
            }
            
            VA_DEBUG.INFO("Sercher: "+cursor, "> ", false);
            
            Scanner scanner = new Scanner(System. in);
            String input = scanner.nextLine();
            
            if (input.equals("ls"))
            {
                switch(cursor)
                {
                    case "Root":
                        System.out.println("ffile - From file");
                        System.out.println("key - From keyboard");
                        System.out.println("re - Reindex");
                        break;
                    case "Root > ffile":
                        System.out.println("load - Load questions from file");
                        System.out.println("quit - Back to main");
                        break;
                    case "Root > key":
                        System.out.println("quit - Back to main");
                        break;
                }
            }
            else if(cursor.equals("Root") && input.equals("ffile")) {
                cursor = "Root > ffile";
            }
            else if (cursor.equals("Root") && input.equals("key")) {
                cursor = "Root > key";
            }
            else if (!cursor.equals("Root") && input.equals("quit")) {
                cursor = "Root";
            }
            else if (cursor.equals("Root > ffile") && input.equals("load")) {
                
            
                if (!Files.isReadable(Paths.get("C:\\Users\\Adrian Simionescu\\Desktop\\i am vadry\\vadry\\LuceneRomanianBot\\questions.txt"))) {
                    System.exit(1);
                }

                try(BufferedReader fileReader = new BufferedReader(new FileReader("C:\\Users\\Adrian Simionescu\\Desktop\\i am vadry\\vadry\\LuceneRomanianBot\\questions.txt"))) {
                    StringBuilder sb = new StringBuilder();
                    String line = fileReader.readLine();

                    while (line != null) {
                        searcher.search(line);

                        //Citim urmatoarea linie
                        line = fileReader.readLine();
                    }

                }
            }
            else if (cursor.equals("Root > key") && !input.equals("quit") && !input.isEmpty()) {
                searcher.search(input);
            }
            else if (cursor.equals("Root") && input.equals("re")) {
                reindex();
            }
            else if (input.equals("exit")) {
                System.exit(0);
            }
            
        }
    }
}
