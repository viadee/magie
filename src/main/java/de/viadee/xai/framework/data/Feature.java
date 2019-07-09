package de.viadee.xai.framework.data;

import de.viadee.xai.framework.exception.RepresentationNotFound;
import de.viadee.xai.framework.utility.Utility;

import java.util.*;

/**
 * Abstract superclass of all features. A feature represents the datatype of one column in a dataset.
 */
public abstract class Feature {

    protected String name;

    private Feature(final String name) {
        this.name = name;
    }

    /**
     * Returns the name of the feature.
     * @return The name of the feature.
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object feature) {
        if (!(feature instanceof Feature)) {
            return false;
        } else {
            return this.getClass().equals(feature.getClass()) &&
                    this.name.equals(((Feature) feature).name);
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Class representing numeric features. A numeric feature holds data of the type double.
     */
    public static class NumericFeature extends Feature {
        private double minValue;
        private double maxValue;

        /**
         * Constructor for a NumericFeature instance.
         * @param name The name of the NumericFeature.
         * @param minValue The minimum value encountered while creating the NumericFeature.
         * @param maxValue The maximum value encountered while creating the NumericFeature.
         */
        public NumericFeature(final String name,
                              final double minValue,
                              final double maxValue) {
            super(name);
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public String toString() {
            return "NumericFeature{" +
                    "name=" + name +
                    ", minValue=" + minValue +
                    ", maxValue=" + maxValue +
                    "}";
        }
    }

    /**
     * Class representing categorical features. A categorical feature holds data of the type String which is mapped
     * to the working-representation of Integer.
     */
    public static class CategoricalFeature extends Feature {
        protected Map<String, Integer> nameToValue;
        protected Map<Integer, String> valueToName;

        protected CategoricalFeature(final String name) {
            super(name);
            nameToValue = new HashMap<>();
            valueToName = new HashMap<>();
        }

        /**
         * Constructor for a CategoricalFeature instance.
         * @param name The name of the CategoricalFeature.
         * @param values The values of the categorical feature (e.g. "male" for the Sex-attribute).
         */
        public CategoricalFeature(final String name,
                                  final String[] values) {
            super(name);
            this.nameToValue = getStringToIntegerMapping(values);

            this.valueToName = new HashMap<>();
            generateValueToName();
        }

        protected CategoricalFeature(String name,
                                     Map<String, Integer> nameToValue) {
            super(name);
            this.nameToValue = new HashMap<>(nameToValue);
            this.valueToName = new HashMap<>();
            generateValueToName();
        }

        /**
         * Constructor for a CategoricalFeature instance. Is used in the case that a categorical label-column is predicted
         * by a blackbox model, to adjust to the new output integer-values.
         * @param copyFrom The CategoricalFeature which's structure is copied.
         * @param newValues The new values of the CategoricalFeature.
         */
        public CategoricalFeature(final CategoricalFeature copyFrom,
                                  final int[] newValues) {
            super(copyFrom.name);
            nameToValue = new HashMap<>();
            valueToName = new HashMap<>();

            for (int i = 0; i < newValues.length; i++) {
                int newValue = newValues[i];
                String name = valueToName.get(newValue);
                if (name == null) {
                    // Check for BBM introducing new values
                    String containedName = copyFrom.getStringRepresentation(newValue);
                    nameToValue.put(containedName, newValue);
                    valueToName.put(newValue, containedName);
                }
            }
        }

        protected static Map<String, Integer> getStringToIntegerMapping(String[] values) {
            Map<String, Integer> nameToValue = new HashMap<>();
            for (int i = 0; i < values.length; i++) {
                if (nameToValue.get(values[i]) == null) {
                    nameToValue.put(values[i], nameToValue.size());
                }
            }
            return nameToValue;
        }

        public static int[] getIntegerizedValues(CategoricalFeature categoricalFeature, String[] values) {
            int[] intergerizedValues = new int[values.length];
            for (int i = 0; i < intergerizedValues.length; i++) {
                intergerizedValues[i] = categoricalFeature.getNumberRepresentation(values[i]);
            }
            return intergerizedValues;
        }

        protected void generateValueToName() {
            for (Map.Entry<String, Integer> entry : nameToValue.entrySet()) {
                valueToName.put(entry.getValue(), entry.getKey());
            }
        }

        /**
         * For the Integer working-representation, get the categorical feature value.
         * @param numberRepresentation The Integer working-representation.
         * @return The String representing the actual categorical feature value.
         */
        public String getStringRepresentation(final Integer numberRepresentation) {
            String name;
            if ((name = valueToName.get(numberRepresentation)) != null) {
                return name;
            } else {
                throw new RepresentationNotFound(this, numberRepresentation);
            }
        }

