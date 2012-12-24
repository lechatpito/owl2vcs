package owl2vcs.changeset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.change.AddAxiomData;
import org.semanticweb.owlapi.change.AddImportData;
import org.semanticweb.owlapi.change.AddOntologyAnnotationData;
import org.semanticweb.owlapi.change.AxiomChangeData;
import org.semanticweb.owlapi.change.ImportChangeData;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.change.RemoveAxiomData;
import org.semanticweb.owlapi.change.RemoveImportData;
import org.semanticweb.owlapi.change.RemoveOntologyAnnotationData;
import org.semanticweb.owlapi.change.SetOntologyIDData;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import owl2vcs.changes.AddPrefixData;
import owl2vcs.changes.ModifyPrefixData;
import owl2vcs.changes.PrefixChangeData;
import owl2vcs.changes.RemovePrefixData;
import owl2vcs.changes.RenamePrefixData;
import owl2vcs.changes.SetOntologyFormatData;

public class FullChangeSet extends ChangeSet {

    private final OWLOntology parent;

    public OWLOntology getParent() {
        return parent;
    }

    private final OWLOntology child;

    public OWLOntology getChild() {
        return child;
    }


    /**
     * Calculate changeset between the two ontologies.
     *
     * @param parent
     *            The original ontology
     * @param child
     *            The updated ontology
     */
    public FullChangeSet(final OWLOntology parent, final OWLOntology child) {

        this.parent = parent;
        this.child = child;

        // Ontology format
        final OWLOntologyFormat parentFormat = parent.getOWLOntologyManager()
                .getOntologyFormat(parent);
        final OWLOntologyFormat childFormat = child.getOWLOntologyManager()
                .getOntologyFormat(child);
        if (!parentFormat.equals(childFormat))
            setFormatChange(new SetOntologyFormatData(childFormat));

        // Prefixes
        setPrefixChanges(findPrefixChanges(parent, child));

        // Ontology ID
        if (!(parent.getOntologyID().isAnonymous() && child.getOntologyID()
                .isAnonymous()))
            if (!parent.getOntologyID().equals(child.getOntologyID()))
                setOntologyIdChange(new SetOntologyIDData(child.getOntologyID()));

        // Imports
        setImportChanges(findImportChanges(parent, child));

        // Annotations
        setAnnotationChanges(findAnnotationChanges(parent, child));

        // Axioms
        setAxiomChanges(findAxiomChanges(parent, child));
    }


    private Collection<ImportChangeData> findImportChanges(
            final OWLOntology parent, final OWLOntology child) {
        final Collection<ImportChangeData> importChanges = new ArrayList<ImportChangeData>();
        final Collection<OWLImportsDeclaration> parentImports = parent
                .getImportsDeclarations();
        final Collection<OWLImportsDeclaration> childImports = child
                .getImportsDeclarations();
        for (final OWLImportsDeclaration decl : parentImports)
            if (!childImports.contains(decl))
                importChanges.add(new RemoveImportData(decl));
        for (final OWLImportsDeclaration decl : childImports)
            if (!parentImports.contains(decl))
                importChanges.add(new AddImportData(decl));
        return importChanges;
    }


    private Collection<OWLOntologyChangeData> findAnnotationChanges(
            final OWLOntology parent, final OWLOntology child) {
        final Collection<OWLOntologyChangeData> annotationChanges = new ArrayList<OWLOntologyChangeData>();
        final Collection<OWLAnnotation> parentAnnotations = parent
                .getAnnotations();
        final Collection<OWLAnnotation> childAnnotations = child
                .getAnnotations();
        for (final OWLAnnotation annotation : parentAnnotations)
            if (!childAnnotations.contains(annotation))
                annotationChanges.add(new RemoveOntologyAnnotationData(annotation));
        for (final OWLAnnotation annotation : childAnnotations)
            if (!parentAnnotations.contains(annotation))
                annotationChanges.add(new AddOntologyAnnotationData(annotation));
        return annotationChanges;
    }


    private Collection<AxiomChangeData> findAxiomChanges(
            final OWLOntology parent, final OWLOntology child) {
        final Collection<AxiomChangeData> axiomChanges = new ArrayList<AxiomChangeData>();
        if (parent.getOWLOntologyManager().getOntologyFormat(parent) instanceof ManchesterOWLSyntaxOntologyFormat) {
            // collect annotation properties
            final Set<OWLAnnotationProperty> parentAnnotationProperties = new HashSet<OWLAnnotationProperty>();
            for (final OWLAnnotation a : parent.getAnnotations())
                parentAnnotationProperties.add(a.getProperty());
            for (final OWLAxiom axiom : parent.getAxioms()) {
                // ignore declarations of used entities
                if (axiom.isOfType(AxiomType.DECLARATION)) {
                    final OWLEntity e = ((OWLDeclarationAxiom) axiom)
                            .getEntity();
                    if (parentAnnotationProperties.contains(e)
                            || (parent.getReferencingAxioms(e).size() > 1))
                        // skip this declaration axiom
                        continue;
                }
                if (!child.containsAxiom(axiom))
                    axiomChanges.add(new RemoveAxiomData(axiom));
            }
        } else
            for (final OWLAxiom axiom : parent.getAxioms())
                if (!child.containsAxiom(axiom))
                    axiomChanges.add(new RemoveAxiomData(axiom));
        if (child.getOWLOntologyManager().getOntologyFormat(child) instanceof ManchesterOWLSyntaxOntologyFormat) {
            // collect annotation properties
            final Set<OWLAnnotationProperty> childAnnotationProperties = new HashSet<OWLAnnotationProperty>();
            for (final OWLAnnotation a : parent.getAnnotations())
                childAnnotationProperties.add(a.getProperty());
            for (final OWLAxiom axiom : child.getAxioms()) {
                if (axiom.isOfType(AxiomType.DECLARATION)) {
                    // ignore declarations of used entities
                    final OWLEntity e = ((OWLDeclarationAxiom) axiom)
                            .getEntity();
                    if (childAnnotationProperties.contains(e)
                            || (child.getReferencingAxioms(e).size() > 1))
                        // skip this declaration axiom
                        continue;
                }
                if (!parent.containsAxiom(axiom))
                    axiomChanges.add(new AddAxiomData(axiom));
            }
        } else
            for (final OWLAxiom axiom : child.getAxioms())
                if (!parent.containsAxiom(axiom))
                    axiomChanges.add(new AddAxiomData(axiom));
        return axiomChanges;
    }


