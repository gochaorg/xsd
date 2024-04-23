package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/**
 The <a href="https://www.w3schools.com/xml/el_restriction.asp">restriction</a> element defines restrictions on 
 a simpleType, simpleContent, or complexContent definition.

 <pre>
&lt;restriction
id=ID
base=QName
any attributes
&gt;

Content for simpleType:
( annotation?,
  ( simpleType?,
    ( minExclusive
    | minInclusive
    | maxExclusive
    | maxInclusive
    | totalDigits
    | fractionDigits
    | length
    | minLength
    | maxLength
    | enumeration
    | whiteSpace
    | pattern
    )*
  )
)

Content for simpleContent:
( annotation?,
 ( simpleType?,
   ( minExclusive
   | minInclusive
   | maxExclusive
   | maxInclusive
   | totalDigits
   | fractionDigits
   | length
   | minLength
   | maxLength
   | enumeration
   | whiteSpace
   | pattern
   )*
 )?,
 (
   ( attribute
   | attributeGroup
   )*,
   anyAttribute?
 )
)

Content for complexContent:
( annotation?,
  ( group
  | all
  | choice
  | sequence
  )?,
  (
    ( attribute
    | attributeGroup
    )*,
  anyAttribute?
 )
)

&lt;/restriction&gt;
 </pre>

 <h2> examples </h2>

 <pre>
 &lt;xs:element name="age"&gt;
     &lt;xs:simpleType&gt;
         &lt;xs:restriction base="xs:integer"&gt;
             &lt;xs:minInclusive value="0"/&gt;
             &lt;xs:maxInclusive value="100"/&gt;
         &lt;/xs:restriction&gt;
     &lt;/xs:simpleType&gt;
 &lt;/xs:element&gt; 

 &lt;xs:element name="initials"&gt;
     &lt;xs:simpleType&gt;
         &lt;xs:restriction base="xs:string"&gt;
            &lt;xs:pattern value="[a-zA-Z][a-zA-Z][a-zA-Z]"/&gt;
         &lt;/xs:restriction&gt;
     &lt;/xs:simpleType&gt;
 &lt;/xs:element&gt;

 &lt;xs:element name="password"&gt;
     &lt;xs:simpleType&gt;
         &lt;xs:restriction base="xs:string"&gt;
             &lt;xs:minLength value="5"/&gt;
             &lt;xs:maxLength value="8"/&gt;
         &lt;/xs:restriction&gt;
     &lt;/xs:simpleType&gt;
 &lt;/xs:element&gt;

 &lt;xs:complexType name="customer"&gt;
     &lt;xs:sequence&gt;
         &lt;xs:element name="firstname" type="xs:string"/&gt;
         &lt;xs:element name="lastname" type="xs:string"/&gt;
         &lt;xs:element name="country" type="xs:string"/&gt;
     &lt;/xs:sequence&gt;
 &lt;/xs:complexType&gt;

 &lt;xs:complexType name="Norwegian_customer"&gt;
     &lt;xs:complexContent&gt;
         &lt;xs:restriction base="customer"&gt;
             &lt;xs:sequence&gt;
                 &lt;xs:element name="firstname" type="xs:string"/&gt;
                 &lt;xs:element name="lastname" type="xs:string"/&gt;
                 &lt;xs:element name="country" type="xs:string" fixed="Norway"/&gt;
             &lt;/xs:sequence&gt;
         &lt;/xs:restriction&gt;
     &lt;/xs:complexContent&gt;
 &lt;/xs:complexType&gt; 
 
 </pre>

 */
public final class XsdRestriction implements Xsd,
                                             SimpleTypeContent,
                                             XsdComplexContent.Nested,
                                             BaseAttribute,
                                             IDAttribute
{
    public static final String Name = "restriction";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdRestriction> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdRestriction((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdRestriction(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<Restriction> getRestrictions(){
        return elem.getChildren().flatMap(Restriction::parseList);
    }
}
