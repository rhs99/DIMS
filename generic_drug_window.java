import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.ArrayList;

public class generic_drug_window
{
    static ListView generic_drugs;
    static ArrayList<String> strings;

    public static void show()
    {
        generic_drugs = new ListView();

        String s;
        for(int i = 0;i<strings.size();i++)
        {
            //s = "";
            //s = id.get(i) + " " + strings.get(i);
            generic_drugs.getItems().add(strings.get(i));
        }


        Text text_select = new Text("Select a Generic Drug:");
        text_select.setFont(Font.font("Helvetica", 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setFont(Font.font("Helvetica", 14));
        submit.setOnAction(e->handle());
        submit.setMinWidth(50);
        submit.setDefaultButton(true);

        HBox submit_pane = new HBox(0, spacer, submit);
        VBox pane = new VBox(20, text_select, generic_drugs, submit_pane);
        pane.setPadding(new Insets(20));

        home_window.field = pane;
        home_window.reset();

        /*home_window.stage.setScene(new Scene(pane));
        home_window.stage.setTitle("Generic Drug Window");
        home_window.stage.show();*/
    }

    private static void handle()
    {
        try
        {
            String s = ((String) generic_drugs.getSelectionModel().getSelectedItems().get(0)).toString();

            int id = 0;
            String query = "SELECT id FROM generic_drug where lower(name) = lower('" + s + "');";
            ResultSet rs = PSQL.query(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }


            query = "select name from medicine " +
                    "where generic_id = " + id + ";";

            rs = PSQL.query(query);

            drug_window.strings = new ArrayList<>();


            while (rs.next()) {
                drug_window.strings.add(rs.getString("name"));
            }

            drug_window.show();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
