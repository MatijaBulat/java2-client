package hr.algebra.client.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class JndiHelper {
    private static final String PROVIDER_URL = "file:d:/";
    private static InitialContext initialContext;
    private static final String CONFIGURATION_FILE_NAME = "conf.properties.txt";

    private static InitialContext getInitialContext() throws NamingException {
        if(initialContext == null) {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
            props.setProperty(Context.PROVIDER_URL, PROVIDER_URL);
            initialContext = new InitialContext(props);
        }

        return initialContext;
    }

    public static String getValueFromConfiguration(String key) throws NamingException, IOException {
        Object configurationFileNameObject = getInitialContext().lookup(CONFIGURATION_FILE_NAME);
        Properties configurationProps = new Properties();
        configurationProps.load(new FileReader(configurationFileNameObject.toString()));
        return configurationProps.getProperty(key);
    }
}
