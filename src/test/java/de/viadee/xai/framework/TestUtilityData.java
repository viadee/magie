package de.viadee.xai.framework;

import de.viadee.xai.framework.data.Feature;
import de.viadee.xai.framework.data.index.RoaringBitmapIndex;
import de.viadee.xai.framework.data.index.SimpleRoaringBitmapIndex;
import de.viadee.xai.framework.data.tabular_data.LabelColumn;
import de.viadee.xai.framework.data.tabular_data.TabularDataset;
import de.viadee.xai.framework.data.tabular_data.TabularDatasetPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data generator for tests.
 * Creates a data set, index, queries, and the expected covers.
 */
public class TestUtilityData {

    protected TabularDataset<? extends LabelColumn, LabelColumn.CategoricalLabelColumn> dataset;
    protected RoaringBitmapIndex roaringBitmapIndex;
    protected Map<Feature.CategoricalFeature, Set<Integer>>[] queries;
    Set<Integer> overallInstances;
    Set<Integer>[] expectedCovered;
    Set<Integer>[] expectedCorrectlyCovered;
    Set<Integer>[] expectedCorrectlyNotCovered;
    Set<Integer>[] expectedIncorrectlyCovered;
    Set<Integer>[] expectedIncorrectlyNotCovered;

    public void setUp() {
        // Twenty instances in total:
        overallInstances = new HashSet<>();
        addToSet(overallInstances, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19});

        /* Set up categorical features and integerized values */
        String[] values0 = new String[] {
                "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20"
        };
        String[] values1 = new String[] {
                "1", "1", "1", "1", "1",
                "1", "1", "1", "1", "1",
                "2", "2", "2", "2", "2",
                "2", "2", "2", "2", "2"
        };
        String[] values2 = new String[] {
                "1", "1", "1", "1", "1",
                "2", "2", "2", "2", "2",
                "3", "3", "3", "3", "3",
                "4", "4", "4", "4", "4"
        };
        String[] values3 = new String[] {
                "1", "1", "1", "1", "2",
                "2", "2", "2", "3", "3",
                "3", "3", "4", "4", "4",
                "4", "5", "5", "5", "5"
        };
        String[] values4 = new String[] {
                "1", "1", "2", "2", "3",
                "3", "4", "4", "5", "5",
                "6", "6", "7", "7", "8",
                "8", "9", "9", "0", "0"
        };

        // Each of the features and their values are manually created.
        Feature.CategoricalFeature categoricalFeature0 = new Feature.CategoricalFeature("feature0", values0);
        Feature.CategoricalFeature categoricalFeature1 = new Feature.CategoricalFeature("feature1", values1);
        Feature.CategoricalFeature categoricalFeature2 = new Feature.CategoricalFeature("feature2", values2);
        Feature.CategoricalFeature categoricalFeature3 = new Feature.CategoricalFeature("feature3", values3);
        Feature.CategoricalFeature categoricalFeature4 = new Feature.CategoricalFeature("feature4", values4);

        int[] integerizedValues0 = Feature.CategoricalFeature.getIntegerizedValues(categoricalFeature0, values0);
        int[] integerizedValues1 = Feature.CategoricalFeature.getIntegerizedValues(categoricalFeature1, values1);
        int[] integerizedValues2 = Feature.CategoricalFeature.getIntegerizedValues(categoricalFeature2, values2);
        int[] integerizedValues3 = Feature.CategoricalFeature.getIntegerizedValues(categoricalFeature3, values3);
        int[] integerizedValues4 = Feature.CategoricalFeature.getIntegerizedValues(categoricalFeature4, values4);

        Map<Feature.CategoricalFeature, int[]> categoricalFeatureMap = new HashMap<>();
        categoricalFeatureMap.put(categoricalFeature0, integerizedValues0);
        categoricalFeatureMap.put(categoricalFeature1, integerizedValues1);
        categoricalFeatureMap.put(categoricalFeature2, integerizedValues2);
        categoricalFeatureMap.put(categoricalFeature3, integerizedValues3);
        categoricalFeatureMap.put(categoricalFeature4, integerizedValues4);

