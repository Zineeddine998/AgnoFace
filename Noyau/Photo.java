package Noyau;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Photo {
    private static final int IMG__WIDTH = 92;
    private static final int IMG__HEIGHT = 112;
    public static String conversion (String  inFilename, int name){


        String outFilename ="./convert/"+name+"ajout.pgm";
        //CONVERSION EN NIVEAUX DE GRIS
        try {
            //String extension = inFilename.substring(inFilename.lastIndexOf("."));
            //System.out.println(extension.substring(1));
            System.out.println("Début de conversion....");
            //Ouverture du fichier
            File inputFile = new File(inFilename);
            BufferedImage imagesrc = ImageIO.read(inputFile);
            //Convertion en grisé
            BufferedImage imagedst = new BufferedImage(imagesrc.getWidth(),

                    imagesrc.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = imagedst.getGraphics();
            g.drawImage(imagesrc, 0, 0, null);
            g.dispose();
            //Enregistrer l'image au format PNG
            File outFile = new File(outFilename);
            ImageIO.write(imagedst, "png", new File("./src/Image/nGris.png"));
            System.out.println("Fin de conversion....");
        } catch (IOException ex) {
            Logger.getLogger(Photo.class.getName()).log(Level.SEVERE, null, ex);
        }

        try{
            //REDIMENTION

            BufferedImage originalImage = ImageIO.read(new File("./src/Image/nGris.png"));
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageHintPng = resizeImageWithHint(originalImage, type);
            //conversion en pgm
            ImageIO.write(resizeImageHintPng, "pnm", new File(outFilename));




        }catch(IOException e){
            System.out.println(e.getMessage());
        }

        return outFilename;
    }


    private static BufferedImage resizeImage(BufferedImage originalImage, int type){
        BufferedImage resizedImage = new BufferedImage(IMG__WIDTH, IMG__HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG__WIDTH, IMG__HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

    private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){

        BufferedImage resizedImage = new BufferedImage(IMG__WIDTH, IMG__HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG__WIDTH, IMG__HEIGHT, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;
    }
}
