package skyart.skyffti;

/**
 * Created by Coltan on 3/3/2017.
 */


import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.rauschig.jarchivelib.Compressor;
/**
 *
 * @author Coltan
 *
 */
public class ImageReducer {

    static int rows = 2;
    static int cols = 2;

    public static void reduce(Bitmap image) throws FileNotFoundException, IOException {


        int chunkWidth = image.getWidth() / rows;
        int chunkHeight = image.getHeight() / cols;
        Bitmap chnks[] = new Bitmap[rows * cols];

        int chnkIndex = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                //init image
               // chnks[chnkIndex] = Bitmap.createBitmap(chunkWidth, chunkHeight, Bitmap.Config.ARGB_4444);

                chnks[chnkIndex] = Bitmap.createBitmap(image, chunkWidth, chunkHeight, chunkWidth * i, chunkHeight * j);

                chnkIndex++;

            }
        }

        System.out.println("Splitting done");

        //writing mini images into image files
        for (int i = 0; i < chnks.length; i++) {

            if (isBlank(chnks[i]) == false) {
                System.out.println(i+": " + isBlank(chnks[i]));

            }
        }

        System.out.println("Mini images created" );

       // combiner(chnks);

    }

    public static boolean isBlank(Bitmap img) {

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {


                int clr = img.getPixel(x,y);
                if (clr != 0) {
                    return false;
                }

            }
        }

        return true;
    }

//    public static void combiner(BufferedImage chnks[]) throws IOException {
//
//        BufferedImage img = new BufferedImage(4096, 2304, 6);
//
//        int chunkWidth = img.getWidth() / rows;
//        int chunkHeight = img.getHeight() / cols;
//
//        int index = 0;
//        for (int x = 0; x < rows; x++) {
//            for (int y = 0; y < cols; y++) {
//                Graphics2D g = img.createGraphics();
//
//                g.drawImage(chnks[index], chunkWidth * x, chunkHeight * y, null);
//                g.dispose();
//                index++;
//            }
//        }
//        isBlank(img);
//        ImageIO.write(img, "png", new File("/home/developer/Downloads/Main.png"));
//
//    }

}
