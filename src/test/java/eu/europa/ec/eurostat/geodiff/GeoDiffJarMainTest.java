package eu.europa.ec.eurostat.geodiff;

import junit.framework.TestCase;

/**
 * @author julien Gaffuri
 *
 */
public class GeoDiffJarMainTest extends TestCase {


	public static void main(String[] args) {
		junit.textui.TestRunner.run(GeoDiffJarMainTest.class);
	}

	public void test() throws Exception {
		GeoDiffJarMain.main(new String[] {"-ini", "src/test/resources/ini_surf.gpkg", "-fin", "src/test/resources/fin_surf.gpkg", "-o", "target/test/"});
	}

}
