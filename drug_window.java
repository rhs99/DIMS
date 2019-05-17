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

public class drug_window
{
    static ListView drugs;
    static ArrayList<String> strings;


    public static void show()
    {
        drugs = new ListView();

        String s;
        for(int i = 0;i<strings.size();i++)
        {
            //s = "";
            //s = id.get(i) + " " + strings.get(i);
            drugs.getItems().add(strings.get(i));
        }

        Text text_select = new Text("Select a Drug:");
        text_select.setFont(Font.font("Helvetica", 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setFont(Font.font("Helvetica", 14));
        submit.setOnAction(e->handle());
        submit.setMinWidth(50);
        submit.setDefaultButton(true);

        HBox submit_pane = new HBox(0, spacer, submit);
        VBox pane = new VBox(20, text_select, drugs, submit_pane);
        pane.setPadding(new Insets(20));

        home_window.field = pane;
        home_window.reset();

        /*home_window.stage.setScene(new Scene(pane));
        home_window.stage.setTitle("Drug Window");
        home_window.stage.show();*/
    }

    private static void handle()
    {
        int idd = 0;
        try {
            //Tokenize.tokenize(drugs.getSelectionModel().getSelectedItems().get(0).toString());
            //int id = Integer.parseInt(Tokenize.tokens.get(0));


            String s = (String)drugs.getSelectionModel().getSelectedItems().get(0).toString();

            // Tokenize.tokenize(s);

            //int id = Integer.parseInt(Tokenize.tokens.get(0));


            int id = 0;
            String query = "SELECT id FROM medicine where lower(name) = lower('" + s + "');";
            ResultSet rs = PSQL.query(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }


            query =
                    "  select med.id aa,med.name bb,gd.name cc,med.medicine_type dd,med.weight ee,com.name ff" +
                            " from (medicine med join company com on med.company_id = com.id)" +
                            " join generic_drug gd on gd.id = med.generic_id" +
                            " where med.id = " + id + ";";

            rs = PSQL.query(query);
            s = "";

            while (rs.next()) {
                idd = rs.getInt("aa");
                s += "Product Name: " + rs.getString("bb") + "\nGeneric Name: ";
                s += rs.getString("cc") + "\nType: " + rs.getString("dd") + "\nWeight/Volume: ";
                s += rs.getString("ee") + "\nCompany: ";
                s += rs.getString("ff") + "\n";
            }


            query = "select * from description where medicine_id = " + id + ";";

            rs = PSQL.query(query);

            while (rs.next()) {
                rs.getInt("medicine_id");

                s += "Adult Dose: " + rs.getString("adult_dose")+"\n";
                s += "Child Dose: " + rs.getString("child_dose")+"\n";
                s += "Renal Dose: " + rs.getString("renal_dose")+"\n";
                s += "Side Effects: " + rs.getString("side_effects")+"\n";;
                s += "Precautions and Warnings: " + rs.getString("precautions_and_warnings")+"\n";;
                s += "Pack Size and Prize: " + rs.getString("pack_size_and_prize")+"\n";;

            }



            message_box.show(s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}