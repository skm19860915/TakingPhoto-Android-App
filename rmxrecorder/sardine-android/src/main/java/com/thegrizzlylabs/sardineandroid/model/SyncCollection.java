//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2015.09.22 at 01:09:41 PM PDT
//


package com.thegrizzlylabs.sardineandroid.model;

import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{DAV:}sync-token"/&gt;
 *         &lt;element ref="{DAV:}sync-level"/&gt;
 *         &lt;element ref="{DAV:}limit" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}prop"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@Root
@Namespace(prefix = "D", reference = "DAV:")
public class SyncCollection {

//    @XmlElement(name = "sync-token", required = true)
//    @XmlSchemaType(name = "anyURI")
    protected String syncToken;
    //@XmlElement(name = "sync-level", required = true)
    protected String syncLevel;
    protected Limit limit;
    //@XmlElement(required = true)
    protected Prop prop;

    /**
     * Gets the value of the syncToken property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSyncToken() {
        return syncToken;
    }

    /**
     * Sets the value of the syncToken property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSyncToken(String value) {
        this.syncToken = value;
    }

    /**
     * Gets the value of the syncLevel property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSyncLevel() {
        return syncLevel;
    }

    /**
     * Sets the value of the syncLevel property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSyncLevel(String value) {
        this.syncLevel = value;
    }

    /**
     * Gets the value of the limit property.
     *
     * @return
     *     possible object is
     *     {@link Limit }
     *
     */
    public Limit getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     *
     * @param value
     *     allowed object is
     *     {@link Limit }
     *
     */
    public void setLimit(Limit value) {
        this.limit = value;
    }

    /**
     * Gets the value of the prop property.
     *
     * @return
     *     possible object is
     *     {@link Prop }
     *
     */
    public Prop getProp() {
        return prop;
    }

    /**
     * Sets the value of the prop property.
     *
     * @param value
     *     allowed object is
     *     {@link Prop }
     *
     */
    public void setProp(Prop value) {
        this.prop = value;
    }

}
