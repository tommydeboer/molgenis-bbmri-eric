package org.molgenis.palga;

import java.io.File;
import java.io.FileWriter;

public class TestFileMaker
{
	private static final String HEADER = "id,xbool,xboolnillable,xcategorical_value,xcategoricalnillable_value,xdate,xdatenillable,xdatetime,xdatetimenillable,xdecimal,xdecimalnillable,xemail,xemailnillable,xenum,xenumnillable,xhtml,xhtmlnillable,xhyperlink,xhyperlinknillable,xint,xintnillable,xintrange,xintrangenillable,xlong,xlongnillable,xlongrange,xlongrangenillable,xmref_value,xmrefnillable_value,xstring,xstringnillable,xtext,xtextnillable,xxref_value,xxrefnillable_value,string1,string2,string3,string4,string5,string6,string7,string8,string9,string10,string11,string12,string13,string14,string15,string16,string17,string18,string19,string20,string21,string22,string23,string24,string25,string26,string27,string28,string29,string30,string31,string32,string33,string34,string35,string36,string37,string38,string39,string40,string41,string42,string43,string44,string45,string46,string47,string48,string49,string50,string51,string52,string53,string54,string55,string56,string57,string58,string59,string60,string61,string62,string63\n";
	// private static final String HEADER =
	// "PALGAexcerptnr,Regelnummer,PALGA-diagnose,PALGA-code,Retrievalterm,Soort onderzoek,Jaar onderzoek,Geslacht,Leeftijdscategorie\n";
	// private static final String ROWS =
	// "1,1,huid*arm*links*biopt*naevus naevocellularis,T01000*TY8000*TYY990*P11400*M87200,*1*2*15*,T,2010,v,>50 jaar\n3,1,maag*biopsie*chronisch actieve ontsteking*intestinale metaplasie*helicobacter pylori,T63000*P11400*M42100*M73320*E13720,**22*23*,T,2011,v,<18 jaar\n";
	private static final String ROWS = ",TRUE,TRUE,ref1,ref1,1985-08-01,1985-08-01,1985-08-12T11:12:13+0500,1985-08-12T11:12:13+0500,1.23,1.23,molgenis@gmail.com,molgenis@gmail.com,enum1,enum1,<h1>html</h1>,<h1>html</h1>,http://www.molgenis.org/,http://www.molgenis.org/,5,1,1,2,1,1,2,2,ref1,ref1,str1,str1,text,text,ref1,ref1,string1,string2,string3,string4,string5,string6,string7,string8,string9,string10,string11,string12,string13,string14,string15,string16,string17,string18,string19,string20,string21,string22,string23,string24,string25,string26,string27,string28,string29,string30,string31,string32,string33,string34,string35,string36,string37,string38,string39,string40,string41,string42,string43,string44,string45,string46,string47,string48,string49,string50,string51,string52,string53,string54,string55,string56,string57,string58,string59,string60,string61,string62,string63\n";

	public static void main(String[] args)
	{
		try
		{
			File f = new File("/Users/erwin/Documents/TypeTest.csv");
			f.createNewFile();

			FileWriter writer = new FileWriter(f);
			writer.write(HEADER);

			for (int i = 0; i < 5000; i++)
			{
				writer.write(i + ROWS);
				if (i % 10000 == 0)
				{
					System.out.println(i);
				}
			}

			writer.close();

			// File palga = new File("/Users/erwin/Documents/palga/Output_lzv1035.psv");
			// File test = new File("/Users/erwin/Documents/palga/Output_lzv1035-sample.psv");
			// test.createNewFile();
			//
			// BufferedReader r = new BufferedReader(new FileReader(palga));
			// Writer w = new FileWriter(test);
			//
			// int count = 0;
			// String line = r.readLine();
			// while (count < 150001)
			// {
			// w.write(line + "\n");
			// count++;
			// line = r.readLine();
			// }
			//
			// r.close();
			// w.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
