package hr.algebra.client.utils;

import java.io.*;
import java.util.Map;

public class SerializationUtils {
    private SerializationUtils() {
    }

    public static void writeObjects(Map<String, Object> objects, String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(objects);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readObjects(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Map<String, Object>) ois.readObject();
        }
    }
}
