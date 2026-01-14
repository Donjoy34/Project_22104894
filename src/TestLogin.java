public class TestLogin {
    public static void main(String[] args) {
        model.DataManager dm = new model.DataManager();
        dm.loadData();
        model.User u = dm.authenticate("admin", "admin", view.UserRole.ADMIN);
        if (u != null) {
            System.out.println("AUTH OK: id=" + u.getId() + ", username=" + u.getUsername() + ", role=" + u.getRole());
        } else {
            System.out.println("AUTH FAILED");
        }
        System.out.println("-- Users loaded --");
        for (model.User x : dm.getUsers()) {
            System.out.println(" - " + x.getId() + "," + x.getUsername() + "," + x.getPasswordHash() + "," + x.getRole());
        }
    }
}
