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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final AtomicBoolean indexInProgress = new AtomicBoolean(false);
    private static final AtomicBoolean searchInProgress = new AtomicBoolean(false);
    private static final Lock indexLock = new ReentrantLock();
    private static final Condition condIndex = indexLock.newCondition();
    private static final Lock searchLock = new ReentrantLock();
    private static final Condition condSearch = searchLock.newCondition();
    
    
    private static Directory indexDir;

    private static Indexer  indexer;
    private static Searcher searcher;
    
    private static void reindex()
    {
        checkSearch();
        
        VA_DEBUG.INFO("Indexer", "Reindex...");
        
        lockIndex();
        try {
            indexer.createIndex();
        } catch (Exception ex) {}

        unlockIndex();
    }
    
    public static void main(String[] args) throws Exception
    {
        indexDir = new RAMDirectory();
        //indexDir = FSDirectory.open(Paths.get(Settings.INDEX_DIRECTORY));
        
        indexer  = new Indexer(indexDir);
        searcher = new Searcher(indexDir);
    

        // First time
        lockIndex();
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
            checkIndex();
            
            VA_DEBUG.INFO("Sercher: "+cursor, "> ", false);
            
            Scanner scanner = new Scanner(System. in);
            String input = scanner.nextLine();
            
            lockSearch();
            if (input.equals("ls"))
            {
                switch(cursor)
                {
                    case "Root":
                        System.out.println("ffile - From file");
                        System.out.println("key - From keyboard");
                        System.out.println("re - Reindex");
                        System.out.println("exit - Exit program");
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
                
            
                if (!Files.isReadable(Paths.get(Settings.ROOT+"questions.txt"))) {
                    System.exit(1);
                }

                try(BufferedReader fileReader = new BufferedReader(new FileReader(Settings.ROOT+"questions.txt"))) {
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
                unlockSearch();
                reindex();
            }
            else if (input.equals("exit")) {
                System.exit(0);
            }
            unlockSearch();
            
        }
    }
    
    private static void checkIndex()
    {
        if (indexInProgress.get() == true)
        {
            indexLock.lock();
            try {
                VA_DEBUG.INFO("Sercher", "Wait reindex...");
                condIndex.await();
            }
            catch (InterruptedException ex) {
                Logger.getLogger(LuceneTester.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally { indexLock.unlock(); }
        }
    }
    
    private static void checkSearch()
    {
        if (searchInProgress.get() == true)
        {
            searchLock.lock();
            try {
                VA_DEBUG.INFO("Indexer", "Wait searching...");
                condSearch.await();
            }
            catch (InterruptedException ex) {
                Logger.getLogger(LuceneTester.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally { searchLock.unlock(); }
        }
    }
    
    private static void lockIndex()
    {
        indexInProgress.set(true);
    }
    
    private static void lockSearch()
    {
        searchInProgress.set(true);
    }
    
    private static void unlockIndex()
    {
        indexInProgress.set(false);
        indexLock.lock();
        try {
            condIndex.signal();
        }
        finally { indexLock.unlock(); }
    }
    
    private static void unlockSearch()
    {
        searchInProgress.set(false);
        searchLock.lock();
        try {
            condSearch.signal();
        }
        finally { searchLock.unlock(); }
    }
}
