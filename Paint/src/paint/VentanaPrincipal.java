package paint;

import eventos.LienzoAdapter;
import eventos.LienzoEvent;
import graficos.Elipse;
import graficos.Figura;
import graficos.FiguraRellenable;
import graficos.Forma;
import graficos.Linea;
import graficos.Rectangulo;
import graficos.RectanguloRedondeado;
import iu.ColorRenderer;
import iu.Lienzo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import sm.sound.SMClipPlayer;
import sm.sound.SMSoundRecorder;
import graficos.TipoRelleno;
import iu.RellenoRenderer;
import graficos.TipoLinea;
import image.SepiaOp;
import image.UmbralizacionOp;
import image.AverageOp;
import image.PurpleOp;
import iu.LienzoImagen;
import iu.TipoLineaRenderer;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.image.EqualizationOp;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.TintOp;
import uk.co.caprica.vlcj.filter.AudioFileFilter;
import uk.co.caprica.vlcj.filter.VideoFileFilter;

/**
 *
 * @author Montserrat Rodríguez Zamorano
 * @version 1.1
 */

public class VentanaPrincipal extends javax.swing.JFrame {
    private File temp = null;
    SMSoundRecorder recorder = null;
    SMClipPlayer player = null;
    private boolean grabando = false;
    BufferedImage imgSource;
    BufferedImage imgdest;
    Point pointTemp = new Point(0,0);
    Figura fSeleccionada = null;
    
    FileNameExtensionFilter imageFilter, soundFilter, videoFilter;
    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
        
        colorTrazoCB.removeAllItems();
        colorRellenoCB.removeAllItems();
        degradado1CB.removeAllItems();
        degradado2CB.removeAllItems();
        colorTintadoCB.removeAllItems();
        tipoRellenoCB.removeAllItems();
        tipoLineaCB.removeAllItems();
        inicializarColores(colorTrazoCB);
        inicializarColores(colorRellenoCB);
        inicializarColores(degradado1CB);
        inicializarColores(degradado2CB);
        inicializarColores(colorTintadoCB);
        inicializarRelleno(tipoRellenoCB);
        inicializarTipoLinea(tipoLineaCB);
 
        //sliders desactivados
        tintadoSlider.setEnabled(false);
        //botones de sonido
        stopRecordBoton.setEnabled(false);
        pausaGrabacionBoton.setEnabled(false);
        
