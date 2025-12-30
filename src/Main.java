import view.MainView;
import model.DataManager;
import controller.SystemController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            DataManager model = new DataManager();
            new SystemController(view, model);

            view.setVisible(true);
        });
    }
}