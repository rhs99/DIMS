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
import jdk.nashorn.internal.ir.IdentNode;

import java.sql.ResultSet;
import java.util.ArrayList;

public class drug_class_window
{
    static ListView classes;
    static ArrayList<String> strings;

    public static void show()
    {
        classes = new ListView();


        for(int i = 0;i<strings.size();i++)
        {
            classes.getItems().add(strings.get(i));
        }

        Text text_select = new Text("Select a Class:");
        text_select.setFont(Font.font("Helvetica", 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setFont(Font.font("Helvetica", 14));
        submit.setOnAction(e->handle());
        submit.setMinWidth(50);
        submit.setDefaultButton(true);

        HBox submit_pane = new HBox(0, spacer, submit);
        VBox pane = new VBox(20, text_select, classes, submit_pane);
        pane.setPadding(new Insets(20));

        home_window.field = pane;
        home_window.reset();

        /*home_window.stage.setScene(new Scene(pane));
        home_window.stage.setTitle("Drug Class Window");
        home_window.stage.show();*/
    }

    private static void handle()
    {
        try
        {
            String s = ((String) classes.getSelectionModel().getSelectedItems().get(0)).toString();

            //Tokenize.tokenize(s);

            //int id = Integer.parseInt(Tokenize.tokens.get(0));

            int id = 0;
            String query = "SELECT id FROM drug_class where name = '" + s + "';";
            ResultSet rs = PSQL.query(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }



            query = "select y.name" +
                    " from drug_class x join drug_subclass y on x.id = y.drug_class_id" +
                    " where x.id = " + id + ";";


           // String query = "select tg.id,tg.name from cures tc join generic_drug tg on" +
                //    " tc.generic_id = tg.id where tc.indication_id = " + id + ";";

            rs = PSQL.query(query);


            drug_subclass_window.strings = new ArrayList<>();


            while (rs.next()) {
                drug_subclass_window.strings.add(rs.getString("name"));
            }

            drug_subclass_window.show();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