        //filtros
        imageFilter = new FileNameExtensionFilter("Archivos de imagen", ImageIO.getReaderFormatNames());
        soundFilter = new FileNameExtensionFilter("Archivos de sonido", AudioFileFilter.INSTANCE.getExtensions());
        videoFilter = new FileNameExtensionFilter("Archivos de video", VideoFileFilter.INSTANCE.getExtensions());
        
    }
   
    /**
     * 
     */
    class MiManejadorLienzo extends LienzoAdapter{
        /**
         * 
         * @param evt 
         */
        @Override
        public void shapeAdded(LienzoEvent evt){
            eventoLabel.setText("Figura " + evt.getFigura() + " añadida");
            FigurasCB.addItem(evt.getFigura());
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void mouseMoved(LienzoEvent evt){
            double x = evt.getPosicion().getX();
            double y = evt.getPosicion().getY();
            pixelLabel.setText("(" + x + "," + y + ")");
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void faltaForma(LienzoEvent evt){
            String mensaje = "Debe seleccionar una forma de dibujo.";
            JOptionPane.showMessageDialog(null, mensaje, "¡Atención!", JOptionPane.ERROR_MESSAGE);
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void faltaColorTrazo(LienzoEvent evt){
            String mensaje = "Debe seleccionar un color de trazo.";
            JOptionPane.showMessageDialog(null, mensaje, "¡Atención!", JOptionPane.ERROR_MESSAGE);
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void faltaTipoLinea(LienzoEvent evt){
            String mensaje = "Debe seleccionar un tipo de línea.";
            JOptionPane.showMessageDialog(null, mensaje, "¡Atención!", JOptionPane.ERROR_MESSAGE);
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void faltaTipoRelleno(LienzoEvent evt){
            String mensaje = "Debe seleccionar un tipo de relleno.";
            JOptionPane.showMessageDialog(null, mensaje, "¡Atención!", JOptionPane.ERROR_MESSAGE);
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void faltaColorRelleno(LienzoEvent evt){
            String mensaje = "Debe seleccionar un color de relleno.";
            JOptionPane.showMessageDialog(null, mensaje, "¡Atención!", JOptionPane.ERROR_MESSAGE);
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void faltaColorDegradado(LienzoEvent evt){
            String mensaje = "Debe seleccionar dos colores de degradado.";
            JOptionPane.showMessageDialog(null, mensaje, "¡Atención!", JOptionPane.ERROR_MESSAGE);
        }
        /**
         * 
         * @param evt 
         */
        @Override
        public void lienzoSeleccionado(LienzoEvent evt){
            VentanaMultimediaImagen vi;
            vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
            if(vi != null){
                actualizarAtributosLienzo(vi.getLienzo());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        formasBG = new javax.swing.ButtonGroup();
        sonidoBG = new javax.swing.ButtonGroup();
        rotacionBG = new javax.swing.ButtonGroup();
        jButton4 = new javax.swing.JButton();
        barraSuperiorTB = new javax.swing.JToolBar();
        nuevoMB = new javax.swing.JButton();
        abrirMB = new javax.swing.JButton();
        guardarMB = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        lineaBoton = new javax.swing.JToggleButton();
        rectanguloBoton = new javax.swing.JToggleButton();
        rectanguloRedBoton = new javax.swing.JToggleButton();
        elipseBoton = new javax.swing.JToggleButton();
        FigurasCB = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        xPosition = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        yPosition = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        colorTrazoCB = new javax.swing.JComboBox<>();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        colorRellenoCB = new javax.swing.JComboBox<>();
        tipoRellenoCB = new javax.swing.JComboBox<>();
        degradado1CB = new javax.swing.JComboBox<>();
        degradado2CB = new javax.swing.JComboBox<>();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        transparenciaSlider = new javax.swing.JSlider();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        alisarBoton = new javax.swing.JToggleButton();
        grosorSpinner = new javax.swing.JSpinner();
        tipoLineaCB = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        recordSonidoBoton = new javax.swing.JToggleButton();
        pausaGrabacionBoton = new javax.swing.JToggleButton();
        stopRecordBoton = new javax.swing.JToggleButton();
        listaMediaCB = new javax.swing.JComboBox<>();
        reproducirSonidoBoton = new javax.swing.JToggleButton();
        pausaSonidoBoton = new javax.swing.JToggleButton();
        stopSonidoBoton = new javax.swing.JToggleButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        webcamBoton = new javax.swing.JButton();
        capturaBoton = new javax.swing.JButton();
        barraizquierdaTB = new javax.swing.JToolBar();
        brilloSlider = new javax.swing.JSlider();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        contrasteBoton = new javax.swing.JButton();
        iluminarBoton = new javax.swing.JButton();
        oscurecerBoton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        sepiaBoton = new javax.swing.JButton();
        tintarBoton = new javax.swing.JToggleButton();
        colorTintadoCB = new javax.swing.JComboBox<>();
        tintadoSlider = new javax.swing.JSlider();
        ecualizacionBoton = new javax.swing.JButton();
        umbralizacionSlider = new javax.swing.JSlider();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        zoomInBoton = new javax.swing.JButton();
        disminuirBoton = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        rot90Boton = new javax.swing.JButton();
        rot180Boton = new javax.swing.JButton();
        rot270Boton = new javax.swing.JButton();
        giroLibreSlider = new javax.swing.JSlider();
        barraEstado = new javax.swing.JPanel();
        pixelLabel = new javax.swing.JLabel();
        eventoLabel = new javax.swing.JLabel();
        barraDerechaTB = new javax.swing.JToolBar();
        disenioPropioBoton = new javax.swing.JButton();
        disenioPropio2Boton = new javax.swing.JButton();
        cosinusoideBoton = new javax.swing.JButton();
        duplicarBoton = new javax.swing.JButton();
        negativoBoton = new javax.swing.JButton();
        relieveBoton = new javax.swing.JButton();
        emborronamientoBoton2 = new javax.swing.JButton();
        enfoqueBoton = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        bandasBoton = new javax.swing.JButton();
        espacioColorCB = new javax.swing.JComboBox<>();
        escritorio = new javax.swing.JDesktopPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        menuBar = new javax.swing.JMenuBar();
        archivoMenuBar = new javax.swing.JMenu();
        nuevoMenuItem = new javax.swing.JMenuItem();
        abrirMenuItem = new javax.swing.JMenuItem();
        guardarMenuItem = new javax.swing.JMenuItem();
        verMenuBar = new javax.swing.JMenu();
        verBarraEstadoCB = new javax.swing.JCheckBoxMenuItem();
        verBarraIzqCB = new javax.swing.JCheckBoxMenuItem();
        verBarraSupCB = new javax.swing.JCheckBoxMenuItem();
        verBarraDerechaCH = new javax.swing.JCheckBoxMenuItem();
        ayudaMenuBar = new javax.swing.JMenu();
        acercaMenuItem = new javax.swing.JMenuItem();

        FormListener formListener = new FormListener();

        jButton4.setText("jButton4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(500, 500));

        barraSuperiorTB.setBorder(null);
        barraSuperiorTB.setRollover(true);
        barraSuperiorTB.setMaximumSize(new java.awt.Dimension(50, 30));
        barraSuperiorTB.setMinimumSize(new java.awt.Dimension(50, 30));
        barraSuperiorTB.setPreferredSize(new java.awt.Dimension(50, 30));

        nuevoMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/nuevo.png"))); // NOI18N
        nuevoMB.setToolTipText("Nueva imagen");
        nuevoMB.setFocusable(false);
        nuevoMB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nuevoMB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nuevoMB.addActionListener(formListener);
        barraSuperiorTB.add(nuevoMB);

        abrirMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/abrir.png"))); // NOI18N
        abrirMB.setToolTipText("Abrir archivo");
        abrirMB.setFocusable(false);
        abrirMB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        abrirMB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        abrirMB.addActionListener(formListener);
        barraSuperiorTB.add(abrirMB);

        guardarMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/save.png"))); // NOI18N
        guardarMB.setToolTipText("Guardar archivo");
        guardarMB.setFocusable(false);
        guardarMB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardarMB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guardarMB.addActionListener(formListener);
        barraSuperiorTB.add(guardarMB);
        barraSuperiorTB.add(jSeparator12);

        formasBG.add(lineaBoton);
        lineaBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Linea.gif"))); // NOI18N
        lineaBoton.setToolTipText("Línea");
        lineaBoton.setFocusable(false);
        lineaBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lineaBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lineaBoton.addActionListener(formListener);
        barraSuperiorTB.add(lineaBoton);

        formasBG.add(rectanguloBoton);
        rectanguloBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Rectangulo.gif"))); // NOI18N
        rectanguloBoton.setToolTipText("Rectángulo");
        rectanguloBoton.setFocusable(false);
        rectanguloBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectanguloBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectanguloBoton.addActionListener(formListener);
        barraSuperiorTB.add(rectanguloBoton);

        formasBG.add(rectanguloRedBoton);
        rectanguloRedBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/rectanguloR.png"))); // NOI18N
        rectanguloRedBoton.setToolTipText("Rectangulo redondeado");
        rectanguloRedBoton.setFocusable(false);
        rectanguloRedBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectanguloRedBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectanguloRedBoton.addActionListener(formListener);
        barraSuperiorTB.add(rectanguloRedBoton);

        formasBG.add(elipseBoton);
        elipseBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Ovalo.gif"))); // NOI18N
        elipseBoton.setToolTipText("Elipse");
        elipseBoton.setFocusable(false);
        elipseBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        elipseBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        elipseBoton.addActionListener(formListener);
        barraSuperiorTB.add(elipseBoton);

        FigurasCB.setToolTipText("Figuras dibujadas");
        FigurasCB.setMaximumSize(new java.awt.Dimension(200, 32767));
        FigurasCB.setMinimumSize(new java.awt.Dimension(200, 27));
        FigurasCB.setPreferredSize(new java.awt.Dimension(200, 27));
        FigurasCB.addActionListener(formListener);
        barraSuperiorTB.add(FigurasCB);

        jLabel1.setText("(");
        barraSuperiorTB.add(jLabel1);

        xPosition.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        xPosition.setText("0");
        xPosition.setToolTipText("Cambiar posición ");
        xPosition.setMaximumSize(new java.awt.Dimension(40, 30));
        xPosition.setMinimumSize(new java.awt.Dimension(40, 30));
        xPosition.setPreferredSize(new java.awt.Dimension(40, 30));
        xPosition.addActionListener(formListener);
        barraSuperiorTB.add(xPosition);

        jLabel2.setText(",");
        barraSuperiorTB.add(jLabel2);

        yPosition.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yPosition.setText("0");
        yPosition.setToolTipText("Cambiar posición");
        yPosition.setMaximumSize(new java.awt.Dimension(40, 30));
        yPosition.setMinimumSize(new java.awt.Dimension(40, 30));
        yPosition.setPreferredSize(new java.awt.Dimension(40, 30));
        yPosition.addActionListener(formListener);
        barraSuperiorTB.add(yPosition);

        jLabel3.setText(")");
        barraSuperiorTB.add(jLabel3);
        barraSuperiorTB.add(jSeparator1);

        colorTrazoCB.setToolTipText("Color trazo");
        colorTrazoCB.setMaximumSize(new java.awt.Dimension(60, 32767));
        colorTrazoCB.setMinimumSize(new java.awt.Dimension(60, 27));
        colorTrazoCB.setPreferredSize(new java.awt.Dimension(60, 27));
        colorTrazoCB.addActionListener(formListener);
        barraSuperiorTB.add(colorTrazoCB);
        barraSuperiorTB.add(jSeparator7);

        colorRellenoCB.setToolTipText("Color relleno");
        colorRellenoCB.setMaximumSize(new java.awt.Dimension(60, 32767));
        colorRellenoCB.setMinimumSize(new java.awt.Dimension(60, 27));
        colorRellenoCB.setPreferredSize(new java.awt.Dimension(60, 27));
        colorRellenoCB.addActionListener(formListener);
        barraSuperiorTB.add(colorRellenoCB);

        tipoRellenoCB.setToolTipText("Tipo relleno");
        tipoRellenoCB.setMaximumSize(new java.awt.Dimension(150, 32767));
        tipoRellenoCB.setMinimumSize(new java.awt.Dimension(150, 27));
        tipoRellenoCB.setPreferredSize(new java.awt.Dimension(200, 27));
        tipoRellenoCB.addActionListener(formListener);
        barraSuperiorTB.add(tipoRellenoCB);

        degradado1CB.setToolTipText("Primer color degradado");
        degradado1CB.setMaximumSize(new java.awt.Dimension(60, 32767));
        degradado1CB.setMinimumSize(new java.awt.Dimension(60, 27));
        degradado1CB.setPreferredSize(new java.awt.Dimension(60, 27));
        degradado1CB.addActionListener(formListener);
        barraSuperiorTB.add(degradado1CB);

        degradado2CB.setToolTipText("Segundo color degradado");
        degradado2CB.setMaximumSize(new java.awt.Dimension(60, 32767));
        degradado2CB.setMinimumSize(new java.awt.Dimension(60, 27));
        degradado2CB.setPreferredSize(new java.awt.Dimension(60, 27));
        degradado2CB.addActionListener(formListener);
        barraSuperiorTB.add(degradado2CB);
        barraSuperiorTB.add(jSeparator6);

        transparenciaSlider.setToolTipText("Nivel transparencia");
        transparenciaSlider.setValue(100);
        transparenciaSlider.setMaximumSize(new java.awt.Dimension(100, 29));
        transparenciaSlider.setMinimumSize(new java.awt.Dimension(100, 29));
        transparenciaSlider.setPreferredSize(new java.awt.Dimension(100, 29));
        transparenciaSlider.addChangeListener(formListener);
        barraSuperiorTB.add(transparenciaSlider);
        barraSuperiorTB.add(jSeparator2);

        alisarBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/alisar.png"))); // NOI18N
        alisarBoton.setToolTipText("Alisado");
        alisarBoton.setFocusable(false);
        alisarBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        alisarBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        alisarBoton.addActionListener(formListener);
        barraSuperiorTB.add(alisarBoton);

        grosorSpinner.setToolTipText("Grosor línea");
        grosorSpinner.setMaximumSize(new java.awt.Dimension(40, 32767));
        grosorSpinner.setMinimumSize(new java.awt.Dimension(40, 26));
        grosorSpinner.setPreferredSize(new java.awt.Dimension(40, 26));
        grosorSpinner.setValue(1);
        grosorSpinner.addChangeListener(formListener);
        barraSuperiorTB.add(grosorSpinner);

        tipoLineaCB.setMaximumSize(new java.awt.Dimension(100, 32767));
        tipoLineaCB.setMinimumSize(new java.awt.Dimension(100, 27));
        tipoLineaCB.setPreferredSize(new java.awt.Dimension(100, 27));
        tipoLineaCB.addActionListener(formListener);
        barraSuperiorTB.add(tipoLineaCB);
        barraSuperiorTB.add(jSeparator3);

        sonidoBG.add(recordSonidoBoton);
        recordSonidoBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/mic.png"))); // NOI18N
        recordSonidoBoton.setToolTipText("Iniciar grabación");
        recordSonidoBoton.setFocusable(false);
        recordSonidoBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        recordSonidoBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        recordSonidoBoton.addActionListener(formListener);
        barraSuperiorTB.add(recordSonidoBoton);

        sonidoBG.add(pausaGrabacionBoton);
        pausaGrabacionBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/pausemic.png"))); // NOI18N
        pausaGrabacionBoton.setToolTipText("Pausar grabación");
        pausaGrabacionBoton.setFocusable(false);
        pausaGrabacionBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pausaGrabacionBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pausaGrabacionBoton.addActionListener(formListener);
        barraSuperiorTB.add(pausaGrabacionBoton);

        sonidoBG.add(stopRecordBoton);
        stopRecordBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/stopmic.png"))); // NOI18N
        stopRecordBoton.setToolTipText("Finalizar grabación");
        stopRecordBoton.setFocusable(false);
        stopRecordBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopRecordBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopRecordBoton.addActionListener(formListener);
        barraSuperiorTB.add(stopRecordBoton);

        listaMediaCB.setToolTipText("Lista de sonidos");
        listaMediaCB.setMaximumSize(new java.awt.Dimension(100, 32767));
        listaMediaCB.setMinimumSize(new java.awt.Dimension(100, 27));
        listaMediaCB.addActionListener(formListener);
        barraSuperiorTB.add(listaMediaCB);

        sonidoBG.add(reproducirSonidoBoton);
        reproducirSonidoBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/play.png"))); // NOI18N
        reproducirSonidoBoton.setToolTipText("Play");
        reproducirSonidoBoton.setFocusable(false);
        reproducirSonidoBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reproducirSonidoBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reproducirSonidoBoton.addActionListener(formListener);
        barraSuperiorTB.add(reproducirSonidoBoton);

        sonidoBG.add(pausaSonidoBoton);
        pausaSonidoBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/pause.png"))); // NOI18N
        pausaSonidoBoton.setToolTipText("Pausa");
        pausaSonidoBoton.setFocusable(false);
        pausaSonidoBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pausaSonidoBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pausaSonidoBoton.addActionListener(formListener);
        barraSuperiorTB.add(pausaSonidoBoton);

        sonidoBG.add(stopSonidoBoton);
        stopSonidoBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/stop.png"))); // NOI18N
        stopSonidoBoton.setToolTipText("Stop");
        stopSonidoBoton.setFocusable(false);
        stopSonidoBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopSonidoBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stopSonidoBoton.addActionListener(formListener);
        barraSuperiorTB.add(stopSonidoBoton);
        barraSuperiorTB.add(jSeparator11);

        webcamBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/webcam.png"))); // NOI18N
        webcamBoton.setToolTipText("Abrir Webcam");
        webcamBoton.setFocusable(false);
        webcamBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        webcamBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        webcamBoton.addActionListener(formListener);
        barraSuperiorTB.add(webcamBoton);

        capturaBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/captura.png"))); // NOI18N
        capturaBoton.setToolTipText("Hacer captura de pantalla");
        capturaBoton.setFocusable(false);
        capturaBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        capturaBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        capturaBoton.addActionListener(formListener);
        barraSuperiorTB.add(capturaBoton);

        getContentPane().add(barraSuperiorTB, java.awt.BorderLayout.PAGE_START);

        barraizquierdaTB.setBorder(null);
        barraizquierdaTB.setOrientation(javax.swing.SwingConstants.VERTICAL);
        barraizquierdaTB.setRollover(true);
        barraizquierdaTB.setMaximumSize(new java.awt.Dimension(40, 30));
        barraizquierdaTB.setMinimumSize(new java.awt.Dimension(40, 30));
        barraizquierdaTB.setPreferredSize(new java.awt.Dimension(45, 35));

        brilloSlider.setBackground(new java.awt.Color(204, 204, 204));
        brilloSlider.setMinimum(-100);
        brilloSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        brilloSlider.setToolTipText("Brillo");
        brilloSlider.setValue(0);
        brilloSlider.setMaximumSize(new java.awt.Dimension(29, 100));
        brilloSlider.setPreferredSize(new java.awt.Dimension(29, 80));
        brilloSlider.addChangeListener(formListener);
        brilloSlider.addFocusListener(formListener);
        barraizquierdaTB.add(brilloSlider);
        barraizquierdaTB.add(jSeparator4);

        contrasteBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/contraste.png"))); // NOI18N
        contrasteBoton.setToolTipText("Contraste");
        contrasteBoton.setFocusable(false);
        contrasteBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        contrasteBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        contrasteBoton.addActionListener(formListener);
        barraizquierdaTB.add(contrasteBoton);

        iluminarBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/iluminar.png"))); // NOI18N
        iluminarBoton.setToolTipText("Iluminar");
        iluminarBoton.setFocusable(false);
        iluminarBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        iluminarBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        iluminarBoton.addActionListener(formListener);
        barraizquierdaTB.add(iluminarBoton);

        oscurecerBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/oscurecer.png"))); // NOI18N
        oscurecerBoton.setToolTipText("Oscurecer");
        oscurecerBoton.setFocusable(false);
        oscurecerBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        oscurecerBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        oscurecerBoton.addActionListener(formListener);
        barraizquierdaTB.add(oscurecerBoton);
        barraizquierdaTB.add(jSeparator5);

        sepiaBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/sepia.png"))); // NOI18N
        sepiaBoton.setToolTipText("Filtro sepia");
        sepiaBoton.setFocusable(false);
        sepiaBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sepiaBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sepiaBoton.addActionListener(formListener);
        barraizquierdaTB.add(sepiaBoton);

        tintarBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/tintado.png"))); // NOI18N
        tintarBoton.setToolTipText("Tintado");
        tintarBoton.setFocusable(false);
        tintarBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tintarBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tintarBoton.addActionListener(formListener);
        barraizquierdaTB.add(tintarBoton);

        colorTintadoCB.setToolTipText("Color tintado");
        colorTintadoCB.setMaximumSize(new java.awt.Dimension(60, 27));
        colorTintadoCB.setMinimumSize(new java.awt.Dimension(60, 27));
        colorTintadoCB.setPreferredSize(new java.awt.Dimension(60, 27));
        colorTintadoCB.addActionListener(formListener);
        barraizquierdaTB.add(colorTintadoCB);

        tintadoSlider.setBackground(new java.awt.Color(204, 204, 204));
        tintadoSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        tintadoSlider.setToolTipText("Nivel de tintado");
        tintadoSlider.setValue(0);
        tintadoSlider.setMaximumSize(new java.awt.Dimension(29, 100));
        tintadoSlider.setPreferredSize(new java.awt.Dimension(29, 80));
        tintadoSlider.addChangeListener(formListener);
        tintadoSlider.addFocusListener(formListener);
        barraizquierdaTB.add(tintadoSlider);

        ecualizacionBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/ecualizar.png"))); // NOI18N
        ecualizacionBoton.setToolTipText("Ecualización");
        ecualizacionBoton.setFocusable(false);
        ecualizacionBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ecualizacionBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ecualizacionBoton.addActionListener(formListener);
        barraizquierdaTB.add(ecualizacionBoton);

        umbralizacionSlider.setBackground(new java.awt.Color(204, 204, 204));
        umbralizacionSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        umbralizacionSlider.setToolTipText("Nivel de umbralización");
        umbralizacionSlider.setMaximumSize(new java.awt.Dimension(29, 100));
        umbralizacionSlider.setPreferredSize(new java.awt.Dimension(29, 80));
        umbralizacionSlider.addChangeListener(formListener);
        umbralizacionSlider.addFocusListener(formListener);
        barraizquierdaTB.add(umbralizacionSlider);
        barraizquierdaTB.add(jSeparator8);

        zoomInBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/zoomin.png"))); // NOI18N
        zoomInBoton.setToolTipText("Zoom in");
        zoomInBoton.setFocusable(false);
        zoomInBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomInBoton.addActionListener(formListener);
        barraizquierdaTB.add(zoomInBoton);

        disminuirBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/zoomout.png"))); // NOI18N
        disminuirBoton.setToolTipText("Zoom out");
        disminuirBoton.setFocusable(false);
        disminuirBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        disminuirBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        disminuirBoton.addActionListener(formListener);
        barraizquierdaTB.add(disminuirBoton);
        barraizquierdaTB.add(jSeparator9);

        rot90Boton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion90.png"))); // NOI18N
        rot90Boton.setToolTipText("Rotación 90º");
        rot90Boton.setFocusable(false);
        rot90Boton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rot90Boton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rot90Boton.addActionListener(formListener);
        barraizquierdaTB.add(rot90Boton);

        rot180Boton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion180.png"))); // NOI18N
        rot180Boton.setToolTipText("Rotacion 180º");
        rot180Boton.setFocusable(false);
        rot180Boton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rot180Boton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rot180Boton.addActionListener(formListener);
        barraizquierdaTB.add(rot180Boton);

        rot270Boton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/rotacion270.png"))); // NOI18N
        rot270Boton.setToolTipText("Rotación 270º");
        rot270Boton.setFocusable(false);
        rot270Boton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rot270Boton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rot270Boton.addActionListener(formListener);
        barraizquierdaTB.add(rot270Boton);

        giroLibreSlider.setBackground(new java.awt.Color(204, 204, 204));
        giroLibreSlider.setMaximum(360);
        giroLibreSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        giroLibreSlider.setToolTipText("Giro libre");
        giroLibreSlider.setValue(0);
        giroLibreSlider.setMaximumSize(new java.awt.Dimension(29, 100));
        giroLibreSlider.setPreferredSize(new java.awt.Dimension(29, 80));
        giroLibreSlider.addChangeListener(formListener);
        giroLibreSlider.addFocusListener(formListener);
        barraizquierdaTB.add(giroLibreSlider);

        getContentPane().add(barraizquierdaTB, java.awt.BorderLayout.LINE_START);

        barraEstado.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        barraEstado.setMaximumSize(new java.awt.Dimension(20, 30));
        barraEstado.setMinimumSize(new java.awt.Dimension(20, 30));
        barraEstado.setPreferredSize(new java.awt.Dimension(20, 30));

        javax.swing.GroupLayout barraEstadoLayout = new javax.swing.GroupLayout(barraEstado);
        barraEstado.setLayout(barraEstadoLayout);
        barraEstadoLayout.setHorizontalGroup(
            barraEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barraEstadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pixelLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1607, Short.MAX_VALUE)
                .addComponent(eventoLabel)
                .addContainerGap())
        );
        barraEstadoLayout.setVerticalGroup(
            barraEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barraEstadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(barraEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(eventoLabel)
                    .addComponent(pixelLabel))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        getContentPane().add(barraEstado, java.awt.BorderLayout.PAGE_END);

        barraDerechaTB.setBorder(null);
        barraDerechaTB.setOrientation(javax.swing.SwingConstants.VERTICAL);
        barraDerechaTB.setRollover(true);
        barraDerechaTB.setMaximumSize(new java.awt.Dimension(100, 30));
        barraDerechaTB.setMinimumSize(new java.awt.Dimension(100, 30));
        barraDerechaTB.setPreferredSize(new java.awt.Dimension(30, 30));

        disenioPropioBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/filtrovioleta.png"))); // NOI18N
        disenioPropioBoton.setToolTipText("Filtro violeta");
        disenioPropioBoton.setFocusable(false);
        disenioPropioBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        disenioPropioBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        disenioPropioBoton.addActionListener(formListener);
        barraDerechaTB.add(disenioPropioBoton);

        disenioPropio2Boton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/mediabandas.png"))); // NOI18N
        disenioPropio2Boton.setToolTipText("Filtro blanco y negro");
        disenioPropio2Boton.setFocusable(false);
        disenioPropio2Boton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        disenioPropio2Boton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        disenioPropio2Boton.addActionListener(formListener);
        barraDerechaTB.add(disenioPropio2Boton);

        cosinusoideBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/coseno.png"))); // NOI18N
        cosinusoideBoton.setToolTipText("Cosinusoide");
        cosinusoideBoton.setFocusable(false);
        cosinusoideBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cosinusoideBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cosinusoideBoton.addActionListener(formListener);
        barraDerechaTB.add(cosinusoideBoton);

        duplicarBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/duplicar.png"))); // NOI18N
        duplicarBoton.setToolTipText("Duplicar");
        duplicarBoton.setFocusable(false);
        duplicarBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        duplicarBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        duplicarBoton.addActionListener(formListener);
        barraDerechaTB.add(duplicarBoton);

        negativoBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/negativo.png"))); // NOI18N
        negativoBoton.setToolTipText("Negativo");
        negativoBoton.setFocusable(false);
        negativoBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        negativoBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        negativoBoton.addActionListener(formListener);
        barraDerechaTB.add(negativoBoton);

        relieveBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/relieve.png"))); // NOI18N
        relieveBoton.setToolTipText("Filtro de relieve");
        relieveBoton.setFocusable(false);
        relieveBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        relieveBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        relieveBoton.addActionListener(formListener);
        barraDerechaTB.add(relieveBoton);

        emborronamientoBoton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/emborronamiento.png"))); // NOI18N
        emborronamientoBoton2.setToolTipText("Filtro emborronamiento");
        emborronamientoBoton2.setFocusable(false);
        emborronamientoBoton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        emborronamientoBoton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        emborronamientoBoton2.addActionListener(formListener);
        barraDerechaTB.add(emborronamientoBoton2);

        enfoqueBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/enfoque.png"))); // NOI18N
        enfoqueBoton.setToolTipText("Filtro de enfoque");
        enfoqueBoton.setFocusable(false);
        enfoqueBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        enfoqueBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        enfoqueBoton.addActionListener(formListener);
        barraDerechaTB.add(enfoqueBoton);
        barraDerechaTB.add(jSeparator10);

        bandasBoton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/bandas.png"))); // NOI18N
        bandasBoton.setToolTipText("Extracción de Bandas");
        bandasBoton.setFocusable(false);
        bandasBoton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bandasBoton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bandasBoton.addActionListener(formListener);
        barraDerechaTB.add(bandasBoton);

        espacioColorCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RGB", "YCC", "GREY" }));
        espacioColorCB.setToolTipText("Espacio de color");
        espacioColorCB.setMaximumSize(new java.awt.Dimension(50, 50));
        espacioColorCB.setMinimumSize(new java.awt.Dimension(50, 50));
        espacioColorCB.setPreferredSize(new java.awt.Dimension(30, 27));
        espacioColorCB.addActionListener(formListener);
        barraDerechaTB.add(espacioColorCB);

        getContentPane().add(barraDerechaTB, java.awt.BorderLayout.LINE_END);

        jScrollPane1.setViewportView(jTextPane1);

        escritorio.add(jScrollPane1);

        getContentPane().add(escritorio, java.awt.BorderLayout.CENTER);

        archivoMenuBar.setText("Archivo");

        nuevoMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/nuevo.png"))); // NOI18N
        nuevoMenuItem.setText("Nuevo");
        nuevoMenuItem.addActionListener(formListener);
        archivoMenuBar.add(nuevoMenuItem);

        abrirMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/abrir.png"))); // NOI18N
        abrirMenuItem.setText("Abrir");
        abrirMenuItem.addActionListener(formListener);
        archivoMenuBar.add(abrirMenuItem);

        guardarMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/save.png"))); // NOI18N
        guardarMenuItem.setText("Guardar");
        guardarMenuItem.addActionListener(formListener);
        archivoMenuBar.add(guardarMenuItem);

        menuBar.add(archivoMenuBar);

        verMenuBar.setText("Ver");

        verBarraEstadoCB.setSelected(true);
        verBarraEstadoCB.setText("Ver barra de estado");
        verBarraEstadoCB.addActionListener(formListener);
        verMenuBar.add(verBarraEstadoCB);

        verBarraIzqCB.setSelected(true);
        verBarraIzqCB.setText("Ver barra izquierda");
        verBarraIzqCB.addActionListener(formListener);
        verMenuBar.add(verBarraIzqCB);

        verBarraSupCB.setSelected(true);
        verBarraSupCB.setText("Ver barra superior");
        verBarraSupCB.addActionListener(formListener);
        verMenuBar.add(verBarraSupCB);

        verBarraDerechaCH.setSelected(true);
        verBarraDerechaCH.setText("Ver barra derecha");
        verBarraDerechaCH.addActionListener(formListener);
        verMenuBar.add(verBarraDerechaCH);

        menuBar.add(verMenuBar);

        ayudaMenuBar.setText("Ayuda");

        acercaMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevos/acercade.png"))); // NOI18N
        acercaMenuItem.setText("Acerca de");
        acercaMenuItem.addActionListener(formListener);
        ayudaMenuBar.add(acercaMenuItem);

        menuBar.add(ayudaMenuBar);

        setJMenuBar(menuBar);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.FocusListener, javax.swing.event.ChangeListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == lineaBoton) {
                VentanaPrincipal.this.lineaBotonActionPerformed(evt);
            }
            else if (evt.getSource() == rectanguloBoton) {
                VentanaPrincipal.this.rectanguloBotonActionPerformed(evt);
            }
            else if (evt.getSource() == rectanguloRedBoton) {
                VentanaPrincipal.this.rectanguloRedBotonActionPerformed(evt);
            }
            else if (evt.getSource() == elipseBoton) {
                VentanaPrincipal.this.elipseBotonActionPerformed(evt);
            }
            else if (evt.getSource() == FigurasCB) {
                VentanaPrincipal.this.FigurasCBActionPerformed(evt);
            }
            else if (evt.getSource() == xPosition) {
                VentanaPrincipal.this.xPositionActionPerformed(evt);
            }
            else if (evt.getSource() == yPosition) {
                VentanaPrincipal.this.yPositionActionPerformed(evt);
            }
            else if (evt.getSource() == colorTrazoCB) {
                VentanaPrincipal.this.colorTrazoCBActionPerformed(evt);
            }
            else if (evt.getSource() == colorRellenoCB) {
                VentanaPrincipal.this.colorRellenoCBActionPerformed(evt);
            }
            else if (evt.getSource() == tipoRellenoCB) {
                VentanaPrincipal.this.tipoRellenoCBActionPerformed(evt);
            }
            else if (evt.getSource() == degradado1CB) {
                VentanaPrincipal.this.degradado1CBActionPerformed(evt);
            }
            else if (evt.getSource() == degradado2CB) {
                VentanaPrincipal.this.degradado2CBActionPerformed(evt);
            }
            else if (evt.getSource() == alisarBoton) {
                VentanaPrincipal.this.alisarBotonActionPerformed(evt);
            }
            else if (evt.getSource() == tipoLineaCB) {
                VentanaPrincipal.this.tipoLineaCBActionPerformed(evt);
            }
            else if (evt.getSource() == recordSonidoBoton) {
                VentanaPrincipal.this.recordSonidoBotonActionPerformed(evt);
            }
            else if (evt.getSource() == pausaGrabacionBoton) {
                VentanaPrincipal.this.pausaGrabacionBotonActionPerformed(evt);
            }
            else if (evt.getSource() == stopRecordBoton) {
                VentanaPrincipal.this.stopRecordBotonActionPerformed(evt);
            }
            else if (evt.getSource() == listaMediaCB) {
                VentanaPrincipal.this.listaMediaCBActionPerformed(evt);
            }
            else if (evt.getSource() == reproducirSonidoBoton) {
                VentanaPrincipal.this.reproducirSonidoBotonActionPerformed(evt);
            }
            else if (evt.getSource() == pausaSonidoBoton) {
                VentanaPrincipal.this.pausaSonidoBotonActionPerformed(evt);
            }
            else if (evt.getSource() == stopSonidoBoton) {
                VentanaPrincipal.this.stopSonidoBotonActionPerformed(evt);
            }
            else if (evt.getSource() == webcamBoton) {
                VentanaPrincipal.this.webcamBotonActionPerformed(evt);
            }
            else if (evt.getSource() == capturaBoton) {
                VentanaPrincipal.this.capturaBotonActionPerformed(evt);
            }
            else if (evt.getSource() == contrasteBoton) {
                VentanaPrincipal.this.contrasteBotonActionPerformed(evt);
            }
            else if (evt.getSource() == iluminarBoton) {
                VentanaPrincipal.this.iluminarBotonActionPerformed(evt);
            }
            else if (evt.getSource() == oscurecerBoton) {
                VentanaPrincipal.this.oscurecerBotonActionPerformed(evt);
            }
            else if (evt.getSource() == sepiaBoton) {
                VentanaPrincipal.this.sepiaBotonActionPerformed(evt);
            }
            else if (evt.getSource() == tintarBoton) {
                VentanaPrincipal.this.tintarBotonActionPerformed(evt);
            }
            else if (evt.getSource() == colorTintadoCB) {
                VentanaPrincipal.this.colorTintadoCBActionPerformed(evt);
            }
            else if (evt.getSource() == ecualizacionBoton) {
                VentanaPrincipal.this.ecualizacionBotonActionPerformed(evt);
            }
            else if (evt.getSource() == zoomInBoton) {
                VentanaPrincipal.this.zoomInBotonActionPerformed(evt);
            }
            else if (evt.getSource() == disminuirBoton) {
                VentanaPrincipal.this.disminuirBotonActionPerformed(evt);
            }
            else if (evt.getSource() == rot90Boton) {
                VentanaPrincipal.this.rot90BotonActionPerformed(evt);
            }
            else if (evt.getSource() == rot180Boton) {
                VentanaPrincipal.this.rot180BotonActionPerformed(evt);
            }
            else if (evt.getSource() == rot270Boton) {
                VentanaPrincipal.this.rot270BotonActionPerformed(evt);
            }
            else if (evt.getSource() == disenioPropioBoton) {
                VentanaPrincipal.this.disenioPropioBotonActionPerformed(evt);
            }
            else if (evt.getSource() == disenioPropio2Boton) {
                VentanaPrincipal.this.disenioPropio2BotonActionPerformed(evt);
            }
            else if (evt.getSource() == cosinusoideBoton) {
                VentanaPrincipal.this.cosinusoideBotonActionPerformed(evt);
            }
            else if (evt.getSource() == duplicarBoton) {
                VentanaPrincipal.this.duplicarBotonActionPerformed(evt);
            }
            else if (evt.getSource() == negativoBoton) {
                VentanaPrincipal.this.negativoBotonActionPerformed(evt);
            }
            else if (evt.getSource() == relieveBoton) {
                VentanaPrincipal.this.relieveBotonActionPerformed(evt);
            }
            else if (evt.getSource() == emborronamientoBoton2) {
                VentanaPrincipal.this.emborronamientoBoton2ActionPerformed(evt);
            }
            else if (evt.getSource() == enfoqueBoton) {
                VentanaPrincipal.this.enfoqueBotonActionPerformed(evt);
            }
            else if (evt.getSource() == bandasBoton) {
                VentanaPrincipal.this.bandasBotonActionPerformed(evt);
            }
            else if (evt.getSource() == espacioColorCB) {
                VentanaPrincipal.this.espacioColorCBActionPerformed(evt);
            }
            else if (evt.getSource() == nuevoMenuItem) {
                VentanaPrincipal.this.nuevoMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == abrirMenuItem) {
                VentanaPrincipal.this.abrirMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == guardarMenuItem) {
                VentanaPrincipal.this.guardarMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == verBarraEstadoCB) {
                VentanaPrincipal.this.verBarraEstadoCBActionPerformed(evt);
            }
            else if (evt.getSource() == verBarraIzqCB) {
                VentanaPrincipal.this.verBarraIzqCBActionPerformed(evt);
            }
            else if (evt.getSource() == verBarraSupCB) {
                VentanaPrincipal.this.verBarraSupCBActionPerformed(evt);
            }
            else if (evt.getSource() == verBarraDerechaCH) {
                VentanaPrincipal.this.verBarraDerechaCHActionPerformed(evt);
            }
            else if (evt.getSource() == acercaMenuItem) {
                VentanaPrincipal.this.acercaMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == nuevoMB) {
                VentanaPrincipal.this.nuevoMBActionPerformed(evt);
            }
            else if (evt.getSource() == abrirMB) {
                VentanaPrincipal.this.abrirMBActionPerformed(evt);
            }
            else if (evt.getSource() == guardarMB) {
                VentanaPrincipal.this.guardarMBActionPerformed(evt);
            }
        }

        public void focusGained(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == brilloSlider) {
                VentanaPrincipal.this.brilloSliderFocusGained(evt);
            }
            else if (evt.getSource() == tintadoSlider) {
                VentanaPrincipal.this.tintadoSliderFocusGained(evt);
            }
            else if (evt.getSource() == umbralizacionSlider) {
                VentanaPrincipal.this.umbralizacionSliderFocusGained(evt);
            }
            else if (evt.getSource() == giroLibreSlider) {
                VentanaPrincipal.this.giroLibreSliderFocusGained(evt);
            }
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == brilloSlider) {
                VentanaPrincipal.this.brilloSliderFocusLost(evt);
            }
            else if (evt.getSource() == tintadoSlider) {
                VentanaPrincipal.this.tintadoSliderFocusLost(evt);
            }
            else if (evt.getSource() == umbralizacionSlider) {
                VentanaPrincipal.this.umbralizacionSliderFocusLost(evt);
            }
            else if (evt.getSource() == giroLibreSlider) {
                VentanaPrincipal.this.giroLibreSliderFocusLost(evt);
            }
        }

        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            if (evt.getSource() == transparenciaSlider) {
                VentanaPrincipal.this.transparenciaSliderStateChanged(evt);
            }
            else if (evt.getSource() == grosorSpinner) {
                VentanaPrincipal.this.grosorSpinnerStateChanged(evt);
            }
            else if (evt.getSource() == brilloSlider) {
                VentanaPrincipal.this.brilloSliderStateChanged(evt);
            }
            else if (evt.getSource() == tintadoSlider) {
                VentanaPrincipal.this.tintadoSliderStateChanged(evt);
            }
            else if (evt.getSource() == umbralizacionSlider) {
                VentanaPrincipal.this.umbralizacionSliderStateChanged(evt);
            }
            else if (evt.getSource() == giroLibreSlider) {
                VentanaPrincipal.this.giroLibreSliderStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents
    /**
     * 
     * @param cb 
     */
    private void inicializarColores(javax.swing.JComboBox<Color> cb){
        cb.addItem(Color.BLACK);
        cb.addItem(Color.RED);
        cb.addItem(Color.BLUE);
        cb.addItem(Color.GREEN);
        cb.addItem(Color.WHITE);
        cb.addItem(Color.YELLOW);
        cb.setRenderer(new ColorRenderer());
        cb.setSelectedItem(null);
    }
    /**
     * 
     * @param cb 
     */
    private void inicializarRelleno(javax.swing.JComboBox<TipoRelleno> cb){
        cb.addItem(TipoRelleno.LISO);
        cb.addItem(TipoRelleno.DEGRADADO_HORIZONTAL);
        cb.addItem(TipoRelleno.DEGRADADO_VERTICAL);
        cb.addItem(TipoRelleno.DEGRADADO_DIAGONAL);
        cb.addItem(TipoRelleno.NINGUNO);
        cb.setRenderer(new RellenoRenderer());
        cb.setSelectedItem(TipoRelleno.NINGUNO);
    }
    /**
     * 
     * @param cb 
     */
    private void inicializarTipoLinea(javax.swing.JComboBox<TipoLinea> cb){
        tipoLineaCB.addItem(TipoLinea.CONTINUA);
        tipoLineaCB.addItem(TipoLinea.DISCONTINUA);
        tipoLineaCB.addItem(TipoLinea.PUNTEADA);
        cb.setRenderer(new TipoLineaRenderer());
        cb.setSelectedItem(TipoLinea.CONTINUA);
    }
    /**
     * 
     */
    private void disableRelleno(){
        colorRellenoCB.setEnabled(false);
        degradado1CB.setEnabled(false);
        degradado2CB.setEnabled(false);
        tipoRellenoCB.setEnabled(false);
    }
    /**
     * 
     */
    private void enableRelleno(){
        colorRellenoCB.setEnabled(true);
        degradado1CB.setEnabled(true);
        degradado2CB.setEnabled(true);
        tipoRellenoCB.setEnabled(true);
    }
    /**
     * 
     * @param figuraSeleccionada 
     */
    private void actualizarAtributosBarraFigura(Figura figuraSeleccionada){
        //comunes a figuras
        if(figuraSeleccionada!=null){
            if(figuraSeleccionada.getClass()==Linea.class){
                lineaBoton.setSelected(true);
            }
            else if(figuraSeleccionada.getClass()==Rectangulo.class){
                rectanguloBoton.setSelected(true);
            }
            else if(figuraSeleccionada.getClass()==RectanguloRedondeado.class){
                rectanguloRedBoton.setSelected(true);
            }
            else if(figuraSeleccionada.getClass()==Elipse.class){
                elipseBoton.setSelected(true);
            }
            colorTrazoCB.setSelectedItem(figuraSeleccionada.getColor());
            tipoLineaCB.setSelectedItem(figuraSeleccionada.getStroke());
            grosorSpinner.setValue(figuraSeleccionada.getGrosor());
            alisarBoton.setSelected(figuraSeleccionada.getAlisado());
            transparenciaSlider.setValue((int) (figuraSeleccionada.getTransparencia()*100));

            try{
                colorRellenoCB.setSelectedItem(((FiguraRellenable)figuraSeleccionada).getColorRelleno());
                degradado1CB.setSelectedItem(((FiguraRellenable)figuraSeleccionada).getDegradado1());
                degradado2CB.setSelectedItem(((FiguraRellenable)figuraSeleccionada).getDegradado2());
                tipoRellenoCB.setSelectedItem(((FiguraRellenable)figuraSeleccionada).getTipoRelleno());
                enableRelleno();
            }catch(Exception e){
                disableRelleno();
            }
            this.repaint();
        }
    }
    /**
     * 
     * @param lienzo 
     */
    private void actualizarAtributosLienzo(Lienzo lienzo){
        //color trazo
        if(lienzo.getColorTrazo()==null){
            colorTrazoCB.removeAllItems();
            inicializarColores(colorTrazoCB);
        }
        else{colorTrazoCB.setSelectedItem(lienzo.getColorTrazo());}
        //color relleno
        if(lienzo.getColorRelleno()==null){
            colorRellenoCB.removeAllItems();
            inicializarColores(colorRellenoCB);}
        else{colorRellenoCB.setSelectedItem(lienzo.getColorRelleno());}
        //color degradado 1
        if(lienzo.getColorDeg1()==null){
            degradado1CB.removeAllItems();
            inicializarColores(degradado1CB);}
        else{degradado1CB.setSelectedItem(lienzo.getColorDeg1());}
        //color degradado 2
        if(lienzo.getColorDeg2()==null){
            degradado2CB.removeAllItems();
            inicializarColores(degradado2CB);}
        else{degradado2CB.setSelectedItem(lienzo.getColorDeg2());}
        //tipo linea
        if(lienzo.getStroke()!=null){
            tipoLineaCB.setSelectedItem(lienzo.getStroke());
        }
        tipoRellenoCB.setSelectedItem(lienzo.getTipoRelleno());
        //grosor
        grosorSpinner.setValue(lienzo.getGrosor());
        //alisado
        alisarBoton.setSelected(lienzo.getAlisadoActivated());
        //transparencia
        transparenciaSlider.setValue((int) (lienzo.getTransparencia()*100));
        if(null==lienzo.getForma()) {//si no hay forma seleccionada
            lineaBoton.setSelected(false);
            rectanguloBoton.setSelected(false);
            rectanguloRedBoton.setSelected(false);
            elipseBoton.setSelected(false);
        }
        else //seleccionar la forma
        switch (lienzo.getForma()) {
            case LINEA:
                lineaBoton.setSelected(true);
                disableRelleno();
                break;
            case RECTANGULO:
                rectanguloBoton.setSelected(true);
                enableRelleno();
                break;
            case RECTANGULOREDONDEADO:
                rectanguloRedBoton.setSelected(true);
                enableRelleno();
                break;
            case OVALO:
                elipseBoton.setSelected(true);
                enableRelleno();
                break;
            default:
                //si no hay forma seleccionada
                lineaBoton.setSelected(false);
                rectanguloBoton.setSelected(false);
                rectanguloRedBoton.setSelected(false);
                elipseBoton.setSelected(false);
                break;
        }
 
        //figuras
        FigurasCB.removeAllItems();
        List <Figura> figuras = lienzo.getvFiguras();
        for(Figura f: figuras){
            FigurasCB.addItem(f);
        }
        FigurasCB.setSelectedItem(null);
        this.repaint();
    }
    /**
     * 
     * @param evt 
     */
    private void nuevoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoMenuItemActionPerformed
        VentanaMultimediaImagen vi = new VentanaMultimediaImagen();
        escritorio.add(vi);
        String hS, wS = null;
        BufferedImage img;
        hS = JOptionPane.showInputDialog(null, "Introduzca altura de la imagen", "300");
        if(hS!=null){
            wS = JOptionPane.showInputDialog(null, "Introduzca ancho de la imagen", "300");
        }
        if(hS!=null && wS!=null){
            int h = new Integer(hS);
            int w = new Integer(wS);

            vi.getLienzo().setDimension(new Dimension(w,h));
            vi.getLienzo().setArea();
            vi.setTitle("Nueva");
            vi.setVisible(true);
            
            actualizarAtributosLienzo(vi.getLienzo());
            
            //Añadimos el manejador
            MiManejadorLienzo manejador = new MiManejadorLienzo();
            vi.getLienzo().addLienzoListener(manejador);
        }
        
    }//GEN-LAST:event_nuevoMenuItemActionPerformed
    /**
     * 
     * @param evt 
     */
    private void guardarMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarMenuItemActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            JFileChooser dlg = new JFileChooser();
            int resp = dlg.showSaveDialog(this);
            if(resp == JFileChooser.APPROVE_OPTION){
                try{
                    BufferedImage img = vi.getLienzo().getImage(true);
                    if(img!=null){
                        File f = dlg.getSelectedFile();
                        int posExtension = f.getName().indexOf(".")+1;
                        String extension = "";
                        if(posExtension==0){//si no se indica extensión
                            extension = "jpg";
                        }
                        else{
                            extension = f.getName().substring(posExtension);
                        }
                        ImageIO.write(img, extension, f);
                        vi.setTitle(f.getName());
                    }
                }catch(Exception ex){
                    System.err.println("Error al guardar la imagen.");
                }
            }
        }
    }//GEN-LAST:event_guardarMenuItemActionPerformed
    /**
     * 
     * @param evt 
     */
    private void abrirMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirMenuItemActionPerformed
        JFileChooser dlg = new JFileChooser();
        
        dlg.setFileFilter(imageFilter);
        dlg.setFileFilter(soundFilter);
        dlg.setFileFilter(videoFilter);
        
        int resp = dlg.showOpenDialog(this);
        if(resp == JFileChooser.APPROVE_OPTION){
            try{
                File f = dlg.getSelectedFile();
                if(imageFilter.accept(f)){//si es imagen
                    BufferedImage img = ImageIO.read(f);
                    VentanaMultimediaImagen vi = new VentanaMultimediaImagen();
                    vi.getLienzo().setImage(img);
                    this.escritorio.add(vi);
                    vi.setTitle(f.getName());
                    vi.setVisible(true);
                    int w = img.getWidth();
                    int h = img.getHeight();
                    vi.getLienzo().setDimension(new Dimension(w,h));
                    
                    //Añadimos el manejador
                    MiManejadorLienzo manejador = new MiManejadorLienzo();
                    vi.getLienzo().addLienzoListener(manejador);
                }
                if(soundFilter.accept(f)){
                    listaMediaCB.addItem(f);
                }
                if(videoFilter.accept(f)){
                    listaMediaCB.addItem(f);
                }
            }catch(Exception ex){
                System.err.println("Error al leer el archivo.");
            }
        }
    }//GEN-LAST:event_abrirMenuItemActionPerformed
    /**
     * 
     * @param evt 
     */
    private void acercaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acercaMenuItemActionPerformed
        String mensaje = "Paint\n05/07/2019 1.1\nMontserrat Rodríguez Zamorano";
        JOptionPane.showMessageDialog(null, mensaje, "Acerca de", JOptionPane.QUESTION_MESSAGE);
    }//GEN-LAST:event_acercaMenuItemActionPerformed
    /**
     * 
     * @param evt 
     */
    private void lineaBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineaBotonActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            vi.getLienzo().setForma(Forma.LINEA);
            disableRelleno();
        }
    }//GEN-LAST:event_lineaBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void rectanguloBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectanguloBotonActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            vi.getLienzo().setForma(Forma.RECTANGULO);
            enableRelleno();
        }
    }//GEN-LAST:event_rectanguloBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void elipseBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elipseBotonActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            vi.getLienzo().setForma(Forma.OVALO);
            enableRelleno();
        }
    }//GEN-LAST:event_elipseBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void alisarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alisarBotonActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            Lienzo l = vi.getLienzo();
            if(l.getFiguraSeleccionada()==null){
                l.changeAlisadoActivated();
            }
            else{
                boolean alisado = l.getFiguraSeleccionada().getAlisado();
                l.getFiguraSeleccionada().setAlisado(!alisado);
                this.repaint();
            }
        }
    }//GEN-LAST:event_alisarBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void recordSonidoBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordSonidoBotonActionPerformed
        try {
            temp = File.createTempFile("audioTemporal","wav");
        } catch (IOException ex) {
            System.err.println("Error al crear el archivo temporal");
        }
        grabando = true;
        recorder = new SMSoundRecorder(temp);
        recorder.record();
        stopRecordBoton.setEnabled(true);
        pausaGrabacionBoton.setEnabled(true);
        
    }//GEN-LAST:event_recordSonidoBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void reproducirSonidoBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reproducirSonidoBotonActionPerformed
        VentanaMultimedia vi = (VentanaMultimedia) escritorio.getSelectedFrame();
        if(vi!=null){
            try{
               ((VentanaMultimediaJMFPlayer)vi).play();
            }catch(Exception e){}
        }
    }//GEN-LAST:event_reproducirSonidoBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void stopRecordBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopRecordBotonActionPerformed
        recorder.stop();
        grabando = false;
        JFileChooser dlg = new JFileChooser();
        int resp = dlg.showSaveDialog(this);
        if(resp == JFileChooser.APPROVE_OPTION){
            try{
                File f = dlg.getSelectedFile();
                Files.copy(temp.toPath(), f.toPath());
                listaMediaCB.addItem(f);
                temp = null;
            }catch(Exception e){
                System.err.println("Error al guardar la grabación");
            }
        }
    }//GEN-LAST:event_stopRecordBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void colorTrazoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorTrazoCBActionPerformed
        Color selectedColor = (Color) colorTrazoCB.getSelectedItem();
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
           if(vi.getLienzo().getFiguraSeleccionada()==null){
               vi.getLienzo().setColorTrazo(selectedColor);
           }
           else{
               vi.getLienzo().getFiguraSeleccionada().setColor(selectedColor);
               this.repaint();
           }
        }
    }//GEN-LAST:event_colorTrazoCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void colorRellenoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorRellenoCBActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            Lienzo lienzo = vi.getLienzo();
            Figura figuraSeleccionada = lienzo.getFiguraSeleccionada();
            Color selectedColor = (Color) colorRellenoCB.getSelectedItem();
            if(figuraSeleccionada==null){
                vi.getLienzo().setColorRelleno(selectedColor);
            }
            else{
                try{
                    ((FiguraRellenable)figuraSeleccionada).setColorRelleno(selectedColor);
                }catch(Exception e){}
            }
        }
    }//GEN-LAST:event_colorRellenoCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void transparenciaSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_transparenciaSliderStateChanged
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            Lienzo lienzo = vi.getLienzo();
            Figura figuraSeleccionada = lienzo.getFiguraSeleccionada();
            float transp = (float) (this.transparenciaSlider.getValue()*0.01);
            if(figuraSeleccionada==null){
                lienzo.setTransparencia(transp);
            }
            else{
                figuraSeleccionada.setTransparencia(transp);
            }
        }
    }//GEN-LAST:event_transparenciaSliderStateChanged
    /**
     * 
     * @param evt 
     */
    private void grosorSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_grosorSpinnerStateChanged
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        int grosor = (int) this.grosorSpinner.getValue();
        if(vi!=null){
            Lienzo lienzo = vi.getLienzo();
            if(lienzo.getFiguraSeleccionada()==null){
                vi.getLienzo().setGrosor(grosor);
            }
            else{
                lienzo.getFiguraSeleccionada().setGrosor(grosor);
                this.repaint();
            }
        }
    }//GEN-LAST:event_grosorSpinnerStateChanged
    /**
     * 
     * @param evt 
     */
    private void pausaGrabacionBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pausaGrabacionBotonActionPerformed
        //si está grabando, pausar
        if(grabando){
            recorder.stop();
            grabando = false;
        }//en otro caso renaudar la grabacion
        else{
            recorder.record();
            grabando = true;
        }
    }//GEN-LAST:event_pausaGrabacionBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void stopSonidoBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopSonidoBotonActionPerformed
        File f = (File) listaMediaCB.getSelectedItem();
        if(f!=null){
            if(soundFilter.accept(f)){//si es sonido
                if(player!=null){
                    player.stop();
                }
            }
            else if (videoFilter.accept(f)){
                System.out.println("ES VIDEO");
            }
        }
    }//GEN-LAST:event_stopSonidoBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void pausaSonidoBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pausaSonidoBotonActionPerformed
        File f = (File) listaMediaCB.getSelectedItem();
        if(f!=null){
            if(soundFilter.accept(f)){//si es  sonido
                if(player!=null){
                    player.pause();
                }
            }
            else if (videoFilter.accept(f)){
                System.out.println("ES VIDEO");
            }
        }
    }//GEN-LAST:event_pausaSonidoBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void tipoRellenoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoRellenoCBActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            TipoRelleno tipoRelleno = (TipoRelleno) tipoRellenoCB.getSelectedItem();
            Lienzo lienzo = vi.getLienzo();
            Figura figuraSeleccionada = lienzo.getFiguraSeleccionada();
            if(figuraSeleccionada==null){
                vi.getLienzo().setTipoRelleno(tipoRelleno);
            }
            else{//si hay una figura seleccionada
                //no se comprueba si es figura rellenable porque inhabilitamos los CB cuando es figura lineal
                ((FiguraRellenable)figuraSeleccionada).setTipoRelleno(tipoRelleno);
            }
        }
    }//GEN-LAST:event_tipoRellenoCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void degradado1CBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_degradado1CBActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            Color selectedColor = (Color) degradado1CB.getSelectedItem();
            Lienzo lienzo = vi.getLienzo();
            Figura figuraSeleccionada = lienzo.getFiguraSeleccionada();
            if(figuraSeleccionada==null){
                vi.getLienzo().setColorDeg1(selectedColor);
            }
            else{//si hay una figura seleccionada
                //no se comprueba si es figura rellenable porque inhabilitamos los CB cuando es figura lineal
                ((FiguraRellenable)figuraSeleccionada).setDegradado1(selectedColor);
                this.repaint();
            }
        }
    }//GEN-LAST:event_degradado1CBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void degradado2CBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_degradado2CBActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            Color selectedColor = (Color) degradado2CB.getSelectedItem();
            Lienzo lienzo = vi.getLienzo();
            Figura figuraSeleccionada = lienzo.getFiguraSeleccionada();
            if(figuraSeleccionada==null){
                vi.getLienzo().setColorDeg2(selectedColor);
            }
            else{//si hay una figura seleccionada
                //no se comprueba si es figura rellenable porque inhabilitamos los CB cuando es figura lineal
                ((FiguraRellenable)figuraSeleccionada).setDegradado2(selectedColor);
                this.repaint();
            }
        }
    }//GEN-LAST:event_degradado2CBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void tipoLineaCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoLineaCBActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            Lienzo lienzo = vi.getLienzo();
            TipoLinea selectedTP = (TipoLinea) tipoLineaCB.getSelectedItem();
            if(lienzo.getFiguraSeleccionada()==null){
                lienzo.setStroke(selectedTP);
            }
            else{
                lienzo.getFiguraSeleccionada().setStroke(selectedTP);
                this.repaint();
            }
        }
    }//GEN-LAST:event_tipoLineaCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void verBarraEstadoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verBarraEstadoCBActionPerformed
         barraEstado.setVisible(!barraEstado.isVisible());
    }//GEN-LAST:event_verBarraEstadoCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void verBarraSupCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verBarraSupCBActionPerformed
        barraSuperiorTB.setVisible(!barraSuperiorTB.isVisible());
    }//GEN-LAST:event_verBarraSupCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void verBarraIzqCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verBarraIzqCBActionPerformed
        barraizquierdaTB.setVisible(!barraizquierdaTB.isVisible());
    }//GEN-LAST:event_verBarraIzqCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void brilloSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_brilloSliderStateChanged
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        float nivelBrillo = (float) this.brilloSlider.getValue();
        if(vi != null){
            if(imgSource != null){
                try{
                    RescaleOp rop = new RescaleOp(1+(nivelBrillo/100), 0.0F, null);
                    BufferedImage imgdest = rop.filter(imgSource, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_brilloSliderStateChanged
    /**
     * 
     * @param evt 
     */
    private void brilloSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brilloSliderFocusGained
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            ColorModel cm = vi.getLienzo().getImage(true).getColorModel();
            WritableRaster raster = vi.getLienzo().getImage(true).copyData(null);
            boolean alfaPre = vi.getLienzo().getImage(true).isAlphaPremultiplied();
            imgSource = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_brilloSliderFocusGained
    /**
     * 
     * @param evt 
     */
    private void brilloSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_brilloSliderFocusLost
        imgSource = imgdest;
        brilloSlider.setValue(0);
    }//GEN-LAST:event_brilloSliderFocusLost
    /**
     * 
     * @param evt 
     */
    private void tintarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tintarBotonActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            LienzoImagen l = vi.getLienzo();
            //si la transparencia está activada
            if(l.getTintadoActivated()){
                l.setTintadoActivated(false);
                tintadoSlider.setEnabled(false);
            }
            else{//si no está activada
                l.setTintadoActivated(true);
                tintadoSlider.setEnabled(true);
            }
        }
    }//GEN-LAST:event_tintarBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void colorTintadoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorTintadoCBActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            Color selectedColor = (Color) colorTintadoCB.getSelectedItem();
            vi.getLienzo().setColorTintado(selectedColor);
        }
    }//GEN-LAST:event_colorTintadoCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void tintadoSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tintadoSliderStateChanged
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        float nivelTintado = (float) this.tintadoSlider.getValue();
        if(vi != null){
            if(imgSource != null){
                try{
                    TintOp tintado = new TintOp(vi.getLienzo().getColorTintado(), nivelTintado/100);
                    imgdest = tintado.filter(imgSource, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_tintadoSliderStateChanged
    /**
     * 
     * @param evt 
     */
    private void tintadoSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tintadoSliderFocusGained
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            ColorModel cm = vi.getLienzo().getImage(true).getColorModel();
            WritableRaster raster = vi.getLienzo().getImage(true).copyData(null);
            boolean alfaPre = vi.getLienzo().getImage(true).isAlphaPremultiplied();
            imgSource = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_tintadoSliderFocusGained
    /**
     * 
     * @param evt 
     */
    private void tintadoSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tintadoSliderFocusLost
        imgSource = imgdest;
        tintadoSlider.setValue(0);
    }//GEN-LAST:event_tintadoSliderFocusLost
    /**
     * 
     * @param evt 
     */
    private void umbralizacionSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_umbralizacionSliderStateChanged
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        int umbral = (int) this.umbralizacionSlider.getValue();
        if(vi != null){
            if(imgSource != null){
                try{
                    UmbralizacionOp umbr = new UmbralizacionOp(umbral);
                    imgdest = umbr.filter(imgSource, null);
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_umbralizacionSliderStateChanged
    /**
     * 
     * @param evt 
     */
    private void umbralizacionSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_umbralizacionSliderFocusGained
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            ColorModel cm = vi.getLienzo().getImage(true).getColorModel();
            WritableRaster raster = vi.getLienzo().getImage(true).copyData(null);
            boolean alfaPre = vi.getLienzo().getImage(true).isAlphaPremultiplied();
            imgSource = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_umbralizacionSliderFocusGained
    /**
     * 
     * @param evt 
     */
    private void umbralizacionSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_umbralizacionSliderFocusLost
        imgSource = imgdest;
        umbralizacionSlider.setValue(50);
    }//GEN-LAST:event_umbralizacionSliderFocusLost
    /**
     * 
     * @param evt 
     */
    private void negativoBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_negativoBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource!=null){
                try{
                    int type = LookupTableProducer.TYPE_NEGATIVE;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    //Imagen origen y destino iguales
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_negativoBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void relieveBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relieveBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource!=null){
                try{
                    Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_RELIEVE_3x3);
                    ConvolveOp cop = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
                    imgSource = cop.filter(imgSource, null);
                    vi.getLienzo().setImage(imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_relieveBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void emborronamientoBoton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emborronamientoBoton2ActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource!=null){
                try{
                    Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_BINOMIAL_3x3);
                    ConvolveOp cop = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
                    imgSource = cop.filter(imgSource, null);
                    vi.getLienzo().setImage(imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_emborronamientoBoton2ActionPerformed
    /**
     * 
     * @param evt 
     */
    private void enfoqueBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enfoqueBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource!=null){
                try{
                    Kernel k = KernelProducer.createKernel(KernelProducer.TYPE_ENFOQUE_3x3);
                    ConvolveOp cop = new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null);
                    imgSource = cop.filter(imgSource, null);
                    vi.getLienzo().setImage(imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_enfoqueBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void contrasteBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contrasteBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                try{
                    int type = LookupTableProducer.TYPE_SFUNCION;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    //imagen origen y destino iguales
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_contrasteBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void iluminarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iluminarBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource!=null){
                try{
                    int type = LookupTableProducer.TYPE_LOGARITHM;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    //Imagen origen y destino iguales
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_iluminarBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void oscurecerBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oscurecerBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource!=null){
                try{
                    int type = LookupTableProducer.TYPE_POWER;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    //Imagen origen y destino iguales
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_oscurecerBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void sepiaBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sepiaBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                SepiaOp sepia = new SepiaOp();
                sepia.filter(imgSource, imgSource);
                vi.getLienzo().repaint();
            }
        }
    }//GEN-LAST:event_sepiaBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void duplicarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicarBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                //añadir nueva ventana interna
                int w = imgSource.getWidth();
                int h = imgSource.getHeight();
                
                VentanaMultimediaImagen newVi = new VentanaMultimediaImagen();
                newVi.getLienzo().setDimension(new Dimension(w,h));
                newVi.getLienzo().setImage(imgSource);
                newVi.setTitle(vi.getTitle()+" copia");
                escritorio.add(newVi);
                newVi.setVisible(true);
            }
        }
    }//GEN-LAST:event_duplicarBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void rot90BotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rot90BotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                try{
                    double r = Math.toRadians(90);
                    //se rota en el punto medio de la imagen
                    int temp0 = imgSource.getWidth()/2;
                    int temp1 = imgSource.getHeight()/2;
                    Point p = new Point(temp0, temp1);
                    AffineTransform ar = AffineTransform.getRotateInstance(r, p.x,p.y);
                    
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(ar, AffineTransformOp.TYPE_BILINEAR);
                    imgdest = atop.filter(imgSource, null);
                    
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_rot90BotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void rot180BotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rot180BotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                try{
                    double r = Math.toRadians(180);
                    //se rota en el punto medio de la imagen
                    int temp0 = imgSource.getWidth()/2;
                    int temp1 = imgSource.getHeight()/2;
                    Point p = new Point(temp0, temp1);
                    AffineTransform ar = AffineTransform.getRotateInstance(r, p.x,p.y);
                    
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(ar, AffineTransformOp.TYPE_BILINEAR);
                    imgdest = atop.filter(imgSource, null);
                    
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_rot180BotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void zoomInBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                try{
                    AffineTransform at = AffineTransform.getScaleInstance(1.25, 1.25);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    imgdest = atop.filter(imgSource, null);
                    
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_zoomInBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void disminuirBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disminuirBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                try{
                    AffineTransform at = AffineTransform.getScaleInstance(0.75, 0.75);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    imgdest = atop.filter(imgSource, null);
                    
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_disminuirBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void ecualizacionBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ecualizacionBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
           imgSource = vi.getLienzo().getImage(true);
           if(imgSource != null){
               try{
                   EqualizationOp ecualizacion = new EqualizationOp();
                   ecualizacion.filter(imgSource, imgSource);
                   vi.getLienzo().setImage(imgSource);
                   vi.getLienzo().repaint();
               }catch(Exception e){
                   System.err.println(e.getLocalizedMessage());
               }
           }
        }
    }//GEN-LAST:event_ecualizacionBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void bandasBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandasBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                //Creamos el modelo de color de la nueva image basado
                //en un espacio de color GRAY
                ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                ComponentColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
                //Crear una imagen por cada banda
                int numBandas = imgSource.getRaster().getNumBands();
                for(int iBanda = 0; iBanda < numBandas; iBanda++){
                    //Creamos el nuevo raster a partir del raster de la imagen original
                    int bandList[] = {iBanda};
                    WritableRaster bandRaster = (WritableRaster) imgSource.getRaster().createWritableChild(0,
                    0, imgSource.getWidth(), imgSource.getHeight(), 0, 0, bandList);
                    //Create una nueva imagen que contiene como raster el
                    //correspondiente a la banda
                    BufferedImage imgBanda = new BufferedImage(cm, bandRaster, false, null);
                    
                    //añadir nueva ventana interna
                    VentanaMultimediaImagen newVi = new VentanaMultimediaImagen();
                    escritorio.add(newVi);
                    newVi.setVisible(true);
                    newVi.getLienzo().setImage(imgBanda);
                    newVi.setTitle("banda" + (iBanda+1));
                    //guardar dimension
                    int w = imgBanda.getWidth();
                    int h = imgBanda.getHeight();
                    newVi.getLienzo().setDimension(new Dimension(w,h));
                }
            }
        }
    }//GEN-LAST:event_bandasBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void espacioColorCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_espacioColorCBActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                String itemSelec = (String) espacioColorCB.getSelectedItem();
                ColorSpace cs = null;
                if("RGB".equals(itemSelec)){
                    cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                }
                else if("YCC".equals(itemSelec)){
                    cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                }
                else if("GREY".equals(itemSelec)){
                    cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                }
                //comprobamos si el espacio de partida es RGB
                if(imgSource.getColorModel().getColorSpace().isCS_sRGB()){
                    ColorConvertOp cop = new ColorConvertOp(cs, null);
                    BufferedImage imgOut = cop.filter(imgSource, null);
                    
                    //añadir nueva ventana interna
                    VentanaMultimediaImagen newVi = new VentanaMultimediaImagen();
                    escritorio.add(newVi);
                    newVi.setVisible(true);
                    newVi.getLienzo().setImage(imgOut);
                    newVi.setTitle("["+itemSelec+"]");
                    //guardar dimension
                    int w = imgOut.getWidth();
                    int h = imgOut.getHeight();
                    newVi.getLienzo().setDimension(new Dimension(w,h));
                }
            }
        }
    }//GEN-LAST:event_espacioColorCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void disenioPropioBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disenioPropioBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                PurpleOp dp = new PurpleOp();
                dp.filter(imgSource, imgSource);
                vi.getLienzo().repaint();
            }
        }
    }//GEN-LAST:event_disenioPropioBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void disenioPropio2BotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disenioPropio2BotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                AverageOp dp = new AverageOp();
                dp.filter(imgSource, imgSource);
                vi.getLienzo().repaint();
            }
        }
    }//GEN-LAST:event_disenioPropio2BotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void verBarraDerechaCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verBarraDerechaCHActionPerformed
        barraDerechaTB.setVisible(!barraDerechaTB.isVisible());
    }//GEN-LAST:event_verBarraDerechaCHActionPerformed

   /**
     * 
     * @param w
     * @return 
     */
    public LookupTable coseno(double w){
        double K = 255.0; //cte de normalizacion
        //Codigo implemententado f(x) = |cos(wx)|
        byte f[] = new byte[256];
        for(int i=0; i<256; i++){
            f[i] = (byte)Math.abs(K*Math.cos(w*i)); //coseno
        }
        ByteLookupTable lt = new ByteLookupTable(0,f);
        return lt;
    }
    /**
     * 
     * @param evt 
     */
    private void cosinusoideBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cosinusoideBotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource!=null){
                try{
                    LookupTable lt = coseno(180);
                    LookupOp lop = new LookupOp(lt, null);
                    //Imagen origen y destino iguales
                    lop.filter(imgSource, imgSource);
                    vi.repaint();
                }catch(Exception e){
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_cosinusoideBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void rot270BotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rot270BotonActionPerformed
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            imgSource = vi.getLienzo().getImage(true);
            if(imgSource != null){
                try{
                    double r = Math.toRadians(270);
                    //se rota en el punto medio de la imagen
                    int temp0 = imgSource.getWidth()/2;
                    int temp1 = imgSource.getHeight()/2;
                    Point p = new Point(temp0, temp1);
                    AffineTransform ar = AffineTransform.getRotateInstance(r, p.x,p.y);
                    
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(ar, AffineTransformOp.TYPE_BILINEAR);
                    imgdest = atop.filter(imgSource, null);
                    
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_rot270BotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void FigurasCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FigurasCBActionPerformed
        fSeleccionada = (Figura) FigurasCB.getSelectedItem();
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        vi.getLienzo().setFiguraSeleccionada(fSeleccionada);
        this.repaint();
    }//GEN-LAST:event_FigurasCBActionPerformed
    /**
     * 
     * @param evt 
     */
    private void giroLibreSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_giroLibreSliderStateChanged
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi != null){
            if(imgSource != null){
                try{
                    double r = Math.toRadians(giroLibreSlider.getValue());
                    //se rota en el punto medio de la imagen
                    int temp0 = imgSource.getWidth()/2;
                    int temp1 = imgSource.getHeight()/2;
                    Point p = new Point(temp0, temp1);
                    AffineTransform ar = AffineTransform.getRotateInstance(r, p.x,p.y);
                    
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(ar, AffineTransformOp.TYPE_BILINEAR);
                    imgdest = atop.filter(imgSource, null);
                    
                    vi.getLienzo().setImage(imgdest);
                    vi.getLienzo().repaint();
                }catch(Exception e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_giroLibreSliderStateChanged
    /**
     * 
     * @param evt 
     */
    private void giroLibreSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_giroLibreSliderFocusGained
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            ColorModel cm = vi.getLienzo().getImage(true).getColorModel();
            WritableRaster raster = vi.getLienzo().getImage(true).copyData(null);
            boolean alfaPre = vi.getLienzo().getImage(true).isAlphaPremultiplied();
            imgSource = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_giroLibreSliderFocusGained
    /**
     * 
     * @param evt 
     */
    private void giroLibreSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_giroLibreSliderFocusLost
        imgSource = imgdest;
        giroLibreSlider.setValue(0);
    }//GEN-LAST:event_giroLibreSliderFocusLost

   /**
     * 
     * @param evt 
     */
    private void xPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xPositionActionPerformed
        pointTemp.x = new Integer(xPosition.getText());
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi.getLienzo().getFiguraSeleccionada()!=null){
            vi.getLienzo().getFiguraSeleccionada().setLocation(pointTemp);
            this.repaint();
        }
    }//GEN-LAST:event_xPositionActionPerformed
    /**
     * 
     * @param evt 
     */
    private void yPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yPositionActionPerformed
        pointTemp.y = new Integer(yPosition.getText());
        VentanaMultimediaImagen vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi.getLienzo().getFiguraSeleccionada()!=null){
            vi.getLienzo().getFiguraSeleccionada().setLocation(pointTemp);
            this.repaint();
        }
    }//GEN-LAST:event_yPositionActionPerformed
    /**
     * 
     * @param evt 
     */
    private void rectanguloRedBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectanguloRedBotonActionPerformed
        VentanaMultimediaImagen vi;
        vi = (VentanaMultimediaImagen) escritorio.getSelectedFrame();
        if(vi!=null){
            vi.getLienzo().setForma(Forma.RECTANGULOREDONDEADO);
            enableRelleno();
        }
    }//GEN-LAST:event_rectanguloRedBotonActionPerformed
    /**
     * 
     * @param evt 
     */
    private void listaMediaCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listaMediaCBActionPerformed
        File selectedFile = (File)listaMediaCB.getSelectedItem();
        VentanaMultimediaJMFPlayer vi = VentanaMultimediaJMFPlayer.getInstance(selectedFile);
        this.escritorio.add(vi);
        vi.setTitle(selectedFile.getName());
        vi.setVisible(true);
    }//GEN-LAST:event_listaMediaCBActionPerformed

    private void webcamBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webcamBotonActionPerformed
        VentanaMultimediaCamara ventanaCamara = VentanaMultimediaCamara.getInstance();
        if(ventanaCamara!=null){
          escritorio.add(ventanaCamara);
          ventanaCamara.setVisible(true);
        }
    }//GEN-LAST:event_webcamBotonActionPerformed

    private void capturaBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_capturaBotonActionPerformed
        VentanaMultimedia viCamara = (VentanaMultimedia)escritorio.getSelectedFrame();
        try{
            BufferedImage img = ((VentanaMultimediaCamara) viCamara).getImage();

            //nueva ventana con la imagen
            VentanaMultimediaImagen vi = new VentanaMultimediaImagen();
            escritorio.add(vi);
            int h = img.getHeight();
            int w = img.getWidth();

            vi.getLienzo().setDimension(new Dimension(w,h));
            vi.getLienzo().setArea();
            vi.getLienzo().setImage(img);
            vi.setTitle("Captura");
            vi.setVisible(true);
        }catch(Exception e){}
    }//GEN-LAST:event_capturaBotonActionPerformed

    private void nuevoMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoMBActionPerformed
        nuevoMenuItemActionPerformed(evt);
    }//GEN-LAST:event_nuevoMBActionPerformed

    private void abrirMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirMBActionPerformed
        abrirMenuItemActionPerformed(evt);
    }//GEN-LAST:event_abrirMBActionPerformed

    private void guardarMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarMBActionPerformed
        guardarMenuItemActionPerformed(evt);
    }//GEN-LAST:event_guardarMBActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Figura> FigurasCB;
    private javax.swing.JButton abrirMB;
    private javax.swing.JMenuItem abrirMenuItem;
    private javax.swing.JMenuItem acercaMenuItem;
    private javax.swing.JToggleButton alisarBoton;
    private javax.swing.JMenu archivoMenuBar;
    private javax.swing.JMenu ayudaMenuBar;
    private javax.swing.JButton bandasBoton;
    private javax.swing.JToolBar barraDerechaTB;
    private javax.swing.JPanel barraEstado;
    private javax.swing.JToolBar barraSuperiorTB;
    private javax.swing.JToolBar barraizquierdaTB;
    private javax.swing.JSlider brilloSlider;
    private javax.swing.JButton capturaBoton;
    private javax.swing.JComboBox<Color> colorRellenoCB;
    private javax.swing.JComboBox<Color> colorTintadoCB;
    private javax.swing.JComboBox<Color> colorTrazoCB;
    private javax.swing.JButton contrasteBoton;
    private javax.swing.JButton cosinusoideBoton;
    private javax.swing.JComboBox<Color> degradado1CB;
    private javax.swing.JComboBox<Color> degradado2CB;
    private javax.swing.JButton disenioPropio2Boton;
    private javax.swing.JButton disenioPropioBoton;
    private javax.swing.JButton disminuirBoton;
    private javax.swing.JButton duplicarBoton;
    private javax.swing.JButton ecualizacionBoton;
    private javax.swing.JToggleButton elipseBoton;
    private javax.swing.JButton emborronamientoBoton2;
    private javax.swing.JButton enfoqueBoton;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JComboBox<String> espacioColorCB;
    private javax.swing.JLabel eventoLabel;
    private javax.swing.ButtonGroup formasBG;
    private javax.swing.JSlider giroLibreSlider;
    private javax.swing.JSpinner grosorSpinner;
    private javax.swing.JButton guardarMB;
    private javax.swing.JMenuItem guardarMenuItem;
    private javax.swing.JButton iluminarBoton;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JToggleButton lineaBoton;
    private javax.swing.JComboBox<File> listaMediaCB;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton negativoBoton;
    private javax.swing.JButton nuevoMB;
    private javax.swing.JMenuItem nuevoMenuItem;
    private javax.swing.JButton oscurecerBoton;
    private javax.swing.JToggleButton pausaGrabacionBoton;
    private javax.swing.JToggleButton pausaSonidoBoton;
    private javax.swing.JLabel pixelLabel;
    private javax.swing.JToggleButton recordSonidoBoton;
    private javax.swing.JToggleButton rectanguloBoton;
    private javax.swing.JToggleButton rectanguloRedBoton;
    private javax.swing.JButton relieveBoton;
    private javax.swing.JToggleButton reproducirSonidoBoton;
    private javax.swing.JButton rot180Boton;
    private javax.swing.JButton rot270Boton;
    private javax.swing.JButton rot90Boton;
    private javax.swing.ButtonGroup rotacionBG;
    private javax.swing.JButton sepiaBoton;
    private javax.swing.ButtonGroup sonidoBG;
    private javax.swing.JToggleButton stopRecordBoton;
    private javax.swing.JToggleButton stopSonidoBoton;
    private javax.swing.JSlider tintadoSlider;
    private javax.swing.JToggleButton tintarBoton;
    private javax.swing.JComboBox<TipoLinea> tipoLineaCB;
    private javax.swing.JComboBox<TipoRelleno> tipoRellenoCB;
    private javax.swing.JSlider transparenciaSlider;
    private javax.swing.JSlider umbralizacionSlider;
    private javax.swing.JCheckBoxMenuItem verBarraDerechaCH;
    private javax.swing.JCheckBoxMenuItem verBarraEstadoCB;
    private javax.swing.JCheckBoxMenuItem verBarraIzqCB;
    private javax.swing.JCheckBoxMenuItem verBarraSupCB;
    private javax.swing.JMenu verMenuBar;
    private javax.swing.JButton webcamBoton;
    private javax.swing.JTextField xPosition;
    private javax.swing.JTextField yPosition;
    private javax.swing.JButton zoomInBoton;
    // End of variables declaration//GEN-END:variables
}
