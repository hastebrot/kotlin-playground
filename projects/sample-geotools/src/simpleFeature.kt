import org.geotools.data.collection.ListFeatureCollection
import org.geotools.feature.DefaultFeatureCollection
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.opengis.geometry.Geometry

fun main(args: Array<String>) {
    val featureType = SimpleFeatureTypeBuilder().apply {
        name = ""
        defaultGeometry = "geom"
        add("geom", Geometry::class.java)
        add("str", String::class.java)
        add("int", Int::class.java)
    }.buildFeatureType()

    val feature = SimpleFeatureBuilder(featureType).apply {
        set("geom", null)
        set("str", "foo")
        set("int", 42)
    }.buildFeature(null)

    val feature2 = SimpleFeatureBuilder.build(featureType, arrayOf(
        null, "foo", 42
    ), null)

    val featureCollection = ListFeatureCollection(featureType)
    featureCollection += feature
    featureCollection += feature2

    val defaultFeatureCollection = DefaultFeatureCollection("", featureType)
    defaultFeatureCollection += feature
    defaultFeatureCollection += feature2

    println(featureType)
    println(featureCollection.toList())
    println(defaultFeatureCollection.toList())
}
