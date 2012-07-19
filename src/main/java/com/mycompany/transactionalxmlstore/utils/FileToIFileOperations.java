/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import java.io.*;
import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.filesystem.exceptions.*;

/**
 *
 * @author Administrator
 */
public class FileToIFileOperations implements IFileOperations
{

    @Override
    public void createFile(File f, boolean isDirectory) throws IOException
    {
        if ( f.isDirectory() )
        {
            f.mkdirs();
        } else
        {
            f.createNewFile();
        }
    }

    @Override
    public InputStream createFileInputStream(File f, boolean lockExclusively) throws IOException
    {
        return new FileInputStream(f);
    }

    @Override
    public OutputStream createFileOutputStream(File f) throws IOException
    {
        return new FileOutputStream(f);
    }

    @Override
    public void deleteFile(File f) throws IOException
    {
        f.delete();
    }

    @Override
    public boolean fileExists(File f, boolean lockExclusively) throws IOException
    {
        return f.exists();
    }
    
    @Override
    public long getFileLength(File f, boolean lockExclusively) throws IOException
    {
        return f.length();
    }

    @Override
    public String[] listFiles(File f, boolean lockExclusively) throws IOException
    {
        return f.list();
    }
}