package net.sourceforge.fenixedu.domain.space;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;

import org.fenixedu.bennu.core.domain.groups.Group;

import pt.ist.fenixframework.dml.runtime.RelationAdapter;

public class BlueprintFile extends BlueprintFile_Base {

    static {
        getRelationBlueprintBlueprintFile().addListener(new BlueprintBlueprintFileListener());
    }

    public BlueprintFile(Blueprint blueprint, String filename, String displayName, Group permittedGroup, byte[] content) {
        super();
        //TODO: bennu-io must support groups on files
        setBlueprint(blueprint);
        init(filename, displayName, content);
        setContentFile(content);
    }

    @Override
    public void setBlueprint(Blueprint blueprint) {
        if (blueprint == null) {
            throw new SpaceDomainException("error.blueprintFile.no.blueprint");
        }
        super.setBlueprint(blueprint);
    }

    @Override
    public void delete() {
        super.setBlueprint(null);
        super.delete();
    }

    public String getDirectDownloadUrlFormat() {
        //TODO: download servlet must be on bennu-io
        return getExternalId() + "/" + getFilename();
    }

    private static class BlueprintBlueprintFileListener extends RelationAdapter<BlueprintFile, Blueprint> {
        @Override
        public void afterRemove(BlueprintFile blueprintFile, Blueprint blueprint) {
            if (blueprintFile != null && blueprint != null) {
                blueprintFile.delete();
            }
        }
    }

    @Override
    public byte[] getContent() {
        return super.getContentFile();
    }

    @Deprecated
    public boolean hasBlueprint() {
        return getBlueprint() != null;
    }

    @Deprecated
    public boolean hasContent() {
        return getContent() != null;
    }

}
