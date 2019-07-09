package de.viadee.xai.framework.persistence.calculator_persistence_service;

import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;

/**
 * Interface for all classes persisting calculators.
 */
public interface CalculatorPersistenceService {

    /**
     * Persists the calculator.
     * @param calculator The calculator which should be persisted.
     * @return The ID of the persisted calculator.
     */
    int persistCalculator(RoaringBitmapCalculator calculator);

    /**
     * Loads the calculator with the specified ID.
     * @param id The id of the calculator which should be loaded.
     * @return The loaded calculator.
     */
    RoaringBitmapCalculator loadCalculator(int id);

    /**
     * Terminates, e.g., the database connection.
     */
    void terminate();
}
