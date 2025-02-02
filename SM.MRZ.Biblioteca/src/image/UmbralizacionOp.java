package image;
import java.awt.Color;
import java.awt.image.BufferedImage;
import sm.image.BufferedImageOpAdapter;

/**
 * Clase PurpleOp. Hereda de BufferedImageOpAdapter. Implementa una operación
 * punto a punto sobre la imagen que umbraliza la imagen: si la intensidad del
 * color está por encima del umbral se convertirá en blanco en la imagen
 * resultante y en negro en caso contrario.
 * @author Montserrat Rodríguez Zamorano
 * @version 1.1
 */

public class UmbralizacionOp extends BufferedImageOpAdapter{
    private int umbral;
    /**
     * Crea un objeto UmbralizacionOp
     * @param umbral int umbral para la umbralización
     */
    public UmbralizacionOp(int umbral){
        this.umbral = umbral;
    }
    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if(src == null){
            throw new NullPointerException("src image is null");
        }
        if(dest == null){
            dest = createCompatibleDestImage(src, null);
        }
        
        for(int x=0; x<src.getWidth(); x++){
            for(int y=0; y<src.getHeight(); y++){
                int srcR, srcG, srcB;
                Color colorSrc = new Color(src.getRGB(x, y));
                //colores del pixel
                srcR = colorSrc.getRed();
                srcG = colorSrc.getGreen();
                srcB = colorSrc.getBlue();
                //intensidad como media de sus componentes
                int intensidad = (srcR + srcG + srcB)/3;
                if(intensidad>=umbral){
                    dest.setRGB(x, y, Color.WHITE.getRGB());
                }
                else{//intensidad < umbral
                    dest.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        
        return dest;
    }
    
}