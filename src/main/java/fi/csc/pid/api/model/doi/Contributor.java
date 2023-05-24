package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Contributor {

    public enum ContributorType{
        ContactPerson,
        DataCollector,
        DataCurator,
        DataManager,
        Distributor,
        Editor,
        HostingInstitution,
        Producer,
        ProjectLeader,
        ProjectManager,
        ProjectMember,
        RegistrationAgency,
        RegistrationAuthority,
        RelatedPerson,
        Researcher,
        ResearchGroup,
        RightsHolder,
        Sponsor,
        Supervisor,
        WorkPackageLeader,
        Other
    }

    //String contributor;
    ContributorType contributorType;
    String contributorName;
    String nameIdentifier;
    String nameIdentifierScheme;
    String affiliation;

    public ContributorType getContributorType() {
        return contributorType;
    }

    public void setContributorType(ContributorType contributorType) {
        this.contributorType = contributorType;
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }

    public String getNameIdentifier() {
        return nameIdentifier;
    }

    public void setNameIdentifier(String nameIdentifier) {
        this.nameIdentifier = nameIdentifier;
    }

    public String getNameIdentifierScheme() {
        return nameIdentifierScheme;
    }

    public void setNameIdentifierScheme(String nameIdentifierScheme) {
        this.nameIdentifierScheme = nameIdentifierScheme;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
}
