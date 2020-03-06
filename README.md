# GeoDiff

[GeoDiff](https://github.com/eurostat/GeoDiff) analyses changes between two versions of a vector geospatial dataset.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/ini.png" /></kbd>

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/fin.png" /></kbd>

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/changes.png" /></kbd>

## Quick start

1. Download [geodiff-1.0.zip](releases/geodiff-1.0.zip?raw=true) and unzip somewhere.
2. Run: `java -jar GeoDiff.jar -ini pathTo/dataset_initial.gpkg -fin pathTo/dataset_final.gpkg -id identCol -o out/` to analyse the changes between two datasets and store the analysis result in *out/* folder. *identCol* is the name of the identifier column in both datasets. You can alternativelly edit and execute *geodiff.bat* (or *geodiff.sh* for Linux users).

## Usage

### Requirements

Java 1.8 or higher is required. The java version installed, if any, can be found with `java --version` command. Recent versions of Java can be installed from [here](https://www.java.com/).

### Input parameters

The help is displayed with `java -jar GeoDiff.jar -h` command.

| Parameter | Required | Description | Default value |
| ------------- | ------------- |-------------| ------|
| -h | | Show the help message |  |
| -ini | * | Input file containing the dataset in its initial state. The supported formats are GeoJSON (\*.geojson extension), SHP (\*.shp extension) and GeoPackage (\*.gpkg extension). |  |
| -fin | * | Input file containing the dataset in its final state. The supported formats are GeoJSON (\*.geojson extension), SHP (\*.shp extension) and GeoPackage (\*.gpkg extension). |  |
| -id |  | Name of the identifier field of the dataset. | 'id' |
| -res |  | The geometrical resolution of the dataset. Geometrical changes below this value will be ignored. | 0 |
| -o |  | Output folder. | The current location of the program. |
| -of |  | Output format. The supported formats are GeoJSON ('geojson'), SHP ('shp') and GeoPackage ('gpkg') | 'gpkg' |

### Outputs

The program produces the following datasets:

- **changes** dataset containing the changes between both versions:
   - Features that have been **deleted** (attribute *change* set to *D*)
   - Features that have been **inserted** (attribute *change* set to *I*)
   - Features that have been **modified**,
      * Either their geometry (attribute *change* set to *G*)
      * or some attribute values (attribute *change* set to *An* where *n* is the number of modified attributes)
      * or both (attribute *change* set to *GAn*)

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/changes.png" /></kbd>

- **geomdiff1** dataset containing a set of linear features representing the [Hausdorf segments](https://en.wikipedia.org/wiki/Hausdorff_distance) between the two versions of the geometries. This segment represents the place where the geometrical change between the two versions is maximum. Its length is a good measure for the change magnitude.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/hausdorf_segment.png" width=100 /></kbd>

(Initial version in gray - Final version blue outline - Corresponding Hausdorf segment in purple)

- **geomdiff2** dataset containing features representing the spatial gains and losses between the two versions of the geometries. Gains are labeled with an attribute *change* set to *I*, and losses are labeled with *D* value.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/geomch.png" /></kbd>

(Geometry gains in green, losses in red)

- **idstab** dataset: The stability of the identifier between two versions of a feature might not be respected, by mistake. This leads to the detection of superfluous pairs (deletion, insertion) of the same feature, which do not reflect genuine changes of the dataset. In general, a pair (deletion, insertion) is not considered as pertinent when both feature versions are the same (or have very similar geometries), but their identifier has changed. This datasets contains the change features representing these superflous (deletion, insertion) pairs. Those pairs could be either removed if both feature versions are exactly the same, or replaced with a change if these versions are similar. The parameter *res* indicates the distance threshold to decide when the geometries are too similar to be considered as representing totally different entities.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/id_stab_issues.png" /></kbd>

(Detected stability issues in pink)

### For coders

Install [JGiscoTools](https://github.com/eurostat/JGiscoTools/) and see the instructions [here](https://github.com/eurostat/JGiscoTools/tree/master/src/site/changedetection).

## Support and contribution

Feel free to [ask support](https://github.com/eurostat/GeoDiff/issues/new), fork the project or simply star it (it's always a pleasure). The source code is currently stored as part of [JGiscoTools](https://github.com/eurostat/JGiscoTools) repository. It is mainly based on [GeoTools](http://www.geotools.org/) and [JTS Topology Suite](https://locationtech.github.io/jts/).
