import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.ArrayList;

public class indication_window
{
    static ListView indications;
    static ArrayList<String> strings;
    static ArrayList<Integer> id;

    public static void show()throws Exception
    {
        indications = new ListView();

        String s;
        for(int i = 0;i<strings.size();i++)
        {
            //s = "";
            //s = id.get(i) + " " + strings.get(i);
            indications.getItems().add(strings.get(i));
        }


        Text text_select = new Text("Select an Indication:");
        text_select.setFont(Font.font("Helvetica", 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setFont(Font.font("Helvetica", 14));
        submit.setMinWidth(50);
        submit.setDefaultButton(true);

        submit.setOnAction(e -> {
            try {
                handle();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        HBox submit_pane = new HBox(0, spacer, submit);
        VBox pane = new VBox(20, text_select, indications, submit_pane);
        pane.setPadding(new Insets(20));

        home_window.field = pane;
        home_window.reset();

        /*home_window.stage.setScene(new Scene(pane));
        home_window.stage.setTitle("Indication Window");
        home_window.stage.show();*/
    }

    private static void handle() throws Exception
    {
        String s = ((String) indications.getSelectionModel().getSelectedItems().get(0)).toString();

        //Tokenize.tokenize(s);

        //int id = Integer.parseInt(Tokenize.tokens.get(0));


       // String query = "select tg.id,tg.name from cures tc join generic_drug tg on" +
               //" tc.generic_id = tg.id where tc.indication_id = " + id + ";";

        int id = 0;
        String query = "SELECT id FROM indication where lower(name) = lower('" + s + "');";
        ResultSet rs = PSQL.query(query);
        while (rs.next()) {
            id = rs.getInt("id");
        }


        query = "select tg.name from cures tc join generic_drug tg on" +
                " tc.generic_id = tg.id where tc.indication_id = " + id + ";";

        rs = PSQL.query(query);

        generic_drug_window.strings = new ArrayList<>();

        while (rs.next()) {
            generic_drug_window.strings.add(rs.getString("name"));
        }

        generic_drug_window.show();
    }
}
