package com.zizhizhan.legacies.scan.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.zizhizhan.legacies.scan.util.Closing;

public final class JarFileScanner {

    public static void scan(final File f, final String parent, final ScannerListener sl) throws IOException {
        new Closing(new FileInputStream(f)).f(in -> scan(in, parent, sl));
    }

    public static void scan(final InputStream in, final String parent, final ScannerListener sl) throws IOException {
        JarInputStream jarIn = null;
        try {
            jarIn = new JarInputStream(in);
            JarEntry e = jarIn.getNextJarEntry();
            while (e != null) {
                if (!e.isDirectory() && e.getName().startsWith(parent)
                        && sl.onAccept(e.getName())) {
                    sl.onProcess(e.getName(), jarIn);
                }
                jarIn.closeEntry();
                e = jarIn.getNextJarEntry();
            }
        } finally {
            if (jarIn != null) {
                jarIn.close();
            }
        }
    }

}
