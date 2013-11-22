package com.csce315501_groupf.project_3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

//import com.csce315501_groupf.project_3.Questions.Question;

public class QuestionsPullParser {
	
	public static class Question {
		public final String q;
		public final String c;
		public final ArrayList<String> a;
		
		private Question(String q, String c, ArrayList<String> a) {
	        this.q = q;
	        this.c = c;
	        this.a = a;
	    }
	}
	
	private static final String ns = null;
	
	QuestionsPullParser() {}
	
	public List<Question> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
//        	Log.d(MainActivity.TAG,String.format("Parsing"));
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readQuestions(parser);
        }
        finally {
            in.close();
        }
    }
	
	private List<Question> readQuestions(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List<Question> questions = new ArrayList<Question>();
//	    Log.d(MainActivity.TAG,String.format("Parsing QUESTIONS"));
	    parser.require(XmlPullParser.START_TAG, ns, "questions");
//	    Log.d(MainActivity.TAG,String.format("Questions: Before while loop"));
	    while (parser.next() != XmlPullParser.END_TAG) {
//	    	Log.d(MainActivity.TAG,String.format("Questions: Top of while loop"));
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
//	        Log.d(MainActivity.TAG,String.format("Questions: name = %s",name));
	        // Starts by looking for the entry tag
	        if (name.equals("question")) {
	            questions.add(readQuestion(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return questions;
	}
	
	private Question readQuestion(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "question");
//	    Log.d(MainActivity.TAG,String.format("Parsing QUESTION"));
	    String q = null;
	    String c = null;
	    ArrayList<String> a = new ArrayList<String>();
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
//	        String correct = parser.
	        if (name.equals("q")) {
	            q = readQ(parser);
//	            Log.d(MainActivity.TAG,String.format("QUESTION: %s",q));
	        } else if (name.equals("a")) {
	        	int att = parser.getAttributeCount();
	        	if (att > 0) c = readA(parser);
	        	else 		 a.add(readA(parser));
	        } else {
	            skip(parser);
	        }
	    }
	    return new Question(q, c, a);
	}
	
	private String readQ(XmlPullParser parser) throws IOException, XmlPullParserException {
//		Log.d(MainActivity.TAG,String.format("Parsing Q"));
		parser.require(XmlPullParser.START_TAG, ns, "q");
	    String q = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "q");
	    return q;
	}
	
	private String readA(XmlPullParser parser) throws IOException, XmlPullParserException {
//		Log.d(MainActivity.TAG,String.format("Parsing A"));
		parser.require(XmlPullParser.START_TAG, ns, "a");
//	    ArrayList<String> a = new ArrayList<String>();
		String a = readText(parser);
//	    a.add(t);
	    parser.require(XmlPullParser.END_TAG, ns, "a");
	    return a;
	}
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
//		Log.d(MainActivity.TAG,String.format("Parsing TEXT"));
		String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
//		Log.d(MainActivity.TAG,String.format("SKIPPING"));
		if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
