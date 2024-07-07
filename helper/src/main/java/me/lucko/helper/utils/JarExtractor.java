package me.lucko.helper.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExtractor {
    public static void extractJar(ClassLoader classLoader, String targetDirectory, File destinationDirectory) {
        try {
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs();
            }

            // Ensure targetDirectory ends with '/'
            if (!targetDirectory.endsWith("/")) {
                targetDirectory = targetDirectory + "/";
            }

            // Get the URL of the resource
            URL resourceUrl = classLoader.getResource(targetDirectory);
            if (resourceUrl == null) {
                throw new FileNotFoundException("Resource not found: " + targetDirectory);
            }

            // Convert to JAR file URL
            String jarPath;
            try {
                URI uri = resourceUrl.toURI();
                // Use toString() to handle spaces and special characters correctly
                String decodedUri = URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
                if (decodedUri.startsWith("jar:file:")) {
                    jarPath = decodedUri.substring(9, decodedUri.indexOf("!"));
                } else if (decodedUri.startsWith("file:")) {
                    jarPath = decodedUri.substring(5);
                } else {
                    throw new IOException("Unexpected URI scheme in " + decodedUri);
                }
            } catch (URISyntaxException e) {
                throw new IOException("Invalid URI syntax: " + resourceUrl, e);
            }

            try (JarFile jar = new JarFile(new File(jarPath))) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    // Check if the entry is within the target folder
                    if (entryName.startsWith(targetDirectory)) {
                        String relativePath = entryName.substring(targetDirectory.length());

                        // Ensure the relative path does not start with a '/'
                        if (relativePath.startsWith("/")) {
                            relativePath = relativePath.substring(1);
                        }

                        File entryDestination = new File(destinationDirectory, relativePath);

                        if (entry.isDirectory()) {
                            entryDestination.mkdirs();
                        } else {
                            File parent = entryDestination.getParentFile();
                            if (parent != null && !parent.exists()) {
                                parent.mkdirs();
                            }

                            try (InputStream in = jar.getInputStream(entry); OutputStream out = new FileOutputStream(entryDestination)) {
                                copy(in, out);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }
}
