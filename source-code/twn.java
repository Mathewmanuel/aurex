import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.lang.*;

public class twn extends HttpServlet {
	public String srhWord;
	public String Dummy;
	public String userWord;
	public String newWord;
	public StringBuffer getSynonym = new StringBuffer();
	public StringBuffer getTechTerm = new StringBuffer();
	public String synonymDisplay;
	public String techTerm;
	public String fullIndex;
	public int nounflag;
	public int verbflag;
	public int adjflag;
	public int advflag;
  	public ArrayList nounIndex;
    public ArrayList nounWords;
    public ArrayList verbIndex;
    public ArrayList verbWords;
    public ArrayList adjIndex;
    public ArrayList adjWords;
    public ArrayList advIndex;
    public ArrayList advWords;
    public ArrayList nounList;
    public ArrayList verbList;
    public ArrayList adjList;
    public ArrayList advList;
  	
  	static String tab[] = {"Ü", "Ý", "Þ", "ß", "à", "á", "â", "ã", "ä", "å", "æ", "ç", 
"è", "é", "ê", "ë", "ì", "í", "î", "ï", "ð", "ñ", "ò", "ó", "ô", "õ", "ö", "÷", "ø", "ù", 
"°", "±", "²", "³", "´", "µ", "¶", "¸", "¹", "º", "»", "¼", "½", "¾", "¿", "À", "Á", "Â", 
"Ã", "Ä", "Å", "Æ", "Ç", "È", "É", "Ë", "Ì", "Í", "Î", "Ï", "Ö", "×", "Ø", "Ù", "Ú", "Û", 
"®", "¯", 
"ú", "û", "ü", "ý", "¢", "£", "¤", "¦", "ª", "«", "¬"};

	static String iscii[] = {"í", "Í", "¬", "¼", "ã", "Ã", "ö", "Ö", "É", "ø", "Ø", "þ", 
"æ", "§", "ò", "¢", "ì", "ú", "ê", "å", "ç", "ë", "á", "õ", "ù", "ô", "È", "ó", "÷", "ä", 
"Æ", "³", "à", "ü", "Ì", "Ñ", "Ê", "Å", "µ", "Ë", "±", "Õ", "Ù", "²", "Ç", "Ó", "×", "Ä", 
"Ô", "º", "Î", "»", "Þ", "½", "­", "¿", "·", "ß", "¹", "¥", "û", "´", "¾", "ñ", "®", "Û", 
"ï", "Ï", 
"¡", "Ü", "£", "Á", "Ð", "è", "¨", "©", "â", "î", "é"};



    Convert conv = new Convert();	
  	PrintWriter out;
   
	public void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
	    res.setContentType("text/html");
    	out=res.getWriter();
		nounIndex = new ArrayList();
	    nounWords = new ArrayList();
	    verbIndex = new ArrayList();
	    verbWords = new ArrayList();
	    adjIndex  = new ArrayList();
	    adjWords  = new ArrayList();
	    advIndex  = new ArrayList();
		advWords  = new ArrayList();
		nounflag  = 0;
		verbflag  = 0;
		adjflag   = 0;
		advflag   = 0;
		String word = req.getParameter("Tamsearch");
		ResultSet rs;
		String cc, outout="";
    	Hashtable hsh = new Hashtable();
	    for(int i=0; i<iscii.length; i++)
    		hsh.put(iscii[i], tab[i]);

		try {
			for(int l=0; l<word.length(); l++) {
				cc = word.substring(l, l+1);
		       	if(cc.equals("¯")) outout = outout + " ";
       			if(hsh.containsKey(cc)) outout = outout + (String)hsh.get(cc); 
       		}
	   		word = outout; 
       		
	   	} catch (Exception e) {  }
		String word1=conv.tab2iits(word);
		
