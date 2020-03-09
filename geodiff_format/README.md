# GeoDiff format

These are the specifications of the GeoDiff format.

The GeoDiff format represents differences between versions of a vector geospatial dataset. GeoDiff data can be used to represent updates of a dataset. It is assumed that the dataset structure does not change between the two versions; only the content changes. The type of changes supported are thus:
- Insertion of features
- Deletion of features 
- Changes of features (geometries and/or attribute values)

## Structure

TODO

Same as the structure of the dataset, with additional attribute *change*.


- Features that have been **deleted** (attribute *change* set to *D*)
- Features that have been **inserted** (attribute *change* set to *I*)
- Features that have been **modified**,
   * Either their geometry (attribute *change* set to *G*)
   * or some attribute values (attribute *change* set to *An* where *n* is the number of modified attributes)
   * or both (attribute *change* set to *GAn*)

## Encodings

TODO

## Examples

TODO

## Implementations

TODO
