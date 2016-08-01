package easy.domain.activemqdomainevent;

import java.util.ArrayList;
import java.util.List;

public abstract class ActiveMqManagerFactory {

	private static final List<ActiveMqManager> managers = new ArrayList<>();

	public static ActiveMqManager createActiveMqManager(String url,
			String clientid, String usrname, String password) {

		ActiveMqManager m = new ActiveMqManager(url, clientid, usrname,
				password);

		managers.add(m);
		return m;
	}

	public static ActiveMqManager createActiveMqManager(String url) {
		ActiveMqManager m = new ActiveMqManager(url);
		managers.add(m);
		return m;
	}

	public static ActiveMqManager createActiveMqManager(String url,
			String clientId) {
		ActiveMqManager m = new ActiveMqManager(url, clientId);
		managers.add(m);
		return m;
	}

	public static void clear() {
		for (ActiveMqManager m : managers) {
			m.close();
		}
	}
}
