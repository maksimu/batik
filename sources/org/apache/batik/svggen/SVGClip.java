/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts a Path object into an SVG clip
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGClip extends AbstractSVGConverter {
    /**
     * Descriptor to use where there is no clip on an element
     */
    public static final SVGClipDescriptor NO_CLIP =
        new SVGClipDescriptor(SVG_NONE_VALUE, null);

    /**
     * Used to convert clip object to SVG elements
     */
    private SVGShape shapeConverter;

    /**
     * @param generatorContext used to build Elements
     */
    public SVGClip(SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.shapeConverter = new SVGShape(generatorContext);
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions.
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc) {
        Shape userClip = gc.getClip();
        return toSVG(userClip);
    }

    /**
     * @param clip the path that should be converted to a clip object.
     * @return value of the clip-path property. If required, a
     *         new clipPath element definition may have been
     *         added to clipDefsMap.
     */
    public SVGClipDescriptor toSVG(Shape clip) {
        SVGClipDescriptor clipDesc = null;

        if (clip != null) {
            StringBuffer clipPathAttrBuf = new StringBuffer(URL_PREFIX);

            // First, convert to a GeneralPath so that the
            GeneralPath clipPath = new GeneralPath(clip);

            // Check if this object is already in the Map
            ClipKey clipKey = new ClipKey(clipPath);
            clipDesc = (SVGClipDescriptor)descMap.get(clipKey);

            if (clipDesc == null) {
                Element clipDef = clipToSVG(clip);
                clipPathAttrBuf.append(SIGN_POUND);
                clipPathAttrBuf.append(clipDef.getAttributeNS(null, ATTR_ID));
                clipPathAttrBuf.append(URL_SUFFIX);

                clipDesc = new SVGClipDescriptor(clipPathAttrBuf.toString(),
                                                 clipDef);

                descMap.put(clipKey, clipDesc);
                defSet.add(clipDef);
            }
        } else
            clipDesc = NO_CLIP;

        return clipDesc;
    }

    /**
     * In the following method, an clipping Shape is converted to
     * an SVG clipPath.
     *
     * @param clip path to convert to an SVG clipPath
     *        element
     */
    private Element clipToSVG(Shape clip) {
        Element clipDef =
            generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_CLIP_PATH_TAG);
        clipDef.setAttributeNS(null, SVG_CLIP_PATH_UNITS_ATTRIBUTE,
                               SVG_USER_SPACE_ON_USE_VALUE);

        clipDef.setAttributeNS(null, ATTR_ID,
                               generatorContext.
                               idGenerator.generateID(ID_PREFIX_CLIP_PATH));

        Element clipPath = shapeConverter.toSVG(clip);
        clipDef.appendChild(clipPath);
        return clipDef;
    }
}

/**
 * Inner class used to key clip definitions in a Map.
 * This is needed because we need to test equality
 * on the value of GeneralPath and GeneralPath's equal
 * method does not implement that behavior.
 */
class ClipKey {
    /**
     * This clip hash code. Based on the serialized path
     * data
     */
    int hashCodeValue = 0;

    /**
     * @param proxiedPath path used as an index in the Map
     */
    public ClipKey(GeneralPath proxiedPath ){
        String pathData = SVGPath.toSVGPathData(proxiedPath);
        hashCodeValue = pathData.hashCode();
    }

    /**
     * @return this object's hashcode
     */
    public int hashCode() {
        return hashCodeValue;
    }

    /**
     * @param object to compare
     * @return true if equal, false otherwise
     */
    public boolean equals(Object clipKey){
        boolean isEqual = false;
        if((clipKey != null) &&clipKey instanceof ClipKey)
            isEqual = (hashCodeValue == ((ClipKey)clipKey).hashCodeValue);

        return isEqual;
    }
}
