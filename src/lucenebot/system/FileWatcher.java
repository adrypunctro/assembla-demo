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

public abstract class FileWatcher
    extends TimerTask
{
    private long timeStamp;
    private final File file;

    public FileWatcher(File file)
    {
        this.file = file;
        this.timeStamp = file.lastModified();
    }

    @Override
    public final void run()
    {
        long tStamp = file.lastModified();

        if( this.timeStamp != tStamp )
        {
            this.timeStamp = tStamp;
            onChange(file);
        }
    }

    protected abstract void onChange( File file );
}
