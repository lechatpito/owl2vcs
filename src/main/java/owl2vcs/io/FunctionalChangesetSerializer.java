package owl2vcs.io;

import java.io.PrintStream;

import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.util.ShortFormProvider;

import owl2vcs.changeset.ChangeSet;
import owl2vcs.render.ChangeFormat;
import owl2vcs.render.ChangeRenderer;
import owl2vcs.render.FullFormProvider;
import owl2vcs.render.FunctionalChangeRenderer;

public class FunctionalChangesetSerializer {

    public void write(ChangeSet cs, PrintStream out) {
        write(cs, out, new FullFormProvider(), ChangeFormat.COMPACT);
    }

    public void write(ChangeSet cs, PrintStream out,
            ShortFormProvider provider, ChangeFormat changeFormat) {
        if (cs.isEmpty()) {
            out.println("no changes");
            return;
        }
        ChangeRenderer cr = new FunctionalChangeRenderer(
                provider, changeFormat);
        if (cs.getFormatChange() != null)
            out.println(cr.render(cs.getFormatChange()));
        if (cs.getOntologyIdChange() != null)
            out.println(cr.render(cs.getOntologyIdChange()));
        for (final OWLOntologyChangeData c : cs.getPrefixChanges())
            out.println(cr.render(c));
        for (final OWLOntologyChangeData c : cs.getImportChanges())
            out.println(cr.render(c));
        for (final OWLOntologyChangeData c : cs.getAnnotationChanges())
            out.println(cr.render(c));
        for (final OWLOntologyChangeData c : cs.getAxiomChanges())
            out.println(cr.render(c));
    }
}
