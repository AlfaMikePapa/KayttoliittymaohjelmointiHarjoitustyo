package opintojarjestelma;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLKirjautuminenController extends opintojarjestelma.Menu {
	@FXML
	public TextField txtSalasana;
	@FXML
	public TextField txtkayttajatunnus;
	@FXML
	public Button BtnKirjaudu;
    
                //Kirjautumismetodi jolla päästään päävalikkoon. tunnus "testi" ja salasana "testi"
		public void kirjautumisestamenuun (ActionEvent event) throws IOException{
		
			if (txtkayttajatunnus.getText().equals("testi") && txtSalasana.getText().contentEquals("testi")) {
		
				Parent Menusikkuna = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
				Scene tableviewScene = new Scene(Menusikkuna);
		
				Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		
				window.setScene(tableviewScene);
				window.show();
		
			}
                //Huomautus jos salasana tai käyttäjätunnus eivät täsmää.        
		else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Kirjautumisvirhe");
			alert.setHeaderText("Virhe");
			alert.setContentText("Virheellinen tunnus tai salasana");
			alert.showAndWait();
		}

        }

}
