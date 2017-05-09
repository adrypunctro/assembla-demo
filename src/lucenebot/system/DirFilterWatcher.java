/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucenebot.system;

import java.io.*;

/**
 *
 * @author ASimionescu
 */
public class DirFilterWatcher
    implements FileFilter
{
    private String filter;

    public DirFilterWatcher() {
        this.filter = "";
    }

    public DirFilterWatcher(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(File file) {
        if ("".equals(filter)) {
            return true;
        }
        return (file.getName().endsWith(filter));
    }
}
