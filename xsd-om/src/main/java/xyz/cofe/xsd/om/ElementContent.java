package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;

import java.util.Optional;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "FieldCanBeLocal"})
public final class ElementContent implements ContentDef {
    public ElementContent(
        Optional<ElementsLayout> elementsLayout,
        ImList<XsdAttribute> attributes,
        ImList<XsdAttributeGroup> attributesGroups,
        Optional<XsdAnyAttribute> anyAttribute
    ) {
        this.elementsLayout = elementsLayout;
        this.attributes = attributes;
        this.attributesGroups = attributesGroups;
        this.anyAttribute = anyAttribute;
    }

    private final Optional<ElementsLayout> elementsLayout;

    public Optional<ElementsLayout> getElementsLayout() {
        return Optional.empty();
    }

    private final ImList<XsdAttribute> attributes;

    public ImList<XsdAttribute> getAttributes() {return ImList.empty();}

    private final ImList<XsdAttributeGroup> attributesGroups;

    public ImList<XsdAttributeGroup> getAttributesGroups() {return ImList.empty();}

    private final Optional<XsdAnyAttribute> anyAttribute;

    public Optional<XsdAnyAttribute> getAnyAttribute() {return Optional.empty();}
}
