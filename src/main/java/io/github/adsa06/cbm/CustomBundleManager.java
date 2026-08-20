package io.github.adsa06.cbm;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CustomBundleManager {
    
    private Map<String, String> bundle = new HashMap<>();
    private String baseName;

    public CustomBundleManager(String baseName, String target) {
        this.baseName = baseName;
        loadBundle(target);
    }

    public void loadBundle(String target) {
        String resourcePath = getResourcePath(baseName, target);

        bundle.clear();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("No se encontró el recurso: " + resourcePath);
            }
            Properties prop = new Properties();
            prop.load(is);

            for (String key : prop.stringPropertyNames()) {
                bundle.put(key, prop.getProperty(key));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando el bundle personalizado: " + resourcePath, e);
        }
    }

    public String getString(String id, Object... arguments) {
        String message = MessageFormat.format(
                bundle.get(id),
                arguments);
        return message;
    }

    private String getResourcePath(String baseName, String target) {
        String resourcePath = baseName.replace(".", "/") + "_" + target + ".properties";
        return resourcePath;
    }
}
