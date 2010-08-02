/*
 *  Copyright (c) 2009 Simon Hardijanto
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */
package magefortress.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import magefortress.core.Singleton;

/**
 * Singleton object that handles all image file i/o operations.
 * 
 */
public final class MFImageLibrary implements Singleton
{
    /** Singleton instance */
    private static MFImageLibrary instance;
    /** The library of cached images */
    private static Map<String, BufferedImage> library;

    /** Here shall lie the images */
    private static final String PATH_TO_ASSETS = "assets";
    /** Logger */
    private static final Logger logger = Logger.getLogger(MFImageLibrary.class.getName());

    /**
     * Hidden constructor. Instances can be created by calling
     * <code>getInstance()</code>.
     */
    private MFImageLibrary()
    {
        library = new HashMap<String, BufferedImage>();
    }

    /**
     * Returns an instance of the image library. On the first call in an
     * application it'll instantiate a new library and cache it for subsequent
     * requests.
     * @return An instance of the image library
     */
    public static synchronized MFImageLibrary getInstance()
    {
        if (instance == null) {
            instance = new MFImageLibrary();
        }

        return instance;
    }

    /**
     * The path to where the images are.
     * @return The path to the images
     */
    public String getAssetsPath()
    {
        return PATH_TO_ASSETS;
    }

    /**
     * Delivers an image. If it's cached it'll be snatched from there. Otherwise
     * it'll be loaded from disk and put into the cache.
     * @param _filename The name of the file inside the assets directory.
     * @return The image representation
     */
    public BufferedImage get(String _filename)
    {
        BufferedImage img;

        // try to load image from cache
        img = library.get(_filename);

        if (img == null) {
            // try to load image from disk
            img = loadFromFile(_filename);
            if (img != null) {
                // save sprite in cache
                library.put(_filename, img);
            } else {
                return null;
            }
        }

        return img;
    }

    /**
     * The number of images cached in the library.
     * @return The number of images in the library
     */
    public int getImageCount()
    {
        return library.size();
    }

    /**
     * Deletes all references to loaded images from the cache.
     */
    public void clear()
    {
        library.clear();
    }

    //---vvv---      PRIVATE METHODS      ---vvv---

    /**
     * Loads an uncached file from disk.
     * @param _filename The name of the file inside the assets directory.
     * @return The representation of an image.
     */
    private BufferedImage loadFromFile(String _filename)
    {
        BufferedImage img = null;
        String path = this.getAssetsPath() + System.getProperty("file.separator") + _filename;
            try {
                img = ImageIO.read(new File(path));
            } catch (IOException e) {
                path = System.getProperty("user.dir") + 
                       System.getProperty("file.separator") +
                       path;
                logger.log(Level.WARNING, "Image not found:" +  path, e);
                return null;
            }
        return img;
    }
}