	   	try {

			Class.forName("org.gjt.mm.mysql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
			userWord = word1;
			userWord = userWord.replace(' ','_');
			String query = "select * from twn where label like binary '"+userWord+"'";
			try {
		       	Statement s = connection.createStatement();
   	   			rs=s.executeQuery(query);
			   	newWord = null;
				while(rs.next()) {     
					newWord  = rs.getString(2);
				}
				if (newWord != null) {
					srhWord = newWord;
				}
				else { 
					srhWord = userWord; 
					String query1 = "select root_word from morphtable where inflated_word  like binary '"+userWord+"'";								
					rs = s.executeQuery(query1);
					while(rs.next()) {     
						newWord  = rs.getString(1);
					}
					if (newWord != null) {
						srhWord = newWord;
					}
					else{
						srhWord=null;
						out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
						String s1 ="vaarttaik kaaNappaTavillai";
						out.print(conv.iits2tab(s1));
						out.print ("</font>");
						s1=null;
					}
				}
	       		select(srhWord);
				rs.close();
		  	} catch(Exception err) { }
		    	connection.close();
		} catch(Exception err) { err.printStackTrace(); }
	}
    
	public void select(String paraWord) {
   		ResultSet rs,rs1;
    	String label;
   		String pos;
   		String hypernym;
   		String wordListIndex;
   		int nlen;
   		int vlen;
    	int adjlen;
	    int advlen;
    	int  n_pt = 0;  
   		int hypercnt;
   		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
			String query = "select * from sense where label like binary '"+paraWord+"' and pos like 'Noun'";
			try {
       			Statement s = conn.createStatement();
       			Statement ns = conn.createStatement();
				rs=s.executeQuery(query);
				fullIndex="";
       			nounIndex.clear();
       			nounWords.clear();
				nounflag = 0;
				while(rs.next()) {     
					label=rs.getString(1);
					pos = rs.getString(2);
					hypercnt = Integer.parseInt(rs.getString(3));
					hypernym = rs.getString(4);
					String posn = "Noun";
					final StringTokenizer tkn = new StringTokenizer(hypernym,":");
					String hyperIndex = tkn.nextToken();
					String nounquery = "select * from twn where nodeindex like  '"+hyperIndex+"' and pos = 'Noun'";        
					ResultSet nrs;
					nrs = ns.executeQuery(nounquery);
					while(nrs.next()) {
						int rel = Integer.parseInt(nrs.getString(5));
						if(rel != 10){
							if (posn.equals (pos)) {
								nounflag = 1;
								nounWords.add(label);
								nounIndex.add(hypernym);
								fullIndex = fullIndex +"+"+hypernym;
							}
						}
					}
					nrs.close();
					nlen = nounIndex.size();
				}
				rs.close();
			} catch(Exception e) {  }
	    	conn.close();
    	} catch(Exception e) {  }

		synonymDisplay = " ";
		getSynonym.setLength(0);
		Object wi[] = nounIndex.toArray();
		Object w[] =nounWords.toArray(); 
		int indexLen = wi.length;
		int i=0;
		int count = 1;
		String searchword = srhWord.replace('_',' '); 
		if(indexLen == 1) {
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println("<br>");
			out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  peyarccol poruL uLLatu"));
			out.println("<br>");
			out.print("</font>");
		}
		else if(indexLen >1){
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println("<br>");
       		out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  peyarccol poruLkaL uLLana"));
			out.println("<br>");
			out.print("</font>");
		}
		else {}
	
