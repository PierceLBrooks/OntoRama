package ontorama.ontotools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 * Testing:
 * - package datamodel
 *
 * No tests for:
 * - all interfaces
 * - package inputsource
 * - package util
 *
 */

public class TestWebkbtoolsPackage extends TestCase {

    public static final String edgeName_subtype = "subtype";
    public static final String edgeName_similar = "similar";
    public static final String edgeName_reverse = "reverse";
    public static final String edgeName_part = "part";
    public static final String edgeName_substance = "substance";
    public static final String edgeName_instance = "instance";
    public static final String edgeName_complement = "complement";
    public static final String edgeName_location = "location";
    public static final String edgeName_member = "member";
    public static final String edgeName_object = "object";
    public static final String edgeName_url = "url";
    public static final String edgeName_nounType = "nounType";
    public static final String edgeName_description = "description";
    public static final String edgeName_synonym = "synonym";
    public static final String edgeName_creator = "creator";


    public TestWebkbtoolsPackage(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ontorama.ontotools");

        suite.addTest(ontorama.ontotools.inputsource.TestSourcePackage.suite());
        suite.addTest(ontorama.ontotools.query.TestQueryPackage.suite());
        suite.addTest(ontorama.ontotools.writer.test.TestWriterPackage.suite());

        return suite;
    }
}