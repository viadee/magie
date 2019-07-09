package de.viadee.xai.framework.persistence.calculator_persistence_service;

import de.viadee.xai.framework.explanation_calculation.calculator.RoaringBitmapCalculator;

public class NullCalculatorPersister implements CalculatorPersistenceService {
    @Override
    public int persistCalculator(RoaringBitmapCalculator calculator) {
        return -1;
    }

    @Override
    public RoaringBitmapCalculator loadCalculator(int id) {
        return null;
    }

    @Override
    public void terminate() {}
}
