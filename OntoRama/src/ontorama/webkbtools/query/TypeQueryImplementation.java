/* Generated by Together */



package ontorama.webkbtools.query;



import java.io.InputStream;

import java.io.DataInputStream;
import java.io.InputStreamReader;

import java.util.Iterator;



import ontorama.webkbtools.query.parser.Parser;



public class TypeQueryImplementation extends TypeQueryBase {



    public TypeQueryImplementation () {

        super();

    }



    public Iterator getTypeRelative(String termName) {

        return null;

    }



    public Iterator getTypeRelative(String termName, int relationLink) {
        // TODO: formulate and execute query to webkb and return Reader

        //parser.getOntologyTypeIterator(new DataInputStream(System.in));

        Iterator iterator = parser.getOntologyTypeIterator(new InputStreamReader(System.in));

        //parser.toString();


        return iterator;

    }



    public String toString() {
        return ("queryUrl = " + queryUrl +
            	", queryOutputFormat = " + queryOutputFormat);
    }

}

