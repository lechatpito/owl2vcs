package owl2vcs.changeset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.change.AxiomChangeData;
import org.semanticweb.owlapi.change.ImportChangeData;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.change.SetOntologyIDData;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import owl2vcs.changes.AddPrefixData;
import owl2vcs.changes.ModifyPrefixData;
import owl2vcs.changes.PrefixChangeData;
import owl2vcs.changes.RemovePrefixData;
import owl2vcs.changes.RenamePrefixData;
import owl2vcs.changes.SetOntologyFormatData;

import com.google.common.collect.Iterables;

public abstract class ChangeSet {

    protected ChangeSet() {
    }

    private SetOntologyFormatData formatChange;

    private Collection<PrefixChangeData> prefixChanges;

    private SetOntologyIDData ontologyIdChange;

    private Collection<ImportChangeData> importChanges;

    private Collection<OWLOntologyChangeData> annotationChanges;

    private Collection<AxiomChangeData> axiomChanges;

    public SetOntologyFormatData getFormatChange() {
        return formatChange;
    }

    public Collection<PrefixChangeData> getPrefixChanges() {
        return prefixChanges;
    }

    public SetOntologyIDData getOntologyIdChange() {
        return ontologyIdChange;
    }

    public Collection<ImportChangeData> getImportChanges() {
        return importChanges;
    }

    public Collection<OWLOntologyChangeData> getAnnotationChanges() {
        return annotationChanges;
    }

    public Collection<AxiomChangeData> getAxiomChanges() {
        return axiomChanges;
    }

    protected void setFormatChange(final SetOntologyFormatData formatChange) {
        this.formatChange = formatChange;
    }

    protected void setPrefixChanges(final Collection<PrefixChangeData> prefixChanges) {
        this.prefixChanges = prefixChanges;
    }

    protected void setOntologyIdChange(final SetOntologyIDData ontologyIdChange) {
        this.ontologyIdChange = ontologyIdChange;
    }

    protected void setImportChanges(final Collection<ImportChangeData> importChanges) {
        this.importChanges = importChanges;
    }

    protected void setAnnotationChanges(
            final Collection<OWLOntologyChangeData> annotationChanges) {
        this.annotationChanges = annotationChanges;
    }

    protected void setAxiomChanges(final Collection<AxiomChangeData> axiomChanges) {
        this.axiomChanges = axiomChanges;
    }

    /**
     * Count all changes.
     *
     * @return
     */
    public int size() {
        int sum = 0;
        if (formatChange != null)
            sum++;
        if (ontologyIdChange != null)
            sum++;
        sum += prefixChanges.size();
        sum += importChanges.size();
        sum += annotationChanges.size();
        sum += axiomChanges.size();
        return sum;
    }

    public Collection<Object> asCollection() {
        ArrayList<Object> result = new ArrayList<Object>();
        if (formatChange != null)
            result.add(formatChange);
        result.addAll(prefixChanges);
        if (ontologyIdChange != null)
            result.add(ontologyIdChange);
        result.addAll(importChanges);
        result.addAll(annotationChanges);
        result.addAll(axiomChanges);
        return result;
    }

    @SuppressWarnings({ "unchecked", "serial" })
    public Iterable<OWLOntologyChangeData> all() {
        return Iterables.concat(
                new ArrayList<OWLOntologyChangeData>() { { add(formatChange); } },
                prefixChanges,
                new ArrayList<OWLOntologyChangeData>() { { add(ontologyIdChange); } },
                importChanges, annotationChanges, axiomChanges);
    }

