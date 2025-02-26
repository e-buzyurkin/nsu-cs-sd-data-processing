
package ru.nsu.buzyurkin.generated;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for children-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="children-type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="daughter" type="{}person-ref" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="son" type="{}person-ref" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "children-type", propOrder = {
    "daughter",
    "son"
})
public class ChildrenType {

    protected List<PersonRef> daughter;
    protected List<PersonRef> son;

    /**
     * Gets the value of the daughter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the daughter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDaughter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonRef }
     * 
     * 
     */
    public List<PersonRef> getDaughter() {
        if (daughter == null) {
            daughter = new ArrayList<PersonRef>();
        }
        return this.daughter;
    }

    /**
     * Gets the value of the son property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the son property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSon().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonRef }
     * 
     * 
     */
    public List<PersonRef> getSon() {
        if (son == null) {
            son = new ArrayList<PersonRef>();
        }
        return this.son;
    }

}
