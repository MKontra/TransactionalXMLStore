/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.bridge.proxies.interfaces.XAFileSystemProxy;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;

/**
 *
 * @author Administrator
 */
public enum XADiskInstanceManager
{

    INSTANCE;

    private static XAFileSystem xaf;
    public XAFileSystem getFileSystemInstance()
    {
        return xaf;
    }
    static
    {
        InputStream is = null;
        Properties prop = new Properties();
        Properties staticConfig = prop;
        try
        {

            URL confurl = ClassLoader.getSystemResource("XMLStoreConfig.xml");
            if (confurl != null)
            {
                is = confurl.openStream();
                prop.loadFromXML(is);
            }


        } catch (IOException ex)
        {
            Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, "Could not load properties", ex);
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            } catch (Throwable ex)
            {
                Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, "Error closing property stream", ex);
            }
        }

        String instanceName = staticConfig.getProperty("XADiskInstanceName", "defaultXADiskSystemLocation");
        String XADiskSystemLocation = staticConfig.getProperty("XADiskSystemLocation", ".") + File.separator + staticConfig.getProperty("APPName", "defaultXADiskInstance");

        StandaloneFileSystemConfiguration configuration =
                new StandaloneFileSystemConfiguration(XADiskSystemLocation, instanceName);


        xaf = XAFileSystemProxy.bootNativeXAFileSystem(configuration);
        try
        {
            xaf.waitForBootup(10000L);
        } catch (InterruptedException ex)
        {
            xaf = null;
            Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
