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

	public void test() {
		for(String gt : new String[] {"surf","lin","pt"})
			GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout/test_"+gt+"/"});
	}

	public void testRes() {
		for(String gt : new String[] {"surf"})
			GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout/test_res_"+gt+"/", "-res", "50"});
	}

	public void testOutFormat() {
		for(String gt : new String[] {"surf","lin","pt"}) {
			GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout/test_of_"+gt+"/", "-of", "gpkg"});
			GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout/test_of_"+gt+"/", "-of", "shp"});
			GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout/test_of_"+gt+"/", "-of", "geojson"});
		}
	}

	public void testUpdateMode() {
		for(String gt : new String[] {"surf","lin","pt"}) {
			GeoDiffJarMain.main(new String[] {"-m", "up", "-d", "src/test/resources/ini_"+gt+".gpkg", "-c", "src/test/resources/geodiff_"+gt+".gpkg"});
		}
	}

}
