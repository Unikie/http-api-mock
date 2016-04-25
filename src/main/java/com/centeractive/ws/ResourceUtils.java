/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA
 */
package com.centeractive.ws;

import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Loads resources from the classpath in a relatively seamless way.<br/>
 * Simplifies the Java API for resource loading.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class ResourceUtils {

    public static URL getResourceWithAbsolutePackagePath(String absolutePackagePath, String resourceName) {
        return getResourceWithAbsolutePackagePath(ResourceUtils.class, absolutePackagePath, resourceName);
    }

    private static class Path {
        String packagePath = "";
        String resourcePath = "";
    }

    private static String getFullPath(String resourcePath) {
        int linuxIndex = resourcePath.lastIndexOf("/");
        int windowsIndex = resourcePath.lastIndexOf("\\");
        int index = Math.max(linuxIndex, windowsIndex);
        if (index < 0) {
            return "";
        }
        return resourcePath.substring(0, index);
    }

    private static Path parsePath(String resourcePath) {
        checkNotNull(resourcePath, "resourcePath cannot be null");
        Path path = new Path();
        path.packagePath = getFullPath(resourcePath);
        path.resourcePath = FilenameUtils.getName(resourcePath);
        return path;
    }

    public static URL getResource(String resourcePath) {
        Path path = parsePath(resourcePath);
        return getResourceWithAbsolutePackagePath(path.packagePath, path.resourcePath);
    }

    public static URL getResource(Class<?> clazz, String resourcePath) {
        Path path = parsePath(resourcePath);
        return getResourceWithAbsolutePackagePath(clazz, path.packagePath, path.resourcePath);
    }

    public static InputStream getResourceAsStream(String resourcePath) {
        Path path = parsePath(resourcePath);
        return getResourceWithAbsolutePackagePathAsStream(path.packagePath, path.resourcePath);
    }

    public static InputStream getResourceAsStream(Class<?> clazz, String resourcePath) {
        Path path = parsePath(resourcePath);
        return getResourceWithAbsolutePackagePathAsStream(clazz, path.packagePath, path.resourcePath);
    }

    public static URL getResourceWithAbsolutePackagePath(Class<?> clazz, String absolutePackagePath, String resourceName) {
        checkNotNull(clazz, "clazz cannot be null");
        String resourcePath = getResourcePath(absolutePackagePath, resourceName);
        URL resource = null;
        // first attempt - outside/inside jar file
        resource = ResourceUtils.class.getClassLoader().getResource(resourcePath);

        // second attempt - servlet container - inside application lib folder
        if (resource == null) {
            if (resourcePath.charAt(0) == '/') {
                String resourcePathWithoutLeadingSlash = resourcePath.substring(1);
                resource = Thread.currentThread().getContextClassLoader().getResource(resourcePathWithoutLeadingSlash);
            }
        }
        checkArgument(resource != null, String.format("Resource [%s] loading failed", resourcePath));
        return resource;
    }

    public static InputStream getResourceWithAbsolutePackagePathAsStream(String absolutePackagePath, String resourceName) {
        return getResourceWithAbsolutePackagePathAsStream(ResourceUtils.class, absolutePackagePath, resourceName);
    }

    public static InputStream getResourceWithAbsolutePackagePathAsStream(Class<?> clazz, String absolutePackagePath, String resourceName) {
        checkNotNull(clazz, "clazz cannot be null");
        String resourcePath = getResourcePath(absolutePackagePath, resourceName);
        InputStream resource = null;
        // first attempt - outside/inside jar file
        resource = clazz.getClass().getResourceAsStream(resourcePath);
        // second attempt - servlet container - inside application lib folder
        if (resource == null) {
            ClassLoader classLoader = clazz.getClass().getClassLoader();
            if (classLoader != null)
                resource = classLoader.getResourceAsStream(resourcePath);
        }
        checkArgument(resource != null, String.format("Resource [%s] loading failed", resourcePath));
        return resource;
    }

    private static String getResourcePath(String absolutePackagePath, String resourceName) {
        checkNotNull(absolutePackagePath, "absolutePackagePath cannot be null");
        checkNotNull(resourceName, "resourceName cannot be null");
        absolutePackagePath = formatArgument(absolutePackagePath);
        resourceName = formatArgument(resourceName);
        return constructResourcePath(absolutePackagePath, resourceName);
    }

    private static String formatArgument(String argument) {
        String argumentWithoutWhiteSpaces = argument.trim();
        return argumentWithoutWhiteSpaces;
    }

    private static String constructResourcePath(String packagePath, String resourceName) {
        String resourcePath = String.format("/%s/%s", packagePath, resourceName);
        String resourcePathUnixSeparators = FilenameUtils.separatorsToUnix(resourcePath);
        String resourcePathNoLeadingSeparators = removeLeadingUnixSeparators(resourcePathUnixSeparators);
        String normalizedResourcePath = FilenameUtils.normalizeNoEndSeparator(resourcePathNoLeadingSeparators);
        return normalizedResourcePath;
    }

    private static String removeLeadingUnixSeparators(String argument) {
        return argument.replaceAll("/+", "/");
    }

}
