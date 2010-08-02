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

import magefortress.graphics.MFImageLibrary;
import java.awt.image.BufferedImage;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MFImageLibraryTest
{
    private static final String PATH_TO_ASSETS = "assets";
    private static final String VALID_IMAGE_NAME = "test.png";
    private static final String INVALID_IMAGE_NAME = "invalid_filename.abc";

    private MFImageLibrary library;

    @Before
    public void setUp()
    {
        library = MFImageLibrary.getInstance();
        library.clear();
    }

    @Test
    public void shouldGetInstance()
    {
        assertTrue(library instanceof MFImageLibrary);
    }

    @Test
    public void shouldGetPathToAssets()
    {
        String path = library.getAssetsPath();
        assertEquals(PATH_TO_ASSETS, path);
    }

    @Test
    public void shouldGetFile()
    {
        String filename = VALID_IMAGE_NAME;

        BufferedImage img = library.get(filename);
        assertNotNull(img);
    }

    @Test
    public void shouldNotGetNonExistentFile()
    {
        String filename = INVALID_IMAGE_NAME;

        BufferedImage img = library.get(filename);
        assertNull(img);
    }

    @Test
    public void shouldCacheImageOnGet()
    {
        int expImageCount = library.getImageCount() + 1;
        String filename = VALID_IMAGE_NAME;

        BufferedImage img = library.get(filename);
        assertNotNull(img);
        assertEquals(expImageCount, library.getImageCount());
    }

    @Test
    public void shouldNotCacheNonExistentImage()
    {
        int expImageCount = library.getImageCount();
        String filename = INVALID_IMAGE_NAME;

        BufferedImage img = library.get(filename);
        assertNull(img);
        assertEquals(expImageCount, library.getImageCount());
    }

    @Test
    public void shouldClearCache()
    {
       library.clear();
       assertEquals(0, library.getImageCount());
    }
}
