/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.web.internal.scan.tomcat;

import com.google.common.collect.AbstractIterator;
import org.reflections.vfs.Vfs;
import org.seedstack.shed.exception.SeedException;
import org.seedstack.seed.core.utils.SeedLoggingUtils;
import org.seedstack.seed.web.internal.WebErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * VFS directory implementation for JNDI JAR scanning.
 *
 * @author adrien.lauer@mpsa.com
 */
class JndiJarInputDir implements Vfs.Dir {
    private static final Logger LOGGER = LoggerFactory.getLogger(JndiJarInputDir.class);

    private final URL url;
    private JarInputStream jarInputStream;

    JndiJarInputDir(URL url) {
        this.url = url;
    }

    @Override
    public String getPath() {
        return url.getPath();
    }

    @Override
    public Iterable<Vfs.File> getFiles() {
        return () -> new AbstractIterator<Vfs.File>() {
            {
                try {
                    jarInputStream = new JarInputStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    SeedLoggingUtils.logWarningWithDebugDetails(LOGGER, e, "Unable to open JAR at {}, ignoring it", url.toExternalForm());
                }
            }

            @Override
            protected Vfs.File computeNext() {
                if (jarInputStream == null) {
                    return endOfData();
                }

                while (true) {
                    try {
                        ZipEntry entry = jarInputStream.getNextEntry();
                        if (entry == null) {
                            return endOfData();
                        }

                        if (!entry.isDirectory()) {
                            return new JndiJarInputFile(entry, jarInputStream);
                        }
                    } catch (IOException e) {
                        throw SeedException.wrap(e, WebErrorCode.UNABLE_TO_SCAN_TOMCAT_JNDI_JAR);
                    }
                }
            }
        };
    }

    @Override
    public void close() {
        try {
            jarInputStream.close();
        } catch (IOException e) {
            SeedLoggingUtils.logWarningWithDebugDetails(LOGGER, e, "Unable to close JAR at {}", url.toExternalForm());
        }
    }

}
