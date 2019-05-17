import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class search_window
{
    static VBox pane;
    static RadioButton drug = new RadioButton("Drug");
    static RadioButton indication = new RadioButton("Indication");
    static RadioButton pharmacy = new RadioButton("Pharmacy");

    static void show()
    {
        ToggleGroup group = new ToggleGroup();

        drug.setFont(Font.font("Helvetica", 17));
        drug.setToggleGroup(group);

        indication.setFont(Font.font("Helvetica", 17));
        indication.setToggleGroup(group);

        pharmacy.setFont(Font.font("Helvetica", 17));
        pharmacy.setToggleGroup(group);

        VBox radios = new VBox(20, drug, indication, pharmacy);
        radios.setPadding(new Insets(10, 0, 20, 40));

        Text text_option = new Text("Search by");
        text_option.setFont(Font.font("Helvetica", 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setOnAction(e->handle());
        submit.setFont(Font.font("Helvetica", 17));
        submit.setMinWidth(80);
        submit.setDefaultButton(true);

        HBox submit_pane = new HBox(0, spacer, submit);
        pane = new VBox(20, text_option, radios, submit_pane);
        pane.setPadding(new Insets(20, 40, 20, 40));
        pane.setMinWidth(300);
        pane.setMinHeight(300);

        home_window.field = pane;
        home_window.reset();

        /*home_window.stage.setScene(new Scene(pane));
        home_window.stage.setTitle("Search Window");
        home_window.stage.show();*/
    }

    static void handle()
    {
        if (drug.isSelected())
        {
            input_window1.show(1);
        }
        else if (indication.isSelected())
        {
            input_window1.show(2);
        }
        else
        {
            input_window2.show();
        }
    }
}
