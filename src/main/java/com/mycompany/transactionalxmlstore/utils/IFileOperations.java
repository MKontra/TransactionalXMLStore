/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public interface IFileOperations
{

    public void createFile(File f, boolean isDirectory) throws IOException;

    public InputStream createFileInputStream(File f, boolean lockExclusively) throws IOException;

    public OutputStream createFileOutputStream(File f) throws IOException;

    public void deleteFile(File f) throws IOException;

    public boolean fileExists(File f, boolean lockExclusively) throws IOException;

    public long getFileLength(File f, boolean lockExclusively) throws IOException;

    public String[] listFiles(File f, boolean lockExclusively) throws IOException;
}