		for(int j=0;j<wi.length;j++) {
			out.println(count+".  ");
	    	out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
	    	out.println("<font color = red>");
	    	out.println(conv.iits2tab(searchword));
	    	out.println("</font>");
	    	String nindexval = (String) wi[j];
	    	hyper h = new hyper();
			String imHypernym = h.immediateHyper(nindexval);
			if(imHypernym != null){
				imHypernym = " @ "+imHypernym;
				imHypernym = imHypernym.replace('_',' ');
				out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
				out.println("<font color = black>");
				out.println(conv.iits2tab(imHypernym));
				out.println("</font>");
				out.println("</font>");
			}
	    	out.print("</font>");
			out.println("<br>");
	      	count++;
	   		i++;
		}	
		Object fullNounIndex[] = nounIndex.toArray();
		if(nounflag == 1) {	     
			nounList = new ArrayList();
			Relation r = new Relation();
			int hyper_ans = r.findHyper(wi);
			if(hyper_ans == 1 ){
				nounList.add("Hypernym");
			}
			int mero_ans = r.findMero(wi);
			if(mero_ans == 1 ){
				nounList.add("Meronym");
			}
			int holo_ans = r.findHolo(wi);
			if(holo_ans == 1 ){
				nounList.add("Holonym");
			}
			int rverb_ans = r.findRelverb(wi);
			if(rverb_ans == 1 ){
				nounList.add("RelVerb");
			}
			int hypo_ans = r.findHypo(wi);
			if(hypo_ans == 1 ){
				nounList.add("Hyponym");
			}
			int syno_ans = r.findSyno(wi);
			if(syno_ans == 1 ){
				nounList.add("Synonym");
			}
			int ctrm_ans = r.findCoterm(wi);
			if(ctrm_ans == 1 ){
				nounList.add("Coterm");
			}
    		out.print("<html>");
		    out.print("<body text=#000000 bgcolor=#ffffff link=#0000ff vlink=#ff0000 alink=#000088>");
		    out.print("<form action='/examples/servlet/Relation' target='Output'>");
	    	out.print("</font>");
	    		out.print("Search for");
		    	out.print("<select name = techterm>");
		    	Object nounRel[] = nounList.toArray();
				for(int j=0;j<nounRel.length;j++){
					if(nounRel[j].equals("Synonym")){
						out.print("<option value ='4'>Synonym</option>");
					}
					else if(nounRel[j].equals("RelNoun")){
						out.print("<option value ='10'>RelNoun</option>");
					}
					else if(nounRel[j].equals("Hypernym")){
						out.print("<option value ='0'>Hypernym</option>");
					}
					else if(nounRel[j].equals("Meronym")){
						out.print("<option value ='1'>Meronym</option>");
					}
					else if(nounRel[j].equals("Holonym")){
						out.print("<option value ='2'>Holonym</option>");
					}
					else if(nounRel[j].equals("Hyponym")){
						out.print("<option value ='3'>Hyponym</option>");
					}
					else if(nounRel[j].equals("Coterm")){
						out.print("<option value ='5'>Nominal</option>");
					}
					else if(nounRel[j].equals("RelVerb")){
						out.print("<option value ='12'>RelVerb</option>");
					}
					else {}
				}	    	
		    	out.print("</select>");
				out.print("of Senses   ");
				out.print("<input type=text name =sense size = 5>");
    			out.print("<input type=submit value=search>");
	    		String poss = "Noun";
	    		out.print("<input type=hidden value='"+fullIndex+"' name=fullindex>");
	    		out.print("<input type=hidden value='"+poss+"' name=poss>");
    			out.print("<input type=hidden name=techterm>");
		    	out.print("<input type=hidden name=sense>");
		    	out.print("</form>");
	    		out.print("</body>");
		    	out.print("</html>");
		}else { 
    			nounIndex.clear(); 
			nounWords.clear();
   		}
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
	 		String query = "select * from sense where label like binary '"+paraWord+"' and pos like 'Verb'";
			try {
	       		Statement s = conn.createStatement();
	       		Statement vs = conn.createStatement();
				rs=s.executeQuery(query);
				verbflag = 0;
				verbWords.clear();
				verbIndex.clear();
				fullIndex="";
	    		while(rs.next()) {     
					label=rs.getString(1);
					pos = rs.getString(2);
					hypercnt = Integer.parseInt(rs.getString(3));
					hypernym = rs.getString(4);
					final StringTokenizer tkn = new StringTokenizer(hypernym,":");
					String hyperIndex = tkn.nextToken();
					String verbquery = "select * from twn where nodeindex like  '"+hyperIndex+"' and pos = 'Verb'";         
					rs1 = vs.executeQuery(verbquery);	
					while(rs1.next()){
						int rel = Integer.parseInt(rs1.getString(5));
						String posn = "Verb";
						if(rel != 10){	
							if (posn.equals (pos)) {
								verbflag = 1;							
								verbWords.add(label);	
								verbIndex.add(hypernym);
								fullIndex = fullIndex +"+"+hypernym;
							}
						}
					}
					rs1.close();	
				}
				vlen = verbIndex.size();
				rs.close();
		 	} catch(Exception e) {  }
      		conn.close();
      	} catch(Exception e) {  }
		getSynonym.setLength(0);
		synonymDisplay = " ";
		Object wiv[] = verbIndex.toArray();
		Object wv[] =verbWords.toArray(); 
		indexLen = wiv.length;
		i=0;
		count = 1;
		if(indexLen == 1){
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
    		out.println("<br>");
			out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  vinaiccol  poruL uLLatu"));
			out.println("<br>");
			out.print("</font>");
		}
		else if(indexLen >1){
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println("<br>");
	    	out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  vinaiccol  poruLkaL uLLana"));
    		out.println("<br>");
    		out.print("</font>");
		}
		else  {}
		for(int j=0;j<wiv.length;j++) {
			out.println(count+".  ");
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
				out.println("<font color = red>");
		    		out.println(conv.iits2tab(searchword));
		    	out.println("</font>");
		    out.print("</font>");	
	    	out.println("<br>");
	      	count++;
		 }
    
		if (verbflag == 1) {	     
			verbList = new ArrayList();
				Relation r = new Relation();
				int hyper_ans = r.findHyper(wiv);
				if(hyper_ans == 1 ){
					verbList.add("Hypernym");
				}
				int tropo_ans = r.findHypo(wiv);
				if(tropo_ans == 1 ){
					verbList.add("Troponym");
				}
				int syno_ans = r.findSyno(wiv);
					if(syno_ans == 1 ){
					verbList.add("Synonym");
				}
				int nom_ans = r.findNominal(wiv);
					if(nom_ans == 1 ){
					verbList.add("Nominal");
				}
				int reln_ans = r.findRelnoun(wiv);
				if(reln_ans == 1 ){
					verbList.add("RelNoun");
				}
    			out.print("<html>");
				out.print("<body text=#000000 bgcolor=#ffffff link=#0000ff vlink=#ff0000 alink=#000088>");
	    		out.print("<form action='/examples/servlet/Relation' target='Output'>");
    			out.print("</font>");
    			out.print("Search for  ");
	    		out.print("<select name = techterm>");
	    		Object verbRel[] = verbList.toArray();
	    		for(int j=0;j<verbRel.length;j++){
					if(verbRel[j].equals("Synonym")){
						out.print("<option value ='4'>Synonym</option>");
					}
					else if(verbRel[j].equals("RelNoun")){
						out.print("<option value ='10'>RelNoun</option>");
					}
					else if(verbRel[j].equals("Hypernym")){
						out.print("<option value ='0'>Hypernym</option>");
					}
					else if(verbRel[j].equals("Troponym")){
						out.print("<option value ='3'>Troponym</option>");
					}
					else if(verbRel[j].equals("Nominal")){
						out.print("<option value ='7'>Nominal</option>");
					}
					else {}
				}		
	    		out.print("</select>");
    			out.print("of Sense");
    			out.print("<input type=text name =sense size = 5>");
	    		out.print("<input type=submit value=search>");
    			String poss = "Verb";
    			out.print("<input type=hidden value='"+fullIndex+"' name=fullindex>");
	    		out.print("<input type=hidden value='"+poss+"' name=poss>");
    			out.print("<input type=hidden name=techterm>");
    			out.print("<input type=hidden name=sense>");
	    		out.print("</form>");
    			out.print("</body>");
    			out.print("</html>");
	    	}
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
			String query = "select * from sense where label like binary '"+paraWord+"' and pos like 'Adjective'";
			try {
      			Statement s = conn.createStatement();
      			Statement adjs = conn.createStatement();
				rs=s.executeQuery(query);
				adjflag =0;
				adjWords.clear();
				adjIndex.clear();
				fullIndex="";
	       		while(rs.next()) {     
					label=rs.getString(1);
					pos = rs.getString(2);
					hypercnt = Integer.parseInt(rs.getString(3));
					hypernym = rs.getString(4);
					final StringTokenizer tkn = new StringTokenizer(hypernym,":");
					String hyperIndex = tkn.nextToken();
					String adjquery = "select * from twn where nodeindex like  '"+hyperIndex+"' and pos = 'Adjective'";        
					rs1 = adjs.executeQuery(adjquery);	
					while(rs1.next()) {
						int rel = Integer.parseInt(rs1.getString(5));
						if(rel != 10){	
							String posn = "Adjective";
							if (posn.equals (pos)) {
								adjflag =1;
								adjWords.add(label);
								adjIndex.add(hypernym);
								fullIndex = fullIndex +"+"+hypernym;
							}
						}
					}
					rs1.close();
					adjlen = adjIndex.size();
				}
				rs.close();
		 	} catch(Exception e) {  }
      		conn.close();
      	} catch(Exception e) {  }
		synonymDisplay = " ";
		getSynonym.setLength(0);
		Object wij[] = adjIndex.toArray();
		Object wj[] =adjWords.toArray(); 
		indexLen = wij.length;
		i=0;	
		count = 1;
		if(indexLen == 1) {
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
     			out.println("<br>");
			out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  peyaraTai  poruL uLLatu"));
			out.println("<br>");
			out.print("</font>");
		}
		else if(indexLen >1){
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println("<br>");
	   		out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  peyaraTai  poruLkaL uLLana"));
		   	out.println("<br>");
		   	out.print("</font>");
		}
		else {}	
		for(int j=0;j<wij.length;j++) {
			out.println(count+".  ");
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println("<font color = red>");
	    	out.println(conv.iits2tab(searchword));
	    	out.println("</font>");
	    	out.print("</font>");
	    	out.println("<br>");		
		    count++;
		 }
		if (adjflag == 1) {	     
			Relation r = new Relation();
			adjList = new ArrayList();
			int syno_ans = r.findSyno(wij);
			if(syno_ans == 1 ){
				adjList.add("Synonym");
			}
			int reln_ans = r.findRelnoun(wij);
			if(reln_ans == 1 ){
				adjList.add("RelNoun");
			}
			out.print("<html>");
     		out.print("<body text=#000000 bgcolor=#ffffff link=#0000ff vlink=#ff0000 alink=#000088>");
	     	out.print("<form action='/examples/servlet/Relation' target='Output'>");
     		out.print("</font>");
	 		out.print("Search for  ");
	     	out.print("<select name = techterm>");
     		Object adjRel[] = adjList.toArray();
			for(int j=0;j<adjRel.length;j++){
				if(adjRel[j].equals("Synonym")){
					out.print("<option value ='4'>Synonym</option>");
				}
				else if(adjRel[j].equals("RelNoun")){
					out.print("<option value ='10'>RelNoun</option>");
				}
					else {}
				}
     			out.print("</select>");
     			out.print("of Sense");
     			out.print("<input type=text name =sense size = 5>");
	     		out.print("<input type=submit value=search>");
     			String poss = "Adjective";
     			out.print("<input type=hidden value='"+fullIndex+"' name=fullindex>");
	     		out.print("<input type=hidden value='"+poss+"' name=poss>");
     			out.print("<input type=hidden name=techterm>");
     			out.print("<input type=hidden name=sense>");
	     		out.print("</form>");
	     		out.print("</body>");
     			out.print("</html>");
      		}
			try {
		 		Class.forName("org.gjt.mm.mysql.Driver");
	 			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
				String query = "select * from sense where label like binary '"+paraWord+"' and pos like 'Adverb'";
		 		try {
        			Statement s = conn.createStatement();
        			Statement advs = conn.createStatement();		
					rs=s.executeQuery(query);
					advflag = 0;
					advWords.clear();
					advIndex.clear();
					fullIndex="";
	   				while(rs.next()) {     
						label=rs.getString(1);
						pos = rs.getString(2);
						hypercnt = Integer.parseInt(rs.getString(3));
						hypernym = rs.getString(4);
						final StringTokenizer tkn = new StringTokenizer(hypernym,":");
						String hyperIndex = tkn.nextToken();
						String advquery = "select * from twn where nodeindex like  '"+hyperIndex+"' and pos = 'Adverb'";        
						rs1 = advs.executeQuery(advquery);
						while(rs1.next()) {
							int rel = Integer.parseInt(rs1.getString(5));
							if(rel != 10){
								String posn = "Adverb";
								if (posn.equals (pos)) {
									advflag =1;
									advWords.add(label);
									advIndex.add(hypernym);
									fullIndex = fullIndex +"+"+hypernym;
								}
							}
						}
						rs1.close();
						advlen = advIndex.size();
					}
					rs.close();
		 		} catch(Exception e) {  }
      			conn.close();
      		} catch(Exception e) { }
		synonymDisplay = " ";
		getSynonym.setLength(0);
		Object wid[] = advIndex.toArray();
		Object wd[] =advWords.toArray(); 
		indexLen = wid.length;
		i=0;
		count = 1;
		if(indexLen == 1){
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");			
			out.println("<br>");
			out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  vinaiyaTai poruL uLLatu"));
			out.println("<br>");
			out.print("</font>");
		}
		else if(indexLen >1){
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println("<br>");
			out.println(conv.iits2tab("koTukkappaTTa vaarttaikku  "+indexLen+"  vinaiyaTai poruLkaL uLLana"));
			out.println("<br>");
			out.print("</font>");
		}
		else {}
		for(int j=0;j<wid.length;j++) {
			out.println(count+".  ");
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println("<font color = red>");
	    	out.println(conv.iits2tab(searchword));
			out.println("</font>");
			out.print("</font>");
		    out.println("<br>");
		   	count++;
		}	
		if (advflag == 1) {	     
			Relation r = new Relation();
			advList = new ArrayList();
			int syno_ans = r.findSyno(wid);
			if(syno_ans == 1 ){
				advList.add("Synonym");
			}
			int reln_ans = r.findRelnoun(wid);
			if(reln_ans == 1 ){
				advList.add("RelNoun");
			}
			
     		out.print("<html>");
	     	out.print("<body text=#000000 bgcolor=#ffffff link=#0000ff vlink=#ff0000 alink=#000088>");
     		out.print("<form action='/examples/servlet/Relation' target='Output'>");
     		out.print("</font>");
	     	out.print("Search  for ");
     		out.print("<select name = techterm>");
	     	Object advRel[] = advList.toArray();
     		for(int j=0;j<advRel.length;j++){
				if(advRel[j].equals("Synonym")){
					out.print("<option value ='4'>Synonym</option>");
				}
				else if(advRel[j].equals("RelNoun")){
					out.print("<option value ='10'>RelNoun</option>");
				}
				else {}
			}
     		out.print("</select>");
	     	out.print("of Sense");
     		out.print("<input type=text name =sense size = 5>");
     		out.print("<input type=submit value=search>");
	     	String poss = "Adverb";
	    	out.print("<input type=hidden value='"+fullIndex+"' name=fullindex>");
     		out.print("<input type=hidden value='"+poss+"' name=poss>");
     		out.print("<input type=hidden name=techterm>");
		    out.print("<input type=hidden name=sense>");
    		out.print("</form>");
     		out.print("</body>");
	     	out.print("</html>");
      	}
      	if(nounflag  != 1 && verbflag != 1 && adjflag !=1 && advflag != 1){
      		out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			String s ="vaarttai kaaNappaTavillai";
			out.print(conv.iits2tab(s));
			out.print ("</font>");
			s=null;
		}
	}		
	
	public class Relation 
	{
		int ans;
		public int findHypo(Object indexval[]) {
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i =0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select label from twn where nodeindex like '"+tmpstr+"')";
				String sword ="";
				ResultSet rs;
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						sword = rs.getString(1);
					}
				} catch(Exception ei) { }
				String hypoqry = "(select  label from twn where nodeindex  like '"+indexarr[i]+",_' and relation ='3' and label not like binary '"+sword+"')";
				Query q = new Query();
				ans = q.runQuery(hypoqry);
				if (ans == 1)
					break;
			}
			return ans;
		}
		public int findMero(Object indexval[]) {
			int ans=0;
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i =0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select label from twn where nodeindex like '"+tmpstr+"')";
				String sword ="";
				ResultSet rs;
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						sword = rs.getString(1);
					}
				} catch(Exception ei) { }
				
				String meroqry = "(select label from twn where nodeindex  like '"+indexarr[i]+",_' and relation ='1' and label not like binary '"+sword+"')";
				Query q = new Query();
				ans = q.runQuery(meroqry);
				if (ans == 1)
					break;
			}
			return ans;
		}
		
		public int findHolo(Object indexval[]) {
			int ans=0;
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i=0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select relation from twn where nodeindex like '"+tmpstr+"')";
				String rel="";
				String holoqry="";
				ResultSet rs;
				int position = tmpstr.lastIndexOf(",");
				String prev = tmpstr.substring(0,position);
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						rel = rs.getString(1);
					}
					int k = Integer.parseInt(rel);
					if( k == 1) {
						
						holoqry ="(select label from twn where nodeindex like '"+prev+"')";		
						
					}
				} catch(Exception ei) { }
				
				Query q = new Query();
				ans = q.runQuery(holoqry);
				if (ans == 1)
					break;
			}
			return ans;
		}
		
		public int findCoterm(Object indexval[]) {
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i =0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select label from twn where nodeindex like '"+tmpstr+"')";
				String sword ="";
				ResultSet rs;
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						sword = rs.getString(1);
					}
				} catch(Exception ei) {  }
				String ctrmqry = "(select label from twn where nodeindex  like '"+indexarr[i]+",_' and relation ='5' and label not like binary '"+sword+"')";
				Query q = new Query();
				ans = q.runQuery(ctrmqry);
				if (ans == 1)
					break;
			}
			return ans;
		}
		
		public int findNominal(Object indexval[]) {
			int ans=0;
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i =0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select label from twn where nodeindex like '"+tmpstr+"')";
				String sword ="";
				ResultSet rs;
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						sword = rs.getString(1);
					}
				} catch(Exception ei) {  }
				String nomqry = "(select label from twn where nodeindex  like '"+indexarr[i]+",_' and relation ='7' and label not like binary '"+sword+"')";
				Query q = new Query();
				ans = q.runQuery(nomqry);
				if (ans == 1)
					break;
			}
			return ans;
		}
		public int findRelnoun(Object indexval[]) {
			int ans=0;
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i=0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select relation,label from twn where nodeindex like '"+tmpstr+"')";
				String rel="";
				String sword ="";
				String rnqry="";
				ResultSet rs;
				int position = tmpstr.lastIndexOf(",");
				String prev = tmpstr.substring(0,position);
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						rel = rs.getString(1);
						sword = rs.getString(2);
					}
					int k = Integer.parseInt(rel);
					if( k == 8 || k == 9 || k == 11) {
						rnqry ="(select label from twn where nodeindex like '"+prev+"')";		
						
					}
					else {
						rnqry = "(select label from twn where nodeindex  like '"+tmpstr+",_' and relation ='10' and label not like binary '"+sword+"') union (select  label from twn where nodeindex  like '"+tmpstr+",__' and relation ='10' and label not like binary '"+sword+"')";
					}
				} catch(Exception ei) { }
				Query q = new Query();
				ans = q.runQuery(rnqry);
				if (ans == 1)
					break;
			}
			return ans;
		}
		
		public int findRelverb(Object indexval[]) {
			int ans=0;
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i=0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select relation from twn where nodeindex like '"+tmpstr+"')";
				String rel="";
				String rverbqry="";
				ResultSet rs;
				int position = tmpstr.lastIndexOf(",");
				String prev = tmpstr.substring(0,position);
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						rel = rs.getString(1);
						
					}
					int k = Integer.parseInt(rel);
					if( k == 7) {
						
						rverbqry ="(select label from twn where nodeindex like '"+prev+"')";		
						
					}
				} catch(Exception ei) { }
				Query q = new Query();
				ans = q.runQuery(rverbqry);
				if (ans == 1)
					break;
			}
			return ans;
		}	
		public int findHyper(Object indexval[]) {
			int ans=0;
			Object indexarr[] = (Object[])splittoken(indexval); 
			for(int i =0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				int position = tmpstr.lastIndexOf(",");
				String prev = tmpstr.substring(0,position);
				
				String hyperqry = " select label from twn where nodeindex like '"+prev+"'";
				Query q = new Query();
				ans = q.runQuery(hyperqry);
				if (ans == 1)
					break;
			}
			return ans;
		}
		
		public int findSyno(Object indexval[]) {
			int ans=0;
			Object indexarr[] = (Object[])splittoken(indexval); 
			
			for(int i=0;i<indexarr.length;i++){
				Object tmpobj = indexarr[i];
				String tmpstr = tmpobj.toString();
				String q1 = "(select relation,label from twn where nodeindex='"+tmpstr+"')";
				String rel="";
				String synqry="";
				String sword="";
				ResultSet rs;
				int position = tmpstr.lastIndexOf(",");
				String prev = tmpstr.substring(0,position);
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
					Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()){
						rel = rs.getString(1);
						sword = rs.getString(2);
					}
					int k = Integer.parseInt(rel);
					if(k != 4) {
						synqry = "(select label from twn where nodeindex like '"+tmpstr+",_' and relation ='4' and label not like binary '"+sword+"')";
						
					}
					else {
						synqry ="(select label from twn where nodeindex like '"+prev+"' and label not like binary '"+sword+"') union (select label from twn where nodeindex like '"+prev+",_' and relation ='4' and nodeindex != '"+tmpstr+"' and label not like binary '"+sword+"' and relation ='4')";		
					}
				} catch(Exception ei) { }
				Query q = new Query();
				ans = q.runQuery(synqry);
				if (ans == 1)
					break;
			}
			return ans;
		}	
		
		public Object splittoken(Object indexval[])
		{
			ArrayList indexlist = new ArrayList();
			for(int i=0;i<indexval.length;i++){
				String s = indexval[i].toString();
				StringTokenizer tkn1 = new StringTokenizer(s,":");
				while(tkn1.hasMoreElements()){
					indexlist.add(tkn1.nextToken());
				}
			}
			Object indexarr[] = indexlist.toArray();
			return indexarr;
		}
	}

	public class hyper
	{
		ResultSet rs_hyper=null;
		String hyperstr=null;
		public String immediateHyper(String indexval){
			final StringTokenizer tkn = new StringTokenizer(indexval,":");
			String index1 = tkn.nextToken();
			int position = index1.lastIndexOf(",");
			String hyperInd = index1.substring(0,position);
			String hyperqry = " select label from twn where nodeindex like '"+hyperInd+"'";
			try {
				Class.forName("org.gjt.mm.mysql.Driver");
				Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
				Statement s4 = conn.createStatement();
				rs_hyper=s4.executeQuery(hyperqry);
				while(rs_hyper.next()){
					hyperstr = rs_hyper.getString(1);
				}
			}
			catch(Exception ei) { }
			return hyperstr;
		}
	}

	public class Query {
		public int runQuery(String qry){
			ResultSet rs;
			int result=0;
			try {
				Class.forName("org.gjt.mm.mysql.Driver");
				Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.2.33:3306/wordnet?user=arulmozi&password=onemoretry");
				Statement s = conn.createStatement();
				rs=s.executeQuery(qry);
				while(rs.next()){
					result = 1;
				}
			}
			catch(Exception ei) { }
			return result;	
		}
	}
	
}
