package fr.mdta.mdta.Tools;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class CacheStorage {
    private CacheStorage() {
    }

    public static void writeObject(Context context, String key, Object object) throws IOException {
        File file;
        ObjectOutputStream outputStream;

        file = new File(context.getCacheDir(), key);
        FileOutputStream ostream = new FileOutputStream(file);
        outputStream = new ObjectOutputStream(ostream);
        outputStream.writeObject(object);
        outputStream.close();

    }

    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        File file = new File(context.getCacheDir(), key);
        FileInputStream inputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        return objectInputStream.readObject();
    }


    public static void clearCache(Context context) {

        File dir = context.getCacheDir();
        if (dir != null && dir.isDirectory()) {
            deleteDir(dir);
        }

    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}
