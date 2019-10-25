package util;

import model.Person;

public class PermissionManager {
	static public boolean hasAccess(Person person, int permission) {
		return person.getPermission() >= permission;
	}
}
