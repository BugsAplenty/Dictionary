import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;


public class DictionaryController implements Initializable {
    public ToggleButton editDefinitionBtn;
    public Button saveDefinitionBtn;
    public Button addTermBtn;
    public Button editTermNameBtn;
    public Button removeTermBtn;
    public Button findBtn;
    public TextArea definitionTextField;
    public TextField termTextField;
    public TextField searchTextField;
    public ListView<String> termList;
    public MenuItem exportCSVMenuItem;
    public MenuItem loadCSVMenuItem;
    private String currentTerm;
    private final TreeMap<String, String> dict = new TreeMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void addTermBtnPressed() {
        TextInputDialog addTermDialog = new TextInputDialog();
        addTermDialog.setTitle("Add New Term");
        addTermDialog.setContentText("Enter new term:");
        Optional<String> result = addTermDialog.showAndWait();
        result.ifPresent(s -> currentTerm = s);
        if (currentTerm.isEmpty()) {
            Alert invalidTermAlert = new Alert(Alert.AlertType.ERROR, "Invalid input!");
            invalidTermAlert.showAndWait();
        } else if (dict.containsKey(currentTerm)) {
            Alert duplicateTermAlert = new Alert(Alert.AlertType.ERROR, "Duplicate term! Edit existing term or choose new name.");
            duplicateTermAlert.showAndWait();
        } else {
            dict.put(currentTerm, "");
        }
        refreshView();
    }

    public void editTermNameBtnPressed() {
        String termDefinition = dict.get(currentTerm);
        dict.remove(currentTerm);
        TextInputDialog editTermNameDialog = new TextInputDialog();
        editTermNameDialog.setTitle("Edit Term Name");
        editTermNameDialog.setContentText("Enter new name:");
        Optional<String> result = editTermNameDialog.showAndWait();
        result.ifPresent(s -> currentTerm = s);
        dict.put(currentTerm, termDefinition);
        refreshView();
    }

    public void removeTermBtnPressed() {
        dict.remove(currentTerm);
        currentTerm = "";
        refreshView();
    }

    public void saveDefinitionBtnPressed() {
        dict.put(currentTerm, definitionTextField.getText());
        Alert duplicateTermAlert = new Alert(Alert.AlertType.INFORMATION, "Definition saved.");
        duplicateTermAlert.showAndWait();
        refreshView();
    }
    public void editDefinitionBtnPressed() {
        definitionTextField.setDisable(!editDefinitionBtn.isSelected());
    }
    public void refreshView() {
        refreshTermList();
        refreshTermField();
        refreshDefinitionField();
        refreshEditDefinitionButton();
    }

    private void refreshEditDefinitionButton() {
        editDefinitionBtn.setSelected(false);
    }

    private void refreshTermField() {
        termTextField.setText(currentTerm);
    }

    private void refreshDefinitionField() {
        if(currentTerm.isEmpty()) {
            definitionTextField.setText("");
        } else {
            definitionTextField.setText(dict.get(currentTerm));
        }
        definitionTextField.setDisable(true);
    }

    private void refreshTermList() {
        ObservableList<String> dictKeysAsList = FXCollections.observableArrayList(dict.keySet());
        termList.setItems(dictKeysAsList);
    }

    public void findBtnPressed() {
        if (searchTextField.getText().isEmpty()) {
            Alert emptySearchFieldAlert = new Alert(Alert.AlertType.ERROR, "Search field empty!");
            emptySearchFieldAlert.showAndWait();
        } else if (!dict.containsKey(searchTextField.getText())) {
            Alert termNotFound = new Alert(Alert.AlertType.INFORMATION, "Term not found!");
            termNotFound.showAndWait();
        } else {
            termList.scrollTo(searchTextField.getText());
            currentTerm = searchTextField.getText();
            refreshView();
        }
    }

    public void termListMouseClicked() {
        currentTerm = termList.getSelectionModel().getSelectedItem();
        refreshView();
    }
    public void loadCSVMenuItemPressed() {
        resetDict();
        File file = getFileCSV();
        ingestCSV(file);
        refreshView();
    }

    private void ingestCSV(File file) {
        try(Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split("\t");
                dict.put(row[0], row[1]);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetDict() {
        dict.clear();
        currentTerm = "";
    }

    public void exportCSVMenuItemPressed() {

    }
    private File getFileCSV() {
        FileChooser.ExtensionFilter csvExtFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().add(csvExtFilter);
        return fileChooser.showOpenDialog(null);
    }
}