        /* Set up label column */
        String[] labelValues = new String[] {
                "1", "2", "1", "2", "1",
                "2", "1", "2", "1", "2",
                "2", "2", "2", "2", "2",
                "2", "2", "2", "2", "2"
        };
        Feature.CategoricalFeature labelFeature = new Feature.CategoricalFeature("label", labelValues);
        LabelColumn.CategoricalLabelColumn labelColumn =
                new LabelColumn.CategoricalLabelColumn(
                        labelFeature,
                        Feature.CategoricalFeature.getIntegerizedValues(labelFeature, labelValues)
                );

        /* Set up overall dataset */
        TabularDatasetPackage<LabelColumn.CategoricalLabelColumn> data =
                new TabularDatasetPackage<>(
                        categoricalFeatureMap,
                        new HashMap<>(),
                        labelColumn
                );
        dataset = new TabularDataset<>(data, data);

        roaringBitmapIndex = new SimpleRoaringBitmapIndex(dataset);

        /* Set up queries */
        Map<Feature.CategoricalFeature, Set<Integer>> query0 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature0},
                        new int[][] {{0}}
                );
        Set<Integer> expectedCovered0 = new HashSet<>();
        expectedCovered0.add(0);
        Set<Integer> expectedCorrectlyCovered0 = new HashSet<>();
        expectedCorrectlyCovered0.add(0); // Label value of 0 will be assumed
        Set<Integer> expectedCorrectlyNotCovered0_0 = new HashSet<>();
        expectedCorrectlyNotCovered0_0.add(1);
        expectedCorrectlyNotCovered0_0.add(3);
        expectedCorrectlyNotCovered0_0.add(5);
        expectedCorrectlyNotCovered0_0.add(7);
        expectedCorrectlyNotCovered0_0.add(9);
        expectedCorrectlyNotCovered0_0.add(10);
        expectedCorrectlyNotCovered0_0.add(11);
        expectedCorrectlyNotCovered0_0.add(12);
        expectedCorrectlyNotCovered0_0.add(13);
        expectedCorrectlyNotCovered0_0.add(14);
        expectedCorrectlyNotCovered0_0.add(15);
        expectedCorrectlyNotCovered0_0.add(16);
        expectedCorrectlyNotCovered0_0.add(17);
        expectedCorrectlyNotCovered0_0.add(18);
        expectedCorrectlyNotCovered0_0.add(19);
        Set<Integer> expectedCorrectlyNotCovered0_1 = new HashSet<>();
        expectedCorrectlyNotCovered0_1.add(2);
        expectedCorrectlyNotCovered0_1.add(4);
        expectedCorrectlyNotCovered0_1.add(6);
        expectedCorrectlyNotCovered0_1.add(8);
        Set<Integer> expectedIncorrectlyNotCovered0_0 = new HashSet<>();
        expectedIncorrectlyNotCovered0_0.add(2);
        expectedIncorrectlyNotCovered0_0.add(4);
        expectedIncorrectlyNotCovered0_0.add(6);
        expectedIncorrectlyNotCovered0_0.add(8);
        Set<Integer> expectedIncorrectlyNotCovered0_1 = new HashSet<>();
        expectedIncorrectlyNotCovered0_1.add(1);
        expectedIncorrectlyNotCovered0_1.add(3);
        expectedIncorrectlyNotCovered0_1.add(5);
        expectedIncorrectlyNotCovered0_1.add(7);
        expectedIncorrectlyNotCovered0_1.add(9);
        expectedIncorrectlyNotCovered0_1.add(10);
        expectedIncorrectlyNotCovered0_1.add(11);
        expectedIncorrectlyNotCovered0_1.add(12);
        expectedIncorrectlyNotCovered0_1.add(13);
        expectedIncorrectlyNotCovered0_1.add(14);
        expectedIncorrectlyNotCovered0_1.add(15);
        expectedIncorrectlyNotCovered0_1.add(16);
        expectedIncorrectlyNotCovered0_1.add(17);
        expectedIncorrectlyNotCovered0_1.add(18);
        expectedIncorrectlyNotCovered0_1.add(19);

        Map<Feature.CategoricalFeature, Set<Integer>> query1 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature0},
                        new int[][] {{0, 1, 2}}
                );
        Set<Integer> expectedCovered1 = new HashSet<>();
        expectedCovered1.add(0);
        expectedCovered1.add(1);
        expectedCovered1.add(2);
        Set<Integer> expectedCorrectlyCovered1 = new HashSet<>();
        expectedCorrectlyCovered1.add(0);
        expectedCorrectlyCovered1.add(2); // Label value of 0 will be assumed.

        Map<Feature.CategoricalFeature, Set<Integer>> query2 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature0},
                        new int[][] {{}}
                );
        Set<Integer> expectedCovered2 = new HashSet<>();
        Set<Integer> expectedCorrectlyCovered2 = new HashSet<>();

        Map<Feature.CategoricalFeature, Set<Integer>> query3 =
                createQuery(
                        new Feature.CategoricalFeature[] {},
                        new int[][] {{}}
                );
        Set<Integer> expectedCovered3 = new HashSet<>();
        Set<Integer> expectedCorrectlyCovered3 = new HashSet<>();
        Set<Integer> expectedIncorrectlyNotCovered3_0 = new HashSet<>();
        expectedIncorrectlyNotCovered3_0.add(0);
        expectedIncorrectlyNotCovered3_0.add(2);
        expectedIncorrectlyNotCovered3_0.add(4);
        expectedIncorrectlyNotCovered3_0.add(6);
        expectedIncorrectlyNotCovered3_0.add(8);
        Set<Integer> expectedIncorrectlyNotCovered3_1 = new HashSet<>();
        expectedIncorrectlyNotCovered3_1.add(1);
        expectedIncorrectlyNotCovered3_1.add(3);
        expectedIncorrectlyNotCovered3_1.add(5);
        expectedIncorrectlyNotCovered3_1.add(7);
        expectedIncorrectlyNotCovered3_1.add(9);
        expectedIncorrectlyNotCovered3_1.add(10);
        expectedIncorrectlyNotCovered3_1.add(11);
        expectedIncorrectlyNotCovered3_1.add(12);
        expectedIncorrectlyNotCovered3_1.add(13);
        expectedIncorrectlyNotCovered3_1.add(14);
        expectedIncorrectlyNotCovered3_1.add(15);
        expectedIncorrectlyNotCovered3_1.add(16);
        expectedIncorrectlyNotCovered3_1.add(17);
        expectedIncorrectlyNotCovered3_1.add(18);
        expectedIncorrectlyNotCovered3_1.add(19);


        Map<Feature.CategoricalFeature, Set<Integer>> query4 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1},
                        new int[][] {{0}}
                );
        Set<Integer> expectedCovered4 = new HashSet<>();
        expectedCovered4.add(0);
        expectedCovered4.add(1);
        expectedCovered4.add(2);
        expectedCovered4.add(3);
        expectedCovered4.add(4);
        expectedCovered4.add(5);
        expectedCovered4.add(6);
        expectedCovered4.add(7);
        expectedCovered4.add(8);
        expectedCovered4.add(9);
        Set<Integer> expectedCorrectlyCovered4_0 = new HashSet<>(); // Label value of 0 will be assumed.
        expectedCorrectlyCovered4_0.add(0);
        expectedCorrectlyCovered4_0.add(2);
        expectedCorrectlyCovered4_0.add(4);
        expectedCorrectlyCovered4_0.add(6);
        expectedCorrectlyCovered4_0.add(8);
        Set<Integer> expectedCorrectlyCovered4_1 = new HashSet<>(); // Label value of 1 will be assumed.
        expectedCorrectlyCovered4_1.add(1);
        expectedCorrectlyCovered4_1.add(3);
        expectedCorrectlyCovered4_1.add(5);
        expectedCorrectlyCovered4_1.add(7);
        expectedCorrectlyCovered4_1.add(9);
        Set<Integer> expectedIncorrectlyCovered4_0 = expectedCorrectlyCovered4_1;
        Set<Integer> expectedIncorrectlyCovered4_1 = expectedCorrectlyCovered4_0;
        Set<Integer> expectedCorrectlyNotCovered4_0 = new HashSet<>();
        expectedCorrectlyNotCovered4_0.add(10);
        expectedCorrectlyNotCovered4_0.add(11);
        expectedCorrectlyNotCovered4_0.add(12);
        expectedCorrectlyNotCovered4_0.add(13);
        expectedCorrectlyNotCovered4_0.add(14);
        expectedCorrectlyNotCovered4_0.add(15);
        expectedCorrectlyNotCovered4_0.add(16);
        expectedCorrectlyNotCovered4_0.add(17);
        expectedCorrectlyNotCovered4_0.add(18);
        expectedCorrectlyNotCovered4_0.add(19);
        Set<Integer> expectedCorrectlyNotCovered4_1 = new HashSet<>();

        Map<Feature.CategoricalFeature, Set<Integer>> query5 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1},
                        new int[][] {{0, 1}}
                );
        Set<Integer> expectedCovered5 = new HashSet<>();
        expectedCovered5.add(0);
        expectedCovered5.add(1);
        expectedCovered5.add(2);
        expectedCovered5.add(3);
        expectedCovered5.add(4);
        expectedCovered5.add(5);
        expectedCovered5.add(6);
        expectedCovered5.add(7);
        expectedCovered5.add(8);
        expectedCovered5.add(9);
        expectedCovered5.add(10);
        expectedCovered5.add(11);
        expectedCovered5.add(12);
        expectedCovered5.add(13);
        expectedCovered5.add(14);
        expectedCovered5.add(15);
        expectedCovered5.add(16);
        expectedCovered5.add(17);
        expectedCovered5.add(18);
        expectedCovered5.add(19);
        Set<Integer> expectedCorrectlyCovered5_0 = new HashSet<>(); // Label value of 0 will be assumed.
        expectedCorrectlyCovered5_0.add(0);
        expectedCorrectlyCovered5_0.add(2);
        expectedCorrectlyCovered5_0.add(4);
        expectedCorrectlyCovered5_0.add(6);
        expectedCorrectlyCovered5_0.add(8);
        Set<Integer> expectedCorrectlyCovered5_1 = new HashSet<>(); // Label value of 1 will be assumed.
        Set<Integer> expectedIncorrectlyCovered5_0 = expectedCorrectlyCovered5_1;
        Set<Integer> expectedIncorrectlyCovered5_1 = expectedCorrectlyCovered5_0;
        expectedCorrectlyCovered5_1.add(1);
        expectedCorrectlyCovered5_1.add(3);
        expectedCorrectlyCovered5_1.add(5);
        expectedCorrectlyCovered5_1.add(7);
        expectedCorrectlyCovered5_1.add(9);
        expectedCorrectlyCovered5_1.add(10);
        expectedCorrectlyCovered5_1.add(11);
        expectedCorrectlyCovered5_1.add(12);
        expectedCorrectlyCovered5_1.add(13);
        expectedCorrectlyCovered5_1.add(14);
        expectedCorrectlyCovered5_1.add(15);
        expectedCorrectlyCovered5_1.add(16);
        expectedCorrectlyCovered5_1.add(17);
        expectedCorrectlyCovered5_1.add(18);
        expectedCorrectlyCovered5_1.add(19);
        Set<Integer> expectedCorrectlyNotCovered5_01 = new HashSet<>();
        Set<Integer> expectedIncorrectlyNotCovered5_01 = new HashSet<>();

        Map<Feature.CategoricalFeature, Set<Integer>> query6 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature2},
                        new int[][] {{0}, {0}}
                );
        Set<Integer> expectedCovered6 = new HashSet<>();
        expectedCovered6.add(0);
        expectedCovered6.add(1);
        expectedCovered6.add(2);
        expectedCovered6.add(3);
        expectedCovered6.add(4);
        Set<Integer> expectedCorrectlyNotCovered6_0 = new HashSet<>();
        expectedCorrectlyNotCovered6_0.add(5);
        expectedCorrectlyNotCovered6_0.add(7);
        expectedCorrectlyNotCovered6_0.add(9);
        expectedCorrectlyNotCovered6_0.add(10);
        expectedCorrectlyNotCovered6_0.add(11);
        expectedCorrectlyNotCovered6_0.add(12);
        expectedCorrectlyNotCovered6_0.add(13);
        expectedCorrectlyNotCovered6_0.add(14);
        expectedCorrectlyNotCovered6_0.add(15);
        expectedCorrectlyNotCovered6_0.add(16);
        expectedCorrectlyNotCovered6_0.add(17);
        expectedCorrectlyNotCovered6_0.add(18);
        expectedCorrectlyNotCovered6_0.add(19);
        Set<Integer> expectedCorrectlyNotCovered6_1 = new HashSet<>();
        expectedCorrectlyNotCovered6_1.add(6);
        expectedCorrectlyNotCovered6_1.add(8);
        Set<Integer> expectedIncorrectlyNotCovered6_0 = expectedCorrectlyNotCovered6_1;
        Set<Integer> expectedIncorrectlyNotCovered6_1 = expectedCorrectlyNotCovered6_0;



        Map<Feature.CategoricalFeature, Set<Integer>> query7 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature2},
                        new int[][] {{0}, {0, 1}}
                );
        Set<Integer> expectedCovered7 = expectedCovered4;
        Set<Integer> expectedCorrectlyCovered7_0 = expectedCorrectlyCovered4_0; // Label value of 0 will be assumed.
        expectedCorrectlyCovered7_0.add(0);
        expectedCorrectlyCovered7_0.add(2);
        expectedCorrectlyCovered7_0.add(4);
        expectedCorrectlyCovered7_0.add(6);
        expectedCorrectlyCovered7_0.add(8);
        Set<Integer> expectedCorrectlyCovered7_1 = expectedCorrectlyCovered4_1; // Label value of 1 will be assumed

        Map<Feature.CategoricalFeature, Set<Integer>> query8 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature4},
                        new int[][] {{1}, {0, 1, 2, 3, 4}}
                );
        Set<Integer> expectedCovered8 = new HashSet<>();
        Set<Integer> expectedCorrectlyCovered8 = new HashSet<>();


        Map<Feature.CategoricalFeature, Set<Integer>> query9 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature4},
                        new int[][] {{1}, {0, 1, 2, 3, 4, 5}}
                );
        Set<Integer> expectedCovered9 = new HashSet<>();
        expectedCovered9.add(10);
        expectedCovered9.add(11);
        Set<Integer> expectedCorrectlyCovered9_0 = new HashSet<>();
        Set<Integer> expectedCorrectlyCovered9_1 = new HashSet<>();
        expectedCorrectlyCovered9_1.add(10);
        expectedCorrectlyCovered9_1.add(11);
        Set<Integer> expectedIncorrectlyCovered9_0 = expectedCorrectlyCovered9_1;
        Set<Integer> expectedIncorrectlyCovered9_1 = expectedCorrectlyCovered9_0;
        Set<Integer> expectedIncorrectlyNotCovered9_0 = new HashSet<>();
        expectedIncorrectlyNotCovered9_0.add(0);
        expectedIncorrectlyNotCovered9_0.add(2);
        expectedIncorrectlyNotCovered9_0.add(4);
        expectedIncorrectlyNotCovered9_0.add(6);
        expectedIncorrectlyNotCovered9_0.add(8);
        Set<Integer> expectedIncorrectlyNotCovered9_1 = new HashSet<>();
        expectedIncorrectlyNotCovered9_1.add(1);
        expectedIncorrectlyNotCovered9_1.add(3);
        expectedIncorrectlyNotCovered9_1.add(5);
        expectedIncorrectlyNotCovered9_1.add(7);
        expectedIncorrectlyNotCovered9_1.add(9);
        expectedIncorrectlyNotCovered9_1.add(12);
        expectedIncorrectlyNotCovered9_1.add(13);
        expectedIncorrectlyNotCovered9_1.add(14);
        expectedIncorrectlyNotCovered9_1.add(15);
        expectedIncorrectlyNotCovered9_1.add(16);
        expectedIncorrectlyNotCovered9_1.add(17);
        expectedIncorrectlyNotCovered9_1.add(18);
        expectedIncorrectlyNotCovered9_1.add(19);

        Map<Feature.CategoricalFeature, Set<Integer>> query10 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature4},
                        new int[][] {{1}, {6, 7}}
                );
        Set<Integer> expectedCovered10 = new HashSet<>();
        expectedCovered10.add(12);
        expectedCovered10.add(13);
        expectedCovered10.add(14);
        expectedCovered10.add(15);
        Set<Integer> expectedCorrectlyCovered10_0 = new HashSet<>();
        Set<Integer> expectedCorrectlyCovered10_1 = new HashSet<>();
        expectedCorrectlyCovered10_1.add(12);
        expectedCorrectlyCovered10_1.add(13);
        expectedCorrectlyCovered10_1.add(14);
        expectedCorrectlyCovered10_1.add(15);

        Map<Feature.CategoricalFeature, Set<Integer>> query11 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature4},
                        new int[][] {{1}, {7, 9}}
                );
        Set<Integer> expectedCovered11 = new HashSet<>();
        expectedCovered11.add(14);
        expectedCovered11.add(15);
        expectedCovered11.add(18);
        expectedCovered11.add(19);
        Set<Integer> expectedCorrectlyCovered11_0 = new HashSet<>();
        Set<Integer> expectedIncorrectlyCovered11_1 = expectedCorrectlyCovered11_0;

        Map<Feature.CategoricalFeature, Set<Integer>> query12 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature2, categoricalFeature3},
                        new int[][] {{0, 1}, {2}, {3}}
                );
        Set<Integer> expectedCovered12 = new HashSet<>();
        expectedCovered12.add(12);
        expectedCovered12.add(13);
        expectedCovered12.add(14);
        Set<Integer> expectedCorrectlyCovered12_0 = new HashSet<>();
        Set<Integer> expectedCorrectlyCovered12_1 = new HashSet<>();
        expectedCorrectlyCovered12_1.add(12);
        expectedCorrectlyCovered12_1.add(13);
        expectedCorrectlyCovered12_1.add(14);


        Map<Feature.CategoricalFeature, Set<Integer>> query13 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature2, categoricalFeature3},
                        new int[][] {{0, 1}, {2}, {2}}
                );
        Set<Integer> expectedCovered13 = new HashSet<>();
        expectedCovered13.add(10);
        expectedCovered13.add(11);
        Set<Integer> expectedCorrectlyCovered13 = new HashSet<>();
        Set<Integer> expectedIncorrectlyNotCovered13_0 = new HashSet<>();
        expectedIncorrectlyNotCovered13_0.add(0);
        expectedIncorrectlyNotCovered13_0.add(2);
        expectedIncorrectlyNotCovered13_0.add(4);
        expectedIncorrectlyNotCovered13_0.add(6);
        expectedIncorrectlyNotCovered13_0.add(8);
        Set<Integer> expectedIncorrectlyNotCovered13_1 = new HashSet<>();
        expectedIncorrectlyNotCovered13_1.add(1);
        expectedIncorrectlyNotCovered13_1.add(3);
        expectedIncorrectlyNotCovered13_1.add(5);
        expectedIncorrectlyNotCovered13_1.add(7);
        expectedIncorrectlyNotCovered13_1.add(9);
        expectedIncorrectlyNotCovered13_1.add(12);
        expectedIncorrectlyNotCovered13_1.add(13);
        expectedIncorrectlyNotCovered13_1.add(14);
        expectedIncorrectlyNotCovered13_1.add(15);
        expectedIncorrectlyNotCovered13_1.add(16);
        expectedIncorrectlyNotCovered13_1.add(17);
        expectedIncorrectlyNotCovered13_1.add(18);
        expectedIncorrectlyNotCovered13_1.add(19);


        Map<Feature.CategoricalFeature, Set<Integer>> query14 =
                createQuery(
                        new Feature.CategoricalFeature[] {categoricalFeature1, categoricalFeature2, categoricalFeature3},
                        new int[][] {{0, 1}, {2}, {2, 3}}
                );
        Set<Integer> expectedCovered14 = new HashSet<>();
        expectedCovered14.add(10);
        expectedCovered14.add(11);
        expectedCovered14.add(12);
        expectedCovered14.add(13);
        expectedCovered14.add(14);
        Set<Integer> expectedCorrectlyCovered14_0 = new HashSet<>();
        Set<Integer> expectedCorrectlyCovered14_1 = new HashSet<>();
        expectedCorrectlyCovered14_1.add(10);
        expectedCorrectlyCovered14_1.add(11);
        expectedCorrectlyCovered14_1.add(12);
        expectedCorrectlyCovered14_1.add(13);
        expectedCorrectlyCovered14_1.add(14);
        Set<Integer> expectedIncorrectlyCovered14_0 = expectedCorrectlyCovered14_1;
        Set<Integer> expectedIncorrectlyCovered14_1 = expectedCorrectlyCovered14_0;
        Set<Integer> expectedCorrectlyNotCovered14_0 = new HashSet<>();
        expectedCorrectlyNotCovered14_0.add(1);
        expectedCorrectlyNotCovered14_0.add(3);
        expectedCorrectlyNotCovered14_0.add(5);
        expectedCorrectlyNotCovered14_0.add(7);
        expectedCorrectlyNotCovered14_0.add(9);
        expectedCorrectlyNotCovered14_0.add(15);
        expectedCorrectlyNotCovered14_0.add(16);
        expectedCorrectlyNotCovered14_0.add(17);
        expectedCorrectlyNotCovered14_0.add(18);
        expectedCorrectlyNotCovered14_0.add(19);
        Set<Integer> expectedCorrectlyNotCovered14_1 = new HashSet<>();
        expectedCorrectlyNotCovered14_1.add(0);
        expectedCorrectlyNotCovered14_1.add(2);
        expectedCorrectlyNotCovered14_1.add(4);
        expectedCorrectlyNotCovered14_1.add(6);
        expectedCorrectlyNotCovered14_1.add(8);

        queries = new Map[] {
                query0, query1, query2, query3, query4, query5,
                query6, query7, query8, query9, query10, query11,
                query12, query13, query14
        };

        expectedCovered = new Set[] {
                expectedCovered0, expectedCovered1, expectedCovered2, expectedCovered3, expectedCovered4,
                expectedCovered5, expectedCovered6, expectedCovered7, expectedCovered8, expectedCovered9,
                expectedCovered10, expectedCovered11, expectedCovered12, expectedCovered13
        };

        expectedCorrectlyCovered = new Set[] {
                expectedCorrectlyCovered0, expectedCorrectlyCovered1, expectedCorrectlyCovered2,
                expectedCorrectlyCovered3, expectedCorrectlyCovered4_0, expectedCorrectlyCovered4_1,
                expectedCorrectlyCovered5_0, expectedCorrectlyCovered5_1, expectedCorrectlyCovered7_0,
                expectedCorrectlyCovered7_1, expectedCorrectlyCovered8, expectedCorrectlyCovered9_0,
                expectedCorrectlyCovered9_1, expectedCorrectlyCovered10_0, expectedCorrectlyCovered10_1,
                expectedCorrectlyCovered11_0, expectedCorrectlyCovered12_0, expectedCorrectlyCovered12_1,
                expectedCorrectlyCovered13, expectedCorrectlyCovered14_0, expectedCorrectlyCovered14_1
        };

        expectedIncorrectlyCovered = new Set[] {
                expectedIncorrectlyCovered4_0, expectedIncorrectlyCovered4_1, expectedIncorrectlyCovered5_0,
                expectedIncorrectlyCovered5_1, expectedIncorrectlyCovered9_0, expectedIncorrectlyCovered9_1,
                expectedIncorrectlyCovered11_1, expectedIncorrectlyCovered14_0, expectedIncorrectlyCovered14_1
        };

        expectedCorrectlyNotCovered = new Set[] {
                expectedCorrectlyNotCovered0_0, expectedCorrectlyNotCovered0_1, expectedCorrectlyNotCovered4_0,
                expectedCorrectlyNotCovered4_1, expectedCorrectlyNotCovered5_01, expectedCorrectlyNotCovered6_0,
                expectedCorrectlyNotCovered6_1, expectedCorrectlyNotCovered14_0, expectedCorrectlyNotCovered14_1
        };

        expectedIncorrectlyNotCovered = new Set[] {
                expectedIncorrectlyNotCovered0_0, expectedIncorrectlyNotCovered0_1, expectedIncorrectlyNotCovered3_0,
                expectedIncorrectlyNotCovered3_1, expectedIncorrectlyNotCovered5_01,
                expectedIncorrectlyNotCovered6_0, expectedIncorrectlyNotCovered6_1, expectedIncorrectlyNotCovered9_0,
                expectedIncorrectlyNotCovered9_1, expectedIncorrectlyNotCovered13_0, expectedIncorrectlyNotCovered13_1
        };
    }

    private Map<Feature.CategoricalFeature, Set<Integer>> createQuery(Feature.CategoricalFeature[] categoricalFeatures,
                                                                      int[][] queryValues) {
        Map<Feature.CategoricalFeature, Set<Integer>> query = new HashMap<>();
        int count = 0;
        for (Feature.CategoricalFeature cf : categoricalFeatures) {
            Set<Integer> queryValuesForCF = new HashSet<>();
            for (int i = 0; i < queryValues[count].length; i++) {
                queryValuesForCF.add(queryValues[count][i]);
            }
            query.put(cf, queryValuesForCF);
            count++;
        }
        return query;
    }

    protected void addToSet(Set<Integer> set, int[] values) {
        for (int i : values) {
            set.add(i);
        }
    }

    public TabularDataset<? extends LabelColumn, LabelColumn.CategoricalLabelColumn> getDataset() {
        return dataset;
    }

    public RoaringBitmapIndex getRoaringBitmapIndex() {
        return roaringBitmapIndex;
    }

    public Map<Feature.CategoricalFeature, Set<Integer>>[] getQueries() {
        return queries;
    }

    public Set<Integer> getOverallInstances() {
        return overallInstances;
    }

    public Set<Integer>[] getExpectedCovered() {
        return expectedCovered;
    }

    public Set<Integer>[] getExpectedCorrectlyCovered() {
        return expectedCorrectlyCovered;
    }

    public Set<Integer>[] getExpectedCorrectlyNotCovered() {
        return expectedCorrectlyNotCovered;
    }

    public Set<Integer>[] getExpectedIncorrectlyCovered() {
        return expectedIncorrectlyCovered;
    }

    public Set<Integer>[] getExpectedIncorrectlyNotCovered() {
        return expectedIncorrectlyNotCovered;
    }
}
