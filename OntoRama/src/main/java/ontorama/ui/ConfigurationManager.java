package ontorama.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Handles all persistent configuration information.
 *
 * @TODO document
 * @TODO what do to with exceptions? At the moment they are caught and the default
 *       value is returned.
 */
public class ConfigurationManager {
    private static final String PROPERTIES_FILE_NAME = "ontorama.sessiondata";
    /**
     * Stores the data we manage.
     */
    static private Properties properties = new Properties();

    /**
     * Initialization code.
     */
    static {
        try {
            FileInputStream in = new FileInputStream(PROPERTIES_FILE_NAME);
            properties.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            // we will just use the defaults
        } catch (Exception e) {
            // nothing we can do here, just print the stack trace
            e.printStackTrace();
        }
    }

    /**
     * No public instances allowed.
     */
    private ConfigurationManager() {
    	// only change access level
    }

    /**
     * Saves the current configuration.
     */
    public static void saveConfiguration() {
        try {
            FileOutputStream out = new FileOutputStream(PROPERTIES_FILE_NAME);
            properties.store(out, "--- OntoRama session data ---");
            out.close();
        } catch (Exception e) {
            // nothing useful we can do here, just print the stack trace
            e.printStackTrace();
        }
    }

    /**
     * Stores the size and position of a window.
     */
    public static void storePlacement(String section, Window window) {
        properties.setProperty(section + "-x", String.valueOf(window.getX()));
        properties.setProperty(section + "-y", String.valueOf(window.getY()));
        properties.setProperty(section + "-width", String.valueOf(window.getWidth()));
        properties.setProperty(section + "-height", String.valueOf(window.getHeight()));
    }

    /**
     * Restores the size and position of a window.
     *
     * If the configuration could not be found or is broken this will do nothing.
     */
    public static void restorePlacement(String section, Window window, Rectangle defaultPlacement) {
        try {
            int x = Integer.parseInt(properties.getProperty(section + "-x"));
            int y = Integer.parseInt(properties.getProperty(section + "-y"));
            int w = Integer.parseInt(properties.getProperty(section + "-width"));
            int h = Integer.parseInt(properties.getProperty(section + "-height"));
            window.setBounds(x, y, w, h);
        } catch (NumberFormatException e) {
            // use default
            window.setBounds(defaultPlacement);
        }
    }

    /**
     * Stores an int value.
     */
    public static void storeInt(String section, String key, int value) {
        properties.setProperty(section + "-" + key, String.valueOf(value));
    }

    /**
     * Retrieves an int value.
     */
    public static int fetchInt(String section, String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(section + "-" + key));
        } catch (NumberFormatException e) {
            return defaultValue;
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Stores a float value.
     */
    public static void storeFloat(String section, String key, float value) {
        properties.setProperty(section + "-" + key, String.valueOf(value));
    }

    /**
     * Retrieves a float value.
     */
    public static float fetchFloat(String section, String key, float defaultValue) {
        try {
            return Float.parseFloat(properties.getProperty(section + "-" + key));
        } catch (NumberFormatException e) {
            return defaultValue;
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Stores a String value.
     */
    public static void storeString(String section, String key, String value) {
        properties.setProperty(section + "-" + key, value);
    }

    /**
     * Retrieves a String value.
     */
    public static String fetchString(String section, String key, String defaultValue) {
        return properties.getProperty(section + "-" + key, defaultValue);
    }

    /**
     * Stores a list of strings.
     */
    public static void storeStringList(String section, String key, List<String> list) {
        Iterator<String> it = list.iterator();
        int index = 1;
        while (it.hasNext()) {
            String cur = it.next();
            properties.setProperty(section + "-" + key + "-" + index, cur);
            index++;
        }
    }

    /**
     * Retrieves a list of strings.
     *
     * The list will at most contains maxItems items, maybe less if less are
     * found.
     */
    public static List<String> fetchStringList(String section, String key, int maxItems) {
        List<String> retVal = new ArrayList<String>();
        for (int i = 1; i <= maxItems; i++) {
            String cur = properties.getProperty(section + "-" + key + "-" + i);
            if (cur != null) {
                retVal.add(cur);
            }
        }
        return retVal;
    }

    /**
     * Stores a Color value.
     */
    public static void storeColor(String section, String key, Color value) {
        storeInt(section, key, value.getRGB());
    }

    /**
     * Retrieves a Color value.
     */
    public static Color fetchColor(String section, String key, Color defaultValue) {
        String propVal = properties.getProperty(section + "-" + key);
        if (propVal == null) {
            return defaultValue;
        } else {
            try {
                return Color.decode(propVal);
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                return defaultValue;
            }
        }
    }
}
