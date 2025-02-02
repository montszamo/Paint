package iu;

import eventos.LienzoEvent;
import eventos.LienzoListener;
import graficos.BoundingBox;
import graficos.Elipse;
import graficos.Figura;
import graficos.Forma;
import graficos.Linea;
import graficos.Rectangulo;
import graficos.RectanguloRedondeado;
import graficos.TipoLinea;
import graficos.TipoRelleno;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Lienzo. Representa el área de dibujo.
 * @author Montserrat Rodríguez Zamorano
 * @version 1.1
 */
public class Lienzo extends javax.swing.JPanel {
    //Eventos
    ArrayList <LienzoListener> lienzoEventListeners = new ArrayList();
 
    private List <Figura> vFiguras = new ArrayList();
    //atributos del lienzo
    //trazo
    private Color colorTrazo = null;
    private TipoLinea stroke = null;
    private int grosor = 1;
    //transparencia
    private float transparencia = 1.0f;
    //relleno
    private Color colorRelleno = null;
    private TipoRelleno tipoRelleno= TipoRelleno.NINGUNO;
    private Color colorDeg1 = null;
    private Color colorDeg2 = null;
    //forma
    private Forma formaActiva = null;
    //alisado
    private boolean alisadoActivated = false;
    
    private Point2D pF = new Point(-1000, -1000);
    private Point2D pI = new Point(-1000, -1000);
    
    //pixel actual
    private Point2D pA = new Point(-1000, -1000);
    
    //dimension
    private Dimension dimension = null;
    Figura fActiva;
    
    Figura figuraSeleccionada;
    Rectangulo boundingBox = null;
    /**
     * Creates new form Lienzo
     */
    public Lienzo() {
        initComponents();
    }
    
    /**
     * Añade una figura al vector de figuras.
     * @param f Figura figura a añadir
     */ 
    public void addFigura(Figura f){
        vFiguras.add(f);
        notifyShapeAddedEvent(new LienzoEvent(this, fActiva, pA, vFiguras,
        colorTrazo, stroke, grosor, transparencia, colorRelleno, tipoRelleno, 
        colorDeg1, colorDeg2, formaActiva, alisadoActivated));
    }
    /**
     * Pinta las figuras almacenadas en el vector, así como la bounding box
     * en el caso de que exista una figura seleccionada y delimita el área
     * de dibujo.
     * @param g Graphics objeto que permite dibujar
     */
    @Override
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.clip(new Rectangle(dimension));
        
        for(Figura f: vFiguras){
          f.paint(g2d);
        }
        
