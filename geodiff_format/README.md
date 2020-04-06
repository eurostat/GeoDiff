# GeoDiff format specification

version: 0.9.1

The GeoDiff format represents differences between two versions of a vector geospatial dataset. GeoDiff data can expecially be used to store and share dataset updates. The type of changes supported are thus:
- Insertion of new features,
- Deletion of features,
- Modification of features (geometries and/or attribute values)

It is assumed that:
- the dataset structure does not change between the two versions; only the content changes.
- the dataset features have an identifier, which is stable between the versions. This means that features which do not change or change only slightly between both versions should keep the same identifier value.

## Structure

- A GeoDiff file describes only the features that have been *inserted*, *deleted* or *modified*. Unchanged features are not described.

- A GeoDiff file has the same structure as the dataset (geometry type, attribute names and types, identifier), with two additional attributes: **GeoDiff** and **ch_id**.

Each instance represents:
- Either an **inserted** feature. For this case, the geometry and attribute values must be specified. The *GeoDiff* attribute must be set to *I*. The *ch_id* attribute is set to the identifier of the inserted feature.
- Or a **deleted** feature. For this case, geometry and attribute values are not required. The *GeoDiff* attribute must be set to *D*. The *ch_id* attribute is set to the identifier of the deleted feature.
- Or a **Modified** feature. For this case, modified elements (geometry and/or attribute values) must be specified. The other unchanged elements are not required. The *ch_id* attribute is set to the identifier of the modified feature. The *GeoDiff* attribute must be set to:
   * *g* if the geometry only was modified,
   * *An* if attribute values only were modified (*n* is the number of modified attributes. *n* is optional) and not the geometry,
   * *GAn* if both the geometry and attribute values were modified.

## Encodings

GeoDiff format does not require any specific encodings.

Any vector geospatial format allowing to represent geographical features can be used. Possible encodings are: [GeoPackage](https://www.geopackage.org/), [Shapefile](https://en.wikipedia.org/wiki/Shapefile), [GeoJSON](https://geojson.org/).

## Example

<kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/geodiff/img/v1.png" width="200" /></kbd> **+** <kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/geodiff/img/v2.png" width="200" /></kbd> **=** <kbd><img src="https://raw.githubusercontent.com/eurostat/JGiscoTools/master/src/site/geodiff/img/geodiff.png" width="200" /></kbd>

## Implementations

- [Eurostat/GeoDiff](https://github.com/eurostat/GeoDiff)
- ...

## Contribution

For any suggestion, feel free to [start a discussion](https://github.com/eurostat/GeoDiff/issues/new) or edit this page.
