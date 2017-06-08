package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import java.util.ArrayList;
import java.util.List;

public class Registration {
    private List<User> users;

    public Registration() {
        users = new ArrayList<User>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
    }
}
