/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Service.
 *
 *     SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Service is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.util;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class FileReader {
    private static final Logger LOG = Logger.getLogger(FileReader.class);

    public String readFileContents(final String fileName) {
        final StringBuilder text = new StringBuilder();
        final String newLine = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            final InputStream resourceAsStream = FileReader.class.getClassLoader().getResourceAsStream(fileName);
            if (resourceAsStream == null) {
                LOG.error("File not found: " + fileName);
                return null;
            } else {
                LOG.info(fileName + " found in classpath");
            }
            scanner = new Scanner(resourceAsStream);
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + newLine);
            }
        } catch (final Exception e) {
            LOG.error("Problem reading file : " + fileName, e);
            return null;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return text.toString();
    }

    public byte[] readBinaryFileContents(final String fileName) {
        try {
            InputStream resourceAsStream = FileReader.class.getClassLoader().getResourceAsStream(fileName);
            return IOUtils.toByteArray(resourceAsStream);
        } catch (IOException e) {
            LOG.error("Problem reading file : " + fileName, e);
            return null;
        }
    }

}
