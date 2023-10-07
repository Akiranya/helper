package me.lucko.helper.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.Nullable;

public class ResourceExtractor {

    // Source: https://stackoverflow.com/a/24316335/10275532

    public static boolean copyFile(
            final @Nullable File origin,
            final @Nullable File target
    ) {
        if (origin == null || target == null)
            return false;
        try {
            return copyStream(new FileInputStream(origin), new FileOutputStream(target));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyResourceRecursively(
            final @Nullable URL origin,
            final @Nullable File targetDirectory
    ) {
        if (origin == null || targetDirectory == null)
            return false;
        try {
            final URLConnection urlConnection = origin.openConnection();
            if (urlConnection instanceof JarURLConnection)
                return copyJarResourcesRecursively((JarURLConnection) urlConnection, targetDirectory);
            else
                return copyFileRecursively(new File(origin.getPath()), targetDirectory);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyFileRecursively(
            final @Nullable File origin,
            final @Nullable File targetDirectory
    ) {
        if (origin == null || targetDirectory == null)
            return false;
        if (!targetDirectory.isDirectory())
            return false;
        if (!origin.isDirectory()) {
            return copyFile(origin, new File(targetDirectory, origin.getName()));
        } else {
            final File newDestinationDir = new File(targetDirectory, origin.getName());
            if (!newDestinationDir.exists() && !newDestinationDir.mkdir())
                return false;
            for (final File child : Objects.requireNonNull(origin.listFiles())) {
                if (!copyFileRecursively(child, newDestinationDir))
                    return false;
            }
        }
        return true;
    }

    private static boolean copyJarResourcesRecursively(
            final @Nullable JarURLConnection jarConnection,
            final @Nullable File targetDir
    ) throws IOException {
        if (targetDir == null || jarConnection == null)
            return false;
        final JarFile jarFile = jarConnection.getJarFile();
        for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
            final JarEntry entry = e.nextElement();
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                final String filename = removeStart(entry.getName(), jarConnection.getEntryName());
                final File file = new File(targetDir, filename);
                if (!entry.isDirectory()) {
                    final InputStream entryInputStream = jarFile.getInputStream(entry);
                    if (!copyStream(entryInputStream, file))
                        return false;
                    entryInputStream.close();
                } else {
                    if (!file.exists() && !file.mkdir()) // ensure directory exists
                        throw new IOException("Could not create directory: " + file.getAbsolutePath());
                }
            }
        }
        return true;
    }

    private static boolean copyStream(
            final @Nullable InputStream inputStream,
            final @Nullable File outputFile
    ) {
        if (inputStream == null || outputFile == null)
            return false;
        try {
            return copyStream(inputStream, new FileOutputStream(outputFile));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyStream(
            final @Nullable InputStream inputStream,
            final @Nullable OutputStream outputStream
    ) {
        if (inputStream == null || outputStream == null)
            return false;
        try {
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Source: org.apache.commons.lang3.StringUtils.removeStart
    private static String removeStart(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    // Source: org.apache.commons.lang3.StringUtils.isEmpty
    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

}
