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
import org.apache.commons.io.FilenameUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.europa.ec.eurostat.jgiscotools.changedetection.ChangeDetection;
import eu.europa.ec.eurostat.jgiscotools.feature.Feature;
import eu.europa.ec.eurostat.jgiscotools.feature.FeatureUtil;
import eu.europa.ec.eurostat.jgiscotools.io.GeoJSONUtil;
import eu.europa.ec.eurostat.jgiscotools.io.GeoPackageUtil;
import eu.europa.ec.eurostat.jgiscotools.io.SHPUtil;

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
		options.addOption(Option.builder("ini").longOpt("initialFile").desc("Input file containing the dataset in its initial state. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());

		//options for change analysis mode
		options.addOption(Option.builder("fin").longOpt("finalFile").desc("Input file containing the dataset in its final state. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("id").longOpt("identifier").desc("Optional. Name of the identifier field of the dataset. Default: 'id'.")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("res").longOpt("resolution").desc("Optional. The geometrical resolution of the dataset. Geometrical changes below this value will be ignored. Default: 0.")
				.hasArg().argName("value").build());
		options.addOption(Option.builder("o").longOpt("outputFolder").desc("Optional. Output folder.")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("of").longOpt("outputFormat").desc("Optional. Output format. The supported formats are GeoJSON ('geojson'), SHP ('shp') and GeoPackage ('gpkg'). Default: 'out.gpkg'.")
				.hasArg().argName("file path").build());

		//options for update mode
		options.addOption(Option.builder("d").longOpt("geoDiffFile").desc("The updates to apply to the initial version, in GeoDiff format. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension). Default: 'changes.gpkg'.")
				.hasArg().argName("file path").build());
		options.addOption(Option.builder("o").longOpt("outputFile").desc("Optional. Output file with changes applied. The supported formats are GeoJSON (*.geojson extension), SHP (*.shp extension) and GeoPackage (*.gpkg extension).")
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

		//ini
		System.out.println("Loading initial dataset...");
		param = cmd.getOptionValue("ini");
		ArrayList<Feature> fsIni = null;
		try {
			fsIni = getFeatures(param);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("   " + fsIni.size() + " features loaded.");

		//crs
		CoordinateReferenceSystem crs = null;
		try {
			crs = getCRS(param);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		//fin
		param = cmd.getOptionValue("fin");
		if(param != null) {
			//change analysis case
			System.out.println("Loading final dataset...");
			ArrayList<Feature> fsFin = null;
			try {
				fsFin = getFeatures(param);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println("   " + fsFin.size() + " features loaded.");

			//TODO check both have the same schema

			//TODO add attributes to ignore

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

			System.out.println("Compute changes...");

			//set identifiers
			FeatureUtil.setId(fsIni, id);
			FeatureUtil.setId(fsFin, id);

			//build geoDiff object
			ChangeDetection geoDiff = new ChangeDetection(fsIni, fsFin, resolution);

			//TODO: change geodiff attribute 'change' to 'GeoDiff'
			try {
				System.out.println(geoDiff.getChanges().size() + " changes found.");
				System.out.println("Save...");

				save(geoDiff.getChanges(), outFolder + File.separator + "geodiff." + outputFileFormat, crs);
				save(geoDiff.getHausdorffGeomChanges(), outFolder + File.separator + "geomdiff1." + outputFileFormat, crs);
				save(geoDiff.getGeomChanges(), outFolder + File.separator + "geomdiff2." + outputFileFormat, crs);
				save(ChangeDetection.findIdStabilityIssues(geoDiff.getChanges(), resolution), outFolder + File.separator + "idstab." + outputFileFormat, crs);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			param = cmd.getOptionValue("d");
			//change application case

			//TODO if param null, exit

			//get changes dataset
			System.out.println("Loading changes...");
			param = cmd.getOptionValue("c");
			ArrayList<Feature> changes = null;
			try {
				changes = getFeatures(param);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println("   " + fsIni.size() + " features loaded.");

			System.out.println("Apply changes...");
			ChangeDetection.applyChanges(fsIni, changes);

			//output file
			String outputFile = cmd.getOptionValue("o");
			if(outputFile == null) outputFile = Paths.get("").toAbsolutePath().toString() + "/out.gpkg";

			System.out.println("Save...");
			try {
				save(fsIni, outputFile, crs);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Get features from a data source parameter
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<Feature> getFeatures(String param) throws Exception {
		String inputFileFormat = FilenameUtils.getExtension(param).toLowerCase();
		switch(inputFileFormat) {
		case "shp":
			return SHPUtil.getFeatures(param);
		case "geojson":
			return GeoJSONUtil.load(param);
		case "gpkg":
			return GeoPackageUtil.getFeatures(param);
		default:
			throw new Exception("Could not retrieve features from data source: "+param);
		}
	}

	private static CoordinateReferenceSystem getCRS(String param) throws Exception {
		String inputFileFormat = FilenameUtils.getExtension(param).toLowerCase();
		switch(inputFileFormat) {
		case "shp":
			return SHPUtil.getCRS(param);
		case "geojson":
			//TODO
			return null;
		case "gpkg":
			return GeoPackageUtil.getCRS(param);
		default:
			throw new Exception("Could not retrieve CRS from data source: "+param);
		}
	}

	private static void save(Collection<Feature> fs, String filePath, CoordinateReferenceSystem crs) throws Exception {
		if(fs.size() == 0) return;
		String outputFileFormat = FilenameUtils.getExtension(filePath).toLowerCase();
		switch(outputFileFormat) {
		case "shp":
			SHPUtil.save(fs, filePath, crs);
			break;
		case "geojson":
			GeoJSONUtil.save(fs, filePath, crs);
			break;
		case "gpkg":
			GeoPackageUtil.save(fs, filePath, crs, true);
			break;
		default:
			throw new Exception("Unsuported output format: " + outputFileFormat);
		}
	}

}
