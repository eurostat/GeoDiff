package eu.europa.ec.eurostat.geodiff;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.europa.ec.eurostat.jgiscotools.feature.Feature;
import eu.europa.ec.eurostat.jgiscotools.feature.FeatureUtil;
import eu.europa.ec.eurostat.jgiscotools.feature.JTSGeomUtil;
import eu.europa.ec.eurostat.jgiscotools.geodiff.GeoDiff;
import eu.europa.ec.eurostat.jgiscotools.io.geo.GeoData;

/**
 * @author julien Gaffuri
 *
 */
public class GeoDiffJarMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//define options
		Options options = new Options();

		//common options
		options.addOption(Option.builder("m").longOpt("mode").desc("For difference analysis, set to 'diff'. For update mode, set to 'up'")
				.hasArg().argName("value").build());
		options.addOption(Option.builder("id").longOpt("identifier").desc("Optional. Name of the identifier field of the dataset. Default: 'id'.")
				.hasArg().argName("file path").build());

		//options for difference analysis mode
		options.addOption(Option.builder("v1").longOpt("version1").desc("Version 1 of the dataset. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("v2").longOpt("version2").desc("Version 2 of the dataset. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("res").longOpt("resolution").desc("Optional. The geometrical resolution of the dataset. Geometrical differences below this value will be ignored. Default: 0.")
				.hasArg().argName("value").build());
		options.addOption(Option.builder("ati").longOpt("attributesToIgnore").desc("Optional. List of attributes to ignore for the comparison, comma separated.")
				.hasArg().argName("value").build());
		options.addOption(Option.builder("o").longOpt("outputFolder").desc("Optional. Output folder.")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("of").longOpt("outputFormat").desc("Optional. Output format. The supported formats are GeoJSON ('geojson'), SHP ('shp') and GeoPackage ('gpkg'). Default: 'out.gpkg'.")
				.hasArg().argName("file path").build());

		//options for update mode
		options.addOption(Option.builder("d").longOpt("dataset").desc("Initial version of the dataset. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("c").longOpt("geoDiffFile").desc("The changes/updates to apply, in GeoDiff format. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("o").longOpt("outputFile").desc("Optional. Output file with changes applied. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension). Default: 'out.gpkg'.")
				.hasArg().argName("file path").build());

		options.addOption(Option.builder("h").desc("Show this help message").build());

		//read options
		CommandLine cmd = null;
		try { cmd = new DefaultParser().parse( options, args); } catch (ParseException e) {
			System.err.println("Error when reading parameters.");
			System.err.println(" Reason: " + e.getMessage() );
			return;
		}

		//help statement
		if(cmd.hasOption("h")) {
			new HelpFormatter().printHelp("java -jar GeoDiff.jar", options);
			return;
		}

		String param;

		//get mode
		param = cmd.getOptionValue("m");
		if(param == null) {
			System.err.println("Mode should be specified. 'diff' for difference analysis mode, 'up' for update mode.");
			return;
		}
		if(! "diff".equals(param) && ! "up".equals(param)) {
			System.err.println("Unexpected mode: " + param + ". Mode parameter should be 'diff' for difference analysis mode or 'up' for update mode.");
			return;			
		}

		//id
		String id = cmd.getOptionValue("id");
		if(id == null) id = "id";

		//difference analysis case
		if("diff".equals(param)) {

			//v1
			System.out.println("Loading dataset version 1...");
			param = cmd.getOptionValue("v1");
			ArrayList<Feature> fs1 = null;
			try {
				fs1 = GeoData.getFeatures(param);
			} catch (Exception e) {
				System.err.println("Could not load data from " + param);
				System.err.println(e.getMessage());
				return;
			}
			System.out.println("   " + fs1.size() + " features loaded.");

			//v2
			System.out.println("Loading dataset version 2...");
			param = cmd.getOptionValue("v2");
			ArrayList<Feature> fs2 = null;
			try {
				fs2 = GeoData.getFeatures(param);
			} catch (Exception e) {
				System.err.println("Could not load data from " + param);
				System.err.println(e.getMessage());
				return;
			}
			System.out.println("   " + fs2.size() + " features loaded.");

			//res
			double resolution = -1;
			param = cmd.getOptionValue("res");
			if(param != null)
				try {
					resolution = Double.parseDouble(param);
				} catch (Exception e) {
					System.err.println("Failed reading parameter 'res'. The default value will be used.");
					System.err.println(e.getMessage());
				}

			//attributesToIgnore
			//TODO add test(s) for that
			String[] attributesToIgnore = null;
			param = cmd.getOptionValue("ati");
			if(param != null && !"".equals(param)) {
				attributesToIgnore = param.split(",");
				//check these attributes are indeed in the datasets?
			}

			//output folder
			String outFolder = cmd.getOptionValue("o");
			if(outFolder == null) outFolder = Paths.get("").toAbsolutePath().toString();

			/*boolean b = */
			new File(outFolder).mkdirs();
			//if(!b) System.err.println("Problem when creating output folder: " + outFolder);

			//output format
			String outputFileFormat = cmd.getOptionValue("of");
			if(outputFileFormat == null) outputFileFormat = "gpkg";
			if(!"geojson".equals(outputFileFormat) && !"gpkg".equals(outputFileFormat) && !"shp".equals(outputFileFormat)) {
				System.err.println("Unexpected output format: " + outputFileFormat + ". The default format will be used.");
				outputFileFormat = "gpkg";
			}

			System.out.println("Compute differences...");

			//check identifiers
			if(! fs1.iterator().next().getAttributes().keySet().contains(id)) {
				System.err.println("Could not find identifier attribute " + id + " in input dataset v1.");
				return;
			}
			if(! fs2.iterator().next().getAttributes().keySet().contains(id)) {
				System.err.println("Could not find identifier attribute " + id + " in input dataset v2.");
				return;
			}

			//set identifiers
			FeatureUtil.setId(fs1, id);
			FeatureUtil.setId(fs2, id);

			//build geoDiff object
			GeoDiff geoDiff = new GeoDiff(fs1, fs2, resolution);

			//set attributes to ignore
			//TODO check both versions have the same schema - if not, ignore different attributes
			if(attributesToIgnore != null)
				geoDiff.setAttributesToIgnore(attributesToIgnore);

			//crs
			CoordinateReferenceSystem crs = null;
			try {
				crs = GeoData.getCRS(cmd.getOptionValue("v1"));
			} catch (Exception e) {
				System.err.println("Could not retrieve CRS from " + cmd.getOptionValue("v1") + ". Use default CRS (EPSG:4326).");
				System.err.println(e.getMessage());
				crs = getCRSWGS84();
			}

			System.out.println(geoDiff.getDifferences().size() + " differences found.");
			System.out.println("Save...");

			//force multi geometry
			//TODO: obsolete?
			for(Feature f : geoDiff.getDifferences()) f.setGeometry( JTSGeomUtil.toMulti(f.getGeometry()) );
			for(Feature f : geoDiff.getGeomDifferences()) f.setGeometry( JTSGeomUtil.toMulti(f.getGeometry()) );

			GeoData.save(geoDiff.getDifferences(), outFolder + File.separator + "geodiff." + outputFileFormat, crs);
			GeoData.save(geoDiff.getHausdorffGeomDifferences(), outFolder + File.separator + "geomdiff1." + outputFileFormat, crs);
			GeoData.save(geoDiff.getGeomDifferences(), outFolder + File.separator + "geomdiff2." + outputFileFormat, crs);

			Collection<Feature> is = GeoDiff.findIdStabilityIssues(geoDiff.getDifferences(), resolution);
			//force multi geometry
			//TODO: obsolete?
			for(Feature f : is) f.setGeometry( JTSGeomUtil.toMulti(f.getGeometry()) );
			GeoData.save(is, outFolder + File.separator + "idstab." + outputFileFormat, crs);
		}

		//change application case
		if("up".equals(param)) {

			//dataset
			System.out.println("Loading dataset...");
			param = cmd.getOptionValue("d");
			ArrayList<Feature> fs = null;
			try {
				fs = GeoData.getFeatures(param);
			} catch (Exception e) {
				System.err.println("Could not load data from " + param);
				System.err.println(e.getMessage());
				return;
			}
			System.out.println("   " + fs.size() + " features loaded.");

			//get changes dataset
			System.out.println("Loading changes...");
			param = cmd.getOptionValue("c");
			ArrayList<Feature> changes = null;
			try {
				changes = GeoData.getFeatures(param);
			} catch (Exception e) {
				System.err.println("Could not load change data from " + param);
				System.err.println(e.getMessage());
				return;
			}
			System.out.println("   " + changes.size() + " changes loaded.");

			//check identifiers
			if(! fs.iterator().next().getAttributes().keySet().contains(id)) {
				System.err.println("Could not find identifier attribute " + id + " in input dataset.");
				return;
			}
			if(! changes.iterator().next().getAttributes().keySet().contains(id)) {
				System.err.println("Could not find identifier attribute " + id + " in input geodiff dataset.");
				return;
			}
			if(! changes.iterator().next().getAttributes().keySet().contains("ch_id")) {
				System.err.println("Could not find identifier attribute ch_id in input geodiff dataset.");
				return;
			}

			//set identifiers
			FeatureUtil.setId(fs, id);
			FeatureUtil.setId(changes, "ch_id");

			System.out.println("Apply changes...");
			GeoDiff.applyChanges(fs, changes);

			//output file
			String outputFile = cmd.getOptionValue("o");
			if(outputFile == null) outputFile = Paths.get("").toAbsolutePath().toString() + "/out.gpkg";

			//crs
			CoordinateReferenceSystem crs = null;
			try {
				crs = GeoData.getCRS(cmd.getOptionValue("d"));
			} catch (Exception e) {
				System.err.println("Could not retrieve CRS from " + cmd.getOptionValue("d") + ". Use default CRS (EPSG:4326).");
				System.err.println(e.getMessage());
				crs = getCRSWGS84();
			}

			System.out.println("Save...");
			GeoData.save(fs, outputFile, crs);
		}

	}

	private static CoordinateReferenceSystem CRSWGS84 = null;
	private static CoordinateReferenceSystem getCRSWGS84() {
		if(CRSWGS84 == null)
			try {
				CRSWGS84 = CRS.decode("EPSG:4326");
			} catch (Exception e) { e.printStackTrace(); }
		return CRSWGS84;
	}

}
