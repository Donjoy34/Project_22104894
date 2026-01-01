import view.MainView;
import model.DataManager;
import model.User;
import controller.SystemController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            DataManager model = new DataManager();
            MainView view = new MainView(model);
            view.setOnLoginSuccess(() -> {
                User currentUser = view.getCurrentUser();
                new SystemController(view, model, currentUser);
            });

            view.setVisible(true);
        });
    }
}