package easy.domain.eventactivemq;

import org.junit.AfterClass;
import org.junit.Test;

import easy.domain.activemqdomainevent.ActiveMqManagerFactory;

public class ApplicationTest {
	@Test
	public void test(){
		ApplicationCenter.getDemo().add();
	}
	@AfterClass
	public static void clear(){
		ActiveMqManagerFactory.clear();
	}
}