    private Collection<PrefixChangeData> findPrefixChanges(
            final OWLOntology parent, final OWLOntology child) {
        final OWLOntologyFormat parentFormat = parent.getOWLOntologyManager()
                .getOntologyFormat(parent);
        final OWLOntologyFormat childFormat = child.getOWLOntologyManager()
                .getOntologyFormat(child);
        final Collection<PrefixChangeData> prefixChanges = new ArrayList<PrefixChangeData>();
        if (parentFormat instanceof PrefixOWLOntologyFormat) {
            final PrefixOWLOntologyFormat parentPrefixFormat = (PrefixOWLOntologyFormat) parentFormat;
            if (childFormat instanceof PrefixOWLOntologyFormat) {
                final PrefixOWLOntologyFormat childPrefixFormat = (PrefixOWLOntologyFormat) childFormat;
                for (final String parentPrefixName : parentPrefixFormat.getPrefixNames()) {
                    final String parentPrefixValue = parentPrefixFormat.getPrefix(parentPrefixName);
                    final String childPrefixValue = childPrefixFormat.getPrefix(parentPrefixName);
                    if (parentPrefixValue.equals(childPrefixValue))
                        continue;
                    String childPrefixName = null;
                    for (final Entry<String, String> e : childPrefixFormat
                            .getPrefixName2PrefixMap().entrySet())
                        if (e.getValue().equals(parentPrefixValue))
                            childPrefixName = e.getKey();
                    if ((childPrefixValue != null)
                            && (!childPrefixValue.equals(parentPrefixValue)))
                        prefixChanges.add(new ModifyPrefixData(parentPrefixName, parentPrefixValue, childPrefixValue));
                    if ((childPrefixName != null)
                            && (!childPrefixName.equals(parentPrefixName)))
                        prefixChanges
                                .add(new RenamePrefixData(parentPrefixName,
                                        parentPrefixValue, childPrefixName));
                    if ((childPrefixValue == null) && (childPrefixName == null))
                        prefixChanges.add(new RemovePrefixData(
                                parentPrefixName, parentPrefixValue));
                }
                for (final String childPrefixName : childPrefixFormat
                        .getPrefixNames()) {
                    final String parentPrefixValue = parentPrefixFormat
                            .getPrefix(childPrefixName);
                    final String childPrefixValue = childPrefixFormat
                            .getPrefix(childPrefixName);
                    if (childPrefixValue.equals(parentPrefixValue))
                        continue;
                    String parentPrefixName = null;
                    for (final Entry<String, String> e : parentPrefixFormat
                            .getPrefixName2PrefixMap().entrySet())
                        if (e.getValue().equals(parentPrefixValue))
                            parentPrefixName = e.getKey();
                    if ((parentPrefixValue == null) && (parentPrefixName == null))
                        prefixChanges.add(new AddPrefixData(
                                childPrefixName, childPrefixValue));
                }
            } else
                // Child has not-prefix format, remove all prefixes
                for (final String prefixName : parentPrefixFormat
                        .getPrefixNames()) {
                    final String prefix = parentPrefixFormat
                            .getPrefix(prefixName);
                    prefixChanges.add(new RemovePrefixData(prefixName,
                            prefix));
                }
        } else if (childFormat instanceof PrefixOWLOntologyFormat) {
            // Parent has not-prefix format, add all child prefixes
            final PrefixOWLOntologyFormat childPrefixFormat = (PrefixOWLOntologyFormat) childFormat;
            for (final String prefixName : childPrefixFormat.getPrefixNames()) {
                final String prefix = childPrefixFormat.getPrefix(prefixName);
                prefixChanges.add(new AddPrefixData(prefixName, prefix));
            }
        }
        return prefixChanges;
    }

    /**
     * @return Prefix name to prefix map
     */
    public Map<String, String> getAllPrefixes() {
        final Map<String, String> prefixName2PrefixMap = new HashMap<String, String>();
        final Map<String, String> prefixName2PrefixMap1 = parent
                .getOWLOntologyManager().getOntologyFormat(parent)
                .asPrefixOWLOntologyFormat().getPrefixName2PrefixMap();
        final Map<String, String> prefixName2PrefixMap2 = child
                .getOWLOntologyManager().getOntologyFormat(child)
                .asPrefixOWLOntologyFormat().getPrefixName2PrefixMap();
        prefixName2PrefixMap.putAll(prefixName2PrefixMap1);
        prefixName2PrefixMap.putAll(prefixName2PrefixMap2);
        return prefixName2PrefixMap;
    }
}