        /**
         * For the String representing the actual categorical feature value, get the Integer working-representation.
         * @param originalValue The String representing the actual categorical feature value.
         * @return The Integer working-representation.
         */
        public Integer getNumberRepresentation(final String originalValue) {
            Integer number;
            if ((number = nameToValue.get(originalValue)) != null) {
                return number;
            } else {
                throw new RepresentationNotFound(this, originalValue);
            }
        }

        /**
         * Returns the set of all possible Integer working-representations.
         * @return The set of all possible Integer working-representations.
         */
        public Set<Integer> getUniqueNumberRepresentations() {
            return new HashSet<>(valueToName.keySet());
        }

        @Override
        public String toString() {
            return "CategoricalFeature{" +
                    "name=" + name +
                    "}";
        }
    }

    /**
     * Class representing all discretized numeric features as a subcase of categorical features.
     * Contains the means to discretize a double value corresponding to the given split points.
     */
    public static class DiscretizedNumericFeature extends CategoricalFeature {
        private final NumericFeature originalFeature;
        private final double[] splitPoints;
        private final double[] originalValues;
        private final int[] discretizations;
        private final int precision;

        /**
         * Constructor for a DiscretizedNumericFeature instance.
         * @param originalFeature The NumericFeature which was transformed to a DiscretizedNumericFeature.
         * @param values The original values used to create this DiscretizedNumericFeature.
         * @param splitPoints The split points used to discretize the values of the original NumericFeature.
         */
        public DiscretizedNumericFeature(final NumericFeature originalFeature,
                                         final double[] values,
                                         final double[] splitPoints) {
            super(originalFeature.getName());
            this.originalFeature = originalFeature;
            this.originalValues = values;
            this.precision = 4;

            double previous = Double.NEGATIVE_INFINITY;
            ArrayList<Double> newSplitPointList = new ArrayList<>();
            for (double p : splitPoints) {
                // Make sure splitPoints are given in sorted order
                if (previous > p) {
                    throw new IllegalArgumentException("The split points must be given in sorted order.");
                }
                // If multiple split points have the same value, remove them
                if (Utility.roundDouble(previous, precision) != Utility.roundDouble(p, precision)) {
                    newSplitPointList.add(p);
                }
                previous = p;
            }

            this.splitPoints = new double[newSplitPointList.size()];
            for (int i = 0; i < this.splitPoints.length; i++) {
                this.splitPoints[i] = newSplitPointList.get(i);
            }

            double lowerBound = this.splitPoints[0];
            String representation = name + "<=" + Utility.roundDouble(lowerBound, precision);
            nameToValue.put(representation, nameToValue.size());
            for (int i = 1; i < this.splitPoints.length; i++) {
                representation =  Utility.roundDouble(lowerBound, precision)+ "<=" + name + "<" + Utility.roundDouble(this.splitPoints[i], precision);
                nameToValue.put(representation, nameToValue.size());
                lowerBound = this.splitPoints[i];
            }
            representation = Utility.roundDouble(lowerBound, precision) + "<=" + name;
            nameToValue.put(representation, nameToValue.size());

            this.discretizations = getDiscretization(values);

            generateValueToName();
        }

        /**
         * Returns the discretization of the given double.
         * @param value The double value to discretize.
         * @return The discretization of the double value.
         */
        public int getDiscretization(final double value) {
            for (int i = 0; i < splitPoints.length; i++) {
                if (value < splitPoints[i]) {
                    return i;
                }
            }
            return splitPoints.length;
        }

        /**
         * Returns the discretizations of the given doubles.
         * @param values The double values to discretize.
         * @return The discretizations of the double values.
         */
        public int[] getDiscretization(final double[] values) {
            int[] result = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                result[i] = getDiscretization(values[i]);
            }
            return result;
        }

        /**
         * Returns the discretizations of the original double values used to create the DiscretizedNumericFeature.
         * @return An int-array representing the discretizations of the initial double values.
         */
        public int[] getDiscretizationOfOriginal() {
            return discretizations;
        }

        /**
         * Returns the original double values used to create the DiscretizedNumericFeature.
         * @return A double-array representing the original values of the discretized NumericFeature.
         */
        public double[] getOriginalValues() {
            return originalValues;
        }

        @Override
        public String toString() {
            return "DiscretizedNumericFeature{" +
                    "name=" + name +
                    ", nameToValue=" + nameToValue +
                    ", splitPoints=" + Arrays.toString(splitPoints) +
                    "}";
        }

        @Override
        public boolean equals(final Object feature) {
            return this.getClass().equals(feature.getClass()) &&
                    this.name.equals(this.name) &&
                    Arrays.equals(this.splitPoints, (((DiscretizedNumericFeature) feature).splitPoints));
        }
    }

}
