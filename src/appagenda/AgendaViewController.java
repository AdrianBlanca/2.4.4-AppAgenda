/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package appagenda;

import entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author usuario
 */
public class AgendaViewController implements Initializable {

    private EntityManager em;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    
    private Persona personaSeleccionada;
    @FXML
    private AnchorPane rootAgendaView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory( cellData -> {
            SimpleStringProperty property=new SimpleStringProperty();
            
            if (cellData.getValue().getProvincia()!=null){
             property.setValue(cellData.getValue().getProvincia().getNombre());
             
            }
            
            return property;
            });

        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            personaSeleccionada = newValue;
            if(personaSeleccionada != null) {
                textFieldNombre.setText(personaSeleccionada.getNombre());
                textFieldApellidos.setText(personaSeleccionada.getApellidos());

                
            }
            
            else {
                textFieldNombre.setText("");
                textFieldApellidos.setText("");
                
            }
        
        });
        
    }    
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
        
    }
    
    public void cargarTodasPersonas() {
        Query queryPersonaFindAll = em.createNamedQuery("Persona.findAll");
        List<Persona> listPersona=queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona));
        
    }

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        if(personaSeleccionada != null) {
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
            
        }
        
        em.getTransaction().begin();
        em.merge(personaSeleccionada);
        em.getTransaction().commit();    
        
        int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
        tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
        
        TablePosition pos = new TablePosition(tableViewAgenda,numFilaSeleccionada,null);
        tableViewAgenda.getFocusModel().focus(pos);
        tableViewAgenda.requestFocus();
        
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            personaSeleccionada = new Persona();
            personaDetalleViewController.setPersona(em, personaSeleccionada, true);
            personaDetalleViewController.mostrarDatos();
            
            rootAgendaView.setVisible(false);
            
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
        } catch(IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex);
            
        }
        
    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonaDetalleView.fxml"));
            Parent rootDetalleView = fxmlLoader.load();
            
            PersonaDetalleViewController personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            personaDetalleViewController.setPersona(em, personaSeleccionada, false);
            personaDetalleViewController.mostrarDatos();
            
            rootAgendaView.setVisible(false);
            
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            rootMain.getChildren().add(rootDetalleView);
            
        } catch(IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE,null,ex);
            
        }
        
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " " + personaSeleccionada.getApellidos());
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.get() == ButtonType.OK) {
            em.getTransaction().begin();
            em.merge(personaSeleccionada);
            em.remove(personaSeleccionada);
            em.getTransaction().commit();
            
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();
            
        }
        
        else {
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
            
        }
        
    }
    
}
