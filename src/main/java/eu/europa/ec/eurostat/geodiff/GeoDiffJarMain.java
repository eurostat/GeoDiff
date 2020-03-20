package eu.europa.ec.eurostat.geodiff;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.europa.ec.eurostat.jgiscotools.feature.Feature;
import eu.europa.ec.eurostat.jgiscotools.feature.FeatureUtil;
import eu.europa.ec.eurostat.jgiscotools.geodiff.DifferenceDetection;
import eu.europa.ec.eurostat.jgiscotools.io.GeoData;

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
		options.addOption(Option.builder("v1").longOpt("version1").desc("Input file containing the version 1 of the dataset. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());

		//options for difference analysis mode
		options.addOption(Option.builder("v2").longOpt("version2").desc("Input file containing the version 2 of the dataset. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("id").longOpt("identifier").desc("Optional. Name of the identifier field of the dataset. Default: 'id'.")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("res").longOpt("resolution").desc("Optional. The geometrical resolution of the dataset. Geometrical differences below this value will be ignored. Default: 0.")
				.hasArg().argName("value").build());
		options.addOption(Option.builder("o").longOpt("outputFolder").desc("Optional. Output folder.")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("of").longOpt("outputFormat").desc("Optional. Output format. The supported formats are GeoJSON ('geojson'), SHP ('shp') and GeoPackage ('gpkg'). Default: 'out.gpkg'.")
				.hasArg().argName("file path").build());

		//options for update mode
		options.addOption(Option.builder("c").longOpt("geoDiffFile").desc("The changes/updates to apply, in GeoDiff format. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension). Default: 'changes.gpkg'.")
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

		//v1
		System.out.println("Loading dataset version 1...");
		param = cmd.getOptionValue("v1");
		ArrayList<Feature> fs1 = null;
		try {
			fs1 = GeoData.getFeatures(param);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("   " + fs1.size() + " features loaded.");

		//crs
		CoordinateReferenceSystem crs = null;
		try {
			crs = GeoData.getCRS(param);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		//v2
		param = cmd.getOptionValue("v2");
		if(param != null) {
			//difference analysis case
			System.out.println("Loading dataset version 2...");
			ArrayList<Feature> fs2 = null;
			try {
				fs2 = GeoData.getFeatures(param);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println("   " + fs2.size() + " features loaded.");

			//TODO check both have the same schema
			//TODO add attributes to ignore as a parameter

			//id
			String id = cmd.getOptionValue("id");
			if(id == null) id = "id";

			//res
			double resolution = -1;
			param = cmd.getOptionValue("res");
			if(param != null)
				try {
					resolution = Double.parseDouble(param);
				} catch (Exception e) {
					System.err.println("Failed reading parameter 'res'. The default value will be used.");
					e.printStackTrace();
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

			//set identifiers
			FeatureUtil.setId(fs1, id);
			FeatureUtil.setId(fs2, id);

			//build geoDiff object
			DifferenceDetection geoDiff = new DifferenceDetection(fs1, fs2, resolution);

			try {
				System.out.println(geoDiff.getDifferences().size() + " differences found.");
				System.out.println("Save...");

				GeoData.save(geoDiff.getDifferences(), outFolder + File.separator + "geodiff." + outputFileFormat, crs);
				GeoData.save(geoDiff.getHausdorffGeomDifferences(), outFolder + File.separator + "geomdiff1." + outputFileFormat, crs);
				GeoData.save(geoDiff.getGeomDifferences(), outFolder + File.separator + "geomdiff2." + outputFileFormat, crs);
				GeoData.save(DifferenceDetection.findIdStabilityIssues(geoDiff.getDifferences(), resolution), outFolder + File.separator + "idstab." + outputFileFormat, crs);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			param = cmd.getOptionValue("c");
			//change application case

			if(param == null) {
				System.err.println("Missing parameter: -v2 or -c.");
				return;
			}

			//get changes dataset
			System.out.println("Loading changes...");
			param = cmd.getOptionValue("c");
			ArrayList<Feature> changes = null;
			try {
				changes = GeoData.getFeatures(param);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println("   " + changes.size() + " changes loaded.");

			System.out.println("Apply changes...");
			DifferenceDetection.applyChanges(fs1, changes);

			//output file
			String outputFile = cmd.getOptionValue("o");
			if(outputFile == null) outputFile = Paths.get("").toAbsolutePath().toString() + "/out.gpkg";

			System.out.println("Save...");
			try {
				GeoData.save(fs1, outputFile, crs);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
