package fi.csc.pid.api.model.doi;

import java.awt.print.Book;

public class Types {

    public enum ResourceTypeGeneral {
        Audiovisual,
        Book,
        BookChapter,
        Collection,
        ComputationalNotebook,
        ConferencePaper,
        ConferenceProceeding,
        DataPaper,
        Dataset,
        Dissertation,
        Event,
        Image,
        InteractiveResource,
        Journal,
        JournalArticle,
        Model,
        OutputManagementPlan,
        PeerReview,
        PhysicalObject,
        Preprint,
        Report,
        Service,
        Software,
        Sound,
        Standard,
        Text,
        Workflow,
        Other
    }

    String resourceType;
    ResourceTypeGeneral resourceTypeGeneral;
}