        if(figuraSeleccionada!=null){
            setBoundingBox();
            boundingBox.paint(g2d);
        }
    }
    /**
     * Getter.
     * @return List Figura lista de figuras almacenadas en el lienzo. 
     */
    public List <Figura> getvFiguras(){return vFiguras;}
    /**
     * Setter.
     * @param dimension Dimension nueva dimensión del área de dibujo
     */
    public void setDimension(Dimension dimension){this.dimension = dimension;}
    /**
     * Getter.
     * @return Dimension dimensión de la zona de dibujo
     */
    public Dimension getDimension(){return dimension;}
    /**
     * Setter.
     * @param color Color nuevo color activo para pintar en el lienzo
     */
    public void setColorTrazo(Color color){colorTrazo = color;}
    /**
     * Getter.
     * @return Color color de trazo activo en el lienzo para pintar
     */
    public Color getColorTrazo(){return colorTrazo;}
    /**
     * Getter.
     * @return Forma forma activa en el lienzo para pintar 
     */
    public Forma getForma(){return formaActiva;}
    /**
     * Setter.
     * @param forma Forma nueva forma activa
     */
    public void setForma(Forma forma){formaActiva = forma;}
    /**
     * Getter.
     * @return Color color de relleno activado en el lienzo
     */
    public Color getColorRelleno(){return colorRelleno;}
    /**
     * Setter.
     * @param color Color nuevo color de relleno
     */
    public void setColorRelleno(Color color){colorRelleno = color;}
    /**
     * Getter.
     * @return float grado de transparencia activado en el lienzo
     */
    public float getTransparencia(){return transparencia;}
    /**
     * Setter.
     * @param transparencia float nuevo grado de transparencia
     */
    public void setTransparencia(float transparencia){this.transparencia = transparencia;}
    /**
     * Getter.
     * @return boolean true/false si relleno activado/desactivado
     */
    public boolean getRellenoActivated(){return (tipoRelleno!=TipoRelleno.NINGUNO);}
    /**
     * Setter.
     * @param color Color nuevo color de relleno
     */
    public void setRelleno(Color color){colorRelleno = color;}
    /**
     * Getter.
     * @return TipoLinea tipo de trazo activo
     */
    public TipoLinea getStroke(){return stroke;}
    /**
     * Setter.
     * @param stroke TipoLinea nuevo tipo de trazo
     */
    public void setStroke(TipoLinea stroke){this.stroke = stroke;}
    /**
     * Getter.
     * @return boolean alisado activado o no 
     */
    public boolean getAlisadoActivated(){return alisadoActivated;}
    /**
     * Cambia el alisado de activado a desactivado o viceversa
     */
    public void changeAlisadoActivated(){ alisadoActivated = !alisadoActivated;}
    /**
     * Setter.
     * @param tp TipoRelleno nuevo tipo de relleno activo en el lienzo
     */
    public void setTipoRelleno(TipoRelleno tp){tipoRelleno = tp;} 
    /**
     * Setter.
     * @param deg Color nuevo primer color de degradado
     */
    public void setColorDeg1(Color deg){colorDeg1 = deg;} 
    /**
     * Getter.
     * @return Color primer color de degradado
     */
    public Color getColorDeg1(){return colorDeg1;}
    /**
     * Setter.
     * @param deg Color nuevo segundo color de degradado
     */
    public void setColorDeg2(Color deg){colorDeg2 = deg;}
    /**
     * Getter.
     * @return Color segundo color de degradado
     */
    public Color getColorDeg2(){return colorDeg2;}
    /**
     * Setter.
     * @param grosor int nuevo grosor de línea
     */
    public void setGrosor(int grosor){this.grosor = grosor;}
    /**
     * Getter.
     * @return int grosor de línea
     */
    public int getGrosor(){return grosor;}
    /**
     * Getter.
     * @return TipoRelleno tipo de relleno activo en el lienzo 
     */
    public TipoRelleno getTipoRelleno(){return tipoRelleno;}
    /**
     * Crea una nueva figura.
     * @param p1 Point2D punto de inicio
     * @param p2 Point2D punto final
     * @return Figura figura creada en función de los atributos activos en el
     * lienzo y los parámetros.
     */
    private Figura createFigura(Point2D p1, Point2D p2){
        if(formaActiva == null){
            notifyFaltaForma(new LienzoEvent(this, null, null, null, null, null, 
            0, 0f, null, null, null, null, null, false));
        }
        else if(colorTrazo == null){
            notifyFaltaColorTrazo(new LienzoEvent(this, null, null, null, null, null, 
            0, 0f, null, null, null, null, null, false));
        }
        else if(stroke == null){
            notifyFaltaTipoLinea(new LienzoEvent(this, null, null, null, null, null, 
            0, 0f, null, null, null, null, null, false));
        }
        else if(getRellenoActivated()  && tipoRelleno==TipoRelleno.LISO && colorRelleno==null){
            notifyFaltaColorRelleno(new LienzoEvent(this, null, null, null, null, null, 
            0, 0f, null, null, null, null, null, false));
        }
        else if(getRellenoActivated()  && 
                (tipoRelleno==TipoRelleno.DEGRADADO_DIAGONAL || 
                tipoRelleno==TipoRelleno.DEGRADADO_HORIZONTAL ||
                tipoRelleno == TipoRelleno.DEGRADADO_VERTICAL) && 
                (colorDeg1==null || colorDeg2 == null) ){
            notifyFaltaColorDegradado(new LienzoEvent(this, null, null, null, null, null, 
            0, 0f, null, null, null, null, null, false));
        }
        else{
            switch(formaActiva){
                case LINEA:
                    fActiva = new Linea(p1,p2,colorTrazo, stroke, grosor, transparencia, alisadoActivated);
                    break;
                case RECTANGULO:
                    fActiva = new Rectangulo(p1, p2, colorTrazo, stroke, grosor, colorRelleno, tipoRelleno, colorDeg1, colorDeg2, transparencia, alisadoActivated);
                    break;
                case OVALO:
                    fActiva = new Elipse(p1, p2, colorTrazo, stroke, grosor, colorRelleno, tipoRelleno, colorDeg1, colorDeg2, transparencia, alisadoActivated);
                    break;
                case RECTANGULOREDONDEADO:
                    fActiva = new RectanguloRedondeado(p1, p2, colorTrazo, stroke, grosor, colorRelleno, tipoRelleno, colorDeg1, colorDeg2, transparencia, alisadoActivated);
                    break;
                default:
                    break;
            }
            return fActiva;
        }
        return null;
    }
    /**
     * Actualiza una figura en función de los nuevos puntos de inicio y final
     */
    private void updateShape(){
        try{
            fActiva.updateShape(pI, pF);
        }catch(Exception e){}
    }
    
    /**
     * Cambia la posición de una figura
     * @param f Figura figura que quiere moverse
     * @param p Point2D nuevo punto de inicio de la figura
     */
    public void changePosition(Figura f, Point2D p){
        f.setLocation(p);
    }
    /**
     * Getter.
     * @return Figura figura seleccionada en el lienzo. 
     */
    public Figura getFiguraSeleccionada(){return figuraSeleccionada;}
    /**
     * Setter.
     * @param f Figura nueva figura seleccionada
     */
    public void setFiguraSeleccionada(Figura f){
        figuraSeleccionada = f;
    }
    /**
     * Crea la bounding box si hay una figura seleccionada.
     */
    private void setBoundingBox(){
        if(figuraSeleccionada!=null){//si hay alguna figura seleccionada
            Point2D p1 = figuraSeleccionada.getPO();
            Point2D p2 = figuraSeleccionada.getPF();
            Rectangle2D frame = new Rectangle();
            frame.setFrameFromDiagonal(p1,p2);
            boundingBox = new BoundingBox(frame);
            this.repaint();
        }
    }
    /**
     * Elimina la bounding box y la figura seleccionada.
     */
    public void quitBoundingBox(){
        figuraSeleccionada=null;
        boundingBox=null;
    }
    /**
     * Añade un LienzoListener.
     * @param listener LienzoListener a añadir.
     */
    public void addLienzoListener(LienzoListener listener){
        if(listener != null){
            lienzoEventListeners.add(listener);
        }
    }
    /**
     * Indica que una figura ha sido añadida
     * @param evt LienzoEvent
     */
    private void notifyShapeAddedEvent(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.shapeAdded(evt);
            }
        }
    }
    /**
     * Indica que una propiedad ha sido modificada
     * @param evt LienzoEvent
     */
    private void notifyPropertyChangeEvent(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.propertyChange(evt);
            }
        }
    }
    /**
     * Indica que el ratón se ha movido sobre el lienzo
     * @param evt LienzoEvent
     */
    private void notifyMouseMoved(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.mouseMoved(evt);
            }
        }
    }
    /**
     * Indica que es necesario seleccionar una forma
     * @param evt LienzoEvent
     */
    private void notifyFaltaForma(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.faltaForma(evt);
            }
        }
    }
    /**
     * Indica que es necesario seleccionar un color de trazo
     * @param evt LienzoEvent
     */
    private void notifyFaltaColorTrazo(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.faltaColorTrazo(evt);
            }
        }
    }
    /**
     * Indica que es necesario seleccionar un tipo de línea
     * @param evt LienzoEvent
     */
    private void notifyFaltaTipoLinea(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.faltaTipoLinea(evt);
            }
        }
    }
    /**
     * Indica que es necesario indicar un tipo de relleno.
     * @param evt LienzoEvent
     */
    private void notifyFaltaTipoRelleno(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.faltaTipoRelleno(evt);
            }
        }
    }
    /**
     * Indica que es necesario seleccionar un color de relleno.
     * @param evt LienzoEvent
     */
    private void notifyFaltaColorRelleno(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.faltaColorRelleno(evt);
            }
        }
    }
    /**
     * Indica que es necesario seleccionar un color de degradado.
     * @param evt LienzoEvent
     */
    private void notifyFaltaColorDegradado(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.faltaColorDegradado(evt);
            }
        }
    }
    /**
     * Indica que un lienzo ha sido seleccionado.
     * @param evt LienzoEvent
     */
    private void notifyLienzoSeleccionado(LienzoEvent evt){
        if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.lienzoSeleccionado(evt);
            }
        }
    }
    /**
     * Indica que un lienzo ha sido cerrado.
     * @param evt LienzoEvent
     */
    private void notifyLienzoCerrado(LienzoEvent evt){
       if(!lienzoEventListeners.isEmpty()){
            for(LienzoListener listener : lienzoEventListeners){
                listener.lienzoCerrado(evt);
            }
        } 
    }    
    /**
     * Indica que el lienzo está activo.
     */
    public void setLienzoActivado(){
        this.notifyLienzoSeleccionado(new LienzoEvent(this, fActiva, pA, vFiguras,
        colorTrazo, stroke, grosor, transparencia, colorRelleno, tipoRelleno, 
        colorDeg1, colorDeg2, formaActiva, alisadoActivated));
    }
    /**
     * Indica que el lienzo está inactivo.
     */
    public void lienzoCerrado(){
        this.notifyLienzoCerrado(new LienzoEvent(this, fActiva, pA, vFiguras,
        colorTrazo, stroke, grosor, transparencia, colorRelleno, tipoRelleno, 
        colorDeg1, colorDeg2, formaActiva, alisadoActivated));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(204, 204, 204));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 549, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Gestiona el evento de pulsar el ratón.
     * @param evt MouseEvent
     */
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        //si hacemos otras cosas, se desactiva la bounding box
        boundingBox = null;
        figuraSeleccionada = null;
        pI = evt.getPoint();
        Figura f = createFigura(pI, pF);
        if(f!=null){
            addFigura(f);
        }
        setFiguraSeleccionada(null);
    }//GEN-LAST:event_formMousePressed
    /**
     * Gestiona el evento de soltar el ratón
     * @param evt MouseEvent
     */
    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        this.formMouseDragged(evt);
    }//GEN-LAST:event_formMouseReleased
    /**
     * Gestiona el evento de arrastrar el ratón.
     * @param evt MouseEvent
     */
    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        pF = evt.getPoint();
        updateShape();
        pA = evt.getPoint();
        notifyMouseMoved(new LienzoEvent(this, fActiva, pA, vFiguras,
        colorTrazo, stroke, grosor, transparencia, colorRelleno, tipoRelleno, 
        colorDeg1, colorDeg2, formaActiva, alisadoActivated));
        this.repaint();
    }//GEN-LAST:event_formMouseDragged
    /**
     * Gestiona el evento del movimiento del ratón.
     * @param evt MouseEvent
     */
    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        pA = evt.getPoint();
        notifyMouseMoved(new LienzoEvent(this, fActiva, pA, vFiguras,
        colorTrazo, stroke, grosor, transparencia, colorRelleno, tipoRelleno, 
        colorDeg1, colorDeg2, formaActiva, alisadoActivated));
    }//GEN-LAST:event_formMouseMoved

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

    }//GEN-LAST:event_formMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
