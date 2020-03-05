# GeoDiff

[GeoDiff](https://github.com/eurostat/GeoDiff) analyses changes between two versions of a vector geospatial dataset.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/ini.png" /></kbd>

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/fin.png" /></kbd>

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/changedetection/img/changes.png" /></kbd>

## Quick start

1. Download [geodiff-1.0.zip](releases/geodiff-1.0.zip?raw=true) and unzip somewhere.
2. Run: `java -jar GeoDiff.jar -ini pathTo/dataset_initial.gpkg -fin pathTo/dataset_final.gpkg -o out/` to analyse changes between two datasets and store the analysis result in *out/* folder. You can alternativelly edit and execute *geodiff.bat* (or *geodiff.sh* for Linux users).

(describe outputs with images)

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

### For coders

Install [JGiscoTools](https://github.com/eurostat/JGiscoTools/) and see the instructions [here](https://github.com/eurostat/JGiscoTools/tree/master/src/site/changedetection).

## Support and contribution

Feel free to [ask support](https://github.com/eurostat/GeoDiff/issues/new), fork the project or simply star it (it's always a pleasure). The source code is currently stored as part of [JGiscoTools](https://github.com/eurostat/JGiscoTools) repository. It is mainly based on [GeoTools](http://www.geotools.org/) and [JTS Topology Suite](https://locationtech.github.io/jts/).
