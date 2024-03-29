# GeoDiff

[GeoDiff](https://github.com/eurostat/GeoDiff) allows: 
- Extracting the differences between two versions of a vector geospatial dataset.
- Applying changes/updates to a vector geospatial dataset.

Both utilisation modes are based on the [GeoDiff format](https://eurostat.github.io/GeoDiff/geodiff_format/).

**Difference analysis mode**

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/v1.png" width="200" /></kbd> **+** <kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/v2.png" width="200" /></kbd> **=** <kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/geodiff.png" width="200" /></kbd>

**Update mode**

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/ini.png" width="200" /></kbd> **+** <kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/changes.png" width="200" /></kbd> **=** <kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/fin.png" width="200" /></kbd>

## Quick start

- Download and unzip [geodiff-2.3.zip](releases/geodiff-2.3.zip?raw=true).

- To compute the difference between two versions of a dataset, run: `java -jar GeoDiff.jar -m diff -v1 pathTo/dataset_v1.gpkg -v2 pathTo/dataset_v2.gpkg -id identCol -o out/`. The result is stored in a new *out/* folder. *identCol* is the name of the identifier column in both datasets.

- To update a dataset with [GeoDiff](https://eurostat.github.io/GeoDiff/geodiff_format/) data, run: `java -jar GeoDiff.jar -m up -d pathTo/dataset.gpkg -c pathTo/geodiff.gpkg`. The updated dataset is stored in a new *out.gpkg* file.

You can alternativelly edit and execute *geodiff.bat* (or *geodiff.sh* for Linux users).

## Requirements

Java 1.8 or higher is required. The java version installed, if any, can be found with `java --version` command. Recent versions of Java can be installed from [here](https://www.java.com/).

## Difference analysis mode

This mode analyses differences between two versions of a vector geospatial dataset. It produces a [GeoDiff](https://eurostat.github.io/GeoDiff/geodiff_format/) file representing the differences between both dataset versions and some auxilary data describing these differences.

### Input parameters

The help is displayed with `java -jar GeoDiff.jar -h` command.

| Parameter | Required | Description | Default value |
| ------------- | ------------- |-------------| ------|
| -h | | Show the help message |  |
| -m | x | Set to 'diff' for difference anaysis mode. |  |
| -v1 | x | First version of the dataset. The supported formats are GeoJSON (\*.geojson extension), SHP (\*.shp extension) and GeoPackage (\*.gpkg extension). |  |
| -v2 | x | Second version of the dataset. The supported formats are GeoJSON (\*.geojson extension), SHP (\*.shp extension) and GeoPackage (\*.gpkg extension). |  |
| -id |  | Name of the identifier field. | 'id' |
| -res |  | The geometrical resolution. Geometrical differences below this value will be ignored. | 0 |
| -ati |  | List of attributes to ignore for the comparison, comma separated. |  |
| -o |  | Output folder. | The current location of the program. |
| -of |  | Output format. The supported formats are GeoJSON ('geojson'), SHP ('shp') and GeoPackage ('gpkg') | 'gpkg' |

### Outputs

The program produces the following datasets:

- **geodiff** dataset containing the differences between both versions in [GeoDiff format](https://eurostat.github.io/GeoDiff/geodiff_format/).

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/geodiff.png" height="250" /></kbd>

- **geomdiff1** dataset containing a set of linear features representing the [Hausdorf segments](https://en.wikipedia.org/wiki/Hausdorff_distance) between the two versions of the geometries. This segment represents the place where the geometrical difference between the two versions is maximum. Its length is a good measure for the difference magnitude.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/hausdorf_segment.png" height="150" /></kbd>

(First version in gray - Second version blue outline - Corresponding Hausdorf segment in purple)

- **geomdiff2** dataset containing features representing the spatial gains and losses between the two versions of the geometries. Gains are labeled with an attribute *GeoDiff* set to *I*, and losses are labeled with *D* value.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/geomch.png" height="150" /></kbd>

(Geometry gains in green, losses in red)

- **idstab** dataset: The stability of the identifier between two versions of a feature might not be respected, by mistake. This leads to the detection of superfluous pairs (deletion, insertion) of the same feature, which do not reflect genuine differences of the dataset. In general, a pair (deletion, insertion) is not considered as pertinent when both feature versions are the same (or have very similar geometries), but their identifier is different. This datasets contains the difference features representing these superflous (deletion, insertion) pairs. Those pairs could either be removed if both feature versions are exactly the same, or replaced with a difference if these versions are similar. The parameter *res* indicates the distance threshold to decide when the geometries are too similar to be considered as representing totally different entities.

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/doc/geodiff/img/id_stab_issues.png" width="250" /></kbd>

(Detected stability issues in pink)


## Update mode

This mode applies updates to a vector geospatial dataset. The updates are specified in a [GeoDiff](https://eurostat.github.io/GeoDiff/geodiff_format/) file.

### Input parameters

The help is displayed with `java -jar GeoDiff.jar -h` command.

| Parameter | Required | Description | Default value |
| ------------- | ------------- |-------------| ------|
| -h | | Show the help message |  |
| -m | x | Set to 'up' for update mode. |  |
| -d | x | Dataset in its initial state. The supported formats are GeoJSON (\*.geojson extension), SHP (\*.shp extension) and GeoPackage (\*.gpkg extension). |  |
| -c | x | The changes/updates to apply to the dataset, in [GeoDiff format](https://eurostat.github.io/GeoDiff/geodiff_format/). The supported formats are GeoJSON (\*.geojson extension), SHP (\*.shp extension) and GeoPackage (\*.gpkg extension). |  |
| -id |  | Name of the identifier field. | 'id' |
| -o |  | Output updated dataset. The supported formats are GeoJSON (\*.geojson extension), SHP (\*.shp extension) and GeoPackage (\*.gpkg extension). | out.gpkg |

### Output

The output is the dataset updated with the specified updates.

## For coders

Install [JGiscoTools](https://github.com/eurostat/JGiscoTools/) and see the instructions [here](https://github.com/eurostat/JGiscoTools/tree/master/doc/geodiff).

## Support and contribution

Feel free to [ask support](https://github.com/eurostat/GeoDiff/issues/new), fork the project or simply star it (it's always a pleasure). The source code is currently stored as part of [JGiscoTools](https://github.com/eurostat/JGiscoTools) repository. It is mainly based on [GeoTools](http://www.geotools.org/) and [JTS Topology Suite](https://locationtech.github.io/jts/).
