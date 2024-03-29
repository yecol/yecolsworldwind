/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
/**
 *
 @version $Id: Configuration.java 12762 2009-10-31 02:56:12Z tgaskins $
 @author Tom Gaskins
 */
package gov.nasa.worldwind;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.*;

import javax.xml.xpath.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * This class manages the initial World Wind configuration. It reads World Wind configuration files and registers their
 * contents. Configurations files contain the names of classes to create at run-time, the initial model definition,
 * including the globe, elevation model and layers, and various control quantities such as cache sizes and data
 * retrieval timeouts.
 * <p/>
 * The Configuration class is a singleton, but its instance is not exposed publicly. It is addressed only via static
 * methods of the class. It is constructed upon first use of any of its static methods.
 * <p/>
 * When the Configuration class is first instantiated it reads the XML document <code>config/worldwind.xml</code> and
 * registers all the information there. The information can subsequently be retrieved via the class' various
 * <code>getValue</code> methods. Many World Wind start-up objects query this information to determine the classes to
 * create. For example, the first World Wind object created by an application is typically a {@link
 * gov.nasa.worldwind.awt.WorldWindowGLCanvas}. During construction that class causes World Wind's internal classes to
 * be constructed, using the names of those classes drawn from the Configuration singleton, this class.
 * <p/>
 * The default World Wind configuration document is <code>config/worldwind.xml</code>. This can be changed by setting
 * the Java property <code>gov.nasa.worldwind.config.file</code> to a different file name or a valid URL prior to
 * creating any World Wind object or invoking any static methods of World Wind classes, including the Configuration
 * class. When an application specifies a different configuration location it typically does so in its main method prior
 * to using World Wind. If a file is specified its location must be on the classpath. (The contents of application and
 * World Wind jar files are typically on the classpath, in which case the configuration file may be in the jar file.)
 * <p/>
 * Additionally, an application may set another Java property, <code>gov.nasa.worldwind.app.config.document</code>, to a
 * file name or URL whose contents contain configuration values to override those of the primary configuration document.
 * World Wind overrides only those values in this application document, it leaves all others to the value specified in
 * the primary document. Applications usually specify an override document in order to specify the initial layers in the
 * model.
 * <p/>
 * See <code>config/worldwind.xml</code> for documentation on setting configuration values.
 * <p/>
 * Configuration values can also be set programatically via {@link Configuration#setValue(String, Object)}, but they are
 * not retroactive so affect only Configuration queries made subsequent to setting the value.
 * <p/>
 * <em>Note:</em> Prior to September of 2009, configuration properties were read from the file
 * <code>config/worldwind.properties</code>. An alternate file could be specified via the
 * <code>gov.nasa.worldwind.config.file</code> Java property. These mechanisms remain available but are deprecated.
 * World Wind no longer contains a <code>worldwind.properties</code> file. If <code>worldwind.properties</code> or its
 * replacement as specified through the Java property exists at run-time and can be found via the classpath,
 * configuration values specified by that mechanism are given precedence over values specified by the new mechanism.
 */
public class Configuration // Singleton
{
    public static final String DEFAULT_LOGGER_NAME = "gov.nasa.worldwind";

    private static final String CONFIG_PROPERTIES_FILE_NAME = "config/worldwind.properties";
    private static final String CONFIG_FILE_PROPERTY_KEY = "gov.nasa.worldwind.config.file";

    private static final String CONFIG_WW_DOCUMENT_KEY = "gov.nasa.worldwind.config.document";
    private static final String CONFIG_WW_DOCUMENT_NAME = "config/worldwind.xml";

    private static final String CONFIG_APP_DOCUMENT_KEY = "gov.nasa.worldwind.app.config.document";

    private static Configuration ourInstance = new Configuration();

    private static Configuration getInstance()
    {
        return ourInstance;
    }

    private final Properties properties;
    private final ArrayList<Document> configDocs = new ArrayList<Document>();//配置文件列表

    /** Private constructor invoked only internally. */
    private Configuration()
   {
    	//Process1：生成 properties
    	//Process2：加载 CONFIG_APP_DOCUMENT_KEY  文件
    	//Process3：加载 CONFIG_WW_DOCUMENT_KEY   文件
    	//Process4：加载 CONFIG_FILE_PROPERTY_KEY 文件
        this.properties = initializeDefaults();
        //基本初始化

        // Load the app's configuration if there is one
        try
        {
            String appConfigLocation = System.getProperty(CONFIG_APP_DOCUMENT_KEY);
            if (appConfigLocation != null)
                this.loadConfigDoc(System.getProperty(CONFIG_APP_DOCUMENT_KEY)); // Load app's config first
        }
        catch (Exception e)
        {
            Logging.logger(DEFAULT_LOGGER_NAME).log(Level.WARNING, "Configuration.ConfigNotFound",
                System.getProperty(CONFIG_APP_DOCUMENT_KEY));
            // Don't stop if the app config file can't be found or parsed
        }

        try
        {
            // Load the default configuration
            this.loadConfigDoc(System.getProperty(CONFIG_WW_DOCUMENT_KEY, CONFIG_WW_DOCUMENT_NAME));

            // Load config properties, ensuring that the app's config takes precedence over wwj's
            for (int i = this.configDocs.size() - 1; i >= 0; i--)
            {
                this.loadConfigProperties(this.configDocs.get(i));
            }
        }
        catch (Exception e)
        {
            Logging.logger(DEFAULT_LOGGER_NAME).log(Level.WARNING, "Configuration.ConfigNotFound",
                System.getProperty(CONFIG_WW_DOCUMENT_KEY));
        }

        // To support old-style configuration, read an existing config properties file and give the properties
        // specified there precedence.
        this.initializeCustom();
        //加载所有客户定义的初始化，其中用户的配置文件数组configDocs通过文件流的形式依次输入。
    }

    private void loadConfigDoc(String configLocation)
    {
    	//从文件读取配置。
        if (!WWUtil.isEmpty(configLocation))
        {
            Document doc = WWXML.openDocument(configLocation);
            if (doc != null)
            {
                this.configDocs.add(doc);
//                this.loadConfigProperties(doc);
            }
        }
    }

    private void insertConfigDoc(String configLocation)
    {
        if (!WWUtil.isEmpty(configLocation))
        {
            Document doc = WWXML.openDocument(configLocation);
            if (doc != null)
            {
                this.configDocs.add(0, doc);
                this.loadConfigProperties(doc);
            }
        }
    }

    private void loadConfigProperties(Document doc)
    {
    	//从doc这个xml文件中读取所有属性名值对添加到this.properties.
        try
        {
            XPath xpath = WWXML.makeXPath();

            NodeList nodes = (NodeList) xpath.evaluate("/WorldWindConfiguration/Property", doc, XPathConstants.NODESET);
            if (nodes == null || nodes.getLength() == 0)
                return;

            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node node = nodes.item(i);
                String prop = xpath.evaluate("@name", node);
                String value = xpath.evaluate("@value", node);
                if (WWUtil.isEmpty(prop) || WWUtil.isEmpty(value))
                    continue;

                this.properties.setProperty(prop, value);
            }
        }
        catch (XPathExpressionException e)
        {
            Logging.logger(DEFAULT_LOGGER_NAME).log(Level.WARNING, "XML.ParserConfigurationException");
        }
    }

    private Properties initializeDefaults()
    {
    	//返回一个属性（AV），根据时区设置初始化的经度。
    	
        Properties defaults = new Properties();
        java.util.TimeZone tz = java.util.Calendar.getInstance().getTimeZone();
        if (tz != null)
            defaults.setProperty(AVKey.INITIAL_LONGITUDE,
                Double.toString(
                    Angle.fromDegrees(180.0 * tz.getOffset(System.currentTimeMillis()) / (12.0 * 3.6e6)).degrees));
        return defaults;
    }

    private void initializeCustom()
    {
        // IMPORTANT NOTE: Always use the single argument version of Logging.logger in this method because the non-arg
        // method assumes an instance of Configuration already exists.

        String configFileName = System.getProperty(CONFIG_FILE_PROPERTY_KEY, CONFIG_PROPERTIES_FILE_NAME);
        try
        {
            java.io.InputStream propsStream = null;
            File file = new File(configFileName);
            if (file.exists())
            {
                try
                {
                    propsStream = new FileInputStream(file);
                }
                catch (FileNotFoundException e)
                {
                    Logging.logger(DEFAULT_LOGGER_NAME).log(Level.FINEST, "Configuration.LocalConfigFileNotFound",
                        configFileName);
                }
            }

            if (propsStream == null)
            {
                propsStream = this.getClass().getResourceAsStream("/" + configFileName);
            }

            if (propsStream != null)
                this.properties.load(propsStream);
        }
        // Use a named logger in all the catch statements below to prevent Logger from calling back into
        // Configuration when this Configuration instance is not yet fully instantiated.
        catch (IOException e)
        {
            Logging.logger(DEFAULT_LOGGER_NAME).log(Level.SEVERE, "Configuration.ExceptionReadingPropsFile", e);
        }
    }

    public static void insertConfigurationDocument(String fileName)
    {
        getInstance().insertConfigDoc(fileName);
    }

    /**
     * Return as a string the value associated with a specified key.
     *
     * @param key          the key for the desired value.
     * @param defaultValue the value to return if the key does not exist.
     *
     * @return the value associated with the key, or the specified default value if the key does not exist.
     */
    public static synchronized String getStringValue(String key, String defaultValue)
    {
        String v = getStringValue(key);
        return v != null ? v : defaultValue;
    }

    /**
     * Return as a string the value associated with a specified key.
     *
     * @param key the key for the desired value.
     *
     * @return the value associated with the key, or null if the key does not exist.
     */
    public static synchronized String getStringValue(String key)
    {
        Object o = getInstance().properties.getProperty(key);
        return o != null ? o.toString() : null;
    }

    /**
     * Return as an Integer the value associated with a specified key.
     *
     * @param key          the key for the desired value.
     * @param defaultValue the value to return if the key does not exist.
     *
     * @return the value associated with the key, or the specified default value if the key does not exist or is not an
     *         Integer or string representation of an Integer.
     */
    public static synchronized Integer getIntegerValue(String key, Integer defaultValue)
    {
        Integer v = getIntegerValue(key);
        return v != null ? v : defaultValue;
    }

    /**
     * Return as an Integer the value associated with a specified key.
     *
     * @param key the key for the desired value.
     *
     * @return the value associated with the key, or null if the key does not exist or is not an Integer or string
     *         representation of an Integer.
     */
    public static synchronized Integer getIntegerValue(String key)
    {
        String v = getStringValue(key);
        if (v == null)
            return null;

        try
        {
            return Integer.parseInt(v);
        }
        catch (NumberFormatException e)
        {
            Logging.logger().log(Level.SEVERE, "Configuration.ConversionError", v);
            return null;
        }
    }

    /**
     * Return as an Long the value associated with a specified key.
     *
     * @param key          the key for the desired value.
     * @param defaultValue the value to return if the key does not exist.
     *
     * @return the value associated with the key, or the specified default value if the key does not exist or is not a
     *         Long or string representation of a Long.
     */
    public static synchronized Long getLongValue(String key, Long defaultValue)
    {
        Long v = getLongValue(key);
        return v != null ? v : defaultValue;
    }

    /**
     * Return as an Long the value associated with a specified key.
     *
     * @param key the key for the desired value.
     *
     * @return the value associated with the key, or null if the key does not exist or is not a Long or string
     *         representation of a Long.
     */
    public static synchronized Long getLongValue(String key)
    {
        String v = getStringValue(key);
        if (v == null)
            return null;

        try
        {
            return Long.parseLong(v);
        }
        catch (NumberFormatException e)
        {
            Logging.logger().log(Level.SEVERE, "Configuration.ConversionError", v);
            return null;
        }
    }

    /**
     * Return as an Double the value associated with a specified key.
     *
     * @param key          the key for the desired value.
     * @param defaultValue the value to return if the key does not exist.
     *
     * @return the value associated with the key, or the specified default value if the key does not exist or is not an
     *         Double or string representation of an Double.
     */
    public static synchronized Double getDoubleValue(String key, Double defaultValue)
    {
        Double v = getDoubleValue(key);
        return v != null ? v : defaultValue;
    }

    /**
     * Return as an Double the value associated with a specified key.
     *
     * @param key the key for the desired value.
     *
     * @return the value associated with the key, or null if the key does not exist or is not an Double or string
     *         representation of an Double.
     */
    public static synchronized Double getDoubleValue(String key)
    {
        String v = getStringValue(key);
        if (v == null)
            return null;

        try
        {
            return Double.parseDouble(v);
        }
        catch (NumberFormatException e)
        {
            Logging.logger().log(Level.SEVERE, "Configuration.ConversionError", v);
            return null;
        }
    }

    /**
     * Determines whether a key exists in the configuration.
     *
     * @param key the key of interest.
     *
     * @return true if the key exists, otherwise false.
     */
    public static synchronized boolean hasKey(String key)
    {
        return getInstance().properties.contains(key);
    }

    /**
     * Removes a key and its value from the configuration if the configuration contains the key.
     *
     * @param key the key of interest.
     */
    public static synchronized void removeKey(String key)
    {
        getInstance().properties.remove(key);
    }

    /**
     * Adds a key and value to the configuration, or changes the value associated with the key if the key is already in
     * the configuration.
     *
     * @param key   the key to set.
     * @param value the value to associate with the key.
     */
    public static synchronized void setValue(String key, Object value)
    {
        getInstance().properties.put(key, value.toString());
    }

    // OS, user, and run-time specific system properties. //

    /**
     * Returns the path to the application's current working directory.
     *
     * @return the absolute path to the application's current working directory.
     */
    public static String getCurrentWorkingDirectory()
    {
        String dir = System.getProperty("user.dir");
        return (dir != null) ? dir : ".";
    }

    /**
     * Returns the path to the application user's home directory.
     *
     * @return the absolute path to the application user's home directory.
     */
    public static String getUserHomeDirectory()
    {
        String dir = System.getProperty("user.home");
        return (dir != null) ? dir : ".";
    }

    /**
     * Returns the path to the operating system's temp directory.
     *
     * @return the absolute path to the operating system's tempopory directory.
     */
    public static String getSystemTempDirectory()
    {
        String dir = System.getProperty("java.io.tmpdir");
        return (dir != null) ? dir : ".";
    }

    /**
     * Determines whether the operating system is a Mac operating system.
     *
     * @return true if the operating system is a Mac operating system, otherwise false.
     */
    public static boolean isMacOS()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("mac");
    }

    /**
     * Determines whether the operating system is Windows operating system.
     *
     * @return true if the operating system is a Windows operating system, otherwise false.
     */
    public static boolean isWindowsOS()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("windows");
    }

    /**
     * Determines whether the operating system is Windows XP operating system.
     *
     * @return true if the operating system is a Windows XP operating system, otherwise false.
     */
    public static boolean isWindowsXPOS()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("windows") && osName.contains("xp");
    }

    /**
     * Determines whether the operating system is Windows Vista operating system.
     *
     * @return true if the operating system is a Windows Vista operating system, otherwise false.
     */
    public static boolean isWindowsVistaOS()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("windows") && osName.contains("vista");
    }

    /**
     * Determines whether the operating system is Linux operating system.
     *
     * @return true if the operating system is a Linux operating system, otherwise false.
     */
    public static boolean isLinuxOS()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("linux");
    }

    /**
     * Determines whether the operating system is Unix operating system.
     *
     * @return true if the operating system is a Unix operating system, otherwise false.
     */
    public static boolean isUnixOS()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("unix");
    }

    /**
     * Determines whether the operating system is Solaris operating system.
     *
     * @return true if the operating system is a Solaris operating system, otherwise false.
     */
    public static boolean isSolarisOS()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("solaris");
    }

    /**
     * Returns the version of the Java virtual machine.
     *
     * @return the Java virtual machine version.
     */
    public static float getJavaVersion()
    {
        float ver = 0f;
        String s = System.getProperty("java.specification.version");
        if (null == s || s.length() == 0)
            s = System.getProperty("java.version");
        try
        {
            ver = Float.parseFloat(s.trim());
        }
        catch (NumberFormatException ignore)
        {
        }
        return ver;
    }

    /**
     * Returns a specified element of an XML configuration document.
     *
     * @param xpathExpression an XPath expression identifying the element of interest.
     *
     * @return the element of interest if the XPath expression is valid and the element exists, otherwise null.
     *
     * @throws NullPointerException if the XPath expression is null.
     */
    public static Element getElement(String xpathExpression)
    {
        XPath xpath = WWXML.makeXPath();

        for (Document doc : getInstance().configDocs)
        {
            try
            {
                Node node = (Node) xpath.evaluate(xpathExpression, doc.getDocumentElement(), XPathConstants.NODE);
                if (node != null)
                    return (Element) node;
            }
            catch (XPathExpressionException e)
            {
                return null;
            }
        }

        return null;
    }
}
