package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.util.ArrayList;
import java.util.List;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;


public class MultipleEntryIterator implements BibEntryIterator {
    
    private List<BibEntryIterator> iterators;
    private BibEntryIterator currentIterator;
    
    public MultipleEntryIterator() {
        iterators = new ArrayList<>();
    }

    @Override
    public BibEntry next() {
        if (currentIterator != null && currentIterator.hasNext()) {
            return currentIterator.next();
        }
        
        if (currentIterator == null || !currentIterator.hasNext()) {
            if (iterators.size() > 0) {
                currentIterator = iterators.get(0);
                iterators.remove(0);
                if (currentIterator.hasNext()) {
                    return currentIterator.next();
                } else {
                    return next();
                }
            }
        }
        
        return null;
    }

    @Override
    public boolean hasNext() {
        if (currentIterator != null && currentIterator.hasNext()) {
            return true;
        }
        if (currentIterator == null || !currentIterator.hasNext()) {
            if (iterators.size() > 0 && iterators.get(0).hasNext()) {
                return true;
            }
        }
        return false;
    }
    
    public void addIterator(BibEntryIterator iterator) {
        if (iterator != null) {
            iterators.add(iterator);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

}
