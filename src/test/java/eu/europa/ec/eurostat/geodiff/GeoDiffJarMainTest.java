package eu.europa.ec.eurostat.geodiff;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import eu.europa.ec.eurostat.jgiscotools.geodiff.GeoDiff;
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
		for(String gt : new String[] {"surf","lin","pt"})
			GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout_diff/test_"+gt+"/"});
	}

	public void testRes() throws Exception {
		for(String gt : new String[] {"surf"})
			GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout_diff/test_res_"+gt+"/", "-res", "50"});
	}

	public void testOutFormat() throws Exception {
		for(String gt : new String[] {"surf","lin","pt"})
			for(String of : new String[] {"gpkg","shp","geojson"})
				GeoDiffJarMain.main(new String[] {"-m", "diff", "-v1", "src/test/resources/ini_"+gt+".gpkg", "-v2", "src/test/resources/fin_"+gt+".gpkg", "-o", "target/testout_diff/test_of_"+gt+"/", "-of", of});
	}

	public void testUpdateMode() throws Exception {
		Configurator.setLevel(GeoDiff.class.getName(), Level.OFF);
		for(String gt : new String[] {"surf","lin","pt"})
			GeoDiffJarMain.main(new String[] {"-m", "up", "-d", "src/test/resources/ini_"+gt+".gpkg", "-c", "src/test/resources/geodiff_"+gt+".gpkg", "-o", "target/testout_up/test_"+gt+".gpkg"});
	}

}
