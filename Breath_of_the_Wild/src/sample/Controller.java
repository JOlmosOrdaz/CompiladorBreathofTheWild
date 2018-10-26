package sample;

import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sample.Constants.Configs.*;

import static sample.Constants.Configs.EXPRESIONES;
import static sample.Constants.Configs.computeHighlighting;
import static sample.Constants.Configs.sampleCode;

public class Controller{
    private Stage stage;

    @FXML
    TextArea txtConsola;

    @FXML private HBox paneSote;



    CodeArea codeArea = new CodeArea();
    @FXML protected void initialize(){
        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.replaceText(0, 0, sampleCode);

        codeArea.setPrefSize(800,500);

        // recompute the syntax highlighting 500 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = codeArea
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        HBox.setHgrow(codeArea, Priority.ALWAYS);
        paneSote.getChildren().add(codeArea);

    }

    public void evtSalir(ActionEvent event){
        System.exit(0);
    }
    public void evtAbrir(ActionEvent event){
        FileChooser of=new FileChooser();
        of.setTitle("Abrir archivo BOTW");
        FileChooser.ExtensionFilter filtro= new FileChooser.ExtensionFilter("Archivos .BOTW","*.BOTW");
        of.getExtensionFilters().add(filtro);
        File file=of.showOpenDialog(stage);
    }//llave abrir

    public void start(Stage stage) throws Exception {
        this.stage=stage;
    }

    public void ejecutar(ActionEvent event){
        compilar();
    }///LLAVE EJECUTAR
    public void compilar(){
        txtConsola.setText("");
        long tInicial=System.currentTimeMillis();

        String texto=codeArea.getText();
        String[] renglones=texto.split("\\n");

        for(int x=0;x<renglones.length;x++){
            boolean bandera=false;
            if (!renglones[x].trim().equals("")){
                for(int y=0;y< EXPRESIONES.length && bandera==false;y++){
                    Pattern patron=Pattern.compile(EXPRESIONES[y]);
                    Matcher matcher=patron.matcher(renglones[x]);
                    if(!matcher.matches()){
                        bandera=true;

                    }
                 }
            }

            if(bandera==false){
                txtConsola.setText(
                        txtConsola.getText()+" \n"+
                                "Error de sintaxis en la linea "+(x+1));
            }//llave if
        }//llave for
        long tFinal=System.currentTimeMillis()-tInicial;
        txtConsola.setText(txtConsola.getText()+"\n"+
                "Compilador en :"+tFinal+" Milisegundos");

    }

}
