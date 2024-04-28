package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

public sealed interface Restriction {
    public static Optional<Restriction> parse(XmlNode elem){
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        Optional<Restriction> r1 = MinExclusive.parseList(elem).head().map(a->a);
        Optional<Restriction> r2 = MinInclusive.parseList(elem).head().map(a->a);
        Optional<Restriction> r3 = MaxExclusive.parseList(elem).head().map(a->a);
        Optional<Restriction> r4 = MaxInclusive.parseList(elem).head().map(a->a);
        Optional<Restriction> r18 = MaxLength.parseList(elem).head().map(a->a);
        Optional<Restriction> r19 = WhiteSpace.parseList(elem).head().map(a->a);
        Optional<Restriction> r5 = TotalDigits.parseList(elem).head().map(a->a);
        Optional<Restriction> r6 = FractionDigits.parseList(elem).head().map(a->a);
        Optional<Restriction> r7 = Length.parseList(elem).head().map(a->a);
        Optional<Restriction> r8 = MinLength.parseList(elem).head().map(a->a);
        Optional<Restriction> r9 = Enumeration.parseList(elem).head().map(a->a);
        Optional<Restriction> r10 = Pattern.parseList(elem).head().map(a->a);
        Optional<Restriction> r11 = Group.parseList(elem).head().map(a->a);
        Optional<Restriction> r12 = All.parseList(elem).head().map(a->a);
        Optional<Restriction> r13 = Choice.parseList(elem).head().map(a->a);
        Optional<Restriction> r14 = Sequence.parseList(elem).head().map(a->a);
        Optional<Restriction> r15 = Attribute.parseList(elem).head().map(a->a);
        Optional<Restriction> r16 = AttributeGroup.parseList(elem).head().map(a->a);
        Optional<Restriction> r17 = AnyAttribute.parseList(elem).head().map(a->a);
        return r1.or(()->r2).or(()->r3).or(()->r4)
            .or(()->r5).or(()->r6).or(()->r7).or(()->r8)
            .or(()->r9).or(()->r10).or(()->r11).or(()->r12)
            .or(()->r13).or(()->r14).or(()->r15).or(()->r16)
            .or(()->r17).or(()->r18).or(()->r19)
            ;
    }

    public static ImList<Restriction> parseList(XmlNode elem){
        ImList<Restriction> r1 = MinExclusive.parseList(elem).map(a->a);
        ImList<Restriction> r2 = MinInclusive.parseList(elem).map(a->a);
        ImList<Restriction> r3 = MaxExclusive.parseList(elem).map(a->a);
        ImList<Restriction> r4 = MaxInclusive.parseList(elem).map(a->a);
        ImList<Restriction> r18 = MaxLength.parseList(elem).map(a->a);
        ImList<Restriction> r19 = WhiteSpace.parseList(elem).map(a->a);
        ImList<Restriction> r5 = TotalDigits.parseList(elem).map(a->a);
        ImList<Restriction> r6 = FractionDigits.parseList(elem).map(a->a);
        ImList<Restriction> r7 = Length.parseList(elem).map(a->a);
        ImList<Restriction> r8 = MinLength.parseList(elem).map(a->a);
        ImList<Restriction> r9 = Enumeration.parseList(elem).map(a->a);
        ImList<Restriction> r10 = Pattern.parseList(elem).map(a->a);
        ImList<Restriction> r11 = Group.parseList(elem).map(a->a);
        ImList<Restriction> r12 = All.parseList(elem).map(a->a);
        ImList<Restriction> r13 = Choice.parseList(elem).map(a->a);
        ImList<Restriction> r14 = Sequence.parseList(elem).map(a->a);
        ImList<Restriction> r15 = Attribute.parseList(elem).map(a->a);
        ImList<Restriction> r16 = AttributeGroup.parseList(elem).map(a->a);
        ImList<Restriction> r17 = AnyAttribute.parseList(elem).map(a->a);
        return r1.join(r2).join(r3).join(r4)
            .join(r5).join(r6).join(r7).join(r8)
            .join(r9).join(r10).join(r11).join(r12)
            .join(r13).join(r14).join(r15).join(r16)
            .join(r17).join(r18).join(r19);
    }

    public final class MinExclusive implements Restriction {
        public static final String Name = "minExclusive";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<MinExclusive> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new MinExclusive((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public MinExclusive(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class MinInclusive implements Restriction {
        public static final String Name = "minInclusive";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<MinInclusive> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new MinInclusive((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public MinInclusive(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class MaxExclusive implements Restriction {
        public static final String Name = "maxExclusive";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<MaxExclusive> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new MaxExclusive((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public MaxExclusive(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class MaxInclusive implements Restriction {
        public static final String Name = "maxInclusive";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<MaxInclusive> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new MaxInclusive((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public MaxInclusive(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class TotalDigits implements Restriction {
        public static final String Name = "totalDigits";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<TotalDigits> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new TotalDigits((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public TotalDigits(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class FractionDigits implements Restriction {
        public static final String Name = "fractionDigits";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<FractionDigits> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new FractionDigits((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public FractionDigits(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class Length implements Restriction {
        public static final String Name = "length";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<Length> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new Length((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public Length(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class MinLength implements Restriction {
        public static final String Name = "minLength";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<MinLength> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new MinLength((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public MinLength(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class MaxLength implements Restriction {
        public static final String Name = "maxLength";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<MaxLength> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new MaxLength((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public MaxLength(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class Enumeration implements Restriction {
        public static final String Name = "maxLength";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<Enumeration> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new Enumeration((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public Enumeration(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class WhiteSpace implements Restriction {
        public static final String Name = "whiteSpace";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<WhiteSpace> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new WhiteSpace((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public WhiteSpace(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class Pattern implements Restriction {
        public static final String Name = "pattern";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<Pattern> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new Pattern((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public Pattern(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class Group implements Restriction {
        public static final String Name = "group";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<Group> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new Group((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public Group(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class All implements Restriction {
        public static final String Name = "all";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<All> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new All((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public All(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class Choice implements Restriction {
        public static final String Name = "choice";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<Choice> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new Choice((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public Choice(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class Sequence implements Restriction {
        public static final String Name = "sequence";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<Sequence> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new Sequence((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public Sequence(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class Attribute implements Restriction {
        public static final String Name = "attribute";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<Attribute> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new Attribute((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public Attribute(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class AttributeGroup implements Restriction {
        public static final String Name = "attributeGroup";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<AttributeGroup> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new AttributeGroup((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public AttributeGroup(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }

    public final class AnyAttribute implements Restriction {
        public static final String Name = "anyAttribute";

        public static boolean isMatch(XmlNode node) {
            return
                node instanceof XmlElem el &&
                    Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                    Objects.equals(el.getLocalName(), Name);
        }

        public static ImList<AnyAttribute> parseList(XmlNode el ){
            if( el==null ) throw new IllegalArgumentException("el==null");
            return isMatch(el)
                ? ImList.first(new AnyAttribute((XmlElem) el))
                : ImList.empty();
        }

        public final XmlElem elem;

        public AnyAttribute(XmlElem elem) {
            if( elem==null ) throw new IllegalArgumentException("elem==null");
            this.elem = elem;
        }
    }
}