    public <R, E extends Exception> void accept(final CustomOntologyChangeDataVisitor<R, E> visitor) throws E {
        if (formatChange != null)
            formatChange.accept(visitor);
        for (final PrefixChangeData c : prefixChanges)
            c.accept(visitor);
        if (ontologyIdChange != null)
            ontologyIdChange.accept(visitor);
        for (final OWLOntologyChangeData c : importChanges)
            c.accept(visitor);
        for (final OWLOntologyChangeData c : annotationChanges)
            c.accept(visitor);
        for (final OWLOntologyChangeData c : axiomChanges)
            c.accept(visitor);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((annotationChanges == null) ? 0 : annotationChanges
                        .hashCode());
        result = prime * result
                + ((axiomChanges == null) ? 0 : axiomChanges.hashCode());
        result = prime * result
                + ((formatChange == null) ? 0 : formatChange.hashCode());
        result = prime * result
                + ((importChanges == null) ? 0 : importChanges.hashCode());
        result = prime
                * result
                + ((ontologyIdChange == null) ? 0 : ontologyIdChange.hashCode());
        result = prime * result
                + ((prefixChanges == null) ? 0 : prefixChanges.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ChangeSet))
            return false;
        ChangeSet other = (ChangeSet) obj;
        if (annotationChanges == null) {
            if (other.annotationChanges != null)
                return false;
        } else if (!collectionsEqual(annotationChanges, other.annotationChanges))
            return false;
        if (axiomChanges == null) {
            if (other.axiomChanges != null)
                return false;
        } else if (!collectionsEqual(axiomChanges, other.axiomChanges))
            return false;
        if (formatChange == null) {
            if (other.formatChange != null)
                return false;
        } else if (!formatChange.equals(other.formatChange))
            return false;
        if (importChanges == null) {
            if (other.importChanges != null)
                return false;
        } else if (!collectionsEqual(importChanges, other.importChanges))
            return false;
        if (ontologyIdChange == null) {
            if (other.ontologyIdChange != null)
                return false;
        } else if (!ontologyIdChange.equals(other.ontologyIdChange))
            return false;
        if (prefixChanges == null) {
            if (other.prefixChanges != null)
                return false;
        } else if (!collectionsEqual(prefixChanges, other.prefixChanges))
            return false;
        return true;
    }

    private <T> boolean collectionsEqual(Collection<T> c1, Collection<T> c2) {
        if ((c1 instanceof Set) && (c2 instanceof Set))
            return c1.equals(c2);
        if ((c1 instanceof Set))
            return c1.equals(new HashSet<T>(c2));
        if ((c2 instanceof Set))
            return new HashSet<T>(c1).equals(c2);
        return false;
    }

    public boolean isEmpty() {
        return (formatChange == null)
                && (prefixChanges == null || prefixChanges.isEmpty())
                && (ontologyIdChange == null)
                && (importChanges == null || importChanges.isEmpty())
                && (annotationChanges == null || annotationChanges.isEmpty())
                && (axiomChanges == null || axiomChanges.isEmpty());
    }

    public void applyTo(OWLOntology ontology) throws UnsupportedOntologyFormatException {
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntologyChangeData c : axiomChanges)
            changes.add(c.createOntologyChange(ontology));
        for (OWLOntologyChangeData c : annotationChanges)
            changes.add(c.createOntologyChange(ontology));
        for (OWLOntologyChangeData c : importChanges)
            changes.add(c.createOntologyChange(ontology));
        if (ontologyIdChange != null)
            manager.applyChange(ontologyIdChange.createOntologyChange(ontology));
        manager.applyChanges(changes);
        // prefix changes can only be applied to ontology in a prefix format
        OWLOntologyFormat oldFormat = manager.getOntologyFormat(ontology);
        Map<String, String> prefixMap = null;
        if (manager.getOntologyFormat(ontology).isPrefixOWLOntologyFormat()) {
            PrefixOWLOntologyFormat oldPrefixFormat = oldFormat.asPrefixOWLOntologyFormat();
            prefixMap = oldPrefixFormat.getPrefixName2PrefixMap();
            for (PrefixChangeData c : prefixChanges) {
                if (c instanceof AddPrefixData) {
                    // prefix should not be there
                    assert prefixMap.get(c.getPrefixName()) == null;
                    prefixMap.put(c.getPrefixName(), c.getPrefix());
                }
                else if (c instanceof RemovePrefixData) {
                    // prefix should already be there
                    assert prefixMap.get(c.getPrefixName()).equals(c.getPrefix());
                    prefixMap.remove(c.getPrefixName());
                }
                else if (c instanceof ModifyPrefixData) {
                    // prefix should already be there
                    assert prefixMap.get(c.getPrefixName()) != null;
                    // and it should not be equal to new prefix
                    assert !prefixMap.get(c.getPrefixName()).equals(c.getPrefix());
                    prefixMap.put(c.getPrefixName(), c.getPrefix());
                }
                else if (c instanceof RenamePrefixData) {
                    String prefixToFind = c.getPrefix();
                    String oldPrefixName = null;
                    for (Map.Entry<String, String> e : prefixMap.entrySet()) {
                        if (e.getValue().equals(prefixToFind)) {
                            oldPrefixName = e.getKey();
                        }
                    }
                    // prefix name should already be there
                    assert oldPrefixName != null;
                    // and it should not be equal to the new name
                    assert !oldPrefixName.equals(c.getPrefixName());

                    prefixMap.remove(oldPrefixName);
                    prefixMap.put(c.getPrefixName(), c.getPrefix());
                }
            }
        }
        OWLOntologyFormat newFormat;
        if (formatChange != null) {
            newFormat = formatChange.getNewFormat();
        }
        else {
            // TODO: test if it is ok to do this
            newFormat = oldFormat;
        }
        // copy prefixes to new format
        if (newFormat.isPrefixOWLOntologyFormat()) {
            PrefixOWLOntologyFormat newPrefixFormat = newFormat.asPrefixOWLOntologyFormat();
            if (prefixMap != null) {
                newPrefixFormat.clearPrefixes();
                for (Map.Entry<String, String> e : prefixMap.entrySet()) {
                    newPrefixFormat.setPrefix(e.getKey(), e.getValue());
                }
            }
        }
        // apply format (with prefixes)
        manager.setOntologyFormat(ontology, newFormat);
    }
}
