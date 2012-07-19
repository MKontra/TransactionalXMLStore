/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.XADiskBasicIOOperations;
import org.xadisk.filesystem.exceptions.*;

/**
 *
 * @author Administrator
 */
public class XADiskSessionToIFileOperations implements IFileOperations
{
    private final XADiskBasicIOOperations xaOps;

    public XADiskSessionToIFileOperations( XADiskBasicIOOperations xaOps )
    {
        this.xaOps = xaOps;
    }

    @Override
    public void createFile(File f, boolean isDirectory) throws IOException
    {
        try
        {
            this.xaOps.createFile(f, isDirectory);
        } catch (Exception ex)
        {
            Logger.getLogger(XADiskSessionToIFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }

    @Override
    public InputStream createFileInputStream(File f, boolean lockExclusively) throws IOException
    {
        try
        {
            return new XAFileInputStreamWrapper(this.xaOps.createXAFileInputStream(f, lockExclusively));
        } catch (Exception ex)
        {
            Logger.getLogger(XADiskSessionToIFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }

    @Override
    public OutputStream createFileOutputStream(File f) throws IOException
    {
        try
        {
            return new XAFileOutputStreamWrapper(this.xaOps.createXAFileOutputStream(f, false));
        } catch (Exception ex)
        {
            Logger.getLogger(XADiskSessionToIFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }

    @Override
    public void deleteFile(File f) throws IOException
    {
        try
        {
            this.xaOps.deleteFile(f);
        } catch (Exception ex)
        {
            Logger.getLogger(XADiskSessionToIFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }

    @Override
    public boolean fileExists(File f, boolean lockExclusively) throws IOException
    {
        try
        {
            return this.xaOps.fileExists(f, lockExclusively);
        } catch (Exception ex)
        {
            Logger.getLogger(XADiskSessionToIFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }

    @Override
    public long getFileLength(File f, boolean lockExclusively) throws IOException
    {
        try
        {
            return this.xaOps.getFileLength(f, lockExclusively);
        } catch (Exception ex)
        {
            Logger.getLogger(XADiskSessionToIFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }

    @Override
    public String[] listFiles(File f, boolean lockExclusively) throws IOException
    {
        try
        {
            return this.xaOps.listFiles(f, lockExclusively);
        } catch (Exception ex)
        {
            Logger.getLogger(XADiskSessionToIFileOperations.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }
    
}
