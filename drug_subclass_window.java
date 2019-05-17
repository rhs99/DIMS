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

public class drug_subclass_window
{
    static ListView subclasses;
    static ArrayList<String> strings;


    public static void show()
    {
        subclasses = new ListView();

        for(int i = 0;i<strings.size();i++)
        {
            subclasses.getItems().add(strings.get(i));
        }


        Text text_select = new Text("Select a Subclass:");
        text_select.setFont(Font.font("Helvetica", 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setFont(Font.font("Helvetica", 14));
        submit.setOnAction(e->handle());
        submit.setMinWidth(50);
        submit.setDefaultButton(true);

        HBox submit_pane = new HBox(0, spacer, submit);
        VBox pane = new VBox(20, text_select, subclasses, submit_pane);
        pane.setPadding(new Insets(20));

        home_window.field = pane;
        home_window.reset();
    }

    private static void handle()
    {
        try
        {

            String s = ((String) subclasses.getSelectionModel().getSelectedItems().get(0)).toString();

            int id = 0;
            String query = "SELECT id FROM drug_subclass where name = '" + s + "';";
            ResultSet rs = PSQL.query(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }

            query =  "select y.name" +
                    " from drug_subclass x join generic_drug y on x.id = y.subclass_id" +
                    " where x.id = " + id +";";

            rs = PSQL.query(query);


            generic_drug_window.strings = new ArrayList<>();


            while (rs.next()) {
                generic_drug_window.strings.add(rs.getString("name"));
            }

            generic_drug_window.show();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
