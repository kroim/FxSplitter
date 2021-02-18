package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

public class ImageProcessing {

    public String resize(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight) {
        // reads input image
        System.out.println(inputImagePath);
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = null;
        try {
            inputImage = ImageIO.read(inputFile);
            // creates output image
            BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());
            // scales the input image to the output image
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();
            // extracts extension of output file
            String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);
            // writes to output file
            ImageIO.write(outputImage, formatName, new File(outputImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputImagePath;
    }

    public Boolean convertToBlackWhite(String inputImagePath, String outputImagePath) {
        try {
            File input = new File(inputImagePath);
            BufferedImage image = ImageIO.read(input);
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D graphic = result.createGraphics();
            graphic.drawImage(image, 0, 0, Color.WHITE, null);
            graphic.dispose();
            File output = new File(outputImagePath);
            ImageIO.write(result, "png", output);
            return true;
        }  catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int[][] getPixelColor() {
        int[][] pixels = new int[320][320];
        for (int i = 0; i < 320; i++) {
            for (int j = 0; j < 320; j++) {
                pixels[i][j] = 0;
            }
        }
        BufferedImage img = null;
        File f = null;
        //read image
        try{
            f = new File("lib/bwPattern.png");
            img = ImageIO.read(f);
            //get image width and height
            int width = img.getWidth();
            int height = img.getHeight();
            //get pixel value
            if (width > 320) width = 320;
            if (height > 320) height = 320;
            for (int i_h = 0; i_h < height; i_h++) {
                for (int i_w = 0; i_w < width; i_w++) {
                    if (img.getRGB(i_h, i_w) == -1)
                        pixels[i_h][i_w] = 1;
                }
            }
        }catch(IOException e){
            System.out.println(e);
        }
        return pixels;
    }

    public Image makeColorTransparent(final BufferedImage im, final Color color)
    {
        final ImageFilter filter = new RGBImageFilter()
        {
            // the color we are looking for (white)... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0x00F0F8FF;
            public final int filterRGB(final int x, final int y, final int rgb)
            {
                if ((rgb | 0xFF000000) == markerRGB)
                {
                    // Mark the alpha bits as zero - transparent
                    return 0x00F0F8FF & rgb;
                }
                else
                {
                    // nothing to do
                    return rgb;
                }
            }
        };
        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
    private BufferedImage[] imageToBufferedImage(final Image image)
    {
        int _row = image.getHeight(null);
        int _col = image.getWidth(null);
        if (_row > 320) _row = 320;
        if (_col > 320) _col = 320;
        int chunks = _row * _col;
        BufferedImage imgs[] = new BufferedImage[chunks];
        int count = 0;
        for (int x = 0; x < _row; x++) {
            for (int y = 0; y < _col; y++){
                imgs[count] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D g2 = imgs[count].createGraphics();
                g2.drawImage(image, 0, 0, 1, 1, y, x, y + 1, x + 1, null);
                g2.dispose();
                count++;
            }
        }
        return imgs;
    }
    public void mergeImg(BufferedImage[] buffImages) {
        int[][] pixel_values = getPixelColor();
        int rows = pixel_values.length;
        int cols = pixel_values[0].length;
        int type = buffImages[0].TYPE_4BYTE_ABGR_PRE;

        //Initializing the final image
        BufferedImage finalImg1 = new BufferedImage(cols, rows, type);
        BufferedImage finalImg2 = new BufferedImage(cols, rows, type);

        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (pixel_values[i][j] == 0) {
                    finalImg1.createGraphics().drawImage(buffImages[num], j, i, null);
                } else {
                    finalImg2.createGraphics().drawImage(buffImages[num], j, i, null);
                }
                num++;
            }
        }
        System.out.println("Image concatenated.....");
        try {
            ImageIO.write(finalImg1, "png", new File("lib/result1.png"));
            ImageIO.write(finalImg2, "png", new File("lib/result2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Image output .....");
    }
    public Boolean splitImages(String imgPath) {
        final File in = new File(imgPath);
        try {
            final BufferedImage source = ImageIO.read(in);
            final int color = source.getRGB(0, 0);
            final Image imageWithTransparency = makeColorTransparent(source, new Color(color));
            final BufferedImage[] transparentImage = imageToBufferedImage(imageWithTransparency);
            mergeImg(transparentImage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
