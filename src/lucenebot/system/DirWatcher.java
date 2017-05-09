/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucenebot.system;

import java.util.*;
import java.io.*;

/**
 *
 * @author ASimionescu
 */
public abstract class DirWatcher
    extends TimerTask
{
    private String path;
    private File filesArray [];
    private HashMap dir = new HashMap();
    private DirFilterWatcher dfw;

    public DirWatcher(String path) {
        this(path, "");
    }

  public DirWatcher(String path, String filter) {
    this.path = path;
    dfw = new DirFilterWatcher(filter);
    filesArray = new File(path).listFiles(dfw);

      // transfer to the hashmap be used a reference and keep the
      // lastModfied value
      for (File filesArray1 : filesArray) {
          dir.put(filesArray1, new Long(filesArray1.lastModified()));
      }
  }

  @Override
  public final void run() {
    HashSet checkedFiles = new HashSet();
    filesArray = new File(path).listFiles(dfw);

      // scan the files and check for modification/addition
      for (File filesArray1 : filesArray) {
          Long current = (Long) dir.get(filesArray1);
          checkedFiles.add(filesArray1);
          if (current == null) {
              // new file
              dir.put(filesArray1, new Long(filesArray1.lastModified()));
              onChange(filesArray1, "add");
          } else if (current.longValue() != filesArray1.lastModified()) {
              // modified file
              dir.put(filesArray1, new Long(filesArray1.lastModified()));
              onChange(filesArray1, "modify");
          }
      }

    // now check for deleted files
    Set ref = ((HashMap)dir.clone()).keySet();
    ref.removeAll((Set)checkedFiles);
    Iterator it = ref.iterator();
    while (it.hasNext()) {
      File deletedFile = (File)it.next();
      dir.remove(deletedFile);
      onChange(deletedFile, "delete");
    }
  }

  protected abstract void onChange( File file, String action );
}
